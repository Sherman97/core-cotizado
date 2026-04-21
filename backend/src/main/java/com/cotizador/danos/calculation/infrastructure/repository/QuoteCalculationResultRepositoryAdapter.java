package com.cotizador.danos.calculation.infrastructure.repository;

import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.calculation.infrastructure.entity.QuoteCalculationResultJpaEntity;
import com.cotizador.danos.calculation.mapper.CalculationPersistenceMapper;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class QuoteCalculationResultRepositoryAdapter implements QuoteCalculationResultRepository {

  private final SpringDataQuoteCalculationResultJpaRepository jpaRepository;
  private final CalculationPersistenceMapper mapper;

  public QuoteCalculationResultRepositoryAdapter(
      SpringDataQuoteCalculationResultJpaRepository jpaRepository,
      CalculationPersistenceMapper mapper
  ) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Optional<QuoteCalculationResult> findByQuoteFolio(String folio) {
    return jpaRepository.findById(folio).map(mapper::toDomain);
  }

  @Override
  public QuoteCalculationResult save(String folio, QuoteCalculationResult result) {
    QuoteCalculationResultJpaEntity entity = jpaRepository.findById(folio)
        .orElseGet(QuoteCalculationResultJpaEntity::new);
    mapper.copyToEntity(folio, result, entity);
    QuoteCalculationResultJpaEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }
}
