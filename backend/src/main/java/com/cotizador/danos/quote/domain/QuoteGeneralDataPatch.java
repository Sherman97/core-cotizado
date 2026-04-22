package com.cotizador.danos.quote.domain;

public record QuoteGeneralDataPatch(
    String productCode,
    String customerName,
    String currency,
    String observations,
    String agentCode,
    String agentNameSnapshot,
    String riskClassification,
    String businessType
) {
}
