package com.cotizador.danos.quote.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record LocationLayoutRequest(
    @Min(0) int expectedLocationCount,
    boolean captureRiskZone,
    boolean captureGeoreference,
    @Size(max = 1000) String notes
) {
}
