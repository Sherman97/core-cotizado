package com.cotizador.danos.coverage.infrastructure.repository;

import com.cotizador.danos.coverage.infrastructure.entity.QuoteCoverageJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataQuoteCoverageJpaRepository extends JpaRepository<QuoteCoverageJpaEntity, Long> {

  List<QuoteCoverageJpaEntity> findByQuoteFolioOrderByIdAsc(String quoteFolio);

  void deleteByQuoteFolio(String quoteFolio);
}
