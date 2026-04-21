package com.cotizador.danos.location.domain;

public record QuoteLocationUpdatePatch(
    String locationName,
    String city,
    String department,
    String address,
    String postalCode,
    String constructionType,
    String occupancyType,
    Long insuredValue
) {
}
