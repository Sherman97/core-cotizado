package com.cotizador.danos.coverage.infrastructure.repository;

import com.cotizador.danos.coverage.infrastructure.entity.CoverageCatalogJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCoverageCatalogJpaRepository extends JpaRepository<CoverageCatalogJpaEntity, String> {

  List<CoverageCatalogJpaEntity> findByActiveTrueOrderByCodeAsc();
}
