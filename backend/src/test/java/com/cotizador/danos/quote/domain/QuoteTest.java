package com.cotizador.danos.quote.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class QuoteTest {

  private static final String FOLIO = "FOLIO-001";

  @Test
  void shouldCreateDraftQuoteWithVersionOneAndCreationDate() {
    Instant createdAt = Instant.parse("2026-04-20T10:15:30Z");

    Quote quote = Quote.createNew(FOLIO, createdAt);

    assertThat(quote.getFolio()).isEqualTo(FOLIO);
    assertThat(quote.getStatus()).isEqualTo(QuoteStatus.DRAFT);
    assertThat(quote.getVersion()).isEqualTo(Quote.INITIAL_VERSION);
    assertThat(quote.getCreatedAt()).isEqualTo(createdAt);
  }
}
