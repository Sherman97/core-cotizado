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
    return new QuoteLocationPatch(0, "Matriz Centro", "Bogota", null, null, "Cundinamarca", "Calle 100 #10-20", "110111", "CONCRETE", 0, 0, "OFFICE", null, null, 1500000, java.util.List.of());
  }

  private QuoteLocationPatch incompletePatch() {
    return new QuoteLocationPatch(0, "Sucursal Norte", "Bogota", null, null, "Cundinamarca", null, null, "CONCRETE", 0, 0, "OFFICE", null, null, 900000, java.util.List.of());
  }

  @Test
  void shouldUpdateLocationAndKeepCurrentIfMissing() {
    QuoteLocation location = QuoteLocation.create(1L, FOLIO, completePatch());

    // Update with purely null values (all should be kept)
    QuoteLocationUpdatePatch emptyPatch = new QuoteLocationUpdatePatch(
        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null
    );

    QuoteLocation notUpdated = location.update(emptyPatch);
    assertThat(notUpdated.getCity()).isEqualTo("Bogota");
    assertThat(notUpdated.getInsuredValue()).isEqualTo(1500000);
    assertThat(notUpdated.getGuarantees()).isEmpty();
    assertThat(notUpdated.getCatastrophicZone()).isNull();

    // Update with non-null values
    QuoteLocationUpdatePatch fullPatch = new QuoteLocationUpdatePatch(
        5, "New Name", "New City", "New Colony", "New Munic", "New Dept", 
        "New Addr", "newZ", "WOOD", 3, 2020, "HOME", "F3", true, 2000L, java.util.List.of("G2")
    );

    QuoteLocation fullyUpdated = location.update(fullPatch);
    assertThat(fullyUpdated.getLocationIndex()).isEqualTo(5);
    assertThat(fullyUpdated.getLocationName()).isEqualTo("New Name");
    assertThat(fullyUpdated.getCity()).isEqualTo("New City");
    assertThat(fullyUpdated.getColony()).isEqualTo("New Colony");
    assertThat(fullyUpdated.getMunicipality()).isEqualTo("New Munic");
    assertThat(fullyUpdated.getDepartment()).isEqualTo("New Dept");
    assertThat(fullyUpdated.getAddress()).isEqualTo("New Addr");
    assertThat(fullyUpdated.getPostalCode()).isEqualTo("newZ");
    assertThat(fullyUpdated.getConstructionType()).isEqualTo("WOOD");
    assertThat(fullyUpdated.getConstructionLevel()).isEqualTo(3);
    assertThat(fullyUpdated.getConstructionYear()).isEqualTo(2020);
    assertThat(fullyUpdated.getOccupancyType()).isEqualTo("HOME");
    assertThat(fullyUpdated.getFireKey()).isEqualTo("F3");
    assertThat(fullyUpdated.getCatastrophicZone()).isTrue();
    assertThat(fullyUpdated.getInsuredValue()).isEqualTo(2000L);
    assertThat(fullyUpdated.getGuarantees()).containsExactly("G2");
  }
}
