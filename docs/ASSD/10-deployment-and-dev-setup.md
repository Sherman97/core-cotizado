# 10. Deployment and Dev Setup

## Runtime objetivo

- backend Spring Boot
- MariaDB 11
- Flyway habilitado en perfil `mariadb`

## Ejecucion recomendada

```bash
docker compose up -d mariadb
./backend/scripts/run-mariadb.sh
```

## Variables de entorno backend

- `SPRING_PROFILES_ACTIVE` (`mariadb` por defecto)
- `SERVER_PORT` (default `8080`)
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Perfiles

- `mariadb`: runtime real local con MariaDB y Flyway
- `local`: arranque sin datasource
- `test`: pruebas con H2 en memoria

## Migraciones

Flyway aplica automaticamente:

- `V1__init_schema.sql`
- `V2__seed_coverage_catalog.sql`
- `V3__add_folio_idempotency.sql`

## Ejecucion de pruebas

```bash
cd backend
./gradlew test
./gradlew jacocoTestCoverageVerification
```

## Entorno E2E REST

- `@SpringBootTest` + `MockMvc`
- perfil `test`
- H2 en memoria
- secuencia `quote_location_seq` creada en `src/test/resources/schema.sql`

## Notas operativas

- runtime local para evaluacion funcional: MariaDB
- test/integracion automatizada: H2
- no se requiere infraestructura adicional para ejecutar suite de pruebas
