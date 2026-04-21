package com.cotizador.danos.location.api;

import com.cotizador.danos.location.api.dto.LocationResponse;
import com.cotizador.danos.location.api.dto.LocationSummaryResponse;
import com.cotizador.danos.location.api.dto.LocationUpsertItemRequest;
import com.cotizador.danos.location.api.dto.UpdateLocationRequest;
import com.cotizador.danos.location.application.GetQuoteLocationSummaryUseCase;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.location.domain.QuoteLocationUpdatePatch;
import org.springframework.stereotype.Component;

@Component
public class LocationApiMapper {

  public QuoteLocationPatch toPatch(LocationUpsertItemRequest request) {
    return new QuoteLocationPatch(
        request.locationName(),
        request.city(),
        request.department(),
        request.address(),
        request.postalCode(),
        request.constructionType(),
        request.occupancyType(),
        request.insuredValue()
    );
  }

  public QuoteLocationUpdatePatch toUpdatePatch(UpdateLocationRequest request) {
    return new QuoteLocationUpdatePatch(
        request.locationName(),
        request.city(),
        request.department(),
        request.address(),
        request.postalCode(),
        request.constructionType(),
        request.occupancyType(),
        request.insuredValue()
    );
  }

  public LocationResponse toResponse(QuoteLocation location) {
    return new LocationResponse(
        location.getId(),
        location.getLocationName(),
        location.getCity(),
        location.getDepartment(),
        location.getAddress(),
        location.getPostalCode(),
        location.getConstructionType(),
        location.getOccupancyType(),
        location.getInsuredValue(),
        location.getValidationStatus().name(),
        location.getAlerts()
    );
  }

  public LocationSummaryResponse toSummaryResponse(GetQuoteLocationSummaryUseCase.Result result) {
    return new LocationSummaryResponse(
        result.totalLocations(),
        result.completeLocations(),
        result.incompleteLocations(),
        result.invalidLocations(),
        result.calculatedLocations(),
        result.calculatedPremium(),
        result.alerts()
    );
  }
}
