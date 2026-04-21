package com.cotizador.danos.agent.api;

import com.cotizador.danos.agent.application.AgentCatalogQueryService;
import com.cotizador.danos.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/agents")
public class AgentController {

  private final AgentCatalogQueryService queryService;
  private final AgentApiMapper mapper;

  public AgentController(
      AgentCatalogQueryService queryService,
      AgentApiMapper mapper
  ) {
    this.queryService = queryService;
    this.mapper = mapper;
  }

  @GetMapping
  public ApiResponse<?> listAgents(
      @RequestParam(required = false) String code,
      @RequestParam(required = false) Boolean active
  ) {
    return ApiResponse.of(
        queryService.findAgents(active, code).stream()
            .map(mapper::toResponse)
            .toList()
    );
  }

  @GetMapping("/{code}")
  public ApiResponse<?> getAgentByCode(@PathVariable String code) {
    return ApiResponse.of(mapper.toResponse(queryService.getByCode(code)));
  }
}
