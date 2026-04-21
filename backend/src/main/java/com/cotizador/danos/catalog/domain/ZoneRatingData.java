package com.cotizador.danos.catalog.domain;

public record ZoneRatingData(
    String postalCode,
    String zoneCode,
    String zoneName,
    double factorValue
) {
}
