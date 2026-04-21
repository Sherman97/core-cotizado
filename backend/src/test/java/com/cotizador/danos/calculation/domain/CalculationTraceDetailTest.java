package com.cotizador.danos.calculation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CalculationTraceDetailTest {

  private static final String FOLIO = "FOLIO-001";
  private static final long LOCATION_ID = 1L;
  private static final String FACTOR_TYPE = "BASE_RATE";
  private static final double APPLIED_VALUE = 0.015;
  private static final int FACTOR_ORDER = 1;

  @Test
  void shouldCreateTraceDetailAssociatedToQuoteAndLocation() {
    CalculationTraceDetail traceDetail = traceDetail();

    assertThat(traceDetail.quoteFolio()).isEqualTo(FOLIO);
    assertThat(traceDetail.locationId()).isEqualTo(LOCATION_ID);
    assertThat(traceDetail.factorType()).isEqualTo(FACTOR_TYPE);
    assertThat(traceDetail.appliedValue()).isEqualTo(APPLIED_VALUE);
    assertThat(traceDetail.factorOrder()).isEqualTo(FACTOR_ORDER);
    assertThat(traceDetail.metadata()).containsEntry("coverageCode", "INCENDIO");
  }

  private CalculationTraceDetail traceDetail() {
    return new CalculationTraceDetail(
        FOLIO,
        LOCATION_ID,
        FACTOR_TYPE,
        APPLIED_VALUE,
        FACTOR_ORDER,
        Map.of("coverageCode", "INCENDIO")
    );
  }
}
