package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "postal_code_zone_map")
public class PostalCodeZoneMapJpaEntity {

  @Id
  @Column(name = "postal_code", nullable = false, length = 32)
  private String postalCode;

  @Column(name = "zone_code", nullable = false, length = 32)
  private String zoneCode;

  @Column(name = "zone_name", nullable = false, length = 120)
  private String zoneName;

  @Column(name = "active", nullable = false)
  private boolean active;

  public String getPostalCode() {
    return postalCode;
  }

  public String getZoneCode() {
    return zoneCode;
  }

  public String getZoneName() {
    return zoneName;
  }

  public boolean isActive() {
    return active;
  }
}
