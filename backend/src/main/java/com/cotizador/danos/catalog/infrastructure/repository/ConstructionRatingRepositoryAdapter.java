package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.domain.ConstructionRatingData;
import com.cotizador.danos.catalog.domain.ConstructionRatingRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ConstructionRatingRepositoryAdapter implements ConstructionRatingRepository {

  private final SpringDataConstructionFactorJpaRepository constructionFactorRepository;

  public ConstructionRatingRepositoryAdapter(SpringDataConstructionFactorJpaRepository constructionFactorRepository) {
    this.constructionFactorRepository = constructionFactorRepository;
  }

  @Override
  public Optional<ConstructionRatingData> findActiveByConstructionType(String constructionType) {
    return constructionFactorRepository.findByConstructionTypeAndActiveTrue(constructionType)
        .map(entity -> new ConstructionRatingData(
            entity.getConstructionType(),
            entity.getFactorValue()
        ));
  }
}
