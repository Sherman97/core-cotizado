package com.cotizador.danos.coverage.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "quote_coverages")
public class QuoteCoverageJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "quote_folio", nullable = false, length = 64)
  private String quoteFolio;

  @Column(name = "coverage_code", nullable = false, length = 64)
  private String coverageCode;

  @Column(name = "coverage_name", nullable = false, length = 255)
  private String coverageName;

  @Column(name = "insured_limit", nullable = false)
  private long insuredLimit;

  @Column(name = "deductible_type", length = 64)
  private String deductibleType;

  @Column(name = "deductible_value")
  private Long deductibleValue;

  @Column(name = "selected", nullable = false)
  private boolean selected;

  public Long getId() {
    return id;
  }

  public String getQuoteFolio() {
    return quoteFolio;
  }

  public void setQuoteFolio(String quoteFolio) {
    this.quoteFolio = quoteFolio;
  }

  public String getCoverageCode() {
    return coverageCode;
  }

  public void setCoverageCode(String coverageCode) {
    this.coverageCode = coverageCode;
  }

  public String getCoverageName() {
    return coverageName;
  }

  public void setCoverageName(String coverageName) {
    this.coverageName = coverageName;
  }

  public long getInsuredLimit() {
    return insuredLimit;
  }

  public void setInsuredLimit(long insuredLimit) {
    this.insuredLimit = insuredLimit;
  }

  public String getDeductibleType() {
    return deductibleType;
  }

  public void setDeductibleType(String deductibleType) {
    this.deductibleType = deductibleType;
  }

  public Long getDeductibleValue() {
    return deductibleValue;
  }

  public void setDeductibleValue(Long deductibleValue) {
    this.deductibleValue = deductibleValue;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
