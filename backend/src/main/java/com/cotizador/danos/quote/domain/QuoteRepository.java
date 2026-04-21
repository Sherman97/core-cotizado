package com.cotizador.danos.quote.domain;

import java.util.Optional;
import java.util.List;

public interface QuoteRepository {

  boolean existsByFolio(String folio);

  Optional<Quote> findByFolio(String folio);

  List<Quote> findVersionsByRootFolio(String rootFolio);

  Optional<Quote> findByRootFolioAndVersion(String rootFolio, int version);

  List<Quote> findAllOrderByCreatedAtDesc();

  Quote save(Quote quote);
}
