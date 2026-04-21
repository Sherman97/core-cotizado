package com.cotizador.danos.quote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.quote.domain.FolioIdempotencyRepository;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateQuoteWithIdempotencyUseCaseTest {

  private static final String IDEMPOTENCY_KEY = "idem-123";
  private static final String FOLIO = "FOL-00000001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-21T12:00:00Z");

  @Mock
  private CreateQuoteUseCase createQuoteUseCase;

  @Mock
  private FolioIdempotencyRepository folioIdempotencyRepository;

  @Mock
  private QuoteRepository quoteRepository;

  @Test
  void shouldCreateAndRegisterIdempotencyKeyWhenKeyIsNew() {
    Quote createdQuote = Quote.createNew(FOLIO, CREATED_AT);
    CreateQuoteWithIdempotencyUseCase useCase = newUseCase();

    when(folioIdempotencyRepository.findFolioByIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(Optional.empty());
    when(createQuoteUseCase.handle()).thenReturn(createdQuote);

    CreateQuoteWithIdempotencyResult result = useCase.handle(IDEMPOTENCY_KEY);

    assertThat(result.replayed()).isFalse();
    assertThat(result.quote().getFolio()).isEqualTo(FOLIO);
    verify(folioIdempotencyRepository).save(IDEMPOTENCY_KEY, FOLIO, CREATED_AT);
  }

  @Test
  void shouldReplayExistingQuoteWhenIdempotencyKeyAlreadyExists() {
    Quote existingQuote = Quote.createNew(FOLIO, CREATED_AT);
    CreateQuoteWithIdempotencyUseCase useCase = newUseCase();

    when(folioIdempotencyRepository.findFolioByIdempotencyKey(IDEMPOTENCY_KEY)).thenReturn(Optional.of(FOLIO));
    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(existingQuote));

    CreateQuoteWithIdempotencyResult result = useCase.handle(IDEMPOTENCY_KEY);

    assertThat(result.replayed()).isTrue();
    assertThat(result.quote().getFolio()).isEqualTo(FOLIO);
    verify(createQuoteUseCase, never()).handle();
    verify(folioIdempotencyRepository, never()).save(IDEMPOTENCY_KEY, FOLIO, CREATED_AT);
  }

  @Test
  void shouldCreateQuoteWithoutIdempotencyWhenHeaderIsMissing() {
    Quote createdQuote = Quote.createNew(FOLIO, CREATED_AT);
    CreateQuoteWithIdempotencyUseCase useCase = newUseCase();
    when(createQuoteUseCase.handle()).thenReturn(createdQuote);

    CreateQuoteWithIdempotencyResult result = useCase.handle(null);

    assertThat(result.replayed()).isFalse();
    assertThat(result.quote().getFolio()).isEqualTo(FOLIO);
    verify(folioIdempotencyRepository, never()).findFolioByIdempotencyKey(IDEMPOTENCY_KEY);
    verify(folioIdempotencyRepository, never()).save(IDEMPOTENCY_KEY, FOLIO, CREATED_AT);
  }

  private CreateQuoteWithIdempotencyUseCase newUseCase() {
    return new CreateQuoteWithIdempotencyUseCase(
        createQuoteUseCase,
        folioIdempotencyRepository,
        quoteRepository
    );
  }
}
