package com.cotizador.danos.calculation.domain;

import com.cotizador.danos.location.domain.LocationValidationStatus;
import java.util.List;

public record LocationCalculationResult(
    long locationId,
    String locationName,
    LocationValidationStatus status,
    double premium,
    List<String> alerts
) {
}
