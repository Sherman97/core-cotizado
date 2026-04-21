package com.cotizador.danos.catalog.api.dto;

import java.util.List;

public record DepartmentResponse(
    String code,
    String name,
    List<CityResponse> cities
) {
}
