# Cotizador de Seguros de Danos

Este proyecto tiene dos partes:

- `backend`: API en Spring Boot
- `frontend`: app Angular para capturar y consultar cotizaciones

La idea general es simple: creas un folio, llenas datos, agregas ubicaciones, eliges coberturas, calculas, y ves el resultado.

## Que ya hace hoy

- Crear folios de cotizacion
- Guardar datos generales
- Guardar ubicaciones (una o varias)
- Configurar coberturas
- Calcular prima
- Ver estado final
- Consultar resultados por ubicacion
- Usar catalogos desde BD (incluyendo geografia en cascada)

## Como levantarlo rapido

Desde la raiz del repo:

```bash
docker compose up -d
```

Esto levanta:

- MariaDB
- Backend (`http://localhost:8080`)
- Frontend (`http://localhost:4200`)

Si prefieres correr frontend local:

```bash
cd frontend
npm install
ng serve
```

## Flujo funcional (sin tecnicismos)

1. Crear cotizacion
2. Completar informacion general
3. Agregar ubicaciones
4. Elegir coberturas
5. Calcular
6. Revisar resultado y alertas

## Datos base que ya vienen en la BD

Estos datos se crean por migraciones (seeds), para que el sistema funcione desde el primer arranque.

### Geografia (catalogo en cascada)

Se crearon 3 departamentos, cada uno con 3 ciudades, y cada ciudad con 2 codigos postales.

- **Cundinamarca**
  - Bogota: `110111`, `110121`
  - Soacha: `250051`, `250052`
  - Chia: `250001`, `250002`
- **Antioquia**
  - Medellin: `050001`, `050021`
  - Envigado: `055420`, `055421`
  - Rionegro: `054040`, `054041`
- **Atlantico**
  - Barranquilla: `080001`, `080020`
  - Soledad: `083001`, `083002`
  - Puerto Colombia: `081001`, `081002`

En el front, esto se ve como:

- primero eliges **departamento**
- luego se filtran las **ciudades**
- luego se filtran los **codigos postales**

### Coberturas base

- `FIRE` (Incendio)
- `EARTHQUAKE` (Terremoto)
- `FLOOD` (Inundacion)

### Ocupaciones base

- `OFFICE`
- `COMMERCE`
- `WAREHOUSE`
- `RESTAURANT`
- `LIGHT_INDUSTRY`

### Tipos constructivos base

- `CONCRETE`
- `MIXED`
- `WOOD`

## Endpoints principales

Base URL: `http://localhost:8080/v1`

- `POST /folios`
- `GET/PUT /quotes/{folio}/general-info`
- `GET/PUT /quotes/{folio}/locations/layout`
- `GET/PUT/PATCH /quotes/{folio}/locations...`
- `GET/PUT /quotes/{folio}/coverage-options`
- `POST /quotes/{folio}/calculate`
- `GET /quotes/{folio}/state`
- `GET /quotes/{folio}/locations/results`
- `GET /catalogs/geography`

## Nota importante sobre validaciones de ubicacion

Para que una ubicacion pase bien hacia calculo:

- debe tener direccion
- debe tener codigo postal valido
- debe tener ocupacion
- debe existir al menos una cobertura seleccionada

Si no, el sistema puede guardarla, pero luego la excluye del calculo y deja alertas.

## Estructura rapida del repo

```text
core_seguros/
  backend/
  frontend/
  docker-compose.yml
```

## Si algo no responde

- Revisa contenedores: `docker ps`
- Revisa logs backend: `docker compose logs -f backend`
- Revisa logs frontend: `docker compose logs -f frontend`

Si quieres una guia paso a paso para pruebas funcionales (tipo checklist), la puedo agregar tambien.
