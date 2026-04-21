package com.cotizador.danos.catalog.application;

import com.cotizador.danos.catalog.domain.OccupancyRatingData;
import com.cotizador.danos.catalog.domain.OccupancyRatingRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class OccupancyFactorQueryService {

  private final OccupancyRatingRepository occupancyRatingRepository;

  public OccupancyFactorQueryService(OccupancyRatingRepository occupancyRatingRepository) {
    this.occupancyRatingRepository = occupancyRatingRepository;
  }

  public Optional<OccupancyRatingData> findByOccupancyCode(String occupancyCode) {
    return occupancyRatingRepository.findActiveByOccupancyCode(occupancyCode);
  }
}
