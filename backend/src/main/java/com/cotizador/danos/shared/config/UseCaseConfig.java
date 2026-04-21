package com.cotizador.danos.shared.config;

import com.cotizador.danos.calculation.application.CalculateQuoteUseCase;
import com.cotizador.danos.calculation.application.GetQuoteLocationResultsUseCase;
import com.cotizador.danos.calculation.domain.CalculationTraceRepository;
import com.cotizador.danos.calculation.domain.PremiumCalculator;
import com.cotizador.danos.calculation.domain.QuoteCalculationResultRepository;
import com.cotizador.danos.coverage.application.ConfigureQuoteCoveragesUseCase;
import com.cotizador.danos.coverage.application.GetQuoteCoverageOptionsUseCase;
import com.cotizador.danos.coverage.application.ListActiveCoveragesUseCase;
import com.cotizador.danos.coverage.domain.CoverageCatalogRepository;
import com.cotizador.danos.coverage.domain.QuoteCoverageRepository;
import com.cotizador.danos.location.application.CreateQuoteLocationUseCase;
import com.cotizador.danos.location.application.GetQuoteLocationSummaryUseCase;
import com.cotizador.danos.location.application.ListQuoteLocationsUseCase;
import com.cotizador.danos.location.application.ReplaceQuoteLocationsUseCase;
import com.cotizador.danos.location.application.UpdateQuoteLocationUseCase;
import com.cotizador.danos.location.domain.LocationRepository;
import com.cotizador.danos.quote.application.CreateQuoteUseCase;
import com.cotizador.danos.quote.application.CreateQuoteWithIdempotencyUseCase;
import com.cotizador.danos.quote.application.GetQuoteByFolioUseCase;
import com.cotizador.danos.quote.application.GetQuoteFinalStatusUseCase;
import com.cotizador.danos.quote.application.ListQuotesUseCase;
import com.cotizador.danos.quote.application.SaveQuoteLocationLayoutUseCase;
import com.cotizador.danos.quote.application.SaveQuoteUseCase;
import com.cotizador.danos.quote.application.UpdateQuoteGeneralDataUseCase;
import com.cotizador.danos.quote.domain.FolioIdempotencyRepository;
import com.cotizador.danos.quote.domain.FolioGenerator;
import com.cotizador.danos.quote.domain.QuoteRepository;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  private final AtomicLong folioSequence = new AtomicLong(1);

  @Bean
  FolioGenerator folioGenerator() {
    return () -> "FOL-" + String.format("%08d", folioSequence.getAndIncrement());
  }

  @Bean
  PremiumCalculator premiumCalculator() {
    return new StubPremiumCalculator();
  }

  @Bean
  CreateQuoteUseCase createQuoteUseCase(
      QuoteRepository quoteRepository,
      FolioGenerator folioGenerator,
      Clock clock
  ) {
    return new CreateQuoteUseCase(quoteRepository, folioGenerator, clock);
  }

  @Bean
  CreateQuoteWithIdempotencyUseCase createQuoteWithIdempotencyUseCase(
      CreateQuoteUseCase createQuoteUseCase,
      FolioIdempotencyRepository folioIdempotencyRepository,
      QuoteRepository quoteRepository
  ) {
    return new CreateQuoteWithIdempotencyUseCase(
        createQuoteUseCase,
        folioIdempotencyRepository,
        quoteRepository
    );
  }

  @Bean
  GetQuoteByFolioUseCase getQuoteByFolioUseCase(QuoteRepository quoteRepository) {
    return new GetQuoteByFolioUseCase(quoteRepository);
  }

  @Bean
  UpdateQuoteGeneralDataUseCase updateQuoteGeneralDataUseCase(QuoteRepository quoteRepository, Clock clock) {
    return new UpdateQuoteGeneralDataUseCase(quoteRepository, clock);
  }

  @Bean
  SaveQuoteLocationLayoutUseCase saveQuoteLocationLayoutUseCase(QuoteRepository quoteRepository, Clock clock) {
    return new SaveQuoteLocationLayoutUseCase(quoteRepository, clock);
  }

  @Bean
  CreateQuoteLocationUseCase createQuoteLocationUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository
  ) {
    return new CreateQuoteLocationUseCase(quoteRepository, locationRepository);
  }

  @Bean
  ReplaceQuoteLocationsUseCase replaceQuoteLocationsUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      Clock clock
  ) {
    return new ReplaceQuoteLocationsUseCase(quoteRepository, locationRepository, clock);
  }

  @Bean
  ListQuoteLocationsUseCase listQuoteLocationsUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository
  ) {
    return new ListQuoteLocationsUseCase(quoteRepository, locationRepository);
  }

  @Bean
  UpdateQuoteLocationUseCase updateQuoteLocationUseCase(
      LocationRepository locationRepository,
      QuoteRepository quoteRepository,
      Clock clock
  ) {
    return new UpdateQuoteLocationUseCase(locationRepository, quoteRepository, clock);
  }

  @Bean
  GetQuoteLocationSummaryUseCase getQuoteLocationSummaryUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository
  ) {
    return new GetQuoteLocationSummaryUseCase(quoteRepository, locationRepository, quoteCalculationResultRepository);
  }

  @Bean
  ConfigureQuoteCoveragesUseCase configureQuoteCoveragesUseCase(
      QuoteRepository quoteRepository,
      QuoteCoverageRepository quoteCoverageRepository,
      Clock clock
  ) {
    return new ConfigureQuoteCoveragesUseCase(quoteRepository, quoteCoverageRepository, clock);
  }

  @Bean
  ListActiveCoveragesUseCase listActiveCoveragesUseCase(CoverageCatalogRepository coverageCatalogRepository) {
    return new ListActiveCoveragesUseCase(coverageCatalogRepository);
  }

  @Bean
  GetQuoteCoverageOptionsUseCase getQuoteCoverageOptionsUseCase(
      QuoteRepository quoteRepository,
      QuoteCoverageRepository quoteCoverageRepository,
      CoverageCatalogRepository coverageCatalogRepository
  ) {
    return new GetQuoteCoverageOptionsUseCase(
        quoteRepository,
        quoteCoverageRepository,
        coverageCatalogRepository
    );
  }

  @Bean
  CalculateQuoteUseCase calculateQuoteUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      QuoteCoverageRepository quoteCoverageRepository,
      PremiumCalculator premiumCalculator,
      CalculationTraceRepository calculationTraceRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository
  ) {
    return new CalculateQuoteUseCase(
        quoteRepository,
        locationRepository,
        quoteCoverageRepository,
        premiumCalculator,
        calculationTraceRepository,
        quoteCalculationResultRepository
    );
  }

  @Bean
  GetQuoteLocationResultsUseCase getQuoteLocationResultsUseCase(
      QuoteRepository quoteRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository,
      LocationRepository locationRepository
  ) {
    return new GetQuoteLocationResultsUseCase(quoteRepository, quoteCalculationResultRepository, locationRepository);
  }

  @Bean
  GetQuoteFinalStatusUseCase getQuoteFinalStatusUseCase(
      QuoteRepository quoteRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository
  ) {
    return new GetQuoteFinalStatusUseCase(quoteRepository, quoteCalculationResultRepository);
  }

  @Bean
  SaveQuoteUseCase saveQuoteUseCase(QuoteRepository quoteRepository) {
    return new SaveQuoteUseCase(quoteRepository);
  }

  @Bean
  ListQuotesUseCase listQuotesUseCase(
      QuoteRepository quoteRepository,
      LocationRepository locationRepository,
      QuoteCalculationResultRepository quoteCalculationResultRepository
  ) {
    return new ListQuotesUseCase(
        quoteRepository,
        locationRepository,
        quoteCalculationResultRepository
    );
  }
}
