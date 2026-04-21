#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")/.."

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-mariadb}"
export SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL:-jdbc:mariadb://localhost:3307/cotizador_danos}"
export SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME:-cotizador}"
export SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-cotizador123}"

./gradlew bootRun
