package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.OccupancyCatalogJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOccupancyCatalogJpaRepository extends JpaRepository<OccupancyCatalogJpaEntity, String> {

  Optional<OccupancyCatalogJpaEntity> findByOccupancyCodeAndActiveTrue(String occupancyCode);
}
