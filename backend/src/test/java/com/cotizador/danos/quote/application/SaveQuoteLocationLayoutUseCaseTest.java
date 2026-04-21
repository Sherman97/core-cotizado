package com.cotizador.danos.quote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteLocationLayout;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaveQuoteLocationLayoutUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final Instant MODIFIED_AT = Instant.parse("2026-04-20T11:30:00Z");

  @Mock
  private QuoteRepository quoteRepository;

  @Test
  void shouldSaveLocationLayoutForExistingQuote() {
    Quote existingQuote = Quote.createNew(FOLIO, CREATED_AT);
    QuoteLocationLayout layout = locationLayout();
    SaveQuoteLocationLayoutUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(existingQuote));
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Quote updatedQuote = useCase.handle(FOLIO, layout);

    assertThat(updatedQuote.getFolio()).isEqualTo(FOLIO);
    assertThat(updatedQuote.getLocationLayout()).isEqualTo(layout);
    assertThat(updatedQuote.getVersion()).isEqualTo(2);
    assertThat(updatedQuote.getModifiedAt()).isEqualTo(MODIFIED_AT);
    verify(quoteRepository).findByFolio(FOLIO);
    verify(quoteRepository).save(updatedQuote);
  }

  @Test
  void shouldThrowControlledErrorWhenQuoteDoesNotExist() {
    QuoteLocationLayout layout = locationLayout();
    SaveQuoteLocationLayoutUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO, layout))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);

    verify(quoteRepository).findByFolio(FOLIO);
  }

  private SaveQuoteLocationLayoutUseCase newUseCase() {
    return new SaveQuoteLocationLayoutUseCase(
        quoteRepository,
        Clock.fixed(MODIFIED_AT, ZoneOffset.UTC)
    );
  }

  private QuoteLocationLayout locationLayout() {
    return new QuoteLocationLayout(3, true, false, "Captura inicial");
  }
}
