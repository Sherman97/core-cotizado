import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-money-summary',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="summary-grid" *ngIf="total !== undefined">
      <div class="card">
        <strong>Prima neta</strong>
        <div>{{ net | number:'1.2-2' }}</div>
      </div>
      <div class="card">
        <strong>Gastos</strong>
        <div>{{ expense | number:'1.2-2' }}</div>
      </div>
      <div class="card">
        <strong>Impuestos</strong>
        <div>{{ tax | number:'1.2-2' }}</div>
      </div>
      <div class="card">
        <strong>Prima total</strong>
        <div>{{ total | number:'1.2-2' }}</div>
      </div>
    </div>
  `
})
export class MoneySummaryComponent {
  @Input() net = 0;
  @Input() expense = 0;
  @Input() tax = 0;
  @Input() total?: number;
}
