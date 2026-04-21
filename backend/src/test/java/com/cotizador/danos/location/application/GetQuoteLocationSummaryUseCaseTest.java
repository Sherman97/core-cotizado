package com.cotizador.danos.location.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.cotizador.danos.calculation.domain.LocationCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
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
class GetQuoteLocationSummaryUseCaseTest {

  private static final String FOLIO = "FOL-001";

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private LocationRepository locationRepository;

  @Mock
  private QuoteCalculationResultRepository quoteCalculationResultRepository;

  @Test
  void shouldBuildSummaryUsingStoredCalculationResult() {
    GetQuoteLocationSummaryUseCase useCase = new GetQuoteLocationSummaryUseCase(
        quoteRepository,
        locationRepository,
        quoteCalculationResultRepository
    );
    Quote quote = Quote.createNew(FOLIO, Instant.parse("2026-04-21T10:00:00Z"));
    QuoteLocation complete = QuoteLocation.create(1L, FOLIO, patch("Site A", "Calle 1", "110111"));
    QuoteLocation incomplete = QuoteLocation.create(2L, FOLIO, patch("Site B", null, "110112"));
    QuoteLocation invalid = QuoteLocation.invalid(3L, FOLIO, "Site C", List.of("Missing data"));
    QuoteCalculationResult calculation = QuoteCalculationResult.fromLocationResults(
        List.of(
            new LocationCalculationResult(1L, "Site A", LocationValidationStatus.COMPLETE, 1250.0, List.of()),
            new LocationCalculationResult(2L, "Site B", LocationValidationStatus.INCOMPLETE, 0.0, List.of("Skipped"))
        ),
        List.of("One location skipped")
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(complete, incomplete, invalid));
    when(quoteCalculationResultRepository.findByQuoteFolio(FOLIO)).thenReturn(Optional.of(calculation));

    GetQuoteLocationSummaryUseCase.Result result = useCase.handle(FOLIO);

    assertThat(result.totalLocations()).isEqualTo(3);
    assertThat(result.completeLocations()).isEqualTo(1);
    assertThat(result.incompleteLocations()).isEqualTo(1);
    assertThat(result.invalidLocations()).isEqualTo(1);
    assertThat(result.calculatedLocations()).isEqualTo(1);
    assertThat(result.calculatedPremium()).isEqualTo(1250.0);
    assertThat(result.alerts()).containsExactly("One location skipped");
  }

  @Test
  void shouldFallbackToEmptyCalculationResultWhenNoneExists() {
    GetQuoteLocationSummaryUseCase useCase = new GetQuoteLocationSummaryUseCase(
        quoteRepository,
        locationRepository,
        quoteCalculationResultRepository
    );
    Quote quote = Quote.createNew(FOLIO, Instant.parse("2026-04-21T10:00:00Z"));
    QuoteLocation complete = QuoteLocation.create(1L, FOLIO, patch("Site A", "Calle 1", "110111"));

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(complete));
    when(quoteCalculationResultRepository.findByQuoteFolio(FOLIO)).thenReturn(Optional.empty());

    GetQuoteLocationSummaryUseCase.Result result = useCase.handle(FOLIO);

    assertThat(result.calculatedLocations()).isZero();
    assertThat(result.calculatedPremium()).isZero();
    assertThat(result.alerts()).isEmpty();
  }

  @Test
  void shouldThrowWhenQuoteDoesNotExist() {
    GetQuoteLocationSummaryUseCase useCase = new GetQuoteLocationSummaryUseCase(
        quoteRepository,
        locationRepository,
        quoteCalculationResultRepository
    );
    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);
  }

  private static QuoteLocationPatch patch(String name, String address, String postalCode) {
    return new QuoteLocationPatch(
        name,
        "Bogota",
        "Cundinamarca",
        address,
        postalCode,
        "CONCRETE",
        "OFFICE",
        1_000_000
    );
  }
}
