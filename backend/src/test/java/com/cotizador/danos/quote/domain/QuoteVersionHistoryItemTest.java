package com.cotizador.danos.quote.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class QuoteVersionHistoryItemTest {

  private static final String FOLIO = "FOLIO-001-V2";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T11:20:00Z");

  @Test
  void shouldCreateVersionHistoryItemWithVersionDateAndStatus() {
    QuoteVersionHistoryItem historyItem = historyItem();

    assertThat(historyItem.folio()).isEqualTo(FOLIO);
    assertThat(historyItem.version()).isEqualTo(2);
    assertThat(historyItem.status()).isEqualTo(QuoteStatus.DRAFT);
    assertThat(historyItem.createdAt()).isEqualTo(CREATED_AT);
  }

  private QuoteVersionHistoryItem historyItem() {
    return new QuoteVersionHistoryItem(
        FOLIO,
        2,
        QuoteStatus.DRAFT,
        CREATED_AT
    );
  }
}
