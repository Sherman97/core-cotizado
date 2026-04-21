import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TextFieldComponent } from '../../../../../shared/components/text-field/text-field.component';
import { SelectFieldComponent } from '../../../../../shared/components/select-field/select-field.component';
import { NumberFieldComponent } from '../../../../../shared/components/number-field/number-field.component';
import { CatalogService } from '../../../../../core/services/catalog.service';
import { OccupancyType, ConstructionType } from '../../../../../core/models/api.models';
import { LocationFormData } from '../../../../../core/models/form.models';

@Component({
  selector: 'app-step-locations',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TextFieldComponent, SelectFieldComponent, NumberFieldComponent],
  template: `
    <div class="step-container">
      <div class="step-header">
        <h2>Step 2: Locations & Properties</h2>
        <p class="step-description">Add and manage insured locations and their characteristics</p>
      </div>

      <div class="locations-list">
        <div *ngFor="let location of locations; let i = index" class="location-card">
          <div class="card-header">
            <h4>Location {{ i + 1 }}: {{ location.locationName || 'Unnamed' }}</h4>
            <button type="button" class="btn-remove" (click)="removeLocation(i)" *ngIf="locations.length > 1">
              ✕
            </button>
          </div>
          <div class="card-content">
            <p class="location-info">
              {{ location.city }}, {{ location.department }} - {{ location.postalCode || 'No postal code' }}
            </p>
            <p class="location-value">
              Insured Value: {{ location.insuredValue | currency }}
            </p>
          </div>
          <button type="button" class="btn btn-sm btn-edit" (click)="editLocation(i)">
            Edit
          </button>
        </div>
      </div>

      <button type="button" class="btn btn-secondary btn-add-location" (click)="toggleAddLocation()">
        + Add Location
      </button>

      <div *ngIf="showAddLocationForm" class="form-section">
        <h3>{{ editingIndex !== null ? 'Edit' : 'New' }} Location</h3>
        <form [formGroup]="locationForm" (ngSubmit)="saveLocation()">
          <div class="form-row">
            <div class="form-col">
              <app-text-field
                formControlName="locationName"
                label="Location Name"
                placeholder="e.g., Main Office, Branch 1"
                [required]="true"
              ></app-text-field>
            </div>
            <div class="form-col">
              <app-text-field
                formControlName="city"
                label="City"
                placeholder="e.g., Bogota"
                [required]="true"
              ></app-text-field>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <app-text-field
                formControlName="department"
                label="Department/State"
                placeholder="e.g., Cundinamarca"
                [required]="true"
              ></app-text-field>
            </div>
            <div class="form-col">
              <app-text-field
                formControlName="postalCode"
                label="Postal Code"
                placeholder="e.g., 110111"
              ></app-text-field>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col full">
              <app-text-field
                formControlName="address"
                label="Address"
                placeholder="Street address (optional)"
              ></app-text-field>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <app-select-field
                formControlName="occupancyType"
                label="Occupancy Type"
                [required]="true"
                [options]="occupancyOptions"
                hint="Risk classification based on property usage"
              ></app-select-field>
            </div>
            <div class="form-col">
              <app-select-field
                formControlName="constructionType"
                label="Construction Type"
                [required]="true"
                [options]="constructionOptions"
                hint="Building material and structure type"
              ></app-select-field>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col full">
              <app-number-field
                formControlName="insuredValue"
                label="Insured Value"
                placeholder="e.g., 1500000"
                [required]="true"
                [min]="1000"
                hint="Total value of property and contents to be insured"
              ></app-number-field>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-secondary" (click)="cancelEditLocation()">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary" [disabled]="locationForm.invalid">
              {{ editingIndex !== null ? 'Update' : 'Add' }} Location
            </button>
          </div>
        </form>
      </div>

      <div class="step-actions">
        <button type="button" class="btn btn-secondary" (click)="onPrevious()">
          ← Previous
        </button>
        <button
          type="button"
          class="btn btn-primary"
          [disabled]="locations.length === 0"
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

    .locations-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 1.5rem;
      margin: 2rem 0;
    }

    .location-card {
      background-color: white;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      overflow: hidden;
      transition: all 0.3s;
    }

    .location-card:hover {
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
      border-color: #3498db;
    }

    .card-header {
      background-color: #f8f9fa;
      padding: 1rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1px solid #e0e0e0;
    }

    .card-header h4 {
      margin: 0;
      color: #2c3e50;
      font-size: 1rem;
    }

    .btn-remove {
      background: none;
      border: none;
      color: #e74c3c;
      cursor: pointer;
      font-size: 1.5rem;
      padding: 0;
      width: 30px;
      height: 30px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      transition: all 0.3s;
    }

    .btn-remove:hover {
      background-color: #ffe6e6;
    }

    .card-content {
      padding: 1rem;
    }

    .location-info {
      margin: 0 0 0.5rem 0;
      color: #7f8c8d;
      font-size: 0.9rem;
    }

    .location-value {
      margin: 0;
      color: #27ae60;
      font-weight: 600;
      font-size: 0.95rem;
    }

    .btn-edit {
      width: 100%;
      background-color: #3498db;
      color: white;
      padding: 0.75rem;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 0.9rem;
    }

    .btn-edit:hover {
      background-color: #2980b9;
    }

    .btn-add-location {
      width: 100%;
      margin: 1rem 0;
      padding: 1rem;
      background-color: #27ae60;
      color: white;
    }

    .btn-add-location:hover {
      background-color: #229954;
    }

    .form-section {
      background-color: #f8f9fa;
      padding: 2rem;
      border-radius: 8px;
      margin: 2rem 0;
      border: 2px solid #3498db;
    }

    .form-section h3 {
      margin-top: 0;
      color: #2c3e50;
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
      gap: 1rem;
      margin-top: 1.5rem;
      padding-top: 1.5rem;
      border-top: 1px solid #ddd;
    }

    .form-actions button {
      flex: 1;
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

    .btn-sm {
      padding: 0.5rem 1rem;
      font-size: 0.875rem;
    }

    @media (max-width: 768px) {
      .form-row {
        grid-template-columns: 1fr;
        gap: 1rem;
      }

      .locations-list {
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
export class StepLocationsComponent implements OnInit {
  @Output() next = new EventEmitter<LocationFormData[]>();
  @Output() previous = new EventEmitter<void>();
  @Input() initialLocations: LocationFormData[] = [];

  locations: LocationFormData[] = [];
  showAddLocationForm: boolean = false;
  editingIndex: number | null = null;
  locationForm!: FormGroup;

  occupancyOptions: any[] = [];
  constructionOptions: any[] = [];

  constructor(
    private fb: FormBuilder,
    private catalogService: CatalogService
  ) {}

  ngOnInit(): void {
    this.locations = [...this.initialLocations];
    this.initializeLocationForm();
    this.loadCatalogs();
  }

  /**
   * Load catalogs from service
   */
  private loadCatalogs(): void {
    this.catalogService.getOccupancyTypes().subscribe(types => {
      this.occupancyOptions = types.map(t => ({
        value: t.code,
        label: t.name
      }));
    });

    this.catalogService.getConstructionTypes().subscribe(types => {
      this.constructionOptions = types.map(t => ({
        value: t.code,
        label: t.name
      }));
    });
  }

  /**
   * Initialize location form
   */
  private initializeLocationForm(): void {
    this.locationForm = this.fb.group({
      locationName: ['', [Validators.required, Validators.minLength(2)]],
      city: ['', [Validators.required, Validators.minLength(2)]],
      department: ['', [Validators.required, Validators.minLength(2)]],
      address: [''],
      postalCode: [''],
      occupancyType: ['', Validators.required],
      constructionType: ['', Validators.required],
      insuredValue: [null, [Validators.required, Validators.min(1000)]]
    });
  }

  /**
   * Toggle add location form visibility
   */
  toggleAddLocation(): void {
    this.showAddLocationForm = !this.showAddLocationForm;
    this.editingIndex = null;
    if (this.showAddLocationForm) {
      this.locationForm.reset();
    }
  }

  /**
   * Edit location
   */
  editLocation(index: number): void {
    this.editingIndex = index;
    this.showAddLocationForm = true;
    this.locationForm.patchValue(this.locations[index]);
  }

  /**
   * Save location (add or update)
   */
  saveLocation(): void {
    if (this.locationForm.valid) {
      const locationData = this.locationForm.value;

      if (this.editingIndex !== null) {
        this.locations[this.editingIndex] = locationData;
      } else {
        this.locations.push(locationData);
      }

      this.cancelEditLocation();
    }
  }

  /**
   * Cancel edit location
   */
  cancelEditLocation(): void {
    this.showAddLocationForm = false;
    this.editingIndex = null;
    this.locationForm.reset();
  }

  /**
   * Remove location
   */
  removeLocation(index: number): void {
    this.locations.splice(index, 1);
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
    this.next.emit(this.locations);
  }
}

