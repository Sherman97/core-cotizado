package com.cotizador.danos.quote.api;

import com.cotizador.danos.quote.api.dto.CreateFolioResponse;
import com.cotizador.danos.quote.api.dto.GeneralInfoRequest;
import com.cotizador.danos.quote.api.dto.GeneralInfoResponse;
import com.cotizador.danos.quote.api.dto.LocationLayoutRequest;
import com.cotizador.danos.quote.api.dto.LocationLayoutResponse;
import com.cotizador.danos.quote.api.dto.QuoteListItemResponse;
import com.cotizador.danos.quote.api.dto.QuoteStateResponse;
import com.cotizador.danos.quote.application.ListQuotesUseCase;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteFinalStatusView;
import com.cotizador.danos.quote.domain.QuoteGeneralDataPatch;
import com.cotizador.danos.quote.domain.QuoteLocationLayout;
import org.springframework.stereotype.Component;

@Component
public class QuoteApiMapper {

  public CreateFolioResponse toCreateFolioResponse(Quote quote) {
    return new CreateFolioResponse(
        quote.getFolio(),
        quote.getStatus().name(),
        quote.getVersion()
    );
  }

  public QuoteGeneralDataPatch toPatch(GeneralInfoRequest request) {
    return new QuoteGeneralDataPatch(
        request.productCode(),
        request.customerName(),
        request.currency(),
        request.observations(),
        request.agentCode(),
        null
    );
  }

  public GeneralInfoResponse toGeneralInfoResponse(Quote quote) {
    return new GeneralInfoResponse(
        quote.getFolio(),
        quote.getProductCode(),
        quote.getCustomerName(),
        quote.getCurrency(),
        quote.getAgentCode(),
        quote.getAgentNameSnapshot(),
        quote.getObservations(),
        quote.getStatus().name(),
        quote.getVersion()
    );
  }

  public QuoteLocationLayout toLayout(LocationLayoutRequest request) {
    return new QuoteLocationLayout(
        request.expectedLocationCount(),
        request.captureRiskZone(),
        request.captureGeoreference(),
        request.notes()
    );
  }

  public LocationLayoutResponse toLayoutResponse(Quote quote) {
    QuoteLocationLayout layout = quote.getLocationLayout();
    if (layout == null) {
      return new LocationLayoutResponse(0, false, false, null);
    }

    return new LocationLayoutResponse(
        layout.expectedLocationCount(),
        layout.captureRiskZone(),
        layout.captureGeoreference(),
        layout.notes()
    );
  }

  public QuoteStateResponse toStateResponse(QuoteFinalStatusView view) {
    return new QuoteStateResponse(
        view.folio(),
        view.status().name(),
        view.netPremium(),
        view.expenseAmount(),
        view.taxAmount(),
        view.totalPremium(),
        view.alerts()
    );
  }

  public QuoteListItemResponse toQuoteListItemResponse(ListQuotesUseCase.QuoteListItem item) {
    return new QuoteListItemResponse(
        item.folio(),
        item.customerName(),
        item.totalInsuredValue(),
        item.totalLocations(),
        item.status(),
        item.createdAt(),
        item.totalPremium()
    );
  }
}
