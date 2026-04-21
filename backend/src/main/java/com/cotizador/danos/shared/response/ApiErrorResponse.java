package com.cotizador.danos.shared.response;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldValidationError> validationErrors
) {

  public record FieldValidationError(
      String field,
      String message
  ) {
  }
}
