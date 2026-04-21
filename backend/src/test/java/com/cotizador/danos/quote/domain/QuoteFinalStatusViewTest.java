package com.cotizador.danos.quote.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class QuoteFinalStatusViewTest {

  private static final String FOLIO = "FOLIO-001";
  private static final String ALERT = "Location Sucursal Norte skipped due to incomplete data";

  @Test
  void shouldCreateFinalStatusViewWithPremiumSummaryAndRelevantAlerts() {
    QuoteFinalStatusView finalStatus = finalStatus();

    assertThat(finalStatus.folio()).isEqualTo(FOLIO);
    assertThat(finalStatus.status()).isEqualTo(QuoteStatus.CALCULATED);
    assertThat(finalStatus.netPremium()).isEqualTo(1000.0);
    assertThat(finalStatus.expenseAmount()).isEqualTo(100.0);
    assertThat(finalStatus.taxAmount()).isEqualTo(176.0);
    assertThat(finalStatus.totalPremium()).isEqualTo(1276.0);
    assertThat(finalStatus.alerts()).contains(ALERT);
  }

  private QuoteFinalStatusView finalStatus() {
    return new QuoteFinalStatusView(
        FOLIO,
        QuoteStatus.CALCULATED,
        1000.0,
        100.0,
        176.0,
        1276.0,
        List.of(ALERT)
    );
  }
}
