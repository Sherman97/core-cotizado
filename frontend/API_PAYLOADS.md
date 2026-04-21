# API Payloads & Examples

This document shows example request and response payloads for the quotation system.

## 1. Create Folio (Quote)

### Request
```
POST /v1/folios
```

No body required. Optional header:
```
Idempotency-Key: unique-key-12345
```

### Response
```json
{
  "success": true,
  "data": {
    "numeroFolio": "FOL-2024-001",
    "createdAt": "2024-01-20T10:00:00Z"
  },
  "timestamp": "2024-01-20T10:00:00Z"
}
```

---

## 2. Save General Information

### Request
```
PUT /v1/quotes/FOL-2024-001/general-info
```

```json
{
  "productCode": "DANOS",
  "customerName": "Comercial Andina SAS",
  "currency": "COP",
  "observations": "Important client, priority handling"
}
```

### Response
```json
{
  "success": true,
  "data": {
    "productCode": "DANOS",
    "customerName": "Comercial Andina SAS",
    "currency": "COP",
    "observations": "Important client, priority handling",
    "businessVersion": 1,
    "modifiedAt": "2024-01-20T10:05:00Z"
  },
  "timestamp": "2024-01-20T10:05:00Z"
}
```

---

## 3. Save Locations

### Request
```
PUT /v1/quotes/FOL-2024-001/locations
```

```json
{
  "locations": [
    {
      "locationName": "Matriz Centro",
      "city": "Bogota",
      "department": "Cundinamarca",
      "address": "Calle 100 #10-20",
      "postalCode": "110111",
      "constructionType": "CONCRETE",
      "occupancyType": "OFFICE",
      "insuredValue": 1500000
    },
    {
      "locationName": "Sucursal Norte",
      "city": "Bogota",
      "department": "Cundinamarca",
      "address": "Calle 80 #15-10",
      "postalCode": "110221",
      "constructionType": "CONCRETE",
      "occupancyType": "COMMERCE",
      "insuredValue": 900000
    }
  ]
}
```

### Response
```json
{
  "success": true,
  "data": {
    "locations": [
      {
        "indice": 1,
        "locationName": "Matriz Centro",
        "city": "Bogota",
        "department": "Cundinamarca",
        "address": "Calle 100 #10-20",
        "postalCode": "110111",
        "constructionType": "CONCRETE",
        "occupancyType": "OFFICE",
        "insuredValue": 1500000
      },
      {
        "indice": 2,
        "locationName": "Sucursal Norte",
        "city": "Bogota",
        "department": "Cundinamarca",
        "address": "Calle 80 #15-10",
        "postalCode": "110221",
        "constructionType": "CONCRETE",
        "occupancyType": "COMMERCE",
        "insuredValue": 900000
      }
    ],
    "businessVersion": 2,
    "modifiedAt": "2024-01-20T10:10:00Z"
  },
  "timestamp": "2024-01-20T10:10:00Z"
}
```

---

## 4. Save Coverage Options

### Request
```
PUT /v1/quotes/FOL-2024-001/coverage-options
```

```json
{
  "coverages": [
    {
      "coverageCode": "FIRE",
      "coverageName": "Fire",
      "insuredLimit": 1000000,
      "deductibleType": "FIXED",
      "deductibleValue": 50000,
      "selected": true
    },
    {
      "coverageCode": "EARTHQUAKE",
      "coverageName": "Earthquake",
      "insuredLimit": 750000,
      "deductibleType": "PERCENTAGE",
      "deductibleValue": 5,
      "selected": true
    },
    {
      "coverageCode": "FLOOD",
      "coverageName": "Flood",
      "insuredLimit": 500000,
      "deductibleType": "FIXED",
      "deductibleValue": 100000,
      "selected": false
    }
  ]
}
```

### Response
```json
{
  "success": true,
  "data": {
    "coverages": [
      {
        "coverageCode": "FIRE",
        "coverageName": "Fire",
        "insuredLimit": 1000000,
        "deductibleType": "FIXED",
        "deductibleValue": 50000,
        "selected": true
      },
      {
        "coverageCode": "EARTHQUAKE",
        "coverageName": "Earthquake",
        "insuredLimit": 750000,
        "deductibleType": "PERCENTAGE",
        "deductibleValue": 5,
        "selected": true
      }
    ],
    "businessVersion": 3,
    "modifiedAt": "2024-01-20T10:15:00Z"
  },
  "timestamp": "2024-01-20T10:15:00Z"
}
```

---

## 5. Calculate Quote

### Request
```
POST /v1/quotes/FOL-2024-001/calculate
```

No body required.

### Response (Success - All Locations Calculable)
```json
{
  "success": true,
  "data": {
    "folio": "FOL-2024-001",
    "totalPremium": 45230.50,
    "riskScore": 72,
    "calculatedAt": "2024-01-20T10:20:00Z",
    "locationResults": [
      {
        "indice": 1,
        "locationName": "Matriz Centro",
        "basePremium": 25000,
        "totalPremium": 27450.50,
        "calculable": true,
        "appliedFactors": [
          {
            "name": "occupancyFactor",
            "value": 1.1,
            "percentage": 10
          },
          {
            "name": "zoneFactor",
            "value": 0.95,
            "percentage": -5
          },
          {
            "name": "constructionFactor",
            "value": 1.05,
            "percentage": 5
          }
        ]
      },
      {
        "indice": 2,
        "locationName": "Sucursal Norte",
        "basePremium": 15000,
        "totalPremium": 17780,
        "calculable": true,
        "appliedFactors": [
          {
            "name": "occupancyFactor",
            "value": 1.15,
            "percentage": 15
          },
          {
            "name": "zoneFactor",
            "value": 0.98,
            "percentage": -2
          },
          {
            "name": "constructionFactor",
            "value": 1.05,
            "percentage": 5
          }
        ]
      }
    ],
    "breakdownFactors": {
      "occupancyFactor": 1.125,
      "zoneFactor": 0.965,
      "constructionFactor": 1.05,
      "riskAdjustment": 1.0,
      "totalFactor": 1.143
    },
    "warnings": []
  },
  "timestamp": "2024-01-20T10:20:00Z"
}
```

### Response (With Exclusions - Some Locations Not Calculable)
```json
{
  "success": true,
  "data": {
    "folio": "FOL-2024-001",
    "totalPremium": 27450.50,
    "riskScore": 68,
    "calculatedAt": "2024-01-20T10:20:00Z",
    "locationResults": [
      {
        "indice": 1,
        "locationName": "Matriz Centro",
        "basePremium": 25000,
        "totalPremium": 27450.50,
        "calculable": true,
        "appliedFactors": [
          {
            "name": "occupancyFactor",
            "value": 1.1,
            "percentage": 10
          },
          {
            "name": "zoneFactor",
            "value": 0.95,
            "percentage": -5
          },
          {
            "name": "constructionFactor",
            "value": 1.05,
            "percentage": 5
          }
        ]
      },
      {
        "indice": 2,
        "locationName": "Sucursal Norte",
        "basePremium": 0,
        "totalPremium": 0,
        "calculable": false,
        "excludedReason": "Missing postal code and occupancy type classification",
        "appliedFactors": []
      }
    ],
    "breakdownFactors": {
      "occupancyFactor": 1.1,
      "zoneFactor": 0.95,
      "constructionFactor": 1.05,
      "riskAdjustment": 1.0,
      "totalFactor": 1.1025
    },
    "warnings": [
      "Location 'Sucursal Norte' was excluded from calculation due to incomplete risk information."
    ]
  },
  "timestamp": "2024-01-20T10:20:00Z"
}
```

---

## 6. Get Quote State

### Request
```
GET /v1/quotes/FOL-2024-001/state
```

### Response
```json
{
  "success": true,
  "data": {
    "folio": "FOL-2024-001",
    "status": "CALCULATED",
    "createdAt": "2024-01-20T10:00:00Z",
    "modifiedAt": "2024-01-20T10:20:00Z",
    "businessVersion": 3,
    "generalInfo": {
      "productCode": "DANOS",
      "customerName": "Comercial Andina SAS",
      "currency": "COP",
      "observations": "Important client, priority handling",
      "businessVersion": 1,
      "modifiedAt": "2024-01-20T10:05:00Z"
    },
    "locations": [
      {
        "indice": 1,
        "locationName": "Matriz Centro",
        "city": "Bogota",
        "department": "Cundinamarca",
        "address": "Calle 100 #10-20",
        "postalCode": "110111",
        "constructionType": "CONCRETE",
        "occupancyType": "OFFICE",
        "insuredValue": 1500000
      },
      {
        "indice": 2,
        "locationName": "Sucursal Norte",
        "city": "Bogota",
        "department": "Cundinamarca",
        "address": "Calle 80 #15-10",
        "postalCode": "110221",
        "constructionType": "CONCRETE",
        "occupancyType": "COMMERCE",
        "insuredValue": 900000
      }
    ],
    "coverages": [
      {
        "coverageCode": "FIRE",
        "coverageName": "Fire",
        "insuredLimit": 1000000,
        "deductibleType": "FIXED",
        "deductibleValue": 50000,
        "selected": true
      },
      {
        "coverageCode": "EARTHQUAKE",
        "coverageName": "Earthquake",
        "insuredLimit": 750000,
        "deductibleType": "PERCENTAGE",
        "deductibleValue": 5,
        "selected": true
      }
    ],
    "calculationResult": {
      "folio": "FOL-2024-001",
      "totalPremium": 45230.50,
      "riskScore": 72,
      "calculatedAt": "2024-01-20T10:20:00Z",
      "locationResults": [
        {
          "indice": 1,
          "locationName": "Matriz Centro",
          "basePremium": 25000,
          "totalPremium": 27450.50,
          "calculable": true,
          "appliedFactors": [...]
        },
        {
          "indice": 2,
          "locationName": "Sucursal Norte",
          "basePremium": 15000,
          "totalPremium": 17780,
          "calculable": true,
          "appliedFactors": [...]
        }
      ],
      "breakdownFactors": {
        "occupancyFactor": 1.125,
        "zoneFactor": 0.965,
        "constructionFactor": 1.05,
        "riskAdjustment": 1.0,
        "totalFactor": 1.143
      }
    }
  },
  "timestamp": "2024-01-20T10:20:00Z"
}
```

---

## 7. Error Response Examples

### Bad Request - Validation Error
```json
{
  "success": false,
  "message": "Validation error: Customer name is required",
  "data": null,
  "timestamp": "2024-01-20T10:25:00Z"
}
```

### Not Found - Folio Does Not Exist
```json
{
  "success": false,
  "message": "Quote with folio FOL-INVALID not found",
  "data": null,
  "timestamp": "2024-01-20T10:25:00Z"
}
```

### Server Error
```json
{
  "success": false,
  "message": "Internal server error during calculation",
  "data": null,
  "timestamp": "2024-01-20T10:25:00Z"
}
```

---

## Frontend Model Mapping

### GeneralInfoFormData → API Model
```typescript
// Form data captured
const formData: GeneralInfoFormData = {
  productCode: 'DANOS',
  customerName: 'Comercial Andina SAS',
  currency: 'COP',
  observations: 'Notes'
};

// Sent to backend as-is
POST /v1/quotes/{folio}/general-info
Body: formData
```

### LocationFormData → API Model
```typescript
// Frontend form captures
const location: LocationFormData = {
  locationName: 'Matriz Centro',
  city: 'Bogota',
  department: 'Cundinamarca',
  address: 'Calle 100 #10-20',
  postalCode: '110111',
  constructionType: 'CONCRETE',  // From catalog
  occupancyType: 'OFFICE',       // From catalog
  insuredValue: 1500000
};

// Wrapped in LocationsPayload
const payload: LocationsPayload = {
  locations: [location1, location2, ...]
};

// Sent to backend
PUT /v1/quotes/{folio}/locations
Body: payload
```

### CoverageFormData → API Model
```typescript
// Frontend form captures coverage selection
const coverage: CoverageFormData = {
  coverageCode: 'FIRE',
  coverageName: 'Fire',
  insuredLimit: 1000000,
  deductibleType: 'FIXED',
  deductibleValue: 50000,
  selected: true
};

// Multiple coverages wrapped
const payload: CoverageOptions = {
  coverages: [coverage1, coverage2, ...]
};

// Sent to backend
PUT /v1/quotes/{folio}/coverage-options
Body: payload
```

---

## Example Frontend Workflow

```typescript
// 1. Create quote
quoteApi.createFolio() → FOL-2024-001

// 2. Save step 1
quoteApi.saveGeneralInfo('FOL-2024-001', {...})

// 3. Save step 2
quoteApi.saveLocations('FOL-2024-001', {...})

// 4. Save step 3
quoteApi.saveCoverageOptions('FOL-2024-001', {...})

// 5. Calculate
quoteApi.calculateQuote('FOL-2024-001')

// 6. Get result
quoteApi.getQuoteState('FOL-2024-001')
  → Shows calculationResult with totalPremium, factors, warnings
```

---

## Catalog Data Example (Mock)

Currently mocked in `CatalogService`:

```typescript
{
  occupancyTypes: [
    { code: 'OFFICE', name: 'Office', description: '...' },
    { code: 'COMMERCE', name: 'Commerce', description: '...' },
    { code: 'RESTAURANT', name: 'Restaurant', description: '...' },
    { code: 'WAREHOUSE', name: 'Warehouse', description: '...' },
    { code: 'LIGHT_INDUSTRY', name: 'Light Industry', description: '...' }
  ],
  constructionTypes: [
    { code: 'CONCRETE', name: 'Concrete', description: '...' },
    { code: 'MIXED', name: 'Mixed', description: '...' },
    { code: 'WOOD', name: 'Wood', description: '...' }
  ],
  coverageTypes: [
    { code: 'FIRE', name: 'Fire', description: '...', maxInsuredLimit: 5000000 },
    { code: 'EARTHQUAKE', name: 'Earthquake', description: '...', maxInsuredLimit: 3000000 },
    { code: 'FLOOD', name: 'Flood', description: '...', maxInsuredLimit: 2000000 }
  ]
}
```

Replace mock with:
```typescript
GET /v1/catalogs → Returns Catalog object
```

