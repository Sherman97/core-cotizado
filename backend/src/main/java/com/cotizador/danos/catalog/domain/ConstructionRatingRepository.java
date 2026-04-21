package com.cotizador.danos.catalog.domain;

import java.util.Optional;

public interface ConstructionRatingRepository {

  Optional<ConstructionRatingData> findActiveByConstructionType(String constructionType);
}
