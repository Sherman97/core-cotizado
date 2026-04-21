import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ApiResponse,
  CreateFolioResponse,
  GeneralInfo,
  LocationLayout,
  Location,
  LocationsPayload,
  LocationPatchPayload,
  LocationsSummary,
  LocationsResult,
  Coverage,
  CoverageOptions,
  CalculationResponse,
  QuoteState,
  OccupancyType,
  ConstructionType,
  CoverageType,
  Catalog
} from '../models/api.models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class QuoteApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/v1`;

  constructor(private http: HttpClient) {}

  /**
   * Create a new folio/quotation
   */
  createFolio(): Observable<ApiResponse<CreateFolioResponse>> {
    return this.http.post<ApiResponse<CreateFolioResponse>>(
      `${this.baseUrl}/folios`,
      {}
    );
  }

  /**
   * Save general information for a quote
   */
  saveGeneralInfo(folio: string, generalInfo: GeneralInfo): Observable<ApiResponse<GeneralInfo>> {
    return this.http.put<ApiResponse<GeneralInfo>>(
      `${this.baseUrl}/quotes/${folio}/general-info`,
      generalInfo
    );
  }

  /**
   * Get general information for a quote
   */
  getGeneralInfo(folio: string): Observable<ApiResponse<GeneralInfo>> {
    return this.http.get<ApiResponse<GeneralInfo>>(
      `${this.baseUrl}/quotes/${folio}/general-info`
    );
  }

  /**
   * Save location layout configuration
   */
  saveLocationLayout(folio: string, layout: LocationLayout): Observable<ApiResponse<LocationLayout>> {
    return this.http.put<ApiResponse<LocationLayout>>(
      `${this.baseUrl}/quotes/${folio}/locations/layout`,
      layout
    );
  }

  /**
   * Get location layout configuration
   */
  getLocationLayout(folio: string): Observable<ApiResponse<LocationLayout>> {
    return this.http.get<ApiResponse<LocationLayout>>(
      `${this.baseUrl}/quotes/${folio}/locations/layout`
    );
  }

  /**
   * Save all locations for a quote
   */
  saveLocations(folio: string, payload: LocationsPayload): Observable<ApiResponse<LocationsResult>> {
    return this.http.put<ApiResponse<LocationsResult>>(
      `${this.baseUrl}/quotes/${folio}/locations`,
      payload
    );
  }

  /**
   * Get all locations for a quote
   */
  getLocations(folio: string): Observable<ApiResponse<LocationsResult>> {
    return this.http.get<ApiResponse<LocationsResult>>(
      `${this.baseUrl}/quotes/${folio}/locations`
    );
  }

  /**
   * Update a specific location using PATCH
   */
  patchLocation(folio: string, indice: number, patch: LocationPatchPayload): Observable<ApiResponse<Location>> {
    return this.http.patch<ApiResponse<Location>>(
      `${this.baseUrl}/quotes/${folio}/locations/${indice}`,
      patch
    );
  }

  /**
   * Get locations summary
   */
  getLocationsSummary(folio: string): Observable<ApiResponse<LocationsSummary>> {
    return this.http.get<ApiResponse<LocationsSummary>>(
      `${this.baseUrl}/quotes/${folio}/locations/summary`
    );
  }

  /**
   * Get coverage options for a quote
   */
  getCoverageOptions(folio: string): Observable<ApiResponse<CoverageOptions>> {
    return this.http.get<ApiResponse<CoverageOptions>>(
      `${this.baseUrl}/quotes/${folio}/coverage-options`
    );
  }

  /**
   * Save coverage options for a quote
   */
  saveCoverageOptions(folio: string, coverageOptions: CoverageOptions): Observable<ApiResponse<CoverageOptions>> {
    return this.http.put<ApiResponse<CoverageOptions>>(
      `${this.baseUrl}/quotes/${folio}/coverage-options`,
      coverageOptions
    );
  }

  /**
   * Trigger calculation for a quote
   */
  calculateQuote(folio: string): Observable<ApiResponse<CalculationResponse>> {
    return this.http.post<ApiResponse<CalculationResponse>>(
      `${this.baseUrl}/quotes/${folio}/calculate`,
      {}
    );
  }

  /**
   * Get final state of a quote
   */
  getQuoteState(folio: string): Observable<ApiResponse<QuoteState>> {
    return this.http.get<ApiResponse<QuoteState>>(
      `${this.baseUrl}/quotes/${folio}/state`
    );
  }

  /**
   * Get locations results (calculated results)
   */
  getLocationsResults(folio: string): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(
      `${this.baseUrl}/quotes/${folio}/locations/results`
    );
  }
}

