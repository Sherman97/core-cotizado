# 04. Architecture

## Estilo arquitectonico

Monolito modular Spring Boot, orientado a clean architecture por capas:

- `api`
- `application`
- `domain`
- `infrastructure`
- `mapper`

## Modulos backend reales

- `quote`
- `location`
- `coverage`
- `calculation`
- `shared`

Modulos presentes como placeholders estructurales:

- `catalog`
- `document`

## Componentes tecnicos principales

- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- MariaDB (runtime)
- Flyway (migraciones runtime)
- H2 (solo perfil de test)

## Estructura de integracion

- API REST bajo `/v1`
- casos de uso en capa `application`
- repositorios de dominio implementados por adapters JPA
- mapeo dominio <-> entidad via mappers dedicados

## Elementos de arquitectura recientes

### Idempotencia de folios

- contrato por header `Idempotency-Key`
- persistencia de clave->folio en tabla dedicada
- replay con `200`, creacion con `201`

### Versionado de negocio

- incremento de version y actualizacion de `modifiedAt` en ediciones parciales relevantes

### Calculo trazable

- resultado financiero consolidado persistido
- trazabilidad de factores persistida por ubicacion

## Principales decisiones tecnicas

1. mantener monolito modular (sin microservicios)
2. no acoplar dominio a JPA
3. calcular con formula MVP simplificada, pero con elegibilidad explicita y trazabilidad
4. usar idempotencia simple y persistida para retries seguros de creacion de folio
