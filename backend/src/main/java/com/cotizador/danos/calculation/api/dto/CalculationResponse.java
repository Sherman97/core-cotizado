package com.cotizador.danos.calculation.api.dto;

import java.util.List;

public record CalculationResponse(
    String numeroFolio,
    double primaNeta,
    double gastos,
    double impuestos,
    double primaComercial,
    List<CalculationLocationResultResponse> primasPorUbicacion,
    List<String> alertas
) {
}
