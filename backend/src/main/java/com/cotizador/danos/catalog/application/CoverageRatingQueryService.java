package com.cotizador.danos.catalog.application;

import com.cotizador.danos.catalog.domain.CoverageBaseRateData;
import com.cotizador.danos.catalog.domain.CoverageFactorData;
import com.cotizador.danos.catalog.domain.CoverageFactorRepository;
import com.cotizador.danos.catalog.domain.CoverageRateRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CoverageRatingQueryService {

  private final CoverageRateRepository coverageRateRepository;
  private final CoverageFactorRepository coverageFactorRepository;

  public CoverageRatingQueryService(
      CoverageRateRepository coverageRateRepository,
      CoverageFactorRepository coverageFactorRepository
  ) {
    this.coverageRateRepository = coverageRateRepository;
    this.coverageFactorRepository = coverageFactorRepository;
  }

  public CoverageRatingSnapshot findByProductAndCoverages(String productCode, Set<String> coverageCodes) {
    List<CoverageBaseRateData> baseRates = coverageRateRepository.findActiveByProductAndCoverageCodes(productCode, coverageCodes);
    List<CoverageFactorData> factors = coverageFactorRepository.findActiveByProductAndCoverageCodes(productCode, coverageCodes);
    return new CoverageRatingSnapshot(baseRates, factors);
  }

  public record CoverageRatingSnapshot(
      List<CoverageBaseRateData> baseRates,
      List<CoverageFactorData> factors
  ) {
  }
}
