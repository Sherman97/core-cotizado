package com.cotizador.danos.calculation.infrastructure.repository;

import com.cotizador.danos.calculation.infrastructure.entity.QuoteCalculationResultJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataQuoteCalculationResultJpaRepository
    extends JpaRepository<QuoteCalculationResultJpaEntity, String> {
}
