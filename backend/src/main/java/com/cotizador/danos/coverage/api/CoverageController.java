package com.cotizador.danos.coverage.api;

import com.cotizador.danos.coverage.api.dto.SaveCoverageOptionsRequest;
import com.cotizador.danos.coverage.application.ConfigureQuoteCoveragesUseCase;
import com.cotizador.danos.coverage.application.GetQuoteCoverageOptionsUseCase;
import com.cotizador.danos.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/quotes/{folio}/coverage-options")
public class CoverageController {

  private final GetQuoteCoverageOptionsUseCase getQuoteCoverageOptionsUseCase;
  private final ConfigureQuoteCoveragesUseCase configureQuoteCoveragesUseCase;
  private final CoverageApiMapper mapper;

  public CoverageController(
      GetQuoteCoverageOptionsUseCase getQuoteCoverageOptionsUseCase,
      ConfigureQuoteCoveragesUseCase configureQuoteCoveragesUseCase,
      CoverageApiMapper mapper
  ) {
    this.getQuoteCoverageOptionsUseCase = getQuoteCoverageOptionsUseCase;
    this.configureQuoteCoveragesUseCase = configureQuoteCoveragesUseCase;
    this.mapper = mapper;
  }

  @GetMapping
  public ApiResponse<?> getCoverageOptions(@PathVariable String folio) {
    return ApiResponse.of(mapper.toResponse(getQuoteCoverageOptionsUseCase.handle(folio)));
  }

  @PutMapping
  public ApiResponse<?> saveCoverageOptions(
      @PathVariable String folio,
      @Valid @RequestBody SaveCoverageOptionsRequest request
  ) {
    configureQuoteCoveragesUseCase.handle(
        folio,
        request.coverages().stream().map(mapper::toPatch).toList()
    );
    return ApiResponse.of(mapper.toResponse(getQuoteCoverageOptionsUseCase.handle(folio)));
  }
}
