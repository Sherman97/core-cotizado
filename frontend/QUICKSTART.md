# Quick Start & Implementation Guide

## Project Setup

### Prerequisites
- Node.js 18+
- npm 9+
- Angular CLI 17+

### Installation

```bash
cd frontend
npm install
```

### Start Development Server

```bash
npm start
# or
ng serve --host 0.0.0.0 --port 4200
```

Navigate to `http://localhost:4200` - Application will auto-reload on code changes.

## Running with Backend

### Prerequisites
- Backend running on `http://localhost:8080`
- Database (MariaDB) running with proper migrations

### Backend Setup
```bash
cd backend
docker compose up -d mariadb
./scripts/run-mariadb.sh
```

### Frontend Configuration

Edit `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080'
};
```

### Test API Connection

1. Open browser DevTools (F12)
2. Start a new quotation
3. Check Network tab for successful API calls to `http://localhost:8080/v1/folios`

## Project Structure Checklist

After generation, verify all files exist:

### Core Module
- [ ] `core/models/api.models.ts` - API contracts
- [ ] `core/models/form.models.ts` - Form data models
- [ ] `core/services/quote-api.service.ts` - API calls
- [ ] `core/services/quotation-wizard.service.ts` - State management
- [ ] `core/services/catalog.service.ts` - Catalog data
- [ ] `core/interceptors/api-error.interceptor.ts` - Error handling

### Shared Components
- [ ] `shared/components/text-field/text-field.component.ts`
- [ ] `shared/components/select-field/select-field.component.ts`
- [ ] `shared/components/number-field/number-field.component.ts`

### Features - Quotes
- [ ] `features/quotes/pages/quotes-list-page/quotes-list-page.component.ts`
- [ ] `features/quotes/pages/quote-wizard-page/quote-wizard-page.component.ts`
- [ ] `features/quotes/pages/quote-result-page/quote-result-page.component.ts`
- [ ] `features/quotes/components/step-general-info/step-general-info.component.ts`
- [ ] `features/quotes/components/step-locations/step-locations.component.ts`
- [ ] `features/quotes/components/step-coverages/step-coverages.component.ts`
- [ ] `features/quotes/components/step-summary/step-summary.component.ts`

### Layout
- [ ] `layout/main-layout/main-layout.component.ts`

### Configuration Files
- [ ] `src/environments/environment.ts`
- [ ] `src/environments/environment.prod.ts`
- [ ] `src/app/app.routes.ts` - Updated routing
- [ ] `package.json` - Updated dependencies

### Documentation
- [ ] `FRONTEND.md` - Architecture overview
- [ ] `ARCHITECTURE.md` - Design patterns
- [ ] `API_PAYLOADS.md` - Example payloads

## Testing the Application

### Step-by-Step Test Flow

#### 1. Create Quotation
```
URL: http://localhost:4200
Action: Click "Create New Quotation"
Expected: Navigate to wizard with folio number in URL
Check: Network shows POST /v1/folios successful
```

#### 2. General Information
```
Action: Fill customer name, select currency
Click: Next Step
Expected: Navigate to step 2
Check: Data saved in QuotationWizardService
```

#### 3. Add Locations
```
Action: 
  - Click "Add Location"
  - Fill location details (Name, City, Department, Postal Code)
  - Select Occupancy and Construction Type from dropdowns
  - Enter Insured Value
  - Click "Add Location"
Expected: Card appears with location summary
Check: Dropdowns populated from CatalogService.getMockCatalog()
```

#### 4. Add Coverages
```
Action:
  - Check coverages (Fire, Earthquake, etc.)
  - Set insured limits and deductibles
Click: Next Step
Expected: Navigate to step 4
```

#### 5. Review Summary
```
Expected: All entered data displayed in collapsed sections
Action:
  - Expand each section to verify
  - Click "Calculate Quote"
Expected: Loading state, then redirect to results page
```

#### 6. View Results
```
Expected: 
  - Total premium displayed prominently
  - Calculation factors shown
  - Location breakdown table
  - Warnings/exclusions (if any)
```

## Common Issues & Solutions

### Issue: API calls return 404 errors

**Problem**: Backend not running or wrong API URL

**Solution**:
1. Check backend is running: `curl http://localhost:8080/v1/folios`
2. Verify `environment.apiBaseUrl` in `environment.ts`
3. Check network tab in browser DevTools
4. Verify CORS is enabled on backend

### Issue: Form fields not displaying correctly

**Problem**: ControlValueAccessor not registered

**Solution**:
1. Ensure form field component has `providers` with `NG_VALUE_ACCESSOR`
2. Check component is imported in parent component
3. Verify `formControlName` matches form control name
4. Check browser console for errors

### Issue: Dropdown options not loading

**Problem**: Catalog service mock not called or import missing

**Solution**:
1. Import `CatalogService` in component
2. Inject via constructor
3. Call service methods in `ngOnInit`
4. Verify mock data in `catalog.service.ts`

### Issue: State not persisting between steps

**Problem**: `QuotationWizardService` not used correctly

**Solution**:
1. Inject service in component
2. Subscribe to `wizardState$` observable
3. Call service methods to update state
4. Use `takeUntil` pattern for unsubscribing

### Issue: TypeScript compilation errors

**Problem**: Type mismatch or missing interfaces

**Solution**:
1. Check imports in model files
2. Verify interface definitions match backend response
3. Use `any` temporarily, then type properly
4. Run `npm run build` to see all errors

## Browser DevTools Testing

### Network Tab
- Check all requests to backend are successful (200 status)
- Verify request/response payloads match documentation
- Look for failed requests and read error messages

### Console Tab
- Check for TypeScript/JavaScript errors
- Verify no 404 errors for resources
- Check for deprecation warnings

### Application Tab
- Inspect component state in Angular DevTools (if installed)
- Check local storage (if persistence added)
- Verify service instances

## Performance Testing

### Lighthouse Audit
```bash
npm run build -- --configuration production
# Deploy built files to test with Lighthouse
```

### Bundle Size
```bash
ng build --configuration production --stats-json
npm install -g webpack-bundle-analyzer
webpack-bundle-analyzer dist/cotizador-danos-frontend/stats.json
```

## Production Build

### Build Command
```bash
npm run build
```

### Output
```
dist/cotizador-danos-frontend/
├── index.html
├── main-XXXXX.js
├── polyfills-XXXXX.js
├── runtime-XXXXX.js
└── ...
```

### Deployment
1. Set `environment.prod.ts` with production API URL
2. Build: `ng build --configuration production`
3. Deploy `dist/` folder to web server
4. Configure web server for SPA routing

## Code Quality

### Format Code
```bash
npm install -g prettier
prettier --write "src/**/*.ts"
```

### Lint
```bash
ng lint
```

### Build
```bash
ng build
```

## Adding New Features

### Add New Form Field Type

1. Create component in `shared/components/`:
```bash
mkdir src/app/shared/components/custom-field
```

2. Implement `ControlValueAccessor`:
```typescript
// custom-field.component.ts
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => CustomFieldComponent),
    multi: true
  }]
})
export class CustomFieldComponent implements ControlValueAccessor {
  // Implement interface methods
}
```

3. Use in form:
```html
<app-custom-field
  formControlName="customControl"
  [options]="options"
></app-custom-field>
```

### Add New Step to Wizard

1. Create component in `features/quotes/components/`:
```typescript
// step-custom.component.ts
@Component({
  selector: 'app-step-custom',
  standalone: true,
  template: `...`
})
export class StepCustomComponent {
  @Input() initialData: any;
  @Output() next = new EventEmitter<any>();
  @Output() previous = new EventEmitter<void>();
}
```

2. Add to wizard orchestrator:
```typescript
// quote-wizard-page.component.ts
currentStep = 5; // New step
steps = [..., 'Custom Step'];

// Add case in template
<app-step-custom
  *ngIf="currentStep === 5"
  [initialData]="wizardState.customData"
  (next)="onCustomNext($event)"
  (previous)="previousStep()"
></app-step-custom>
```

3. Handle data:
```typescript
onCustomNext(data: any): void {
  this.wizardService.setCustomData(data);
  this.wizardService.goToStep(6);
}
```

### Replace Mock Catalog with Backend API

1. Update `catalog.service.ts`:
```typescript
export class CatalogService {
  constructor(private http: HttpClient) {}

  getCatalogs(): Observable<Catalog> {
    return this.http.get<Catalog>('/v1/catalogs');
    // Instead of: return new Observable(observer => {...})
  }
}
```

2. Components automatically use backend data

## Maintenance & Monitoring

### Keep Dependencies Updated
```bash
npm outdated
npm update
```

### Monitor Bundle Size
```bash
npm run build -- --stats-json
```

### Check for Security Vulnerabilities
```bash
npm audit
npm audit fix
```

## Troubleshooting Commands

```bash
# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Hard rebuild
ng clean
npm run build

# Run development server with verbose output
ng serve --verbose

# Check Angular version
ng version

# Update Angular CLI
npm install -g @angular/cli@latest
```

## Documentation Files

Generated documentation:

1. **FRONTEND.md** - Architecture and overview
2. **ARCHITECTURE.md** - Design patterns and principles
3. **API_PAYLOADS.md** - Example request/response payloads
4. **This file** - Quick start guide

## Next Steps

1. ✅ Review generated folder structure
2. ✅ Run `npm install` to install dependencies
3. ✅ Run `npm start` to start dev server
4. ✅ Complete test flow from above
5. ✅ Configure backend API URL
6. ✅ Replace mock catalog with real backend API
7. ✅ Add additional form steps if needed
8. ✅ Implement quotation persistence
9. ✅ Add quotation filtering and search
10. ✅ Deploy to production

## Support

For issues or questions:
1. Check browser console for errors
2. Verify backend is running
3. Check network requests in DevTools
4. Review documentation files
5. Check backend logs for API errors

