// Recorre la aplicacion con los datos de demostracion y guarda capturas reales.
const { chromium, devices } = require('playwright');
const fs = require('fs');

const BASE = process.env.BASE_URL || 'http://localhost:8080';
const SALIDA = 'evidencias/capturas';

async function capturar(pagina, nombre) {
  await pagina.waitForLoadState('networkidle');
  await pagina.waitForTimeout(1200);
  await pagina.screenshot({ path: `${SALIDA}/${nombre}.png`, fullPage: true });
}

async function ingresar(pagina, correo, clave) {
  await pagina.goto(`${BASE}/ingresar`);
  await pagina.fill('#correo', correo);
  await pagina.fill('#clave', clave);
  await Promise.all([pagina.waitForNavigation(), pagina.click('form[data-ingreso] button[type="submit"]')]);
}

(async () => {
  fs.mkdirSync(SALIDA, { recursive: true });
  const navegador = await chromium.launch();
  const errores = [];

  const visitante = await navegador.newPage({ viewport: { width: 1440, height: 900 }, deviceScaleFactor: 2 });
  visitante.on('pageerror', (e) => errores.push(`visitante: ${e.message}`));
  await visitante.goto(BASE);
  await capturar(visitante, '01-portada');
  await visitante.goto(`${BASE}/explorar`);
  await capturar(visitante, '02-explorar');
  await visitante.goto(`${BASE}/ranking`);
  await capturar(visitante, '07-ranking');
  await visitante.goto(`${BASE}/ingresar`);
  await capturar(visitante, '08-ingresar');
  await visitante.goto(`${BASE}/cocinas/999999`);
  await capturar(visitante, '11-error-404');

  const cazador = await navegador.newPage({ viewport: { width: 1440, height: 900 }, deviceScaleFactor: 2 });
  cazador.on('pageerror', (e) => errores.push(`cazador: ${e.message}`));
  await ingresar(cazador, 'cazador@ghostkitchen.co', 'Cazador2026');
  await cazador.goto(`${BASE}/explorar?abiertas=true`);
  const pendiente = cazador.locator('.ficha', { hasNot: cazador.locator('.cazada') }).first();
  await pendiente.locator('h3 a').click();
  await cazador.waitForLoadState('networkidle');
  await capturar(cazador, '03-cocina-antes');
  await Promise.all([cazador.waitForNavigation(), cazador.click('form[action$="/cazar"] button')]);
  await capturar(cazador, '04-cocina-cazada');
  await cazador.goto(`${BASE}/perfil`);
  await capturar(cazador, '05-panel');
  await cazador.goto(`${BASE}/retos`);
  await capturar(cazador, '06-retos');
  await Promise.all([cazador.waitForNavigation(), cazador.click('.deshacer form button')]);
  await capturar(cazador, '06b-deshacer');

  const admin = await navegador.newPage({ viewport: { width: 1440, height: 900 }, deviceScaleFactor: 2 });
  admin.on('pageerror', (e) => errores.push(`admin: ${e.message}`));
  await ingresar(admin, 'admin@ghostkitchen.co', 'Admin2026');
  await capturar(admin, '09-admin-cocinas');
  await admin.goto(`${BASE}/admin/retos`);
  await capturar(admin, '10-admin-retos');

  const movil = await navegador.newPage({ ...devices['iPhone 13'] });
  movil.on('pageerror', (e) => errores.push(`movil: ${e.message}`));
  await movil.goto(BASE);
  await capturar(movil, '12-movil-portada');
  await movil.goto(`${BASE}/explorar`);
  await capturar(movil, '13-movil-explorar');

  await navegador.close();
  if (errores.length > 0) {
    console.error('Errores de JavaScript en las paginas:\n' + errores.join('\n'));
    process.exit(1);
  }
})().catch((error) => { console.error(error); process.exit(1); });
