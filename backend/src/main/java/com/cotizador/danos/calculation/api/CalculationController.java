package com.cotizador.danos.calculation.api;

import com.cotizador.danos.calculation.application.CalculateQuoteUseCase;
import com.cotizador.danos.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/quotes/{folio}")
public class CalculationController {

  private final CalculateQuoteUseCase calculateQuoteUseCase;
  private final CalculationApiMapper mapper;

  public CalculationController(CalculateQuoteUseCase calculateQuoteUseCase, CalculationApiMapper mapper) {
    this.calculateQuoteUseCase = calculateQuoteUseCase;
    this.mapper = mapper;
  }

  @PostMapping("/calculate")
  public ApiResponse<?> calculate(@PathVariable String folio) {
    return ApiResponse.of(mapper.toResponse(folio, calculateQuoteUseCase.handle(folio)));
  }
}
