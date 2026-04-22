import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-location-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <form class="card grid grid-2" [formGroup]="form" (ngSubmit)="submit()">
      <div class="field"><label>Nombre</label><input formControlName="locationName"></div>
      <div class="field"><label>Departamento</label><input formControlName="department"></div>
      <div class="field"><label>Ciudad</label><input formControlName="city"></div>
      <div class="field"><label>Municipio</label><input formControlName="municipality"></div>
      <div class="field"><label>Colonia</label><input formControlName="colony"></div>
      <div class="field"><label>Dirección</label><input formControlName="address"></div>
      <div class="field"><label>Zona</label><input formControlName="riskZoneCode"></div>
      <div class="field"><label>Ocupación</label><input formControlName="occupancyType"></div>
      <div class="field"><label>Construcción</label><input formControlName="constructionType"></div>
      <div class="field"><label>Nivel de construcción</label><input type="number" formControlName="constructionLevel"></div>
      <div class="field"><label>Uso</label><input formControlName="usageType"></div>
      <div class="field"><label>Clave Bomberos</label><input formControlName="fireKey"></div>
      <div class="field" style="display: flex; align-items: center; gap: 8px;">
        <input type="checkbox" formControlName="catastrophicZone" id="catastrophicZone">
        <label for="catastrophicZone" style="margin-bottom: 0;">Zona Catastrófica</label>
      </div>
      <div class="field"><label>Valor asegurado</label><input type="number" formControlName="insuredValue"></div>
      <div class="field"><label>Pisos</label><input type="number" formControlName="floorsCount"></div>
      <div class="field"><label>Año construcción</label><input type="number" formControlName="constructionYear"></div>
      <div class="actions" style="grid-column: 1 / -1;">
        <button class="btn btn-primary" type="submit" [disabled]="form.invalid">Agregar ubicación</button>
      </div>
    </form>
  `
})
export class LocationFormComponent {
  @Output() saved = new EventEmitter<Record<string, unknown>>();

  private readonly fb = inject(FormBuilder);

  readonly form = this.fb.nonNullable.group({
    locationIndex: [null],
    locationName: ['', Validators.required],
    department: '',
    city: '',
    municipality: '',
    colony: '',
    address: '',
    postalCode: '',
    riskZoneCode: 'A',
    occupancyType: 'OFICINA',
    constructionType: 'CONCRETO',
    constructionLevel: 1,
    usageType: 'ADMINISTRATIVO',
    fireKey: '',
    catastrophicZone: false,
    insuredValue: [0, Validators.required],
    builtArea: 0,
    floorsCount: 1,
    constructionYear: new Date().getFullYear(),
    guarantees: [[]],
    isPrimary: false
  });

  submit(): void {
    this.saved.emit(this.form.getRawValue());
    this.form.patchValue({ locationName: '', insuredValue: 0 });
  }
}
