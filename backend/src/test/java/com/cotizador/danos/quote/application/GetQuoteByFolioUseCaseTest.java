package com.cotizador.danos.quote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetQuoteByFolioUseCaseTest {

  private static final String EXISTING_FOLIO = "FOLIO-001";
  private static final String UNKNOWN_FOLIO = "FOLIO-404";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T11:30:00Z");

  @Mock
  private QuoteRepository quoteRepository;

  @Test
  void shouldReturnQuoteWhenFolioExists() {
    Quote existingQuote = existingQuote();
    GetQuoteByFolioUseCase getQuoteByFolioUseCase = newUseCase();

    when(quoteRepository.findByFolio(EXISTING_FOLIO)).thenReturn(Optional.of(existingQuote));

    Quote retrievedQuote = getQuoteByFolioUseCase.handle(EXISTING_FOLIO);

    assertThat(retrievedQuote).isSameAs(existingQuote);
    assertThat(retrievedQuote.getStatus()).isEqualTo(existingQuote.getStatus());
    assertThat(retrievedQuote.getVersion()).isEqualTo(existingQuote.getVersion());
    verify(quoteRepository).findByFolio(EXISTING_FOLIO);
  }

  @Test
  void shouldThrowControlledErrorWhenFolioDoesNotExist() {
    GetQuoteByFolioUseCase getQuoteByFolioUseCase = newUseCase();

    when(quoteRepository.findByFolio(UNKNOWN_FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> getQuoteByFolioUseCase.handle(UNKNOWN_FOLIO))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(UNKNOWN_FOLIO);

    verify(quoteRepository).findByFolio(UNKNOWN_FOLIO);
  }

  private GetQuoteByFolioUseCase newUseCase() {
    return new GetQuoteByFolioUseCase(quoteRepository);
  }

  private Quote existingQuote() {
    return Quote.createNew(EXISTING_FOLIO, CREATED_AT);
  }
}
