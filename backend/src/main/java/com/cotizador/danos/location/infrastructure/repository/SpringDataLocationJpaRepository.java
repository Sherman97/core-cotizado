package com.cotizador.danos.location.infrastructure.repository;

import com.cotizador.danos.location.infrastructure.entity.QuoteLocationJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataLocationJpaRepository extends JpaRepository<QuoteLocationJpaEntity, Long> {

  List<QuoteLocationJpaEntity> findByQuoteFolioOrderByIdAsc(String quoteFolio);

  Optional<QuoteLocationJpaEntity> findByQuoteFolioAndId(String quoteFolio, Long id);

  void deleteByQuoteFolio(String quoteFolio);

  @Query(value = "SELECT NEXT VALUE FOR quote_location_seq", nativeQuery = true)
  long nextIdValue();
}
