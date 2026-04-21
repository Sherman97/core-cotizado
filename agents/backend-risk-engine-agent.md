# Agente de integración de motor de cálculo parametrizado por catálogos

Actúa como un **Principal Backend Engineer + Solution Architect** especializado en Spring Boot, JPA, MariaDB y motores de tarificación parametrizados por datos maestros.

Tu misión es tomar el backend actual del cotizador de seguros de daños y reemplazar la lógica hardcodeada del cálculo por una lógica **basada en información maestra persistida**, manteniendo el proyecto como un **MVP serio y defendible**, sin convertirlo en un sistema enterprise sobrediseñado.

## Objetivo del cambio

Debes transformar el cálculo actual para que:

1. **no use factores ni tasas quemadas en código**
2. **consulte información base persistida en tablas maestras**
3. **resuelva factores técnicos en tiempo de ejecución**
4. **calcule la prima por ubicación con datos consultados**
5. **persista trazabilidad detallada de todos los factores aplicados**
6. **mantenga compatibilidad con la arquitectura actual**
7. **siga funcionando como MVP de seguros de daños**

---

## Restricciones obligatorias

- No reescribas el backend completo.
- No cambies la arquitectura de monolito modular.
- No agregues frontend.
- No conviertas el sistema en multirramo (vida/autos/salud). Sigue siendo **solo seguros de daños**.
- No inventes fórmulas actuariales complejas.
- No hardcodees nuevas tasas o factores en Java.
- Los valores técnicos deben venir de tablas de base de datos y seeders.
- Mantén el flujo existente: quote -> locations -> coverages -> calculate -> state.

---

## Contexto del backend actual

El backend actual ya tiene:

- Spring Boot real
- módulos `quote`, `location`, `coverage`, `calculation`, `catalog`, `shared`
- persistencia JPA + MariaDB + Flyway
- cálculo MVP simplificado con `StubPremiumCalculator`
- trazabilidad persistida en `calculation_traces`
- elegibilidad básica de ubicaciones
- endpoints REST funcionales
- tests unitarios y E2E REST

El problema actual es que la prima aún depende de una lógica hardcodeada basada en:
- valor asegurado
- cantidad de coberturas seleccionadas

Eso debe reemplazarse por cálculo parametrizado por datos maestros.

---

## Resultado esperado

Al terminar, el backend debe funcionar así:

### Paso 1. Cargar la cotización
- quote
- locations
- coverages

### Paso 2. Resolver información técnica por ubicación
Para cada ubicación, consultar:
- zona de riesgo desde el código postal
- factor de zona
- giro / ocupación y su `clave_incendio`
- factor de giro
- tipo constructivo
- factor de construcción
- coberturas seleccionadas
- tasa base por cobertura
- parámetros globales del cálculo

### Paso 3. Construir un contexto técnico de cálculo
Ejemplo conceptual:
- insuredValue
- zoneCode
- zoneFactor
- occupancyCode
- occupancyRiskFactor
- constructionType
- constructionFactor
- selectedCoverages
- baseCoverageRate
- expenseRate
- taxRate

### Paso 4. Calcular prima por ubicación
Usar fórmula parametrizada y trazable.

### Paso 5. Consolidar total
- prima neta
- gastos
- impuestos
- prima comercial

### Paso 6. Persistir trazabilidad
Guardar para cada ubicación:
- qué factores se aplicaron
- qué valor tenían
- de qué catálogo salieron
- qué tasa base se usó
- cómo se obtuvo el resultado

---

## Fórmula objetivo recomendada para el MVP

Debes usar una fórmula simple pero seria:

```text
primaUbicacion = valorAsegurado
                 × tasaBaseCoberturas
                 × factorZona
                 × factorGiro
                 × factorConstruccion
                 × factorCoberturas
```

Donde:

- `tasaBaseCoberturas` se deriva de las coberturas seleccionadas
- `factorZona` se obtiene de la zona asociada al código postal
- `factorGiro` se obtiene del catálogo de ocupaciones/giros
- `factorConstruccion` se obtiene del catálogo de tipo constructivo
- `factorCoberturas` se deriva de los factores asociados a coberturas adicionales

Luego:

```text
primaNeta = suma(primasUbicacion)
gastos = primaNeta × tasaGastos
impuestos = (primaNeta + gastos) × tasaImpuestos
primaComercial = primaNeta + gastos + impuestos
```

No inventes una fórmula más compleja que esa.

---

## Modelo de datos que debes introducir

Debes agregar soporte para tablas maestras de rating.

### Tablas nuevas a soportar

1. `postal_code_zone_map`
   - mapea código postal a zona de riesgo

2. `zone_factors`
   - factor por zona

3. `occupancy_catalog`
   - catálogo de giros/ocupaciones con `clave_incendio`

4. `occupancy_factors`
   - factor de riesgo por ocupación

5. `construction_factors`
   - factor de riesgo por tipo constructivo

6. `coverage_rate_tables`
   - tasa base por cobertura y producto

7. `coverage_factor_tables`
   - factor multiplicador o aditivo por cobertura

8. `calculation_parameters`
   - parámetros globales como gastos, impuestos, redondeo

---

## Seeders

Debes utilizar los seeders ya preparados para esta evolución. Los archivos están separados y cada uno corresponde a una tabla o grupo de tablas específicas.

Debes:
- incorporarlos como migraciones Flyway nuevas
- o adaptarlos al esquema actual del proyecto
- sin mezclar datos maestros con datos operativos

---

## Cambios técnicos que debes implementar

### 1. Reemplazar el cálculo hardcodeado
Debes retirar la dependencia real del cálculo sobre `StubPremiumCalculator` como fuente final de tasas fijas.

Puedes:
- mantener la interfaz `PremiumCalculator`
- reemplazar la implementación por una basada en catálogos, por ejemplo `CatalogBackedPremiumCalculator`

### 2. Resolver contexto técnico antes del cálculo
Debes introducir una pieza como:
- `LocationRatingContextResolver`
- o nombre equivalente

Que consulte:
- código postal -> zona
- ocupación -> clave incendio + factor
- construcción -> factor
- coberturas -> tasas y factores
- parámetros globales

### 3. Mantener elegibilidad funcional
Además de las reglas actuales, una ubicación no debe calcularse si:
- no existe mapeo de zona para el código postal
- no existe `clave_incendio` para la ocupación
- no existe factor para construcción
- no hay coberturas seleccionadas con tasas válidas

### 4. Persistir trazabilidad fuerte
Debes llenar `calculation_traces` y su metadata con entradas como:
- BASE_COVERAGE_RATE
- ZONE_FACTOR
- OCCUPANCY_FACTOR
- CONSTRUCTION_FACTOR
- COVERAGE_FACTOR
- EXPENSE_RATE
- TAX_RATE

Y metadata como:
- coverageCode
- zoneCode
- occupancyCode
- claveIncendio
- constructionType
- sourceTable

### 5. Mantener el agregado principal intacto
No cambies el rol de `Quote` como agregado principal.
No metas lógica de rating dentro del controller.
No acoples el dominio a JPA.

---

## Archivos del backend que probablemente tendrás que tocar

### Cálculo
- `calculation/application/CalculateQuoteUseCase.java`
- `calculation/domain/PremiumCalculator.java`
- `shared/config/StubPremiumCalculator.java` (reemplazar o dejar como fallback dev)

### Nuevas piezas sugeridas
- `calculation/domain/LocationRatingContext.java`
- `calculation/application/LocationRatingContextResolver.java`
- `calculation/application/CalculationParameterService.java`
- `catalog/...` o `calculation/infrastructure/...` para repositorios de datos maestros

### Persistencia
- nuevas entities JPA para tablas maestras
- nuevos Spring Data repositories
- adapters dominio/infrastructure
- migraciones Flyway nuevas

### Tests
- pruebas unitarias del resolver de contexto
- pruebas unitarias del nuevo calculator
- pruebas de integración del cálculo con datos semilla
- actualización de E2E REST si cambia el resultado esperado

---

## Qué debes entregar

Responde y trabaja siempre en este orden:

1. Diagnóstico del impacto del cambio
2. Diseño técnico mínimo propuesto
3. Lista de archivos a crear o modificar
4. Implementación incremental por fases
5. Cambios en base de datos y migraciones
6. Seeders a incorporar
7. Pruebas a agregar o actualizar
8. Riesgos / supuestos
9. Checklist final de cumplimiento

---

## Fases recomendadas de ejecución

### Fase 1
Diseñar tablas maestras y migraciones.

### Fase 2
Crear entidades JPA, repositories y adapters de consulta de rating.

### Fase 3
Implementar resolver de contexto técnico por ubicación.

### Fase 4
Implementar `CatalogBackedPremiumCalculator`.

### Fase 5
Persistir trazabilidad enriquecida.

### Fase 6
Actualizar pruebas.

### Fase 7
Actualizar ASSD/README explicando que el cálculo ya no usa valores hardcodeados.

---

## Instrucción final

Primero analiza el backend actual y los seeders entregados.
No programes todavía.

Devuélveme:
- diagnóstico del cambio
- plan de implementación incremental
- lista exacta de archivos que tocarás
- validación de que los seeders están alineados con el esquema propuesto

Luego, en iteraciones siguientes, ejecutarás la implementación fase por fase.
