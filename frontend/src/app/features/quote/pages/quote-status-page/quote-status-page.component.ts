import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { QuoteApiService } from '../../services/quote-api.service';

@Component({
  selector: 'app-quote-status-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="page-shell">
      <div class="card">
        <h1>Estado final de la cotización</h1>
        <div *ngIf="status() as item; else loading">
          <p><strong>Folio:</strong> {{ item.folio }}</p>
          <p><strong>Estado:</strong> {{ item.status }}</p>
          <p><strong>Paso:</strong> {{ item.currentStep }}</p>
          <p><strong>Prima neta:</strong> {{ item.netPremium | number:'1.2-2' }}</p>
          <p><strong>Prima total:</strong> {{ item.totalPremium | number:'1.2-2' }}</p>
          <div *ngIf="item.alerts?.length">
            <h3>Alertas</h3>
            <ul>
              <li *ngFor="let alert of item.alerts">{{ alert }}</li>
            </ul>
          </div>
          <a [routerLink]="['/quotes', item.folio, 'wizard']">Volver al wizard</a>
        </div>
      </div>
      <ng-template #loading>
        <div class="card">Cargando estado...</div>
      </ng-template>
    </div>
  `
})
export class QuoteStatusPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly quoteApi = inject(QuoteApiService);

  readonly status = signal<any | null>(null);

  ngOnInit(): void {
    const folio = this.route.snapshot.paramMap.get('folio') ?? '';
    this.quoteApi.getStatus(folio).subscribe((status) => this.status.set(status));
  }
}
