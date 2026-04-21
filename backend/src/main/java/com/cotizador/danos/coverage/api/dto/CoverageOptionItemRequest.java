package com.cotizador.danos.coverage.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CoverageOptionItemRequest(
    @NotBlank @Size(max = 64) String coverageCode,
    @NotBlank @Size(max = 255) String coverageName,
    @PositiveOrZero long insuredLimit,
    @Size(max = 64) String deductibleType,
    @PositiveOrZero Long deductibleValue,
    boolean selected
) {
}
