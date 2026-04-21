package com.cotizador.danos.quote.api.dto;

public record GeneralInfoResponse(
    String numeroFolio,
    String productCode,
    String customerName,
    String currency,
    String agentCode,
    String agentNameSnapshot,
    String observations,
    String estadoCotizacion,
    int version
) {
}
