package com.cotizador.danos.quote.domain;

import java.time.Instant;

public record QuoteVersionHistoryItem(
    String folio,
    int version,
    QuoteStatus status,
    Instant createdAt
) {
}
