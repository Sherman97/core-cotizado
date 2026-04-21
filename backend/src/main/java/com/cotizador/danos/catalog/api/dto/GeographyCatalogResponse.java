package com.cotizador.danos.catalog.api.dto;

import java.util.List;

public record GeographyCatalogResponse(
    List<DepartmentResponse> departments
) {
}
