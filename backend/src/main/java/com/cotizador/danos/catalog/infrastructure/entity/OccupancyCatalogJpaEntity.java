package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "occupancy_catalog")
public class OccupancyCatalogJpaEntity {

  @Id
  @Column(name = "occupancy_code", nullable = false, length = 64)
  private String occupancyCode;

  @Column(name = "occupancy_name", nullable = false, length = 255)
  private String occupancyName;

  @Column(name = "clave_incendio", nullable = false, length = 64)
  private String fireKey;

  @Column(name = "active", nullable = false)
  private boolean active;

  public String getOccupancyCode() {
    return occupancyCode;
  }

  public String getOccupancyName() {
    return occupancyName;
  }

  public String getFireKey() {
    return fireKey;
  }

  public boolean isActive() {
    return active;
  }
}
