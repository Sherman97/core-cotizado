package com.cotizador.danos.location.infrastructure.entity;

import com.cotizador.danos.location.domain.LocationValidationStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quote_locations")
public class QuoteLocationJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "quote_folio", nullable = false, length = 64)
  private String quoteFolio;

  @Column(name = "location_name", nullable = false, length = 255)
  private String locationName;

  @Column(name = "city", length = 120)
  private String city;

  @Column(name = "department", length = 120)
  private String department;

  @Column(name = "address", length = 255)
  private String address;

  @Column(name = "postal_code", length = 32)
  private String postalCode;

  @Column(name = "construction_type", length = 64)
  private String constructionType;

  @Column(name = "occupancy_type", length = 64)
  private String occupancyType;

  @Column(name = "insured_value", nullable = false)
  private long insuredValue;

  @Enumerated(EnumType.STRING)
  @Column(name = "validation_status", nullable = false, length = 32)
  private LocationValidationStatus validationStatus;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "quote_location_alerts", joinColumns = @JoinColumn(name = "location_id"))
  @Column(name = "alert_message", nullable = false, length = 255)
  @OrderColumn(name = "alert_order")
  private List<String> alerts = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getQuoteFolio() {
    return quoteFolio;
  }

  public void setQuoteFolio(String quoteFolio) {
    this.quoteFolio = quoteFolio;
  }

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getConstructionType() {
    return constructionType;
  }

  public void setConstructionType(String constructionType) {
    this.constructionType = constructionType;
  }

  public String getOccupancyType() {
    return occupancyType;
  }

  public void setOccupancyType(String occupancyType) {
    this.occupancyType = occupancyType;
  }

  public long getInsuredValue() {
    return insuredValue;
  }

  public void setInsuredValue(long insuredValue) {
    this.insuredValue = insuredValue;
  }

  public LocationValidationStatus getValidationStatus() {
    return validationStatus;
  }

  public void setValidationStatus(LocationValidationStatus validationStatus) {
    this.validationStatus = validationStatus;
  }

  public List<String> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<String> alerts) {
    this.alerts = alerts;
  }
}
