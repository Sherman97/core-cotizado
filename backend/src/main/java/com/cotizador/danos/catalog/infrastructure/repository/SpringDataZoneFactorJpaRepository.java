package com.cotizador.danos.catalog.infrastructure.repository;

import com.cotizador.danos.catalog.infrastructure.entity.ZoneFactorJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataZoneFactorJpaRepository extends JpaRepository<ZoneFactorJpaEntity, Long> {

  Optional<ZoneFactorJpaEntity> findByZoneCodeAndActiveTrue(String zoneCode);
}
