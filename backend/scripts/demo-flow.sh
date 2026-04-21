#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
FIXTURES_DIR="$(cd "$(dirname "$0")/.." && pwd)/fixtures"

echo "==> Creating folio"
CREATE_RESPONSE="$(curl -s -X POST "${BASE_URL}/v1/folios")"
FOLIO="$(echo "${CREATE_RESPONSE}" | sed -n 's/.*"numeroFolio":"\([^"]*\)".*/\1/p')"

if [[ -z "${FOLIO}" ]]; then
  echo "No se pudo obtener numeroFolio de la respuesta:"
  echo "${CREATE_RESPONSE}"
  exit 1
fi

echo "Folio: ${FOLIO}"

echo "==> Saving general info"
curl -s -X PUT "${BASE_URL}/v1/quotes/${FOLIO}/general-info" \
  -H "Content-Type: application/json" \
  -d @"${FIXTURES_DIR}/general-info.json" >/dev/null

echo "==> Saving location layout"
curl -s -X PUT "${BASE_URL}/v1/quotes/${FOLIO}/locations/layout" \
  -H "Content-Type: application/json" \
  -d @"${FIXTURES_DIR}/layout.json" >/dev/null

echo "==> Saving locations"
curl -s -X PUT "${BASE_URL}/v1/quotes/${FOLIO}/locations" \
  -H "Content-Type: application/json" \
  -d @"${FIXTURES_DIR}/locations.json" >/dev/null

echo "==> Saving coverages"
curl -s -X PUT "${BASE_URL}/v1/quotes/${FOLIO}/coverage-options" \
  -H "Content-Type: application/json" \
  -d @"${FIXTURES_DIR}/coverages.json" >/dev/null

echo "==> Calculating quote"
curl -s -X POST "${BASE_URL}/v1/quotes/${FOLIO}/calculate" >/dev/null

echo "==> Final state"
curl -s "${BASE_URL}/v1/quotes/${FOLIO}/state"
echo
