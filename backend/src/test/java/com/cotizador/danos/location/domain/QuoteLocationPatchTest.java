package com.cotizador.danos.location.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QuoteLocationPatchTest {

  @Test
  void shouldReturnFalseWhenAddressIsBlank() {
    QuoteLocationPatch patch = new QuoteLocationPatch(
        "Sede 1",
        "Bogota",
        "Cundinamarca",
        "   ",
        "110111",
        "CONCRETE",
        "OFFICE",
        500_000
    );

    assertThat(patch.hasRequiredData()).isFalse();
  }

  @Test
  void shouldReturnFalseWhenPostalCodeIsBlank() {
    QuoteLocationPatch patch = new QuoteLocationPatch(
        "Sede 2",
        "Bogota",
        "Cundinamarca",
        "Calle 1 # 1-01",
        "  ",
        "CONCRETE",
        "OFFICE",
        500_000
    );

    assertThat(patch.hasRequiredData()).isFalse();
  }
}
