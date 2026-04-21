# Paquete para evolución del motor de cálculo basado en datos maestros

Este paquete contiene:

- un agente para que Codex implemente la evolución del cálculo del backend
- seeders separados por dominio técnico
- scripts SQL de creación de tablas maestras y carga inicial de datos

## Objetivo

Eliminar tasas y factores hardcodeados del cálculo MVP y reemplazarlos por un motor basado en:

- catálogos de zona
- factores por zona
- catálogo de ocupaciones con clave incendio
- factores por ocupación
- factores por tipo constructivo
- tasas por cobertura
- factores por cobertura
- parámetros globales del cálculo

## Estructura

- `agents/backend-risk-engine-agent.md`
- `seeders/V4__create_rating_master_tables.sql`
- `seeders/V5__seed_postal_code_zones.sql`
- `seeders/V6__seed_zone_factors.sql`
- `seeders/V7__seed_occupancy_catalog.sql`
- `seeders/V8__seed_occupancy_factors.sql`
- `seeders/V9__seed_construction_factors.sql`
- `seeders/V10__seed_coverage_rate_tables.sql`
- `seeders/V11__seed_coverage_factor_tables.sql`
- `seeders/V12__seed_calculation_parameters.sql`

## Recomendación de uso

1. Ejecutar primero el agente para que Codex diagnostique el impacto.
2. Incorporar la migración de creación de tablas maestras.
3. Incorporar los seeders en el orden propuesto.
4. Reemplazar el `StubPremiumCalculator` por un `CatalogBackedPremiumCalculator`.
5. Agregar pruebas unitarias e integración para el cálculo con datos maestros.

## Nota importante

Los datos semilla están diseñados para un MVP de **seguros de daños**, no para vida ni autos.
