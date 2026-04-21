package com.cotizador.danos.catalog.domain;

import java.util.List;
import java.util.Set;

public interface CoverageFactorRepository {

  List<CoverageFactorData> findActiveByProductAndCoverageCodes(String productCode, Set<String> coverageCodes);
}
