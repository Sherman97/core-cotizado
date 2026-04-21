package com.cotizador.danos.catalog.domain;

import java.util.List;
import java.util.Set;

public interface CoverageRateRepository {

  List<CoverageBaseRateData> findActiveByProductAndCoverageCodes(String productCode, Set<String> coverageCodes);
}
