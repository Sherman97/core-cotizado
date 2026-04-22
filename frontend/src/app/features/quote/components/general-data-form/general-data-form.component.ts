import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-general-data-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form class="card grid grid-2" [formGroup]="form" (ngSubmit)="submit()">
      <div class="field">
        <label>Cliente</label>
        <input formControlName="customerName">
      </div>
      <div class="field">
        <label>Documento</label>
        <input formControlName="customerDocument">
      </div>
      <div class="field">
        <label>Email</label>
        <input formControlName="customerEmail">
      </div>
      <div class="field">
        <label>Teléfono</label>
        <input formControlName="customerPhone">
      </div>
      <div class="field">
        <label>Producto</label>
        <input formControlName="productCode">
      </div>
      <div class="field">
        <label>Tipo de póliza</label>
        <input formControlName="policyType">
      </div>
      <div class="field">
        <label>Moneda</label>
        <input formControlName="currency">
      </div>
      <div class="field">
        <label>Valor asegurado</label>
        <input type="number" formControlName="insuredValue">
      </div>
      <div class="field">
        <label>Clasificación Riesgo</label>
        <input formControlName="riskClassification">
      </div>
      <div class="field">
        <label>Giro Empresarial</label>
        <input formControlName="businessType">
      </div>
      <div class="field" style="grid-column: 1 / -1;">
        <label>Observaciones</label>
        <textarea formControlName="observations"></textarea>
      </div>
      <div class="actions" style="grid-column: 1 / -1;">
        <button class="btn btn-primary" type="submit">Guardar datos generales</button>
      </div>
    </form>
  `
})
export class GeneralDataFormComponent {
  @Output() saved = new EventEmitter<Record<string, unknown>>();

  private readonly fb = inject(FormBuilder);

  readonly form = this.fb.nonNullable.group({
    customerName: '',
    customerDocument: '',
    customerEmail: '',
    customerPhone: '',
    productCode: 'DANOS_MVP',
    policyType: 'ANUAL',
    currency: 'COP',
    insuredValue: 0,
    riskClassification: '',
    businessType: '',
    observations: ''
  });

  submit(): void {
    this.saved.emit(this.form.getRawValue());
  }
}
