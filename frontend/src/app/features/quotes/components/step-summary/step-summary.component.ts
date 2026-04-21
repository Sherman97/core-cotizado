import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GeneralInfoFormData, LocationFormData, CoverageFormData } from '../../../../../core/models/form.models';

@Component({
  selector: 'app-step-summary',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="step-container">
      <div class="step-header">
        <h2>Step 4: Review Summary</h2>
        <p class="step-description">Verify all information before calculation</p>
      </div>

      <div class="summary-sections">
        <!-- General Information Section -->
        <div class="summary-section">
          <div class="section-header" (click)="toggleSection('general')">
            <h3>General Information</h3>
            <button type="button" class="btn-expand">
              {{ expandedSections.general ? '▼' : '▶' }}
            </button>
          </div>
          <div *ngIf="expandedSections.general" class="section-content">
            <div class="summary-item">
              <span class="label">Customer Name:</span>
              <span class="value">{{ generalInfo?.customerName }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Currency:</span>
              <span class="value">{{ generalInfo?.currency }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Observations:</span>
              <span class="value">{{ generalInfo?.observations || 'None' }}</span>
            </div>
            <button type="button" class="btn btn-sm btn-edit" (click)="onEditStep(1)">
              Edit
            </button>
          </div>
        </div>

        <!-- Locations Section -->
        <div class="summary-section">
          <div class="section-header" (click)="toggleSection('locations')">
            <h3>Locations ({{ locations?.length || 0 }})</h3>
            <button type="button" class="btn-expand">
              {{ expandedSections.locations ? '▼' : '▶' }}
            </button>
          </div>
          <div *ngIf="expandedSections.locations" class="section-content">
            <div *ngFor="let loc of locations; let i = index" class="location-summary">
              <h4>Location {{ i + 1 }}: {{ loc.locationName }}</h4>
              <div class="summary-grid">
                <div class="summary-item">
                  <span class="label">City:</span>
                  <span class="value">{{ loc.city }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Department:</span>
                  <span class="value">{{ loc.department }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Postal Code:</span>
                  <span class="value">{{ loc.postalCode || 'N/A' }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Address:</span>
                  <span class="value">{{ loc.address || 'N/A' }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Occupancy:</span>
                  <span class="value badge">{{ loc.occupancyType }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Construction:</span>
                  <span class="value badge">{{ loc.constructionType }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Insured Value:</span>
                  <span class="value price">{{ loc.insuredValue | currency }}</span>
                </div>
              </div>
            </div>
            <button type="button" class="btn btn-sm btn-edit" (click)="onEditStep(2)">
              Edit
            </button>
          </div>
        </div>

        <!-- Coverages Section -->
        <div class="summary-section">
          <div class="section-header" (click)="toggleSection('coverages')">
            <h3>Coverages ({{ coverages?.length || 0 }})</h3>
            <button type="button" class="btn-expand">
              {{ expandedSections.coverages ? '▼' : '▶' }}
            </button>
          </div>
          <div *ngIf="expandedSections.coverages" class="section-content">
            <div *ngFor="let cov of coverages" class="coverage-summary">
              <div class="coverage-header">
                <h4>{{ cov.coverageName }}</h4>
                <span class="badge badge-success">Selected</span>
              </div>
              <div class="summary-grid">
                <div class="summary-item">
                  <span class="label">Coverage Code:</span>
                  <span class="value">{{ cov.coverageCode }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Insured Limit:</span>
                  <span class="value price">{{ cov.insuredLimit | currency }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Deductible Type:</span>
                  <span class="value">{{ cov.deductibleType }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Deductible Value:</span>
                  <span class="value">
                    {{ cov.deductibleValue }}{{ cov.deductibleType === 'PERCENTAGE' ? '%' : '' }}
                  </span>
                </div>
              </div>
            </div>
            <button type="button" class="btn btn-sm btn-edit" (click)="onEditStep(3)">
              Edit
            </button>
          </div>
        </div>
      </div>

      <!-- Validation Status -->
      <div class="validation-status">
        <div class="status-item" [class.valid]="isGeneralInfoValid()">
          <span class="status-icon">{{ isGeneralInfoValid() ? '✓' : '✗' }}</span>
          General Information
        </div>
        <div class="status-item" [class.valid]="isLocationsValid()">
          <span class="status-icon">{{ isLocationsValid() ? '✓' : '✗' }}</span>
          Locations
        </div>
        <div class="status-item" [class.valid]="isCoveragesValid()">
          <span class="status-icon">{{ isCoveragesValid() ? '✓' : '✗' }}</span>
          Coverages
        </div>
      </div>

      <div class="step-actions">
        <button type="button" class="btn btn-secondary" (click)="onPrevious()">
          ← Previous
        </button>
        <button
          type="button"
          class="btn btn-success btn-lg"
          [disabled]="!isFormValid()"
          (click)="onCalculate()"
        >
          🔢 Calculate Quote
        </button>
      </div>
    </div>
  `,
  styles: [`
    .step-container {
      padding: 2rem;
    }

    .step-header {
      margin-bottom: 2rem;
    }

    .step-header h2 {
      margin: 0 0 0.5rem 0;
      color: #2c3e50;
      font-size: 1.75rem;
    }

    .step-description {
      color: #7f8c8d;
      margin: 0;
      font-size: 0.95rem;
    }

    .summary-sections {
      margin: 2rem 0;
    }

    .summary-section {
      background-color: white;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      margin-bottom: 1rem;
      overflow: hidden;
    }

    .section-header {
      background-color: #f8f9fa;
      padding: 1rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      cursor: pointer;
      user-select: none;
      transition: background-color 0.3s;
    }

    .section-header:hover {
      background-color: #ecf0f1;
    }

    .section-header h3 {
      margin: 0;
      color: #2c3e50;
      font-size: 1.1rem;
    }

    .btn-expand {
      background: none;
      border: none;
      cursor: pointer;
      font-size: 1rem;
      color: #7f8c8d;
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .section-content {
      padding: 1.5rem;
      border-top: 1px solid #e0e0e0;
      background-color: #fafbfc;
    }

    .summary-item {
      display: flex;
      justify-content: space-between;
      padding: 0.5rem 0;
      align-items: center;
    }

    .summary-item .label {
      font-weight: 600;
      color: #2c3e50;
      min-width: 150px;
    }

    .summary-item .value {
      color: #555;
      text-align: right;
      flex: 1;
    }

    .summary-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
      margin-bottom: 1rem;
    }

    .location-summary,
    .coverage-summary {
      background-color: white;
      padding: 1rem;
      border-radius: 4px;
      border: 1px solid #ecf0f1;
      margin-bottom: 1rem;
    }

    .location-summary h4,
    .coverage-summary h4 {
      margin: 0 0 1rem 0;
      color: #2c3e50;
    }

    .coverage-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }

    .badge {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 600;
      background-color: #e3f2fd;
      color: #1976d2;
    }

    .badge-success {
      background-color: #d4edda;
      color: #155724;
    }

    .price {
      color: #27ae60;
      font-weight: 600;
    }

    .btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 1rem;
      font-weight: 500;
      transition: all 0.3s;
    }

    .btn-sm {
      padding: 0.5rem 1rem;
      font-size: 0.875rem;
    }

    .btn-edit {
      background-color: #3498db;
      color: white;
      margin-top: 1rem;
    }

    .btn-edit:hover {
      background-color: #2980b9;
    }

    .validation-status {
      background-color: #f0f8ff;
      border: 1px solid #b3d9ff;
      border-radius: 8px;
      padding: 1.5rem;
      margin: 2rem 0;
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 1rem;
    }

    .status-item {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      padding: 0.75rem;
      border-radius: 4px;
      background-color: white;
      color: #7f8c8d;
    }

    .status-item.valid {
      background-color: #d4edda;
      color: #155724;
    }

    .status-icon {
      font-weight: 700;
      font-size: 1.2rem;
    }

    .step-actions {
      display: flex;
      justify-content: space-between;
      margin-top: 2rem;
      padding-top: 2rem;
      border-top: 1px solid #ddd;
    }

    .btn-primary {
      background-color: #3498db;
      color: white;
    }

    .btn-primary:hover:not(:disabled) {
      background-color: #2980b9;
    }

    .btn-primary:disabled {
      background-color: #bdc3c7;
      cursor: not-allowed;
    }

    .btn-secondary {
      background-color: #95a5a6;
      color: white;
    }

    .btn-secondary:hover {
      background-color: #7f8c8d;
    }

    .btn-success {
      background-color: #27ae60;
      color: white;
    }

    .btn-success:hover:not(:disabled) {
      background-color: #229954;
    }

    .btn-success:disabled {
      background-color: #bdc3c7;
      cursor: not-allowed;
    }

    .btn-lg {
      padding: 1rem 2rem;
      font-size: 1.1rem;
    }

    @media (max-width: 768px) {
      .summary-grid {
        grid-template-columns: 1fr;
      }

      .validation-status {
        grid-template-columns: 1fr;
      }

      .step-actions {
        flex-direction: column;
        gap: 1rem;
      }

      .step-actions button {
        width: 100%;
      }
    }
  `]
})
export class StepSummaryComponent {
  @Output() calculate = new EventEmitter<void>();
  @Output() previous = new EventEmitter<void>();
  @Output() editStep = new EventEmitter<number>();
  @Input() generalInfo: GeneralInfoFormData | null = null;
  @Input() locations: LocationFormData[] = [];
  @Input() coverages: CoverageFormData[] = [];

  expandedSections = {
    general: true,
    locations: true,
    coverages: true
  };

  /**
   * Toggle section expansion
   */
  toggleSection(section: keyof typeof this.expandedSections): void {
    this.expandedSections[section] = !this.expandedSections[section];
  }

  /**
   * Validate general info
   */
  isGeneralInfoValid(): boolean {
    return this.generalInfo !== null && !!this.generalInfo.customerName;
  }

  /**
   * Validate locations
   */
  isLocationsValid(): boolean {
    return this.locations && this.locations.length > 0;
  }

  /**
   * Validate coverages
   */
  isCoveragesValid(): boolean {
    return this.coverages && this.coverages.length > 0;
  }

  /**
   * Check if entire form is valid
   */
  isFormValid(): boolean {
    return this.isGeneralInfoValid() && this.isLocationsValid() && this.isCoveragesValid();
  }

  /**
   * Move to previous step
   */
  onPrevious(): void {
    this.previous.emit();
  }

  /**
   * Move to step for editing
   */
  onEditStep(step: number): void {
    this.editStep.emit(step);
  }

  /**
   * Trigger calculation
   */
  onCalculate(): void {
    if (this.isFormValid()) {
      this.calculate.emit();
    }
  }
}

