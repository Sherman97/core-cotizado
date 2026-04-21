package com.cotizador.danos.quote.infrastructure.entity;

import com.cotizador.danos.quote.domain.QuoteStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@Table(name = "quotes")
public class QuoteJpaEntity {

  @Id
  @Column(name = "folio", nullable = false, length = 64)
  private String folio;

  @Column(name = "root_folio", nullable = false, length = 64)
  private String rootFolio;

  @Column(name = "parent_quote_folio", length = 64)
  private String parentQuoteFolio;

  @Column(name = "product_code", length = 64)
  private String productCode;

  @Column(name = "customer_name", length = 255)
  private String customerName;

  @Column(name = "currency", length = 16)
  private String currency;

  @Column(name = "agent_code", length = 32)
  private String agentCode;

  @Column(name = "agent_name_snapshot", length = 255)
  private String agentNameSnapshot;

  @Column(name = "observations", length = 1000)
  private String observations;

  @Column(name = "layout_expected_location_count")
  private Integer layoutExpectedLocationCount;

  @Column(name = "layout_capture_risk_zone")
  private Boolean layoutCaptureRiskZone;

  @Column(name = "layout_capture_georeference")
  private Boolean layoutCaptureGeoreference;

  @Column(name = "layout_notes", length = 1000)
  private String layoutNotes;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private QuoteStatus status;

  @Column(name = "business_version", nullable = false)
  private int businessVersion;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "modified_at")
  private Instant modifiedAt;

  @Version
  @Column(name = "lock_version", nullable = false)
  private Long lockVersion;

  public String getFolio() {
    return folio;
  }

  public void setFolio(String folio) {
    this.folio = folio;
  }

  public String getRootFolio() {
    return rootFolio;
  }

  public void setRootFolio(String rootFolio) {
    this.rootFolio = rootFolio;
  }

  public String getParentQuoteFolio() {
    return parentQuoteFolio;
  }

  public void setParentQuoteFolio(String parentQuoteFolio) {
    this.parentQuoteFolio = parentQuoteFolio;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = observations;
  }

  public String getAgentCode() {
    return agentCode;
  }

  public void setAgentCode(String agentCode) {
    this.agentCode = agentCode;
  }

  public String getAgentNameSnapshot() {
    return agentNameSnapshot;
  }

  public void setAgentNameSnapshot(String agentNameSnapshot) {
    this.agentNameSnapshot = agentNameSnapshot;
  }

  public Integer getLayoutExpectedLocationCount() {
    return layoutExpectedLocationCount;
  }

  public void setLayoutExpectedLocationCount(Integer layoutExpectedLocationCount) {
    this.layoutExpectedLocationCount = layoutExpectedLocationCount;
  }

  public Boolean getLayoutCaptureRiskZone() {
    return layoutCaptureRiskZone;
  }

  public void setLayoutCaptureRiskZone(Boolean layoutCaptureRiskZone) {
    this.layoutCaptureRiskZone = layoutCaptureRiskZone;
  }

  public Boolean getLayoutCaptureGeoreference() {
    return layoutCaptureGeoreference;
  }

  public void setLayoutCaptureGeoreference(Boolean layoutCaptureGeoreference) {
    this.layoutCaptureGeoreference = layoutCaptureGeoreference;
  }

  public String getLayoutNotes() {
    return layoutNotes;
  }

  public void setLayoutNotes(String layoutNotes) {
    this.layoutNotes = layoutNotes;
  }

  public QuoteStatus getStatus() {
    return status;
  }

  public void setStatus(QuoteStatus status) {
    this.status = status;
  }

  public int getBusinessVersion() {
    return businessVersion;
  }

  public void setBusinessVersion(int businessVersion) {
    this.businessVersion = businessVersion;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getModifiedAt() {
    return modifiedAt;
  }

  public void setModifiedAt(Instant modifiedAt) {
    this.modifiedAt = modifiedAt;
  }
}
