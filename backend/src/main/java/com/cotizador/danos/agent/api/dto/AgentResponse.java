package com.cotizador.danos.agent.api.dto;

public record AgentResponse(
    String agentCode,
    String agentName,
    String channel,
    String branch,
    boolean active
) {
}
