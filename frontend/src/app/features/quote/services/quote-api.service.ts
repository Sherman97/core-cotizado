import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ApiHttpClient } from '../../../core/api/http-client.service';
import { Quote } from '../../../shared/models/quote.model';
import { QuoteLocation } from '../../../shared/models/location.model';
import { QuoteCoverage } from '../../../shared/models/coverage.model';
import { CalculationResult } from '../../../shared/models/calculation.model';

interface ApiEnvelope<T> {
  data: T;
}

@Injectable({ providedIn: 'root' })
export class QuoteApiService {
  private readonly api = inject(ApiHttpClient);

  createQuote(productCode = 'DANOS_MVP'): Observable<Quote> {
    return this.api.post<ApiEnvelope<Quote>>('/quotes', { productCode }).pipe(map((r) => r.data));
  }

  getQuote(folio: string): Observable<Quote> {
    return this.api.get<ApiEnvelope<Quote>>(`/quotes/${folio}`).pipe(map((r) => r.data));
  }

  updateGeneralData(folio: string, payload: Partial<Quote>): Observable<Quote> {
    return this.api.put<ApiEnvelope<Quote>>(`/quotes/${folio}/general-data`, payload).pipe(map((r) => r.data));
  }

  saveLocationLayout(folio: string, payload: unknown): Observable<Quote> {
    return this.api.put<ApiEnvelope<Quote>>(`/quotes/${folio}/location-layout`, payload).pipe(map((r) => r.data));
  }

  listLocations(folio: string): Observable<QuoteLocation[]> {
    return this.api.get<ApiEnvelope<QuoteLocation[]>>(`/quotes/${folio}/locations`).pipe(map((r) => r.data));
  }

  createLocation(folio: string, payload: unknown): Observable<QuoteLocation> {
    return this.api.post<ApiEnvelope<QuoteLocation>>(`/quotes/${folio}/locations`, payload).pipe(map((r) => r.data));
  }

  configureCoverages(folio: string, payload: { coverages: QuoteCoverage[] }): Observable<QuoteCoverage[]> {
    return this.api.put<ApiEnvelope<QuoteCoverage[]>>(`/quotes/${folio}/coverages`, payload).pipe(map((r) => r.data));
  }

  getCoverageCatalog(): Observable<Array<{ code: string; name: string; description: string }>> {
    return this.api.get<ApiEnvelope<Array<{ code: string; name: string; description: string }>>>('/catalogs/coverages')
      .pipe(map((r) => r.data));
  }

  calculate(folio: string): Observable<CalculationResult> {
    return this.api.post<ApiEnvelope<CalculationResult>>(`/quotes/${folio}/calculate`, {}).pipe(map((r) => r.data));
  }

  getStatus(folio: string): Observable<unknown> {
    return this.api.get<ApiEnvelope<unknown>>(`/quotes/${folio}/status`).pipe(map((r) => r.data));
  }
}
