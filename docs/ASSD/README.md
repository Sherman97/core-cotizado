# Documentación ASSD del MVP de Cotizador de Seguros de Daños

## Propósito

Este directorio contiene la documentación ASSD formal del MVP. La colección está organizada para que un revisor técnico pueda entender:

- el problema que resuelve el sistema
- el flujo funcional
- el modelo de dominio
- la arquitectura
- el diseño de datos
- el cálculo
- la API
- la UI
- la estrategia de pruebas
- el despliegue
- los supuestos, decisiones y riesgos

## Orden recomendado de lectura

1. [00-backend-assd-completo.md](/Users/germanrojas/Desktop/core_seguros/docs/ASSD/00-backend-assd-completo.md)
2. [13-backend-module-analysis.md](/Users/germanrojas/Desktop/core_seguros/docs/ASSD/13-backend-module-analysis.md)

## Resumen de cada documento

### 01-overview

Contexto, stakeholders, alcance, fuera de alcance y glosario.

### 02-business-flow

Flujo funcional del wizard, reglas por etapa y escenarios relevantes.

### 03-domain-model

Modelo de dominio, agregado principal, invariantes y mapeo a tablas.

### 04-architecture

Justificación del monolito modular, módulos, frontend y trade-offs.

### 05-data-design

Diseño de persistencia sobre MariaDB, tablas, índices e integridad.

### 06-calculation-design

Diseño funcional del cálculo, clasificación de ubicaciones y trazabilidad.

### 07-api-design

Endpoints, contratos funcionales, errores y secuencia de consumo.

### 08-ui-ux

Wizard, pantallas, estados visuales y lineamientos enterprise.

### 09-testing-strategy

Enfoque TDD, pruebas unitarias e integración objetivo.

### 10-deployment-and-dev-setup

Entorno local, Docker, variables y troubleshooting.

### 11-decisions-assumptions-risks

Decisiones, supuestos, limitaciones y deuda técnica aceptada.

### 12-traceability-matrix

Mapa entre requerimientos, módulos, endpoints, pruebas y documentación.

### 13-backend-module-analysis

Analisis del backend real: modulos implementados, clases existentes, logica por servicio, dependencias efectivas y hallazgos tecnicos del repositorio.

## Cómo usar la documentación durante revisión técnica

- usar `01` y `02` para explicar el problema y el flujo
- usar `03`, `04`, `05` y `06` para defender el diseño técnico
- usar `07` y `08` para explicar cómo se consume el sistema
- usar `09` para justificar el enfoque TDD
- usar `10` y `11` para responder preguntas de operación y madurez
- usar `12` para demostrar trazabilidad formal
- usar `13` para defender la implementacion real del backend y sus dependencias

## Criterio de uso

La documentación debe leerse como entregable formal del MVP, no como especificación de producto final. Cuando exista una simplificación deliberada, debe consultarse [11-decisions-assumptions-risks.md](/Users/germanrojas/Desktop/core_seguros/docs/ASSD/11-decisions-assumptions-risks.md).

## Nota de version consolidada

Para evaluación backend, la referencia principal y vigente es [00-backend-assd-completo.md](/Users/germanrojas/Desktop/core_seguros/docs/ASSD/00-backend-assd-completo.md). Este documento está basado en el código implementado actual del backend y excluye alcance de frontend.
