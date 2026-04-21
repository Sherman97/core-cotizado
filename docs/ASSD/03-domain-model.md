# 03. Domain Model

## Modelo de dominio implementado

El agregado principal es `Quote`.  
Subdominios funcionales implementados: `quote`, `location`, `coverage`, `calculation`.

## Entidades/objetos de dominio clave

### Quote

- identidad: `folio`
- estado: `DRAFT`, `CALCULATED`
- version de negocio: `version`
- metadatos temporales: `createdAt`, `modifiedAt`
- datos generales y layout

Comportamiento relevante:

- creacion inicial (`version=1`)
- actualizacion parcial de datos generales
- actualizacion de layout
- incremento de version de negocio en ediciones funcionales
- transicion a `CALCULATED` en calculo

### QuoteLocation

- datos de ubicacion
- `validationStatus`: `COMPLETE`, `INCOMPLETE`, `INVALID`
- alertas por ubicacion

### QuoteCoverageSelection

- cobertura configurada por folio
- bandera `selected` usada en calculabilidad

### QuoteCalculationResult y LocationCalculationResult

- resultado financiero consolidado
- resultado por ubicacion
- alertas globales y por ubicacion

### CalculationTraceDetail

- trazabilidad de factores aplicados en el calculo
- metadata por factor

## Invariantes aplicados en el backend

- una quote nueva inicia en `DRAFT`
- cada edicion funcional incrementa version de negocio
- cada edicion funcional actualiza `modifiedAt`
- ubicaciones no calculables no entran a prima
- la exclusion de ubicaciones conserva razon en alertas
- el resultado financiero y la trazabilidad se persisten

## Reglas de modelado conservador explicitas

Por ausencia de campos dedicados en el dominio actual:

- `giro.claveIncendio` se mapea a `occupancyType`
- `garantias tarifables` se mapean a coberturas con `selected=true`

Estos mapeos son deliberados, documentados y limitados al MVP actual.
