package com.cotizador.danos.location.api.dto;

import java.util.List;

public record LocationSummaryResponse(
    int totalUbicaciones,
    int ubicacionesCompletas,
    int ubicacionesIncompletas,
    int ubicacionesInvalidas,
    int ubicacionesCalculadas,
    double primaNetaCalculada,
    List<String> alertas
) {
}
