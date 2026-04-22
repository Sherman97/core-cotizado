# Cotizador de Seguros de Danos

Proyecto fullstack para gestionar cotizaciones de danos con guardado por pasos.

- `backend`: Spring Boot + JPA + Flyway + MariaDB
- `frontend`: Angular (wizard de cotizacion)

## Estado funcional actual

El sistema ya permite:

- Crear folio de cotizacion (con soporte de idempotencia)
- Guardar informacion general por pasos
- Guardar layout de ubicaciones
- Guardar y editar ubicaciones
- Configurar coberturas
- Calcular prima
- Consultar estado final y resultados por ubicacion
- Guardar cotizacion final
- Consultar catalogo de agentes
- Consultar catalogo geografico en cascada (departamento -> ciudad -> codigo postal)

## Levantar entorno rapido

Desde la raiz:

```bash
docker compose up -d
```

Servicios:

- MariaDB
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:4200`

## Flujo de negocio (paso a paso)

1. Crear cotizacion (`POST /v1/folios`)
2. Guardar informacion general (`PUT /v1/quotes/{folio}/general-info`)
3. Guardar ubicaciones (`PUT /v1/quotes/{folio}/locations`)
4. Guardar coberturas (`PUT /v1/quotes/{folio}/coverage-options`)
5. Calcular (`POST /v1/quotes/{folio}/calculate`)
6. Consultar estado/resultados (`GET /state`, `GET /locations/results`)
7. Guardar final (`POST /v1/quotes/{folio}/save`)

## Persistencia por pasos

El sistema conserva datos en cada paso del wizard:

- General info se guarda de forma parcial y no depende de ubicaciones/coberturas.
- Ubicaciones se guardan como reemplazo de lista y se pueden actualizar por item.
- Coberturas se guardan de forma independiente.
- El calculo usa lo que ya este persistido.

## Reglas minimas para que el calculo salga bien

Una ubicacion puede estar guardada pero quedar excluida del calculo si falta informacion minima.

Campos minimos relevantes:

- Quote:
  - `riskClassification`
  - `businessType`
- Ubicacion:
  - `address`
  - `postalCode` valido (5-6 digitos)
  - `occupancyType`
  - `fireKey`
  - `constructionLevel > 0`
  - `constructionYear >= 1900`
- Debe existir al menos una cobertura seleccionada.

Si algo falta, el backend responde alertas de exclusion y prima en `0` para esos casos.

## Catalogos y relaciones

### Geografia (seed en BD, cascada real)

Se crea un catalogo jerarquico:

- Departamento -> Ciudades -> Codigos postales

Datos semilla actuales:

- Cundinamarca:
  - Bogota: `110111`, `110121`
  - Soacha: `250051`, `250052`
  - Chia: `250001`, `250002`
- Antioquia:
  - Medellin: `050001`, `050021`
  - Envigado: `055420`, `055421`
  - Rionegro: `054040`, `054041`
- Atlantico:
  - Barranquilla: `080001`, `080020`
  - Soledad: `083001`, `083002`
  - Puerto Colombia: `081001`, `081002`

### Agentes (seed en BD)

Catalogo de agentes:

- `AGT-001` / Juan Perez / BROKER / Bogota Centro / activo
- `AGT-002` / Maria Gomez / DIRECT / Medellin Norte / activo

Comportamiento:

- Si llega `agentCode` en general-info, backend valida que exista y este activo.
- Se persiste `agentCode` y `agentNameSnapshot` en la cotizacion.

### Ocupacion y fireKey

Mapa base actual:

- `OFFICE` -> `GIR-OFF-01`
- `COMMERCE` -> `GIR-RET-01`
- `WAREHOUSE` -> `GIR-WAR-01`
- `RESTAURANT` -> `GIR-RES-01`
- `LIGHT_INDUSTRY` -> `GIR-IND-01`

### Nivel y anio de construccion

No existe hoy una tabla catalogo dedicada para `constructionLevel` ni `constructionYear`.
En frontend se manejan como listas controladas (`select`) para evitar valores invalidos.

## Comportamiento actual del frontend (wizard)

En el paso de ubicaciones:

- `department`, `city`, `postalCode` son selects en cascada.
- `occupancyType` y `constructionType` son selects.
- `fireKey` es select dependiente de `occupancyType`.
- `constructionLevel` y `constructionYear` son selects controlados.
- Ya no se permite enviar `postalCode` fuera del catalogo de la ciudad seleccionada.

## Endpoints principales

Base URL: `http://localhost:8080/v1`

- Folios:
  - `POST /folios`
- Cotizaciones:
  - `GET /quotes`
  - `GET /quotes/{folio}/general-info`
  - `PUT /quotes/{folio}/general-info`
  - `GET /quotes/{folio}/locations/layout`
  - `PUT /quotes/{folio}/locations/layout`
  - `GET /quotes/{folio}/state`
  - `POST /quotes/{folio}/save`
- Ubicaciones:
  - `GET /quotes/{folio}/locations`
  - `PUT /quotes/{folio}/locations`
  - `PATCH /quotes/{folio}/locations/{indice}`
  - `GET /quotes/{folio}/locations/summary`
  - `GET /quotes/{folio}/locations/results`
- Coberturas:
  - `GET /quotes/{folio}/coverage-options`
  - `PUT /quotes/{folio}/coverage-options`
- Calculo:
  - `POST /quotes/{folio}/calculate`
- Catalogos:
  - `GET /catalogs/geography`
  - `GET /agents`
  - `GET /agents/{code}`

## Payload minimo recomendado para calcular

### General info

```json
{
  "productCode": "DANOS",
  "customerName": "Cliente Demo",
  "currency": "COP",
  "agentCode": "AGT-001",
  "riskClassification": "LOW",
  "businessType": "RETAIL",
  "observations": "Texto opcional"
}
```

### Item de ubicacion

```json
{
  "locationIndex": 1,
  "locationName": "Matriz Centro",
  "city": "Bogota",
  "department": "Cundinamarca",
  "address": "Calle 100 #10-20",
  "postalCode": "110111",
  "constructionType": "CONCRETE",
  "constructionLevel": 2,
  "constructionYear": 2018,
  "occupancyType": "OFFICE",
  "fireKey": "GIR-OFF-01",
  "insuredValue": 1500000
}
```

## Migraciones relevantes recientes

- `V14__seed_geography_catalog.sql`
- `V15__create_agents_catalog_and_quote_agent_fields.sql`
- `V16__add_challenge_minimum_quote_fields.sql`

## Pruebas y cobertura backend

Ultima ejecucion registrada: **21 de abril de 2026**

Comandos:

```bash
cd backend
./gradlew clean test
./gradlew jacocoTestReport
```

Resultado:

- Tests: **91**
- Fallos: **0**
- Errores: **0**
- Omitidos: **0**

Cobertura JaCoCo global:

- Lineas: **92.52%** (594/642)
- Ramas: **91.38%** (106/116)
- Instrucciones: **92.62%** (2824/3049)
- Metodos: **91.03%** (203/223)
- Clases: **93.65%** (59/63)
- Complejidad: **89.32%** (251/281)

## Estructura del repo

```text
core_seguros/
  backend/
  frontend/
  docker-compose.yml
```

## Diagnostico rapido

- Contenedores: `docker ps`
- Logs backend: `docker compose logs -f backend`
- Logs frontend: `docker compose logs -f frontend`
