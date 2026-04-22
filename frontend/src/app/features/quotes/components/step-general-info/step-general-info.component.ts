import { Component, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TextFieldComponent } from '../../../../shared/components/text-field/text-field.component';
import { SelectFieldComponent } from '../../../../shared/components/select-field/select-field.component';
import { GeneralInfoFormData } from '../../../../core/models/form.models';
import { Agent } from '../../../../core/models/api.models';
import { AgentApiService } from '../../../../core/services/agent-api.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-step-general-info',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, TextFieldComponent, SelectFieldComponent],
  template: `
    <div class="step-container">
      <div class="step-header">
        <h2>Paso 1: Información general</h2>
        <p class="step-description">Ingresa los datos básicos de la cotización y del solicitante</p>
      </div>

      <form [formGroup]="form" class="form-section">
        <div class="form-row">
          <div class="form-col">
            <app-text-field
              formControlName="customerName"
              label="Nombre del cliente"
              placeholder="Ingresa el nombre del cliente o empresa"
              [required]="true"
              hint="Nombre legal completo del solicitante"
            ></app-text-field>
          </div>
          <div class="form-col">
            <app-select-field
              formControlName="currency"
              label="Moneda"
              [required]="true"
              [options]="currencyOptions"
            ></app-select-field>
          </div>
        </div>

        <div class="form-row">
          <div class="form-col">
            <label for="agent-search" class="form-label">Buscar asesor</label>
            <input
              id="agent-search"
              type="text"
              class="agent-search-input"
              placeholder="Escribe código o nombre"
              (input)="onAgentSearchChange($event)"
            />
            <small class="search-hint">Filtra asesores activos del catálogo</small>
          </div>
          <div class="form-col">
            <app-select-field
              formControlName="agentCode"
              label="Asesor"
              placeholder="Selecciona un asesor"
              [required]="false"
              [options]="agentOptions"
            ></app-select-field>
            <small *ngIf="selectedAgentName" class="agent-selected-info">
              Asesor seleccionado: {{ selectedAgentName }}
            </small>
          </div>
        </div>

        <div class="form-row">
          <div class="form-col">
            <app-select-field
              formControlName="riskClassification"
              label="Clasificación de riesgo"
              [required]="true"
              [options]="riskClassificationOptions"
            ></app-select-field>
          </div>
          <div class="form-col">
            <app-select-field
              formControlName="businessType"
              label="Tipo de negocio"
              [required]="true"
              [options]="businessTypeOptions"
            ></app-select-field>
          </div>
        </div>

        <div class="form-row">
          <div class="form-col full">
            <app-text-field
              formControlName="observations"
              label="Observaciones"
              placeholder="Agrega notas adicionales sobre esta cotización"
              hint="Opcional: requisitos especiales o comentarios"
            ></app-text-field>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-secondary" (click)="onCancel()">
            Cancelar
          </button>
          <button
            type="button"
            class="btn btn-primary"
            [disabled]="form.invalid"
            (click)="onNext()"
          >
            Siguiente paso →
          </button>
        </div>
      </form>

      <div *ngIf="form.invalid && form.touched" class="validation-errors">
        <div *ngIf="form.get('customerName')?.hasError('required')" class="error-message">
          El nombre del cliente es obligatorio
        </div>
        <div *ngIf="form.get('currency')?.hasError('required')" class="error-message">
          La moneda es obligatoria
        </div>
        <div *ngIf="form.get('riskClassification')?.hasError('required')" class="error-message">
          La clasificación de riesgo es obligatoria
        </div>
        <div *ngIf="form.get('businessType')?.hasError('required')" class="error-message">
          El tipo de negocio es obligatorio
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

    .form-label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 500;
      color: #2c3e50;
    }

    .agent-search-input {
      width: 100%;
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
    }

    .search-hint,
    .agent-selected-info {
      display: block;
      margin-top: 0.35rem;
      color: #6c757d;
      font-size: 0.85rem;
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
export class StepGeneralInfoComponent implements OnInit, OnChanges, OnDestroy {
  @Input() initialGeneralInfo: GeneralInfoFormData | null = null;
  @Output() next = new EventEmitter<GeneralInfoFormData>();
  @Output() cancel = new EventEmitter<void>();

  form!: FormGroup;
  agentOptions: Array<{ value: string; label: string }> = [];
  selectedAgentName = '';
  private allAgents: Agent[] = [];
  private readonly destroy$ = new Subject<void>();

  currencyOptions = [
    { value: 'COP', label: 'Peso colombiano (COP)' },
    { value: 'USD', label: 'Dólar estadounidense (USD)' },
    { value: 'MXN', label: 'Peso mexicano (MXN)' }
  ];
  riskClassificationOptions = [
    { value: 'LOW', label: 'Bajo' },
    { value: 'MEDIUM', label: 'Medio' },
    { value: 'HIGH', label: 'Alto' }
  ];
  businessTypeOptions = [
    { value: 'COMMERCE', label: 'Comercio' },
    { value: 'SERVICES', label: 'Servicios' },
    { value: 'INDUSTRIAL', label: 'Industrial' }
  ];

  constructor(
    private fb: FormBuilder,
    private agentApi: AgentApiService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadAgents();
    this.syncSelectedAgentName();
    this.applyInitialGeneralInfo();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['initialGeneralInfo']) {
      this.applyInitialGeneralInfo();
      this.syncSelectedAgentName();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Initialize form with validators
   */
  private initializeForm(): void {
    this.form = this.fb.group({
      productCode: ['DANOS', Validators.required],
      customerName: ['', [Validators.required, Validators.minLength(3)]],
      currency: ['COP', Validators.required],
      agentCode: [''],
      riskClassification: ['', Validators.required],
      businessType: ['', Validators.required],
      observations: ['']
    });

    this.form.get('agentCode')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.syncSelectedAgentName());
  }

  private applyInitialGeneralInfo(): void {
    if (!this.form || !this.initialGeneralInfo) {
      return;
    }

    this.form.patchValue(
      {
        productCode: this.initialGeneralInfo.productCode ?? 'DANOS',
        customerName: this.initialGeneralInfo.customerName ?? '',
        currency: this.initialGeneralInfo.currency ?? 'COP',
        agentCode: this.initialGeneralInfo.agentCode ?? '',
        riskClassification: this.initialGeneralInfo.riskClassification ?? '',
        businessType: this.initialGeneralInfo.businessType ?? '',
        observations: this.initialGeneralInfo.observations ?? ''
      },
      { emitEvent: false }
    );
    this.syncSelectedAgentName();
  }

  private loadAgents(): void {
    this.agentApi.listAgents(true)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.allAgents = response.data;
          this.agentOptions = this.toAgentOptions(this.allAgents);
          this.syncSelectedAgentName();
        },
        error: () => {
          this.allAgents = [];
          this.agentOptions = [];
          this.selectedAgentName = '';
        }
      });
  }

  onAgentSearchChange(event: Event): void {
    const value = (event.target as HTMLInputElement | null)?.value?.trim().toLowerCase() ?? '';
    if (!value) {
      this.agentOptions = this.toAgentOptions(this.allAgents);
      return;
    }
    const filtered = this.allAgents.filter((agent) =>
      agent.agentCode.toLowerCase().includes(value) || agent.agentName.toLowerCase().includes(value)
    );
    this.agentOptions = this.toAgentOptions(filtered);
  }

  private toAgentOptions(agents: Agent[]): Array<{ value: string; label: string }> {
    return agents.map((agent) => ({
      value: agent.agentCode,
      label: `${agent.agentCode} - ${agent.agentName}`
    }));
  }

  private syncSelectedAgentName(): void {
    const selectedCode = this.form?.get('agentCode')?.value;
    this.selectedAgentName = this.allAgents.find((agent) => agent.agentCode === selectedCode)?.agentName ?? '';
  }

  /**
   * Emit next event with form data
   */
  onNext(): void {
    if (this.form.valid) {
      const data = this.form.value as GeneralInfoFormData;
      const selected = this.allAgents.find((agent) => agent.agentCode === data.agentCode);
      this.next.emit({
        ...data,
        agentNameSnapshot: selected?.agentName
      });
    }
  }

  /**
   * Emit cancel event
   */
  onCancel(): void {
    this.cancel.emit();
  }
}
