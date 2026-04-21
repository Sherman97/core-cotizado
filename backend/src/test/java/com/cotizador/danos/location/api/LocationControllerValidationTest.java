package com.cotizador.danos.location.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cotizador.danos.calculation.api.CalculationApiMapper;
import com.cotizador.danos.calculation.application.GetQuoteLocationResultsUseCase;
import com.cotizador.danos.location.application.GetQuoteLocationSummaryUseCase;
import com.cotizador.danos.location.application.ListQuoteLocationsUseCase;
import com.cotizador.danos.location.application.ReplaceQuoteLocationsUseCase;
import com.cotizador.danos.location.application.UpdateQuoteLocationUseCase;
import com.cotizador.danos.shared.exception.ApiExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LocationController.class)
@Import({LocationApiMapper.class, CalculationApiMapper.class, ApiExceptionHandler.class})
class LocationControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ReplaceQuoteLocationsUseCase replaceQuoteLocationsUseCase;

  @MockBean
  private ListQuoteLocationsUseCase listQuoteLocationsUseCase;

  @MockBean
  private UpdateQuoteLocationUseCase updateQuoteLocationUseCase;

  @MockBean
  private GetQuoteLocationSummaryUseCase getQuoteLocationSummaryUseCase;

  @MockBean
  private GetQuoteLocationResultsUseCase getQuoteLocationResultsUseCase;

  @Test
  void shouldReturnBadRequestWhenLocationNameIsBlank() throws Exception {
    when(replaceQuoteLocationsUseCase.handle(anyString(), any())).thenReturn(List.of());

    mockMvc.perform(put("/v1/quotes/FOL-00000001/locations")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "locations": [
                    {
                      "locationName": " ",
                      "city": "Bogota",
                      "department": "Cundinamarca",
                      "address": "Calle 100 #10-20",
                      "postalCode": "110111",
                      "constructionType": "CONCRETO",
                      "occupancyType": "OFICINA",
                      "insuredValue": 1500000
                    }
                  ]
                }
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Validation error"))
        .andExpect(jsonPath("$.validationErrors[0].field").value("locations[0].locationName"));
  }
}
