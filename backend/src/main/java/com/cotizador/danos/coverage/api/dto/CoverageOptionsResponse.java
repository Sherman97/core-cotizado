package com.cotizador.danos.coverage.api.dto;

import java.util.List;

public record CoverageOptionsResponse(
    List<CoverageCatalogItemResponse> availableCoverages,
    List<CoverageSelectionResponse> selectedCoverages
) {
}
