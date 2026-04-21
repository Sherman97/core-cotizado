import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StepperComponent } from '../../../../shared/components/stepper/stepper.component';
import { AlertListComponent } from '../../../../shared/components/alert-list/alert-list.component';
import { GeneralDataFormComponent } from '../../components/general-data-form/general-data-form.component';
import { LocationLayoutFormComponent } from '../../components/location-layout-form/location-layout-form.component';
import { LocationFormComponent } from '../../components/location-form/location-form.component';
import { LocationsGridComponent } from '../../components/locations-grid/locations-grid.component';
import { CoverageSelectorComponent } from '../../components/coverage-selector/coverage-selector.component';
import { PremiumSummaryComponent } from '../../components/premium-summary/premium-summary.component';
import { CalculationTracePanelComponent } from '../../components/calculation-trace-panel/calculation-trace-panel.component';
import { QuoteApiService } from '../../services/quote-api.service';
import { QuoteWizardStore } from '../../store/quote-wizard.store';

@Component({
  selector: 'app-quote-wizard-page',
  standalone: true,
  imports: [
    CommonModule,
    StepperComponent,
    AlertListComponent,
    GeneralDataFormComponent,
    LocationLayoutFormComponent,
    LocationFormComponent,
    LocationsGridComponent,
    CoverageSelectorComponent,
    PremiumSummaryComponent,
    CalculationTracePanelComponent
  ],
  template: `
    <div class="page-shell">
      <app-stepper [steps]="steps" [current]="currentStep()"></app-stepper>

      <div class="card" *ngIf="quoteStore.quote() as quote" style="margin-bottom:20px;">
        <div style="display:flex; justify-content:space-between; gap:16px; flex-wrap:wrap;">
          <div>
            <h2>Folio {{ quote.folio }}</h2>
            <p>Estado: {{ getStatusLabel(quote.status) }} | Paso: {{ getStepLabel(quote.currentStep) }}</p>
          </div>
          <div class="actions">
            <button class="btn btn-secondary" (click)="goToStatus()">Ver estado final</button>
          </div>
        </div>
      </div>

      <app-general-data-form (saved)="saveGeneralData($event)"></app-general-data-form>
      <app-location-layout-form (saved)="saveLayout($event)"></app-location-layout-form>
      <app-location-form (saved)="saveLocation($event)"></app-location-form>
      <app-locations-grid [locations]="quoteStore.locations()"></app-locations-grid>

      <app-coverage-selector
        [coverages]="coverageCatalog"
        (configured)="saveCoverages($event)">
      </app-coverage-selector>

      <div class="actions" style="margin:20px 0;">
        <button class="btn btn-primary" (click)="calculate()">Ejecutar cálculo</button>
      </div>

      <app-alert-list [alerts]="quoteStore.calculation()?.alerts ?? []"></app-alert-list>
      <app-premium-summary [result]="quoteStore.calculation()"></app-premium-summary>
      <app-calculation-trace-panel [result]="quoteStore.calculation()"></app-calculation-trace-panel>
    </div>
  `
})
export class QuoteWizardPageComponent implements OnInit {
  readonly quoteStore = inject(QuoteWizardStore);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly quoteApi = inject(QuoteApiService);

  readonly steps = [
    { value: 'GENERAL_DATA', label: 'Datos generales' },
    { value: 'LOCATION_LAYOUT', label: 'Estructura de ubicaciones' },
    { value: 'LOCATIONS', label: 'Ubicaciones' },
    { value: 'COVERAGES', label: 'Coberturas' },
    { value: 'RESULT', label: 'Resultado' }
  ];
  readonly currentStep = computed(() => this.quoteStore.quote()?.currentStep ?? 'GENERAL_DATA');
  coverageCatalog: Array<{ code: string; name: string; description: string; selected?: boolean }> = [];

  ngOnInit(): void {
    const folio = this.folio;
    this.quoteApi.getQuote(folio).subscribe((quote) => this.quoteStore.setQuote(quote));
    this.refreshLocations();
    this.quoteApi.getCoverageCatalog().subscribe((catalog) => {
      this.coverageCatalog = catalog.map((item) => ({ ...item, selected: false }));
    });
  }

  saveGeneralData(payload: Record<string, unknown>): void {
    this.quoteApi.updateGeneralData(this.folio, payload).subscribe((quote) => this.quoteStore.setQuote(quote));
  }

  saveLayout(payload: Record<string, unknown>): void {
    this.quoteApi.saveLocationLayout(this.folio, payload).subscribe((quote) => this.quoteStore.setQuote(quote));
  }

  saveLocation(payload: Record<string, unknown>): void {
    this.quoteApi.createLocation(this.folio, payload).subscribe(() => this.refreshLocations());
  }

  saveCoverages(payload: Array<{ coverageCode: string; coverageName: string; isSelected: boolean }>): void {
    this.quoteApi.configureCoverages(this.folio, { coverages: payload }).subscribe((coverages) => {
      this.quoteStore.setCoverages(coverages);
    });
  }

  calculate(): void {
    this.quoteApi.calculate(this.folio).subscribe((result) => {
      this.quoteStore.setCalculation(result);
      this.quoteApi.getQuote(this.folio).subscribe((quote) => this.quoteStore.setQuote(quote));
    });
  }

  goToStatus(): void {
    this.router.navigate(['/quotes', this.folio, 'status']);
  }

  private refreshLocations(): void {
    this.quoteApi.listLocations(this.folio).subscribe((locations) => this.quoteStore.setLocations(locations));
  }

  private get folio(): string {
    return this.route.snapshot.paramMap.get('folio') ?? '';
  }

  getStatusLabel(status?: string): string {
    switch (status) {
      case 'DRAFT':
        return 'Borrador';
      case 'PENDING_CALCULATION':
        return 'Pendiente de cálculo';
      case 'CALCULATED':
        return 'Calculada';
      case 'SAVED':
        return 'Guardada';
      default:
        return status ?? '-';
    }
  }

  getStepLabel(step?: string): string {
    switch (step) {
      case 'GENERAL_DATA':
        return 'Datos generales';
      case 'LOCATION_LAYOUT':
        return 'Estructura de ubicaciones';
      case 'LOCATIONS':
        return 'Ubicaciones';
      case 'COVERAGES':
        return 'Coberturas';
      case 'RESULT':
        return 'Resultado';
      default:
        return step ?? '-';
    }
  }
}
