import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { Catalog, OccupancyType, ConstructionType, CoverageType } from '../models/api.models';
import { environment } from '../../../environments/environment';

export interface PostalCodeCatalogItem {
  code: string;
  cityCode: string;
}

export interface GeographyCityCatalogItem {
  code: string;
  name: string;
  postalCodes: string[];
}

export interface GeographyDepartmentCatalogItem {
  code: string;
  name: string;
  cities: GeographyCityCatalogItem[];
}

interface GeographyCatalogResponse {
  departments: GeographyDepartmentCatalogItem[];
}

interface ApiEnvelope<T> {
  data: T;
}

/**
 * Mock catalog service for development.
 * In production, this would fetch from backend catalogs API.
 */
@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  private readonly baseUrl = `${environment.apiBaseUrl}/v1`;
  private catalogSubject = new BehaviorSubject<Catalog>(this.getMockCatalog());
  public catalog$ = this.catalogSubject.asObservable();
  private geographyCatalog$?: Observable<GeographyDepartmentCatalogItem[]>;

  constructor(private http: HttpClient) {}

  /**
   * Get catalogs (in production, this would call backend)
   */
  getCatalogs(): Observable<Catalog> {
    return this.catalog$;
  }

  /**
   * Get occupancy types
   */
  getOccupancyTypes(): Observable<OccupancyType[]> {
    return new Observable(observer => {
      this.catalog$.subscribe(catalog => {
        observer.next(catalog.occupancyTypes);
        observer.complete();
      });
    });
  }

  /**
   * Get construction types
   */
  getConstructionTypes(): Observable<ConstructionType[]> {
    return new Observable(observer => {
      this.catalog$.subscribe(catalog => {
        observer.next(catalog.constructionTypes);
        observer.complete();
      });
    });
  }

  /**
   * Get coverage types
   */
  getCoverageTypes(): Observable<CoverageType[]> {
    return new Observable(observer => {
      this.catalog$.subscribe(catalog => {
        observer.next(catalog.coverageTypes);
        observer.complete();
      });
    });
  }

  /**
   * Valid postal codes (seeded in backend migration V5).
   */
  getPostalCodes(): Observable<PostalCodeCatalogItem[]> {
    return this.getGeographyCatalog().pipe(
      map((departments) => departments.flatMap((department) =>
        department.cities.flatMap((city) => city.postalCodes.map((code) => ({ code, cityCode: city.code })))
      ))
    );
  }

  getDepartments(): Observable<GeographyDepartmentCatalogItem[]> {
    return this.getGeographyCatalog();
  }

  getCitiesByDepartment(departmentCode: string): Observable<GeographyCityCatalogItem[]> {
    return this.getGeographyCatalog().pipe(
      map((departments) => departments.find((department) => department.code === departmentCode)?.cities ?? [])
    );
  }

  getPostalCodesByCity(cityCode: string): Observable<PostalCodeCatalogItem[]> {
    return this.getGeographyCatalog().pipe(
      map((departments) => departments
        .flatMap((department) => department.cities)
        .filter((city) => city.code === cityCode)
        .flatMap((city) => city.postalCodes.map((code) => ({ code, cityCode })))
      )
    );
  }

  private getGeographyCatalog(): Observable<GeographyDepartmentCatalogItem[]> {
    if (!this.geographyCatalog$) {
      this.geographyCatalog$ = this.http
        .get<ApiEnvelope<GeographyCatalogResponse>>(`${this.baseUrl}/catalogs/geography`)
        .pipe(
          map((response) => response.data.departments ?? []),
          shareReplay(1)
        );
    }
    return this.geographyCatalog$;
  }

  /**
   * Mock catalog data - Replace with backend API call
   */
  private getMockCatalog(): Catalog {
    return {
      occupancyTypes: [
        { code: 'OFFICE', name: 'Oficina', description: 'Espacios de oficina y áreas administrativas' },
        { code: 'COMMERCE', name: 'Comercio', description: 'Espacios comerciales de venta al por menor' },
        { code: 'RESTAURANT', name: 'Restaurante', description: 'Establecimientos de servicio de alimentos' },
        { code: 'WAREHOUSE', name: 'Bodega', description: 'Instalaciones de almacenamiento y bodegaje' },
        { code: 'LIGHT_INDUSTRY', name: 'Industria ligera', description: 'Manufactura e industria liviana' }
      ],
      constructionTypes: [
        { code: 'CONCRETE', name: 'Concreto', description: 'Construcción en concreto reforzado' },
        { code: 'MIXED', name: 'Mixta', description: 'Construcción con materiales mixtos' },
        { code: 'WOOD', name: 'Madera', description: 'Construcción en estructura de madera' }
      ],
      coverageTypes: [
        { code: 'FIRE', name: 'Incendio', description: 'Cobertura por incendio y explosión', maxInsuredLimit: 5000000 },
        { code: 'EARTHQUAKE', name: 'Terremoto', description: 'Cobertura por eventos sísmicos', maxInsuredLimit: 3000000 },
        { code: 'FLOOD', name: 'Inundación', description: 'Cobertura por daños por agua e inundación', maxInsuredLimit: 2000000 }
      ]
    };
  }
}
