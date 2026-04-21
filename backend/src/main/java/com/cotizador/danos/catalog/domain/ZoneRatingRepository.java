package com.cotizador.danos.catalog.domain;

import java.util.Optional;

public interface ZoneRatingRepository {

  Optional<ZoneRatingData> findActiveByPostalCode(String postalCode);
}
