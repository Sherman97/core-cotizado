package com.cotizador.danos.e2e;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuoteApiE2ETest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldCreateFolioWithIdempotencyAndRetrieveGeneralInfo() throws Exception {
    MvcResult firstCreateResult = mockMvc.perform(post("/v1/folios")
            .header("Idempotency-Key", "e2e-flow-1"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.numeroFolio").isString())
        .andReturn();
    String firstFolio = extractFolio(firstCreateResult);

    MvcResult replayCreateResult = mockMvc.perform(post("/v1/folios")
            .header("Idempotency-Key", "e2e-flow-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.numeroFolio").value(firstFolio))
        .andReturn();
    String replayFolio = extractFolio(replayCreateResult);

    mockMvc.perform(get("/v1/quotes/{folio}/general-info", replayFolio))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.numeroFolio").value(replayFolio))
        .andExpect(jsonPath("$.data.estadoCotizacion").value("DRAFT"));
  }

  @Test
  void shouldCompleteLocationsFlowThroughRestApi() throws Exception {
    String folio = createFolio();

    mockMvc.perform(put("/v1/quotes/{folio}/locations/layout", folio)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "expectedLocationCount": 2,
                  "captureRiskZone": false,
                  "captureGeoreference": false,
                  "notes": "Layout E2E"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.expectedLocationCount").value(2));

    mockMvc.perform(put("/v1/quotes/{folio}/locations", folio)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "locations": [
                    {
                      "locationIndex": 1,
                      "locationName": "Matriz Centro",
                      "city": "Bogota",
                      "department": "Cundinamarca",
                      "address": "Calle 100 #10-20",
                      "postalCode": "110111",
                      "constructionType": "CONCRETE",
                      "constructionLevel": 2,
                      "constructionYear": 2018,
                      "occupancyType": "OFFICE",
                      "fireKey": "F1",
                      "insuredValue": 1500000
                    },
                    {
                      "locationIndex": 2,
                      "locationName": "Sucursal Norte",
                      "city": "Bogota",
                      "department": "Cundinamarca",
                      "address": null,
                      "postalCode": null,
                      "constructionType": "CONCRETE",
                      "constructionLevel": 2,
                      "constructionYear": 2016,
                      "occupancyType": "OFFICE",
                      "fireKey": "F1",
                      "insuredValue": 900000
                    }
                  ]
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(2));

    mockMvc.perform(get("/v1/quotes/{folio}/locations/summary", folio))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.totalUbicaciones").value(2))
        .andExpect(jsonPath("$.data.ubicacionesCompletas").value(1))
        .andExpect(jsonPath("$.data.ubicacionesIncompletas").value(1));

    mockMvc.perform(get("/v1/quotes/{folio}/locations", folio))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.length()").value(2));
  }

  @Test
  void shouldCalculateAndExposeFinalStateWithAlertsForNonCalculableLocation() throws Exception {
    String folio = createFolio();

    mockMvc.perform(put("/v1/quotes/{folio}/general-info", folio)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "productCode": "DANOS",
                  "customerName": "Comercial Andina",
                  "currency": "COP",
                  "riskClassification": "LOW",
                  "businessType": "RETAIL",
                  "observations": "Flujo E2E calculo"
                }
                """))
        .andExpect(status().isOk());

    mockMvc.perform(put("/v1/quotes/{folio}/locations", folio)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "locations": [
                    {
                      "locationIndex": 1,
                      "locationName": "Matriz Centro",
                      "city": "Bogota",
                      "department": "Cundinamarca",
                      "address": "Calle 100 #10-20",
                      "postalCode": "110111",
                      "constructionType": "CONCRETE",
                      "constructionLevel": 2,
                      "constructionYear": 2018,
                      "occupancyType": "OFFICE",
                      "fireKey": "F1",
                      "insuredValue": 1500000
                    },
                    {
                      "locationIndex": 2,
                      "locationName": "Bodega Sur",
                      "city": "Bogota",
                      "department": "Cundinamarca",
                      "address": "Carrera 10 #22-50",
                      "postalCode": "110911",
                      "constructionType": "CONCRETE",
                      "constructionLevel": 2,
                      "constructionYear": 2016,
                      "occupancyType": null,
                      "insuredValue": 800000
                    }
                  ]
                }
                """))
        .andExpect(status().isOk());

    mockMvc.perform(put("/v1/quotes/{folio}/coverage-options", folio)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "coverages": [
                    {
                      "coverageCode": "FIRE",
                      "coverageName": "Incendio",
                      "insuredLimit": 1000000,
                      "deductibleType": "FIXED",
                      "deductibleValue": 50000,
                      "selected": true
                    }
                  ]
                }
                """))
        .andExpect(status().isOk());

    mockMvc.perform(post("/v1/quotes/{folio}/calculate", folio))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.primaNeta").isNumber())
        .andExpect(jsonPath("$.data.primaNeta").value(org.hamcrest.Matchers.greaterThan(0.0)))
        .andExpect(jsonPath("$.data.alertas").isArray())
        .andExpect(jsonPath("$.data.alertas[0]").value(org.hamcrest.Matchers.containsString("excluded: missing occupancy.fireKey")));

    mockMvc.perform(get("/v1/quotes/{folio}/state", folio))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.estadoCotizacion").value("CALCULATED"))
        .andExpect(jsonPath("$.data.primaNeta").value(org.hamcrest.Matchers.greaterThan(0.0)))
        .andExpect(jsonPath("$.data.alertas[0]").value(org.hamcrest.Matchers.containsString("excluded: missing occupancy.fireKey")));
  }

  private String createFolio() throws Exception {
    MvcResult createResult = mockMvc.perform(post("/v1/folios"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.numeroFolio").isString())
        .andReturn();
    return extractFolio(createResult);
  }

  private String extractFolio(MvcResult result) throws Exception {
    JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
    return root.path("data").path("numeroFolio").asText();
  }

}
