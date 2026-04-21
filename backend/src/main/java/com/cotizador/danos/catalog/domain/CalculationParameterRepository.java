package com.cotizador.danos.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CalculationParameterRepository {

  Optional<CalculationParameterData> findActiveByCode(String parameterCode);

  List<CalculationParameterData> findActiveByCodes(Set<String> parameterCodes);
}
