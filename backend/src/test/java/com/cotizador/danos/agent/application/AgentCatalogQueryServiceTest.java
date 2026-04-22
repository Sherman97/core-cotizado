package com.cotizador.danos.agent.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.agent.application.AgentCatalogQueryService.AgentItem;
import com.cotizador.danos.agent.infrastructure.entity.AgentJpaEntity;
import com.cotizador.danos.agent.infrastructure.repository.SpringDataAgentJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AgentCatalogQueryServiceTest {

  @Mock
  private SpringDataAgentJpaRepository repository;

  private AgentCatalogQueryService service;

  @BeforeEach
  void setUp() {
    service = new AgentCatalogQueryService(repository);
  }

  @Test
  void findAgents_withCode_activeFiltersCorrectly() {
    AgentJpaEntity entity = new AgentJpaEntity();
    ReflectionTestUtils.setField(entity, "agentCode", "A1");
    ReflectionTestUtils.setField(entity, "active", true);

    when(repository.findById("A1")).thenReturn(Optional.of(entity));

    // active = null matches anything
    List<AgentItem> res1 = service.findAgents(null, "A1");
    assertThat(res1).hasSize(1);

    // active = true matches
    List<AgentItem> res2 = service.findAgents(true, "A1");
    assertThat(res2).hasSize(1);

    // active = false does not match (agent is true)
    List<AgentItem> res3 = service.findAgents(false, "A1");
    assertThat(res3).isEmpty();
  }

  @Test
  void findAgents_withoutCode_usesActiveFilter() {
    AgentJpaEntity entity = new AgentJpaEntity();
    ReflectionTestUtils.setField(entity, "agentCode", "A2");
    ReflectionTestUtils.setField(entity, "active", false);

    when(repository.findAllByOrderByAgentNameAsc()).thenReturn(List.of(entity));
    when(repository.findByActiveOrderByAgentNameAsc(false)).thenReturn(List.of(entity));

    List<AgentItem> res1 = service.findAgents(null, null);
    assertThat(res1).hasSize(1);

    List<AgentItem> res2 = service.findAgents(false, "");
    assertThat(res2).hasSize(1);
  }

  @Test
  void getByCode_throwsExceptionWhenNotFound() {
    when(repository.findById("X99")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getByCode("X99"))
        .isInstanceOf(AgentNotFoundException.class);
  }

  @Test
  void findActiveByCode_handlesNullOrBlankCode() {
    assertThat(service.findActiveByCode(null)).isEmpty();
    assertThat(service.findActiveByCode("")).isEmpty();
    assertThat(service.findActiveByCode("  ")).isEmpty();
  }

  @Test
  void findActiveByCode_returnsWhenFound() {
    AgentJpaEntity entity = new AgentJpaEntity();
    ReflectionTestUtils.setField(entity, "agentCode", "A3");
    ReflectionTestUtils.setField(entity, "active", true);
    when(repository.findByAgentCodeAndActiveTrue("A3")).thenReturn(Optional.of(entity));

    Optional<AgentItem> res = service.findActiveByCode("A3");
    assertThat(res).isPresent();
    assertThat(res.get().code()).isEqualTo("A3");
  }
}
