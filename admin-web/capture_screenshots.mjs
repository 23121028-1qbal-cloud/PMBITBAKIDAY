import { chromium } from 'playwright';
import fs from 'fs';
import path from 'path';

const outDir = '/media/For_Laptop/Android_Development/AndroidStudioProjects/PMBITBAKIDAY/screenshots';

async function run() {
  console.log('🚀 Launching Chromium with Playwright...');
  const browser = await chromium.launch({
    headless: true,
  });

  const context = await browser.newContext({
    viewport: { width: 1440, height: 900 },
    deviceScaleFactor: 2, // High resolution
  });

  const page = await context.newPage();

  // 1. Login Page
  console.log('📸 1. Capturing Login Page...');
  await page.goto('http://127.0.0.1:3000/login', { waitUntil: 'networkidle' });
  await page.screenshot({ path: path.join(outDir, '01-halaman-login-admin.png'), fullPage: true });

  // 2. Perform Login as Panitia
  console.log('🔑 Performing login...');
  await page.fill('#email', 'panitia@example.com');
  await page.fill('#password', 'password123');
  await page.screenshot({ path: path.join(outDir, '01b-login-form-terisi.png') });
  await page.click('button[type="submit"]');

  // Wait for navigation to dashboard
  console.log('⏳ Waiting for Dashboard...');
  await page.waitForURL('**/dashboard', { timeout: 15000 });
  await page.waitForTimeout(2000); // Wait for stats to load

  // 2. Dashboard Page
  console.log('📸 2. Capturing Dashboard...');
  await page.screenshot({ path: path.join(outDir, '02-dashboard-statistik.png'), fullPage: true });

  // 3. Applications List Page
  console.log('📸 3. Capturing Applications List...');
  await page.goto('http://127.0.0.1:3000/applications', { waitUntil: 'networkidle' });
  await page.waitForTimeout(2000);
  await page.screenshot({ path: path.join(outDir, '03-daftar-pendaftar-applications.png'), fullPage: true });

  // 4. Detail Application Page
  console.log('📸 4. Capturing Application Detail...');
  // Let's find an application link or directly go to /applications/1
  const detailLink = await page.$('a[href^="/applications/"]');
  if (detailLink) {
    await detailLink.click();
    await page.waitForTimeout(2000);
  } else {
    await page.goto('http://127.0.0.1:3000/applications/1', { waitUntil: 'networkidle' });
    await page.waitForTimeout(2000);
  }
  await page.screenshot({ path: path.join(outDir, '04-detail-verifikasi-berkas.png'), fullPage: true });

  // 5. Backend Laravel Welcome / Root Endpoint
  console.log('📸 5. Capturing Backend Laravel Welcome Page...');
  await page.goto('http://127.0.0.1:8000', { waitUntil: 'networkidle' });
  await page.screenshot({ path: path.join(outDir, '05-laravel-backend-welcome.png'), fullPage: true });

  await browser.close();
  console.log('✨ Selesai! Semua screenshot berhasil disimpan di:', outDir);
}

run().catch((err) => {
  console.error('❌ Error capturing screenshots:', err);
  process.exit(1);
});
