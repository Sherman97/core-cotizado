package com.cotizador.danos.calculation.infrastructure.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "calculation_traces")
public class CalculationTraceJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "quote_folio", nullable = false, length = 64)
  private String quoteFolio;

  @Column(name = "location_id")
  private Long locationId;

  @Column(name = "factor_type", nullable = false, length = 64)
  private String factorType;

  @Column(name = "applied_value", nullable = false)
  private double appliedValue;

  @Column(name = "factor_order", nullable = false)
  private int factorOrder;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "calculation_trace_metadata", joinColumns = @JoinColumn(name = "trace_id"))
  @MapKeyColumn(name = "metadata_key", length = 64)
  @Column(name = "metadata_value", length = 255)
  private Map<String, String> metadata = new LinkedHashMap<>();

  public String getQuoteFolio() {
    return quoteFolio;
  }

  public void setQuoteFolio(String quoteFolio) {
    this.quoteFolio = quoteFolio;
  }

  public Long getLocationId() {
    return locationId;
  }

  public void setLocationId(Long locationId) {
    this.locationId = locationId;
  }

  public String getFactorType() {
    return factorType;
  }

  public void setFactorType(String factorType) {
    this.factorType = factorType;
  }

  public double getAppliedValue() {
    return appliedValue;
  }

  public void setAppliedValue(double appliedValue) {
    this.appliedValue = appliedValue;
  }

  public int getFactorOrder() {
    return factorOrder;
  }

  public void setFactorOrder(int factorOrder) {
    this.factorOrder = factorOrder;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }
}
