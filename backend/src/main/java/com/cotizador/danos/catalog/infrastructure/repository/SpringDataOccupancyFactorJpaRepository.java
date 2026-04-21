package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.OccupancyFactorJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOccupancyFactorJpaRepository extends JpaRepository<OccupancyFactorJpaEntity, Long> {

  Optional<OccupancyFactorJpaEntity> findByOccupancyCodeAndActiveTrue(String occupancyCode);
}
