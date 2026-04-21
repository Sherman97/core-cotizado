package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteGeneralDataPatch;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;

public class UpdateQuoteGeneralDataUseCase {

  private final QuoteRepository quoteRepository;
  private final Clock clock;

  public UpdateQuoteGeneralDataUseCase(QuoteRepository quoteRepository, Clock clock) {
    this.quoteRepository = quoteRepository;
    this.clock = clock;
  }

  public Quote handle(String folio, QuoteGeneralDataPatch patch) {
    Quote existingQuote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    Quote updatedQuote = existingQuote.updateGeneralData(patch, Instant.now(clock));
    return quoteRepository.save(updatedQuote);
  }
}
