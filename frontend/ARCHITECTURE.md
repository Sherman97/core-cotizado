# Frontend Architecture & Design Patterns

## Overview

The frontend follows **Clean Architecture** principles with a clear separation between:
- **Presentation Layer** (Components & Pages)
- **Business Logic Layer** (Services & State Management)
- **Data Layer** (API Services & Models)

## Directory Structure Philosophy

```
core/                    → Business logic, external integration
├── models/             → TypeScript interfaces (pure data contracts)
├── services/           → API calls, state management, business logic
└── interceptors/       → Global HTTP handling

shared/                 → Reusable UI components, utilities
├── components/         → Generic form fields, layouts
└── utils/              → Shared functions, pipes, directives

features/               → Feature-specific implementation
├── quotes/
│   ├── pages/         → Container/Smart components (orchestration)
│   ├── components/    → Dumb/Presentational components (UI)
│   └── services/      → Feature-specific services if needed

layout/                → App-wide layout structure
```

## Key Design Patterns

### 1. Service Layer Pattern

**Purpose**: Centralize all business logic and HTTP calls

```typescript
// quote-api.service.ts
@Injectable({ providedIn: 'root' })
export class QuoteApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/v1`;

  constructor(private http: HttpClient) {}

  createFolio(): Observable<ApiResponse<CreateFolioResponse>> {
    return this.http.post<ApiResponse<CreateFolioResponse>>(
      `${this.baseUrl}/folios`,
      {}
    );
  }

  calculateQuote(folio: string): Observable<ApiResponse<CalculationResponse>> {
    return this.http.post<ApiResponse<CalculationResponse>>(
      `${this.baseUrl}/quotes/${folio}/calculate`,
      {}
    );
  }
}
```

**Benefits**:
- Single responsibility
- Testable
- Reusable across components
- Centralized configuration

### 2. Model/Interface Abstraction

**Purpose**: Strong typing and contract definition

```typescript
// api.models.ts - API contracts
export interface GeneralInfo {
  productCode: string;
  customerName: string;
  currency: string;
  observations?: string;
}

// form.models.ts - Form data
export interface GeneralInfoFormData {
  productCode: string;
  customerName: string;
  currency: string;
  observations?: string;
}
```

**Benefits**:
- IDE autocompletion
- Type safety
- Documentation
- Easy refactoring

### 3. Container / Presentational Component Pattern

**Container (Smart) Components**:
```typescript
// quote-wizard-page.component.ts
@Component({ ... })
export class QuoteWizardPageComponent {
  constructor(
    private wizardService: QuotationWizardService,
    private quoteApi: QuoteApiService
  ) {}

  onGeneralInfoNext(data: GeneralInfoFormData): void {
    this.wizardService.setGeneralInfo(data);
    this.saveGeneralInfoToBackend(data);
  }
}
```

Responsibilities:
- Route parameter handling
- Service orchestration
- State management
- Business logic

**Presentational (Dumb) Components**:
```typescript
// step-general-info.component.ts
@Component({ ... })
export class StepGeneralInfoComponent {
  @Input() initialData: GeneralInfoFormData;
  @Output() next = new EventEmitter<GeneralInfoFormData>();

  form: FormGroup;

  onNext(): void {
    if (this.form.valid) {
      this.next.emit(this.form.value);
    }
  }
}
```

Responsibilities:
- UI rendering
- User input capture
- Event emission
- No service calls

**Benefits**:
- Reusability
- Testability
- Single responsibility
- Clear data flow

### 4. State Management via Services & Observables

```typescript
@Injectable({ providedIn: 'root' })
export class QuotationWizardService {
  private wizardStateSubject = new BehaviorSubject<QuotationWizardState>(initialState);
  public wizardState$ = this.wizardStateSubject.asObservable();

  setGeneralInfo(data: GeneralInfoFormData): void {
    const current = this.getState();
    current.generalInfo = data;
    this.wizardStateSubject.next({ ...current });
  }
}
```

**Usage in Component**:
```typescript
export class QuoteWizardPageComponent implements OnInit {
  wizardState: QuotationWizardState;

  ngOnInit(): void {
    this.wizardService.wizardState$
      .pipe(takeUntil(this.destroy$))
      .subscribe(state => {
        this.wizardState = state;
      });
  }
}
```

**Benefits**:
- Reactive
- Time-travel debugging
- Predictable state
- Observable subscriptions

### 5. Reactive Forms Pattern

```typescript
export class StepGeneralInfoComponent implements OnInit {
  form: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      customerName: ['', [Validators.required, Validators.minLength(3)]],
      currency: ['COP', Validators.required],
      observations: ['']
    });
  }

  onNext(): void {
    if (this.form.valid) {
      this.next.emit(this.form.value);
    }
  }
}
```

**Benefits**:
- Reactive approach
- Type-safe
- Dynamic validation
- Easy to test

### 6. ControlValueAccessor Pattern

```typescript
@Component({ ... })
export class TextFieldComponent implements ControlValueAccessor {
  @Input() label: string;
  @Input() placeholder: string;
  value: string = '';

  onChange: any = () => {};
  onTouched: any = () => {};

  onInput(event: any): void {
    this.value = event.target.value;
    this.onChange(this.value);
  }

  writeValue(value: any): void {
    this.value = value || '';
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }
}
```

**Usage in Forms**:
```typescript
<app-text-field
  formControlName="customerName"
  label="Customer"
  placeholder="Enter name"
  [required]="true"
></app-text-field>
```

**Benefits**:
- Seamless integration with reactive forms
- Custom form controls
- Two-way binding
- Validation support

### 7. Error Handling Pattern

**Global Interceptor**:
```typescript
export const apiErrorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('API error', error);
      return throwError(() => error);
    })
  );
};
```

**Component Level**:
```typescript
export class QuoteWizardPageComponent {
  errorMessage: string = '';

  onCalculate(): void {
    this.loading = true;
    this.quoteApi.calculateQuote(this.folio).subscribe({
      next: (response) => {
        // Handle success
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error calculating quote';
      }
    });
  }
}
```

## Data Flow Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    User Interaction                      │
└────────────┬────────────────────────────────────────┬───┘
             │                                        │
             ▼                                        ▼
    ┌────────────────┐                      ┌────────────────┐
    │ Presentational │                      │  Container     │
    │ Component      │                      │  Component     │
    │ (Dumb)         │                      │  (Smart)       │
    └────────┬───────┘                      └────────┬───────┘
             │                                        │
             └─── @Output() ────► Form Data ◄─── @Input() ──┘
                                    │
                                    ▼
                          ┌──────────────────┐
                          │ Service Layer    │
                          │ (Business Logic) │
                          └────────┬─────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │                             │
                    ▼                             ▼
            ┌──────────────┐            ┌──────────────┐
            │ State Service│            │ API Service  │
            │ (RxJS)       │            │ (HTTP)       │
            └──────────────┘            └──────┬───────┘
                    │                          │
                    ▼                          ▼
            ┌──────────────┐            ┌──────────────┐
            │ Observable   │            │ HTTP Requests│
            │ State Stream │            │ to Backend   │
            └──────────────┘            └──────────────┘
```

## Backend-Driven Design

### Principle: No Hardcoded Business Logic

❌ **Wrong - Hardcoded Factors**:
```typescript
const occupancyFactor = {
  OFFICE: 1.0,
  COMMERCE: 1.2,
  WAREHOUSE: 0.8
};
```

✅ **Right - Backend-Driven**:
```typescript
// Load from backend catalog
this.catalogService.getOccupancyFactors().subscribe(factors => {
  this.occupancyOptions = factors.map(f => ({
    value: f.code,
    label: f.name
  }));
});
```

### Principle: Configuration Over Convention

❌ **Wrong - Static Form**:
```typescript
form = this.fb.group({
  occupancy: ['OFFICE'],        // Hardcoded default
  deductible: [50000],          // Hardcoded default
  maxLimit: [5000000]           // Hardcoded max
});
```

✅ **Right - Backend Configuration**:
```typescript
// Get configuration from backend
this.configService.getFieldConfig('occupancy').subscribe(config => {
  this.form.patchValue({
    occupancy: config.defaultValue
  });
});
```

## Scalability Considerations

### Adding New Features

1. **Define Models** (if needed)
   ```typescript
   // core/models/custom.models.ts
   export interface CustomData { ... }
   ```

2. **Create API Service**
   ```typescript
   // core/services/custom-api.service.ts
   @Injectable({ providedIn: 'root' })
   export class CustomApiService { ... }
   ```

3. **Build Components**
   ```typescript
   // features/custom/components/
   // features/custom/pages/
   ```

4. **Add Routes**
   ```typescript
   // app.routes.ts
   { path: 'custom', component: CustomPageComponent }
   ```

### Adding New Form Field Type

1. Create component implementing ControlValueAccessor
2. Add to shared/components/
3. Use in reactive forms like native controls

## Testing Strategy

### Unit Testing Services
```typescript
describe('QuoteApiService', () => {
  it('should create folio', () => {
    service.createFolio().subscribe(result => {
      expect(result.data.numeroFolio).toBeDefined();
    });
  });
});
```

### Unit Testing Components
```typescript
describe('StepGeneralInfoComponent', () => {
  it('should emit next event when form valid', () => {
    component.form.patchValue({
      customerName: 'Test',
      currency: 'COP'
    });
    spyOn(component.next, 'emit');
    component.onNext();
    expect(component.next.emit).toHaveBeenCalled();
  });
});
```

### Integration Testing
```typescript
describe('QuoteWizardPageComponent', () => {
  it('should complete full wizard flow', fakeAsync(() => {
    // Test complete flow from step 1 to result
  }));
});
```

## Performance Optimization

### OnPush Change Detection
```typescript
@Component({
  selector: 'app-step',
  changeDetection: ChangeDetectionStrategy.OnPush
})
```

### Unsubscribe Pattern
```typescript
private destroy$ = new Subject<void>();

ngOnInit() {
  this.service.data$
    .pipe(takeUntil(this.destroy$))
    .subscribe(...);
}

ngOnDestroy() {
  this.destroy$.next();
  this.destroy$.complete();
}
```

### Lazy Loading
```typescript
const routes: Routes = [
  {
    path: 'quotes',
    loadComponent: () => import('./features/quotes/...').then(m => m.QuotesComponent)
  }
];
```

## Development Workflow

### Local Development
1. Backend runs on http://localhost:8080
2. Frontend runs on http://localhost:4200
3. API calls proxy through environment.apiBaseUrl

### Build for Production
```bash
ng build --configuration production
# Uses environment.prod.ts with production API URL
```

### Code Organization Rules

1. **One file, one responsibility**
2. **No circular dependencies**
3. **Services never call components**
4. **Components use DI for services**
5. **Models are pure data contracts**
6. **No business logic in components**

## Future Architecture Enhancements

- [ ] Implement NgRx for complex state management
- [ ] Add router guards for access control
- [ ] Implement feature-based lazy loading
- [ ] Add analytics and logging layer
- [ ] Implement service worker for offline support
- [ ] Add API versioning strategy
- [ ] Implement request caching layer
- [ ] Add end-to-end testing with Cypress/Playwright

