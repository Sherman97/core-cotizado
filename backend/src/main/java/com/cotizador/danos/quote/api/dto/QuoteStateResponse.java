package com.cotizador.danos.quote.api.dto;

import java.util.List;

public record QuoteStateResponse(
    String numeroFolio,
    String estadoCotizacion,
    double primaNeta,
    double gastos,
    double impuestos,
    double primaComercial,
    List<String> alertas
) {
}
