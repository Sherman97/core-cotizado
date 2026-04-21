package com.cotizador.danos.calculation.domain;

import com.cotizador.danos.location.domain.LocationValidationStatus;
import java.util.List;

public record QuoteLocationResultView(
    long locationId,
    String locationName,
    LocationValidationStatus validationStatus,
    double premium,
    boolean calculated,
    List<String> alerts
) {
}
