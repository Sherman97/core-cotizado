package com.cotizador.danos.quote.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteFinalStatusView;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import com.cotizador.danos.quote.domain.QuoteStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetQuoteFinalStatusUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final String ALERT = "Location Sucursal Norte skipped due to incomplete data";

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private QuoteCalculationResultRepository quoteCalculationResultRepository;

  @Test
  void shouldReturnCurrentStatusPremiumSummaryAndRelevantAlerts() {
    Quote calculatedQuote = calculatedQuote();
    QuoteCalculationResult calculationResult = calculationResultWithAlert();
    GetQuoteFinalStatusUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(calculatedQuote));
    when(quoteCalculationResultRepository.findByQuoteFolio(FOLIO)).thenReturn(Optional.of(calculationResult));

    QuoteFinalStatusView result = useCase.handle(FOLIO);

    assertThat(result.folio()).isEqualTo(FOLIO);
    assertThat(result.status()).isEqualTo(QuoteStatus.CALCULATED);
    assertThat(result.netPremium()).isEqualTo(calculationResult.getNetPremium());
    assertThat(result.expenseAmount()).isEqualTo(calculationResult.getExpenseAmount());
    assertThat(result.taxAmount()).isEqualTo(calculationResult.getTaxAmount());
    assertThat(result.totalPremium()).isEqualTo(calculationResult.getTotalPremium());
    assertThat(result.alerts()).contains(ALERT);
    verify(quoteRepository).findByFolio(FOLIO);
    verify(quoteCalculationResultRepository).findByQuoteFolio(FOLIO);
  }

  @Test
  void shouldThrowControlledErrorWhenQuoteDoesNotExist() {
    GetQuoteFinalStatusUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);

    verify(quoteRepository).findByFolio(FOLIO);
  }

  private GetQuoteFinalStatusUseCase newUseCase() {
    return new GetQuoteFinalStatusUseCase(
        quoteRepository,
        quoteCalculationResultRepository
    );
  }

  private Quote calculatedQuote() {
    return Quote.createNew(FOLIO, CREATED_AT).markAsCalculated();
  }

  private QuoteCalculationResult calculationResultWithAlert() {
    return QuoteCalculationResult.fromLocationResults(List.of(), List.of(ALERT));
  }
}
