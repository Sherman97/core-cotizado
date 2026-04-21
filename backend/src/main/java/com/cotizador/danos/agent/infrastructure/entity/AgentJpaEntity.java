package com.cotizador.danos.agent.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agents_catalog")
public class AgentJpaEntity {

  @Id
  @Column(name = "agent_code", nullable = false, length = 32)
  private String agentCode;

  @Column(name = "agent_name", nullable = false, length = 255)
  private String agentName;

  @Column(name = "channel", nullable = false, length = 64)
  private String channel;

  @Column(name = "branch", nullable = false, length = 120)
  private String branch;

  @Column(name = "active", nullable = false)
  private boolean active;

  public String getAgentCode() {
    return agentCode;
  }

  public String getAgentName() {
    return agentName;
  }

  public String getChannel() {
    return channel;
  }

  public String getBranch() {
    return branch;
  }

  public boolean isActive() {
    return active;
  }
}
