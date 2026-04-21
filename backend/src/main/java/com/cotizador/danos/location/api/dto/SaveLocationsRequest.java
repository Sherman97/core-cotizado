package com.cotizador.danos.location.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaveLocationsRequest(
    @Valid @NotEmpty List<LocationUpsertItemRequest> locations
) {
}
