package com.cotizador.danos.coverage.api;

import com.cotizador.danos.coverage.api.dto.CoverageCatalogItemResponse;
import com.cotizador.danos.coverage.api.dto.CoverageOptionItemRequest;
import com.cotizador.danos.coverage.api.dto.CoverageOptionsResponse;
import com.cotizador.danos.coverage.api.dto.CoverageSelectionResponse;
import com.cotizador.danos.coverage.application.GetQuoteCoverageOptionsUseCase;
import com.cotizador.danos.coverage.domain.CoverageCatalogItem;
import com.cotizador.danos.coverage.domain.QuoteCoveragePatch;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import org.springframework.stereotype.Component;

@Component
public class CoverageApiMapper {

  public QuoteCoveragePatch toPatch(CoverageOptionItemRequest request) {
    return new QuoteCoveragePatch(
        request.coverageCode(),
        request.coverageName(),
        request.insuredLimit(),
        request.deductibleType(),
        request.deductibleValue(),
        request.selected()
    );
  }

  public CoverageOptionsResponse toResponse(GetQuoteCoverageOptionsUseCase.Result result) {
    return new CoverageOptionsResponse(
        result.availableCoverages().stream().map(this::toCatalogItemResponse).toList(),
        result.selectedCoverages().stream().map(this::toSelectionResponse).toList()
    );
  }

  public CoverageSelectionResponse toSelectionResponse(QuoteCoverageSelection selection) {
    return new CoverageSelectionResponse(
        selection.getCoverageCode(),
        selection.getCoverageName(),
        selection.getInsuredLimit(),
        selection.getDeductibleType(),
        selection.getDeductibleValue(),
        selection.isSelected()
    );
  }

  private CoverageCatalogItemResponse toCatalogItemResponse(CoverageCatalogItem item) {
    return new CoverageCatalogItemResponse(item.code(), item.name(), item.active());
  }
}
