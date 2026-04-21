package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.domain.CalculationParameterData;
import com.cotizador.danos.catalog.domain.CalculationParameterRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class CalculationParameterRepositoryAdapter implements CalculationParameterRepository {

  private final SpringDataCalculationParameterJpaRepository calculationParameterRepository;

  public CalculationParameterRepositoryAdapter(SpringDataCalculationParameterJpaRepository calculationParameterRepository) {
    this.calculationParameterRepository = calculationParameterRepository;
  }

  @Override
  public Optional<CalculationParameterData> findActiveByCode(String parameterCode) {
    return calculationParameterRepository.findByParameterCodeAndActiveTrue(parameterCode)
        .map(entity -> new CalculationParameterData(
            entity.getParameterCode(),
            entity.getParameterValue(),
            entity.getDescription()
        ));
  }

  @Override
  public List<CalculationParameterData> findActiveByCodes(Set<String> parameterCodes) {
    if (parameterCodes.isEmpty()) {
      return List.of();
    }

    return calculationParameterRepository.findByParameterCodeInAndActiveTrue(parameterCodes).stream()
        .map(entity -> new CalculationParameterData(
            entity.getParameterCode(),
            entity.getParameterValue(),
            entity.getDescription()
        ))
        .toList();
  }
}
