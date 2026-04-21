import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Catalog, OccupancyType, ConstructionType, CoverageType } from '../models/api.models';

/**
 * Mock catalog service for development.
 * In production, this would fetch from backend catalogs API.
 */
@Injectable({
  providedIn: 'root'
})
export class CatalogService {
  private catalogSubject = new BehaviorSubject<Catalog>(this.getMockCatalog());
  public catalog$ = this.catalogSubject.asObservable();

  constructor() {}

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
   * Mock catalog data - Replace with backend API call
   */
  private getMockCatalog(): Catalog {
    return {
      occupancyTypes: [
        { code: 'OFFICE', name: 'Office', description: 'Office spaces and administrative areas' },
        { code: 'COMMERCE', name: 'Commerce', description: 'Commercial retail spaces' },
        { code: 'RESTAURANT', name: 'Restaurant', description: 'Food service establishments' },
        { code: 'WAREHOUSE', name: 'Warehouse', description: 'Storage and warehouse facilities' },
        { code: 'LIGHT_INDUSTRY', name: 'Light Industry', description: 'Manufacturing and light industrial' }
      ],
      constructionTypes: [
        { code: 'CONCRETE', name: 'Concrete', description: 'Reinforced concrete construction' },
        { code: 'MIXED', name: 'Mixed', description: 'Mixed materials construction' },
        { code: 'WOOD', name: 'Wood', description: 'Wooden structure construction' }
      ],
      coverageTypes: [
        { code: 'FIRE', name: 'Fire', description: 'Fire and explosion coverage', maxInsuredLimit: 5000000 },
        { code: 'EARTHQUAKE', name: 'Earthquake', description: 'Seismic event coverage', maxInsuredLimit: 3000000 },
        { code: 'FLOOD', name: 'Flood', description: 'Water damage and flooding coverage', maxInsuredLimit: 2000000 }
      ]
    };
  }
}

