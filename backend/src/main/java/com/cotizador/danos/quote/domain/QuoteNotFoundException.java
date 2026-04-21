package com.cotizador.danos.quote.domain;

public class QuoteNotFoundException extends RuntimeException {

  private static final String MESSAGE_TEMPLATE = "Quote not found for folio: %s";

  public QuoteNotFoundException(String folio) {
    super(MESSAGE_TEMPLATE.formatted(folio));
  }
}
