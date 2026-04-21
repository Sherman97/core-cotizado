# Cotizador de Daños - Frontend

Modern Angular 17 frontend application for an insurance quotation system with clean architecture and backend-driven business logic.

## Architecture Overview

The application follows clean architecture principles with a strong separation of concerns:

```
src/app/
├── core/                          # Core services, models, interceptors
│   ├── models/
│   │   ├── api.models.ts         # API response/request models
│   │   └── form.models.ts        # Form data models
│   ├── services/
│   │   ├── quote-api.service.ts          # Backend API calls
│   │   ├── quotation-wizard.service.ts   # Wizard state management
│   │   └── catalog.service.ts            # Catalog data
│   └── interceptors/
│       └── api-error.interceptor.ts      # Global error handling
├── shared/                        # Shared reusable components
│   └── components/
│       ├── text-field/
│       ├── select-field/
│       └── number-field/
├── features/                      # Feature modules
│   └── quotes/
│       ├── pages/
│       │   ├── quotes-list-page/         # Quotation listing
│       │   ├── quote-wizard-page/        # Multi-step wizard
│       │   └── quote-result-page/        # Calculation results
│       └── components/
│           ├── step-general-info/
│           ├── step-locations/
│           ├── step-coverages/
│           └── step-summary/
├── layout/                        # Layout components
│   └── main-layout/
└── environments/                  # Environment configs
    ├── environment.ts
    └── environment.prod.ts
```

## Key Principles

### 1. Backend-Driven Design
- **No Hardcoded Business Rules**: All calculation logic, factors, and percentages come from the backend
- **Dynamic Catalogs**: Occupancy types, construction types, and coverages are loaded from backend
- **Configuration-First**: Form requirements, validations, and dependencies are driven by API responses

### 2. Separation of Concerns
- **API Services**: All HTTP calls in dedicated service classes
- **Form Models**: Clear interfaces for each form step
- **Presentation Components**: Pure UI components with input/output decorators
- **Container Pages**: Orchestration logic separated from presentation

### 3. Reactive Forms & RxJS
- Strong typing with TypeScript interfaces
- Reactive programming using RxJS observables
- Form validation at the component level
- State management through services and observables

### 4. Reusable Components
- Generic form field components (TextFieldComponent, SelectFieldComponent, NumberFieldComponent)
- Form fields are ControlValueAccessor-compliant for integration with reactive forms
- Reusable summary and result components

## Quotation Wizard Flow

### Step 1: General Information
Captures:
- Customer name
- Currency selection
- Observations

### Step 2: Locations & Properties
- Add multiple locations
- Configure location-specific details:
  - Location name, city, department, postal code
  - Occupancy type (from catalog)
  - Construction type (from catalog)
  - Insured value
- Edit/remove locations
- Visual card-based interface

### Step 3: Coverage Options
- Select coverages from available catalog
- Configure per coverage:
  - Insured limit
  - Deductible type (fixed or percentage)
  - Deductible value
- Visual coverage selection with configuration

### Step 4: Summary & Review
- Review all entered information
- Collapsible sections for each step
- Validation status indicators
- Edit capability to return to any previous step
- Calculate button to trigger backend calculation

## Quote Calculation Result

After calculation, displays:
- **Total Premium**: Prominently displayed with gradient card
- **Risk Score**: If provided by backend
- **Calculation Factors**: Occupancy, zone, construction, risk adjustment
- **Location Breakdown**: Premium per location with applied factors
- **Exclusions**: Warnings for non-calculable locations
- **Actions**: Edit, save, print/export functionality

## API Integration

### Endpoint Usage

All endpoints consumed from backend `/v1` prefix:

```typescript
// Create quotation
POST /v1/folios
→ CreateFolioResponse { numeroFolio, createdAt }

// General Information
PUT /v1/quotes/{folio}/general-info
GET /v1/quotes/{folio}/general-info

// Locations
PUT /v1/quotes/{folio}/locations
GET /v1/quotes/{folio}/locations
PATCH /v1/quotes/{folio}/locations/{indice}
GET /v1/quotes/{folio}/locations/summary

// Coverage Options
PUT /v1/quotes/{folio}/coverage-options
GET /v1/quotes/{folio}/coverage-options

// Calculation & State
POST /v1/quotes/{folio}/calculate
GET /v1/quotes/{folio}/state
GET /v1/quotes/{folio}/locations/results
```

### Mock Catalog Service

Currently uses mock data. Replace with real backend API calls:

```typescript
// catalog.service.ts
private getMockCatalog(): Catalog {
  // Replace with: this.http.get('/v1/catalogs')
}
```

## Form Validation

### Reactive Forms Approach
- All forms use FormBuilder with Validators
- Validators applied at field and form level
- Real-time validation feedback
- Conditional validation based on previous selections

### Validation Rules
- Required fields: Customer name, locations, coverages
- Minimum lengths: Names, addresses
- Numeric ranges: Insured values, deductibles
- Cross-field validation: Location completeness
- Format validation: Postal codes (when required)

## State Management

### Wizard State Service
```typescript
QuotationWizardService manages:
- generalInfo: GeneralInfoFormData
- locations: LocationFormData[]
- coverages: CoverageFormData[]
- currentStep: number
```

### Persistence
- State persisted via service observable (BehaviorSubject)
- Data auto-saved to backend after each step
- Error handling for backend failures
- State available across component hierarchy

## Environment Configuration

### Development
```typescript
// environment.ts
export const environment = {
  production: false,
  apiBaseUrl: 'http://localhost:8080'
};
```

### Production
```typescript
// environment.prod.ts
export const environment = {
  production: true,
  apiBaseUrl: 'https://api.cotizador.com'
};
```

## Error Handling

### Global Error Interceptor
- Catches all HTTP errors
- Logs to console for debugging
- Propagates errors to components
- User-friendly error messages

### Component-Level Handling
- Try-catch for async operations
- Error state flags (loading, errorMessage)
- User feedback via alerts
- Retry capabilities

## Styling

### CSS Approach
- Global styles in `styles.css`
- Component-scoped styles via Angular styleUrls
- CSS variables for theming
- Responsive design (mobile-first)
- Flexbox and CSS Grid layouts

### Design System
- Colors: Primary (#3498db), Success (#27ae60), Warning, Danger
- Typography: System fonts, semantic sizing
- Spacing: Consistent rem-based units
- Shadows: Subtle elevation effects
- Transitions: Smooth 0.3s animations

## Development Workflow

### Setup
```bash
cd frontend
npm install
npm start
```

### Build
```bash
npm run build
```

### Testing
```bash
ng test
```

## Integration with Backend

### Expected Backend Responses

#### Quote Creation
```json
{
  "success": true,
  "data": {
    "numeroFolio": "FOL-2024-001",
    "createdAt": "2024-01-20T10:00:00Z"
  }
}
```

#### Calculation Result
```json
{
  "success": true,
  "data": {
    "folio": "FOL-2024-001",
    "totalPremium": 45000,
    "riskScore": 75,
    "locationResults": [
      {
        "indice": 1,
        "locationName": "Main Office",
        "basePremium": 20000,
        "totalPremium": 22500,
        "calculable": true,
        "appliedFactors": [
          { "name": "Occupancy", "value": 1.1, "percentage": 10 }
        ]
      }
    ],
    "breakdownFactors": {
      "occupancyFactor": 1.1,
      "zoneFactor": 0.95,
      "constructionFactor": 1.0,
      "totalFactor": 1.045
    }
  }
}
```

## Key Features

✅ **Multi-Step Wizard**: Guided user flow with step indicators
✅ **Dynamic Forms**: Catalogs loaded from backend
✅ **Live Validation**: Real-time feedback with error messages
✅ **Backend Integration**: All data flows through API
✅ **Result Visualization**: Clear premium breakdown and factors
✅ **Responsive Design**: Works on desktop and tablet
✅ **Professional UI**: Enterprise-grade styling
✅ **Type Safety**: Full TypeScript typing throughout
✅ **Error Handling**: Comprehensive error management
✅ **Scalable Architecture**: Easy to extend and maintain

## Future Enhancements

- [ ] Add custom risk factor options (Step 5)
- [ ] Implement quotation saving and retrieval
- [ ] Add print/PDF export functionality
- [ ] Multi-language support (i18n)
- [ ] Advanced filters and search on quotation list
- [ ] History and audit trail
- [ ] Real-time calculation preview
- [ ] Quotation comparison feature
- [ ] Role-based access control
- [ ] Mobile app version

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Dependencies

- Angular 17.3
- TypeScript 5.4
- RxJS 7.8
- Angular Forms & Reactive Forms
- Angular HTTP Client

## License

Proprietary - Cotizador de Daños System

