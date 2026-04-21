package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "zone_factors")
public class ZoneFactorJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "zone_code", nullable = false, length = 32)
  private String zoneCode;

  @Column(name = "zone_name", nullable = false, length = 120)
  private String zoneName;

  @Column(name = "factor_value", nullable = false)
  private double factorValue;

  @Column(name = "active", nullable = false)
  private boolean active;

  public Long getId() {
    return id;
  }

  public String getZoneCode() {
    return zoneCode;
  }

  public String getZoneName() {
    return zoneName;
  }

  public double getFactorValue() {
    return factorValue;
  }

  public boolean isActive() {
    return active;
  }
}
