package com.cotizador.danos.calculation.domain;

import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.location.domain.QuoteLocation;
import java.util.List;

public interface PremiumCalculator {

  double calculate(QuoteLocation location, List<QuoteCoverageSelection> coverages);

  default List<CalculationTraceDetail> traceFor(
      QuoteLocation location,
      List<QuoteCoverageSelection> coverages
  ) {
    return List.of();
  }
}
