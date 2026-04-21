package com.cotizador.danos.coverage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class QuoteCoverageSelectionTest {

  private static final String FOLIO = "FOLIO-001";

  @Test
  void shouldCreateCoverageConfigurationWithParameters() {
    QuoteCoverageSelection selection = QuoteCoverageSelection.create(
        FOLIO,
        fireCoverage(1000000L, 50000L)
    );

    assertThat(selection.getQuoteFolio()).isEqualTo(FOLIO);
    assertThat(selection.getCoverageCode()).isEqualTo("FIRE");
    assertThat(selection.getCoverageName()).isEqualTo("Incendio");
    assertThat(selection.getInsuredLimit()).isEqualTo(1000000L);
    assertThat(selection.getDeductibleType()).isEqualTo("FIXED");
    assertThat(selection.getDeductibleValue()).isEqualTo(50000L);
    assertThat(selection.isSelected()).isTrue();
  }

  @Test
  void shouldReplacePreviousCoverageConfigurationWhenUpdated() {
    QuoteCoverageSelection currentSelection = QuoteCoverageSelection.create(
        FOLIO,
        fireCoverage(1000000L, 50000L)
    );

    List<QuoteCoverageSelection> updatedSelections = QuoteCoverageSelection.replaceAll(
        FOLIO,
        List.of(currentSelection),
        List.of(
            fireCoverage(1200000L, 60000L),
            earthquakeCoverage()
        )
    );

    assertThat(updatedSelections).hasSize(2);
    assertThat(updatedSelections).extracting(QuoteCoverageSelection::getCoverageCode)
        .containsExactly("FIRE", "EARTHQUAKE");
    assertThat(updatedSelections).extracting(QuoteCoverageSelection::getInsuredLimit)
        .containsExactly(1200000L, 800000L);
  }

  private QuoteCoveragePatch fireCoverage(long insuredLimit, long deductibleValue) {
    return new QuoteCoveragePatch("FIRE", "Incendio", insuredLimit, "FIXED", deductibleValue, true);
  }

  private QuoteCoveragePatch earthquakeCoverage() {
    return new QuoteCoveragePatch("EARTHQUAKE", "Terremoto", 800000L, null, null, true);
  }
}
