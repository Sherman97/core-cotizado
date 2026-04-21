# Cotizador de Seguros de Danos - Backend

Backend Spring Boot para cotizacion MVP de seguros de danos.

Estado documentado en este archivo: implementacion real actual del backend, incluyendo idempotencia en creacion de folio, versionado de negocio, calculo MVP trazable, pruebas E2E REST y capa read-only para tablas maestras de rating.

## 1. Alcance implementado

Incluye:

- API REST bajo `/v1`
- creacion de folios
- captura de `general-info`
- captura de layout y ubicaciones
- configuracion de coberturas
- calculo MVP por ubicacion
- persistencia de resultado y trazabilidad
- consulta de estado final
- idempotencia en `POST /v1/folios`
- versionado de negocio en ediciones funcionales
- integracion read-only de catalogos/factores de rating (sin reemplazar aun el motor de calculo)

No incluye:

- frontend
- autenticacion/autorizacion
- motor actuarial real
- integraciones externas productivas

## 2. Requisitos

- Java 21
- Docker y Docker Compose (recomendado para MariaDB local)
- Bash

## 3. Perfiles y variables de entorno

| Variable | Default | Uso |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `mariadb` | Perfil de ejecucion |
| `SERVER_PORT` | `8080` | Puerto API |
| `SPRING_DATASOURCE_URL` | `jdbc:mariadb://localhost:3306/cotizador_danos` | Conexion MariaDB |
| `SPRING_DATASOURCE_USERNAME` | `cotizador` | Usuario DB |
| `SPRING_DATASOURCE_PASSWORD` | `cotizador123` | Password DB |

Perfiles:

- `mariadb`: runtime local normal, JPA + Flyway habilitados
- `local`: arranque sin datasource
- `test`: pruebas con H2 en memoria

## 4. Ejecucion local

### Runtime con MariaDB (recomendado)

```bash
docker compose up -d mariadb
./backend/scripts/run-mariadb.sh
```

### Arranque sin base de datos

```bash
./backend/scripts/run-local.sh
```

## 5. Contrato idempotente de creacion de folio

Endpoint:

- `POST /v1/folios`

Header:

- `Idempotency-Key` (opcional)

Comportamiento:

- clave nueva: crea folio y responde `201`
- misma clave ya registrada: replay del mismo folio y responde `200`
- sin header: creacion normal y responde `201`

Persistencia:

- tabla `folio_idempotency_keys` (migracion `V3__add_folio_idempotency.sql`)

## 6. Endpoints soportados

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

## 7. Flujos funcionales criticos

### 7.1 Versionado de negocio

Se incrementa `businessVersion` y se actualiza `modifiedAt` en:

- `general-info`
- `locations/layout`
- `locations` (replace y patch)
- `coverage-options`

### 7.2 Regla de calculabilidad por ubicacion

Una ubicacion no se calcula si:

- no tiene codigo postal valido
- no tiene `giro.claveIncendio`
- no tiene garantias tarifables

Mapeo conservador vigente:

- `giro.claveIncendio` se representa con `occupancyType`
- garantias tarifables se representan con coberturas `selected=true`

Comportamiento:

- la ubicacion no calculable se excluye
- se agrega alerta de exclusion
- las ubicaciones validas restantes si se calculan

## 8. Estado de calculo y rating

- El motor de calculo activo sigue siendo MVP (`StubPremiumCalculator`).
- La formula sigue siendo simplificada (no actuarial real).
- Ya existe integracion read-only de tablas maestras de rating en modulo `catalog`:
  - entities JPA
  - Spring Data repositories
  - adapters de dominio
  - servicios de consulta
- Esa capa aun no reemplaza el flujo de calculo productivo.

## 9. Tablas maestras integradas (read-only)

Soportadas en codigo:

- `postal_code_zone_map`
- `zone_factors`
- `occupancy_catalog`
- `occupancy_factors`
- `construction_factors`
- `coverage_rate_tables`
- `coverage_factor_tables`
- `calculation_parameters`

Codigos canonicos tecnicos:

- `productCode`: `DANOS`
- `occupancyType`: `OFFICE`, `COMMERCE`, `RESTAURANT`, `WAREHOUSE`, `LIGHT_INDUSTRY`
- `constructionType`: `CONCRETE`, `MIXED`, `WOOD`
- `coverageCode`: `FIRE`, `EARTHQUAKE`, `FLOOD`

## 10. Pruebas y cobertura

Suite completa:

```bash
cd backend
./gradlew test
```

Cobertura:

```bash
./gradlew jacocoTestReport jacocoTestCoverageVerification
```

Estado validado:

- `./gradlew test` en verde
- `./gradlew jacocoTestCoverageVerification` en verde

Pruebas E2E REST:

- `QuoteApiE2ETest`
- stack: `@SpringBootTest` + `MockMvc`
- perfil `test` con H2 en memoria

## 11. Notas de base de datos

- Runtime local: MariaDB (`mariadb` profile), Flyway habilitado.
- Test: H2 en memoria (`test` profile), Flyway deshabilitado en pruebas y esquema generado para tests.

## 12. Limitaciones y supuestos del MVP

- formula de prima simplificada, no actuarial
- sin authn/authz
- sin integraciones externas productivas
- mapeos conservadores explicitos:
  - `occupancyType` como representacion temporal de `giro.claveIncendio`
  - `selected=true` como representacion temporal de garantia tarifable
- capa de rating en BD integrada solo para consulta read-only en esta etapa
