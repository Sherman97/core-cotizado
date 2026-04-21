package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.CoverageRateTableJpaEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCoverageRateTableJpaRepository extends JpaRepository<CoverageRateTableJpaEntity, Long> {

  List<CoverageRateTableJpaEntity> findByProductCodeAndCoverageCodeInAndActiveTrue(
      String productCode,
      Set<String> coverageCodes
  );
}
