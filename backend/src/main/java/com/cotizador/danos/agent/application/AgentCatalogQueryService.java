package com.cotizador.danos.agent.application;

import com.cotizador.danos.agent.infrastructure.entity.AgentJpaEntity;
import com.cotizador.danos.agent.infrastructure.repository.SpringDataAgentJpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentCatalogQueryService {

  private final SpringDataAgentJpaRepository repository;

  public AgentCatalogQueryService(SpringDataAgentJpaRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public List<AgentItem> findAgents(Boolean active, String code) {
    if (code != null && !code.isBlank()) {
      return repository.findById(code)
          .filter(agent -> active == null || agent.isActive() == active)
          .map(this::toItem)
          .map(List::of)
          .orElse(List.of());
    }

    List<AgentJpaEntity> entities = active == null
        ? repository.findAllByOrderByAgentNameAsc()
        : repository.findByActiveOrderByAgentNameAsc(active);
    return entities.stream().map(this::toItem).toList();
  }

  @Transactional(readOnly = true)
  public AgentItem getByCode(String code) {
    return repository.findById(code)
        .map(this::toItem)
        .orElseThrow(() -> new AgentNotFoundException(code));
  }

  @Transactional(readOnly = true)
  public Optional<AgentItem> findActiveByCode(String code) {
    if (code == null || code.isBlank()) {
      return Optional.empty();
    }
    return repository.findByAgentCodeAndActiveTrue(code).map(this::toItem);
  }

  private AgentItem toItem(AgentJpaEntity entity) {
    return new AgentItem(
        entity.getAgentCode(),
        entity.getAgentName(),
        entity.getChannel(),
        entity.getBranch(),
        entity.isActive()
    );
  }

  public record AgentItem(
      String code,
      String name,
      String channel,
      String branch,
      boolean active
  ) {
  }
}
