package com.cotizador.danos.quote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.quote.domain.FolioGenerator;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import com.cotizador.danos.quote.domain.QuoteStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateQuoteVersionUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final String NEW_VERSION_FOLIO = "FOLIO-001-V2";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final Clock CLOCK = Clock.fixed(
      Instant.parse("2026-04-20T11:20:00Z"),
      ZoneOffset.UTC
  );

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private FolioGenerator folioGenerator;

  @Test
  void shouldCreateAndPersistNewVersionLinkedToPreviousQuote() {
    Quote previousVersion = previousVersion();
    CreateQuoteVersionUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(previousVersion));
    when(folioGenerator.generate()).thenReturn(NEW_VERSION_FOLIO);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Quote newVersion = useCase.handle(FOLIO);

    assertThat(newVersion.getFolio()).isEqualTo(NEW_VERSION_FOLIO);
    assertThat(newVersion.getParentQuoteFolio()).isEqualTo(FOLIO);
    assertThat(newVersion.getVersion()).isEqualTo(previousVersion.getVersion() + 1);
    assertThat(newVersion.getStatus()).isEqualTo(QuoteStatus.DRAFT);
    verify(quoteRepository).findByFolio(FOLIO);
    verify(quoteRepository).save(any(Quote.class));
  }

  @Test
  void shouldThrowControlledErrorWhenPreviousQuoteDoesNotExist() {
    CreateQuoteVersionUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);

    verify(quoteRepository).findByFolio(FOLIO);
  }

  @Test
  void shouldGenerateAnotherFolioWhenFirstOneAlreadyExists() {
    Quote previousVersion = previousVersion();
    CreateQuoteVersionUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(previousVersion));
    when(folioGenerator.generate()).thenReturn("FOLIO-DUPLICATED", NEW_VERSION_FOLIO);
    when(quoteRepository.existsByFolio("FOLIO-DUPLICATED")).thenReturn(true);
    when(quoteRepository.existsByFolio(NEW_VERSION_FOLIO)).thenReturn(false);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Quote newVersion = useCase.handle(FOLIO);

    assertThat(newVersion.getFolio()).isEqualTo(NEW_VERSION_FOLIO);
    verify(folioGenerator, times(2)).generate();
    verify(quoteRepository).existsByFolio("FOLIO-DUPLICATED");
    verify(quoteRepository).existsByFolio(NEW_VERSION_FOLIO);
  }

  private Quote previousVersion() {
    return Quote.createNew(FOLIO, CREATED_AT).markAsCalculated();
  }

  private CreateQuoteVersionUseCase newUseCase() {
    return new CreateQuoteVersionUseCase(
        quoteRepository,
        folioGenerator,
        CLOCK
    );
  }
}
