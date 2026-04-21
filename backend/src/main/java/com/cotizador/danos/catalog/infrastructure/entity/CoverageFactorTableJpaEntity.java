package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coverage_factor_tables")
public class CoverageFactorTableJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "product_code", nullable = false, length = 64)
  private String productCode;

  @Column(name = "coverage_code", nullable = false, length = 64)
  private String coverageCode;

  @Column(name = "factor_value", nullable = false)
  private double factorValue;

  @Column(name = "active", nullable = false)
  private boolean active;

  public Long getId() {
    return id;
  }

  public String getProductCode() {
    return productCode;
  }

  public String getCoverageCode() {
    return coverageCode;
  }

  public double getFactorValue() {
    return factorValue;
  }

  public boolean isActive() {
    return active;
  }
}
