package com.cotizador.danos.coverage.domain;

import java.util.List;

public interface CoverageCatalogRepository {

  List<CoverageCatalogItem> findActive();
}
