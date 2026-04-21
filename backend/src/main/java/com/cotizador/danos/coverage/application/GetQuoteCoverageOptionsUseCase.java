package com.cotizador.danos.coverage.application;

import com.cotizador.danos.coverage.domain.CoverageCatalogItem;
import com.cotizador.danos.coverage.domain.CoverageCatalogRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.util.List;

public class GetQuoteCoverageOptionsUseCase {

  public record Result(
      List<CoverageCatalogItem> availableCoverages,
      List<QuoteCoverageSelection> selectedCoverages
  ) {
  }

  private final QuoteRepository quoteRepository;
  private final QuoteCoverageRepository quoteCoverageRepository;
  private final CoverageCatalogRepository coverageCatalogRepository;

  public GetQuoteCoverageOptionsUseCase(
      QuoteRepository quoteRepository,
      QuoteCoverageRepository quoteCoverageRepository,
      CoverageCatalogRepository coverageCatalogRepository
  ) {
    this.quoteRepository = quoteRepository;
    this.quoteCoverageRepository = quoteCoverageRepository;
    this.coverageCatalogRepository = coverageCatalogRepository;
  }

  public Result handle(String folio) {
    quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    return new Result(
        coverageCatalogRepository.findActive(),
        quoteCoverageRepository.findByQuoteFolio(folio)
    );
  }
}
