package com.cotizador.danos.coverage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.coverage.domain.QuoteCoveragePatch;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigureQuoteCoveragesUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final Instant MODIFIED_AT = Instant.parse("2026-04-20T11:30:00Z");

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private QuoteCoverageRepository quoteCoverageRepository;

  @Test
  void shouldSaveSelectedCoveragesAssociatedToQuote() {
    ConfigureQuoteCoveragesUseCase useCase = newUseCase();
    List<QuoteCoveragePatch> requestedCoverages = requestedCoverages();

    when(quoteRepository.findByFolio(FOLIO))
        .thenReturn(Optional.of(Quote.createNew(FOLIO, CREATED_AT)));
    when(quoteRepository.save(org.mockito.ArgumentMatchers.any(Quote.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(quoteCoverageRepository.replaceForQuote(FOLIO, requestedCoverages))
        .thenReturn(List.of(
            QuoteCoverageSelection.create(FOLIO, requestedCoverages.get(0)),
            QuoteCoverageSelection.create(FOLIO, requestedCoverages.get(1))
        ));

    List<QuoteCoverageSelection> savedCoverages = useCase.handle(FOLIO, requestedCoverages);

    assertThat(savedCoverages).hasSize(2);
    assertThat(savedCoverages).extracting(QuoteCoverageSelection::getCoverageCode)
        .containsExactly("INCENDIO", "TERREMOTO");
    ArgumentCaptor<Quote> savedQuoteCaptor = ArgumentCaptor.forClass(Quote.class);
    verify(quoteRepository).save(savedQuoteCaptor.capture());
    assertThat(savedQuoteCaptor.getValue().getVersion()).isEqualTo(2);
    assertThat(savedQuoteCaptor.getValue().getModifiedAt()).isEqualTo(MODIFIED_AT);
    verify(quoteRepository).findByFolio(FOLIO);
    verify(quoteCoverageRepository).replaceForQuote(FOLIO, requestedCoverages);
  }

  @Test
  void shouldUpdatePreviouslyConfiguredCoverages() {
    ConfigureQuoteCoveragesUseCase useCase = newUseCase();
    List<QuoteCoveragePatch> requestedCoverages = List.of(fireCoverage(1200000L, 60000L));

    when(quoteRepository.findByFolio(FOLIO))
        .thenReturn(Optional.of(Quote.createNew(FOLIO, CREATED_AT)));
    when(quoteRepository.save(org.mockito.ArgumentMatchers.any(Quote.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(quoteCoverageRepository.replaceForQuote(FOLIO, requestedCoverages))
        .thenReturn(List.of(
            QuoteCoverageSelection.create(FOLIO, requestedCoverages.get(0))
        ));

    List<QuoteCoverageSelection> updatedCoverages = useCase.handle(FOLIO, requestedCoverages);

    assertThat(updatedCoverages).hasSize(1);
    assertThat(updatedCoverages.get(0).getInsuredLimit()).isEqualTo(1200000L);
    assertThat(updatedCoverages.get(0).getDeductibleValue()).isEqualTo(60000L);
    verify(quoteCoverageRepository).replaceForQuote(FOLIO, requestedCoverages);
  }

  @Test
  void shouldThrowControlledErrorWhenQuoteDoesNotExist() {
    ConfigureQuoteCoveragesUseCase useCase = newUseCase();
    List<QuoteCoveragePatch> requestedCoverages = List.of(fireCoverage(1000000L, 50000L));

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO, requestedCoverages))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);

    verify(quoteRepository).findByFolio(FOLIO);
  }

  private ConfigureQuoteCoveragesUseCase newUseCase() {
    return new ConfigureQuoteCoveragesUseCase(
        quoteRepository,
        quoteCoverageRepository,
        Clock.fixed(MODIFIED_AT, ZoneOffset.UTC)
    );
  }

  private List<QuoteCoveragePatch> requestedCoverages() {
    return List.of(
        fireCoverage(1000000L, 50000L),
        new QuoteCoveragePatch("TERREMOTO", "Terremoto", 800000L, null, null, true)
    );
  }

  private QuoteCoveragePatch fireCoverage(long insuredLimit, long deductibleValue) {
    return new QuoteCoveragePatch("INCENDIO", "Incendio", insuredLimit, "FIXED", deductibleValue, true);
  }
}
