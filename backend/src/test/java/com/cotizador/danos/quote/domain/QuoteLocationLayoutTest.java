package com.cotizador.danos.quote.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class QuoteLocationLayoutTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final Instant MODIFIED_AT = Instant.parse("2026-04-20T11:30:00Z");

  @Test
  void shouldSaveLocationLayoutAssociatedToQuote() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    QuoteLocationLayout layout = locationLayout();

    Quote updatedQuote = quote.updateLocationLayout(layout, MODIFIED_AT);

    assertThat(updatedQuote.getLocationLayout()).isEqualTo(layout);
    assertThat(updatedQuote.getVersion()).isEqualTo(2);
    assertThat(updatedQuote.getModifiedAt()).isEqualTo(MODIFIED_AT);
  }

  private QuoteLocationLayout locationLayout() {
    return new QuoteLocationLayout(3, true, false, "Captura inicial");
  }
}
