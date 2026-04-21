# 01. Overview

## Proposito del documento

Este documento resume el estado final implementado del backend del cotizador de danos y delimita su alcance real para evaluacion tecnica.

## Objetivo del backend

Soportar un flujo MVP de cotizacion con:

1. creacion de folio
2. captura de datos generales
3. configuracion de layout de ubicaciones
4. registro y edicion de ubicaciones
5. configuracion de coberturas
6. calculo por ubicacion
7. consulta de estado final y resultados

## Capacidades implementadas

- API REST bajo `/v1`
- persistencia JPA en MariaDB
- migraciones Flyway
- idempotencia en `POST /v1/folios` con `Idempotency-Key`
- versionado de negocio en ediciones parciales
- calculo MVP simplificado con trazabilidad
- pruebas unitarias y E2E REST

## Alcance funcional actual

Incluye:

- folio unico por creacion y replay idempotente por clave
- estado inicial `DRAFT` y cambio a `CALCULATED` al calcular
- version de negocio incrementada en operaciones funcionales de edicion
- exclusion de ubicaciones no calculables con alerta explicita
- persistencia de resultados financieros y de trazabilidad de calculo

No incluye:

- frontend
- autenticacion/autorizacion
- formulas actuariales reales
- integraciones reales con servicios core externos

## Glosario operativo

- `numeroFolio`: identificador de cotizacion.
- `businessVersion`: version de negocio expuesta en respuestas.
- `modifiedAt`: fecha de ultima actualizacion de quote.
- `Idempotency-Key`: clave para evitar duplicidad en reintentos de creacion de folio.
- ubicacion calculable: ubicacion que cumple reglas minimas para entrar al calculo.
- trazabilidad: detalle de factores aplicados por ubicacion en el calculo.

## Supuestos declarados del MVP

- `giro.claveIncendio` se representa con el campo de dominio actual `occupancyType`.
- `garantias tarifables` se representan con coberturas seleccionadas (`selected=true`).
- la formula de prima es simplificada y no actuarial.
