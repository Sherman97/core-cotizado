package com.cotizador.danos.calculation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.cotizador.danos.location.domain.LocationValidationStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuoteCalculationResultTest {

  @Test
  void shouldConsolidateNetAndCommercialPremiumFromValidLocations() {
    QuoteCalculationResult result = QuoteCalculationResult.fromLocationResults(
        List.of(
            validLocationResult(1L, "Matriz Centro", 1000.0),
            validLocationResult(2L, "Sucursal Norte", 500.0)
        ),
        List.of()
    );

    assertThat(result.getNetPremium()).isEqualTo(1500.0);
    assertThat(result.getExpenseAmount()).isEqualTo(150.0);
    assertThat(result.getTaxAmount()).isEqualTo(264.0);
    assertThat(result.getTotalPremium()).isEqualTo(1914.0);
    assertThat(result.getLocations()).hasSize(2);
    assertThat(result.getAlerts()).isEmpty();
  }

  @Test
  void shouldKeepAlertsForIncompleteLocationsAndReturnPartialResult() {
    QuoteCalculationResult result = QuoteCalculationResult.fromLocationResults(
        List.of(
            validLocationResult(1L, "Matriz Centro", 1000.0),
            incompleteLocationResult(2L, "Sucursal Norte")
        ),
        List.of("Location Sucursal Norte skipped due to incomplete data")
    );

    assertThat(result.getNetPremium()).isEqualTo(1000.0);
    assertThat(result.getLocations()).hasSize(2);
    assertThat(result.getAlerts()).contains("Location Sucursal Norte skipped due to incomplete data");
  }

  private LocationCalculationResult validLocationResult(long id, String name, double premium) {
    return new LocationCalculationResult(id, name, LocationValidationStatus.COMPLETE, premium, List.of());
  }

  private LocationCalculationResult incompleteLocationResult(long id, String name) {
    return new LocationCalculationResult(
        id,
        name,
        LocationValidationStatus.INCOMPLETE,
        0.0,
        List.of("Location has incomplete required data")
    );
  }
}
