package com.cotizador.danos.quote.application;

import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteFinalStatusView;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.util.List;

public class GetQuoteFinalStatusUseCase {

  private static final QuoteCalculationResult EMPTY_CALCULATION_RESULT =
      QuoteCalculationResult.fromLocationResults(List.of(), List.of());

  private final QuoteRepository quoteRepository;
  private final QuoteCalculationResultRepository quoteCalculationResultRepository;

  public GetQuoteFinalStatusUseCase(
      QuoteRepository quoteRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository
  ) {
    this.quoteRepository = quoteRepository;
    this.quoteCalculationResultRepository = quoteCalculationResultRepository;
  }

  public QuoteFinalStatusView handle(String folio) {
    Quote quote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    QuoteCalculationResult calculationResult = quoteCalculationResultRepository.findByQuoteFolio(folio)
        .orElse(EMPTY_CALCULATION_RESULT);

    return new QuoteFinalStatusView(
        quote.getFolio(),
        quote.getStatus(),
        calculationResult.getNetPremium(),
        calculationResult.getExpenseAmount(),
        calculationResult.getTaxAmount(),
        calculationResult.getTotalPremium(),
        calculationResult.getAlerts()
    );
  }
}
