import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { QuoteApiService } from '../../services/quote-api.service';

@Component({
  selector: 'app-create-quote-page',
  standalone: true,
  template: `
    <div class="page-shell">
      <div class="card">
        <p style="text-transform:uppercase; letter-spacing:0.12em; color:var(--accent); font-weight:700;">MVP Cotizador</p>
        <h1>Cotizador de seguros de daños</h1>
        <p>Inicia una cotización en estado borrador y continúa el flujo completo del asistente.</p>
        <div class="actions">
          <button class="btn btn-primary" (click)="createQuote()">Crear folio</button>
        </div>
      </div>
    </div>
  `
})
export class CreateQuotePageComponent {
  private readonly router = inject(Router);
  private readonly quoteApi = inject(QuoteApiService);

  createQuote(): void {
    this.quoteApi.createQuote().subscribe((quote) => {
      this.router.navigate(['/quotes', quote.folio, 'wizard']);
    });
  }
}
