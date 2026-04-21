package com.cotizador.danos.quote.api;

import com.cotizador.danos.quote.api.dto.GeneralInfoRequest;
import com.cotizador.danos.quote.api.dto.LocationLayoutRequest;
import com.cotizador.danos.quote.application.GetQuoteByFolioUseCase;
import com.cotizador.danos.quote.application.GetQuoteFinalStatusUseCase;
import com.cotizador.danos.quote.application.SaveQuoteLocationLayoutUseCase;
import com.cotizador.danos.quote.application.SaveQuoteUseCase;
import com.cotizador.danos.quote.application.UpdateQuoteGeneralDataUseCase;
import com.cotizador.danos.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/quotes/{folio}")
public class QuoteController {

  private final GetQuoteByFolioUseCase getQuoteByFolioUseCase;
  private final UpdateQuoteGeneralDataUseCase updateQuoteGeneralDataUseCase;
  private final SaveQuoteLocationLayoutUseCase saveQuoteLocationLayoutUseCase;
  private final GetQuoteFinalStatusUseCase getQuoteFinalStatusUseCase;
  private final SaveQuoteUseCase saveQuoteUseCase;
  private final QuoteApiMapper mapper;

  public QuoteController(
      GetQuoteByFolioUseCase getQuoteByFolioUseCase,
      UpdateQuoteGeneralDataUseCase updateQuoteGeneralDataUseCase,
      SaveQuoteLocationLayoutUseCase saveQuoteLocationLayoutUseCase,
      GetQuoteFinalStatusUseCase getQuoteFinalStatusUseCase,
      SaveQuoteUseCase saveQuoteUseCase,
      QuoteApiMapper mapper
  ) {
    this.getQuoteByFolioUseCase = getQuoteByFolioUseCase;
    this.updateQuoteGeneralDataUseCase = updateQuoteGeneralDataUseCase;
    this.saveQuoteLocationLayoutUseCase = saveQuoteLocationLayoutUseCase;
    this.getQuoteFinalStatusUseCase = getQuoteFinalStatusUseCase;
    this.saveQuoteUseCase = saveQuoteUseCase;
    this.mapper = mapper;
  }

  @GetMapping("/general-info")
  public ApiResponse<?> getGeneralInfo(@PathVariable String folio) {
    return ApiResponse.of(mapper.toGeneralInfoResponse(getQuoteByFolioUseCase.handle(folio)));
  }

  @PutMapping("/general-info")
  public ApiResponse<?> saveGeneralInfo(@PathVariable String folio, @Valid @RequestBody GeneralInfoRequest request) {
    return ApiResponse.of(mapper.toGeneralInfoResponse(
        updateQuoteGeneralDataUseCase.handle(folio, mapper.toPatch(request))
    ));
  }

  @GetMapping("/locations/layout")
  public ApiResponse<?> getLocationLayout(@PathVariable String folio) {
    return ApiResponse.of(mapper.toLayoutResponse(getQuoteByFolioUseCase.handle(folio)));
  }

  @PutMapping("/locations/layout")
  public ApiResponse<?> saveLocationLayout(
      @PathVariable String folio,
      @Valid @RequestBody LocationLayoutRequest request
  ) {
    return ApiResponse.of(mapper.toLayoutResponse(
        saveQuoteLocationLayoutUseCase.handle(folio, mapper.toLayout(request))
    ));
  }

  @GetMapping("/state")
  public ApiResponse<?> getState(@PathVariable String folio) {
    return ApiResponse.of(mapper.toStateResponse(getQuoteFinalStatusUseCase.handle(folio)));
  }

  @PostMapping("/save")
  public ApiResponse<?> saveQuote(@PathVariable String folio) {
    return ApiResponse.of(mapper.toGeneralInfoResponse(saveQuoteUseCase.handle(folio)));
  }
}
