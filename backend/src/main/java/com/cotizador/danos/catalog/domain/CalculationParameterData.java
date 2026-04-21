package com.cotizador.danos.catalog.domain;

public record CalculationParameterData(
    String parameterCode,
    double parameterValue,
    String description
) {
}
