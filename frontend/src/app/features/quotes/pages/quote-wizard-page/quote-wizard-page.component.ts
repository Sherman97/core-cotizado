import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { StepGeneralInfoComponent } from '../components/step-general-info/step-general-info.component';
import { StepLocationsComponent } from '../components/step-locations/step-locations.component';
import { StepCoveragesComponent } from '../components/step-coverages/step-coverages.component';
import { StepSummaryComponent } from '../components/step-summary/step-summary.component';

import { QuotationWizardService } from '../../../core/services/quotation-wizard.service';
import { QuoteApiService } from '../../../core/services/quote-api.service';
import { QuotationWizardState, GeneralInfoFormData, LocationFormData, CoverageFormData } from '../../../core/models/form.models';
import { Location as LocationModel } from '../../../core/models/api.models';

@Component({
  selector: 'app-quote-wizard-page',
  standalone: true,
  imports: [
    CommonModule,
    StepGeneralInfoComponent,
    StepLocationsComponent,
    StepCoveragesComponent,
    StepSummaryComponent
  ],
  template: `
    <div class="wizard-container">
      <!-- Wizard Header -->
      <div class="wizard-header">
        <h1>Create Quotation</h1>
        <p class="folio-info">Folio: <strong>{{ folio }}</strong></p>
      </div>

      <!-- Step Indicators -->
      <div class="step-indicators">
        <div
          *ngFor="let step of steps; let i = index"
          [class]="'step-indicator ' + (i + 1 <= currentStep ? 'active' : '') + (i + 1 < currentStep ? ' completed' : '')"
          (click)="navigateToStep(i + 1)"
        >
          <div class="step-number">
            <span *ngIf="i + 1 < currentStep" class="checkmark">✓</span>
            <span *ngIf="i + 1 >= currentStep">{{ i + 1 }}</span>
          </div>
          <div class="step-title">{{ step }}</div>
        </div>
      </div>

      <!-- Loading State -->
      <div *ngIf="loading" class="loading-state">
        <p>Processing your quotation...</p>
        <div class="spinner"></div>
      </div>

      <!-- Error State -->
      <div *ngIf="errorMessage" class="error-alert">
        <strong>Error:</strong> {{ errorMessage }}
        <button type="button" class="btn-close" (click)="clearError()">×</button>
      </div>

      <!-- Step Content -->
      <div class="step-content" *ngIf="!loading">
        <!-- Step 1: General Info -->
        <app-step-general-info
          *ngIf="currentStep === 1"
          (next)="onGeneralInfoNext($event)"
          (cancel)="onCancel()"
        ></app-step-general-info>

        <!-- Step 2: Locations -->
        <app-step-locations
          *ngIf="currentStep === 2"
          [initialLocations]="wizardState.locations"
          (next)="onLocationsNext($event)"
          (previous)="previousStep()"
        ></app-step-locations>

        <!-- Step 3: Coverages -->
        <app-step-coverages
          *ngIf="currentStep === 3"
          [initialCoverages]="wizardState.coverages"
          (next)="onCoveragesNext($event)"
          (previous)="previousStep()"
        ></app-step-coverages>

        <!-- Step 4: Summary -->
        <app-step-summary
          *ngIf="currentStep === 4"
          [generalInfo]="wizardState.generalInfo"
          [locations]="wizardState.locations"
          [coverages]="wizardState.coverages"
          (calculate)="onCalculate()"
          (previous)="previousStep()"
          (editStep)="navigateToStep($event)"
        ></app-step-summary>
      </div>

      <!-- Success Message -->
      <div *ngIf="successMessage" class="success-alert">
        {{ successMessage }}
      </div>
    </div>
  `,
  styles: [`
    .wizard-container {
      max-width: 900px;
      margin: 0 auto;
      padding: 2rem;
    }

    .wizard-header {
      text-align: center;
      margin-bottom: 2rem;
    }

    .wizard-header h1 {
      margin: 0 0 0.5rem 0;
      color: #2c3e50;
      font-size: 2rem;
    }

    .folio-info {
      margin: 0;
      color: #7f8c8d;
      font-size: 0.95rem;
    }

    .step-indicators {
      display: flex;
      justify-content: space-between;
      margin-bottom: 2rem;
      position: relative;
    }

    .step-indicators::before {
      content: '';
      position: absolute;
      top: 20px;
      left: 0;
      right: 0;
      height: 2px;
      background-color: #ecf0f1;
      z-index: 0;
    }

    .step-indicator {
      position: relative;
      z-index: 1;
      text-align: center;
      cursor: pointer;
      flex: 1;
    }

    .step-number {
      width: 40px;
      height: 40px;
      background-color: white;
      border: 2px solid #bdc3c7;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 0.5rem;
      font-weight: 600;
      color: #7f8c8d;
      transition: all 0.3s;
    }

    .step-indicator.active .step-number {
      background-color: #3498db;
      border-color: #3498db;
      color: white;
    }

    .step-indicator.completed .step-number {
      background-color: #27ae60;
      border-color: #27ae60;
      color: white;
    }

    .step-indicator.completed::before {
      content: '';
      position: absolute;
      left: 0;
      right: 100%;
      height: 2px;
      background-color: #27ae60;
      top: 19px;
      z-index: -1;
    }

    .step-title {
      font-size: 0.9rem;
      color: #7f8c8d;
      font-weight: 500;
    }

    .step-indicator.active .step-title {
      color: #3498db;
      font-weight: 600;
    }

    .step-content {
      background-color: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      animation: fadeIn 0.3s ease-in;
    }

    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .loading-state {
      text-align: center;
      padding: 3rem;
      background-color: white;
      border-radius: 8px;
    }

    .loading-state p {
      margin-bottom: 1rem;
      color: #2c3e50;
      font-size: 1.1rem;
    }

    .spinner {
      width: 40px;
      height: 40px;
      margin: 0 auto;
      border: 4px solid #ecf0f1;
      border-top-color: #3498db;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      to {
        transform: rotate(360deg);
      }
    }

    .error-alert {
      background-color: #f8d7da;
      border: 1px solid #f5c6cb;
      border-radius: 4px;
      padding: 1rem;
      margin-bottom: 1rem;
      color: #721c24;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .success-alert {
      background-color: #d4edda;
      border: 1px solid #c3e6cb;
      border-radius: 4px;
      padding: 1rem;
      margin-top: 1rem;
      color: #155724;
      text-align: center;
    }

    .btn-close {
      background: none;
      border: none;
      cursor: pointer;
      font-size: 1.5rem;
      color: #721c24;
    }

    @media (max-width: 768px) {
      .wizard-container {
        padding: 1rem;
      }

      .step-indicators {
        flex-wrap: wrap;
        gap: 1rem;
      }

      .step-indicator {
        flex-basis: calc(50% - 0.5rem);
      }

      .step-title {
        font-size: 0.8rem;
      }
    }
  `]
})
export class QuoteWizardPageComponent implements OnInit, OnDestroy {
  folio: string = '';
  currentStep: number = 1;
  steps = ['General Info', 'Locations', 'Coverages', 'Review'];
  wizardState: QuotationWizardState = {
    generalInfo: null,
    locations: [],
    coverages: [],
    currentStep: 1
  };

  loading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private wizardService: QuotationWizardService,
    private quoteApi: QuoteApiService
  ) {}

  ngOnInit(): void {
    this.folio = this.route.snapshot.paramMap.get('folio') || '';
    this.loadQuotationState();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load existing quotation state
   */
  private loadQuotationState(): void {
    this.wizardService.wizardState$
      .pipe(takeUntil(this.destroy$))
      .subscribe(state => {
        this.wizardState = state;
        this.currentStep = state.currentStep;
      });
  }

  /**
   * Navigate to specific step
   */
  navigateToStep(step: number): void {
    if (step <= this.currentStep) {
      this.wizardService.goToStep(step);
    }
  }

  /**
   * Previous step
   */
  previousStep(): void {
    if (this.currentStep > 1) {
      this.wizardService.goToStep(this.currentStep - 1);
    }
  }

  /**
   * Handle general info completion
   */
  onGeneralInfoNext(data: GeneralInfoFormData): void {
    this.wizardService.setGeneralInfo(data);
    this.saveGeneralInfoToBackend(data);
    this.wizardService.goToStep(2);
  }

  /**
   * Handle locations completion
   */
  onLocationsNext(locations: LocationFormData[]): void {
    this.wizardService.setLocations(locations);
    this.saveLocationsToBackend(locations);
    this.wizardService.goToStep(3);
  }

  /**
   * Handle coverages completion
   */
  onCoveragesNext(coverages: CoverageFormData[]): void {
    this.wizardService.setCoverages(coverages);
    this.saveCoveragesToBackend(coverages);
    this.wizardService.goToStep(4);
  }

  /**
   * Calculate quote
   */
  onCalculate(): void {
    this.loading = true;
    this.quoteApi.calculateQuote(this.folio).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = 'Quote calculated successfully!';
        setTimeout(() => {
          this.router.navigate(['/quotes', this.folio, 'result']);
        }, 1000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error calculating quote: ' + (err.error?.message || err.message);
        console.error('Calculation error:', err);
      }
    });
  }

  /**
   * Save general info to backend
   */
  private saveGeneralInfoToBackend(data: GeneralInfoFormData): void {
    this.quoteApi.saveGeneralInfo(this.folio, data).subscribe({
      error: (err) => {
        console.error('Error saving general info:', err);
      }
    });
  }

  /**
   * Save locations to backend
   */
  private saveLocationsToBackend(locations: LocationFormData[]): void {
    const payload = {
      locations: locations.map((loc, idx) => ({
        ...loc,
        indice: idx + 1
      }))
    };

    this.quoteApi.saveLocations(this.folio, payload).subscribe({
      error: (err) => {
        console.error('Error saving locations:', err);
      }
    });
  }

  /**
   * Save coverages to backend
   */
  private saveCoveragesToBackend(coverages: CoverageFormData[]): void {
    const payload = {
      coverages: coverages.map(cov => ({
        ...cov,
        selected: true
      }))
    };

    this.quoteApi.saveCoverageOptions(this.folio, payload).subscribe({
      error: (err) => {
        console.error('Error saving coverages:', err);
      }
    });
  }

  /**
   * Cancel wizard
   */
  onCancel(): void {
    this.router.navigate(['/']);
  }

  /**
   * Clear error message
   */
  clearError(): void {
    this.errorMessage = '';
  }
}

