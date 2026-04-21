package com.cotizador.danos.location.api;

import com.cotizador.danos.calculation.api.CalculationApiMapper;
import com.cotizador.danos.calculation.application.GetQuoteLocationResultsUseCase;
import com.cotizador.danos.location.api.dto.SaveLocationsRequest;
import com.cotizador.danos.location.api.dto.UpdateLocationRequest;
import com.cotizador.danos.location.application.GetQuoteLocationSummaryUseCase;
import com.cotizador.danos.location.application.ListQuoteLocationsUseCase;
import com.cotizador.danos.location.application.ReplaceQuoteLocationsUseCase;
import com.cotizador.danos.location.application.UpdateQuoteLocationUseCase;
import com.cotizador.danos.shared.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/quotes/{folio}/locations")
public class LocationController {

  private final ReplaceQuoteLocationsUseCase replaceQuoteLocationsUseCase;
  private final ListQuoteLocationsUseCase listQuoteLocationsUseCase;
  private final UpdateQuoteLocationUseCase updateQuoteLocationUseCase;
  private final GetQuoteLocationSummaryUseCase getQuoteLocationSummaryUseCase;
  private final GetQuoteLocationResultsUseCase getQuoteLocationResultsUseCase;
  private final LocationApiMapper mapper;
  private final CalculationApiMapper calculationApiMapper;

  public LocationController(
      ReplaceQuoteLocationsUseCase replaceQuoteLocationsUseCase,
      ListQuoteLocationsUseCase listQuoteLocationsUseCase,
      UpdateQuoteLocationUseCase updateQuoteLocationUseCase,
      GetQuoteLocationSummaryUseCase getQuoteLocationSummaryUseCase,
      GetQuoteLocationResultsUseCase getQuoteLocationResultsUseCase,
      LocationApiMapper mapper,
      CalculationApiMapper calculationApiMapper
  ) {
    this.replaceQuoteLocationsUseCase = replaceQuoteLocationsUseCase;
    this.listQuoteLocationsUseCase = listQuoteLocationsUseCase;
    this.updateQuoteLocationUseCase = updateQuoteLocationUseCase;
    this.getQuoteLocationSummaryUseCase = getQuoteLocationSummaryUseCase;
    this.getQuoteLocationResultsUseCase = getQuoteLocationResultsUseCase;
    this.mapper = mapper;
    this.calculationApiMapper = calculationApiMapper;
  }

  @GetMapping
  public ApiResponse<?> getLocations(@PathVariable String folio) {
    return ApiResponse.of(listQuoteLocationsUseCase.handle(folio).stream().map(mapper::toResponse).toList());
  }

  @PutMapping
  public ApiResponse<?> saveLocations(@PathVariable String folio, @Valid @RequestBody SaveLocationsRequest request) {
    return ApiResponse.of(replaceQuoteLocationsUseCase.handle(
        folio,
        request.locations().stream().map(mapper::toPatch).toList()
    ).stream().map(mapper::toResponse).toList());
  }

  @PatchMapping("/{indice}")
  public ApiResponse<?> updateLocation(
      @PathVariable String folio,
      @PathVariable long indice,
      @Valid @RequestBody UpdateLocationRequest request
  ) {
    return ApiResponse.of(mapper.toResponse(
        updateQuoteLocationUseCase.handle(folio, indice, mapper.toUpdatePatch(request))
    ));
  }

  @GetMapping("/summary")
  public ApiResponse<?> getSummary(@PathVariable String folio) {
    return ApiResponse.of(mapper.toSummaryResponse(getQuoteLocationSummaryUseCase.handle(folio)));
  }

  @GetMapping("/results")
  public ApiResponse<?> getLocationResults(@PathVariable String folio) {
    return ApiResponse.of(Map.of(
        "items",
        getQuoteLocationResultsUseCase.handle(folio).stream()
            .map(calculationApiMapper::toLocationResultResponse)
            .toList()
    ));
  }
}
