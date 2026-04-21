package com.cotizador.danos.location.application;

import com.cotizador.danos.location.domain.LocationNotFoundException;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationUpdatePatch;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;

public class UpdateQuoteLocationUseCase {

  private final LocationRepository locationRepository;
  private final QuoteRepository quoteRepository;
  private final Clock clock;

  public UpdateQuoteLocationUseCase(
      LocationRepository locationRepository,
      QuoteRepository quoteRepository,
      Clock clock
  ) {
    this.locationRepository = locationRepository;
    this.quoteRepository = quoteRepository;
    this.clock = clock;
  }

  public QuoteLocation handle(String folio, long locationId, QuoteLocationUpdatePatch patch) {
    var quote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    QuoteLocation existingLocation = locationRepository.findByQuoteFolioAndId(folio, locationId)
        .orElseThrow(() -> new LocationNotFoundException(locationId));

    QuoteLocation updatedLocation = existingLocation.update(patch);
    QuoteLocation savedLocation = locationRepository.save(updatedLocation);
    quoteRepository.save(quote.incrementBusinessVersion(Instant.now(clock)));
    return savedLocation;
  }
}
