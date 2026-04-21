package com.cotizador.danos.coverage.api.dto;

public record CoverageCatalogItemResponse(
    String code,
    String name,
    boolean active
) {
}
