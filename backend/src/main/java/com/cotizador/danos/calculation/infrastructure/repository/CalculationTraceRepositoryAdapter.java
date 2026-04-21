package com.cotizador.danos.calculation.infrastructure.repository;

import com.cotizador.danos.calculation.domain.CalculationTraceDetail;
import com.cotizador.danos.calculation.domain.CalculationTraceRepository;
import com.cotizador.danos.calculation.mapper.CalculationPersistenceMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class CalculationTraceRepositoryAdapter implements CalculationTraceRepository {

  private final SpringDataCalculationTraceJpaRepository jpaRepository;
  private final CalculationPersistenceMapper mapper;

  public CalculationTraceRepositoryAdapter(
      SpringDataCalculationTraceJpaRepository jpaRepository,
      CalculationPersistenceMapper mapper
  ) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public void saveAll(List<CalculationTraceDetail> traceDetails) {
    jpaRepository.saveAll(traceDetails.stream().map(mapper::toEntity).toList());
  }
}
