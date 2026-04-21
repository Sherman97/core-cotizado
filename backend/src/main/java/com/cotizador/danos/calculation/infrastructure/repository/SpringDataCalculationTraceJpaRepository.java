package com.cotizador.danos.calculation.infrastructure.repository;

import com.cotizador.danos.calculation.infrastructure.entity.CalculationTraceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCalculationTraceJpaRepository extends JpaRepository<CalculationTraceJpaEntity, Long> {
}
