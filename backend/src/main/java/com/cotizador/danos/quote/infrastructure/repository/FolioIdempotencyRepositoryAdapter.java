package com.cotizador.danos.quote.infrastructure.repository;

import com.cotizador.danos.quote.domain.FolioIdempotencyRepository;
import com.cotizador.danos.quote.infrastructure.entity.FolioIdempotencyJpaEntity;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class FolioIdempotencyRepositoryAdapter implements FolioIdempotencyRepository {

  private final SpringDataFolioIdempotencyJpaRepository jpaRepository;

  public FolioIdempotencyRepositoryAdapter(SpringDataFolioIdempotencyJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<String> findFolioByIdempotencyKey(String idempotencyKey) {
    return jpaRepository.findById(idempotencyKey).map(FolioIdempotencyJpaEntity::getFolio);
  }

  @Override
  public void save(String idempotencyKey, String folio, Instant createdAt) {
    FolioIdempotencyJpaEntity entity = new FolioIdempotencyJpaEntity();
    entity.setIdempotencyKey(idempotencyKey);
    entity.setFolio(folio);
    entity.setCreatedAt(createdAt);
    try {
      jpaRepository.save(entity);
    } catch (DataIntegrityViolationException ignored) {
      // If concurrent retry inserts same key, treat as already registered idempotency key.
    }
  }
}
