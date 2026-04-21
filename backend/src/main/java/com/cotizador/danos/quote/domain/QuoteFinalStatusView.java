package com.cotizador.danos.quote.domain;

import java.util.List;

public record QuoteFinalStatusView(
    String folio,
    QuoteStatus status,
    double netPremium,
    double expenseAmount,
    double taxAmount,
    double totalPremium,
    List<String> alerts
) {
}
