package com.cotizador.danos.calculation.application;

import com.cotizador.danos.calculation.domain.CalculationTraceDetail;
import com.cotizador.danos.calculation.domain.CalculationTraceRepository;
import com.cotizador.danos.calculation.domain.LocationCalculationResult;
import com.cotizador.danos.calculation.domain.PremiumCalculator;
import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.location.domain.LocationValidationStatus;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class CalculateQuoteUseCase {

  private static final String UNKNOWN_EXCLUSION_REASON = "Unknown exclusion reason";
  private static final String MISSING_OCCUPANCY_FIRE_KEY_REASON = "missing occupancy.fireKey";
  private static final String MISSING_TARIFFABLE_GUARANTEES_REASON = "missing tariffable guarantees";
  private static final String INVALID_POSTAL_CODE_REASON = "invalid postal code";
  private static final String NO_LOCATIONS_CONFIGURED_ALERT = "No locations configured for calculation";
  private static final String NO_COVERAGES_CONFIGURED_ALERT = "No coverage options configured for calculation";
  private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{5,6}$");
  private static final CalculationTraceRepository NO_OP_TRACE_REPOSITORY = traceDetails -> {
  };
  private static final QuoteCalculationResultRepository NO_OP_RESULT_REPOSITORY = new QuoteCalculationResultRepository() {
    @Override
    public java.util.Optional<QuoteCalculationResult> findByQuoteFolio(String folio) {
      return java.util.Optional.empty();
    }

    @Override
    public QuoteCalculationResult save(String folio, QuoteCalculationResult result) {
      return result;
    }
  };

  private final QuoteRepository quoteRepository;
  private final LocationRepository locationRepository;
  private final QuoteCoverageRepository quoteCoverageRepository;
  private final PremiumCalculator premiumCalculator;
  private final CalculationTraceRepository calculationTraceRepository;
  private final QuoteCalculationResultRepository quoteCalculationResultRepository;

  public CalculateQuoteUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      QuoteCoverageRepository quoteCoverageRepository,
      PremiumCalculator premiumCalculator
  ) {
    this(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator,
        NO_OP_TRACE_REPOSITORY,
        NO_OP_RESULT_REPOSITORY
    );
  }

  public CalculateQuoteUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      QuoteCoverageRepository quoteCoverageRepository,
      PremiumCalculator premiumCalculator,
      CalculationTraceRepository calculationTraceRepository
  ) {
    this(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator,
        calculationTraceRepository,
        NO_OP_RESULT_REPOSITORY
    );
  }

  public CalculateQuoteUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      QuoteCoverageRepository quoteCoverageRepository,
      PremiumCalculator premiumCalculator,
      CalculationTraceRepository calculationTraceRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository
  ) {
    this.quoteRepository = quoteRepository;
    this.locationRepository = locationRepository;
    this.quoteCoverageRepository = quoteCoverageRepository;
    this.premiumCalculator = premiumCalculator;
    this.calculationTraceRepository = calculationTraceRepository;
    this.quoteCalculationResultRepository = quoteCalculationResultRepository;
  }

  @Transactional
  public QuoteCalculationResult handle(String folio) {
    Quote quote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    List<QuoteLocation> locations = locationRepository.findByQuoteFolio(folio);
    List<QuoteCoverageSelection> coverages = quoteCoverageRepository.findByQuoteFolio(folio);

    List<LocationCalculationResult> locationResults = new ArrayList<>();
    List<String> alerts = new ArrayList<>();
    List<CalculationTraceDetail> traceDetails = new ArrayList<>();
    addPreCalculationAlerts(locations, coverages, alerts);

    for (QuoteLocation location : locations) {
      if (location.getValidationStatus() == LocationValidationStatus.COMPLETE) {
        List<String> nonCalculableReasons = nonCalculableReasons(location, coverages);
        if (!nonCalculableReasons.isEmpty()) {
          alerts.add("Location " + location.getLocationName() + " excluded: " + String.join(", ", nonCalculableReasons));
          continue;
        }

        double premium = premiumCalculator.calculate(location, coverages);
        locationResults.add(toLocationResult(location, premium));
        traceDetails.addAll(traceDetailsFor(location, coverages));
      } else if (location.getValidationStatus() == LocationValidationStatus.INVALID) {
        alerts.add(exclusionAlertFor(location));
      } else {
        locationResults.add(toLocationResult(location, 0.0));
        alerts.add("Location " + location.getLocationName() + " skipped due to incomplete data");
      }
    }

    QuoteCalculationResult result = QuoteCalculationResult.fromLocationResults(locationResults, alerts);
    QuoteCalculationResult savedResult = quoteCalculationResultRepository.save(folio, result);
    calculationTraceRepository.saveAll(traceDetails);
    quoteRepository.save(quote.markAsCalculated());
    return savedResult;
  }

  private LocationCalculationResult toLocationResult(QuoteLocation location, double premium) {
    return new LocationCalculationResult(
        location.getId(),
        location.getLocationName(),
        location.getValidationStatus(),
        premium,
        location.getAlerts()
    );
  }

  private List<CalculationTraceDetail> traceDetailsFor(
      QuoteLocation location,
      List<QuoteCoverageSelection> coverages
  ) {
    return premiumCalculator.traceFor(location, coverages);
  }

  private String exclusionAlertFor(QuoteLocation location) {
    String reason = location.getAlerts().isEmpty() ? UNKNOWN_EXCLUSION_REASON : location.getAlerts().get(0);
    return "Location " + location.getLocationName() + " excluded: " + reason;
  }

  private List<String> nonCalculableReasons(QuoteLocation location, List<QuoteCoverageSelection> coverages) {
    List<String> reasons = new ArrayList<>();
    if (!isPostalCodeValid(location.getPostalCode())) {
      reasons.add(INVALID_POSTAL_CODE_REASON);
    }
    if (!StringUtils.hasText(location.getOccupancyType())) {
      reasons.add(MISSING_OCCUPANCY_FIRE_KEY_REASON);
    }
    if (!hasTariffableGuarantees(coverages)) {
      reasons.add(MISSING_TARIFFABLE_GUARANTEES_REASON);
    }
    return reasons;
  }

  private boolean hasTariffableGuarantees(List<QuoteCoverageSelection> coverages) {
    return coverages.stream().anyMatch(QuoteCoverageSelection::isSelected);
  }

  private boolean isPostalCodeValid(String postalCode) {
    if (!StringUtils.hasText(postalCode)) {
      return false;
    }
    return POSTAL_CODE_PATTERN.matcher(postalCode).matches();
  }

  private void addPreCalculationAlerts(
      List<QuoteLocation> locations,
      List<QuoteCoverageSelection> coverages,
      List<String> alerts
  ) {
    if (locations.isEmpty()) {
      alerts.add(NO_LOCATIONS_CONFIGURED_ALERT);
    }
    if (!hasTariffableGuarantees(coverages)) {
      alerts.add(NO_COVERAGES_CONFIGURED_ALERT);
    }
  }
}
