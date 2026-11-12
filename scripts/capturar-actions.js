// Captura las paginas publicas de GitHub Actions: el historial del flujo de CI, el build que
// fallo por la violacion intencional de ArchUnit y el build en verde despues del revert.
const { chromium } = require('playwright');

(async () => {
  const repo = process.env.REPO || 'NicolasSnchz/sanchez-ghostkitchen';
  const vistas = [['actions-historial', `https://github.com/${repo}/actions/workflows/ci.yml`]];
  if (process.env.FALLIDO) {
    vistas.push(['actions-build-fallido', `https://github.com/${repo}/actions/runs/${process.env.FALLIDO}`]);
  }
  if (process.env.VERDE) {
    vistas.push(['actions-build-verde', `https://github.com/${repo}/actions/runs/${process.env.VERDE}`]);
  }
  const navegador = await chromium.launch();
  const pagina = await navegador.newPage({ viewport: { width: 1440, height: 1000 }, deviceScaleFactor: 2 });
  for (const [nombre, url] of vistas) {
    await pagina.goto(url, { waitUntil: 'networkidle' });
    await pagina.waitForTimeout(2000);
    await pagina.screenshot({ path: `evidencias/${nombre}.png`, fullPage: true });
  }
  await navegador.close();
})().catch((error) => { console.error(error); process.exit(1); });
