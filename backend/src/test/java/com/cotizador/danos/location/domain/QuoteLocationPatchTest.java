package com.cotizador.danos.location.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QuoteLocationPatchTest {

  @Test
  void shouldReturnFalseWhenAddressIsBlank() {
    QuoteLocationPatch patch = new QuoteLocationPatch(0, "Sede 1", "Bogota", null, null, "Cundinamarca", "   ", "110111", "CONCRETE", 0, 0, "OFFICE", null, null, 500_000, java.util.List.of());

    assertThat(patch.hasRequiredData()).isFalse();
  }

  @Test
  void shouldReturnFalseWhenPostalCodeIsBlank() {
    QuoteLocationPatch patch = new QuoteLocationPatch(0, "Sede 2", "Bogota", null, null, "Cundinamarca", "Calle 1 # 1-01", "  ", "CONCRETE", 0, 0, "OFFICE", null, null, 500_000, java.util.List.of());

    assertThat(patch.hasRequiredData()).isFalse();
  }
}
