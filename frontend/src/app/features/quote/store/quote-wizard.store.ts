import { Injectable, signal } from '@angular/core';
import { Quote } from '../../../shared/models/quote.model';
import { QuoteLocation } from '../../../shared/models/location.model';
import { QuoteCoverage } from '../../../shared/models/coverage.model';
import { CalculationResult } from '../../../shared/models/calculation.model';

@Injectable({ providedIn: 'root' })
export class QuoteWizardStore {
  readonly quote = signal<Quote | null>(null);
  readonly locations = signal<QuoteLocation[]>([]);
  readonly coverages = signal<QuoteCoverage[]>([]);
  readonly calculation = signal<CalculationResult | null>(null);

  setQuote(quote: Quote): void {
    this.quote.set(quote);
  }

  setLocations(locations: QuoteLocation[]): void {
    this.locations.set(locations);
  }

  setCoverages(coverages: QuoteCoverage[]): void {
    this.coverages.set(coverages);
  }

  setCalculation(calculation: CalculationResult): void {
    this.calculation.set(calculation);
  }
}
