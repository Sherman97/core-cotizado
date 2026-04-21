import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { CalculationResult } from '../../../../shared/models/calculation.model';

@Component({
  selector: 'app-calculation-trace-panel',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card" *ngIf="result">
      <h3>Resultado por ubicación</h3>
      <div class="grid">
        <div *ngFor="let item of result.locations" style="padding:12px; border:1px solid var(--line); border-radius:12px;">
          <strong>{{ item.locationName }}</strong>
          <p>Estado: {{ item.status }}</p>
          <p>Prima: {{ item.premium | number:'1.2-2' }}</p>
          <ul *ngIf="item.alerts.length">
            <li *ngFor="let alert of item.alerts">{{ alert }}</li>
          </ul>
        </div>
      </div>
    </div>
  `
})
export class CalculationTracePanelComponent {
  @Input() result: CalculationResult | null = null;
}
