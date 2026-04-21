package com.cotizador.danos.quote.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class QuoteVersioningTest {

  private static final String FOLIO = "FOLIO-001";
  private static final String NEW_VERSION_FOLIO = "FOLIO-001-V2";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final Instant NEW_VERSION_CREATED_AT = Instant.parse("2026-04-20T11:20:00Z");

  @Test
  void shouldCreateEditableNewVersionLinkedToPreviousSnapshot() {
    Quote previousVersion = previousVersion();

    Quote newVersion = previousVersion.createNewVersion(NEW_VERSION_FOLIO, NEW_VERSION_CREATED_AT);

    assertThat(newVersion.getFolio()).isEqualTo(NEW_VERSION_FOLIO);
    assertThat(newVersion.getParentQuoteFolio()).isEqualTo(FOLIO);
    assertThat(newVersion.getVersion()).isEqualTo(previousVersion.getVersion() + 1);
    assertThat(newVersion.getStatus()).isEqualTo(QuoteStatus.DRAFT);
    assertThat(newVersion.getCreatedAt()).isEqualTo(NEW_VERSION_CREATED_AT);

    assertThat(previousVersion.getFolio()).isEqualTo(FOLIO);
    assertThat(previousVersion.getVersion()).isEqualTo(Quote.INITIAL_VERSION);
    assertThat(previousVersion.getStatus()).isEqualTo(QuoteStatus.CALCULATED);
  }

  private Quote previousVersion() {
    return Quote.createNew(FOLIO, CREATED_AT).markAsCalculated();
  }
}
