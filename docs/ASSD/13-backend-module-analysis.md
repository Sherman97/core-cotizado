# Analisis del Backend del MVP de Cotizador de Seguros de Danos

## 1. Proposito

Este documento describe el backend real existente en el repositorio al momento de la revision. Su objetivo es complementar la documentacion ASSD con un analisis operativo de:

- modulos implementados
- clases y casos de uso presentes
- responsabilidades funcionales
- reglas logicas efectivamente codificadas
- dependencias entre modulos
- huecos de implementacion y puntos de acoplamiento

No es una especificacion aspiracional. El contenido refleja el estado actual del codigo fuente de `backend/src/main/java/com/cotizador/danos`.

## 2. Vista General del Backend

### 2.1 Organizacion actual

El backend esta estructurado como un monolito modular por dominio bajo el paquete base `com.cotizador.danos`. Los modulos detectados son:

- `quote`
- `location`
- `coverage`
- `calculation`
- `catalog`
- `document`
- `shared`

Cada modulo usa, en distinta madurez, subpaquetes `application`, `domain`, `api`, `infrastructure` y `mapper`. En el estado actual, la logica funcional real vive casi por completo en `application` y `domain`.

### 2.2 Estado de madurez

El backend ya contiene logica de negocio suficiente para cubrir el flujo MVP en nivel de dominio y casos de uso, pero todavia no expone de forma completa una capa HTTP ni una persistencia real integrada.

Estado observado:

- `quote`, `location`, `coverage` y `calculation`: implementados a nivel de dominio y aplicacion
- `catalog`, `document` y `shared`: estructura reservada, sin logica funcional implementada
- repositorios: definidos como contratos de dominio, sin adapters persistentes visibles en este paquete
- uso de Spring Boot: objetivo declarado del proyecto, pero el backend analizado esta actualmente mas cerca de un nucleo Java con TDD que de una aplicacion Spring completamente cableada

### 2.3 Patron predominante

El patron predominante es:

1. un caso de uso en `application`
2. dependencias a contratos del dominio
3. entidades o value objects inmutables o cuasi inmutables en `domain`
4. persistencia abstraida mediante interfaces de repositorio

Esto favorece:

- testeo unitario directo
- bajo acoplamiento con infraestructura
- trazabilidad de reglas por caso de uso

Tambien implica que hoy existe una distancia entre el nucleo de negocio y la aplicacion desplegable completa.

## 3. Mapa de Modulos y Dependencias

### 3.1 Dependencias funcionales entre modulos

El acoplamiento real entre modulos es el siguiente:

- `quote`
  - no depende funcionalmente de `location`, `coverage` ni `calculation` para crear o editar cotizacion
  - depende de `calculation` solo para consultar estado final mediante `QuoteCalculationResultRepository`
- `location`
  - depende de `quote` para validar que el folio exista antes de crear o listar ubicaciones
- `coverage`
  - depende de `quote` para validar que el folio exista antes de configurar coberturas
  - no depende del modulo `catalog`; usa su propio contrato `CoverageCatalogRepository`
- `calculation`
  - depende de `quote` para validar existencia de la cotizacion
  - depende de `location` para recuperar ubicaciones y su estado de validacion
  - depende de `coverage` para recuperar coberturas configuradas
  - depende de sus propios contratos para calculo, trazabilidad y lectura de resultados
- `catalog`
  - no tiene dependencias efectivas porque no tiene implementacion
- `document`
  - no tiene dependencias efectivas porque no tiene implementacion
- `shared`
  - no articula comportamiento aun

### 3.2 Lectura sintetica del flujo tecnico

El flujo backend real del MVP puede resumirse asi:

1. `quote` crea la cotizacion y mantiene folio, version, datos generales y layout.
2. `location` agrega y actualiza ubicaciones ligadas al folio.
3. `coverage` consulta coberturas activas y guarda la configuracion elegida para el folio.
4. `calculation` toma cotizacion, ubicaciones y coberturas; calcula solo ubicaciones completas; excluye invalidas; alerta por incompletas; consolida prima y guarda trazabilidad.
5. `quote` puede consultar estado final e historial de versiones.
6. `calculation` puede consultar resultado por ubicacion.

## 4. Analisis por Modulo

## 4.1 Modulo `quote`

### 4.1.1 Responsabilidad del modulo

`quote` es el modulo principal del backend. Modela la cotizacion como agregado raiz y concentra:

- identidad por `folio`
- versionamiento
- datos generales
- layout de ubicaciones
- estado macro de la cotizacion
- consultas de recuperacion por folio, estado final e historial

### 4.1.2 Clases detectadas

#### Capa `application`

- `CreateQuoteUseCase`
- `CreateQuoteVersionUseCase`
- `GetQuoteByFolioUseCase`
- `GetQuoteFinalStatusUseCase`
- `GetQuoteVersionHistoryUseCase`
- `SaveQuoteLocationLayoutUseCase`
- `UpdateQuoteGeneralDataUseCase`

#### Capa `domain`

- `Quote`
- `QuoteRepository`
- `FolioGenerator`
- `QuoteStatus`
- `QuoteNotFoundException`
- `QuoteGeneralDataPatch`
- `QuoteLocationLayout`
- `QuoteFinalStatusView`
- `QuoteVersionHistoryItem`

### 4.1.3 Logica funcional implementada

#### `CreateQuoteUseCase`

Funcion:

- crea una cotizacion inicial en estado `DRAFT`
- solicita folio a `FolioGenerator`
- verifica unicidad usando `QuoteRepository.existsByFolio`
- persiste la cotizacion

Logica clave:

- el folio es el identificador principal
- la version inicial es `1`
- el estado inicial es `DRAFT`
- la fecha de creacion se toma desde `Clock`

#### `UpdateQuoteGeneralDataUseCase`

Funcion:

- actualiza parcialmente datos generales de una cotizacion

Logica clave:

- carga por folio
- si no existe, lanza `QuoteNotFoundException`
- solo cambia los campos informados en `QuoteGeneralDataPatch`
- actualiza `modifiedAt`

#### `SaveQuoteLocationLayoutUseCase`

Funcion:

- guarda o reemplaza la configuracion base del layout de ubicaciones

Logica clave:

- requiere folio existente
- actualiza `locationLayout` dentro del agregado `Quote`

#### `GetQuoteByFolioUseCase`

Funcion:

- recupera la cotizacion completa por folio

Logica clave:

- si no existe, lanza `QuoteNotFoundException`

#### `GetQuoteFinalStatusUseCase`

Funcion:

- proyecta una vista final de la cotizacion
- une datos del agregado `Quote` con el resultado calculado

Logica clave:

- depende de `QuoteCalculationResultRepository`
- si no existe resultado calculado, usa un resultado vacio por defecto
- devuelve `folio`, `status`, primas y alertas

Implicacion:

- el modulo `quote` asume la lectura del resultado de `calculation`, pero no ejecuta el calculo

#### `CreateQuoteVersionUseCase`

Funcion:

- genera una nueva version editable ligada a una cotizacion previa

Logica clave:

- carga version previa por folio
- genera nuevo folio unico
- usa `Quote.createNewVersion(...)`
- incrementa version
- fija `status = DRAFT`
- conserva referencia al `parentQuoteFolio`

#### `GetQuoteVersionHistoryUseCase`

Funcion:

- lista historial de versiones
- consulta una version especifica

Logica clave:

- exige que exista la cotizacion raiz solicitada
- mapea `Quote` a `QuoteVersionHistoryItem`
- depende de dos consultas especializadas en `QuoteRepository`

### 4.1.4 Entidad principal: `Quote`

Campos observados:

- `folio`
- `parentQuoteFolio`
- `productCode`
- `customerName`
- `currency`
- `observations`
- `locationLayout`
- `status`
- `version`
- `createdAt`
- `modifiedAt`

Responsabilidades efectivas:

- crear una cotizacion inicial
- generar nueva version
- actualizar datos generales
- actualizar layout
- marcarse como calculada

Invariantes efectivas:

- una cotizacion nueva inicia en `DRAFT`
- una nueva version siempre incrementa la version previa
- la nueva version hereda datos generales y layout de la version base

### 4.1.5 Dependencias del modulo `quote`

Dependencias entrantes:

- `location` usa `QuoteRepository` para validar existencia de cotizacion
- `coverage` usa `QuoteRepository` para validar existencia de cotizacion
- `calculation` usa `QuoteRepository` para calcular y consultar resultados

Dependencias salientes:

- `GetQuoteFinalStatusUseCase` depende de `QuoteCalculationResultRepository` del modulo `calculation`

### 4.1.6 Observaciones de diseno

- `quote` esta correctamente posicionado como agregado principal del dominio.
- El versionamiento esta modelado en el agregado, no en un servicio externo.
- Existe `QuoteStatus.CALCULATED`, pero el flujo de calculo actual no siempre lo aplica automaticamente.

## 4.2 Modulo `location`

### 4.2.1 Responsabilidad del modulo

`location` maneja la captura, actualizacion, validacion operativa y consulta de ubicaciones asociadas a una cotizacion.

### 4.2.2 Clases detectadas

#### Capa `application`

- `CreateQuoteLocationUseCase`
- `ListQuoteLocationsUseCase`
- `UpdateQuoteLocationUseCase`

#### Capa `domain`

- `QuoteLocation`
- `QuoteLocationPatch`
- `QuoteLocationUpdatePatch`
- `LocationRepository`
- `LocationValidationStatus`
- `LocationNotFoundException`

### 4.2.3 Logica funcional implementada

#### `CreateQuoteLocationUseCase`

Funcion:

- crea una nueva ubicacion ligada al folio

Logica clave:

- valida que la cotizacion exista usando `QuoteRepository`
- solicita identificador a `LocationRepository.nextId()`
- delega la clasificacion inicial a `QuoteLocation.create(...)`

#### `UpdateQuoteLocationUseCase`

Funcion:

- actualiza parcialmente una ubicacion existente

Logica clave:

- carga por id
- si no existe, lanza `LocationNotFoundException`
- vuelve a evaluar el estado de validacion despues del cambio

#### `ListQuoteLocationsUseCase`

Funcion:

- lista todas las ubicaciones de una cotizacion

Logica clave:

- valida que el folio exista antes de listar

### 4.2.4 Entidad principal: `QuoteLocation`

Campos observados:

- `id`
- `quoteFolio`
- `locationName`
- `city`
- `department`
- `addressLine`
- `postalCode`
- `constructionType`
- `occupancyType`
- `insuredValue`
- `validationStatus`
- `alerts`

Estados observados:

- `COMPLETE`
- `INCOMPLETE`
- `INVALID`

### 4.2.5 Reglas logicas efectivas

#### Creacion inicial

La fabrica `QuoteLocation.create(...)` clasifica la ubicacion como:

- `COMPLETE` si `QuoteLocationPatch.hasRequiredData()` devuelve verdadero
- `INCOMPLETE` en caso contrario

Si queda incompleta:

- no bloquea el flujo
- genera la alerta `"Location has incomplete required data"`

#### Actualizacion

La operacion `update(...)` recompone la instancia y recalcula el estado usando una regla simplificada:

- si `addressLine` y `postalCode` quedan informados, la ubicacion pasa a `COMPLETE`
- si falta alguno, pasa a `INCOMPLETE`

Esto es una simplificacion del MVP. No representa una validacion tecnica o suscriptiva completa.

#### Ubicacion invalida

La fabrica `QuoteLocation.invalid(...)` permite representar explicitamente una ubicacion excluida del calculo.

Caracteristicas:

- estado `INVALID`
- mantiene `locationName`
- conserva alertas como razon de exclusion
- el resto del contenido queda minimizado o nulo

### 4.2.6 Dependencias del modulo `location`

Dependencias salientes:

- `CreateQuoteLocationUseCase` y `ListQuoteLocationsUseCase` dependen de `quote`

Dependencias entrantes:

- `calculation` consume `LocationRepository` y `LocationValidationStatus`

### 4.2.7 Observaciones de diseno

- El modulo `location` encapsula bien la clasificacion operativa de ubicaciones.
- Hay una discrepancia semantica relevante: el frontend usa `VALID`, mientras el backend usa `COMPLETE`.
- La regla actual de completitud es deliberadamente simple y debe leerse como MVP.

## 4.3 Modulo `coverage`

### 4.3.1 Responsabilidad del modulo

`coverage` resuelve dos necesidades del MVP:

- consultar el catalogo activo de coberturas
- guardar la configuracion de coberturas seleccionadas para una cotizacion

### 4.3.2 Clases detectadas

#### Capa `application`

- `ListActiveCoveragesUseCase`
- `ConfigureQuoteCoveragesUseCase`

#### Capa `domain`

- `CoverageCatalogItem`
- `CoverageCatalogRepository`
- `QuoteCoveragePatch`
- `QuoteCoverageSelection`
- `QuoteCoverageRepository`

### 4.3.3 Logica funcional implementada

#### `ListActiveCoveragesUseCase`

Funcion:

- retorna las coberturas activas del catalogo

Logica clave:

- delega completamente en `CoverageCatalogRepository.findActive()`
- no hace filtrado adicional ni enrichment

#### `ConfigureQuoteCoveragesUseCase`

Funcion:

- guarda o reemplaza la configuracion de coberturas asociada a un folio

Logica clave:

- valida que la cotizacion exista
- consulta las selecciones actuales
- reemplaza la configuracion mediante `QuoteCoverageRepository.replaceForQuote(...)`

Importante:

- el comportamiento actual es reemplazo completo, no merge parcial
- `currentSelections` se carga para expresar la intencion del flujo, pero no existe diff funcional

### 4.3.4 Entidad principal: `QuoteCoverageSelection`

Responsabilidades:

- representar una cobertura configurada en una cotizacion
- capturar `coverageCode`, `coverageName`, `insuredLimit`, `deductibleType`, `deductibleValue` y `selected`

Reglas efectivas:

- `create(...)` transforma un patch en una seleccion persistible
- `replaceAll(...)` reconstruye la coleccion entera para el folio

### 4.3.5 Dependencias del modulo `coverage`

Dependencias salientes:

- depende de `quote` para validar existencia del folio

Dependencias entrantes:

- `calculation` usa `QuoteCoverageRepository` y `QuoteCoverageSelection`

### 4.3.6 Observaciones de diseno

- La consulta del catalogo esta en `coverage`, no en `catalog`. Esto es coherente con el estado actual del codigo, aunque conceptualmente podria migrarse mas adelante.
- No existe validacion de consistencia entre cobertura seleccionada y catalogo activo en el flujo de configuracion.
- No existe endpoint visible en este paquete para exponer el modulo; solo la logica de aplicacion.

## 4.4 Modulo `calculation`

### 4.4.1 Responsabilidad del modulo

`calculation` concentra el calculo simplificado del MVP, la consolidacion de primas, la trazabilidad por factores y las consultas de resultado.

### 4.4.2 Clases detectadas

#### Capa `application`

- `CalculateQuoteUseCase`
- `GetQuoteLocationResultsUseCase`

#### Capa `domain`

- `PremiumCalculator`
- `QuoteCalculationResult`
- `LocationCalculationResult`
- `QuoteLocationResultView`
- `CalculationTraceDetail`
- `CalculationTraceRepository`
- `QuoteCalculationResultRepository`

### 4.4.3 Logica funcional implementada

#### `CalculateQuoteUseCase`

Funcion:

- ejecuta el calculo de una cotizacion completa

Flujo efectivo:

1. carga la cotizacion por folio
2. recupera ubicaciones del folio
3. recupera coberturas configuradas
4. itera por ubicacion
5. calcula solo las `COMPLETE`
6. excluye las `INVALID`
7. conserva como resultado no calculado a las `INCOMPLETE`
8. consolida primas y alertas
9. persiste trazabilidad
10. persiste la cotizacion

Reglas logicas efectivas:

- `COMPLETE`:
  - invoca `PremiumCalculator.calculate(...)`
  - genera `LocationCalculationResult`
  - recupera trazabilidad via `PremiumCalculator.traceFor(...)`
- `INVALID`:
  - no se calcula
  - no se agrega al detalle calculado
  - produce una alerta global de exclusion
- `INCOMPLETE`:
  - no se bloquea el flujo
  - entra al resultado con prima `0.0`
  - produce una alerta global de omision

Alertas observadas:

- exclusion: `"Location {name} excluded: {reason}"`
- incompleta: `"Location {name} skipped due to incomplete data"`

Punto importante:

- el caso de uso guarda la cotizacion al final, pero no marca explicitamente `CALCULATED`

#### `GetQuoteLocationResultsUseCase`

Funcion:

- arma la vista final por ubicacion para el frontend o una API de consulta

Logica clave:

- valida que la cotizacion exista
- toma el resultado consolidado si existe; si no, usa uno vacio
- cruza ubicaciones base con resultados calculados por `locationId`
- devuelve una vista por cada ubicacion registrada

Regla actual para `calculated`:

- solo es `true` si la ubicacion tiene estado `COMPLETE` y `premium > 0.0`

Implicacion:

- una prima valida igual a cero seria interpretada como no calculada
- esta regla es util para el MVP, pero debe considerarse una simplificacion

### 4.4.4 Entidades y contratos del modulo

#### `PremiumCalculator`

Rol:

- contrato central para la formula de prima

Metodos:

- `calculate(location, coverages)`
- `traceFor(location, coverages)` con implementacion por defecto vacia

Lectura arquitectonica:

- el modulo separa orquestacion de formula
- permite stub o doble de prueba sin acoplar el caso de uso a una formula concreta

#### `QuoteCalculationResult`

Rol:

- consolidar el resultado total de la cotizacion

Logica observable:

- consolida `netPremium`
- calcula `expenseAmount` con una tasa fija de `10%`
- calcula `taxAmount` con una tasa fija de `16%`
- calcula `totalPremium`
- conserva detalle por ubicacion y alertas

#### `CalculationTraceDetail`

Rol:

- representar la traza del calculo por factor aplicado

Campos observados:

- `quoteFolio`
- `locationId`
- `factorType`
- `appliedValue`
- `factorOrder`
- `metadata`

### 4.4.5 Dependencias del modulo `calculation`

Dependencias salientes:

- `QuoteRepository` desde `quote`
- `LocationRepository` y `LocationValidationStatus` desde `location`
- `QuoteCoverageRepository` y `QuoteCoverageSelection` desde `coverage`

Dependencias entrantes:

- `quote` depende de `QuoteCalculationResultRepository` para estado final

### 4.4.6 Observaciones de diseno

- `calculation` es el modulo con mayor acoplamiento funcional, lo cual es razonable porque materializa el resultado del negocio.
- La formula real no esta en el caso de uso; esta correctamente abstraida en `PremiumCalculator`.
- La trazabilidad ya esta contemplada como contrato, lo que fortalece auditabilidad.
- El modulo todavia necesita adapters reales para persistir resultado y trazabilidad.

## 4.5 Modulo `catalog`

### 4.5.1 Estado actual

`catalog` existe solo como estructura de paquetes:

- `api`
- `application`
- `domain`
- `infrastructure`

No se detectaron clases funcionales.

### 4.5.2 Lectura arquitectonica

Este modulo parece reservado para evolucionar catalogos de:

- coberturas
- factores
- tarifas

Sin embargo, en el estado actual:

- el catalogo de coberturas esta modelado dentro de `coverage`
- los insumos actuariales del modelo de datos no tienen implementacion visible aqui

### 4.5.3 Implicacion

El modulo no debe presentarse como implementado. Debe documentarse como reservado para evolucion futura.

## 4.6 Modulo `document`

### 4.6.1 Estado actual

`document` tambien existe solo como estructura vacia de paquetes.

No se detectaron:

- casos de uso
- entidades
- contratos de repositorio
- exposicion API

### 4.6.2 Implicacion

Aunque el modelo de datos objetivo contempla `quote_documents`, este backend aun no implementa el modulo documental.

## 4.7 Modulo `shared`

### 4.7.1 Estado actual

`shared` contiene carpetas reservadas:

- `config`
- `exception`
- `response`

No se detecto logica reusable concreta dentro de ellas.

### 4.7.2 Implicacion

Todavia no existe una capa transversal consolidada para:

- excepciones HTTP
- respuestas estandar
- configuracion comun

Esto es consistente con el estado actual del backend, que aun privilegia el nucleo de negocio sobre la integracion completa.

## 5. Servicios, Casos de Uso y Lo que Ejecutan

La tabla siguiente resume los servicios de aplicacion existentes.

| Modulo | Caso de uso | Funcion principal | Dependencias |
|---|---|---|---|
| `quote` | `CreateQuoteUseCase` | Crear cotizacion inicial con folio unico | `QuoteRepository`, `FolioGenerator`, `Clock` |
| `quote` | `UpdateQuoteGeneralDataUseCase` | Actualizar datos generales parcialmente | `QuoteRepository`, `Clock` |
| `quote` | `SaveQuoteLocationLayoutUseCase` | Guardar layout de ubicaciones | `QuoteRepository`, `Clock` |
| `quote` | `GetQuoteByFolioUseCase` | Consultar cotizacion por folio | `QuoteRepository` |
| `quote` | `GetQuoteFinalStatusUseCase` | Consultar estado final y resumen de primas | `QuoteRepository`, `QuoteCalculationResultRepository` |
| `quote` | `CreateQuoteVersionUseCase` | Generar nueva version ligada a la anterior | `QuoteRepository`, `FolioGenerator`, `Clock` |
| `quote` | `GetQuoteVersionHistoryUseCase` | Listar historial y consultar version puntual | `QuoteRepository` |
| `location` | `CreateQuoteLocationUseCase` | Crear ubicacion asociada a un folio | `QuoteRepository`, `LocationRepository` |
| `location` | `UpdateQuoteLocationUseCase` | Editar ubicacion y recalcular estado | `LocationRepository` |
| `location` | `ListQuoteLocationsUseCase` | Listar ubicaciones del folio | `QuoteRepository`, `LocationRepository` |
| `coverage` | `ListActiveCoveragesUseCase` | Consultar coberturas activas | `CoverageCatalogRepository` |
| `coverage` | `ConfigureQuoteCoveragesUseCase` | Reemplazar configuracion de coberturas | `QuoteRepository`, `QuoteCoverageRepository` |
| `calculation` | `CalculateQuoteUseCase` | Ejecutar calculo, consolidar y trazar | `QuoteRepository`, `LocationRepository`, `QuoteCoverageRepository`, `PremiumCalculator`, `CalculationTraceRepository` |
| `calculation` | `GetQuoteLocationResultsUseCase` | Consultar resultado por ubicacion | `QuoteRepository`, `QuoteCalculationResultRepository`, `LocationRepository` |

## 6. Reglas Logicas Transversales Detectadas

Las reglas mas importantes implementadas hoy en el backend son:

- el `folio` es la identidad funcional de la cotizacion
- la cotizacion nace en `DRAFT`
- la nueva version hereda datos y layout, pero incrementa version y reinicia estado editable
- una ubicacion incompleta genera alertas y no bloquea el flujo
- una ubicacion invalida no entra al calculo
- el calculo opera solo sobre ubicaciones `COMPLETE`
- la configuracion de coberturas se reemplaza completa por folio
- el resultado final se consolida a partir de primas por ubicacion
- el calculo puede generar trazabilidad por factores

## 7. Acoplamientos Relevantes

### 7.1 Acoplamientos sanos

- `quote` como raiz de consistencia del proceso
- `location` y `coverage` validando existencia del folio en lugar de duplicar estado
- `calculation` desacoplado de la formula via `PremiumCalculator`
- repositorios definidos como contratos, no como dependencias concretas de infraestructura

### 7.2 Acoplamientos a vigilar

- `quote` depende de una vista calculada externa para construir estado final
- `calculation` concentra varias dependencias de otros modulos y por tanto merece especial cuidado si crece
- `coverage` aloja logica de catalogo que podria migrar a `catalog` en una version posterior

## 8. Hallazgos Tecnicos del Repositorio

### 8.1 Madurez Spring Boot

Aunque el proyecto se describe como Spring Boot, el archivo `backend/build.gradle` observado no muestra aun el plugin Spring Boot ni starters visibles en este nivel. El backend analizado debe entenderse como nucleo de negocio y pruebas, no como aplicacion Spring completa ya ensamblada.

### 8.2 Falta de adapters visibles

No se detectaron implementaciones concretas para:

- `QuoteRepository`
- `LocationRepository`
- `QuoteCoverageRepository`
- `CoverageCatalogRepository`
- `QuoteCalculationResultRepository`
- `CalculationTraceRepository`

Tampoco se detecto una capa `api` funcional equivalente a controladores REST para los casos de uso ya modelados.

### 8.3 Artefactos compilados en `src/main/java`

Se detectaron archivos `.class` dentro del arbol fuente de `backend/src/main/java/com/cotizador/danos/quote`. Esto no deberia existir en un repositorio limpio y sugiere contaminacion del codigo fuente con artefactos compilados.

Impacto:

- ruido en revision tecnica
- riesgo de confundir fuente con binarios
- mala practica de control de versiones

### 8.4 Divergencias semanticas con frontend

Se detecta una diferencia entre:

- backend: `LocationValidationStatus.COMPLETE`
- frontend: `validationStatus: 'VALID' | 'INCOMPLETE' | 'INVALID'`

Esto exige un adapter o normalizacion contractual para evitar inconsistencias de integracion.

### 8.5 Estado `CALCULATED` parcialmente integrado

El agregado `Quote` ya soporta `markAsCalculated()` y `QuoteStatus.CALCULATED`, pero `CalculateQuoteUseCase` no refleja de manera observable ese cambio en el flujo principal.

### 8.6 Modulos reservados no implementados

`catalog`, `document` y `shared` deben entenderse como modulos reservados, no como modulos operativos.

## 9. Evaluacion Arquitectonica

### 9.1 Fortalezas

- separacion clara entre aplicacion y dominio
- contratos de persistencia desacoplados
- reglas del MVP explicitadas por caso de uso
- facilidad para pruebas unitarias
- versionamiento incorporado desde el agregado raiz
- calculo trazable como preocupacion de primer nivel

### 9.2 Limitaciones actuales

- falta capa HTTP visible
- falta persistencia real visible
- modulos reservados aun vacios
- parte del lenguaje ubicacion valida vs completa no esta alineado entre backend y frontend
- el backend esta mas cerca de un nucleo hexagonal testeable que de una entrega operativa full-stack cerrada

## 10. Conclusiones

El backend actual esta bien orientado para un MVP guiado por TDD. La mayor parte del valor ya esta implementada como logica de dominio y casos de uso, especialmente en:

- creacion y versionamiento de cotizacion
- manejo de ubicaciones
- configuracion de coberturas
- calculo simplificado con trazabilidad
- consultas de estado final, historial y resultado por ubicacion

La arquitectura real es consistente con un monolito modular y con un enfoque conservador: primero se consolida el nucleo de negocio, luego se completa infraestructura.

Para una defensa tecnica honesta, este backend debe presentarse como:

- funcional en logica central
- bien desacoplado para pruebas
- todavia incompleto en integracion y persistencia final

Esa lectura es consistente con el alcance MVP y con el estado actual del repositorio.
