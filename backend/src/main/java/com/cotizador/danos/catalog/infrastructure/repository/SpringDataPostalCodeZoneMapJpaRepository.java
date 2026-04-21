package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.PostalCodeZoneMapJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPostalCodeZoneMapJpaRepository extends JpaRepository<PostalCodeZoneMapJpaEntity, String> {

  Optional<PostalCodeZoneMapJpaEntity> findByPostalCodeAndActiveTrue(String postalCode);
}
