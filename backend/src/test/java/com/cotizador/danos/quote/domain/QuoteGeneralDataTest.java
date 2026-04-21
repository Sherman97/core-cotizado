package com.cotizador.danos.quote.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class QuoteGeneralDataTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final Instant FIRST_MODIFIED_AT = Instant.parse("2026-04-20T11:30:00Z");
  private static final Instant SECOND_MODIFIED_AT = Instant.parse("2026-04-20T12:45:00Z");

  @Test
  void shouldUpdateGeneralDataAndModificationDate() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    QuoteGeneralDataPatch patch = completePatch();

    Quote updatedQuote = quote.updateGeneralData(patch, FIRST_MODIFIED_AT);

    assertThat(updatedQuote.getProductCode()).isEqualTo("DANOS");
    assertThat(updatedQuote.getCustomerName()).isEqualTo("Cliente Demo");
    assertThat(updatedQuote.getCurrency()).isEqualTo("USD");
    assertThat(updatedQuote.getObservations()).isEqualTo("Observacion inicial");
    assertThat(updatedQuote.getVersion()).isEqualTo(2);
    assertThat(updatedQuote.getModifiedAt()).isEqualTo(FIRST_MODIFIED_AT);
  }

  @Test
  void shouldUpdateOnlyProvidedFieldsAndPreserveNonSentFields() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT)
        .updateGeneralData(completePatch(), FIRST_MODIFIED_AT);

    Quote updatedQuote = quote.updateGeneralData(
        new QuoteGeneralDataPatch(
            null,
            null,
            "EUR",
            null
        ),
        SECOND_MODIFIED_AT
    );

    assertThat(updatedQuote.getProductCode()).isEqualTo("DANOS");
    assertThat(updatedQuote.getCustomerName()).isEqualTo("Cliente Demo");
    assertThat(updatedQuote.getCurrency()).isEqualTo("EUR");
    assertThat(updatedQuote.getObservations()).isEqualTo("Observacion inicial");
    assertThat(updatedQuote.getVersion()).isEqualTo(3);
    assertThat(updatedQuote.getModifiedAt()).isEqualTo(SECOND_MODIFIED_AT);
  }

  private QuoteGeneralDataPatch completePatch() {
    return new QuoteGeneralDataPatch(
        "DANOS",
        "Cliente Demo",
        "USD",
        "Observacion inicial"
    );
  }
}
