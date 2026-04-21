package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteLocationLayout;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;

public class SaveQuoteLocationLayoutUseCase {

  private final QuoteRepository quoteRepository;
  private final Clock clock;

  public SaveQuoteLocationLayoutUseCase(QuoteRepository quoteRepository, Clock clock) {
    this.quoteRepository = quoteRepository;
    this.clock = clock;
  }

  public Quote handle(String folio, QuoteLocationLayout locationLayout) {
    Quote existingQuote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    Quote updatedQuote = existingQuote.updateLocationLayout(locationLayout, Instant.now(clock));
    return quoteRepository.save(updatedQuote);
  }
}
