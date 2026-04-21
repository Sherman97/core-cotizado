package com.cotizador.danos.agent.application;

public class AgentNotFoundException extends RuntimeException {

  public AgentNotFoundException(String code) {
    super("Agent not found: " + code);
  }
}
