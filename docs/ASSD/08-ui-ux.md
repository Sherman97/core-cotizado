# 08. UI / UX

## Estructura de vistas del MVP

La aplicación Angular implementa tres vistas principales:

- pantalla de creación de folio
- wizard de cotización
- pantalla de estado final

## Wizard de cotización

La navegación principal ocurre en `/quotes/:folio/wizard`.

Pasos visibles:

- `GENERAL_DATA`
- `LOCATION_LAYOUT`
- `LOCATIONS`
- `COVERAGES`
- `RESULT`

## Propósito de cada pantalla

### Pantalla de creación

Ruta:

- `/`

Propósito:

- iniciar una nueva cotización
- crear un folio
- redirigir al wizard

### Pantalla de wizard

Ruta:

- `/quotes/:folio/wizard`

Propósito:

- capturar información de la cotización
- registrar ubicaciones
- seleccionar coberturas
- ejecutar cálculo
- ver alertas y resumen preliminar

### Pantalla de estado final

Ruta:

- `/quotes/:folio/status`

Propósito:

- mostrar estado actual
- mostrar resumen de prima
- mostrar alertas relevantes

## Componentes principales del frontend

La feature `quote` se construye con:

- `general-data-form`
- `location-layout-form`
- `location-form`
- `locations-grid`
- `coverage-selector`
- `premium-summary`
- `calculation-trace-panel`

Componentes compartidos:

- `stepper`
- `alert-list`
- `money-summary`

## Estados de UI

### Loading

Debe mostrarse al menos:

- al consultar cotización
- al cargar catálogo de coberturas
- al ejecutar cálculo
- al consultar estado final

### Empty

Aplicable cuando:

- no hay ubicaciones registradas
- no hay coberturas seleccionadas
- no hay alertas
- no hay historial de versiones

### Error

Aplicable cuando:

- el `folio` no existe
- falla la llamada al backend
- falla el cálculo

### Success

Aplicable cuando:

- se crea el folio
- se guarda una etapa del wizard
- se completa el cálculo

### Warning

Aplicable cuando:

- hay ubicaciones incompletas
- hay ubicaciones excluidas
- el resultado es parcial

## Alertas por ubicaciones incompletas

La UI debe:

- listarlas de forma visible y no intrusiva
- indicar que no bloquean el flujo
- permitir distinguirlas de exclusiones `INVALID`

Recomendación:

- usar panel de alertas persistente en la parte baja del wizard y en estado final

## Comportamiento del resultado del cálculo

La UI debe mostrar:

- resumen monetario consolidado
- detalle por ubicación
- alertas globales
- trazabilidad simplificada

La pantalla de resultado no debe ocultar ubicaciones no calculadas; debe diferenciarlas claramente.

## Lineamientos de usabilidad

- el `folio` debe ser visible en todo el wizard
- el paso actual debe ser claro
- la acción primaria de cada etapa debe ser obvia
- las alertas deben ser explicativas y no ambiguas
- el resultado monetario debe usar formato consistente
- los mensajes deben diferenciar:
  - incompleto
  - inválido
  - calculado

## Modo claro y oscuro

La UI debe ser usable en ambos modos.

Lineamientos:

- contraste AA como mínimo
- colores de warning diferenciados de error
- tablas y cards con bordes sutiles en dark mode
- no depender solo de color para comunicar estado

## Criterio visual enterprise

Se recomienda una estética sobria, operativa y legible:

- tipografía clara
- cards y paneles con jerarquía visual estable
- stepper visible
- tablas simples para ubicaciones e historial
- métricas monetarias destacadas sin exceso decorativo

## Supuestos

- El frontend actual ya contiene los componentes base del wizard, pero no necesariamente todos los contratos finales del backend.
- La documentación describe la UX objetivo del MVP con base en la estructura existente y en los estados requeridos por el dominio.
