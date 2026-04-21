package com.cotizador.danos.agent.api;

import com.cotizador.danos.agent.api.dto.AgentResponse;
import com.cotizador.danos.agent.application.AgentCatalogQueryService;
import org.springframework.stereotype.Component;

@Component
public class AgentApiMapper {

  public AgentResponse toResponse(AgentCatalogQueryService.AgentItem item) {
    return new AgentResponse(
        item.code(),
        item.name(),
        item.channel(),
        item.branch(),
        item.active()
    );
  }
}
