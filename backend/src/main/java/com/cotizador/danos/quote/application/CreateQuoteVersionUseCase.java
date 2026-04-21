package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.FolioGenerator;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;

public class CreateQuoteVersionUseCase {

  private final QuoteRepository quoteRepository;
  private final FolioGenerator folioGenerator;
  private final Clock clock;

  public CreateQuoteVersionUseCase(
      QuoteRepository quoteRepository,
      FolioGenerator folioGenerator,
      Clock clock
  ) {
    this.quoteRepository = quoteRepository;
    this.folioGenerator = folioGenerator;
    this.clock = clock;
  }

  public Quote handle(String parentFolio) {
    Quote previousVersion = loadPreviousVersion(parentFolio);
    Quote newVersion = createVersionFrom(previousVersion);
    return quoteRepository.save(newVersion);
  }

  private Quote loadPreviousVersion(String parentFolio) {
    return quoteRepository.findByFolio(parentFolio)
        .orElseThrow(() -> new QuoteNotFoundException(parentFolio));
  }

  private Quote createVersionFrom(Quote previousVersion) {
    return previousVersion.createNewVersion(
        generateUniqueFolio(),
        Instant.now(clock)
    );
  }

  private String generateUniqueFolio() {
    String folio = folioGenerator.generate();
    while (quoteRepository.existsByFolio(folio)) {
      folio = folioGenerator.generate();
    }
    return folio;
  }
}
