import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map } from 'rxjs';
import {
  ApiResponse,
  CalculationResponse,
  CoverageOptions,
  GeneralInfo,
  Location,
  LocationCalculationResult,
  LocationLayout,
  LocationPatchPayload,
  LocationsPayload,
  LocationsResult,
  LocationsSummary,
  QuoteState,
  CreateFolioResponse,
  QuoteListItem
} from '../models/api.models';
import { environment } from '../../../environments/environment';

interface ApiEnvelope<T> {
  data: T;
}

interface BackendCreateFolioResponse {
  numeroFolio: string;
  estadoCotizacion: string;
  version: number;
}

interface BackendGeneralInfoResponse {
  numeroFolio: string;
  productCode: string | null;
  customerName: string | null;
  currency: string | null;
  observations: string | null;
}

interface BackendLocationLayoutResponse {
  expectedLocationCount: number;
  captureRiskZone: boolean;
  captureGeoreference: boolean;
  notes: string | null;
}

interface BackendLocationResponse {
  indice: number;
  nombreUbicacion: string;
  ciudad: string;
  departamento: string;
  direccion: string | null;
  codigoPostal: string | null;
  tipoConstructivo: string;
  ocupacion: string;
  valorAsegurado: number;
}

interface BackendLocationsSummaryResponse {
  totalUbicaciones: number;
  ubicacionesCompletas: number;
  primaNetaCalculada: number;
}

interface BackendCoverageCatalogItemResponse {
  code: string;
  name: string;
  active: boolean;
}

interface BackendCoverageSelectionResponse {
  coverageCode: string;
  coverageName: string;
  insuredLimit: number;
  deductibleType: 'FIXED' | 'PERCENTAGE';
  deductibleValue: number | null;
  selected: boolean;
}

interface BackendCoverageOptionsResponse {
  availableCoverages: BackendCoverageCatalogItemResponse[];
  selectedCoverages: BackendCoverageSelectionResponse[];
}

interface BackendCalculationLocationResultResponse {
  indice: number;
  nombreUbicacion: string;
  calculada: boolean;
  prima: number;
  alertas: string[];
}

interface BackendCalculationResponse {
  numeroFolio: string;
  primaComercial: number;
  primasPorUbicacion: BackendCalculationLocationResultResponse[];
  alertas: string[];
}

interface BackendQuoteStateResponse {
  numeroFolio: string;
  estadoCotizacion: 'DRAFT' | 'PENDING_CALCULATION' | 'CALCULATED' | 'SAVED';
  primaNeta: number;
  gastos: number;
  impuestos: number;
  primaComercial: number;
  alertas: string[];
}

interface BackendQuoteListItemResponse {
  folio: string;
  customerName: string | null;
  totalInsuredValue: number;
  totalLocations: number;
  status: string;
  createdAt: string | null;
  totalPremium: number | null;
}

interface BackendLocationResultsResponse {
  items: BackendCalculationLocationResultResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class QuoteApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/v1`;

  constructor(private http: HttpClient) {}

  createFolio(): Observable<ApiResponse<CreateFolioResponse>> {
    return this.http.post<ApiEnvelope<BackendCreateFolioResponse>>(`${this.baseUrl}/folios`, {}).pipe(
      map(({ data }) => ({
        data: {
          numeroFolio: data.numeroFolio,
          estadoCotizacion: data.estadoCotizacion,
          version: data.version
        }
      }))
    );
  }

  listQuotes(): Observable<ApiResponse<QuoteListItem[]>> {
    return this.http.get<ApiEnvelope<BackendQuoteListItemResponse[]>>(`${this.baseUrl}/quotes`).pipe(
      map(({ data }) => ({
        data: data.map((item) => ({
          folio: item.folio,
          customerName: item.customerName ?? '',
          totalInsuredValue: item.totalInsuredValue,
          totalLocations: item.totalLocations,
          status: item.status,
          createdAt: item.createdAt ?? undefined,
          totalPremium: item.totalPremium
        }))
      }))
    );
  }

  saveGeneralInfo(folio: string, generalInfo: GeneralInfo): Observable<ApiResponse<GeneralInfo>> {
    return this.http
      .put<ApiEnvelope<BackendGeneralInfoResponse>>(`${this.baseUrl}/quotes/${folio}/general-info`, generalInfo)
      .pipe(map(({ data }) => ({ data: this.mapGeneralInfo(data) })));
  }

  getGeneralInfo(folio: string): Observable<ApiResponse<GeneralInfo>> {
    return this.http
      .get<ApiEnvelope<BackendGeneralInfoResponse>>(`${this.baseUrl}/quotes/${folio}/general-info`)
      .pipe(map(({ data }) => ({ data: this.mapGeneralInfo(data) })));
  }

  saveLocationLayout(folio: string, layout: LocationLayout): Observable<ApiResponse<LocationLayout>> {
    return this.http
      .put<ApiEnvelope<BackendLocationLayoutResponse>>(`${this.baseUrl}/quotes/${folio}/locations/layout`, layout)
      .pipe(map(({ data }) => ({ data: this.mapLocationLayout(data) })));
  }

  getLocationLayout(folio: string): Observable<ApiResponse<LocationLayout>> {
    return this.http
      .get<ApiEnvelope<BackendLocationLayoutResponse>>(`${this.baseUrl}/quotes/${folio}/locations/layout`)
      .pipe(map(({ data }) => ({ data: this.mapLocationLayout(data) })));
  }

  saveLocations(folio: string, payload: LocationsPayload): Observable<ApiResponse<LocationsResult>> {
    return this.http
      .put<ApiEnvelope<BackendLocationResponse[]>>(`${this.baseUrl}/quotes/${folio}/locations`, payload)
      .pipe(map(({ data }) => ({ data: { locations: data.map((item) => this.mapLocation(item)) } })));
  }

  getLocations(folio: string): Observable<ApiResponse<LocationsResult>> {
    return this.http
      .get<ApiEnvelope<BackendLocationResponse[]>>(`${this.baseUrl}/quotes/${folio}/locations`)
      .pipe(map(({ data }) => ({ data: { locations: data.map((item) => this.mapLocation(item)) } })));
  }

  patchLocation(folio: string, indice: number, patch: LocationPatchPayload): Observable<ApiResponse<Location>> {
    return this.http
      .patch<ApiEnvelope<BackendLocationResponse>>(`${this.baseUrl}/quotes/${folio}/locations/${indice}`, patch)
      .pipe(map(({ data }) => ({ data: this.mapLocation(data) })));
  }

  getLocationsSummary(folio: string): Observable<ApiResponse<LocationsSummary>> {
    return this.http
      .get<ApiEnvelope<BackendLocationsSummaryResponse>>(`${this.baseUrl}/quotes/${folio}/locations/summary`)
      .pipe(
        map(({ data }) => ({
          data: {
            totalLocations: data.totalUbicaciones,
            completedLocations: data.ubicacionesCompletas,
            totalInsuredValue: data.primaNetaCalculada
          }
        }))
      );
  }

  getCoverageOptions(folio: string): Observable<ApiResponse<CoverageOptions>> {
    return this.http
      .get<ApiEnvelope<BackendCoverageOptionsResponse>>(`${this.baseUrl}/quotes/${folio}/coverage-options`)
      .pipe(map(({ data }) => ({ data: this.mapCoverageOptions(data) })));
  }

  saveCoverageOptions(folio: string, coverageOptions: CoverageOptions): Observable<ApiResponse<CoverageOptions>> {
    return this.http
      .put<ApiEnvelope<BackendCoverageOptionsResponse>>(`${this.baseUrl}/quotes/${folio}/coverage-options`, {
        coverages: coverageOptions.coverages
      })
      .pipe(map(({ data }) => ({ data: this.mapCoverageOptions(data) })));
  }

  calculateQuote(folio: string): Observable<ApiResponse<CalculationResponse>> {
    return this.http.post<ApiEnvelope<BackendCalculationResponse>>(`${this.baseUrl}/quotes/${folio}/calculate`, {}).pipe(
      map(({ data }) => ({ data: this.mapCalculationResponse(data) }))
    );
  }

  getQuoteState(folio: string): Observable<ApiResponse<QuoteState>> {
    return this.http.get<ApiEnvelope<BackendQuoteStateResponse>>(`${this.baseUrl}/quotes/${folio}/state`).pipe(
      map(({ data }) => ({
        data: {
          folio: data.numeroFolio,
          status: data.estadoCotizacion,
          netPremium: data.primaNeta,
          expenseAmount: data.gastos,
          taxAmount: data.impuestos,
          totalPremium: data.primaComercial,
          warnings: data.alertas
        }
      }))
    );
  }

  saveQuote(folio: string): Observable<ApiResponse<GeneralInfo>> {
    return this.http
      .post<ApiEnvelope<BackendGeneralInfoResponse>>(`${this.baseUrl}/quotes/${folio}/save`, {})
      .pipe(map(({ data }) => ({ data: this.mapGeneralInfo(data) })));
  }

  getLocationsResults(folio: string): Observable<ApiResponse<{ items: LocationCalculationResult[] }>> {
    return this.http.get<ApiEnvelope<BackendLocationResultsResponse>>(`${this.baseUrl}/quotes/${folio}/locations/results`).pipe(
      map(({ data }) => ({
        data: {
          items: data.items.map((item) => this.mapLocationResult(item))
        }
      }))
    );
  }

  getCalculationResult(folio: string): Observable<ApiResponse<CalculationResponse>> {
    return forkJoin({
      state: this.getQuoteState(folio),
      locations: this.getLocationsResults(folio)
    }).pipe(
      map(({ state, locations }) => ({
        data: {
          folio: state.data.folio,
          totalPremium: state.data.totalPremium ?? 0,
          locationResults: locations.data.items,
          warnings: state.data.warnings ?? []
        }
      }))
    );
  }

  private mapGeneralInfo(data: BackendGeneralInfoResponse): GeneralInfo {
    return {
      productCode: data.productCode ?? '',
      customerName: data.customerName ?? '',
      currency: data.currency ?? '',
      observations: data.observations ?? undefined
    };
  }

  private mapLocationLayout(data: BackendLocationLayoutResponse): LocationLayout {
    return {
      expectedLocationCount: data.expectedLocationCount,
      captureRiskZone: data.captureRiskZone,
      captureGeoreference: data.captureGeoreference,
      notes: data.notes ?? undefined
    };
  }

  private mapLocation(data: BackendLocationResponse): Location {
    return {
      indice: data.indice,
      locationName: data.nombreUbicacion,
      city: data.ciudad,
      department: data.departamento,
      address: data.direccion ?? undefined,
      postalCode: data.codigoPostal ?? undefined,
      constructionType: data.tipoConstructivo,
      occupancyType: data.ocupacion,
      insuredValue: data.valorAsegurado
    };
  }

  private mapCoverageOptions(data: BackendCoverageOptionsResponse): CoverageOptions {
    return {
      availableCoverages: data.availableCoverages.map((item) => ({
        code: item.code,
        name: item.name,
        active: item.active
      })),
      coverages: data.selectedCoverages.map((item) => ({
        coverageCode: item.coverageCode,
        coverageName: item.coverageName,
        insuredLimit: item.insuredLimit,
        deductibleType: item.deductibleType,
        deductibleValue: item.deductibleValue ?? 0,
        selected: item.selected
      }))
    };
  }

  private mapLocationResult(data: BackendCalculationLocationResultResponse): LocationCalculationResult {
    return {
      indice: data.indice,
      locationName: data.nombreUbicacion,
      basePremium: data.prima,
      appliedFactors: [],
      totalPremium: data.prima,
      calculable: data.calculada,
      excludionReason: data.alertas?.[0]
    };
  }

  private mapCalculationResponse(data: BackendCalculationResponse): CalculationResponse {
    return {
      folio: data.numeroFolio,
      totalPremium: data.primaComercial,
      locationResults: data.primasPorUbicacion.map((item) => this.mapLocationResult(item)),
      warnings: data.alertas
    };
  }
}
