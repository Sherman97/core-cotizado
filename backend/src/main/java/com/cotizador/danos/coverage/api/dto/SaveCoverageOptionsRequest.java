package com.cotizador.danos.coverage.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaveCoverageOptionsRequest(
    @Valid @NotEmpty List<CoverageOptionItemRequest> coverages
) {
}
