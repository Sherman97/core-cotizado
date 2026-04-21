package com.cotizador.danos.coverage.infrastructure.repository;

import com.cotizador.danos.coverage.domain.QuoteCoveragePatch;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.coverage.mapper.QuoteCoveragePersistenceMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class QuoteCoverageRepositoryAdapter implements QuoteCoverageRepository {

  private final SpringDataQuoteCoverageJpaRepository jpaRepository;
  private final QuoteCoveragePersistenceMapper mapper;

  public QuoteCoverageRepositoryAdapter(
      SpringDataQuoteCoverageJpaRepository jpaRepository,
      QuoteCoveragePersistenceMapper mapper
  ) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public List<QuoteCoverageSelection> findByQuoteFolio(String quoteFolio) {
    return jpaRepository.findByQuoteFolioOrderByIdAsc(quoteFolio).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public List<QuoteCoverageSelection> replaceForQuote(String quoteFolio, List<QuoteCoveragePatch> coverages) {
    jpaRepository.deleteByQuoteFolio(quoteFolio);
    return jpaRepository.saveAll(
            coverages.stream()
                .map(coverage -> mapper.toEntity(quoteFolio, coverage))
                .toList()
        ).stream()
        .map(mapper::toDomain)
        .toList();
  }
}
