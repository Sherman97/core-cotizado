package com.cotizador.danos.quote.infrastructure.repository;

import com.cotizador.danos.quote.infrastructure.entity.FolioIdempotencyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataFolioIdempotencyJpaRepository extends JpaRepository<FolioIdempotencyJpaEntity, String> {
}
