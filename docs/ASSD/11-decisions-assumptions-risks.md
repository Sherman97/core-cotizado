# 11. Decisions, Assumptions and Risks

## Decisiones tecnicas implementadas

1. monolito modular Spring Boot con separacion por capas
2. idempotencia persistida para `POST /v1/folios` via `Idempotency-Key`
3. versionado de negocio incremental en ediciones funcionales
4. calculo MVP simplificado con trazabilidad persistida
5. integracion read-only de tablas maestras en modulo `catalog` antes de reemplazar motor de calculo

## Supuestos explicitos

- `giro.claveIncendio` se representa hoy con `occupancyType`.
- `garantias tarifables` se representan hoy con coberturas `selected=true`.
- la formula de prima es MVP (no actuarial).
- el backend se mantiene en alcance exclusivo de seguros de danos.

## Riesgos tecnicos

- deuda de transicion: servicios de catalogo integrados pero no conectados aun al flujo de calculo.
- posible ajuste de reglas al introducir campos de dominio dedicados para giro y garantias.
- diferencias de comportamiento potenciales entre H2 y MariaDB en escenarios edge.

## Riesgos funcionales

- interpretacion de la prima MVP como prima final productiva.
- dependencia temporal de mapeos conservadores para reglas de elegibilidad.

## Mitigaciones implementadas

- mapeos conservadores documentados en ASSD y README
- trazabilidad de factores persistida
- pruebas unitarias + casos de uso + E2E REST
- verificacion de cobertura automatizada con JaCoCo
