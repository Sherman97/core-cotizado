package com.cotizador.danos.location.api.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdateLocationRequest(
    @Size(max = 255) String locationName,
    @Size(max = 120) String city,
    @Size(max = 120) String department,
    @Size(max = 255) String address,
    @Size(max = 32) String postalCode,
    @Size(max = 64) String constructionType,
    @Size(max = 64) String occupancyType,
    @PositiveOrZero Long insuredValue
) {
}
