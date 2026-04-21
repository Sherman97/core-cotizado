package com.cotizador.danos.catalog.api;

import com.cotizador.danos.catalog.api.dto.CityResponse;
import com.cotizador.danos.catalog.api.dto.DepartmentResponse;
import com.cotizador.danos.catalog.api.dto.GeographyCatalogResponse;
import com.cotizador.danos.catalog.application.GeographyCatalogQueryService;
import org.springframework.stereotype.Component;

@Component
public class CatalogApiMapper {

  public GeographyCatalogResponse toResponse(Iterable<GeographyCatalogQueryService.DepartmentItem> departments) {
    return new GeographyCatalogResponse(
        toDepartmentResponses(departments)
    );
  }

  private java.util.List<DepartmentResponse> toDepartmentResponses(
      Iterable<GeographyCatalogQueryService.DepartmentItem> departments
  ) {
    java.util.List<DepartmentResponse> items = new java.util.ArrayList<>();
    for (GeographyCatalogQueryService.DepartmentItem department : departments) {
      items.add(new DepartmentResponse(
          department.code(),
          department.name(),
          department.cities().stream()
              .map(city -> new CityResponse(city.code(), city.name(), city.postalCodes()))
              .toList()
      ));
    }
    return items;
  }
}
