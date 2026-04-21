package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.domain.CoverageFactorData;
import com.cotizador.danos.catalog.domain.CoverageFactorRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class CoverageFactorRepositoryAdapter implements CoverageFactorRepository {

  private final SpringDataCoverageFactorTableJpaRepository coverageFactorRepository;

  public CoverageFactorRepositoryAdapter(SpringDataCoverageFactorTableJpaRepository coverageFactorRepository) {
    this.coverageFactorRepository = coverageFactorRepository;
  }

  @Override
  public List<CoverageFactorData> findActiveByProductAndCoverageCodes(String productCode, Set<String> coverageCodes) {
    if (coverageCodes.isEmpty()) {
      return List.of();
    }

    return coverageFactorRepository.findByProductCodeAndCoverageCodeInAndActiveTrue(productCode, coverageCodes).stream()
        .map(entity -> new CoverageFactorData(
            entity.getProductCode(),
            entity.getCoverageCode(),
            entity.getFactorValue()
        ))
        .toList();
  }
}
