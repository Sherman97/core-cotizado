import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { QuotesListPageComponent } from './features/quotes/pages/quotes-list-page/quotes-list-page.component';
import { QuoteWizardPageComponent } from './features/quotes/pages/quote-wizard-page/quote-wizard-page.component';
import { QuoteResultPageComponent } from './features/quotes/pages/quote-result-page/quote-result-page.component';

export const appRoutes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', component: QuotesListPageComponent },
      { path: 'quotes/:folio/wizard', component: QuoteWizardPageComponent },
      { path: 'quotes/:folio/result', component: QuoteResultPageComponent },
      { path: '**', redirectTo: '' }
    ]
  }
];
