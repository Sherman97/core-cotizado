package com.cotizador.danos.calculation.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.calculation.domain.CalculationTraceDetail;
import com.cotizador.danos.calculation.domain.CalculationTraceRepository;
import com.cotizador.danos.calculation.domain.PremiumCalculator;
import com.cotizador.danos.coverage.domain.QuoteCoveragePatch;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateQuoteTraceabilityTest {

  private static final String FOLIO = "FOLIO-001";
  private static final Instant CREATED_AT = Instant.parse("2026-04-20T10:15:30Z");
  private static final String INVALID_LOCATION_NAME = "Bodega Temporal";
  private static final String EXCLUSION_REASON = "Location missing minimum data for calculation";

  @Mock
  private QuoteRepository quoteRepository;

  @Mock
  private LocationRepository locationRepository;

  @Mock
  private QuoteCoverageRepository quoteCoverageRepository;

  @Mock
  private PremiumCalculator premiumCalculator;

  @Mock
  private CalculationTraceRepository calculationTraceRepository;

  @Test
  void shouldPersistAppliedFactorDetailsWhenCalculationFinishes() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    QuoteLocation validLocation = validLocation();
    List<QuoteCoverageSelection> coverages = configuredCoverages();
    CalculationTraceDetail traceDetail = traceDetailFor(validLocation);
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator,
        calculationTraceRepository
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(validLocation));
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(coverages);
    when(premiumCalculator.calculate(validLocation, coverages)).thenReturn(1000.0);
    when(premiumCalculator.traceFor(validLocation, coverages)).thenReturn(List.of(traceDetail));
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    useCase.handle(FOLIO);

    verify(calculationTraceRepository).saveAll(List.of(traceDetail));
  }

  @Test
  void shouldPersistTraceOnlyForCalculatedLocations() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    QuoteLocation validLocation = validLocation();
    QuoteLocation incompleteLocation = incompleteLocation();
    QuoteLocation invalidLocation = invalidLocation();
    List<QuoteCoverageSelection> coverages = configuredCoverages();
    CalculationTraceDetail traceDetail = traceDetailFor(validLocation);
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator,
        calculationTraceRepository
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(validLocation, incompleteLocation, invalidLocation));
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(coverages);
    when(premiumCalculator.calculate(validLocation, coverages)).thenReturn(1000.0);
    when(premiumCalculator.traceFor(validLocation, coverages)).thenReturn(List.of(traceDetail));
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    useCase.handle(FOLIO);

    verify(calculationTraceRepository).saveAll(List.of(traceDetail));
    verify(premiumCalculator, never()).traceFor(incompleteLocation, coverages);
    verify(premiumCalculator, never()).traceFor(invalidLocation, coverages);
  }

  private QuoteLocation validLocation() {
    return QuoteLocation.create(
        1L,
        FOLIO,
        new QuoteLocationPatch(
            "Matriz Centro",
            "Bogota",
            "Cundinamarca",
            "Calle 100 #10-20",
            "110111",
            "CONCRETO",
            "OFICINA",
            1500000
        )
    );
  }

  private QuoteLocation incompleteLocation() {
    return QuoteLocation.create(
        2L,
        FOLIO,
        new QuoteLocationPatch(
            "Sucursal Norte",
            "Bogota",
            "Cundinamarca",
            null,
            null,
            "CONCRETO",
            "OFICINA",
            900000
        )
    );
  }

  private QuoteLocation invalidLocation() {
    return QuoteLocation.invalid(
        3L,
        FOLIO,
        INVALID_LOCATION_NAME,
        List.of(EXCLUSION_REASON)
    );
  }

  private List<QuoteCoverageSelection> configuredCoverages() {
    return List.of(
        fireCoverage()
    );
  }

  private QuoteCoverageSelection fireCoverage() {
    return QuoteCoverageSelection.create(
        FOLIO,
        new QuoteCoveragePatch("INCENDIO", "Incendio", 1000000L, "FIXED", 50000L, true)
    );
  }

  private CalculationTraceDetail traceDetailFor(QuoteLocation location) {
    return new CalculationTraceDetail(
        FOLIO,
        location.getId(),
        "BASE_RATE",
        0.015,
        1,
        Map.of("coverageCode", "INCENDIO")
    );
  }
}
