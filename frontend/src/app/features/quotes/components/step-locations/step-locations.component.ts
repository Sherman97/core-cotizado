import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TextFieldComponent } from '../../../../shared/components/text-field/text-field.component';
import { SelectFieldComponent } from '../../../../shared/components/select-field/select-field.component';
import { NumberFieldComponent } from '../../../../shared/components/number-field/number-field.component';
import {
  CatalogService,
  GeographyCityCatalogItem,
  GeographyDepartmentCatalogItem,
  PostalCodeCatalogItem
} from '../../../../core/services/catalog.service';
import { OccupancyType, ConstructionType } from '../../../../core/models/api.models';
import { LocationFormData } from '../../../../core/models/form.models';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-step-locations',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TextFieldComponent, SelectFieldComponent, NumberFieldComponent],
  template: `
    <div class="step-container">
      <div class="step-header">
        <h2>Paso 2: Ubicaciones y propiedades</h2>
        <p class="step-description">Agrega y administra las ubicaciones aseguradas y sus características</p>
      </div>

      <div class="locations-list">
        <div *ngFor="let location of locations; let i = index" class="location-card">
          <div class="card-header">
            <h4>Ubicación {{ i + 1 }}: {{ location.locationName || 'Sin nombre' }}</h4>
            <button type="button" class="btn-remove" (click)="removeLocation(i)" *ngIf="locations.length > 1">
              ✕
            </button>
          </div>
          <div class="card-content">
            <p class="location-info">
              {{ location.city }}, {{ location.department }} - {{ location.postalCode || 'Sin código postal' }}
            </p>
            <p class="location-value">
              Valor asegurado: {{ location.insuredValue | currency }}
            </p>
          </div>
          <button type="button" class="btn btn-sm btn-edit" (click)="editLocation(i)">
            Editar
          </button>
        </div>
      </div>

      <button type="button" class="btn btn-secondary btn-add-location" (click)="toggleAddLocation()">
        + Agregar ubicación
      </button>

      <div *ngIf="showAddLocationForm" class="form-section">
        <h3>{{ editingIndex !== null ? 'Editar' : 'Nueva' }} ubicación</h3>
        <form [formGroup]="locationForm" (ngSubmit)="saveLocation()">
          <div class="form-row">
            <div class="form-col">
              <app-text-field
                formControlName="locationName"
                label="Nombre de la ubicación"
                placeholder="Ej.: Sede principal, Sucursal 1"
                [required]="true"
              ></app-text-field>
            </div>
            <div class="form-col">
              <app-select-field
                formControlName="department"
                label="Departamento"
                [options]="departmentOptions"
                [required]="true"
              ></app-select-field>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <app-select-field
                formControlName="city"
                label="Ciudad"
                [options]="cityOptions"
                [required]="true"
              ></app-select-field>
            </div>
            <div class="form-col">
              <app-select-field
                formControlName="postalCode"
                label="Código postal"
                [required]="true"
                [options]="postalCodeOptions"
                hint="Filtrado según la ciudad seleccionada"
              ></app-select-field>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col full">
              <app-text-field
                formControlName="address"
                label="Dirección"
                placeholder="Dirección (opcional)"
              ></app-text-field>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <app-select-field
                formControlName="occupancyType"
                label="Tipo de ocupación"
                [required]="true"
                [options]="occupancyOptions"
                hint="Clasificación de riesgo según el uso del inmueble"
              ></app-select-field>
            </div>
            <div class="form-col">
              <app-select-field
                formControlName="constructionType"
                label="Tipo de construcción"
                [required]="true"
                [options]="constructionOptions"
                hint="Material y tipo de estructura de la edificación"
              ></app-select-field>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col full">
              <app-number-field
                formControlName="insuredValue"
                label="Valor asegurado"
                placeholder="Ej.: 1500000"
                [required]="true"
                [min]="1000"
                hint="Valor total del inmueble y su contenido a asegurar"
              ></app-number-field>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-secondary" (click)="cancelEditLocation()">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary" [disabled]="locationForm.invalid">
              {{ editingIndex !== null ? 'Actualizar' : 'Agregar' }} ubicación
            </button>
          </div>
        </form>
      </div>

      <div class="step-actions">
        <button type="button" class="btn btn-secondary" (click)="onPrevious()">
          ← Anterior
        </button>
        <button
          type="button"
          class="btn btn-primary"
          [disabled]="locations.length === 0"
          (click)="onNext()"
        >
          Siguiente paso →
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
export class StepLocationsComponent implements OnInit, OnDestroy {
  @Output() next = new EventEmitter<LocationFormData[]>();
  @Output() previous = new EventEmitter<void>();
  @Input() initialLocations: LocationFormData[] = [];

  locations: LocationFormData[] = [];
  showAddLocationForm: boolean = false;
  editingIndex: number | null = null;
  locationForm!: FormGroup;

  occupancyOptions: Array<{ value: string; label: string }> = [];
  constructionOptions: Array<{ value: string; label: string }> = [];
  departmentOptions: Array<{ value: string; label: string }> = [];
  cityOptions: Array<{ value: string; label: string }> = [];
  postalCodeOptions: Array<{ value: string; label: string }> = [];
  private departmentsCatalog: GeographyDepartmentCatalogItem[] = [];
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private catalogService: CatalogService
  ) {}

  ngOnInit(): void {
    this.locations = [...this.initialLocations];
    this.initializeLocationForm();
    this.loadCatalogs();
    this.setupDependentSelectors();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Load catalogs from service
   */
  private loadCatalogs(): void {
    this.catalogService.getOccupancyTypes().pipe(takeUntil(this.destroy$)).subscribe((types: OccupancyType[]) => {
      this.occupancyOptions = types.map((t: OccupancyType) => ({
        value: t.code,
        label: t.name
      }));
    });

    this.catalogService.getConstructionTypes().pipe(takeUntil(this.destroy$)).subscribe((types: ConstructionType[]) => {
      this.constructionOptions = types.map((t: ConstructionType) => ({
        value: t.code,
        label: t.name
      }));
    });

    this.catalogService.getDepartments().pipe(takeUntil(this.destroy$)).subscribe((departments: GeographyDepartmentCatalogItem[]) => {
      this.departmentsCatalog = departments;
      this.departmentOptions = departments.map((item) => ({
        value: item.code,
        label: item.name
      }));
    });
  }

  private setupDependentSelectors(): void {
    this.locationForm.get('department')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((departmentCode: string) => {
        this.applyDepartmentSelection(departmentCode, true);
      });

    this.locationForm.get('city')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((cityCode: string) => {
        this.applyCitySelection(cityCode, true);
      });
  }

  private applyDepartmentSelection(departmentCode: string, resetChildren: boolean): void {
    const department = this.departmentsCatalog.find((item) => item.code === departmentCode);
    const cities = department?.cities ?? [];
    this.cityOptions = cities.map((city: GeographyCityCatalogItem) => ({
      value: city.code,
      label: city.name
    }));

    if (resetChildren) {
      this.locationForm.patchValue({ city: '', postalCode: '' }, { emitEvent: false });
      this.postalCodeOptions = [];
    }
  }

  private applyCitySelection(cityCode: string, resetPostalCode: boolean): void {
    this.catalogService.getPostalCodesByCity(cityCode).pipe(takeUntil(this.destroy$)).subscribe((codes: PostalCodeCatalogItem[]) => {
      this.postalCodeOptions = codes.map((item) => ({
        value: item.code,
        label: item.code
      }));

      if (resetPostalCode) {
        this.locationForm.patchValue({ postalCode: '' }, { emitEvent: false });
      }
    });
  }

  /**
   * Initialize location form
   */
  private initializeLocationForm(): void {
    this.locationForm = this.fb.group({
      locationName: ['', [Validators.required, Validators.minLength(2)]],
      city: ['', Validators.required],
      department: ['', Validators.required],
      address: [''],
      postalCode: ['', Validators.required],
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
    const location = this.locations[index];
    this.applyDepartmentSelection(location.department, false);
    this.applyCitySelection(location.city, false);
    this.locationForm.patchValue(location);
  }

  /**
   * Save location (add or update)
   */
  saveLocation(): void {
    const selectedPostalCode = this.locationForm.get('postalCode')?.value;
    if (selectedPostalCode && !this.isPostalCodeAllowed(selectedPostalCode)) {
      this.locationForm.get('postalCode')?.setErrors({ invalidPostalCode: true });
      return;
    }

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

  private isPostalCodeAllowed(postalCode: string): boolean {
    const selectedCity = this.locationForm.get('city')?.value;
    return this.postalCodeOptions.some((opt) => opt.value === postalCode) && !!selectedCity;
  }

  /**
   * Cancel edit location
   */
  cancelEditLocation(): void {
    this.showAddLocationForm = false;
    this.editingIndex = null;
    this.locationForm.reset();
    this.cityOptions = [];
    this.postalCodeOptions = [];
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
