# 09. Testing Strategy

## Estrategia aplicada

Se usa TDD y validacion en tres niveles:

1. dominio
2. casos de uso (application)
3. E2E REST con stack real Spring Boot

## Stack de pruebas

- JUnit 5
- AssertJ
- Mockito
- Spring Boot Test
- MockMvc
- JaCoCo

## Pruebas unitarias/casos de uso relevantes

Cobertura directa de:

- idempotencia de creacion de folio
- versionado de negocio en ediciones parciales
- reglas de calculabilidad por ubicacion
- persistencia de resultado de calculo y trazabilidad
- manejo de errores de dominio

## Pruebas E2E REST implementadas

Clase:

- `QuoteApiE2ETest`

Flujos cubiertos:

1. crear folio + replay idempotente + consulta general-info
2. crear folio + guardar layout + registrar/editar ubicaciones + consultar resumen/listado
3. crear folio + general-info + ubicaciones + coberturas + calcular + consultar estado final con alertas

Tecnica:

- `@SpringBootTest`
- `@AutoConfigureMockMvc`
- perfil `test` con H2

## Cobertura

JaCoCo configurado para paquetes criticos (`application`, `domain`) con umbral minimo:

- `LINE_COVERED_RATIO >= 80%`

Comandos:

```bash
./gradlew test
./gradlew jacocoTestCoverageVerification
```

Estado final:

- ambos comandos en verde.
