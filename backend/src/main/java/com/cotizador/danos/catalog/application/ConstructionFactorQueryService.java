package com.cotizador.danos.catalog.application;

import com.cotizador.danos.catalog.domain.ConstructionRatingData;
import com.cotizador.danos.catalog.domain.ConstructionRatingRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ConstructionFactorQueryService {

  private final ConstructionRatingRepository constructionRatingRepository;

  public ConstructionFactorQueryService(ConstructionRatingRepository constructionRatingRepository) {
    this.constructionRatingRepository = constructionRatingRepository;
  }

  public Optional<ConstructionRatingData> findByConstructionType(String constructionType) {
    return constructionRatingRepository.findActiveByConstructionType(constructionType);
  }
}
