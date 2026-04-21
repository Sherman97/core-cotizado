# Cotizador de Seguros de Danos - Backend

Backend Spring Boot para cotizacion de seguros de danos.  
Estado final sincronizado: API REST, persistencia JPA/MariaDB, Flyway, calculo MVP trazable, idempotencia en creacion de folio, versionado de negocio en ediciones parciales, pruebas unitarias y E2E REST.

## 1. Alcance backend implementado

Incluye:

- gestion de folios y cotizaciones
- captura de `general-info`
- configuracion de `locations/layout`
- alta, edicion y resumen de ubicaciones
- configuracion de coberturas
- ejecucion de calculo por ubicacion
- persistencia de resultado financiero y trazabilidad
- consulta de estado final

No incluye:

- frontend
- autenticacion/autorizacion
- integraciones externas reales con servicios core
- formula actuarial productiva

## 2. Requisitos

- Java 21
- Docker + Docker Compose (recomendado para MariaDB)
- bash

## 3. Variables de entorno

| Variable | Default | Uso |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `mariadb` | Perfil de ejecucion |
| `SERVER_PORT` | `8080` | Puerto API |
| `SPRING_DATASOURCE_URL` | `jdbc:mariadb://localhost:3306/cotizador_danos` | Conexion MariaDB |
| `SPRING_DATASOURCE_USERNAME` | `cotizador` | Usuario DB |
| `SPRING_DATASOURCE_PASSWORD` | `cotizador123` | Password DB |

Perfiles:

- `mariadb`: runtime normal, JPA + Flyway habilitados
- `local`: arranque sin datasource
- `test`: pruebas E2E con H2 en memoria

## 4. Ejecucion local

### Opcion recomendada (MariaDB + backend)

```bash
docker compose up -d mariadb
./backend/scripts/run-mariadb.sh
```

### Opcion sin base de datos (perfil local)

```bash
./backend/scripts/run-local.sh
```

## 5. Contrato de idempotencia en creacion de folio

Endpoint:

- `POST /v1/folios`

Header opcional:

- `Idempotency-Key: <valor>`

Comportamiento:

- primera solicitud con una clave nueva: crea cotizacion y responde `201`
- reintento con la misma clave: no crea nueva cotizacion, responde el mismo folio con `200`
- sin `Idempotency-Key`: comportamiento tradicional de creacion (`201`)

Persistencia:

- tabla `folio_idempotency_keys` (Flyway `V3__add_folio_idempotency.sql`)

## 6. Endpoints del backend

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

## 7. Reglas funcionales clave implementadas

### 7.1 Versionado de negocio en ediciones parciales

Se incrementa `businessVersion` y se actualiza `modifiedAt` en:

- `general-info`
- `locations/layout`
- `locations` (replace y patch)
- `coverage-options`

### 7.2 Elegibilidad de calculo por ubicacion

Una ubicacion no se calcula si:

- no tiene codigo postal valido
- no tiene `giro.claveIncendio`
- no tiene garantias tarifables

Resolucion conservadora actual del modelo:

- `giro.claveIncendio` -> campo `occupancyType`
- garantias tarifables -> coberturas seleccionadas (`selected=true`)

Si una ubicacion no es calculable:

- se excluye del calculo
- se agrega alerta de exclusion
- el calculo continua con ubicaciones validas

## 8. Datos de prueba y coleccion API

- seeds: `backend/src/main/resources/db/migration/V2__seed_coverage_catalog.sql`
- fixtures JSON: `backend/fixtures/`
- script demo: `backend/scripts/demo-flow.sh`
- Postman: `backend/postman/cotizador-danos-backend.postman_collection.json`

## 9. Pruebas

Ejecutar suite completa:

```bash
cd backend
./gradlew test
```

Ejecutar cobertura:

```bash
./gradlew jacocoTestReport jacocoTestCoverageVerification
```

Estado actual validado:

- `./gradlew test` -> OK
- `./gradlew jacocoTestCoverageVerification` -> OK

Pruebas E2E REST:

- `QuoteApiE2ETest` (Spring Boot + MockMvc, perfil `test`)
- usa H2 en memoria para entorno de test

## 10. Notas de runtime y test

- runtime productivo local: MariaDB (`mariadb` profile)
- pruebas de integracion/E2E: H2 (`test` profile)
- Flyway aplica esquema runtime en MariaDB

## 11. Supuestos y limitaciones MVP

- calculo simplificado (no actuarial real)
- sin autenticacion/autorizacion
- sin integraciones externas reales de catalogos/tarifas
- modulos `catalog` y `document` sin capacidad funcional completa
- mapeo conservador de datos tecnicos:
  - `occupancyType` como `giro.claveIncendio`
  - `selected=true` como garantia tarifable
