import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TextFieldComponent } from '../../../../../shared/components/text-field/text-field.component';
import { SelectFieldComponent } from '../../../../../shared/components/select-field/select-field.component';

@Component({
  selector: 'app-step-general-info',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TextFieldComponent, SelectFieldComponent],
  template: `
    <div class="step-container">
      <div class="step-header">
        <h2>Step 1: General Information</h2>
        <p class="step-description">Provide basic quotation and applicant details</p>
      </div>

      <form [formGroup]="form" class="form-section">
        <div class="form-row">
          <div class="form-col">
            <app-text-field
              formControlName="customerName"
              label="Customer Name"
              placeholder="Enter customer or business name"
              [required]="true"
              hint="Full legal name of the applicant"
            ></app-text-field>
          </div>
          <div class="form-col">
            <app-select-field
              formControlName="currency"
              label="Currency"
              [required]="true"
              [options]="currencyOptions"
            ></app-select-field>
          </div>
        </div>

        <div class="form-row">
          <div class="form-col full">
            <app-text-field
              formControlName="observations"
              label="Observations"
              placeholder="Add any additional notes about this quotation"
              hint="Optional: Any special requirements or comments"
            ></app-text-field>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-secondary" (click)="onCancel()">
            Cancel
          </button>
          <button
            type="button"
            class="btn btn-primary"
            [disabled]="form.invalid"
            (click)="onNext()"
          >
            Next Step →
          </button>
        </div>
      </form>

      <div *ngIf="form.invalid && form.touched" class="validation-errors">
        <div *ngIf="form.get('customerName')?.hasError('required')" class="error-message">
          Customer name is required
        </div>
        <div *ngIf="form.get('currency')?.hasError('required')" class="error-message">
          Currency selection is required
        </div>
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

    .form-section {
      background-color: #f8f9fa;
      padding: 2rem;
      border-radius: 8px;
      margin-bottom: 2rem;
    }

    .form-row {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 2rem;
      margin-bottom: 1rem;
    }

    .form-row.full {
      grid-template-columns: 1fr;
    }

    .form-col {
      display: flex;
      flex-direction: column;
    }

    .form-col.full {
      grid-column: 1 / -1;
    }

    .form-actions {
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

    .validation-errors {
      margin-top: 1rem;
      padding: 1rem;
      background-color: #f8d7da;
      border: 1px solid #f5c6cb;
      border-radius: 4px;
      color: #721c24;
    }

    .error-message {
      margin: 0.5rem 0;
    }

    .error-message:first-child {
      margin-top: 0;
    }

    @media (max-width: 768px) {
      .form-row {
        grid-template-columns: 1fr;
        gap: 1rem;
      }

      .form-actions {
        flex-direction: column;
        gap: 1rem;
      }

      .btn {
        width: 100%;
      }
    }
  `]
})
export class StepGeneralInfoComponent implements OnInit {
  @Output() next = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;

  currencyOptions = [
    { value: 'COP', label: 'Colombian Peso (COP)' },
    { value: 'USD', label: 'US Dollar (USD)' },
    { value: 'MXN', label: 'Mexican Peso (MXN)' }
  ];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  /**
   * Initialize form with validators
   */
  private initializeForm(): void {
    this.form = this.fb.group({
      productCode: ['DANOS', Validators.required],
      customerName: ['', [Validators.required, Validators.minLength(3)]],
      currency: ['COP', Validators.required],
      observations: ['']
    });
  }

  /**
   * Emit next event with form data
   */
  onNext(): void {
    if (this.form.valid) {
      this.next.emit(this.form.value);
    }
  }

  /**
   * Emit cancel event
   */
  onCancel(): void {
    this.cancel.emit();
  }
}

