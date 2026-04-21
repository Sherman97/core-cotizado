package com.cotizador.danos.location.domain;

public record QuoteLocationPatch(
    String locationName,
    String city,
    String department,
    String address,
    String postalCode,
    String constructionType,
    String occupancyType,
    long insuredValue
) {

  public boolean hasRequiredData() {
    return hasValue(address) && hasValue(postalCode);
  }

  private boolean hasValue(String value) {
    return value != null && !value.isBlank();
  }
}
