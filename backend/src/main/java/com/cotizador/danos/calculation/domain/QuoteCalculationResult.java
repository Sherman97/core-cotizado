package com.cotizador.danos.calculation.domain;

import java.util.List;

public final class QuoteCalculationResult {

  private static final double EXPENSE_RATE = 0.10;
  private static final double TAX_RATE = 0.16;

  private final double netPremium;
  private final double expenseAmount;
  private final double taxAmount;
  private final double totalPremium;
  private final List<LocationCalculationResult> locations;
  private final List<String> alerts;

  private QuoteCalculationResult(
      double netPremium,
      double expenseAmount,
      double taxAmount,
      double totalPremium,
      List<LocationCalculationResult> locations,
      List<String> alerts
  ) {
    this.netPremium = netPremium;
    this.expenseAmount = expenseAmount;
    this.taxAmount = taxAmount;
    this.totalPremium = totalPremium;
    this.locations = locations;
    this.alerts = alerts;
  }

  public static QuoteCalculationResult fromLocationResults(
      List<LocationCalculationResult> locations,
      List<String> alerts
  ) {
    double netPremium = locations.stream()
        .mapToDouble(LocationCalculationResult::premium)
        .sum();
    double expenseAmount = netPremium * EXPENSE_RATE;
    double taxAmount = (netPremium + expenseAmount) * TAX_RATE;
    double totalPremium = netPremium + expenseAmount + taxAmount;

    return new QuoteCalculationResult(
        netPremium,
        expenseAmount,
        taxAmount,
        totalPremium,
        locations,
        alerts
    );
  }

  public double getNetPremium() {
    return netPremium;
  }

  public double getExpenseAmount() {
    return expenseAmount;
  }

  public double getTaxAmount() {
    return taxAmount;
  }

  public double getTotalPremium() {
    return totalPremium;
  }

  public List<LocationCalculationResult> getLocations() {
    return locations;
  }

  public List<String> getAlerts() {
    return alerts;
  }
}
