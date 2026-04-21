#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")/.."

SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-local}" \
./gradlew bootRun
