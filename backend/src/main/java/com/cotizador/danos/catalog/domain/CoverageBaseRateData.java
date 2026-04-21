package com.cotizador.danos.catalog.domain;

public record CoverageBaseRateData(
    String productCode,
    String coverageCode,
    double baseRate
) {
}
