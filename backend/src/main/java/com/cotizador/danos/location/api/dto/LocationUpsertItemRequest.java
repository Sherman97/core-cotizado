package com.cotizador.danos.location.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record LocationUpsertItemRequest(
    @PositiveOrZero Integer locationIndex,
    @NotBlank @Size(max = 255) String locationName,
    @Size(max = 120) String city,
    @Size(max = 120) String colony,
    @Size(max = 120) String municipality,
    @Size(max = 120) String department,
    @Size(max = 255) String address,
    @Size(max = 32) String postalCode,
    @Size(max = 64) String constructionType,
    @PositiveOrZero Integer constructionLevel,
    @PositiveOrZero Integer constructionYear,
    @Size(max = 64) String occupancyType,
    @Size(max = 64) String fireKey,
    Boolean catastrophicZone,
    @PositiveOrZero long insuredValue,
    java.util.List<String> guarantees
) {
}
