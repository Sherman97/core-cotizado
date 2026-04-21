# 05. Data Design

## Persistencia implementada

- Motor runtime: MariaDB
- ORM: Spring Data JPA
- Migraciones: Flyway

## Migraciones vigentes

- `V1__init_schema.sql`
- `V2__seed_coverage_catalog.sql`
- `V3__add_folio_idempotency.sql`
- `V4__create_rating_master_tables.sql`
- `V5__seed_postal_code_zones.sql`
- `V6__seed_zone_factors.sql`
- `V7__seed_occupancy_catalog.sql`
- `V8__seed_occupancy_factors.sql`
- `V9__seed_construction_factors.sql`
- `V10__seed_coverage_rate_tables.sql`
- `V11__seed_coverage_factor_tables.sql`
- `V12__seed_calculation_parameters.sql`

## Tablas operativas principales

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

## Tablas maestras de rating integradas

- `postal_code_zone_map`
- `zone_factors`
- `occupancy_catalog`
- `occupancy_factors`
- `construction_factors`
- `coverage_rate_tables`
- `coverage_factor_tables`
- `calculation_parameters`

## Codigos tecnicos canonicos (normalizados)

- `product_code`: `DANOS`
- `coverage_code`: `FIRE`, `EARTHQUAKE`, `FLOOD`
- `occupancy_code`: `OFFICE`, `COMMERCE`, `RESTAURANT`, `WAREHOUSE`, `LIGHT_INDUSTRY`
- `construction_type`: `CONCRETE`, `MIXED`, `WOOD`

## Idempotencia de folios

Tabla:

- `folio_idempotency_keys(idempotency_key, folio, created_at)`

Uso:

- evita duplicidad funcional de creacion ante reintentos de cliente

## Versionado optimista

Implementado en:

- `quotes.lock_version`
- `quote_calculation_results.lock_version`

Conflictos traducidos a `409 CONFLICT` en API.

## Persistencia de trazabilidad de calculo

- `calculation_traces`
- `calculation_trace_metadata`

Guarda factor, valor aplicado, orden y metadata por ubicacion/quote.

## Nota de entorno de pruebas

- perfil `test` usa H2 en memoria
- runtime local usa MariaDB
