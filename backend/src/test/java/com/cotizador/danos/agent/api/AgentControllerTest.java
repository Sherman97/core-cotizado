package com.cotizador.danos.agent.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AgentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    jdbcTemplate.update("DELETE FROM agents_catalog");
    jdbcTemplate.update(
        "INSERT INTO agents_catalog (agent_code, agent_name, channel, branch, active) VALUES (?, ?, ?, ?, ?)",
        "AGT-001", "Juan Perez", "BROKER", "Bogota Centro", true
    );
    jdbcTemplate.update(
        "INSERT INTO agents_catalog (agent_code, agent_name, channel, branch, active) VALUES (?, ?, ?, ?, ?)",
        "AGT-002", "Maria Gomez", "DIRECT", "Medellin Norte", false
    );
  }

  @Test
  void shouldListAgentsFromCatalog() throws Exception {
    mockMvc.perform(get("/v1/agents"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].agentCode").value("AGT-001"))
        .andExpect(jsonPath("$.data[1].agentCode").value("AGT-002"));
  }

  @Test
  void shouldFilterAgentsByActiveFlag() throws Exception {
    mockMvc.perform(get("/v1/agents").param("active", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(1))
        .andExpect(jsonPath("$.data[0].agentCode").value("AGT-001"));
  }

  @Test
  void shouldGetAgentByCode() throws Exception {
    mockMvc.perform(get("/v1/agents/{code}", "AGT-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.agentCode").value("AGT-001"))
        .andExpect(jsonPath("$.data.agentName").value("Juan Perez"));
  }
}
