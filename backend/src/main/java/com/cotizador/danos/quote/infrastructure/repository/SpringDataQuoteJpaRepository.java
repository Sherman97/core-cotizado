package com.cotizador.danos.quote.infrastructure.repository;

import com.cotizador.danos.quote.infrastructure.entity.QuoteJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataQuoteJpaRepository extends JpaRepository<QuoteJpaEntity, String> {

  boolean existsByFolio(String folio);

  List<QuoteJpaEntity> findByRootFolioOrderByBusinessVersionAsc(String rootFolio);

  Optional<QuoteJpaEntity> findByRootFolioAndBusinessVersion(String rootFolio, int businessVersion);

  List<QuoteJpaEntity> findAllByOrderByCreatedAtDesc();
}
