package com.cotizador.danos.coverage.domain;

import java.util.List;

public interface QuoteCoverageRepository {

  List<QuoteCoverageSelection> findByQuoteFolio(String quoteFolio);

  List<QuoteCoverageSelection> replaceForQuote(String quoteFolio, List<QuoteCoveragePatch> coverages);
}
