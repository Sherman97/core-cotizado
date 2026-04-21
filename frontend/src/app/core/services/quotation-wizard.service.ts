import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { QuotationWizardState, GeneralInfoFormData, LocationFormData, CoverageFormData } from '../models/form.models';

@Injectable({
  providedIn: 'root'
})
export class QuotationWizardService {
  private initialState: QuotationWizardState = {
    generalInfo: null,
    locations: [],
    coverages: [],
    currentStep: 1
  };

  private wizardStateSubject = new BehaviorSubject<QuotationWizardState>(this.initialState);
  public wizardState$ = this.wizardStateSubject.asObservable();

  constructor() {}

  /**
   * Get current wizard state
   */
  getState(): QuotationWizardState {
    return this.wizardStateSubject.getValue();
  }

  /**
   * Update general info
   */
  setGeneralInfo(data: GeneralInfoFormData): void {
    const current = this.getState();
    current.generalInfo = data;
    this.wizardStateSubject.next({ ...current });
  }

  /**
   * Add location
   */
  addLocation(location: LocationFormData): void {
    const current = this.getState();
    current.locations.push(location);
    this.wizardStateSubject.next({ ...current });
  }

  /**
   * Update location
   */
  updateLocation(index: number, location: LocationFormData): void {
    const current = this.getState();
    if (index >= 0 && index < current.locations.length) {
      current.locations[index] = location;
      this.wizardStateSubject.next({ ...current });
    }
  }

  /**
   * Remove location
   */
  removeLocation(index: number): void {
    const current = this.getState();
    if (index >= 0 && index < current.locations.length) {
      current.locations.splice(index, 1);
      this.wizardStateSubject.next({ ...current });
    }
  }

  /**
   * Set all locations at once
   */
  setLocations(locations: LocationFormData[]): void {
    const current = this.getState();
    current.locations = locations;
    this.wizardStateSubject.next({ ...current });
  }

  /**
   * Add coverage
   */
  addCoverage(coverage: CoverageFormData): void {
    const current = this.getState();
    current.coverages.push(coverage);
    this.wizardStateSubject.next({ ...current });
  }

  /**
   * Update coverage
   */
  updateCoverage(index: number, coverage: CoverageFormData): void {
    const current = this.getState();
    if (index >= 0 && index < current.coverages.length) {
      current.coverages[index] = coverage;
      this.wizardStateSubject.next({ ...current });
    }
  }

  /**
   * Set all coverages at once
   */
  setCoverages(coverages: CoverageFormData[]): void {
    const current = this.getState();
    current.coverages = coverages;
    this.wizardStateSubject.next({ ...current });
  }

  /**
   * Move to step
   */
  goToStep(step: number): void {
    const current = this.getState();
    current.currentStep = step;
    this.wizardStateSubject.next({ ...current });
  }

  /**
   * Reset wizard state
   */
  reset(): void {
    this.wizardStateSubject.next({ ...this.initialState });
  }
}

