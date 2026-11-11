// Captura el panel del proyecto en SonarQube como evidencia de las metricas.
const { chromium } = require('playwright');

(async () => {
  const navegador = await chromium.launch();
  const pagina = await navegador.newPage({ viewport: { width: 1440, height: 1000 }, deviceScaleFactor: 2 });
  const base = 'http://localhost:9000';
  const proyecto = process.env.PROYECTO || 'NicolasSnchz_sanchez-ghostkitchen';
  await pagina.goto(`${base}/sessions/new`);
  await pagina.fill('input[name="login"]', 'admin');
  await pagina.fill('input[name="password"]', process.env.CLAVE_SONAR);
  await pagina.press('input[name="password"]', 'Enter');
  await pagina.waitForURL((url) => !url.pathname.startsWith('/sessions'));
  const vistas = [
    ['sonarqube-resumen', `/dashboard?id=${proyecto}&codeScope=overall`],
    ['sonarqube-medidas', `/component_measures?id=${proyecto}`],
    ['sonarqube-incidencias', `/project/issues?id=${proyecto}&resolved=false`],
  ];
  for (const [nombre, ruta] of vistas) {
    await pagina.goto(base + ruta, { waitUntil: 'networkidle' });
    await pagina.waitForTimeout(2500);
    await pagina.screenshot({ path: `evidencias/${nombre}.png`, fullPage: true });
  }
  await navegador.close();
})().catch((error) => { console.error(error); process.exit(1); });
