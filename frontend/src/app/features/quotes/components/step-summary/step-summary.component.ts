import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GeneralInfoFormData, LocationFormData, CoverageFormData } from '../../../../core/models/form.models';

@Component({
  selector: 'app-step-summary',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="step-container">
      <div class="step-header">
        <h2>Paso 4: Resumen de revisión</h2>
        <p class="step-description">Verifica toda la información antes del cálculo</p>
      </div>

      <div class="summary-sections">
        <!-- Sección de información general -->
        <div class="summary-section">
          <div class="section-header" (click)="toggleSection('general')">
            <h3>Información general</h3>
            <button type="button" class="btn-expand">
              {{ expandedSections.general ? '▼' : '▶' }}
            </button>
          </div>
          <div *ngIf="expandedSections.general" class="section-content">
            <div class="summary-item">
              <span class="label">Nombre del cliente:</span>
              <span class="value">{{ generalInfo?.customerName }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Moneda:</span>
              <span class="value">{{ generalInfo?.currency }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Asesor:</span>
              <span class="value">{{ generalInfo?.agentNameSnapshot || generalInfo?.agentCode || 'No seleccionado' }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Clasificación de riesgo:</span>
              <span class="value">{{ generalInfo?.riskClassification || 'N/D' }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Tipo de negocio:</span>
              <span class="value">{{ generalInfo?.businessType || 'N/D' }}</span>
            </div>
            <div class="summary-item">
              <span class="label">Observaciones:</span>
              <span class="value">{{ generalInfo?.observations || 'Ninguna' }}</span>
            </div>
            <button type="button" class="btn btn-sm btn-edit" (click)="onEditStep(1)">
              Editar
            </button>
          </div>
        </div>

        <!-- Sección de ubicaciones -->
        <div class="summary-section">
          <div class="section-header" (click)="toggleSection('locations')">
            <h3>Ubicaciones ({{ locations.length }})</h3>
            <button type="button" class="btn-expand">
              {{ expandedSections.locations ? '▼' : '▶' }}
            </button>
          </div>
          <div *ngIf="expandedSections.locations" class="section-content">
            <div *ngFor="let loc of locations; let i = index" class="location-summary">
              <h4>Ubicación {{ i + 1 }}: {{ loc.locationName }}</h4>
              <div class="summary-grid">
                <div class="summary-item">
                  <span class="label">Ciudad:</span>
                  <span class="value">{{ loc.city }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Departamento:</span>
                  <span class="value">{{ loc.department }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Código postal:</span>
                  <span class="value">{{ loc.postalCode || 'N/D' }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Dirección:</span>
                  <span class="value">{{ loc.address || 'N/D' }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Ocupación:</span>
                  <span class="value badge">{{ loc.occupancyType }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Construcción:</span>
                  <span class="value badge">{{ loc.constructionType }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Nivel construcción:</span>
                  <span class="value">{{ loc.constructionLevel ?? 'N/D' }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Año construcción:</span>
                  <span class="value">{{ loc.constructionYear ?? 'N/D' }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Fire key:</span>
                  <span class="value">{{ loc.fireKey || 'N/D' }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Valor asegurado:</span>
                  <span class="value price">{{ loc.insuredValue | currency }}</span>
                </div>
              </div>
            </div>
            <button type="button" class="btn btn-sm btn-edit" (click)="onEditStep(2)">
              Editar
            </button>
          </div>
        </div>

        <!-- Sección de coberturas -->
        <div class="summary-section">
          <div class="section-header" (click)="toggleSection('coverages')">
            <h3>Coberturas ({{ coverages.length }})</h3>
            <button type="button" class="btn-expand">
              {{ expandedSections.coverages ? '▼' : '▶' }}
            </button>
          </div>
          <div *ngIf="expandedSections.coverages" class="section-content">
            <div *ngFor="let cov of coverages" class="coverage-summary">
              <div class="coverage-header">
                <h4>{{ cov.coverageName }}</h4>
                <span class="badge badge-success">Seleccionada</span>
              </div>
              <div class="summary-grid">
                <div class="summary-item">
                  <span class="label">Código de cobertura:</span>
                  <span class="value">{{ cov.coverageCode }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Límite asegurado:</span>
                  <span class="value price">{{ cov.insuredLimit | currency }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Tipo de deducible:</span>
                  <span class="value">{{ cov.deductibleType }}</span>
                </div>
                <div class="summary-item">
                  <span class="label">Valor del deducible:</span>
                  <span class="value">
                    {{ cov.deductibleValue }}{{ cov.deductibleType === 'PERCENTAGE' ? '%' : '' }}
                  </span>
                </div>
              </div>
            </div>
            <button type="button" class="btn btn-sm btn-edit" (click)="onEditStep(3)">
              Editar
            </button>
          </div>
        </div>
      </div>

      <!-- Estado de validación -->
      <div class="validation-status">
        <div class="status-item" [class.valid]="isGeneralInfoValid()">
          <span class="status-icon">{{ isGeneralInfoValid() ? '✓' : '✗' }}</span>
          Información general
        </div>
        <div class="status-item" [class.valid]="isLocationsValid()">
          <span class="status-icon">{{ isLocationsValid() ? '✓' : '✗' }}</span>
          Ubicaciones
        </div>
        <div class="status-item" [class.valid]="isCoveragesValid()">
          <span class="status-icon">{{ isCoveragesValid() ? '✓' : '✗' }}</span>
          Coberturas
        </div>
      </div>

      <div class="step-actions">
        <button type="button" class="btn btn-secondary" (click)="onPrevious()">
          ← Anterior
        </button>
        <button
          type="button"
          class="btn btn-success btn-lg"
          [disabled]="!isFormValid()"
          (click)="onCalculate()"
        >
          🔢 Calcular cotización
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
    return !!(
      this.generalInfo &&
      this.generalInfo.customerName &&
      this.generalInfo.currency &&
      this.generalInfo.riskClassification &&
      this.generalInfo.businessType
    );
  }

  /**
   * Validate locations
   */
  isLocationsValid(): boolean {
    return this.locations.length > 0 && this.locations.every((loc) =>
      !!(
        loc.locationName &&
        loc.city &&
        loc.department &&
        loc.postalCode &&
        loc.occupancyType &&
        loc.constructionType &&
        loc.constructionLevel &&
        loc.constructionLevel > 0 &&
        loc.constructionYear &&
        loc.constructionYear >= 1900 &&
        loc.fireKey &&
        loc.insuredValue > 0
      )
    );
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
