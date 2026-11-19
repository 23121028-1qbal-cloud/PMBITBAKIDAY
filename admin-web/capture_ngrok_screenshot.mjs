import { chromium } from 'playwright';
import path from 'path';

const outDir = '/media/For_Laptop/Android_Development/AndroidStudioProjects/PMBITBAKIDAY/screenshots';
const ngrokUrl = 'https://stroller-dowry-expulsion.ngrok-free.dev';

async function run() {
  console.log('🚀 Launching Chromium with Playwright to test Ngrok HTTPS...');
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({
    viewport: { width: 1440, height: 900 },
    deviceScaleFactor: 2,
    extraHTTPHeaders: {
      'ngrok-skip-browser-warning': 'true',
    },
  });

  const page = await context.newPage();

  console.log(`🌐 Navigating to Ngrok URL: ${ngrokUrl}/login...`);
  await page.goto(`${ngrokUrl}/login`, { waitUntil: 'networkidle', timeout: 30000 });
  await page.waitForTimeout(2000);

  // If ngrok interstitial warning button appears, click it
  const visitButton = await page.$('button:has-text("Visit Site")');
  if (visitButton) {
    console.log('Clicking Ngrok interstitial warning button...');
    await visitButton.click();
    await page.waitForLoadState('networkidle');
    await page.waitForTimeout(2000);
  }

  console.log('📸 Capturing Ngrok HTTPS Login Page...');
  await page.screenshot({ path: path.join(outDir, '06-ngrok-https-login-page.png'), fullPage: true });

  console.log('🔑 Logging in via Ngrok domain...');
  await page.fill('#email', 'panitia@example.com');
  await page.fill('#password', 'password123');
  await page.click('button[type="submit"]');

  console.log('⏳ Waiting for Dashboard on Ngrok domain...');
  await page.waitForURL('**/dashboard', { timeout: 20000 });
  await page.waitForTimeout(3000);

  console.log('📸 Capturing Ngrok HTTPS Dashboard Page...');
  await page.screenshot({ path: path.join(outDir, '07-ngrok-https-dashboard.png'), fullPage: true });

  await browser.close();
  console.log('✨ Sukses! Screenshot Ngrok tersimpan di folder screenshots!');
}

run().catch((err) => {
  console.error('❌ Error:', err);
  process.exit(1);
});

