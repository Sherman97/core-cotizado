package com.cotizador.danos.quote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import com.cotizador.danos.quote.domain.QuoteStatus;
import com.cotizador.danos.quote.domain.QuoteVersionHistoryItem;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetQuoteVersionHistoryUseCaseTest {

  private static final String ROOT_FOLIO = "FOLIO-001";
  private static final Instant ROOT_CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final String VERSION_TWO_FOLIO = "FOLIO-001-V2";
  private static final Instant VERSION_TWO_CREATED_AT = Instant.parse("2026-04-20T11:20:00Z");

  @Mock
  private QuoteRepository quoteRepository;

  @Test
  void shouldReturnVersionHistoryWithVersionDateAndStatus() {
    Quote rootQuote = rootQuote();
    Quote versionTwo = versionTwo(rootQuote);
    GetQuoteVersionHistoryUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(ROOT_FOLIO)).thenReturn(Optional.of(rootQuote));
    when(quoteRepository.findVersionsByRootFolio(ROOT_FOLIO)).thenReturn(List.of(rootQuote, versionTwo));

    List<QuoteVersionHistoryItem> history = useCase.handle(ROOT_FOLIO);

    assertThat(history).hasSize(2);
    assertThat(history).extracting(QuoteVersionHistoryItem::version).containsExactly(1, 2);
    assertThat(history).extracting(QuoteVersionHistoryItem::status).containsExactly(QuoteStatus.CALCULATED, QuoteStatus.DRAFT);
    assertThat(history).extracting(QuoteVersionHistoryItem::createdAt)
        .containsExactly(ROOT_CREATED_AT, VERSION_TWO_CREATED_AT);
  }

  @Test
  void shouldReturnSpecificVersionWhenRequested() {
    Quote rootQuote = rootQuote();
    Quote versionTwo = versionTwo(rootQuote);
    GetQuoteVersionHistoryUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(ROOT_FOLIO)).thenReturn(Optional.of(rootQuote));
    when(quoteRepository.findByRootFolioAndVersion(ROOT_FOLIO, 2)).thenReturn(Optional.of(versionTwo));

    QuoteVersionHistoryItem version = useCase.handleVersion(ROOT_FOLIO, 2);

    assertThat(version.folio()).isEqualTo(VERSION_TWO_FOLIO);
    assertThat(version.version()).isEqualTo(2);
    assertThat(version.status()).isEqualTo(QuoteStatus.DRAFT);
  }

  @Test
  void shouldThrowControlledErrorWhenQuoteDoesNotExist() {
    GetQuoteVersionHistoryUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(ROOT_FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(ROOT_FOLIO))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(ROOT_FOLIO);

    verify(quoteRepository).findByFolio(ROOT_FOLIO);
  }

  private GetQuoteVersionHistoryUseCase newUseCase() {
    return new GetQuoteVersionHistoryUseCase(quoteRepository);
  }

  private Quote rootQuote() {
    return Quote.createNew(ROOT_FOLIO, ROOT_CREATED_AT).markAsCalculated();
  }

  private Quote versionTwo(Quote rootQuote) {
    return rootQuote.createNewVersion(VERSION_TWO_FOLIO, VERSION_TWO_CREATED_AT);
  }
}
