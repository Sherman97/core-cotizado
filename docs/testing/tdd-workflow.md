# Flujo TDD por Historia de Usuario

## Entrada
- `docs/hus/<HU>.md`
- `docs/gherkin/<HU>.feature`
- dependencias previas mínimas ya implementadas

## Fase Red
1. Leer reglas y criterios de aceptación.
2. Diseñar pruebas antes de código productivo.
3. Mapear escenarios Gherkin a tests.
4. Ejecutar y confirmar fallo inicial.

## Fase Green
1. Implementar el mínimo código necesario.
2. Hacer pasar pruebas.
3. No agregar lógica no solicitada.

## Fase Refactor
1. Mejorar diseño interno.
2. Mantener pruebas en verde.
3. Confirmar cobertura objetivo.

## Cierre de HU
- Validar cobertura >= 80% sobre lógica crítica.
- Registrar deuda técnica si existe.
- Preparar siguiente HU.
