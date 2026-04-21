package com.cotizador.danos.calculation.domain;

import java.util.List;

public interface CalculationTraceRepository {

  void saveAll(List<CalculationTraceDetail> traceDetails);
}
