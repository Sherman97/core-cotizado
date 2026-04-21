package com.cotizador.danos.quote.api.dto;

import jakarta.validation.constraints.Size;

public record GeneralInfoRequest(
    @Size(max = 64) String productCode,
    @Size(max = 255) String customerName,
    @Size(max = 16) String currency,
    @Size(max = 1000) String observations
) {
}
