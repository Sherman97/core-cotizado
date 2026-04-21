import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MoneySummaryComponent } from '../../../../shared/components/money-summary/money-summary.component';
import { CalculationResult } from '../../../../shared/models/calculation.model';

@Component({
  selector: 'app-premium-summary',
  standalone: true,
  imports: [CommonModule, MoneySummaryComponent],
  template: `
    <app-money-summary
      *ngIf="result"
      [net]="result.netPremium"
      [expense]="result.expenseAmount"
      [tax]="result.taxAmount"
      [total]="result.totalPremium">
    </app-money-summary>
  `
})
export class PremiumSummaryComponent {
  @Input() result: CalculationResult | null = null;
}
