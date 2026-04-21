package com.cotizador.danos.catalog.domain;

public record CoverageFactorData(
    String productCode,
    String coverageCode,
    double factorValue
) {
}
