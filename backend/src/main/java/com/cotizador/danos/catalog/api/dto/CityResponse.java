package com.cotizador.danos.catalog.api.dto;

import java.util.List;

public record CityResponse(
    String code,
    String name,
    List<String> postalCodes
) {
}
