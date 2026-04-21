# 01. Overview

## Proposito

Documentar el estado real implementado del backend del cotizador de danos, sin extender alcance fuera del MVP actual.

## Objetivo del backend

Soportar el flujo de cotizacion:

1. crear folio
2. capturar datos generales
3. capturar layout y ubicaciones
4. configurar coberturas
5. ejecutar calculo por ubicacion
6. consultar estado final y resultados

## Capacidades implementadas

- API REST `/v1`
- JPA + MariaDB + Flyway
- idempotencia en `POST /v1/folios` por `Idempotency-Key`
- versionado de negocio en ediciones parciales
- calculo MVP trazable
- persistencia de alertas y trazas de calculo
- pruebas unitarias, de aplicacion y E2E REST
- integracion read-only de tablas maestras de rating en modulo `catalog`

## Delimitacion de alcance

Incluye:

- cotizador de danos MVP
- manejo de folios y estado de quote
- persistencia operacional y tecnica

No incluye:

- frontend
- autenticacion y autorizacion
- formula actuarial real
- integraciones externas productivas

## Supuestos conservadores vigentes

- `giro.claveIncendio` se representa temporalmente con `occupancyType`.
- `garantias tarifables` se representan temporalmente con coberturas `selected=true`.
- el motor de calculo sigue siendo stub MVP, aunque ya existe capa read-only para catalogos/factores persistidos.
