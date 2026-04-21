package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.CoverageFactorTableJpaEntity;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCoverageFactorTableJpaRepository extends JpaRepository<CoverageFactorTableJpaEntity, Long> {

  List<CoverageFactorTableJpaEntity> findByProductCodeAndCoverageCodeInAndActiveTrue(
      String productCode,
      Set<String> coverageCodes
  );
}
