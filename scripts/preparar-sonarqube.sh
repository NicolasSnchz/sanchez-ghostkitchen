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

# Quality Profile propio: hereda Sonar way y activa la regla de complejidad ciclomatica por metodo (metrica M2).
PERFIL="GhostKitchen"
api qualityprofiles/create --data-urlencode "language=java" --data-urlencode "name=$PERFIL" >/dev/null \
  || echo "El perfil ya existia"
api qualityprofiles/change_parent --data-urlencode "language=java" --data-urlencode "qualityProfile=$PERFIL" \
  --data-urlencode "parentQualityProfile=Sonar way"
CLAVE_PERFIL=$(curl -sf -u "admin:$CLAVE" "$URL/api/qualityprofiles/search?language=java&qualityProfile=$PERFIL" \
  | jq -r '.profiles[0].key')
api qualityprofiles/activate_rule --data-urlencode "key=$CLAVE_PERFIL" --data-urlencode "rule=java:S1541" \
  --data-urlencode "severity=MAJOR" --data-urlencode "params=Max=10" && echo "Regla java:S1541 activa (Max=10)"
api qualityprofiles/activate_rule --data-urlencode "key=$CLAVE_PERFIL" --data-urlencode "rule=java:S3776" \
  --data-urlencode "params=Threshold=15" && echo "Regla java:S3776 activa (Threshold=15)"
api qualityprofiles/add_project --data-urlencode "language=java" --data-urlencode "qualityProfile=$PERFIL" \
  --data-urlencode "project=$PROYECTO"

api qualitygates/create --data-urlencode "name=$PUERTA" >/dev/null || echo "La puerta ya existia"
condicion() {
  api qualitygates/create_condition --data-urlencode "gateName=$PUERTA" --data-urlencode "metric=$1" \
    --data-urlencode "op=$2" --data-urlencode "error=$3" >/dev/null \
    && echo "Condicion: $1 $2 $3" || echo "No se pudo crear la condicion $1"
}
condicion coverage LT 70                 # M1: cobertura global >= 70 %
condicion duplicated_lines_density GT 3  # M4: duplicacion <= 3 %
condicion sqale_rating GT 1              # M3: mantenibilidad A (deuda <= 5 % del costo de desarrollo)
# M3 del protocolo: deuda <= 30 min por KLOC. SonarQube no tiene esa metrica, pero con su costo por defecto
# de 30 min por linea, sqale_debt_ratio = 100 * deuda / (30 * lineas), asi que 30 min/KLOC equivale a 0,1 %.
condicion sqale_debt_ratio GT 0.1
condicion reliability_rating GT 1        # sin bugs
condicion security_rating GT 1           # sin vulnerabilidades
api qualitygates/select --data-urlencode "gateName=$PUERTA" --data-urlencode "projectKey=$PROYECTO"

TOKEN=$(api user_tokens/generate --data-urlencode "name=ci-$(date +%s)" | sed -E 's/.*"token":"([^"]+)".*/\1/')
echo "::add-mask::$TOKEN"
echo "TOKEN_SONAR=$TOKEN" >> "$GITHUB_ENV"
