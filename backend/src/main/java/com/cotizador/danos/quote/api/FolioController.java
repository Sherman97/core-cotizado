package com.cotizador.danos.quote.api;

import com.cotizador.danos.quote.application.CreateQuoteWithIdempotencyResult;
import com.cotizador.danos.quote.application.CreateQuoteWithIdempotencyUseCase;
import com.cotizador.danos.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/folios")
public class FolioController {

  private final CreateQuoteWithIdempotencyUseCase createQuoteWithIdempotencyUseCase;
  private final QuoteApiMapper quoteApiMapper;

  public FolioController(
      CreateQuoteWithIdempotencyUseCase createQuoteWithIdempotencyUseCase,
      QuoteApiMapper quoteApiMapper
  ) {
    this.createQuoteWithIdempotencyUseCase = createQuoteWithIdempotencyUseCase;
    this.quoteApiMapper = quoteApiMapper;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<?>> createFolio(
      @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
  ) {
    CreateQuoteWithIdempotencyResult result = createQuoteWithIdempotencyUseCase.handle(idempotencyKey);
    HttpStatus status = result.replayed() ? HttpStatus.OK : HttpStatus.CREATED;
    return ResponseEntity.status(status).body(ApiResponse.of(quoteApiMapper.toCreateFolioResponse(result.quote())));
  }
}
