"""Calcula las metricas de paquetes de Robert C. Martin a partir de los imports reales.

Ca: paquetes que dependen de este. Ce: paquetes de los que este depende.
I = Ce / (Ca + Ce): inestabilidad. A: proporcion de interfaces y clases abstractas.
D = |A + I - 1|: distancia a la secuencia principal.
Uso: python3 scripts/acoplamiento-paquetes.py [--mermaid]
"""
import re
import sys
from collections import defaultdict
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent / "src/main/java"
BASE = "co.edu.udes.ghostkitchen."
depende = defaultdict(set)
tipos = defaultdict(lambda: [0, 0])  # [abstractos, total]

for archivo in RAIZ.rglob("*.java"):
    texto = archivo.read_text(encoding="utf-8")
    paquete = re.search(r"^package ([\w.]+);", texto, re.M).group(1)
    if paquete + "." == BASE:
        continue
    nombre = paquete.removeprefix(BASE)
    for importado in re.findall(r"^import (co\.edu\.udes\.ghostkitchen(?:\.[a-z]\w*)+)\.[A-Z]", texto, re.M):
        destino = importado.removeprefix(BASE)
        if destino != nombre:
            depende[nombre].add(destino)
    abstracto = bool(re.search(r"^(public )?(abstract class|interface) ", texto, re.M))
    tipos[nombre][1] += 1
    tipos[nombre][0] += 1 if abstracto else 0

def decimal(valor):
    return f"{valor:.2f}".replace(".", ",")


paquetes = sorted(set(tipos) | {d for ds in depende.values() for d in ds})
if "--dot" not in sys.argv:
    print("| Paquete | Tipos | Ca | Ce | I | A | D |")
    print("|---|---:|---:|---:|---:|---:|---:|")
    for p in paquetes:
        ca = sum(1 for otro, ds in depende.items() if p in ds and otro != p)
        ce = len(depende[p])
        inestabilidad = ce / (ca + ce) if ca + ce else 0
        abstractos, total = tipos[p]
        abstraccion = abstractos / total if total else 0
        distancia = abs(abstraccion + inestabilidad - 1)
        print(f"| `{p}` | {total} | {ca} | {ce} | {decimal(inestabilidad)} | {decimal(abstraccion)} | {decimal(distancia)} |")

COLORES = {"presentacion": "#d9e8f5", "seguridad": "#f5dede", "configuracion": "#ececec",
           "aplicacion": "#fdebc8", "persistencia": "#e3dcf7", "dominio": "#d8efdf"}
if "--dot" in sys.argv:
    print("digraph paquetes {")
    print('    graph [rankdir=TB, newrank=true, splines=spline, nodesep=0.3, ranksep=0.8, fontname="DejaVu Sans", fontsize=13, '
          'label="Dependencias entre paquetes calculadas de los imports reales.\\nSe omiten las flechas hacia dominio.modelo y dominio.excepcion (las usan casi todos; ver Ca en cada nodo).", labelloc=t];')
    print('    node [shape=tab, style=filled, fontname="DejaVu Sans", fontsize=12, color="#555555"];')
    print('    edge [color="#55555599", arrowsize=0.7];')
    for capa, color in COLORES.items():
        print(f'    subgraph cluster_{capa} {{ label="{capa}"; style="rounded,dashed"; color="#999999";')
        for p in paquetes:
            if p.split(".")[0] == capa:
                ca = sum(1 for otro, ds in depende.items() if p in ds and otro != p)
                ce = len(depende[p])
                i = ce / (ca + ce) if ca + ce else 0
                print(f'        "{p}" [fillcolor="{color}", label="{p}\\nCa={ca}  Ce={ce}  I={decimal(i)}"];')
        print("    }")
    omitidos = {"dominio.modelo", "dominio.excepcion"}
    for p in paquetes:
        for d in sorted(depende[p]):
            if d not in omitidos or p.startswith("dominio"):
                print(f'    "{p}" -> "{d}";')
    print("}")
