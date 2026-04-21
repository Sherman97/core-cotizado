package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.domain.OccupancyRatingData;
import com.cotizador.danos.catalog.domain.OccupancyRatingRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class OccupancyRatingRepositoryAdapter implements OccupancyRatingRepository {

  private final SpringDataOccupancyCatalogJpaRepository occupancyCatalogRepository;
  private final SpringDataOccupancyFactorJpaRepository occupancyFactorRepository;

  public OccupancyRatingRepositoryAdapter(
      SpringDataOccupancyCatalogJpaRepository occupancyCatalogRepository,
      SpringDataOccupancyFactorJpaRepository occupancyFactorRepository
  ) {
    this.occupancyCatalogRepository = occupancyCatalogRepository;
    this.occupancyFactorRepository = occupancyFactorRepository;
  }

  @Override
  public Optional<OccupancyRatingData> findActiveByOccupancyCode(String occupancyCode) {
    return occupancyCatalogRepository.findByOccupancyCodeAndActiveTrue(occupancyCode)
        .flatMap(catalog -> occupancyFactorRepository.findByOccupancyCodeAndActiveTrue(occupancyCode)
            .map(factor -> new OccupancyRatingData(
                catalog.getOccupancyCode(),
                catalog.getFireKey(),
                factor.getFactorValue()
            )));
  }
}
