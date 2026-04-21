package com.cotizador.danos.calculation.domain;

import java.util.Optional;

public interface QuoteCalculationResultRepository {

  Optional<QuoteCalculationResult> findByQuoteFolio(String folio);

  QuoteCalculationResult save(String folio, QuoteCalculationResult result);
}
