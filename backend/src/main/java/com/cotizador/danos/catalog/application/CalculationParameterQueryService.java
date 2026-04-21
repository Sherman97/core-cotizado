package com.cotizador.danos.catalog.application;

import com.cotizador.danos.catalog.domain.CalculationParameterData;
import com.cotizador.danos.catalog.domain.CalculationParameterRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CalculationParameterQueryService {

  private final CalculationParameterRepository calculationParameterRepository;

  public CalculationParameterQueryService(CalculationParameterRepository calculationParameterRepository) {
    this.calculationParameterRepository = calculationParameterRepository;
  }

  public Optional<CalculationParameterData> findByCode(String parameterCode) {
    return calculationParameterRepository.findActiveByCode(parameterCode);
  }

  public List<CalculationParameterData> findByCodes(Set<String> parameterCodes) {
    return calculationParameterRepository.findActiveByCodes(parameterCodes);
  }
}
