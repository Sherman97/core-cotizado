package com.cotizador.danos.location.domain;

public class LocationNotFoundException extends RuntimeException {

  public LocationNotFoundException(long locationId) {
    super("Location not found for id: " + locationId);
  }
}
