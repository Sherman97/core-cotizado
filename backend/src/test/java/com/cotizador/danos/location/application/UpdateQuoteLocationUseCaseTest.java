package com.cotizador.danos.location.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.location.domain.LocationNotFoundException;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.LocationValidationStatus;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.location.domain.QuoteLocationUpdatePatch;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateQuoteLocationUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final long LOCATION_ID = 1L;
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final Instant MODIFIED_AT = Instant.parse("2026-04-20T11:30:00Z");

  @Mock
  private LocationRepository locationRepository;
  @Mock
  private QuoteRepository quoteRepository;

  @Test
  void shouldUpdateLocationAndRecalculateValidationStatus() {
    QuoteLocation existingLocation = QuoteLocation.create(
        LOCATION_ID,
        FOLIO,
        incompletePatch()
    );
    QuoteLocationUpdatePatch patch = completeMissingDataPatch();
    UpdateQuoteLocationUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(Quote.createNew(FOLIO, CREATED_AT)));
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(locationRepository.findByQuoteFolioAndId(FOLIO, LOCATION_ID)).thenReturn(Optional.of(existingLocation));
    when(locationRepository.save(any(QuoteLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteLocation updatedLocation = useCase.handle(FOLIO, LOCATION_ID, patch);

    assertThat(updatedLocation.getId()).isEqualTo(LOCATION_ID);
    assertThat(updatedLocation.getQuoteFolio()).isEqualTo(FOLIO);
    assertThat(updatedLocation.getValidationStatus()).isEqualTo(LocationValidationStatus.COMPLETE);
    assertThat(updatedLocation.getAlerts()).isEmpty();
    ArgumentCaptor<Quote> savedQuoteCaptor = ArgumentCaptor.forClass(Quote.class);
    verify(quoteRepository).save(savedQuoteCaptor.capture());
    assertThat(savedQuoteCaptor.getValue().getVersion()).isEqualTo(2);
    assertThat(savedQuoteCaptor.getValue().getModifiedAt()).isEqualTo(MODIFIED_AT);
    verify(locationRepository).findByQuoteFolioAndId(FOLIO, LOCATION_ID);
    verify(locationRepository).save(updatedLocation);
  }

  @Test
  void shouldUpdateOnlyProvidedFields() {
    QuoteLocation existingLocation = QuoteLocation.create(
        LOCATION_ID,
        FOLIO,
        completePatch()
    );
    QuoteLocationUpdatePatch patch = cityOnlyPatch();
    UpdateQuoteLocationUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(Quote.createNew(FOLIO, CREATED_AT)));
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(locationRepository.findByQuoteFolioAndId(FOLIO, LOCATION_ID)).thenReturn(Optional.of(existingLocation));
    when(locationRepository.save(any(QuoteLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteLocation updatedLocation = useCase.handle(FOLIO, LOCATION_ID, patch);

    assertThat(updatedLocation.getQuoteFolio()).isEqualTo(FOLIO);
    assertThat(updatedLocation.getLocationName()).isEqualTo("Matriz Centro");
    assertThat(updatedLocation.getCity()).isEqualTo("Medellin");
    assertThat(updatedLocation.getAddress()).isEqualTo("Calle 100 #10-20");
    verify(locationRepository).save(updatedLocation);
  }

  @Test
  void shouldThrowControlledErrorWhenLocationDoesNotExist() {
    QuoteLocationUpdatePatch patch = cityOnlyPatch();
    UpdateQuoteLocationUseCase useCase = newUseCase();

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(Quote.createNew(FOLIO, CREATED_AT)));
    when(locationRepository.findByQuoteFolioAndId(FOLIO, LOCATION_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO, LOCATION_ID, patch))
        .isInstanceOf(LocationNotFoundException.class)
        .hasMessageContaining(String.valueOf(LOCATION_ID));

    verify(locationRepository).findByQuoteFolioAndId(FOLIO, LOCATION_ID);
  }

  private UpdateQuoteLocationUseCase newUseCase() {
    return new UpdateQuoteLocationUseCase(
        locationRepository,
        quoteRepository,
        Clock.fixed(MODIFIED_AT, ZoneOffset.UTC)
    );
  }

  private QuoteLocationPatch completePatch() {
    return new QuoteLocationPatch(
        "Matriz Centro",
        "Bogota",
        "Cundinamarca",
        "Calle 100 #10-20",
        "110111",
        "CONCRETE",
        "OFFICE",
        1500000
    );
  }

  private QuoteLocationPatch incompletePatch() {
    return new QuoteLocationPatch(
        "Sucursal Norte",
        "Bogota",
        "Cundinamarca",
        null,
        null,
        "CONCRETE",
        "OFFICE",
        900000
    );
  }

  private QuoteLocationUpdatePatch completeMissingDataPatch() {
    return new QuoteLocationUpdatePatch(
        null,
        null,
        null,
        "Calle 80 #15-10",
        "110221",
        null,
        null,
        null
    );
  }

  private QuoteLocationUpdatePatch cityOnlyPatch() {
    return new QuoteLocationUpdatePatch(
        null,
        "Medellin",
        null,
        null,
        null,
        null,
        null,
        null
    );
  }
}
