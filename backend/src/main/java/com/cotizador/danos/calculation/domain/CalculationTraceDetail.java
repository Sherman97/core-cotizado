package com.cotizador.danos.calculation.domain;

import java.util.Map;

public record CalculationTraceDetail(
    String quoteFolio,
    Long locationId,
    String factorType,
    double appliedValue,
    int factorOrder,
    Map<String, String> metadata
) {
}
