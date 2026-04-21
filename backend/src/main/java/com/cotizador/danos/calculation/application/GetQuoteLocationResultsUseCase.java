package com.cotizador.danos.calculation.application;

import com.cotizador.danos.calculation.domain.LocationCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.calculation.domain.QuoteLocationResultView;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.LocationValidationStatus;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.util.List;
import java.util.Optional;

public class GetQuoteLocationResultsUseCase {

  private static final QuoteCalculationResult EMPTY_CALCULATION_RESULT =
      QuoteCalculationResult.fromLocationResults(List.of(), List.of());

  private final QuoteRepository quoteRepository;
  private final QuoteCalculationResultRepository quoteCalculationResultRepository;
  private final LocationRepository locationRepository;

  public GetQuoteLocationResultsUseCase(
      QuoteRepository quoteRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository,
      LocationRepository locationRepository
  ) {
    this.quoteRepository = quoteRepository;
    this.quoteCalculationResultRepository = quoteCalculationResultRepository;
    this.locationRepository = locationRepository;
  }

  public List<QuoteLocationResultView> handle(String folio) {
    quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    QuoteCalculationResult calculationResult = quoteCalculationResultRepository.findByQuoteFolio(folio)
        .orElse(EMPTY_CALCULATION_RESULT);
    List<QuoteLocation> locations = locationRepository.findByQuoteFolio(folio);

    return locations.stream()
        .map(location -> toResultView(location, findCalculationResult(location, calculationResult)))
        .toList();
  }

  private QuoteLocationResultView toResultView(
      QuoteLocation location,
      Optional<LocationCalculationResult> calculationResult
  ) {
    if (calculationResult.isPresent()) {
      LocationCalculationResult result = calculationResult.get();
      return new QuoteLocationResultView(
          result.locationId(),
          result.locationName(),
          result.status(),
          result.premium(),
          isCalculated(result),
          result.alerts()
      );
    }

    return new QuoteLocationResultView(
        location.getId(),
        location.getLocationName(),
        location.getValidationStatus(),
        0.0,
        false,
        location.getAlerts()
    );
  }

  private boolean isCalculated(LocationCalculationResult result) {
    return result.status() == LocationValidationStatus.COMPLETE && result.premium() > 0.0;
  }

  private Optional<LocationCalculationResult> findCalculationResult(
      QuoteLocation location,
      QuoteCalculationResult calculationResult
  ) {
    return calculationResult.getLocations().stream()
        .filter(result -> result.locationId() == location.getId())
        .findFirst();
  }
}
