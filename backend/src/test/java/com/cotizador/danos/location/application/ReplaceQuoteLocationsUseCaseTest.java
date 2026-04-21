package com.cotizador.danos.location.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReplaceQuoteLocationsUseCaseTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final Instant MODIFIED_AT = Instant.parse("2026-04-20T11:30:00Z");

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private LocationRepository locationRepository;

  @Test
  void shouldReplaceLocationsAndIncrementBusinessVersion() {
    ReplaceQuoteLocationsUseCase useCase = new ReplaceQuoteLocationsUseCase(
        quoteRepository,
        locationRepository,
        Clock.fixed(MODIFIED_AT, ZoneOffset.UTC)
    );
    Quote existingQuote = Quote.createNew(FOLIO, CREATED_AT);
    List<QuoteLocationPatch> request = List.of(
        new QuoteLocationPatch("Matriz", "Bogota", "Cundinamarca", "Calle 100", "110111", "CONCRETE", "OFFICE", 1000000)
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(existingQuote));
    when(locationRepository.nextId()).thenReturn(1L);
    when(locationRepository.save(any(QuoteLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    List<QuoteLocation> savedLocations = useCase.handle(FOLIO, request);

    assertThat(savedLocations).hasSize(1);
    ArgumentCaptor<Quote> savedQuoteCaptor = ArgumentCaptor.forClass(Quote.class);
    verify(quoteRepository).save(savedQuoteCaptor.capture());
    assertThat(savedQuoteCaptor.getValue().getVersion()).isEqualTo(2);
    assertThat(savedQuoteCaptor.getValue().getModifiedAt()).isEqualTo(MODIFIED_AT);
  }
}
