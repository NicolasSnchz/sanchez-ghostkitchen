"""Resume los reportes XML de Surefire (unitarias) y Failsafe (integracion).

Uso: python3 scripts/resumen-pruebas.py [--html salida.html]
Escribe una tabla Markdown por la salida estandar y, con --html, una pagina para capturarla.
"""
import html
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

SUITES = [("Unitarias (Surefire)", Path("target/surefire-reports")),
          ("Integracion (Failsafe)", Path("target/failsafe-reports"))]

filas = []
for tipo, carpeta in SUITES:
    for reporte in sorted(carpeta.glob("TEST-*.xml")):
        raiz = ET.parse(reporte).getroot()
        filas.append({
            "tipo": tipo,
            "clase": raiz.get("name", reporte.stem).rsplit(".", 1)[-1],
            "pruebas": int(raiz.get("tests", 0)),
            "fallas": int(raiz.get("failures", 0)) + int(raiz.get("errors", 0)),
            "omitidas": int(raiz.get("skipped", 0)),
            "segundos": float(raiz.get("time", 0) or 0),
        })

total = sum(f["pruebas"] for f in filas)
fallas = sum(f["fallas"] for f in filas)
omitidas = sum(f["omitidas"] for f in filas)
estado = "sin reportes" if total == 0 else ("todas en verde" if fallas == 0 else f"{fallas} con fallas")

print(f"**{total} pruebas, {estado}, {omitidas} omitidas.**\n")
print("| Tipo | Clase | Pruebas | Fallas | Omitidas | Segundos |")
print("|---|---|---:|---:|---:|---:|")
for f in filas:
    print(f"| {f['tipo']} | `{f['clase']}` | {f['pruebas']} | {f['fallas']} | {f['omitidas']} | {f['segundos']:.2f} |")

if "--html" in sys.argv:
    salida = Path(sys.argv[sys.argv.index("--html") + 1])
    cuerpo = "".join(
        f"<tr class='{'mal' if f['fallas'] else 'bien'}'><td>{html.escape(f['tipo'])}</td>"
        f"<td><code>{html.escape(f['clase'])}</code></td><td>{f['pruebas']}</td><td>{f['fallas']}</td>"
        f"<td>{f['omitidas']}</td><td>{f['segundos']:.2f}</td></tr>" for f in filas)
    salida.write_text(f"""<!doctype html><html lang="es"><meta charset="utf-8"><title>Pruebas</title>
<style>body{{font:15px system-ui,sans-serif;margin:32px;color:#191627}}h1{{font-size:22px;margin:0 0 4px}}
p{{margin:0 0 18px;color:#555}}table{{border-collapse:collapse;width:100%}}th,td{{padding:6px 10px;
border-bottom:1px solid #ddd;text-align:left}}td:nth-child(n+3),th:nth-child(n+3){{text-align:right}}
tr.bien td:nth-child(4){{color:#1a7f37}}tr.mal{{background:#ffebe9}}code{{font-size:14px}}</style>
<h1>GhostKitchen Hunter: {total} pruebas, {html.escape(estado)}</h1>
<p>Resumen generado en GitHub Actions a partir de los reportes XML de Surefire y Failsafe ({omitidas} omitidas).</p>
<table><tr><th>Tipo</th><th>Clase</th><th>Pruebas</th><th>Fallas</th><th>Omitidas</th><th>Segundos</th></tr>
{cuerpo}</table></html>""", encoding="utf-8")
