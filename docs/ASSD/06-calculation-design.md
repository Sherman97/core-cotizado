# 06. Calculation Design

## Objetivo del modulo

Calcular prima por ubicacion elegible, consolidar resultado financiero y persistir trazabilidad de factores aplicados.

## Flujo implementado

1. cargar quote por folio
2. cargar ubicaciones y coberturas
3. evaluar elegibilidad por ubicacion
4. calcular primas de ubicaciones elegibles
5. consolidar prima neta/comercial
6. persistir resultado y trazabilidad
7. actualizar quote a estado `CALCULATED`

## Reglas de elegibilidad implementadas

Una ubicacion no es calculable si:

- codigo postal invalido
- falta `giro.claveIncendio`
- faltan garantias tarifables

Mapeo conservador actual:

- `giro.claveIncendio` -> `occupancyType`
- garantias tarifables -> coberturas seleccionadas (`selected=true`)

Comportamiento:

- la ubicacion no calculable se excluye
- se agrega alerta de exclusion con razon
- el calculo continua para ubicaciones validas

## Formula MVP (no actuarial)

Calculo por ubicacion en `StubPremiumCalculator`:

- `rate = BASE_RATE + COVERAGE_RATE * cantidadCoberturasSeleccionadas`
- `BASE_RATE = 0.0015`
- `COVERAGE_RATE = 0.0002`
- `primaUbicacion = valorAsegurado * rate`

Consolidacion:

- `gastos = primaNeta * 0.10`
- `impuestos = (primaNeta + gastos) * 0.16`
- `primaComercial = primaNeta + gastos + impuestos`

## Persistencia de salida

Se persisten:

- resultado financiero consolidado
- primas por ubicacion
- alertas globales y por ubicacion
- trazas de calculo (`factor_type`, `applied_value`, `factor_order`, `metadata`)

## Trazabilidad funcional

Por cada ubicacion calculada se guardan factores en `calculation_traces` y metadata en `calculation_trace_metadata`, permitiendo explicar el resultado final.
