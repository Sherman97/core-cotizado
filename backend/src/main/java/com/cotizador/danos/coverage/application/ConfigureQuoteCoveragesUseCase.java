package com.cotizador.danos.coverage.application;

import com.cotizador.danos.coverage.domain.QuoteCoveragePatch;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class ConfigureQuoteCoveragesUseCase {

  private final QuoteRepository quoteRepository;
  private final QuoteCoverageRepository quoteCoverageRepository;
  private final Clock clock;

  public ConfigureQuoteCoveragesUseCase(
      QuoteRepository quoteRepository,
      QuoteCoverageRepository quoteCoverageRepository,
      Clock clock
  ) {
    this.quoteRepository = quoteRepository;
    this.quoteCoverageRepository = quoteCoverageRepository;
    this.clock = clock;
  }

  public List<QuoteCoverageSelection> handle(String folio, List<QuoteCoveragePatch> requestedCoverages) {
    var quote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    List<QuoteCoverageSelection> replaced = quoteCoverageRepository.replaceForQuote(folio, requestedCoverages);
    quoteRepository.save(quote.incrementBusinessVersion(Instant.now(clock)));
    return replaced;
  }
}
