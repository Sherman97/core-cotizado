package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.ConstructionFactorJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataConstructionFactorJpaRepository extends JpaRepository<ConstructionFactorJpaEntity, Long> {

  Optional<ConstructionFactorJpaEntity> findByConstructionTypeAndActiveTrue(String constructionType);
}
