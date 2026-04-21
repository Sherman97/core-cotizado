package com.cotizador.danos.location.api.dto;

import java.util.List;

public record LocationResponse(
    long indice,
    String nombreUbicacion,
    String ciudad,
    String departamento,
    String direccion,
    String codigoPostal,
    String tipoConstructivo,
    String ocupacion,
    long valorAsegurado,
    String estadoValidacion,
    List<String> alertas
) {
}
