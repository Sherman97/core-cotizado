# Política de cobertura por HU

## Objetivo
Cada Historia de Usuario debe alcanzar **mínimo 80% de cobertura sobre la lógica crítica implementada**, no sobre líneas triviales.

## Qué se considera lógica crítica
- reglas de negocio
- validaciones del dominio
- estados del flujo
- cálculo por ubicación
- consolidación de primas
- exclusión de ubicaciones inválidas
- trazabilidad del cálculo
- versionado básico

## Qué NO cuenta como prioridad de cobertura
- getters/setters
- DTOs triviales sin lógica
- código de configuración sin comportamiento
- mocks sin reglas
- serialización decorativa

## Mínimos por HU
1. Al menos 1 prueba por criterio de aceptación relevante.
2. Al menos 1 caso feliz.
3. Al menos 1 caso de error o borde cuando aplique.
4. Trazabilidad explícita Gherkin -> test.

## Tipos de pruebas esperadas
- Unit tests para lógica de negocio.
- Integration tests para flujos críticos cuando la HU lo requiera.
- No se exige UI test en fases iniciales del MVP.
