package com.cotizador.danos.catalog.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.cotizador.danos.catalog.domain.CalculationParameterData;
import com.cotizador.danos.catalog.domain.CalculationParameterRepository;
import com.cotizador.danos.catalog.domain.ConstructionRatingData;
import com.cotizador.danos.catalog.domain.ConstructionRatingRepository;
import com.cotizador.danos.catalog.domain.CoverageBaseRateData;
import com.cotizador.danos.catalog.domain.CoverageFactorData;
import com.cotizador.danos.catalog.domain.CoverageFactorRepository;
import com.cotizador.danos.catalog.domain.CoverageRateRepository;
import com.cotizador.danos.catalog.domain.OccupancyRatingData;
import com.cotizador.danos.catalog.domain.OccupancyRatingRepository;
import com.cotizador.danos.catalog.domain.ZoneRatingData;
import com.cotizador.danos.catalog.domain.ZoneRatingRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CatalogQueryServicesTest {

  @Mock
  private ZoneRatingRepository zoneRatingRepository;

  @Mock
  private OccupancyRatingRepository occupancyRatingRepository;

  @Mock
  private ConstructionRatingRepository constructionRatingRepository;

  @Mock
  private CoverageRateRepository coverageRateRepository;

  @Mock
  private CoverageFactorRepository coverageFactorRepository;

  @Mock
  private CalculationParameterRepository calculationParameterRepository;

  @Test
  void shouldQueryZoneFactorByPostalCode() {
    ZoneFactorQueryService service = new ZoneFactorQueryService(zoneRatingRepository);
    ZoneRatingData expected = new ZoneRatingData("110111", "LOW", "Zona baja", 1.0);
    when(zoneRatingRepository.findActiveByPostalCode("110111")).thenReturn(Optional.of(expected));

    Optional<ZoneRatingData> result = service.findByPostalCode("110111");

    assertThat(result).contains(expected);
  }

  @Test
  void shouldQueryOccupancyFactorByOccupancyCode() {
    OccupancyFactorQueryService service = new OccupancyFactorQueryService(occupancyRatingRepository);
    OccupancyRatingData expected = new OccupancyRatingData("OFFICE", "GIR-OFF-01", 1.0);
    when(occupancyRatingRepository.findActiveByOccupancyCode("OFFICE")).thenReturn(Optional.of(expected));

    Optional<OccupancyRatingData> result = service.findByOccupancyCode("OFFICE");

    assertThat(result).contains(expected);
  }

  @Test
  void shouldQueryConstructionFactorByConstructionType() {
    ConstructionFactorQueryService service = new ConstructionFactorQueryService(constructionRatingRepository);
    ConstructionRatingData expected = new ConstructionRatingData("CONCRETE", 1.0);
    when(constructionRatingRepository.findActiveByConstructionType("CONCRETE")).thenReturn(Optional.of(expected));

    Optional<ConstructionRatingData> result = service.findByConstructionType("CONCRETE");

    assertThat(result).contains(expected);
  }

  @Test
  void shouldQueryCoverageRatesAndFactorsByProductAndCoverages() {
    CoverageRatingQueryService service = new CoverageRatingQueryService(coverageRateRepository, coverageFactorRepository);
    Set<String> coverageCodes = Set.of("FIRE", "EARTHQUAKE");
    List<CoverageBaseRateData> rates = List.of(
        new CoverageBaseRateData("DANOS", "FIRE", 0.0015),
        new CoverageBaseRateData("DANOS", "EARTHQUAKE", 0.00045)
    );
    List<CoverageFactorData> factors = List.of(
        new CoverageFactorData("DANOS", "FIRE", 1.0),
        new CoverageFactorData("DANOS", "EARTHQUAKE", 1.08)
    );
    when(coverageRateRepository.findActiveByProductAndCoverageCodes("DANOS", coverageCodes)).thenReturn(rates);
    when(coverageFactorRepository.findActiveByProductAndCoverageCodes("DANOS", coverageCodes)).thenReturn(factors);

    CoverageRatingQueryService.CoverageRatingSnapshot snapshot = service.findByProductAndCoverages("DANOS", coverageCodes);

    assertThat(snapshot.baseRates()).containsExactlyElementsOf(rates);
    assertThat(snapshot.factors()).containsExactlyElementsOf(factors);
  }

  @Test
  void shouldQueryCalculationParametersByCodeAndList() {
    CalculationParameterQueryService service = new CalculationParameterQueryService(calculationParameterRepository);
    CalculationParameterData expenseRate = new CalculationParameterData("EXPENSE_RATE", 0.10, "Expense rate");
    CalculationParameterData taxRate = new CalculationParameterData("TAX_RATE", 0.16, "Tax rate");
    when(calculationParameterRepository.findActiveByCode("EXPENSE_RATE")).thenReturn(Optional.of(expenseRate));
    when(calculationParameterRepository.findActiveByCodes(Set.of("EXPENSE_RATE", "TAX_RATE")))
        .thenReturn(List.of(expenseRate, taxRate));

    Optional<CalculationParameterData> one = service.findByCode("EXPENSE_RATE");
    List<CalculationParameterData> list = service.findByCodes(Set.of("EXPENSE_RATE", "TAX_RATE"));

    assertThat(one).contains(expenseRate);
    assertThat(list).containsExactly(expenseRate, taxRate);
  }
}
