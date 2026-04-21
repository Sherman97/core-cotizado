package com.cotizador.danos.quote.api.dto;

public record CreateFolioResponse(
    String numeroFolio,
    String estadoCotizacion,
    int version
) {
}
