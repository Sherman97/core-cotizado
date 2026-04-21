package com.cotizador.danos.coverage.domain;

import java.util.List;

public final class QuoteCoverageSelection {

  private final String quoteFolio;
  private final String coverageCode;
  private final String coverageName;
  private final long insuredLimit;
  private final String deductibleType;
  private final Long deductibleValue;
  private final boolean selected;

  private QuoteCoverageSelection(
      String quoteFolio,
      String coverageCode,
      String coverageName,
      long insuredLimit,
      String deductibleType,
      Long deductibleValue,
      boolean selected
  ) {
    this.quoteFolio = quoteFolio;
    this.coverageCode = coverageCode;
    this.coverageName = coverageName;
    this.insuredLimit = insuredLimit;
    this.deductibleType = deductibleType;
    this.deductibleValue = deductibleValue;
    this.selected = selected;
  }

  public static QuoteCoverageSelection create(String quoteFolio, QuoteCoveragePatch patch) {
    return new QuoteCoverageSelection(
        quoteFolio,
        patch.coverageCode(),
        patch.coverageName(),
        patch.insuredLimit(),
        patch.deductibleType(),
        patch.deductibleValue(),
        patch.selected()
    );
  }

  public static List<QuoteCoverageSelection> replaceAll(
      String quoteFolio,
      List<QuoteCoverageSelection> currentSelections,
      List<QuoteCoveragePatch> newSelections
  ) {
    return newSelections.stream()
        .map(patch -> create(quoteFolio, patch))
        .toList();
  }

  public String getQuoteFolio() {
    return quoteFolio;
  }

  public String getCoverageCode() {
    return coverageCode;
  }

  public String getCoverageName() {
    return coverageName;
  }

  public long getInsuredLimit() {
    return insuredLimit;
  }

  public String getDeductibleType() {
    return deductibleType;
  }

  public Long getDeductibleValue() {
    return deductibleValue;
  }

  public boolean isSelected() {
    return selected;
  }
}
