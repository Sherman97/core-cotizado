package com.cotizador.danos.coverage.infrastructure.repository;

import com.cotizador.danos.coverage.domain.CoverageCatalogItem;
import com.cotizador.danos.coverage.domain.CoverageCatalogRepository;
import com.cotizador.danos.coverage.mapper.QuoteCoveragePersistenceMapper;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class CoverageCatalogRepositoryAdapter implements CoverageCatalogRepository {

  private final SpringDataCoverageCatalogJpaRepository jpaRepository;
  private final QuoteCoveragePersistenceMapper mapper;

  public CoverageCatalogRepositoryAdapter(
      SpringDataCoverageCatalogJpaRepository jpaRepository,
      QuoteCoveragePersistenceMapper mapper
  ) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public List<CoverageCatalogItem> findActive() {
    return jpaRepository.findByActiveTrueOrderByCodeAsc().stream()
        .map(mapper::toDomain)
        .toList();
  }
}
