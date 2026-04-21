package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "geo_postal_codes")
public class GeoPostalCodeJpaEntity {

  @Id
  @Column(name = "postal_code", nullable = false, length = 32)
  private String postalCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "city_id", nullable = false)
  private GeoCityJpaEntity city;

  @Column(name = "active", nullable = false)
  private boolean active;

  public String getPostalCode() {
    return postalCode;
  }

  public GeoCityJpaEntity getCity() {
    return city;
  }

  public boolean isActive() {
    return active;
  }
}
