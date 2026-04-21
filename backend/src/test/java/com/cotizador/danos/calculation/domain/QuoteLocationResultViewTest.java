package com.cotizador.danos.calculation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.cotizador.danos.location.domain.LocationValidationStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class QuoteLocationResultViewTest {

  private static final long LOCATION_ID = 1L;
  private static final String LOCATION_NAME = "Matriz Centro";

  @Test
  void shouldCreateLocationResultViewWithPremiumAndValidationStatus() {
    QuoteLocationResultView resultView = resultView();

    assertThat(resultView.locationId()).isEqualTo(LOCATION_ID);
    assertThat(resultView.locationName()).isEqualTo(LOCATION_NAME);
    assertThat(resultView.validationStatus()).isEqualTo(LocationValidationStatus.COMPLETE);
    assertThat(resultView.premium()).isEqualTo(1000.0);
    assertThat(resultView.calculated()).isTrue();
    assertThat(resultView.alerts()).isEmpty();
  }

  private QuoteLocationResultView resultView() {
    return new QuoteLocationResultView(
        LOCATION_ID,
        LOCATION_NAME,
        LocationValidationStatus.COMPLETE,
        1000.0,
        true,
        List.of()
    );
  }
}
