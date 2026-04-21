package com.cotizador.danos.catalog.domain;

import java.util.Optional;

public interface OccupancyRatingRepository {

  Optional<OccupancyRatingData> findActiveByOccupancyCode(String occupancyCode);
}
