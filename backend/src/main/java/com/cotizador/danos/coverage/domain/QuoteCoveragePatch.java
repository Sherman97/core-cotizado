package com.cotizador.danos.coverage.domain;

public record QuoteCoveragePatch(
    String coverageCode,
    String coverageName,
    long insuredLimit,
    String deductibleType,
    Long deductibleValue,
    boolean selected
) {
}
