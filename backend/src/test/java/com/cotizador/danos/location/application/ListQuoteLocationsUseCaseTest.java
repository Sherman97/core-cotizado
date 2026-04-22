package com.cotizador.danos.location.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
class ListQuoteLocationsUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private LocationRepository locationRepository;

  @Test
  void shouldReturnAllLocationsForQuoteWithValidationStatusAndAlerts() {
    QuoteLocation completeLocation = completeLocation();
    QuoteLocation incompleteLocation = incompleteLocation();
    ListQuoteLocationsUseCase useCase = new ListQuoteLocationsUseCase(quoteRepository, locationRepository);

    when(quoteRepository.findByFolio(FOLIO))
        .thenReturn(Optional.of(Quote.createNew(FOLIO, CREATED_AT)));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(completeLocation, incompleteLocation));

    List<QuoteLocation> locations = useCase.handle(FOLIO);

    assertThat(locations).hasSize(2);
    assertThat(locations.get(0).getValidationStatus()).isEqualTo(LocationValidationStatus.COMPLETE);
    assertThat(locations.get(0).getAlerts()).isEmpty();
    assertThat(locations.get(1).getValidationStatus()).isEqualTo(LocationValidationStatus.INCOMPLETE);
    assertThat(locations.get(1).getAlerts()).contains("Location has incomplete required data");
    verify(quoteRepository).findByFolio(FOLIO);
    verify(locationRepository).findByQuoteFolio(FOLIO);
  }

  @Test
  void shouldThrowControlledErrorWhenQuoteDoesNotExist() {
    ListQuoteLocationsUseCase useCase = new ListQuoteLocationsUseCase(quoteRepository, locationRepository);

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);

    verify(quoteRepository).findByFolio(FOLIO);
  }

  private QuoteLocation completeLocation() {
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
}
