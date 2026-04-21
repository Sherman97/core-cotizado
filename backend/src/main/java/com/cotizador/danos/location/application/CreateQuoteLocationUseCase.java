package com.cotizador.danos.location.application;

import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;

public class CreateQuoteLocationUseCase {

  private final QuoteRepository quoteRepository;
  private final LocationRepository locationRepository;

  public CreateQuoteLocationUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository
  ) {
    this.quoteRepository = quoteRepository;
    this.locationRepository = locationRepository;
  }

  public QuoteLocation handle(String folio, QuoteLocationPatch patch) {
    quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    QuoteLocation location = QuoteLocation.create(locationRepository.nextId(), folio, patch);
    return locationRepository.save(location);
  }
}
