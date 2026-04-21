package com.cotizador.danos.location.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QuoteLocationTest {

  private static final String FOLIO = "FOLIO-001";

  @Test
  void shouldMarkLocationAsCompleteWhenAllRequiredDataIsPresent() {
    QuoteLocationPatch patch = completePatch();

    QuoteLocation location = QuoteLocation.create(1L, FOLIO, patch);

    assertThat(location.getQuoteFolio()).isEqualTo(FOLIO);
    assertThat(location.getLocationName()).isEqualTo("Matriz Centro");
    assertThat(location.getValidationStatus()).isEqualTo(LocationValidationStatus.COMPLETE);
    assertThat(location.getAlerts()).isEmpty();
  }

  @Test
  void shouldMarkLocationAsIncompleteAndGenerateAlertWhenRequiredDataIsMissing() {
    QuoteLocationPatch patch = incompletePatch();

    QuoteLocation location = QuoteLocation.create(2L, FOLIO, patch);

    assertThat(location.getValidationStatus()).isEqualTo(LocationValidationStatus.INCOMPLETE);
    assertThat(location.getAlerts()).contains("Location has incomplete required data");
  }

  private QuoteLocationPatch completePatch() {
    return new QuoteLocationPatch(
        "Matriz Centro",
        "Bogota",
        "Cundinamarca",
        "Calle 100 #10-20",
        "110111",
        "CONCRETE",
        "OFFICE",
        1500000
    );
  }

  private QuoteLocationPatch incompletePatch() {
    return new QuoteLocationPatch(
        "Sucursal Norte",
        "Bogota",
        "Cundinamarca",
        null,
        null,
        "CONCRETE",
        "OFFICE",
        900000
    );
  }
}
