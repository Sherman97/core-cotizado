package com.cotizador.danos.quote.application;

import com.cotizador.danos.agent.application.AgentCatalogQueryService;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteGeneralDataPatch;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;

public class UpdateQuoteGeneralDataUseCase {

  private final QuoteRepository quoteRepository;
  private final AgentCatalogQueryService agentCatalogQueryService;
  private final Clock clock;

  public UpdateQuoteGeneralDataUseCase(
      QuoteRepository quoteRepository,
      AgentCatalogQueryService agentCatalogQueryService,
      Clock clock
  ) {
    this.quoteRepository = quoteRepository;
    this.agentCatalogQueryService = agentCatalogQueryService;
    this.clock = clock;
  }

  public Quote handle(String folio, QuoteGeneralDataPatch patch) {
    Quote existingQuote = quoteRepository.findByFolio(folio)
        .orElseThrow(() -> new QuoteNotFoundException(folio));

    QuoteGeneralDataPatch resolvedPatch = resolveAgentPatch(patch);
    Quote updatedQuote = existingQuote.updateGeneralData(resolvedPatch, Instant.now(clock));
    return quoteRepository.save(updatedQuote);
  }

  private QuoteGeneralDataPatch resolveAgentPatch(QuoteGeneralDataPatch patch) {
    if (patch.agentCode() == null) {
      return patch;
    }
    if (patch.agentCode().isBlank()) {
      return new QuoteGeneralDataPatch(
          patch.productCode(),
          patch.customerName(),
          patch.currency(),
          patch.observations(),
          null,
          null,
          patch.riskClassification(),
          patch.businessType()
      );
    }

    var agent = agentCatalogQueryService.findActiveByCode(patch.agentCode())
        .orElseThrow(() -> new IllegalArgumentException("Agent code not found or inactive: " + patch.agentCode()));

    return new QuoteGeneralDataPatch(
        patch.productCode(),
        patch.customerName(),
        patch.currency(),
        patch.observations(),
        agent.code(),
        agent.name(),
        patch.riskClassification(),
        patch.businessType()
    );
  }
}
