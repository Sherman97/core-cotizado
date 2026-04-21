import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-coverage-selector',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card">
      <h3>Coberturas</h3>
      <div class="grid">
        <label *ngFor="let coverage of coverages" style="display:flex; gap:12px; align-items:flex-start;">
          <input type="checkbox" [checked]="coverage.selected" (change)="toggle(coverage, $any($event.target).checked)">
          <span>
            <strong>{{ coverage.name }}</strong><br>
            <small>{{ coverage.description }}</small>
          </span>
        </label>
      </div>
      <div class="actions" style="margin-top:16px;">
        <button class="btn btn-primary" type="button" (click)="save()">Guardar coberturas</button>
      </div>
    </div>
  `
})
export class CoverageSelectorComponent {
  @Input() coverages: Array<{ code: string; name: string; description: string; selected?: boolean }> = [];
  @Output() configured = new EventEmitter<Array<{ coverageCode: string; coverageName: string; isSelected: boolean }>>();

  toggle(item: { selected?: boolean }, checked: boolean): void {
    item.selected = checked;
  }

  save(): void {
    this.configured.emit(
      this.coverages
        .filter((item) => item.selected)
        .map((item) => ({
          coverageCode: item.code,
          coverageName: item.name,
          isSelected: true
        }))
    );
  }
}
