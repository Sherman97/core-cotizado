package com.cotizador.danos.quote.api.dto;

public record LocationLayoutResponse(
    int expectedLocationCount,
    boolean captureRiskZone,
    boolean captureGeoreference,
    String notes
) {
}
