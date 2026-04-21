package com.cotizador.danos.flow;

import static org.assertj.core.api.Assertions.assertThat;

import com.cotizador.danos.calculation.application.CalculateQuoteUseCase;
import com.cotizador.danos.calculation.domain.CalculationTraceDetail;
import com.cotizador.danos.calculation.domain.CalculationTraceRepository;
import com.cotizador.danos.calculation.domain.PremiumCalculator;
import com.cotizador.danos.calculation.domain.QuoteCalculationResult;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.coverage.application.ConfigureQuoteCoveragesUseCase;
import com.cotizador.danos.coverage.domain.QuoteCoveragePatch;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageSelection;
import com.cotizador.danos.location.application.ListQuoteLocationsUseCase;
import com.cotizador.danos.location.application.ReplaceQuoteLocationsUseCase;
import com.cotizador.danos.location.application.UpdateQuoteLocationUseCase;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.location.domain.LocationValidationStatus;
import com.cotizador.danos.location.domain.QuoteLocation;
import com.cotizador.danos.location.domain.QuoteLocationPatch;
import com.cotizador.danos.location.domain.QuoteLocationUpdatePatch;
import com.cotizador.danos.quote.application.CreateQuoteUseCase;
import com.cotizador.danos.quote.domain.FolioGenerator;
import com.cotizador.danos.quote.domain.Quote;
import com.cotizador.danos.quote.domain.QuoteRepository;
import com.cotizador.danos.quote.domain.QuoteStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class CriticalBackendFlowsTest {

  private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-04-21T12:00:00Z"), ZoneOffset.UTC);

  @Test
  void shouldCreateFolioFlow() {
    InMemoryQuoteRepository quoteRepository = new InMemoryQuoteRepository();
    FolioGenerator folioGenerator = () -> "FOL-00000001";
    CreateQuoteUseCase createQuoteUseCase = new CreateQuoteUseCase(quoteRepository, folioGenerator, FIXED_CLOCK);

    Quote createdQuote = createQuoteUseCase.handle();

    assertThat(createdQuote.getFolio()).isEqualTo("FOL-00000001");
    assertThat(createdQuote.getStatus()).isEqualTo(QuoteStatus.DRAFT);
    assertThat(createdQuote.getVersion()).isEqualTo(Quote.INITIAL_VERSION);
    assertThat(quoteRepository.findByFolio("FOL-00000001")).isPresent();
  }

  @Test
  void shouldCompleteLocationsFlow() {
    InMemoryQuoteRepository quoteRepository = new InMemoryQuoteRepository();
    InMemoryLocationRepository locationRepository = new InMemoryLocationRepository();
    String folio = createBaseQuote(quoteRepository);

    ReplaceQuoteLocationsUseCase replaceLocationsUseCase = new ReplaceQuoteLocationsUseCase(
        quoteRepository,
        locationRepository,
        FIXED_CLOCK
    );
    ListQuoteLocationsUseCase listQuoteLocationsUseCase = new ListQuoteLocationsUseCase(quoteRepository, locationRepository);
    UpdateQuoteLocationUseCase updateQuoteLocationUseCase = new UpdateQuoteLocationUseCase(
        locationRepository,
        quoteRepository,
        FIXED_CLOCK
    );

    replaceLocationsUseCase.handle(folio, List.of(
        new QuoteLocationPatch("Matriz Centro", "Bogota", "Cundinamarca", "Calle 100 #10-20", "110111", "CONCRETE", "OFFICE", 1_500_000),
        new QuoteLocationPatch("Sucursal Norte", "Bogota", "Cundinamarca", null, null, "CONCRETE", "OFFICE", 900_000)
    ));

    List<QuoteLocation> currentLocations = listQuoteLocationsUseCase.handle(folio);
    QuoteLocation incompleteLocation = currentLocations.stream()
        .filter(location -> location.getValidationStatus() == LocationValidationStatus.INCOMPLETE)
        .findFirst()
        .orElseThrow();

    QuoteLocation updatedLocation = updateQuoteLocationUseCase.handle(
        folio,
        incompleteLocation.getId(),
        new QuoteLocationUpdatePatch(null, null, null, "Calle 80 #15-10", "110221", null, null, null)
    );

    assertThat(updatedLocation.getValidationStatus()).isEqualTo(LocationValidationStatus.COMPLETE);
    assertThat(listQuoteLocationsUseCase.handle(folio))
        .extracting(QuoteLocation::getValidationStatus)
        .containsOnly(LocationValidationStatus.COMPLETE);
    assertThat(quoteRepository.findByFolio(folio).orElseThrow().getVersion()).isEqualTo(3);
  }

  @Test
  void shouldRunCalculationFlowAndPersistTraceability() {
    InMemoryQuoteRepository quoteRepository = new InMemoryQuoteRepository();
    InMemoryLocationRepository locationRepository = new InMemoryLocationRepository();
    InMemoryQuoteCoverageRepository quoteCoverageRepository = new InMemoryQuoteCoverageRepository();
    InMemoryQuoteCalculationResultRepository calculationResultRepository = new InMemoryQuoteCalculationResultRepository();
    InMemoryCalculationTraceRepository calculationTraceRepository = new InMemoryCalculationTraceRepository();
    PremiumCalculator premiumCalculator = new FixedPremiumCalculator();
    String folio = createBaseQuote(quoteRepository);

    new ReplaceQuoteLocationsUseCase(quoteRepository, locationRepository, FIXED_CLOCK).handle(folio, List.of(
        new QuoteLocationPatch("Matriz Centro", "Bogota", "Cundinamarca", "Calle 100 #10-20", "110111", "CONCRETE", "OFFICE", 1_500_000),
        new QuoteLocationPatch("Sucursal Norte", "Bogota", "Cundinamarca", null, null, "CONCRETE", "OFFICE", 900_000)
    ));
    new ConfigureQuoteCoveragesUseCase(quoteRepository, quoteCoverageRepository, FIXED_CLOCK).handle(
        folio,
        List.of(new QuoteCoveragePatch("FIRE", "Incendio", 1_000_000, "FIXED", 50_000L, true))
    );

    CalculateQuoteUseCase calculateQuoteUseCase = new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator,
        calculationTraceRepository,
        calculationResultRepository
    );

    QuoteCalculationResult result = calculateQuoteUseCase.handle(folio);

    assertThat(result.getNetPremium()).isEqualTo(1000.0);
    assertThat(result.getAlerts()).contains("Location Sucursal Norte skipped due to incomplete data");
    assertThat(calculationResultRepository.findByQuoteFolio(folio)).isPresent();
    assertThat(calculationTraceRepository.savedTraceDetails).hasSize(1);
    assertThat(quoteRepository.findByFolio(folio).orElseThrow().getStatus()).isEqualTo(QuoteStatus.CALCULATED);
  }

  private String createBaseQuote(InMemoryQuoteRepository quoteRepository) {
    CreateQuoteUseCase createQuoteUseCase = new CreateQuoteUseCase(
        quoteRepository,
        () -> "FOL-00000001",
        FIXED_CLOCK
    );
    return createQuoteUseCase.handle().getFolio();
  }

  private static final class FixedPremiumCalculator implements PremiumCalculator {

    @Override
    public double calculate(QuoteLocation location, List<QuoteCoverageSelection> coverages) {
      return 1000.0;
    }

    @Override
    public List<CalculationTraceDetail> traceFor(QuoteLocation location, List<QuoteCoverageSelection> coverages) {
      return List.of(new CalculationTraceDetail(
          location.getQuoteFolio(),
          location.getId(),
          "BASE_RATE",
          0.015,
          1,
          Map.of("coverageCode", "FIRE")
      ));
    }
  }

  private static final class InMemoryQuoteRepository implements QuoteRepository {

    private final Map<String, Quote> storage = new HashMap<>();

    @Override
    public boolean existsByFolio(String folio) {
      return storage.containsKey(folio);
    }

    @Override
    public Optional<Quote> findByFolio(String folio) {
      return Optional.ofNullable(storage.get(folio));
    }

    @Override
    public List<Quote> findVersionsByRootFolio(String rootFolio) {
      return storage.values().stream()
          .filter(quote -> resolveRootFolio(quote).equals(rootFolio))
          .sorted((left, right) -> Integer.compare(left.getVersion(), right.getVersion()))
          .toList();
    }

    @Override
    public Optional<Quote> findByRootFolioAndVersion(String rootFolio, int version) {
      return findVersionsByRootFolio(rootFolio).stream()
          .filter(quote -> quote.getVersion() == version)
          .findFirst();
    }

    @Override
    public Quote save(Quote quote) {
      storage.put(quote.getFolio(), quote);
      return quote;
    }

    private String resolveRootFolio(Quote quote) {
      if (quote.getParentQuoteFolio() == null || quote.getParentQuoteFolio().isBlank()) {
        return quote.getFolio();
      }
      Quote parent = storage.get(quote.getParentQuoteFolio());
      return parent == null ? quote.getParentQuoteFolio() : resolveRootFolio(parent);
    }
  }

  private static final class InMemoryLocationRepository implements LocationRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<String, List<QuoteLocation>> storage = new HashMap<>();

    @Override
    public long nextId() {
      return sequence.getAndIncrement();
    }

    @Override
    public Optional<QuoteLocation> findById(long id) {
      return storage.values().stream()
          .flatMap(List::stream)
          .filter(location -> location.getId() == id)
          .findFirst();
    }

    @Override
    public Optional<QuoteLocation> findByQuoteFolioAndId(String quoteFolio, long id) {
      return storage.getOrDefault(quoteFolio, List.of()).stream()
          .filter(location -> location.getId() == id)
          .findFirst();
    }

    @Override
    public List<QuoteLocation> findByQuoteFolio(String quoteFolio) {
      return new ArrayList<>(storage.getOrDefault(quoteFolio, List.of()));
    }

    @Override
    public void deleteByQuoteFolio(String quoteFolio) {
      storage.remove(quoteFolio);
    }

    @Override
    public QuoteLocation save(QuoteLocation quoteLocation) {
      List<QuoteLocation> current = new ArrayList<>(storage.getOrDefault(quoteLocation.getQuoteFolio(), List.of()));
      current.removeIf(location -> location.getId() == quoteLocation.getId());
      current.add(quoteLocation);
      storage.put(quoteLocation.getQuoteFolio(), current);
      return quoteLocation;
    }
  }

  private static final class InMemoryQuoteCoverageRepository implements QuoteCoverageRepository {

    private final Map<String, List<QuoteCoverageSelection>> storage = new HashMap<>();

    @Override
    public List<QuoteCoverageSelection> findByQuoteFolio(String quoteFolio) {
      return new ArrayList<>(storage.getOrDefault(quoteFolio, List.of()));
    }

    @Override
    public List<QuoteCoverageSelection> replaceForQuote(String quoteFolio, List<QuoteCoveragePatch> coverages) {
      List<QuoteCoverageSelection> replaced = coverages.stream()
          .map(patch -> QuoteCoverageSelection.create(quoteFolio, patch))
          .toList();
      storage.put(quoteFolio, replaced);
      return replaced;
    }
  }

  private static final class InMemoryQuoteCalculationResultRepository implements QuoteCalculationResultRepository {

    private final Map<String, QuoteCalculationResult> storage = new HashMap<>();

    @Override
    public Optional<QuoteCalculationResult> findByQuoteFolio(String folio) {
      return Optional.ofNullable(storage.get(folio));
    }

    @Override
    public QuoteCalculationResult save(String folio, QuoteCalculationResult result) {
      storage.put(folio, result);
      return result;
    }
  }

  private static final class InMemoryCalculationTraceRepository implements CalculationTraceRepository {

    private final List<CalculationTraceDetail> savedTraceDetails = new ArrayList<>();

    @Override
    public void saveAll(List<CalculationTraceDetail> traceDetails) {
      savedTraceDetails.addAll(traceDetails);
    }
  }
}
