# 07. API Design

## Base path

Todos los endpoints implementados usan base `/v1`.

## Endpoints implementados

- `POST /v1/folios`
- `GET /v1/quotes/{folio}/general-info`
- `PUT /v1/quotes/{folio}/general-info`
- `GET /v1/quotes/{folio}/locations/layout`
- `PUT /v1/quotes/{folio}/locations/layout`
- `GET /v1/quotes/{folio}/locations`
- `PUT /v1/quotes/{folio}/locations`
- `PATCH /v1/quotes/{folio}/locations/{indice}`
- `GET /v1/quotes/{folio}/locations/summary`
- `GET /v1/quotes/{folio}/locations/results`
- `GET /v1/quotes/{folio}/coverage-options`
- `PUT /v1/quotes/{folio}/coverage-options`
- `POST /v1/quotes/{folio}/calculate`
- `GET /v1/quotes/{folio}/state`

## Contrato de respuesta

Exito:

```json
{
  "data": {}
}
```

Error:

```json
{
  "timestamp": "2026-04-21T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error",
  "path": "/v1/...",
  "validationErrors": []
}
```

## Idempotencia en `POST /v1/folios`

Header opcional:

- `Idempotency-Key`

Reglas:

- clave nueva -> crea folio (`201`)
- clave ya registrada -> replay del mismo folio (`200`)
- sin clave -> crea folio (`201`)

## Errores HTTP relevantes

- `400`: validacion de request, body malformado
- `404`: folio o ubicacion inexistente
- `409`: conflicto de version optimista
- `500`: error inesperado

## Validaciones de request destacadas

- ubicaciones: `locationName` no vacio, `insuredValue >= 0`
- coberturas: `coverageCode` y `coverageName` obligatorios, montos no negativos
- layout: `expectedLocationCount >= 0`

## Notas de consistencia con calculo

El endpoint `POST /v1/quotes/{folio}/calculate` puede devolver alertas por ubicaciones excluidas por elegibilidad:

- codigo postal invalido
- falta de `giro.claveIncendio` (mapeado a `occupancyType`)
- falta de garantias tarifables (mapeadas a `selected=true`)
