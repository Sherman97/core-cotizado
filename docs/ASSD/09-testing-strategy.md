# 09. Testing Strategy

## Enfoque

Validacion en tres niveles:

1. dominio
2. casos de uso (application)
3. E2E REST sobre stack Spring Boot

## Stack de pruebas

- JUnit 5
- AssertJ
- Mockito
- Spring Boot Test
- MockMvc
- JaCoCo

## Cobertura funcional ya implementada

- idempotencia en creacion de folio
- versionado de negocio en ediciones parciales
- reglas de calculabilidad por ubicacion
- persistencia de resultado y trazabilidad de calculo
- estados finales de quote
- validaciones API
- consultas read-only de servicios de catalogo

## Pruebas E2E REST

Clase:

- `QuoteApiE2ETest`

Escenarios:

1. crear folio con replay idempotente y consultar general-info
2. guardar layout, registrar/editar ubicaciones y consultar resumen/listado
3. configurar datos y coberturas, calcular, consultar estado final con alertas

Tecnologia:

- `@SpringBootTest`
- `@AutoConfigureMockMvc`
- perfil `test`

## Pruebas de servicios de catalogo (fase read-only)

Clase:

- `CatalogQueryServicesTest`

Valida consultas de:

- zona por codigo postal
- ocupacion
- construccion
- tasas/factores de coberturas
- parametros globales

## Comandos de validacion

```bash
cd backend
./gradlew test
./gradlew jacocoTestCoverageVerification
```

## Estado actual

- `./gradlew test` en verde
- `./gradlew jacocoTestCoverageVerification` en verde
