package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.FolioGenerator;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;

public class CreateQuoteUseCase {

  private final QuoteRepository quoteRepository;
  private final FolioGenerator folioGenerator;
  private final Clock clock;

  public CreateQuoteUseCase(
      QuoteRepository quoteRepository,
      FolioGenerator folioGenerator,
      Clock clock
  ) {
    this.quoteRepository = quoteRepository;
    this.folioGenerator = folioGenerator;
    this.clock = clock;
  }

  public Quote handle() {
    Quote newQuote = Quote.createNew(generateUniqueFolio(), Instant.now(clock));
    return quoteRepository.save(newQuote);
  }

  private String generateUniqueFolio() {
    String folio = folioGenerator.generate();
    while (quoteRepository.existsByFolio(folio)) {
      folio = folioGenerator.generate();
    }
    return folio;
  }
}
