// Captura los reportes locales del build: el resumen de pruebas y el reporte HTML de JaCoCo.
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

(async () => {
  const base = path.resolve('evidencias/reportes');
  const vistas = [
    ['pruebas-en-verde', path.join(base, 'pruebas.html')],
    ['jacoco-resumen', path.join(base, 'jacoco-html/index.html')],
    ['jacoco-dominio-puntos', path.join(base, 'jacoco-html/co.edu.udes.ghostkitchen.dominio.puntos/index.html')],
  ];
  const navegador = await chromium.launch();
  const pagina = await navegador.newPage({ viewport: { width: 1280, height: 900 }, deviceScaleFactor: 2 });
  for (const [nombre, archivo] of vistas) {
    if (!fs.existsSync(archivo)) {
      console.log(`No existe ${archivo}; se omite ${nombre}`);
      continue;
    }
    await pagina.goto('file://' + archivo);
    await pagina.screenshot({ path: `evidencias/${nombre}.png`, fullPage: true });
  }
  await navegador.close();
})().catch((error) => { console.error(error); process.exit(1); });
