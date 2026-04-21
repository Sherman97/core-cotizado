package com.cotizador.danos.catalog.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "calculation_parameters")
public class CalculationParameterJpaEntity {

  @Id
  @Column(name = "parameter_code", nullable = false, length = 64)
  private String parameterCode;

  @Column(name = "parameter_value", nullable = false)
  private double parameterValue;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "active", nullable = false)
  private boolean active;

  public String getParameterCode() {
    return parameterCode;
  }

  public double getParameterValue() {
    return parameterValue;
  }

  public String getDescription() {
    return description;
  }

  public boolean isActive() {
    return active;
  }
}
