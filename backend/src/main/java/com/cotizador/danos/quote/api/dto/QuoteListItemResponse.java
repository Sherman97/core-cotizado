package com.cotizador.danos.quote.api.dto;

import java.time.Instant;

public record QuoteListItemResponse(
    String folio,
    String customerName,
    long totalInsuredValue,
    int totalLocations,
    String status,
    Instant createdAt,
    Double totalPremium
) {
}
