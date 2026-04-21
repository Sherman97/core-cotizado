package com.cotizador.danos.location.infrastructure.repository;

import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.infrastructure.entity.QuoteLocationJpaEntity;
import com.cotizador.danos.location.mapper.QuoteLocationPersistenceMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class LocationRepositoryAdapter implements LocationRepository {

  private final SpringDataLocationJpaRepository jpaRepository;
  private final QuoteLocationPersistenceMapper mapper;

  public LocationRepositoryAdapter(
      SpringDataLocationJpaRepository jpaRepository,
      QuoteLocationPersistenceMapper mapper
  ) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public long nextId() {
    return jpaRepository.nextIdValue();
  }

  @Override
  public Optional<QuoteLocation> findById(long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<QuoteLocation> findByQuoteFolioAndId(String quoteFolio, long id) {
    return jpaRepository.findByQuoteFolioAndId(quoteFolio, id).map(mapper::toDomain);
  }

  @Override
  public List<QuoteLocation> findByQuoteFolio(String quoteFolio) {
    return jpaRepository.findByQuoteFolioOrderByIdAsc(quoteFolio).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public void deleteByQuoteFolio(String quoteFolio) {
    jpaRepository.deleteByQuoteFolio(quoteFolio);
  }

  @Override
  public QuoteLocation save(QuoteLocation quoteLocation) {
    QuoteLocationJpaEntity entity = jpaRepository.findById(quoteLocation.getId())
        .orElseGet(QuoteLocationJpaEntity::new);
    mapper.copyToEntity(quoteLocation, entity);
    QuoteLocationJpaEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }
}
