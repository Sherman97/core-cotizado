package com.cotizador.danos.calculation.infrastructure.entity;

import com.cotizador.danos.location.domain.LocationValidationStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "location_calculation_results")
public class LocationCalculationResultJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quote_folio", nullable = false)
  private QuoteCalculationResultJpaEntity calculationResult;

  @Column(name = "location_id", nullable = false)
  private long locationId;

  @Column(name = "location_name", nullable = false, length = 255)
  private String locationName;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private LocationValidationStatus status;

  @Column(name = "premium", nullable = false)
  private double premium;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "location_calculation_alerts", joinColumns = @JoinColumn(name = "location_result_id"))
  @Column(name = "alert_message", nullable = false, length = 255)
  @OrderColumn(name = "alert_order")
  private List<String> alerts = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public QuoteCalculationResultJpaEntity getCalculationResult() {
    return calculationResult;
  }

  public void setCalculationResult(QuoteCalculationResultJpaEntity calculationResult) {
    this.calculationResult = calculationResult;
  }

  public long getLocationId() {
    return locationId;
  }

  public void setLocationId(long locationId) {
    this.locationId = locationId;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public LocationValidationStatus getStatus() {
    return status;
  }

  public void setStatus(LocationValidationStatus status) {
    this.status = status;
  }

  public double getPremium() {
    return premium;
  }

  public void setPremium(double premium) {
    this.premium = premium;
  }

  public List<String> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<String> alerts) {
    this.alerts = alerts;
  }
}
