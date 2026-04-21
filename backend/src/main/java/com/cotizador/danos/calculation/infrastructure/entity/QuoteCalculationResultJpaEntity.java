package com.cotizador.danos.calculation.infrastructure.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quote_calculation_results")
public class QuoteCalculationResultJpaEntity {

  @Id
  @Column(name = "quote_folio", nullable = false, length = 64)
  private String quoteFolio;

  @Column(name = "net_premium", nullable = false)
  private double netPremium;

  @Column(name = "expense_amount", nullable = false)
  private double expenseAmount;

  @Column(name = "tax_amount", nullable = false)
  private double taxAmount;

  @Column(name = "total_premium", nullable = false)
  private double totalPremium;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "quote_calculation_alerts", joinColumns = @JoinColumn(name = "quote_folio"))
  @Column(name = "alert_message", nullable = false, length = 255)
  @OrderColumn(name = "alert_order")
  private List<String> alerts = new ArrayList<>();

  @OneToMany(mappedBy = "calculationResult", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  @OrderBy("id ASC")
  private List<LocationCalculationResultJpaEntity> locations = new ArrayList<>();

  @Version
  @Column(name = "lock_version", nullable = false)
  private Long lockVersion;

  public String getQuoteFolio() {
    return quoteFolio;
  }

  public void setQuoteFolio(String quoteFolio) {
    this.quoteFolio = quoteFolio;
  }

  public double getNetPremium() {
    return netPremium;
  }

  public void setNetPremium(double netPremium) {
    this.netPremium = netPremium;
  }

  public double getExpenseAmount() {
    return expenseAmount;
  }

  public void setExpenseAmount(double expenseAmount) {
    this.expenseAmount = expenseAmount;
  }

  public double getTaxAmount() {
    return taxAmount;
  }

  public void setTaxAmount(double taxAmount) {
    this.taxAmount = taxAmount;
  }

  public double getTotalPremium() {
    return totalPremium;
  }

  public void setTotalPremium(double totalPremium) {
    this.totalPremium = totalPremium;
  }

  public List<String> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<String> alerts) {
    this.alerts = alerts;
  }

  public List<LocationCalculationResultJpaEntity> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationCalculationResultJpaEntity> locations) {
    this.locations = locations;
  }
}
