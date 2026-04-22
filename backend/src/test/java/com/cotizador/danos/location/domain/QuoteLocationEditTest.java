package com.cotizador.danos.location.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QuoteLocationEditTest {

  private static final String FOLIO = "FOLIO-001";

  @Test
  void shouldCompletePreviouslyIncompleteLocationWhenMissingDataIsProvided() {
    QuoteLocation incompleteLocation = QuoteLocation.create(
        1L,
        FOLIO,
        incompletePatch()
    );

    QuoteLocation updatedLocation = incompleteLocation.update(
        completeMissingDataPatch()
    );

    assertThat(updatedLocation.getId()).isEqualTo(1L);
    assertThat(updatedLocation.getQuoteFolio()).isEqualTo(FOLIO);
    assertThat(updatedLocation.getAddress()).isEqualTo("Calle 80 #15-10");
    assertThat(updatedLocation.getPostalCode()).isEqualTo("110221");
    assertThat(updatedLocation.getValidationStatus()).isEqualTo(LocationValidationStatus.COMPLETE);
    assertThat(updatedLocation.getAlerts()).isEmpty();
  }

  @Test
  void shouldUpdateOnlyProvidedFieldsAndPreserveExistingAssociation() {
    QuoteLocation existingLocation = QuoteLocation.create(
        2L,
        FOLIO,
        completePatch()
    );

    QuoteLocation updatedLocation = existingLocation.update(
        cityOnlyPatch()
    );

    assertThat(updatedLocation.getId()).isEqualTo(2L);
    assertThat(updatedLocation.getQuoteFolio()).isEqualTo(FOLIO);
    assertThat(updatedLocation.getLocationName()).isEqualTo("Matriz Centro");
    assertThat(updatedLocation.getCity()).isEqualTo("Medellin");
    assertThat(updatedLocation.getAddress()).isEqualTo("Calle 100 #10-20");
    assertThat(updatedLocation.getPostalCode()).isEqualTo("110111");
    assertThat(updatedLocation.getValidationStatus()).isEqualTo(LocationValidationStatus.COMPLETE);
  }

  @Test
  void shouldMarkLocationAsIncompleteWhenUpdatedWithBlankAddress() {
    QuoteLocation existingLocation = QuoteLocation.create(
        3L,
        FOLIO,
        completePatch()
    );

    QuoteLocation updatedLocation = existingLocation.update(
        new QuoteLocationUpdatePatch(null, null, null, null, null, null, " ", null, null, null, null, null, null, null, null, null)
    );

    assertThat(updatedLocation.getValidationStatus()).isEqualTo(LocationValidationStatus.INCOMPLETE);
    assertThat(updatedLocation.getAlerts()).containsExactly("Location has incomplete required data");
  }

  private QuoteLocationPatch completePatch() {
    return new QuoteLocationPatch(0, "Matriz Centro", "Bogota", null, null, "Cundinamarca", "Calle 100 #10-20", "110111", "CONCRETE", 0, 0, "OFFICE", null, null, 1500000, java.util.List.of());
  }

  private QuoteLocationPatch incompletePatch() {
    return new QuoteLocationPatch(0, "Sucursal Norte", "Bogota", null, null, "Cundinamarca", null, null, "CONCRETE", 0, 0, "OFFICE", null, null, 900000, java.util.List.of());
  }

  private QuoteLocationUpdatePatch completeMissingDataPatch() {
    return new QuoteLocationUpdatePatch(null, null, null, null, null, null, "Calle 80 #15-10", "110221", null, null, null, null, null, null, null, null);
  }

  private QuoteLocationUpdatePatch cityOnlyPatch() {
    return new QuoteLocationUpdatePatch(null, null, "Medellin", null, null, null, null, null, null, null, null, null, null, null, null, null);
  }
}
