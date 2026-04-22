package com.cotizador.danos.location.domain;

import java.util.List;

public record QuoteLocationUpdatePatch(
    Integer locationIndex,
    String locationName,
    String city,
    String colony,
    String municipality,
    String department,
    String address,
    String postalCode,
    String constructionType,
    Integer constructionLevel,
    Integer constructionYear,
    String occupancyType,
    String fireKey,
    Boolean catastrophicZone,
    Long insuredValue,
    List<String> guarantees
) {
}
