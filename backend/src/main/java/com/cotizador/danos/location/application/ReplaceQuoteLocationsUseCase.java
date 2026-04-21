package com.cotizador.danos.location.application;

import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class ReplaceQuoteLocationsUseCase {

  private final QuoteRepository quoteRepository;
  private final LocationRepository locationRepository;
  private final Clock clock;

  public ReplaceQuoteLocationsUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      Clock clock
  ) {
    this.quoteRepository = quoteRepository;
    this.locationRepository = locationRepository;
    this.clock = clock;
  }

  public List<QuoteLocation> handle(String folio, List<QuoteLocationPatch> locations) {
    var quote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    locationRepository.deleteByQuoteFolio(folio);

    List<QuoteLocation> savedLocations = locations.stream()
        .map(location -> QuoteLocation.create(locationRepository.nextId(), folio, location))
        .map(locationRepository::save)
        .toList();
    quoteRepository.save(quote.incrementBusinessVersion(Instant.now(clock)));
    return savedLocations;
  }
}
