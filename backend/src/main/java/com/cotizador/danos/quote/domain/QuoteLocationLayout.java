package com.cotizador.danos.quote.domain;

public record QuoteLocationLayout(
    int expectedLocationCount,
    boolean captureRiskZone,
    boolean captureGeoreference,
    String notes
) {
}
