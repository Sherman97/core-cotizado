package com.cotizador.danos.location.domain;

import java.util.List;

public record QuoteLocationPatch(
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
    long insuredValue,
    List<String> guarantees
) {

  public boolean hasRequiredData() {
    return hasValue(address) && hasValue(postalCode);
  }

  private boolean hasValue(String value) {
    return value != null && !value.isBlank();
  }
}
