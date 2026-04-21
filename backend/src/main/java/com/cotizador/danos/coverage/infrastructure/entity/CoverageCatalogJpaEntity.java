package com.cotizador.danos.coverage.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coverage_catalog")
public class CoverageCatalogJpaEntity {

  @Id
  @Column(name = "code", nullable = false, length = 64)
  private String code;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "active", nullable = false)
  private boolean active;

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public boolean isActive() {
    return active;
  }
}
