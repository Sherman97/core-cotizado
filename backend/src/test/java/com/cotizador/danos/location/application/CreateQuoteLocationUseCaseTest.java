package com.cotizador.danos.location.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateQuoteLocationUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private LocationRepository locationRepository;

  @Test
  void shouldCreateCompleteLocationForExistingQuote() {
    QuoteLocationPatch patch = completePatch();
    CreateQuoteLocationUseCase useCase = new CreateQuoteLocationUseCase(quoteRepository, locationRepository);

    when(quoteRepository.findByFolio(FOLIO))
        .thenReturn(Optional.of(Quote.createNew(FOLIO, CREATED_AT)));
    when(locationRepository.nextId()).thenReturn(1L);
    when(locationRepository.save(any(QuoteLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteLocation location = useCase.handle(FOLIO, patch);

    assertThat(location.getQuoteFolio()).isEqualTo(FOLIO);
    assertThat(location.getValidationStatus()).isEqualTo(LocationValidationStatus.COMPLETE);
    verify(quoteRepository).findByFolio(FOLIO);
    verify(locationRepository).save(location);
  }

  @Test
  void shouldCreateIncompleteLocationWithAlertWithoutBlockingFlow() {
    QuoteLocationPatch patch = incompletePatch();
    CreateQuoteLocationUseCase useCase = new CreateQuoteLocationUseCase(quoteRepository, locationRepository);

    when(quoteRepository.findByFolio(FOLIO))
        .thenReturn(Optional.of(Quote.createNew(FOLIO, CREATED_AT)));
    when(locationRepository.nextId()).thenReturn(2L);
    when(locationRepository.save(any(QuoteLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteLocation location = useCase.handle(FOLIO, patch);

    assertThat(location.getValidationStatus()).isEqualTo(LocationValidationStatus.INCOMPLETE);
    assertThat(location.getAlerts()).contains("Location has incomplete required data");
    verify(locationRepository).save(location);
  }

  @Test
  void shouldThrowControlledErrorWhenQuoteDoesNotExist() {
    QuoteLocationPatch patch = patchForMissingQuote();
    CreateQuoteLocationUseCase useCase = new CreateQuoteLocationUseCase(quoteRepository, locationRepository);

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO, patch))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);

    verify(quoteRepository).findByFolio(FOLIO);
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

  private QuoteLocationPatch patchForMissingQuote() {
    return new QuoteLocationPatch(
        "Sucursal Norte",
        "Bogota",
        "Cundinamarca",
        "Calle 80 #15-10",
        "110221",
        "CONCRETE",
        "OFFICE",
        900000
    );
  }
}
