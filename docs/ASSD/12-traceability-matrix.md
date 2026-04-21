# 12. Traceability Matrix

## Objetivo

Mapear requerimientos funcionales y tecnicos contra implementacion real y evidencia de prueba.

| Requerimiento | Implementacion | Evidencia | Estado |
| --- | --- | --- | --- |
| Crear folio | `POST /v1/folios`, `CreateQuoteUseCase` | `CreateQuoteUseCaseTest` | Cumplido |
| Idempotencia creacion de folio | `CreateQuoteWithIdempotencyUseCase`, tabla `folio_idempotency_keys` | `CreateQuoteWithIdempotencyUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| `201` en create y `200` en replay idempotente | `FolioController` | `QuoteApiE2ETest` | Cumplido |
| Editar `general-info` | `UpdateQuoteGeneralDataUseCase` | `UpdateQuoteGeneralDataUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Editar layout de ubicaciones | `SaveQuoteLocationLayoutUseCase` | `SaveQuoteLocationLayoutUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Alta/edicion/listado de ubicaciones | use cases de `location` | tests `Create/Update/List/ReplaceQuoteLocations*`, `QuoteApiE2ETest` | Cumplido |
| Configurar coberturas | `ConfigureQuoteCoveragesUseCase` | `ConfigureQuoteCoveragesUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Versionado de negocio en ediciones parciales | incremento de `businessVersion` + `modifiedAt` | `QuoteGeneralDataTest`, `QuoteVersioningTest`, tests de application | Cumplido |
| Calcular quote | `CalculateQuoteUseCase` | `CalculateQuoteUseCaseTest`, `CriticalBackendFlowsTest`, `QuoteApiE2ETest` | Cumplido |
| Excluir ubicacion por postal invalido | regla en `CalculateQuoteUseCase` | `CalculateQuoteUseCaseTest` | Cumplido |
| Excluir ubicacion por falta de giro.claveIncendio | mapeo a `occupancyType` | `CalculateQuoteUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| Excluir ubicacion por falta de garantias tarifables | mapeo a `selected=true` | `CalculateQuoteUseCaseTest` | Cumplido |
| Persistir resultado financiero | `QuoteCalculationResultRepositoryAdapter` | `GetQuoteLocationResultsUseCaseTest`, `CriticalBackendFlowsTest` | Cumplido |
| Persistir trazabilidad | `CalculationTraceRepositoryAdapter` | `CalculateQuoteTraceabilityTest` | Cumplido |
| Consultar estado final | `GetQuoteFinalStatusUseCase` + endpoint `/state` | `GetQuoteFinalStatusUseCaseTest`, `QuoteApiE2ETest` | Cumplido |
| E2E REST minimos | clase `QuoteApiE2ETest` (3 escenarios) | `./gradlew test` | Cumplido |
| Integrar tablas maestras (fase read-only) | modulo `catalog` con entities + repos + adapters + query services | `CatalogQueryServicesTest` | Cumplido |

## Nota de trazabilidad de supuestos

Mapeos conservadores vigentes y explicitos:

- `occupancyType` como representacion temporal de `giro.claveIncendio`.
- `selected=true` como representacion temporal de garantia tarifable.
