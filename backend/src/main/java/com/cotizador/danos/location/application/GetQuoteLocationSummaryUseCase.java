package com.cotizador.danos.location.application;

import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.location.domain.LocationValidationStatus;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.util.List;

public class GetQuoteLocationSummaryUseCase {

  public record Result(
      int totalLocations,
      int completeLocations,
      int incompleteLocations,
      int invalidLocations,
      int calculatedLocations,
      double calculatedPremium,
      List<String> alerts
  ) {
  }

  private final QuoteRepository quoteRepository;
  private final LocationRepository locationRepository;
  private final QuoteCalculationResultRepository quoteCalculationResultRepository;

  public GetQuoteLocationSummaryUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository
  ) {
    this.quoteRepository = quoteRepository;
    this.locationRepository = locationRepository;
    this.quoteCalculationResultRepository = quoteCalculationResultRepository;
  }

  public Result handle(String folio) {
    quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    List<QuoteLocation> locations = locationRepository.findByQuoteFolio(folio);
    QuoteCalculationResult calculationResult = quoteCalculationResultRepository.findByQuoteFolio(folio)
        .orElse(QuoteCalculationResult.fromLocationResults(List.of(), List.of()));

    int complete = countByStatus(locations, LocationValidationStatus.COMPLETE);
    int incomplete = countByStatus(locations, LocationValidationStatus.INCOMPLETE);
    int invalid = countByStatus(locations, LocationValidationStatus.INVALID);
    int calculated = (int) calculationResult.getLocations().stream()
        .filter(result -> result.premium() > 0.0)
        .count();

    return new Result(
        locations.size(),
        complete,
        incomplete,
        invalid,
        calculated,
        calculationResult.getNetPremium(),
        calculationResult.getAlerts()
    );
  }

  private int countByStatus(List<QuoteLocation> locations, LocationValidationStatus status) {
    return (int) locations.stream()
        .filter(location -> location.getValidationStatus() == status)
        .count();
  }
}
