package com.cotizador.danos.location.domain;

import java.util.List;

public final class QuoteLocation {

  private static final String INCOMPLETE_REQUIRED_DATA_ALERT = "Location has incomplete required data";

  private final long id;
  private final String quoteFolio;
  private final Integer locationIndex;
  private final String locationName;
  private final String city;
  private final String colony;
  private final String municipality;
  private final String department;
  private final String address;
  private final String postalCode;
  private final String constructionType;
  private final Integer constructionLevel;
  private final Integer constructionYear;
  private final String occupancyType;
  private final String fireKey;
  private final Boolean catastrophicZone;
  private final long insuredValue;
  private final List<String> guarantees;
  private final LocationValidationStatus validationStatus;
  private final List<String> alerts;

  private QuoteLocation(
      long id,
      String quoteFolio,
      Integer locationIndex,
      String locationName,
      String city,
      String colony,
      String municipality,
      String department,
      String address,
      String postalCode,
      String constructionType,
      Integer constructionLevel,
      Integer constructionYear,
      String occupancyType,
      String fireKey,
      Boolean catastrophicZone,
      long insuredValue,
      List<String> guarantees,
      LocationValidationStatus validationStatus,
      List<String> alerts
  ) {
    this.id = id;
    this.quoteFolio = quoteFolio;
    this.locationIndex = locationIndex;
    this.locationName = locationName;
    this.city = city;
    this.colony = colony;
    this.municipality = municipality;
    this.department = department;
    this.address = address;
    this.postalCode = postalCode;
    this.constructionType = constructionType;
    this.constructionLevel = constructionLevel;
    this.constructionYear = constructionYear;
    this.occupancyType = occupancyType;
    this.fireKey = fireKey;
    this.catastrophicZone = catastrophicZone;
    this.insuredValue = insuredValue;
    this.guarantees = guarantees;
    this.validationStatus = validationStatus;
    this.alerts = alerts;
  }

  public static QuoteLocation create(long id, String quoteFolio, QuoteLocationPatch patch) {
    boolean isComplete = patch.hasRequiredData();
    return new QuoteLocation(
        id,
        quoteFolio,
        patch.locationIndex(),
        patch.locationName(),
        patch.city(),
        patch.colony(),
        patch.municipality(),
        patch.department(),
        patch.address(),
        patch.postalCode(),
        patch.constructionType(),
        patch.constructionLevel(),
        patch.constructionYear(),
        patch.occupancyType(),
        patch.fireKey(),
        patch.catastrophicZone(),
        patch.insuredValue(),
        patch.guarantees() != null ? patch.guarantees() : List.of(),
        isComplete ? LocationValidationStatus.COMPLETE : LocationValidationStatus.INCOMPLETE,
        isComplete ? List.of() : List.of(INCOMPLETE_REQUIRED_DATA_ALERT)
    );
  }

  public static QuoteLocation invalid(long id, String quoteFolio, String locationName, List<String> alerts) {
    return new QuoteLocation(
        id,
        quoteFolio,
        null,
        locationName,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0L,
        List.of(),
        LocationValidationStatus.INVALID,
        alerts
    );
  }

  public QuoteLocation update(QuoteLocationUpdatePatch patch) {
    Integer updatedLocationIndex = patch.locationIndex() != null ? patch.locationIndex() : locationIndex;
    String updatedLocationName = keepCurrentIfMissing(patch.locationName(), locationName);
    String updatedCity = keepCurrentIfMissing(patch.city(), city);
    String updatedColony = keepCurrentIfMissing(patch.colony(), colony);
    String updatedMunicipality = keepCurrentIfMissing(patch.municipality(), municipality);
    String updatedDepartment = keepCurrentIfMissing(patch.department(), department);
    String updatedAddress = keepCurrentIfMissing(patch.address(), address);
    String updatedPostalCode = keepCurrentIfMissing(patch.postalCode(), postalCode);
    String updatedConstructionType = keepCurrentIfMissing(patch.constructionType(), constructionType);
    Integer updatedConstructionLevel = patch.constructionLevel() != null ? patch.constructionLevel() : constructionLevel;
    Integer updatedConstructionYear = patch.constructionYear() != null ? patch.constructionYear() : constructionYear;
    String updatedOccupancyType = keepCurrentIfMissing(patch.occupancyType(), occupancyType);
    String updatedFireKey = keepCurrentIfMissing(patch.fireKey(), fireKey);
    Boolean updatedCatastrophicZone = patch.catastrophicZone() != null ? patch.catastrophicZone() : catastrophicZone;
    long updatedInsuredValue = patch.insuredValue() != null ? patch.insuredValue() : insuredValue;
    List<String> updatedGuarantees = patch.guarantees() != null ? patch.guarantees() : guarantees;

    return recreate(
        updatedLocationIndex,
        updatedLocationName,
        updatedCity,
        updatedColony,
        updatedMunicipality,
        updatedDepartment,
        updatedAddress,
        updatedPostalCode,
        updatedConstructionType,
        updatedConstructionLevel,
        updatedConstructionYear,
        updatedOccupancyType,
        updatedFireKey,
        updatedCatastrophicZone,
        updatedInsuredValue,
        updatedGuarantees
    );
  }

  private QuoteLocation recreate(
      Integer locationIndex,
      String locationName,
      String city,
      String colony,
      String municipality,
      String department,
      String address,
      String postalCode,
      String constructionType,
      Integer constructionLevel,
      Integer constructionYear,
      String occupancyType,
      String fireKey,
      Boolean catastrophicZone,
      long insuredValue,
      List<String> guarantees
  ) {
    boolean isComplete = hasValue(address) && hasValue(postalCode);

    return new QuoteLocation(
        id,
        quoteFolio,
        locationIndex,
        locationName,
        city,
        colony,
        municipality,
        department,
        address,
        postalCode,
        constructionType,
        constructionLevel,
        constructionYear,
        occupancyType,
        fireKey,
        catastrophicZone,
        insuredValue,
        guarantees,
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

  public Integer getLocationIndex() {
    return locationIndex;
  }

  public String getLocationName() {
    return locationName;
  }

  public String getCity() {
    return city;
  }

  public String getColony() {
    return colony;
  }

  public String getMunicipality() {
    return municipality;
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

  public Integer getConstructionLevel() {
    return constructionLevel;
  }

  public Integer getConstructionYear() {
    return constructionYear;
  }

  public String getOccupancyType() {
    return occupancyType;
  }

  public String getFireKey() {
    return fireKey;
  }

  public Boolean getCatastrophicZone() {
    return catastrophicZone;
  }

  public long getInsuredValue() {
    return insuredValue;
  }

  public List<String> getGuarantees() {
    return guarantees;
  }

  public LocationValidationStatus getValidationStatus() {
    return validationStatus;
  }

  public List<String> getAlerts() {
    return alerts;
  }
}
