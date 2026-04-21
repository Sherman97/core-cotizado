import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TextFieldComponent } from '../../../../shared/components/text-field/text-field.component';
import { SelectFieldComponent } from '../../../../shared/components/select-field/select-field.component';
import { NumberFieldComponent } from '../../../../shared/components/number-field/number-field.component';
import { CoverageFormData } from '../../../../core/models/form.models';
import { CoverageType } from '../../../../core/models/api.models';

@Component({
  selector: 'app-step-coverages',
  standalone: true,
  imports: [CommonModule, FormsModule, TextFieldComponent, SelectFieldComponent, NumberFieldComponent],
  template: `
    <div class="step-container">
      <div class="step-header">
        <h2>Step 3: Coverage Options</h2>
        <p class="step-description">Select and configure coverage types and deductibles</p>
      </div>

      <div class="coverages-grid">
        <div *ngFor="let coverage of availableCoverages" class="coverage-card">
          <div class="card-header">
            <input
              type="checkbox"
              [id]="'coverage-' + coverage.code"
              [checked]="isCoverageSelected(coverage.code)"
              (change)="toggleCoverage(coverage)"
            />
            <label [for]="'coverage-' + coverage.code" class="card-title">
              {{ coverage.name }}
            </label>
          </div>
          <div class="card-content">
            <p class="card-description">{{ coverage.description }}</p>
            
            <div *ngIf="isCoverageSelected(coverage.code)" class="coverage-details">
              <div class="form-group">
                <label>Insured Limit</label>
                <input
                  type="number"
                  class="form-control"
                  [value]="getCoverageLimit(coverage.code)"
                  [max]="coverage.maxInsuredLimit ?? null"
                  (input)="updateCoverageLimit(coverage.code, $event)"
                />
                <small *ngIf="coverage.maxInsuredLimit">
                  Maximum: {{ coverage.maxInsuredLimit | currency }}
                </small>
              </div>

              <div class="form-group">
                <label>Deductible Type</label>
                <select class="form-control" [value]="getDeductibleType(coverage.code)" (change)="updateDeductibleType(coverage.code, $event)">
                  <option value="FIXED">Fixed Amount</option>
                  <option value="PERCENTAGE">Percentage</option>
                </select>
              </div>

              <div class="form-group">
                <label>Deductible Value</label>
                <input
                  type="number"
                  class="form-control"
                  [value]="getDeductibleValue(coverage.code)"
                  (input)="updateDeductibleValue(coverage.code, $event)"
                  [placeholder]="getDeductibleType(coverage.code) === 'FIXED' ? 'Amount' : 'Percentage'"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="selectedCoverages.length === 0" class="no-coverage-warning">
        <p>⚠ Please select at least one coverage to proceed</p>
      </div>

      <div class="selected-coverages-summary">
        <h3>Selected Coverages</h3>
        <div *ngIf="selectedCoverages.length === 0" class="empty-summary">
          No coverages selected
        </div>
        <div *ngIf="selectedCoverages.length > 0" class="coverage-list">
          <div *ngFor="let cov of selectedCoverages" class="coverage-item">
            <strong>{{ cov.coverageName }}</strong>
            <span class="coverage-limit">Limit: {{ cov.insuredLimit | currency }}</span>
            <span class="deductible">
              Deductible: {{ cov.deductibleValue }}{{ cov.deductibleType === 'PERCENTAGE' ? '%' : '' }}
            </span>
          </div>
        </div>
      </div>

      <div class="step-actions">
        <button type="button" class="btn btn-secondary" (click)="onPrevious()">
          ← Previous
        </button>
        <button
          type="button"
          class="btn btn-primary"
          [disabled]="selectedCoverages.length === 0"
          (click)="onNext()"
        >
          Next Step →
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

    .coverages-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 1.5rem;
      margin: 2rem 0;
    }

    .coverage-card {
      background-color: white;
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      overflow: hidden;
      transition: all 0.3s;
    }

    .coverage-card:hover {
      border-color: #3498db;
      box-shadow: 0 4px 12px rgba(52, 152, 219, 0.15);
    }

    .card-header {
      background-color: #f8f9fa;
      padding: 1rem;
      display: flex;
      align-items: center;
      gap: 1rem;
      border-bottom: 1px solid #e0e0e0;
    }

    .card-header input[type="checkbox"] {
      width: 20px;
      height: 20px;
      cursor: pointer;
    }

    .card-title {
      margin: 0;
      font-weight: 600;
      color: #2c3e50;
      cursor: pointer;
    }

    .card-content {
      padding: 1rem;
    }

    .card-description {
      margin: 0 0 1rem 0;
      color: #7f8c8d;
      font-size: 0.9rem;
    }

    .coverage-details {
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid #e0e0e0;
    }

    .form-group {
      margin-bottom: 1rem;
    }

    .form-group label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 500;
      color: #2c3e50;
      font-size: 0.9rem;
    }

    .form-control {
      width: 100%;
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.9rem;
    }

    .form-group small {
      display: block;
      margin-top: 0.25rem;
      color: #7f8c8d;
      font-size: 0.8rem;
    }

    .no-coverage-warning {
      background-color: #fff3cd;
      border: 1px solid #ffc107;
      border-radius: 4px;
      padding: 1rem;
      margin: 2rem 0;
      color: #856404;
    }

    .no-coverage-warning p {
      margin: 0;
    }

    .selected-coverages-summary {
      background-color: #f0f8ff;
      border: 1px solid #b3d9ff;
      border-radius: 8px;
      padding: 1.5rem;
      margin: 2rem 0;
    }

    .selected-coverages-summary h3 {
      margin-top: 0;
      color: #2c3e50;
    }

    .empty-summary {
      color: #7f8c8d;
      font-style: italic;
    }

    .coverage-list {
      display: grid;
      gap: 1rem;
    }

    .coverage-item {
      background-color: white;
      padding: 1rem;
      border-radius: 4px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 1rem;
    }

    .coverage-limit,
    .deductible {
      font-size: 0.9rem;
      color: #7f8c8d;
    }

    .coverage-limit::before {
      content: '• ';
      margin-right: 0.25rem;
    }

    .deductible::before {
      content: '• ';
      margin-right: 0.25rem;
    }

    .step-actions {
      display: flex;
      justify-content: space-between;
      margin-top: 2rem;
      padding-top: 2rem;
      border-top: 1px solid #ddd;
    }

    .btn {
      padding: 0.75rem 2rem;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 1rem;
      font-weight: 500;
      transition: all 0.3s;
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

    @media (max-width: 768px) {
      .coverages-grid {
        grid-template-columns: 1fr;
      }

      .coverage-item {
        flex-direction: column;
        align-items: flex-start;
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
export class StepCoveragesComponent implements OnInit {
  @Output() next = new EventEmitter<CoverageFormData[]>();
  @Output() previous = new EventEmitter<void>();
  @Input() initialCoverages: CoverageFormData[] = [];
  @Input() availableCoverages: CoverageType[] = [];

  selectedCoverages: CoverageFormData[] = [];

  constructor() {}

  ngOnInit(): void {
    this.selectedCoverages = [...this.initialCoverages];
  }

  /**
   * Check if coverage is selected
   */
  isCoverageSelected(code: string): boolean {
    return this.selectedCoverages.some(c => c.coverageCode === code);
  }

  /**
   * Toggle coverage selection
   */
  toggleCoverage(coverage: CoverageType): void {
    const existingIndex = this.selectedCoverages.findIndex(c => c.coverageCode === coverage.code);

    if (existingIndex >= 0) {
      this.selectedCoverages.splice(existingIndex, 1);
    } else {
      const newCoverage: CoverageFormData = {
        coverageCode: coverage.code,
        coverageName: coverage.name,
        insuredLimit: coverage.maxInsuredLimit || 1000000,
        deductibleType: 'FIXED',
        deductibleValue: 50000,
        selected: true
      };
      this.selectedCoverages.push(newCoverage);
    }
  }

  /**
   * Get coverage limit value
   */
  getCoverageLimit(code: string): number {
    const cov = this.selectedCoverages.find(c => c.coverageCode === code);
    return cov ? cov.insuredLimit : 0;
  }

  /**
   * Update coverage limit
   */
  updateCoverageLimit(code: string, event: any): void {
    const cov = this.selectedCoverages.find(c => c.coverageCode === code);
    if (cov) {
      cov.insuredLimit = Number(event.target.value);
    }
  }

  /**
   * Get deductible type
   */
  getDeductibleType(code: string): string {
    const cov = this.selectedCoverages.find(c => c.coverageCode === code);
    return cov ? cov.deductibleType : 'FIXED';
  }

  /**
   * Update deductible type
   */
  updateDeductibleType(code: string, event: any): void {
    const cov = this.selectedCoverages.find(c => c.coverageCode === code);
    if (cov) {
      cov.deductibleType = event.target.value;
    }
  }

  /**
   * Get deductible value
   */
  getDeductibleValue(code: string): number {
    const cov = this.selectedCoverages.find(c => c.coverageCode === code);
    return cov ? cov.deductibleValue : 0;
  }

  /**
   * Update deductible value
   */
  updateDeductibleValue(code: string, event: any): void {
    const cov = this.selectedCoverages.find(c => c.coverageCode === code);
    if (cov) {
      cov.deductibleValue = Number(event.target.value);
    }
  }

  /**
   * Move to previous step
   */
  onPrevious(): void {
    this.previous.emit();
  }

  /**
   * Move to next step
   */
  onNext(): void {
    this.next.emit(this.selectedCoverages);
  }
}
