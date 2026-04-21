import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-location-layout-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form class="card grid grid-2" [formGroup]="form" (ngSubmit)="submit()">
      <div class="field">
        <label>Número esperado de ubicaciones</label>
        <input type="number" formControlName="expectedLocationCount">
      </div>
      <div class="field">
        <label>Capturar zona de riesgo</label>
        <select formControlName="captureRiskZone">
          <option [ngValue]="true">Sí</option>
          <option [ngValue]="false">No</option>
        </select>
      </div>
      <div class="field">
        <label>Capturar georreferencia</label>
        <select formControlName="captureGeoreference">
          <option [ngValue]="true">Sí</option>
          <option [ngValue]="false">No</option>
        </select>
      </div>
      <div class="field">
        <label>Notas</label>
        <input formControlName="notes">
      </div>
      <div class="actions" style="grid-column: 1 / -1;">
        <button class="btn btn-secondary" type="submit">Guardar layout</button>
      </div>
    </form>
  `
})
export class LocationLayoutFormComponent {
  @Output() saved = new EventEmitter<Record<string, unknown>>();

  private readonly fb = inject(FormBuilder);

  readonly form = this.fb.nonNullable.group({
    expectedLocationCount: 1,
    captureRiskZone: true,
    captureGeoreference: false,
    notes: ''
  });

  submit(): void {
    this.saved.emit(this.form.getRawValue());
  }
}
