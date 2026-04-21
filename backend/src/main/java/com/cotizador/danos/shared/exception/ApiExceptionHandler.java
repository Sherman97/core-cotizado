package com.cotizador.danos.shared.exception;

import com.cotizador.danos.agent.application.AgentNotFoundException;
import com.cotizador.danos.location.domain.LocationNotFoundException;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.shared.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(QuoteNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleQuoteNotFound(
      QuoteNotFoundException exception,
      HttpServletRequest request
  ) {
    return errorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
  }

  @ExceptionHandler(LocationNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleLocationNotFound(
      LocationNotFoundException exception,
      HttpServletRequest request
  ) {
    return errorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
  }

  @ExceptionHandler(AgentNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleAgentNotFound(
      AgentNotFoundException exception,
      HttpServletRequest request
  ) {
    return errorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request, List.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(
      MethodArgumentNotValidException exception,
      HttpServletRequest request
  ) {
    List<ApiErrorResponse.FieldValidationError> validationErrors = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(this::toFieldError)
        .toList();

    return errorResponse(HttpStatus.BAD_REQUEST, "Validation error", request, validationErrors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(
      HttpMessageNotReadableException exception,
      HttpServletRequest request
  ) {
    return errorResponse(HttpStatus.BAD_REQUEST, "Malformed request body", request, List.of());
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ResponseEntity<ApiErrorResponse> handleOptimisticLocking(
      OptimisticLockingFailureException exception,
      HttpServletRequest request
  ) {
    return errorResponse(HttpStatus.CONFLICT, "Concurrent update detected", request, List.of());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
      IllegalArgumentException exception,
      HttpServletRequest request
  ) {
    return errorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request, List.of());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleUnexpected(
      Exception exception,
      HttpServletRequest request
  ) {
    return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request, List.of());
  }

  private ApiErrorResponse.FieldValidationError toFieldError(FieldError fieldError) {
    return new ApiErrorResponse.FieldValidationError(
        fieldError.getField(),
        fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage()
    );
  }

  private ResponseEntity<ApiErrorResponse> errorResponse(
      HttpStatus status,
      String message,
      HttpServletRequest request,
      List<ApiErrorResponse.FieldValidationError> validationErrors
  ) {
    return ResponseEntity.status(status).body(new ApiErrorResponse(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        request.getRequestURI(),
        validationErrors
    ));
  }
}
