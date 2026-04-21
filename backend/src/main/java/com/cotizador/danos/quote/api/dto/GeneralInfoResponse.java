package com.cotizador.danos.quote.api.dto;

public record GeneralInfoResponse(
    String numeroFolio,
    String productCode,
    String customerName,
    String currency,
    String observations,
    String estadoCotizacion,
    int version
) {
}
