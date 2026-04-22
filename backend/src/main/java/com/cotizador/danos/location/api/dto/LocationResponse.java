package com.cotizador.danos.location.api.dto;

import java.util.List;

public record LocationResponse(
    long indice,
    String nombreUbicacion,
    String ciudad,
    String colonia,
    String municipio,
    String departamento,
    String direccion,
    String codigoPostal,
    String tipoConstructivo,
    Integer nivelConstruccion,
    Integer anioConstruccion,
    String ocupacion,
    String claveBomberos,
    Boolean zonaCatastrofica,
    long valorAsegurado,
    List<String> garantias,
    String estadoValidacion,
    List<String> alertas
) {
}
