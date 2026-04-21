package com.cotizador.danos.location.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class InvalidQuoteLocationTest {

  private static final long LOCATION_ID = 3L;
  private static final String FOLIO = "FOLIO-001";
  private static final String LOCATION_NAME = "Bodega Temporal";
  private static final String EXCLUSION_REASON = "Location missing minimum data for calculation";

  @Test
  void shouldCreateInvalidLocationWithExclusionReason() {
    QuoteLocation invalidLocation = invalidLocation();

    assertThat(invalidLocation.getId()).isEqualTo(LOCATION_ID);
    assertThat(invalidLocation.getQuoteFolio()).isEqualTo(FOLIO);
    assertThat(invalidLocation.getLocationName()).isEqualTo(LOCATION_NAME);
    assertThat(invalidLocation.getValidationStatus()).isEqualTo(LocationValidationStatus.INVALID);
    assertThat(invalidLocation.getAlerts()).contains(EXCLUSION_REASON);
  }

  private QuoteLocation invalidLocation() {
    return QuoteLocation.invalid(
        LOCATION_ID,
        FOLIO,
        LOCATION_NAME,
        List.of(EXCLUSION_REASON)
    );
  }
}
