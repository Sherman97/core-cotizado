import { Routes } from '@angular/router';
import { CreateQuotePageComponent } from './features/quote/pages/create-quote-page/create-quote-page.component';
import { QuoteWizardPageComponent } from './features/quote/pages/quote-wizard-page/quote-wizard-page.component';
import { QuoteStatusPageComponent } from './features/quote/pages/quote-status-page/quote-status-page.component';

export const appRoutes: Routes = [
  { path: '', component: CreateQuotePageComponent },
  { path: 'quotes/:folio/wizard', component: QuoteWizardPageComponent },
  { path: 'quotes/:folio/status', component: QuoteStatusPageComponent },
  { path: '**', redirectTo: '' }
];
