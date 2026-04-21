package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import com.cotizador.danos.quote.domain.QuoteVersionHistoryItem;
import java.util.List;

public class GetQuoteVersionHistoryUseCase {

  private final QuoteRepository quoteRepository;

  public GetQuoteVersionHistoryUseCase(QuoteRepository quoteRepository) {
    this.quoteRepository = quoteRepository;
  }

  public List<QuoteVersionHistoryItem> handle(String rootFolio) {
    ensureRootQuoteExists(rootFolio);

    return quoteRepository.findVersionsByRootFolio(rootFolio).stream()
        .map(this::toHistoryItem)
        .toList();
  }

  public QuoteVersionHistoryItem handleVersion(String rootFolio, int version) {
    ensureRootQuoteExists(rootFolio);

    Quote quote = quoteRepository.findByRootFolioAndVersion(rootFolio, version)
        .orElseThrow(() -> new QuoteNotFoundException(rootFolio));
    return toHistoryItem(quote);
  }

  private void ensureRootQuoteExists(String rootFolio) {
    quoteRepository.findByFolio(rootFolio)
        .orElseThrow(() -> new QuoteNotFoundException(rootFolio));
  }

  private QuoteVersionHistoryItem toHistoryItem(Quote quote) {
    return new QuoteVersionHistoryItem(
        quote.getFolio(),
        quote.getVersion(),
        quote.getStatus(),
        quote.getCreatedAt()
    );
  }
}
