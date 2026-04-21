package com.cotizador.danos.quote.domain;

import java.time.Instant;

public final class Quote {

  public static final int INITIAL_VERSION = 1;

  private final String folio;
  private final String parentQuoteFolio;
  private final String productCode;
  private final String customerName;
  private final String currency;
  private final String observations;
  private final QuoteLocationLayout locationLayout;
  private final QuoteStatus status;
  private final int version;
  private final Instant createdAt;
  private final Instant modifiedAt;

  private Quote(
      String folio,
      String parentQuoteFolio,
      String productCode,
      String customerName,
      String currency,
      String observations,
      QuoteLocationLayout locationLayout,
      QuoteStatus status,
      int version,
      Instant createdAt,
      Instant modifiedAt
  ) {
    this.folio = folio;
    this.parentQuoteFolio = parentQuoteFolio;
    this.productCode = productCode;
    this.customerName = customerName;
    this.currency = currency;
    this.observations = observations;
    this.locationLayout = locationLayout;
    this.status = status;
    this.version = version;
    this.createdAt = createdAt;
    this.modifiedAt = modifiedAt;
  }

  public static Quote createNew(String folio, Instant createdAt) {
    return new Quote(
        folio,
        null,
        null,
        null,
        null,
        null,
        null,
        QuoteStatus.DRAFT,
        INITIAL_VERSION,
        createdAt,
        null
    );
  }

  public Quote createNewVersion(String newFolio, Instant createdAt) {
    return new Quote(
        newFolio,
        folio,
        productCode,
        customerName,
        currency,
        observations,
        locationLayout,
        QuoteStatus.DRAFT,
        version + 1,
        createdAt,
        null
    );
  }

  public Quote updateGeneralData(QuoteGeneralDataPatch patch, Instant modifiedAt) {
    return copyWith(
        keepCurrentIfMissing(patch.productCode(), productCode),
        keepCurrentIfMissing(patch.customerName(), customerName),
        keepCurrentIfMissing(patch.currency(), currency),
        keepCurrentIfMissing(patch.observations(), observations),
        locationLayout,
        modifiedAt,
        version + 1
    );
  }

  public Quote updateLocationLayout(QuoteLocationLayout locationLayout, Instant modifiedAt) {
    return copyWith(
        productCode,
        customerName,
        currency,
        observations,
        locationLayout,
        modifiedAt,
        version + 1
    );
  }

  public Quote markAsCalculated() {
    return copyWithStatus(QuoteStatus.CALCULATED);
  }

  public Quote markAsSaved() {
    return copyWithStatus(QuoteStatus.SAVED);
  }

  public Quote incrementBusinessVersion(Instant modifiedAt) {
    return copyWith(
        productCode,
        customerName,
        currency,
        observations,
        locationLayout,
        modifiedAt,
        version + 1
    );
  }

  private Quote copyWithStatus(QuoteStatus status) {
    return new Quote(
        folio,
        parentQuoteFolio,
        productCode,
        customerName,
        currency,
        observations,
        locationLayout,
        status,
        version,
        createdAt,
        modifiedAt
    );
  }

  private String keepCurrentIfMissing(String newValue, String currentValue) {
    return newValue != null ? newValue : currentValue;
  }

  private Quote copyWith(
      String productCode,
      String customerName,
      String currency,
      String observations,
      QuoteLocationLayout locationLayout,
      Instant modifiedAt,
      int version
  ) {
    return new Quote(
        folio,
        parentQuoteFolio,
        productCode,
        customerName,
        currency,
        observations,
        locationLayout,
        status,
        version,
        createdAt,
        modifiedAt
    );
  }

  public String getFolio() {
    return folio;
  }

  public String getParentQuoteFolio() {
    return parentQuoteFolio;
  }

  public String getProductCode() {
    return productCode;
  }

  public String getCustomerName() {
    return customerName;
  }

  public String getCurrency() {
    return currency;
  }

  public String getObservations() {
    return observations;
  }

  public QuoteLocationLayout getLocationLayout() {
    return locationLayout;
  }

  public QuoteStatus getStatus() {
    return status;
  }

  public int getVersion() {
    return version;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getModifiedAt() {
    return modifiedAt;
  }
}
