package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.domain.ZoneRatingData;
import com.cotizador.danos.catalog.domain.ZoneRatingRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ZoneRatingRepositoryAdapter implements ZoneRatingRepository {

  private final SpringDataPostalCodeZoneMapJpaRepository postalCodeZoneMapRepository;
  private final SpringDataZoneFactorJpaRepository zoneFactorRepository;

  public ZoneRatingRepositoryAdapter(
      SpringDataPostalCodeZoneMapJpaRepository postalCodeZoneMapRepository,
      SpringDataZoneFactorJpaRepository zoneFactorRepository
  ) {
    this.postalCodeZoneMapRepository = postalCodeZoneMapRepository;
    this.zoneFactorRepository = zoneFactorRepository;
  }

  @Override
  public Optional<ZoneRatingData> findActiveByPostalCode(String postalCode) {
    return postalCodeZoneMapRepository.findByPostalCodeAndActiveTrue(postalCode)
        .flatMap(map -> zoneFactorRepository.findByZoneCodeAndActiveTrue(map.getZoneCode())
            .map(zoneFactor -> new ZoneRatingData(
                map.getPostalCode(),
                zoneFactor.getZoneCode(),
                zoneFactor.getZoneName(),
                zoneFactor.getFactorValue()
            )));
  }
}
