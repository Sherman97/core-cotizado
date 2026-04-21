package com.cotizador.danos.shared.config;

import com.cotizador.danos.calculation.domain.CalculationTraceDetail;
import com.cotizador.danos.calculation.domain.PremiumCalculator;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.location.domain.QuoteLocation;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class StubPremiumCalculator implements PremiumCalculator {

  private static final BigDecimal BASE_RATE = new BigDecimal("0.0015");
  private static final BigDecimal COVERAGE_RATE = new BigDecimal("0.0002");

  @Override
  public double calculate(QuoteLocation location, List<QuoteCoverageSelection> coverages) {
    BigDecimal insuredValue = BigDecimal.valueOf(location.getInsuredValue());
    long selectedCoverages = coverages.stream().filter(QuoteCoverageSelection::isSelected).count();
    BigDecimal rate = BASE_RATE.add(COVERAGE_RATE.multiply(BigDecimal.valueOf(selectedCoverages)));

    return insuredValue.multiply(rate)
        .setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
  }

  @Override
  public List<CalculationTraceDetail> traceFor(QuoteLocation location, List<QuoteCoverageSelection> coverages) {
    List<CalculationTraceDetail> details = new ArrayList<>();

    details.add(new CalculationTraceDetail(
        location.getQuoteFolio(),
        location.getId(),
        "BASE_RATE",
        BASE_RATE.doubleValue(),
        1,
        Map.of("insuredValue", String.valueOf(location.getInsuredValue()))
    ));

    int order = 2;
    for (QuoteCoverageSelection coverage : coverages) {
      if (!coverage.isSelected()) {
        continue;
      }

      Map<String, String> metadata = new LinkedHashMap<>();
      metadata.put("coverageCode", coverage.getCoverageCode());
      metadata.put("insuredLimit", String.valueOf(coverage.getInsuredLimit()));
      details.add(new CalculationTraceDetail(
          location.getQuoteFolio(),
          location.getId(),
          "COVERAGE_FACTOR",
          COVERAGE_RATE.doubleValue(),
          order++,
          metadata
      ));
    }

    return details;
  }
}
