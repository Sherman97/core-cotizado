# 06. Calculation Design

## Estado actual del calculo implementado

El backend usa un motor MVP simplificado (`StubPremiumCalculator`) con trazabilidad persistida.

No se implemento aun reemplazo del motor por lectura de tablas maestras en el flujo de `CalculateQuoteUseCase`.

## Flujo de calculo vigente

1. cargar quote por folio
2. cargar ubicaciones y coberturas
3. evaluar calculabilidad por ubicacion
4. calcular prima de ubicaciones elegibles
5. consolidar resultado financiero
6. persistir resultado y trazabilidad
7. marcar quote como `CALCULATED`

## Regla de calculabilidad completada

Una ubicacion no se calcula si:

- no tiene codigo postal valido
- no tiene `giro.claveIncendio`
- no tiene garantias tarifables

Mapeos conservadores vigentes:

- `giro.claveIncendio` -> `occupancyType`
- `garantias tarifables` -> coberturas `selected=true`

Comportamiento:

- se excluye la ubicacion no calculable
- se registra alerta con razon de exclusion
- el calculo continua con ubicaciones validas

## Formula MVP (no actuarial)

Calculo por ubicacion:

- `rate = BASE_RATE + COVERAGE_RATE * cantidadCoberturasSeleccionadas`
- `BASE_RATE = 0.0015`
- `COVERAGE_RATE = 0.0002`
- `primaUbicacion = valorAsegurado * rate`

Consolidacion:

- `gastos = primaNeta * 0.10`
- `impuestos = (primaNeta + gastos) * 0.16`
- `primaComercial = primaNeta + gastos + impuestos`

## Trazabilidad del calculo

Persistencia en:

- `calculation_traces`
- `calculation_trace_metadata`

Incluye:

- tipo de factor
- valor aplicado
- orden
- metadata explicativa por ubicacion

## Integracion de datos maestros ya disponible (fase read-only)

Se implementaron servicios de consulta en modulo `catalog` para:

- zona/factor por codigo postal
- ocupacion/factor
- construccion/factor
- tasas/factores por cobertura
- parametros globales

Estos servicios estan listos para ser usados por una siguiente fase de reemplazo del calculo hardcodeado.
