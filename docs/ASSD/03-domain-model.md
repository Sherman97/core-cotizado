# 03. Domain Model

## Agregado principal

`Quote` es el agregado raiz del backend.

Responsabilidades:

- identidad por `folio`
- estado (`DRAFT`, `CALCULATED`)
- version de negocio (`businessVersion`)
- datos generales y layout
- control de timestamps (`createdAt`, `modifiedAt`)

## Objetos de dominio relevantes

### `QuoteLocation`

- datos de ubicacion
- estado de validacion (`COMPLETE`, `INCOMPLETE`, `INVALID`)
- alertas por ubicacion

### `QuoteCoverageSelection`

- cobertura configurada para la quote
- flag `selected` usado en regla de garantias tarifables

### `QuoteCalculationResult` y `LocationCalculationResult`

- resultado consolidado y por ubicacion
- alertas de calculo

### `CalculationTraceDetail`

- factor aplicado
- valor aplicado
- orden del factor
- metadata explicativa

## Reglas de dominio implementadas

- Quote nueva inicia en `DRAFT`.
- En operaciones funcionales relevantes se incrementa `businessVersion`.
- En operaciones funcionales relevantes se actualiza `modifiedAt`.
- Solo ubicaciones elegibles entran a prima.
- Ubicaciones no elegibles se excluyen con alerta explicita.
- Al calcular se persisten resultado y trazabilidad.

## Mapeos conservadores declarados

- `occupancyType` se usa como representacion temporal de `giro.claveIncendio`.
- `selected=true` se usa como representacion temporal de garantia tarifable.

## Modelo de catalogos de rating (integrado en fase read-only)

Se agregaron puertos de dominio para consulta de datos maestros:

- zona por codigo postal y factor de zona
- ocupacion y factor de ocupacion
- factor de construccion
- tasas y factores por cobertura
- parametros globales de calculo

Estos puertos estan implementados por adapters JPA, pero aun no reemplazan el motor de calculo MVP.
