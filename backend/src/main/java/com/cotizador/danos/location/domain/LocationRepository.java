package com.cotizador.danos.location.domain;

import java.util.List;
import java.util.Optional;

public interface LocationRepository {

  long nextId();

  Optional<QuoteLocation> findById(long id);

  Optional<QuoteLocation> findByQuoteFolioAndId(String quoteFolio, long id);

  List<QuoteLocation> findByQuoteFolio(String quoteFolio);

  void deleteByQuoteFolio(String quoteFolio);

  QuoteLocation save(QuoteLocation quoteLocation);
}
