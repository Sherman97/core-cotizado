package com.cotizador.danos.catalog.domain;

public record OccupancyRatingData(
    String occupancyCode,
    String fireKey,
    double factorValue
) {
}
