package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "construction_factors")
public class ConstructionFactorJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "construction_type", nullable = false, length = 64)
  private String constructionType;

  @Column(name = "factor_value", nullable = false)
  private double factorValue;

  @Column(name = "active", nullable = false)
  private boolean active;

  public Long getId() {
    return id;
  }

  public String getConstructionType() {
    return constructionType;
  }

  public double getFactorValue() {
    return factorValue;
  }

  public boolean isActive() {
    return active;
  }
}
