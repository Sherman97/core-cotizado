package com.cotizador.danos.quote.domain;

import java.time.Instant;
import java.util.Optional;

public interface FolioIdempotencyRepository {

  Optional<String> findFolioByIdempotencyKey(String idempotencyKey);

  void save(String idempotencyKey, String folio, Instant createdAt);
}
