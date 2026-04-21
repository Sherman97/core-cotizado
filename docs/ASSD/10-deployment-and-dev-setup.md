# 10. Deployment and Dev Setup

## Runtime objetivo

- Spring Boot backend
- MariaDB 11
- Flyway habilitado en perfil `mariadb`

## Ejecucion local recomendada

```bash
docker compose up -d mariadb
./backend/scripts/run-mariadb.sh
```

## Variables de entorno relevantes

- `SPRING_PROFILES_ACTIVE` (default `mariadb`)
- `SERVER_PORT` (default `8080`)
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Perfiles

- `mariadb`: runtime local real con DB
- `local`: arranque sin datasource
- `test`: pruebas con H2 en memoria

## Migraciones runtime

Flyway aplica:

- `V1` a `V12` (esquema operativo + idempotencia + tablas maestras de rating + seeds)

## Setup de pruebas

```bash
cd backend
./gradlew test
./gradlew jacocoTestCoverageVerification
```

Entorno de pruebas:

- Spring Boot Test + MockMvc
- H2 en memoria
- perfil `test`

## Nota MariaDB vs H2

- Runtime del backend: MariaDB
- Pruebas automatizadas: H2

Este desacople esta implementado para mantener ejecucion de pruebas rapida y aislada.
