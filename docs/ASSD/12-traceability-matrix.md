# 12. Traceability Matrix

## Objetivo

Trazar requerimientos del reto contra implementacion real del backend final.

| Requerimiento | Implementacion | Evidencia de prueba | Estado |
| --- | --- | --- | --- |
| Crear folio | `POST /v1/folios` + `CreateQuoteUseCase` | `CreateQuoteUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Idempotencia de creacion de folio | `Idempotency-Key` + `CreateQuoteWithIdempotencyUseCase` + `folio_idempotency_keys` | `CreateQuoteWithIdempotencyUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Guardar/consultar general-info | `GET/PUT /v1/quotes/{folio}/general-info` | `UpdateQuoteGeneralDataUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Guardar/consultar layout | `GET/PUT /v1/quotes/{folio}/locations/layout` | `SaveQuoteLocationLayoutUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Guardar/editar/listar ubicaciones | `PUT/PATCH/GET /v1/quotes/{folio}/locations` | `ReplaceQuoteLocationsUseCaseTest`, `UpdateQuoteLocationUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Resumen de ubicaciones | `GET /v1/quotes/{folio}/locations/summary` | `QuoteApiE2ETest` | Cumplido |
| Guardar/consultar coberturas | `PUT/GET /v1/quotes/{folio}/coverage-options` | `ConfigureQuoteCoveragesUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Incremento de version en ediciones parciales | `Quote.update*` + `incrementBusinessVersion` en casos de uso de edicion | tests de quote/location/coverage actualizados | Cumplido |
| Calculo por ubicacion | `POST /v1/quotes/{folio}/calculate` | `CalculateQuoteUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Elegibilidad: postal valido | validacion en `CalculateQuoteUseCase` | `CalculateQuoteUseCaseTest` | Cumplido |
| Elegibilidad: giro.claveIncendio | mapeado a `occupancyType` en `CalculateQuoteUseCase` | `CalculateQuoteUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Elegibilidad: garantias tarifables | mapeado a `selected=true` en coberturas | `CalculateQuoteUseCaseTest` | Cumplido |
| Persistir resultado financiero | `QuoteCalculationResultRepositoryAdapter` | `CalculateQuoteTraceabilityTest`, `CriticalBackendFlowsTest` | Cumplido |
| Persistir trazabilidad de calculo | `CalculationTraceRepositoryAdapter` | `CalculateQuoteTraceabilityTest` | Cumplido |
| Cambiar estado final de quote | `quote.markAsCalculated()` en calculo | `CalculateQuoteUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Consultar estado final | `GET /v1/quotes/{folio}/state` | `GetQuoteFinalStatusUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Pruebas E2E REST minimas (3) | `QuoteApiE2ETest` | suite `./gradlew test` | Cumplido |

## Nota de mapeos conservadores

Se documenta explicitamente:

- `occupancyType` como representacion temporal de `giro.claveIncendio`.
- `selected=true` como representacion temporal de garantia tarifable.
