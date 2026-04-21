# 05. Data Design

## Persistencia implementada

Motor runtime: MariaDB.  
ORM: Spring Data JPA.  
Migraciones: Flyway.

## Migraciones actuales

- `V1__init_schema.sql`
- `V2__seed_coverage_catalog.sql`
- `V3__add_folio_idempotency.sql`

## Tablas principales implementadas

- `quotes`
- `quote_locations`
- `quote_location_alerts`
- `quote_coverages`
- `coverage_catalog`
- `quote_calculation_results`
- `quote_calculation_alerts`
- `location_calculation_results`
- `location_calculation_alerts`
- `calculation_traces`
- `calculation_trace_metadata`
- `folio_idempotency_keys`

## Persistencia de idempotencia

Tabla:

- `folio_idempotency_keys(idempotency_key, folio, created_at)`

Uso:

- guarda relacion entre `Idempotency-Key` y folio creado
- permite replay consistente en retries

## Versionado optimista

Implementado con `@Version` en:

- `quotes.lock_version`
- `quote_calculation_results.lock_version`

Manejo API:

- conflicto optimista se responde como `409 CONFLICT`

## Persistencia de calculo y trazabilidad

Resultado financiero:

- `quote_calculation_results`
- `location_calculation_results`

Alertas:

- `quote_calculation_alerts`
- `location_calculation_alerts`

Trazabilidad:

- `calculation_traces`
- `calculation_trace_metadata`

## Catalogo de coberturas seed

Inicializado por migracion:

- `INCENDIO`
- `TERREMOTO`
- `INUNDACION`

## Notas de pruebas

Para pruebas E2E REST:

- perfil `test` usa H2 en memoria
- schema test agrega secuencia `quote_location_seq`
- runtime productivo local sigue en MariaDB
