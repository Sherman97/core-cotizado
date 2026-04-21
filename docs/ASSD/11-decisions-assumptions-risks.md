# 11. Decisions, Assumptions and Risks

## Decisiones tecnicas implementadas

1. monolito modular Spring Boot con capas limpias
2. idempotencia simple en creacion de folio via `Idempotency-Key` persistida
3. versionado de negocio incremental en ediciones funcionales
4. formula de calculo MVP simplificada con persistencia de trazabilidad
5. manejo centralizado de errores API

## Supuestos explicitos del modelo actual

- `giro.claveIncendio` se representa con `occupancyType`
- `garantias tarifables` se representan con coberturas `selected=true`
- validacion de codigo postal se resuelve con patron simplificado (`5-6` digitos)

## Limitaciones conocidas

- formula no actuarial
- sin autenticacion/autorizacion
- sin integraciones core reales (agentes, zip externos, tarifas externas)
- modulos `catalog` y `document` aun no completos funcionalmente

## Riesgos tecnicos

- posible ajuste futuro de dominio cuando se incorporen campos reales de giro/garantias
- cambios de contrato si se reemplaza validacion simplificada de codigo postal por servicio externo
- crecimiento de reglas de calculo puede requerir separar estrategia de elegibilidad/tarifacion

## Riesgos funcionales

- interpretacion de prima como valor final de negocio cuando la formula es MVP
- dependencia de mapeos conservadores para reglas tecnicas incompletas del dominio

## Mitigacion actual

- reglas y mapeos conservadores documentados de forma explicita
- pruebas unitarias y E2E cubriendo casos criticos
- trazabilidad persistida para explicar resultados de calculo
