# 07. API Design

## Base path

Todos los endpoints se publican bajo `/v1`.

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

## Envelope de respuesta

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

## Contrato idempotente de `POST /v1/folios`

Header opcional:

- `Idempotency-Key`

Reglas:

- clave nueva -> crea folio, `201`
- clave repetida -> replay del mismo folio, `200`
- sin clave -> crea folio, `201`

## Errores HTTP relevantes

- `400`: validacion y request invalido
- `404`: folio o ubicacion no encontrada
- `409`: conflicto de version optimista
- `500`: error interno no controlado

## Reglas visibles desde API de calculo

En `POST /v1/quotes/{folio}/calculate`, puede haber alertas por exclusion de ubicacion:

- codigo postal invalido
- falta de `giro.claveIncendio` (mapeado a `occupancyType`)
- falta de garantias tarifables (mapeado a `selected=true`)

La exclusion es por ubicacion; el backend calcula el resto de ubicaciones elegibles.
