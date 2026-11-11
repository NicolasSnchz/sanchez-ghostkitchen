#!/usr/bin/env bash
# Deja SonarQube listo para analizar el proyecto: clave nueva para admin,
# modo estandar, proyecto creado, puerta de calidad con los umbrales del
# protocolo y un token para Maven (exportado en TOKEN_SONAR).
set -euo pipefail

URL="http://localhost:9000"
PROYECTO="${PROYECTO:-NicolasSnchz_sanchez-ghostkitchen}"
CLAVE="${CLAVE_SONAR:?Falta CLAVE_SONAR}"
PUERTA="GhostKitchen"

curl -sf -u admin:admin -X POST "$URL/api/users/change_password" \
  --data-urlencode "login=admin" --data-urlencode "previousPassword=admin" --data-urlencode "password=$CLAVE" \
  || echo "La clave de admin ya habia cambiado"
api() { curl -sf -u "admin:$CLAVE" -X POST "$URL/api/$1" "${@:2}"; }

# Modo estandar: reglas con tipos clasicos (bug, vulnerabilidad, code smell) y deuda tecnica.
api settings/set --data-urlencode "key=sonar.multi-quality-mode.enabled" --data-urlencode "value=false" \
  || echo "La version de SonarQube no tiene modo MQR configurable"

api projects/create --data-urlencode "project=$PROYECTO" --data-urlencode "name=GhostKitchen Hunter" >/dev/null \
  || echo "El proyecto ya existia"

api qualitygates/create --data-urlencode "name=$PUERTA" >/dev/null || echo "La puerta ya existia"
condicion() {
  api qualitygates/create_condition --data-urlencode "gateName=$PUERTA" --data-urlencode "metric=$1" \
    --data-urlencode "op=$2" --data-urlencode "error=$3" >/dev/null \
    && echo "Condicion: $1 $2 $3" || echo "No se pudo crear la condicion $1"
}
condicion coverage LT 70                 # M1: cobertura global >= 70 %
condicion duplicated_lines_density GT 3  # M4: duplicacion <= 3 %
condicion sqale_rating GT 1              # M3: mantenibilidad A (deuda <= 5 % del costo de desarrollo)
condicion reliability_rating GT 1        # sin bugs
condicion security_rating GT 1           # sin vulnerabilidades
api qualitygates/select --data-urlencode "gateName=$PUERTA" --data-urlencode "projectKey=$PROYECTO"

TOKEN=$(api user_tokens/generate --data-urlencode "name=ci-$(date +%s)" | sed -E 's/.*"token":"([^"]+)".*/\1/')
echo "::add-mask::$TOKEN"
echo "TOKEN_SONAR=$TOKEN" >> "$GITHUB_ENV"
