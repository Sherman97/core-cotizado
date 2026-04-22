package com.cotizador.danos.calculation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cotizador.danos.calculation.domain.PremiumCalculator;
import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.coverage.domain.QuoteCoveragePatch;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteNotFoundException;
import com.cotizador.danos.quote.domain.QuoteRepository;
import com.cotizador.danos.quote.domain.QuoteStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateQuoteUseCaseTest {

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

  @Test
  void shouldCalculateOnlyValidLocationsAndUpdateQuoteStatus() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    quote = quote.updateGeneralData(new com.cotizador.danos.quote.domain.QuoteGeneralDataPatch(quote.getProductCode(), quote.getCustomerName(), quote.getCurrency(), quote.getObservations(), "AGT01", "Agent", "LOW", "RETAIL"), quote.getCreatedAt());
    QuoteLocation validLocation = validLocation();
    QuoteLocation incompleteLocation = incompleteLocation();
    List<QuoteCoverageSelection> coverages = configuredCoverages();
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(validLocation, incompleteLocation));
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(coverages);
    when(premiumCalculator.calculate(validLocation, coverages)).thenReturn(1000.0);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteCalculationResult result = useCase.handle(FOLIO);

    assertThat(result.getNetPremium()).isEqualTo(1000.0);
    assertThat(result.getAlerts()).contains("Location Sucursal Norte skipped due to incomplete data");
    ArgumentCaptor<Quote> savedQuoteCaptor = ArgumentCaptor.forClass(Quote.class);
    verify(quoteRepository).save(savedQuoteCaptor.capture());
    assertThat(savedQuoteCaptor.getValue().getStatus()).isEqualTo(QuoteStatus.CALCULATED);
    verify(quoteRepository).findByFolio(FOLIO);
    verify(locationRepository).findByQuoteFolio(FOLIO);
    verify(quoteCoverageRepository).findByQuoteFolio(FOLIO);
  }

  @Test
  void shouldExcludeInvalidLocationsAndRegisterExclusionReason() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    quote = quote.updateGeneralData(new com.cotizador.danos.quote.domain.QuoteGeneralDataPatch(quote.getProductCode(), quote.getCustomerName(), quote.getCurrency(), quote.getObservations(), "AGT01", "Agent", "LOW", "RETAIL"), quote.getCreatedAt());
    QuoteLocation validLocation = validLocation();
    QuoteLocation invalidLocation = invalidLocation();
    List<QuoteCoverageSelection> coverages = configuredCoverages();
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(validLocation, invalidLocation));
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(coverages);
    when(premiumCalculator.calculate(validLocation, coverages)).thenReturn(1000.0);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteCalculationResult result = useCase.handle(FOLIO);

    assertThat(result.getNetPremium()).isEqualTo(1000.0);
    assertThat(result.getLocations()).extracting(location -> location.locationName())
        .containsExactly("Matriz Centro");
    assertThat(result.getAlerts()).contains("Location " + INVALID_LOCATION_NAME + " excluded: " + EXCLUSION_REASON);
    verify(premiumCalculator).calculate(validLocation, coverages);
    verify(premiumCalculator, never()).calculate(invalidLocation, coverages);
  }

  @Test
  void shouldGeneratePreValidationAlertAndSkipCalculationWhenNoCoveragesConfigured() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    quote = quote.updateGeneralData(new com.cotizador.danos.quote.domain.QuoteGeneralDataPatch(quote.getProductCode(), quote.getCustomerName(), quote.getCurrency(), quote.getObservations(), "AGT01", "Agent", "LOW", "RETAIL"), quote.getCreatedAt());
    QuoteLocation validLocation = validLocation();
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(validLocation));
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of());
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteCalculationResult result = useCase.handle(FOLIO);

    assertThat(result.getNetPremium()).isZero();
    assertThat(result.getAlerts()).contains(
        "No coverage options configured for calculation",
        "Location Matriz Centro excluded: missing tariffable guarantees"
    );
    verify(premiumCalculator, never()).calculate(any(), any());
  }

  @Test
  void shouldExcludeLocationWhenGiroClaveIncendioIsMissing() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    quote = quote.updateGeneralData(new com.cotizador.danos.quote.domain.QuoteGeneralDataPatch(quote.getProductCode(), quote.getCustomerName(), quote.getCurrency(), quote.getObservations(), "AGT01", "Agent", "LOW", "RETAIL"), quote.getCreatedAt());
    QuoteLocation locationWithoutGiro = locationWithoutGiroClaveIncendio();
    List<QuoteCoverageSelection> coverages = configuredCoverages();
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(locationWithoutGiro));
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(coverages);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteCalculationResult result = useCase.handle(FOLIO);

    assertThat(result.getNetPremium()).isZero();
    assertThat(result.getAlerts()).contains("Location Bodega Sur excluded: missing occupancy.fireKey");
    verify(premiumCalculator, never()).calculate(any(), any());
  }

  @Test
  void shouldExcludeLocationWhenPostalCodeIsInvalid() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    quote = quote.updateGeneralData(new com.cotizador.danos.quote.domain.QuoteGeneralDataPatch(quote.getProductCode(), quote.getCustomerName(), quote.getCurrency(), quote.getObservations(), "AGT01", "Agent", "LOW", "RETAIL"), quote.getCreatedAt());
    QuoteLocation locationWithInvalidPostalCode = locationWithInvalidPostalCode();
    List<QuoteCoverageSelection> coverages = configuredCoverages();
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(locationWithInvalidPostalCode));
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(coverages);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteCalculationResult result = useCase.handle(FOLIO);

    assertThat(result.getNetPremium()).isZero();
    assertThat(result.getAlerts()).contains("Location Bodega Oriente excluded: invalid postal code");
    verify(premiumCalculator, never()).calculate(any(), any());
  }

  @Test
  void shouldExcludeLocationWhenConstructionDataIsMissing() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    quote = quote.updateGeneralData(new com.cotizador.danos.quote.domain.QuoteGeneralDataPatch(quote.getProductCode(), quote.getCustomerName(), quote.getCurrency(), quote.getObservations(), "AGT01", "Agent", "LOW", "RETAIL"), quote.getCreatedAt());
    QuoteLocation location = QuoteLocation.create(
        6L,
        FOLIO,
        new QuoteLocationPatch(0, "Bodega Nueva", "Bogota", "Centro", "Bogota", "Cundinamarca", "Calle 10", "110911", "CONCRETE", 0, 1800, "OFFICE", "F1", false, 750000, java.util.List.of("G1"))
    );
    List<QuoteCoverageSelection> coverages = configuredCoverages();
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of(location));
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(coverages);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteCalculationResult result = useCase.handle(FOLIO);

    assertThat(result.getNetPremium()).isZero();
    assertThat(result.getAlerts()).contains("Location Bodega Nueva excluded: missing valid construction level, missing valid construction year");
  }

  @Test
  void shouldAddPrecalculationAlertsWhenQuoteDataAndLocationsAreMissing() {
    Quote quote = Quote.createNew(FOLIO, CREATED_AT);
    List<QuoteCoverageSelection> coverages = configuredCoverages();
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(quoteRepository, locationRepository, quoteCoverageRepository, premiumCalculator);

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.of(quote));
    when(locationRepository.findByQuoteFolio(FOLIO)).thenReturn(List.of());
    when(quoteCoverageRepository.findByQuoteFolio(FOLIO)).thenReturn(coverages);
    when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

    QuoteCalculationResult result = useCase.handle(FOLIO);

    assertThat(result.getNetPremium()).isZero();
    assertThat(result.getAlerts()).contains(
        "Missing quote mandatory data (Risk Classification / Business Type)",
        "No locations configured for calculation"
    );
  }

  @Test
  void shouldThrowControlledErrorWhenQuoteDoesNotExist() {
    CalculateQuoteUseCase useCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator
    );

    when(quoteRepository.findByFolio(FOLIO)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> useCase.handle(FOLIO))
        .isInstanceOf(QuoteNotFoundException.class)
        .hasMessageContaining(FOLIO);

    verify(quoteRepository).findByFolio(FOLIO);
  }

  private QuoteLocation validLocation() {
    return QuoteLocation.create(
        1L,
        FOLIO,
        new QuoteLocationPatch(1, "Matriz Centro", "Bogota", null, null, "Cundinamarca", "Calle 100 #10-20", "110111", "CONCRETE", 2, 2018, "OFFICE", "F1", null, 1500000, java.util.List.of())
    );
  }

  private QuoteLocation incompleteLocation() {
    return QuoteLocation.create(
        2L,
        FOLIO,
        new QuoteLocationPatch(2, "Sucursal Norte", "Bogota", null, null, "Cundinamarca", null, null, "CONCRETE", 2, 2015, "OFFICE", "F1", null, 900000, java.util.List.of())
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

  private QuoteLocation locationWithoutGiroClaveIncendio() {
    return QuoteLocation.create(
        4L,
        FOLIO,
        new QuoteLocationPatch(3, "Bodega Sur", "Bogota", null, null, "Cundinamarca", "Calle 10 #2-30", "110911", "CONCRETE", 2, 2016, null, null, null, 750000, java.util.List.of())
    );
  }

  private QuoteLocation locationWithInvalidPostalCode() {
    return QuoteLocation.create(
        5L,
        FOLIO,
        new QuoteLocationPatch(4, "Bodega Oriente", "Bogota", null, null, "Cundinamarca", "Carrera 15 #45-20", "ABC12", "CONCRETE", 2, 2016, "OFFICE", "F1", null, 600000, java.util.List.of())
    );
  }

  private List<QuoteCoverageSelection> configuredCoverages() {
    return List.of(
        QuoteCoverageSelection.create(
            FOLIO,
            new QuoteCoveragePatch("FIRE", "Incendio", 1000000L, "FIXED", 50000L, true)
        )
    );
  }
}
