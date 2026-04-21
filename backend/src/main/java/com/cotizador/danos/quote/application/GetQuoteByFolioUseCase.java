package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;

public class GetQuoteByFolioUseCase {

  private final QuoteRepository quoteRepository;

  public GetQuoteByFolioUseCase(QuoteRepository quoteRepository) {
    this.quoteRepository = quoteRepository;
  }

  public Quote handle(String folio) {
    return quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));
  }
}
