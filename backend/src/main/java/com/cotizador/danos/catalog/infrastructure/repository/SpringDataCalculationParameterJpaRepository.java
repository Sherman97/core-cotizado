package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.CalculationParameterJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCalculationParameterJpaRepository extends JpaRepository<CalculationParameterJpaEntity, String> {

  Optional<CalculationParameterJpaEntity> findByParameterCodeAndActiveTrue(String parameterCode);

  List<CalculationParameterJpaEntity> findByParameterCodeInAndActiveTrue(Set<String> parameterCodes);
}
