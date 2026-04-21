# 02. Business Flow

## Flujo minimo demostrable implementado

1. `POST /v1/folios`
2. `PUT /v1/quotes/{folio}/general-info`
3. `PUT /v1/quotes/{folio}/locations/layout`
4. `PUT /v1/quotes/{folio}/locations`
5. `PUT /v1/quotes/{folio}/coverage-options`
6. `POST /v1/quotes/{folio}/calculate`
7. `GET /v1/quotes/{folio}/state`

## Reglas de flujo implementadas

### Creacion de folio

- con `Idempotency-Key` nueva -> crea y responde `201`
- con `Idempotency-Key` repetida -> replay y responde `200`
- sin `Idempotency-Key` -> crea normalmente (`201`)

### Edicion funcional

Cada operacion funcional incrementa version de negocio y actualiza fecha de modificacion:

- general-info
- layout de ubicaciones
- operaciones sobre locations
- coverage-options

### Ubicaciones

Estados de validacion del dominio:

- `COMPLETE`
- `INCOMPLETE`
- `INVALID`

Comportamiento:

- `INCOMPLETE`: se mantiene en flujo y genera alerta
- `INVALID`: no se calcula y genera razon de exclusion

### Calculo

Se calcula por ubicacion elegible y luego se consolida:

- prima neta
- gastos
- impuestos
- prima comercial

El backend persiste:

- resultado financiero consolidado
- resultados por ubicacion
- alertas
- trazabilidad de factores

## Elegibilidad de ubicacion en calculo

Una ubicacion queda excluida del calculo si:

- tiene codigo postal invalido
- no tiene `giro.claveIncendio`
- no tiene garantias tarifables

Mapeo conservador aplicado:

- `giro.claveIncendio` -> `occupancyType`
- garantias tarifables -> coberturas seleccionadas (`selected=true`)

## Resultado final

Al finalizar calculo:

- quote cambia a estado `CALCULATED`
- se conserva la trazabilidad de factores aplicados
- se expone estado final con primas y alertas
