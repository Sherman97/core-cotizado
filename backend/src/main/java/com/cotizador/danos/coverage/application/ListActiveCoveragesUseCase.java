package com.cotizador.danos.coverage.application;

import com.cotizador.danos.coverage.domain.CoverageCatalogItem;
import com.cotizador.danos.coverage.domain.CoverageCatalogRepository;
import java.util.List;

public class ListActiveCoveragesUseCase {

  private final CoverageCatalogRepository coverageCatalogRepository;

  public ListActiveCoveragesUseCase(CoverageCatalogRepository coverageCatalogRepository) {
    this.coverageCatalogRepository = coverageCatalogRepository;
  }

  public List<CoverageCatalogItem> handle() {
    return coverageCatalogRepository.findActive();
  }
}
