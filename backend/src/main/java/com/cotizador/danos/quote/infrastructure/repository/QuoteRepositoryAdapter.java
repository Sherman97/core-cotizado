package com.cotizador.danos.quote.infrastructure.repository;

import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import com.cotizador.danos.quote.infrastructure.entity.QuoteJpaEntity;
import com.cotizador.danos.quote.mapper.QuotePersistenceMapper;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class QuoteRepositoryAdapter implements QuoteRepository {

  private final SpringDataQuoteJpaRepository jpaRepository;
  private final QuotePersistenceMapper mapper;

  public QuoteRepositoryAdapter(SpringDataQuoteJpaRepository jpaRepository, QuotePersistenceMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public boolean existsByFolio(String folio) {
    return jpaRepository.existsByFolio(folio);
  }

  @Override
  public Optional<Quote> findByFolio(String folio) {
    return jpaRepository.findById(folio).map(mapper::toDomain);
  }

  @Override
  public List<Quote> findVersionsByRootFolio(String rootFolio) {
    return jpaRepository.findByRootFolioOrderByBusinessVersionAsc(rootFolio).stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Optional<Quote> findByRootFolioAndVersion(String rootFolio, int version) {
    return jpaRepository.findByRootFolioAndBusinessVersion(rootFolio, version)
        .map(mapper::toDomain);
  }

  @Override
  public List<Quote> findAllOrderByCreatedAtDesc() {
    return jpaRepository.findAllByOrderByCreatedAtDesc().stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Quote save(Quote quote) {
    QuoteJpaEntity entity = jpaRepository.findById(quote.getFolio()).orElseGet(QuoteJpaEntity::new);
    mapper.copyToEntity(quote, entity, resolveRootFolio(quote));
    QuoteJpaEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  private String resolveRootFolio(Quote quote) {
    if (quote.getParentQuoteFolio() == null || quote.getParentQuoteFolio().isBlank()) {
      return quote.getFolio();
    }

    return jpaRepository.findById(quote.getParentQuoteFolio())
        .map(QuoteJpaEntity::getRootFolio)
        .orElse(quote.getParentQuoteFolio());
  }
}
