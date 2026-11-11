#!/usr/bin/env bash
# Descarga las metricas reales del analisis y las resume para el README y el protocolo.
set -uo pipefail

URL="http://localhost:9000"
PROYECTO="${PROYECTO:-NicolasSnchz_sanchez-ghostkitchen}"
CLAVE="${CLAVE_SONAR:?Falta CLAVE_SONAR}"
mkdir -p evidencias
get() { curl -sf -u "admin:$CLAVE" "$URL/api/$1"; }

METRICAS="ncloc lines classes functions coverage line_coverage branch_coverage duplicated_lines_density \
complexity cognitive_complexity sqale_index sqale_debt_ratio sqale_rating code_smells bugs vulnerabilities \
security_hotspots reliability_rating security_rating"
echo "{" > evidencias/metricas-sonarqube.json
primera=1
for metrica in $METRICAS; do
  valor=$(get "measures/component?component=$PROYECTO&metricKeys=$metrica" \
    | sed -nE 's/.*"value":"([^"]*)".*/\1/p')
  [ -z "$valor" ] && continue
  [ $primera -eq 0 ] && echo "," >> evidencias/metricas-sonarqube.json
  printf '  "%s": "%s"' "$metrica" "$valor" >> evidencias/metricas-sonarqube.json
  primera=0
done
echo -e "\n}" >> evidencias/metricas-sonarqube.json

get "qualitygates/project_status?projectKey=$PROYECTO" > evidencias/puerta-de-calidad.json || true
get "issues/search?componentKeys=$PROYECTO&types=CODE_SMELL&ps=500&resolved=false" > evidencias/code-smells.json || true
get "issues/search?componentKeys=$PROYECTO&types=BUG,VULNERABILITY&ps=500&resolved=false" > evidencias/bugs-y-vulnerabilidades.json || true

python3 - <<'PY' >> "$GITHUB_STEP_SUMMARY"
import json
m = json.load(open("evidencias/metricas-sonarqube.json"))
ncloc = float(m.get("ncloc", "0") or 0)
deuda = float(m.get("sqale_index", "0") or 0)
por_kloc = deuda / (ncloc / 1000) if ncloc else 0
estado = json.load(open("evidencias/puerta-de-calidad.json")).get("projectStatus", {}).get("status", "?")
print("## Métricas de SonarQube\n")
print(f"Puerta de calidad: **{estado}**\n")
print("| Métrica | Valor |\n|---|---|")
for clave, valor in m.items():
    print(f"| {clave} | {valor} |")
print(f"| deuda por KLOC (min) | {por_kloc:.1f} |")
json.dump({"deuda_min_por_kloc": round(por_kloc, 1)}, open("evidencias/deuda-por-kloc.json", "w"))
PY
cat evidencias/metricas-sonarqube.json
