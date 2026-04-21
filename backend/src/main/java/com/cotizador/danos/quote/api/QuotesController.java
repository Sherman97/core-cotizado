package com.cotizador.danos.quote.api;

import com.cotizador.danos.quote.application.ListQuotesUseCase;
import com.cotizador.danos.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/quotes")
public class QuotesController {

  private final ListQuotesUseCase listQuotesUseCase;
  private final QuoteApiMapper mapper;

  public QuotesController(
      ListQuotesUseCase listQuotesUseCase,
      QuoteApiMapper mapper
  ) {
    this.listQuotesUseCase = listQuotesUseCase;
    this.mapper = mapper;
  }

  @GetMapping
  public ApiResponse<?> listQuotes() {
    return ApiResponse.of(
        listQuotesUseCase.handle().stream()
            .map(mapper::toQuoteListItemResponse)
            .toList()
    );
  }
}
