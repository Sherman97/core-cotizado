package com.cotizador.danos.quote.application;

import com.cotizador.danos.quote.domain.Quote;

public record CreateQuoteWithIdempotencyResult(
    Quote quote,
    boolean replayed
) {
}
