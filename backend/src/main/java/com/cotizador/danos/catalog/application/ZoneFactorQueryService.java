package com.cotizador.danos.catalog.application;

import com.cotizador.danos.catalog.domain.ZoneRatingData;
import com.cotizador.danos.catalog.domain.ZoneRatingRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ZoneFactorQueryService {

  private final ZoneRatingRepository zoneRatingRepository;

  public ZoneFactorQueryService(ZoneRatingRepository zoneRatingRepository) {
    this.zoneRatingRepository = zoneRatingRepository;
  }

  public Optional<ZoneRatingData> findByPostalCode(String postalCode) {
    return zoneRatingRepository.findActiveByPostalCode(postalCode);
  }
}
