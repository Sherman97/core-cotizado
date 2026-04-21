package com.cotizador.danos.quote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.quote.domain.FolioGenerator;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import com.cotizador.danos.quote.domain.QuoteStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateQuoteUseCaseTest {

  private static final Instant CREATED_AT = Instant.parse("2026-04-20T11:30:00Z");

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private FolioGenerator folioGenerator;

  @Test
  void shouldCreateAndPersistDraftQuoteWithUniqueFolio() {
    CreateQuoteUseCase createQuoteUseCase = newUseCase();

    when(folioGenerator.generate()).thenReturn("FOLIO-001");
    when(quoteRepository.existsByFolio("FOLIO-001")).thenReturn(false);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Quote createdQuote = createQuoteUseCase.handle();

    assertDraftQuote(createdQuote, "FOLIO-001");
    verify(quoteRepository).save(any(Quote.class));
  }

  @Test
  void shouldRegenerateFolioWhenGeneratedValueAlreadyExists() {
    CreateQuoteUseCase createQuoteUseCase = newUseCase();

    when(folioGenerator.generate()).thenReturn("FOLIO-001", "FOLIO-002");
    when(quoteRepository.existsByFolio("FOLIO-001")).thenReturn(true);
    when(quoteRepository.existsByFolio("FOLIO-002")).thenReturn(false);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Quote createdQuote = createQuoteUseCase.handle();

    assertDraftQuote(createdQuote, "FOLIO-002");
    verify(folioGenerator, times(2)).generate();
    verify(quoteRepository).save(any(Quote.class));
  }

  private CreateQuoteUseCase newUseCase() {
    Clock clock = Clock.fixed(CREATED_AT, ZoneOffset.UTC);
    return new CreateQuoteUseCase(quoteRepository, folioGenerator, clock);
  }

  private void assertDraftQuote(Quote quote, String expectedFolio) {
    assertThat(quote.getFolio()).isEqualTo(expectedFolio);
    assertThat(quote.getStatus()).isEqualTo(QuoteStatus.DRAFT);
    assertThat(quote.getVersion()).isEqualTo(Quote.INITIAL_VERSION);
    assertThat(quote.getCreatedAt()).isEqualTo(CREATED_AT);
  }
}
