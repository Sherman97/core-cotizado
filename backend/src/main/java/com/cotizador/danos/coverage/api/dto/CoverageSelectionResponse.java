package com.cotizador.danos.coverage.api.dto;

public record CoverageSelectionResponse(
    String coverageCode,
    String coverageName,
    long insuredLimit,
    String deductibleType,
    Long deductibleValue,
    boolean selected
) {
}
