# 02. Business Flow

## Flujo operativo implementado

1. `POST /v1/folios`
2. `PUT /v1/quotes/{folio}/general-info`
3. `PUT /v1/quotes/{folio}/locations/layout`
4. `PUT /v1/quotes/{folio}/locations`
5. `PUT /v1/quotes/{folio}/coverage-options`
6. `POST /v1/quotes/{folio}/calculate`
7. `GET /v1/quotes/{folio}/state`

## Reglas de negocio implementadas

### Creacion de folio e idempotencia

- con `Idempotency-Key` nueva: crea y responde `201`
- con `Idempotency-Key` repetida: replay del mismo folio y responde `200`
- sin `Idempotency-Key`: crea normalmente y responde `201`

### Versionado de negocio

Se incrementa `businessVersion` y se actualiza `modifiedAt` en:

- general-info
- locations/layout
- locations (replace y patch)
- coverage-options

### Regla de calculabilidad por ubicacion

Una ubicacion se excluye de calculo si:

- no tiene codigo postal valido
- no tiene `giro.claveIncendio`
- no tiene garantias tarifables

Mapeos conservadores del dominio actual:

- `giro.claveIncendio` -> `occupancyType`
- `garantias tarifables` -> coberturas con `selected=true`

### Resultado del calculo

- se calculan solo ubicaciones elegibles
- se consolida prima neta, gastos, impuestos y prima comercial
- se persisten alertas y trazabilidad
- la quote termina en estado `CALCULATED`

## Flujos cubiertos por E2E REST

1. creacion de folio + replay idempotente
2. captura y edicion de ubicaciones
3. cotizacion completa con calculo y consulta de estado final
