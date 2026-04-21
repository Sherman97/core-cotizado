package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;

public class SaveQuoteUseCase {

  private final QuoteRepository quoteRepository;

  public SaveQuoteUseCase(QuoteRepository quoteRepository) {
    this.quoteRepository = quoteRepository;
  }

  public Quote handle(String folio) {
    Quote existingQuote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));
    return quoteRepository.save(existingQuote.markAsSaved());
  }
}
