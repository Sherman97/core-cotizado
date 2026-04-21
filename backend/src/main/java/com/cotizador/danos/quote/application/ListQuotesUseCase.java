package com.cotizador.danos.quote.application;

import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Instant;
import java.util.List;

public class ListQuotesUseCase {

  private final QuoteRepository quoteRepository;
  private final LocationRepository locationRepository;
  private final QuoteCalculationResultRepository quoteCalculationResultRepository;

  public ListQuotesUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository
  ) {
    this.quoteRepository = quoteRepository;
    this.locationRepository = locationRepository;
    this.quoteCalculationResultRepository = quoteCalculationResultRepository;
  }

  public List<QuoteListItem> handle() {
    return quoteRepository.findAllOrderByCreatedAtDesc().stream()
        .map(this::toItem)
        .toList();
  }

  private QuoteListItem toItem(Quote quote) {
    var locations = locationRepository.findByQuoteFolio(quote.getFolio());
    long totalInsuredValue = locations.stream().mapToLong(location -> location.getInsuredValue()).sum();
    int totalLocations = locations.size();
    Double totalPremium = quoteCalculationResultRepository.findByQuoteFolio(quote.getFolio())
        .map(result -> result.getTotalPremium())
        .orElse(null);

    return new QuoteListItem(
        quote.getFolio(),
        quote.getCustomerName(),
        totalInsuredValue,
        totalLocations,
        quote.getStatus().name(),
        quote.getCreatedAt(),
        totalPremium
    );
  }

  public record QuoteListItem(
      String folio,
      String customerName,
      long totalInsuredValue,
      int totalLocations,
      String status,
      Instant createdAt,
      Double totalPremium
  ) {
  }
}
