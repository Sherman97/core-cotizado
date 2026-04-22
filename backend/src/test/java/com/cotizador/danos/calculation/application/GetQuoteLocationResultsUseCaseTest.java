package com.cotizador.danos.calculation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.calculation.domain.LocationCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.calculation.domain.QuoteLocationResultView;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.LocationValidationStatus;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetQuoteLocationResultsUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final String INVALID_LOCATION_NAME = "Bodega Temporal";
  private static final String INVALID_LOCATION_ALERT = "Location missing minimum data for calculation";

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private QuoteCalculationResultRepository quoteCalculationResultRepository;

  @Mock
  private LocationRepository locationRepository;

  @Test
  void shouldReturnCalculatedAndNonCalculatedLocationsDifferentiated() {
    Quote calculatedQuote = Quote.createNew(FOLIO, CREATED_AT).markAsCalculated();
    QuoteLocation calculatedLocation = calculatedLocation();
    QuoteLocation incompleteLocation = incompleteLocation();
    QuoteLocation invalidLocation = invalidLocation();
    QuoteCalculationResult calculationResult = QuoteCalculationResult.fromLocationResults(
        List.of(
            new LocationCalculationResult(
                calculatedLocation.getId(),
                calculatedLocation.getLocationName(),
                LocationValidationStatus.COMPLETE,
                1000.0,
                List.of()
            ),
            new LocationCalculationResult(
                incompleteLocation.getId(),
                incompleteLocation.getLocationName(),
                LocationValidationStatus.INCOMPLETE,
                0.0,
                incompleteLocation.getAlerts()
            )
        ),
        List.of("Location Bodega Temporal excluded: Location missing minimum data for calculation")
    );
    GetQuoteLocationResultsUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(calculatedQuote));
    when(quoteCalculationResultRepository.findByQuoteFolio(FOLIO)).thenReturn(Optional.of(calculationResult));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(calculatedLocation, incompleteLocation, invalidLocation));

    List<QuoteLocationResultView> result = useCase.handle(FOLIO);

    assertThat(result).hasSize(3);
    assertThat(result).extracting(QuoteLocationResultView::locationName)
        .containsExactly("Matriz Centro", "Sucursal Norte", "Bodega Temporal");
    assertThat(result).extracting(QuoteLocationResultView::calculated)
        .containsExactly(true, false, false);
    assertThat(result).extracting(QuoteLocationResultView::validationStatus)
        .containsExactly(LocationValidationStatus.COMPLETE, LocationValidationStatus.INCOMPLETE, LocationValidationStatus.INVALID);
  }

  @Test
  void shouldThrowControlledErrorWhenQuoteDoesNotExist() {
    GetQuoteLocationResultsUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);

    verify(quoteRepository).findByFolio(FOLIO);
  }

  private QuoteLocation calculatedLocation() {
    return QuoteLocation.create(
        1L,
        FOLIO,
        new QuoteLocationPatch(0, "Matriz Centro", "Bogota", null, null, "Cundinamarca", "Calle 100 #10-20", "110111", "CONCRETE", 0, 0, "OFFICE", null, null, 1500000, java.util.List.of())
    );
  }

  private QuoteLocation incompleteLocation() {
    return QuoteLocation.create(
        2L,
        FOLIO,
        new QuoteLocationPatch(0, "Sucursal Norte", "Bogota", null, null, "Cundinamarca", null, null, "CONCRETE", 0, 0, "OFFICE", null, null, 900000, java.util.List.of())
    );
  }

  private QuoteLocation invalidLocation() {
    return QuoteLocation.invalid(
        3L,
        FOLIO,
        INVALID_LOCATION_NAME,
        List.of(INVALID_LOCATION_ALERT)
    );
  }

  private GetQuoteLocationResultsUseCase newUseCase() {
    return new GetQuoteLocationResultsUseCase(
        quoteRepository,
        quoteCalculationResultRepository,
        locationRepository
    );
  }
}
