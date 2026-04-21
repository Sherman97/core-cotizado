# 04. Architecture

## Estilo

Monolito modular con Spring Boot y separacion por capas:

- `api`
- `application`
- `domain`
- `infrastructure`
- `mapper`

## Modulos backend

- `quote`
- `location`
- `coverage`
- `calculation`
- `catalog`
- `shared`
- `document` (placeholder)

## Patrons implementados

- puertos en `domain` y adapters en `infrastructure`
- casos de uso en `application`
- controllers REST en `api`
- mappers de persistencia dedicados

## Integraciones tecnicas clave

- Spring Data JPA
- Flyway
- MariaDB (runtime)
- H2 (tests)

## Cambios arquitectonicos relevantes ya implementados

### Idempotencia en creacion de folio

- `POST /v1/folios` con `Idempotency-Key`
- persistencia de clave y folio
- respuesta `201` (create) o `200` (replay)

### Versionado de negocio

- incremento de `businessVersion` y actualizacion de `modifiedAt` en ediciones funcionales

### Trazabilidad de calculo

- persistencia de factores en `calculation_traces` y metadata

### Integracion read-only de tablas maestras

En modulo `catalog`:

- 8 entidades JPA de rating
- repositorios Spring Data
- adapters de dominio para consulta
- servicios read-only para zona, ocupacion, construccion, coberturas y parametros

Nota: esta integracion no cambia aun el flujo de `CalculateQuoteUseCase`.
