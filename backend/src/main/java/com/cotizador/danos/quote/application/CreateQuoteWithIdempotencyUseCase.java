package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.FolioIdempotencyRepository;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class CreateQuoteWithIdempotencyUseCase {

  private final CreateQuoteUseCase createQuoteUseCase;
  private final FolioIdempotencyRepository folioIdempotencyRepository;
  private final QuoteRepository quoteRepository;

  public CreateQuoteWithIdempotencyUseCase(
      CreateQuoteUseCase createQuoteUseCase,
      FolioIdempotencyRepository folioIdempotencyRepository,
      QuoteRepository quoteRepository
  ) {
    this.createQuoteUseCase = createQuoteUseCase;
    this.folioIdempotencyRepository = folioIdempotencyRepository;
    this.quoteRepository = quoteRepository;
  }

  @Transactional
  public CreateQuoteWithIdempotencyResult handle(String idempotencyKey) {
    if (!StringUtils.hasText(idempotencyKey)) {
      return new CreateQuoteWithIdempotencyResult(createQuoteUseCase.handle(), false);
    }

    var existingFolio = folioIdempotencyRepository.findFolioByIdempotencyKey(idempotencyKey);
    if (existingFolio.isPresent()) {
      Quote existingQuote = quoteRepository.findByFolio(existingFolio.get())
          .orElseThrow(() -> new QuoteNotFoundException(existingFolio.get()));
      return new CreateQuoteWithIdempotencyResult(existingQuote, true);
    }

    Quote createdQuote = createQuoteUseCase.handle();
    folioIdempotencyRepository.save(idempotencyKey, createdQuote.getFolio(), createdQuote.getCreatedAt());
    return new CreateQuoteWithIdempotencyResult(createdQuote, false);
  }
}
