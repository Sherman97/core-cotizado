package com.cotizador.danos.location.domain;

import java.util.List;

public final class QuoteLocation {

  private static final String INCOMPLETE_REQUIRED_DATA_ALERT = "Location has incomplete required data";

  private final long id;
  private final String quoteFolio;
  private final String locationName;
  private final String city;
  private final String department;
  private final String address;
  private final String postalCode;
  private final String constructionType;
  private final String occupancyType;
  private final long insuredValue;
  private final LocationValidationStatus validationStatus;
  private final List<String> alerts;

  private QuoteLocation(
      long id,
      String quoteFolio,
      String locationName,
      String city,
      String department,
      String address,
      String postalCode,
      String constructionType,
      String occupancyType,
      long insuredValue,
      LocationValidationStatus validationStatus,
      List<String> alerts
  ) {
    this.id = id;
    this.quoteFolio = quoteFolio;
    this.locationName = locationName;
    this.city = city;
    this.department = department;
    this.address = address;
    this.postalCode = postalCode;
    this.constructionType = constructionType;
    this.occupancyType = occupancyType;
    this.insuredValue = insuredValue;
    this.validationStatus = validationStatus;
    this.alerts = alerts;
  }

  public static QuoteLocation create(long id, String quoteFolio, QuoteLocationPatch patch) {
    boolean isComplete = patch.hasRequiredData();
    return new QuoteLocation(
        id,
        quoteFolio,
        patch.locationName(),
        patch.city(),
        patch.department(),
        patch.address(),
        patch.postalCode(),
        patch.constructionType(),
        patch.occupancyType(),
        patch.insuredValue(),
        isComplete ? LocationValidationStatus.COMPLETE : LocationValidationStatus.INCOMPLETE,
        isComplete ? List.of() : List.of(INCOMPLETE_REQUIRED_DATA_ALERT)
    );
  }

  public static QuoteLocation invalid(long id, String quoteFolio, String locationName, List<String> alerts) {
    return new QuoteLocation(
        id,
        quoteFolio,
        locationName,
        null,
        null,
        null,
        null,
        null,
        null,
        0L,
        LocationValidationStatus.INVALID,
        alerts
    );
  }

  public QuoteLocation update(QuoteLocationUpdatePatch patch) {
    String updatedLocationName = keepCurrentIfMissing(patch.locationName(), locationName);
    String updatedCity = keepCurrentIfMissing(patch.city(), city);
    String updatedDepartment = keepCurrentIfMissing(patch.department(), department);
    String updatedAddress = keepCurrentIfMissing(patch.address(), address);
    String updatedPostalCode = keepCurrentIfMissing(patch.postalCode(), postalCode);
    String updatedConstructionType = keepCurrentIfMissing(patch.constructionType(), constructionType);
    String updatedOccupancyType = keepCurrentIfMissing(patch.occupancyType(), occupancyType);
    long updatedInsuredValue = patch.insuredValue() != null ? patch.insuredValue() : insuredValue;

    return recreate(
        updatedLocationName,
        updatedCity,
        updatedDepartment,
        updatedAddress,
        updatedPostalCode,
        updatedConstructionType,
        updatedOccupancyType,
        updatedInsuredValue
    );
  }

  private QuoteLocation recreate(
      String locationName,
      String city,
      String department,
      String address,
      String postalCode,
      String constructionType,
      String occupancyType,
      long insuredValue
  ) {
    boolean isComplete = hasValue(address) && hasValue(postalCode);

    return new QuoteLocation(
        id,
        quoteFolio,
        locationName,
        city,
        department,
        address,
        postalCode,
        constructionType,
        occupancyType,
        insuredValue,
        isComplete ? LocationValidationStatus.COMPLETE : LocationValidationStatus.INCOMPLETE,
        isComplete ? List.of() : List.of(INCOMPLETE_REQUIRED_DATA_ALERT)
    );
  }

  private static boolean hasValue(String value) {
    return value != null && !value.isBlank();
  }

  private String keepCurrentIfMissing(String newValue, String currentValue) {
    return newValue != null ? newValue : currentValue;
  }

  public long getId() {
    return id;
  }

  public String getQuoteFolio() {
    return quoteFolio;
  }

  public String getLocationName() {
    return locationName;
  }

  public String getCity() {
    return city;
  }

  public String getDepartment() {
    return department;
  }

  public String getAddress() {
    return address;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getConstructionType() {
    return constructionType;
  }

  public String getOccupancyType() {
    return occupancyType;
  }

  public long getInsuredValue() {
    return insuredValue;
  }

  public LocationValidationStatus getValidationStatus() {
    return validationStatus;
  }

  public List<String> getAlerts() {
    return alerts;
  }
}
