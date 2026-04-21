package com.cotizador.danos.calculation.api.dto;

import java.util.List;

public record CalculationLocationResultResponse(
    long indice,
    String nombreUbicacion,
    String estadoValidacion,
    boolean calculada,
    double prima,
    List<String> alertas
) {
}
