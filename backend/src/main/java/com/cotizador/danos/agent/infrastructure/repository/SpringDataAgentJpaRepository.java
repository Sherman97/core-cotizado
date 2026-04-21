package com.cotizador.danos.agent.infrastructure.repository;

import com.cotizador.danos.agent.infrastructure.entity.AgentJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAgentJpaRepository extends JpaRepository<AgentJpaEntity, String> {

  List<AgentJpaEntity> findAllByOrderByAgentNameAsc();

  List<AgentJpaEntity> findByActiveOrderByAgentNameAsc(boolean active);

  Optional<AgentJpaEntity> findByAgentCodeAndActiveTrue(String agentCode);
}
