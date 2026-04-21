# ASSD Backend Implementado - Cotizador de Seguros de Danos

## 1. Overview del sistema

El backend implementa un cotizador de seguros de danos en arquitectura monolito modular con Spring Boot.  
El flujo operativo soportado es:

1. Crear folio.
2. Capturar informacion general de la cotizacion.
3. Configurar layout de ubicaciones.
4. Registrar y editar ubicaciones.
5. Configurar coberturas.
6. Ejecutar calculo.
7. Consultar resultados y estado final.

El backend corre sobre Java 21, Gradle, Spring Web, Spring Data JPA, Flyway y MariaDB.

## 2. Alcance del backend implementado

Incluye:

- API REST para folios, datos generales, layout, ubicaciones, coberturas, calculo y estado.
- Persistencia JPA completa para quote, locations, coverages, calculation result y trace.
- Migraciones de esquema y seed de catalogo de coberturas.
- Manejo uniforme de errores API.
- Pruebas unitarias y pruebas de flujo automatizadas.

No incluye:

- Frontend.
- Autenticacion/autorizacion.
- Integraciones reales con servicios externos de core.
- Modulos funcionales de `catalog` y `document` (solo placeholders estructurales).

## 3. Arquitectura

### 3.1 Estilo arquitectonico

- Monolito modular por dominio (`quote`, `location`, `coverage`, `calculation`, `shared`).
- Clean architecture por capas:
  - `domain`: reglas de negocio puras.
  - `application`: casos de uso.
  - `api`: controllers + DTO + mappers API.
  - `infrastructure`: entidades JPA, repositorios Spring Data, adapters de persistencia.

### 3.2 Composicion tecnica

- Aplicacion principal: `com.cotizador.danos.DanosBackendApplication`.
- Wiring de casos de uso y stubs: `shared/config/UseCaseConfig`.
- Bean comun de tiempo: `shared/config/ApplicationBeansConfig`.
- Perfiles:
  - `mariadb`: datasource + JPA + Flyway habilitados.
  - `local`: excluye `DataSourceAutoConfiguration`.

### 3.3 Mocks/Stubs existentes

- `StubPremiumCalculator`: formula simplificada y trazable para prima por ubicacion.
- `FolioGenerator` in-memory con `AtomicLong` en `UseCaseConfig`.

No hay cliente HTTP ni adapter implementado hacia servicios externos.

## 4. Modelo de dominio

### 4.1 Agregado principal

`Quote` es el agregado principal y contiene:

- identidad: `folio`, `parentQuoteFolio`.
- datos generales: `productCode`, `customerName`, `currency`, `observations`.
- layout: `QuoteLocationLayout`.
- estado: `QuoteStatus` (`DRAFT`, `CALCULATED`).
- version de negocio: `version`.
- fechas: `createdAt`, `modifiedAt`.

### 4.2 Subdominios relevantes

- `location`:
  - `QuoteLocation` con `LocationValidationStatus` (`COMPLETE`, `INCOMPLETE`, `INVALID`).
  - Regla de completitud implementada: direccion + codigo postal requeridos.
- `coverage`:
  - `QuoteCoverageSelection` por folio y codigo de cobertura.
- `calculation`:
  - `QuoteCalculationResult`, `LocationCalculationResult`, `CalculationTraceDetail`.

### 4.3 Reglas implementadas en dominio/aplicacion

- Ubicaciones incompletas no bloquean el calculo total.
- Ubicaciones invalidas se excluyen del calculo.
- Si no hay coberturas configuradas, se generan alertas y no se calcula prima por ubicacion completa.
- Resultado de calculo consolida `primaNeta`, `gastos`, `impuestos`, `primaComercial`.
- Al calcular, la cotizacion pasa a estado `CALCULATED`.

## 5. Diseno de persistencia

### 5.1 Base de datos y migraciones

- Motor: MariaDB.
- Migraciones Flyway:
  - `V1__init_schema.sql`: esquema completo.
  - `V2__seed_coverage_catalog.sql`: seed de catalogo (`INCENDIO`, `TERREMOTO`, `INUNDACION`).

### 5.2 Modelo relacional implementado

Tablas principales:

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

### 5.3 Entidades JPA y adapters

- Quote:
  - entidad: `QuoteJpaEntity`
  - adapter: `QuoteRepositoryAdapter`
  - mapper: `QuotePersistenceMapper`
- Location:
  - entidad: `QuoteLocationJpaEntity`
  - adapter: `LocationRepositoryAdapter`
  - mapper: `QuoteLocationPersistenceMapper`
- Coverage:
  - entidades: `QuoteCoverageJpaEntity`, `CoverageCatalogJpaEntity`
  - adapters: `QuoteCoverageRepositoryAdapter`, `CoverageCatalogRepositoryAdapter`
  - mapper: `QuoteCoveragePersistenceMapper`
- Calculation:
  - entidades: `QuoteCalculationResultJpaEntity`, `LocationCalculationResultJpaEntity`, `CalculationTraceJpaEntity`
  - adapters: `QuoteCalculationResultRepositoryAdapter`, `CalculationTraceRepositoryAdapter`
  - mapper: `CalculationPersistenceMapper`

### 5.4 Versionado optimista

Implementado con `@Version` en:

- `quotes.lock_version`
- `quote_calculation_results.lock_version`

La capa API maneja conflicto optimista como HTTP `409 CONFLICT` (`OptimisticLockingFailureException`).

## 6. Diseno de APIs

### 6.1 Endpoints implementados

- `POST /v1/folios`
- `GET /v1/quotes/{folio}/general-info`
- `PUT /v1/quotes/{folio}/general-info`
- `GET /v1/quotes/{folio}/locations/layout`
- `PUT /v1/quotes/{folio}/locations/layout`
- `GET /v1/quotes/{folio}/locations`
- `PUT /v1/quotes/{folio}/locations`
- `PATCH /v1/quotes/{folio}/locations/{indice}`
- `GET /v1/quotes/{folio}/locations/summary`
- `GET /v1/quotes/{folio}/locations/results` (adicional)
- `GET /v1/quotes/{folio}/coverage-options`
- `PUT /v1/quotes/{folio}/coverage-options`
- `POST /v1/quotes/{folio}/calculate`
- `GET /v1/quotes/{folio}/state`

### 6.2 Estructura de respuesta

- Exito: `{"data": ...}`
- Error: `ApiErrorResponse` con:
  - `timestamp`
  - `status`
  - `error`
  - `message`
  - `path`
  - `validationErrors[]`

### 6.3 Validaciones API

Se aplican con Bean Validation en DTOs (`@NotBlank`, `@NotEmpty`, `@Size`, `@Min`, `@PositiveOrZero`) y se normalizan por `ApiExceptionHandler`.

## 7. Explicacion del calculo implementado

### 7.1 Flujo de calculo (`CalculateQuoteUseCase`)

1. Carga quote por folio.
2. Carga ubicaciones del folio.
3. Carga coberturas seleccionadas del folio.
4. Ejecuta validaciones previas:
   - sin ubicaciones configuradas -> alerta global.
   - sin coberturas configuradas -> alerta global.
5. Recorre ubicaciones:
   - `COMPLETE`:
     - con coberturas -> calcula prima por ubicacion + guarda trazas.
     - sin coberturas -> prima `0.0` + alerta de falta de coberturas tarifables.
   - `INCOMPLETE`: prima `0.0` + alerta de omision.
   - `INVALID`: excluida + alerta de exclusion.
6. Consolida resultado financiero:
   - `primaNeta = sum(primasPorUbicacion)`
   - `gastos = primaNeta * 0.10`
   - `impuestos = (primaNeta + gastos) * 0.16`
   - `primaComercial = primaNeta + gastos + impuestos`
7. Persiste resultado financiero.
8. Persiste trazabilidad de factores aplicados.
9. Cambia estado de quote a `CALCULATED`.

### 7.2 Formula de prima por ubicacion (stub actual)

Implementada en `StubPremiumCalculator`:

- `rate = BASE_RATE + (COVERAGE_RATE * coberturasSeleccionadas)`
- `BASE_RATE = 0.0015`
- `COVERAGE_RATE = 0.0002`
- `primaUbicacion = valorAsegurado * rate`

Trazabilidad guardada:

- factor `BASE_RATE`
- factor(es) `COVERAGE_FACTOR` por cobertura seleccionada
- metadata asociada (ej. `coverageCode`, `insuredLimit`)

## 8. Estrategia de pruebas

### 8.1 Alcance de pruebas

- Unit tests por dominio y casos de uso.
- Pruebas de validacion de API (Web MVC).
- Pruebas automatizadas de flujo critico end-to-end en capa de aplicacion con repos in-memory.

### 8.2 Suite relevante implementada

- Calculo:
  - `CalculateQuoteUseCaseTest`
  - `CalculateQuoteTraceabilityTest`
  - `GetQuoteLocationResultsUseCaseTest`
- Quote:
  - `CreateQuoteUseCaseTest`
  - `UpdateQuoteGeneralDataUseCaseTest`
  - `GetQuoteFinalStatusUseCaseTest`
  - `CreateQuoteVersionUseCaseTest`
- Locations:
  - `CreateQuoteLocationUseCaseTest`
  - `ListQuoteLocationsUseCaseTest`
  - `UpdateQuoteLocationUseCaseTest`
  - `LocationControllerValidationTest`
- Flujos:
  - `CriticalBackendFlowsTest`:
    - creacion de folio
    - flujo de ubicaciones
    - ejecucion de calculo

### 8.3 Cobertura

JaCoCo integrado en `build.gradle` con verificacion minima `80%` para paquetes `application` y `domain`.

## 9. Supuestos y limitaciones

### 9.1 Supuestos

- Las tarifas tecnicas se simulan con `StubPremiumCalculator`.
- El folio se genera localmente en memoria durante la ejecucion.
- El catalogo de coberturas se inicializa por seed SQL local.

### 9.2 Limitaciones actuales

- No hay autenticacion/autorizacion.
- No hay integraciones reales con servicio core externo.
- `catalog` y `document` no tienen logica funcional implementada.
- La version de negocio (`business_version`) se incrementa al crear una nueva version de quote, no en cada edicion parcial.
- El calculo actuarial esta simplificado por diseno de MVP.

## 10. Decisiones tecnicas

1. Monolito modular en vez de microservicios para reducir complejidad operativa del MVP.
2. Dominio desacoplado de JPA; persistencia por adapters + mappers.
3. Uso de `DomainReflectionMapper` para reconstruir objetos de dominio inmutables con constructores no publicos.
4. Flyway como fuente unica del esquema y seeds reproducibles.
5. Manejo centralizado de errores con `ApiExceptionHandler`.
6. Perfil `local` para desarrollo sin dependencia obligatoria de base de datos.
7. Trazabilidad de calculo persistida en tablas dedicadas (`calculation_traces`, `calculation_trace_metadata`).

## 11. Matriz de trazabilidad (requisito -> implementacion)

| Requisito del reto | Implementacion backend | Evidencia de prueba | Estado |
|---|---|---|---|
| Crear folio | `POST /v1/folios`, `CreateQuoteUseCase` | `CreateQuoteUseCaseTest`, `CriticalBackendFlowsTest` | Cumplido |
| Consultar/guardar datos generales | `GET/PUT /general-info`, `GetQuoteByFolioUseCase`, `UpdateQuoteGeneralDataUseCase` | `GetQuoteByFolioUseCaseTest`, `UpdateQuoteGeneralDataUseCaseTest` | Cumplido |
| Consultar/guardar layout de ubicaciones | `GET/PUT /locations/layout`, `SaveQuoteLocationLayoutUseCase` | `SaveQuoteLocationLayoutUseCaseTest` | Cumplido |
| Registrar/consultar/editar ubicaciones | `PUT/GET/PATCH /locations`, `Replace/List/UpdateQuoteLocationUseCase` | `CreateQuoteLocationUseCaseTest`, `ListQuoteLocationsUseCaseTest`, `UpdateQuoteLocationUseCaseTest`, `CriticalBackendFlowsTest` | Cumplido |
| Resumen de ubicaciones | `GET /locations/summary`, `GetQuoteLocationSummaryUseCase` | Cobertura indirecta en flujos y calculo | Cumplido |
| Consultar/guardar coberturas | `GET/PUT /coverage-options`, `GetQuoteCoverageOptionsUseCase`, `ConfigureQuoteCoveragesUseCase` | `ConfigureQuoteCoveragesUseCaseTest`, `ListActiveCoveragesUseCaseTest` | Cumplido |
| Ejecutar calculo por ubicacion | `POST /calculate`, `CalculateQuoteUseCase` | `CalculateQuoteUseCaseTest`, `CalculateQuoteTraceabilityTest`, `CriticalBackendFlowsTest` | Cumplido |
| Ubicacion incompleta genera alerta y no bloquea | Reglas en `CalculateQuoteUseCase` + `QuoteLocation` | `CalculateQuoteUseCaseTest`, `CriticalBackendFlowsTest` | Cumplido |
| Persistir resultado financiero sin sobrescribir otras secciones | `QuoteCalculationResultRepositoryAdapter` + tabla `quote_calculation_results` y relacionadas | `CalculateQuoteTraceabilityTest`, `CriticalBackendFlowsTest` | Cumplido |
| Cambio de estado final de cotizacion | `quote.markAsCalculated()` en `CalculateQuoteUseCase` | `CalculateQuoteUseCaseTest`, `CriticalBackendFlowsTest` | Cumplido |
| Consultar estado final del folio | `GET /state`, `GetQuoteFinalStatusUseCase` | `GetQuoteFinalStatusUseCaseTest` | Cumplido |
| Versionado optimista | `@Version` en `QuoteJpaEntity` y `QuoteCalculationResultJpaEntity`; manejo `409` | Validacion por diseno de capa API | Cumplido |
| Validaciones de request y errores consistentes | Bean Validation + `ApiExceptionHandler` | `LocationControllerValidationTest` | Cumplido |

## 12. Anexos operativos

- Aplicacion principal: `backend/src/main/java/com/cotizador/danos/DanosBackendApplication.java`
- Configuracion y perfiles: `backend/src/main/resources/application.yml`
- Migraciones: `backend/src/main/resources/db/migration`
- Script demo backend: `backend/scripts/demo-flow.sh`
- Coleccion Postman: `backend/postman/cotizador-danos-backend.postman_collection.json`
