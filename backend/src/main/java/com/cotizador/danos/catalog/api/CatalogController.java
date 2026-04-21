package com.cotizador.danos.catalog.api;

import com.cotizador.danos.catalog.application.GeographyCatalogQueryService;
import com.cotizador.danos.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/catalogs")
public class CatalogController {

  private final GeographyCatalogQueryService geographyCatalogQueryService;
  private final CatalogApiMapper mapper;

  public CatalogController(
      GeographyCatalogQueryService geographyCatalogQueryService,
      CatalogApiMapper mapper
  ) {
    this.geographyCatalogQueryService = geographyCatalogQueryService;
    this.mapper = mapper;
  }

  @GetMapping("/geography")
  public ApiResponse<?> getGeographyCatalog() {
    return ApiResponse.of(mapper.toResponse(geographyCatalogQueryService.getActiveGeography()));
  }
}
