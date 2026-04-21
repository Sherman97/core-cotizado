package com.cotizador.danos.location.application;

import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.util.List;

public class ListQuoteLocationsUseCase {

  private final QuoteRepository quoteRepository;
  private final LocationRepository locationRepository;

  public ListQuoteLocationsUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository
  ) {
    this.quoteRepository = quoteRepository;
    this.locationRepository = locationRepository;
  }

  public List<QuoteLocation> handle(String folio) {
    quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    return locationRepository.findByQuoteFolio(folio);
  }
}
