package com.cotizador.danos.calculation.api;

import com.cotizador.danos.calculation.api.dto.CalculationLocationResultResponse;
import com.cotizador.danos.calculation.api.dto.CalculationResponse;
import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteLocationResultView;
import org.springframework.stereotype.Component;

@Component
public class CalculationApiMapper {

  public CalculationResponse toResponse(String folio, QuoteCalculationResult result) {
    return new CalculationResponse(
        folio,
        result.getNetPremium(),
        result.getExpenseAmount(),
        result.getTaxAmount(),
        result.getTotalPremium(),
        result.getLocations().stream()
            .map(location -> new CalculationLocationResultResponse(
                location.locationId(),
                location.locationName(),
                location.status().name(),
                location.status().name().equals("COMPLETE") && location.premium() > 0.0,
                location.premium(),
                location.alerts()
            ))
            .toList(),
        result.getAlerts()
    );
  }

  public CalculationLocationResultResponse toLocationResultResponse(QuoteLocationResultView result) {
    return new CalculationLocationResultResponse(
        result.locationId(),
        result.locationName(),
        result.validationStatus().name(),
        result.calculated(),
        result.premium(),
        result.alerts()
    );
  }
}
