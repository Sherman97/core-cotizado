package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.domain.CoverageBaseRateData;
import com.cotizador.danos.catalog.domain.CoverageRateRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class CoverageRateRepositoryAdapter implements CoverageRateRepository {

  private final SpringDataCoverageRateTableJpaRepository coverageRateRepository;

  public CoverageRateRepositoryAdapter(SpringDataCoverageRateTableJpaRepository coverageRateRepository) {
    this.coverageRateRepository = coverageRateRepository;
  }

  @Override
  public List<CoverageBaseRateData> findActiveByProductAndCoverageCodes(String productCode, Set<String> coverageCodes) {
    if (coverageCodes.isEmpty()) {
      return List.of();
    }

    return coverageRateRepository.findByProductCodeAndCoverageCodeInAndActiveTrue(productCode, coverageCodes).stream()
        .map(entity -> new CoverageBaseRateData(
            entity.getProductCode(),
            entity.getCoverageCode(),
            entity.getBaseRate()
        ))
        .toList();
  }
}
