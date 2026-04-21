#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/../.." && pwd)"
cd "${ROOT_DIR}"

docker compose up -d --build mariadb backend frontend

echo
echo "Stack levantado:"
echo "- Backend: http://localhost:8080"
echo "- Health:  http://localhost:8080/actuator/health"
echo "- Frontend: http://localhost:4200"
echo "- MariaDB:  localhost:3307 (db: cotizador_danos, user: cotizador)"
echo
echo "Logs del stack (Ctrl+C para salir):"
docker compose logs -f backend frontend mariadb
