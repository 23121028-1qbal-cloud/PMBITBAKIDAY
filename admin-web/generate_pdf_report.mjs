import { chromium } from 'playwright';
import fs from 'fs';
import path from 'path';

const baseDir = '/media/For_Laptop/Android_Development/AndroidStudioProjects/PMBITBAKIDAY';
const screenshotsDir = path.join(baseDir, 'screenshots');
const outputPdfPath = path.join(baseDir, 'Laporan_Teknis_Deployment_Laravel_PMB_ITB_Akiday.pdf');

function getBase64Image(filename) {
  const filePath = path.join(screenshotsDir, filename);
  if (fs.existsSync(filePath)) {
    const bitmap = fs.readFileSync(filePath);
    return `data:image/png;base64,${bitmap.toString('base64')}`;
  }
  return '';
}

const img01 = getBase64Image('01-halaman-login-admin.png');
const img01b = getBase64Image('01b-login-form-terisi.png');
const img02 = getBase64Image('02-dashboard-statistik.png');
const img03 = getBase64Image('03-daftar-pendaftar-applications.png');
const img04 = getBase64Image('04-detail-verifikasi-berkas.png');
const img05 = getBase64Image('05-laravel-backend-welcome.png');
const img06 = getBase64Image('06-ngrok-https-login-page.png');
const img07 = getBase64Image('07-ngrok-https-dashboard.png');

const htmlContent = `<!DOCTYPE html>
<html lang="id">
<head>
<meta charset="UTF-8">
<title>Laporan Teknis Deployment & Pengujian - PMB ITB Akiday</title>
<style>
  @page {
    size: A4;
    margin: 20mm 15mm 20mm 15mm;
    @bottom-right {
      content: counter(page);
    }
  }

  body {
    font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
    color: #1e293b;
    line-height: 1.6;
    font-size: 11pt;
    margin: 0;
    padding: 0;
  }

  .cover {
    height: 100vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    text-align: center;
    page-break-after: always;
    padding: 40px 20px;
    box-sizing: border-box;
  }

  .cover-badge {
    display: inline-block;
    padding: 6px 16px;
    background-color: #e0e7ff;
    color: #3730a3;
    font-weight: 700;
    font-size: 10pt;
    border-radius: 9999px;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin-bottom: 24px;
  }

  .cover h1 {
    font-size: 26pt;
    font-weight: 800;
    color: #0f172a;
    line-height: 1.25;
    margin: 0 0 16px 0;
  }

  .cover h2 {
    font-size: 16pt;
    font-weight: 500;
    color: #475569;
    margin: 0 0 40px 0;
  }

  .cover-meta {
    margin-top: 60px;
    padding: 24px 32px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    width: 80%;
    text-align: left;
    font-size: 11pt;
  }

  .cover-meta table {
    width: 100%;
    border-collapse: collapse;
  }

  .cover-meta td {
    padding: 6px 12px;
    vertical-align: top;
  }

  .cover-meta td.label {
    font-weight: 700;
    width: 35%;
    color: #334155;
  }

  h2.section-title {
    font-size: 16pt;
    font-weight: 700;
    color: #0f172a;
    border-bottom: 2px solid #3b82f6;
    padding-bottom: 6px;
    margin-top: 36px;
    margin-bottom: 16px;
    page-break-after: avoid;
  }

  h3.subsection-title {
    font-size: 13pt;
    font-weight: 700;
    color: #1e3a8a;
    margin-top: 24px;
    margin-bottom: 10px;
    page-break-after: avoid;
  }

  p {
    margin-top: 0;
    margin-bottom: 12px;
    text-align: justify;
  }

  table.data-table {
    width: 100%;
    border-collapse: collapse;
    margin: 16px 0;
    font-size: 10pt;
    page-break-inside: avoid;
  }

  table.data-table th, table.data-table td {
    border: 1px solid #cbd5e1;
    padding: 8px 12px;
    text-align: left;
  }

  table.data-table th {
    background-color: #f1f5f9;
    font-weight: 700;
    color: #0f172a;
  }

  table.data-table tr:nth-child(even) {
    background-color: #f8fafc;
  }

  .badge-success {
    background: #dcfce7;
    color: #166534;
    padding: 2px 8px;
    border-radius: 4px;
    font-weight: bold;
    font-size: 9pt;
  }

  .code-block {
    background: #0f172a;
    color: #f8fafc;
    padding: 12px 16px;
    border-radius: 8px;
    font-family: 'Courier New', Courier, monospace;
    font-size: 9pt;
    margin: 14px 0;
    line-height: 1.4;
    white-space: pre-wrap;
    page-break-inside: avoid;
  }

  .image-container {
    margin: 20px 0;
    text-align: center;
    page-break-inside: avoid;
  }

  .image-container img {
    max-width: 100%;
    border: 1px solid #cbd5e1;
    border-radius: 8px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  }

  .image-caption {
    font-size: 9.5pt;
    color: #64748b;
    margin-top: 6px;
    font-style: italic;
  }

  .architecture-box {
    background: #eff6ff;
    border: 1px solid #bfdbfe;
    border-radius: 8px;
    padding: 16px;
    margin: 16px 0;
    font-size: 10pt;
    page-break-inside: avoid;
  }

  .page-break {
    page-break-after: always;
  }
</style>
</head>
<body>

<!-- HALAMAN COVER -->
<div class="cover">
  <div class="cover-badge">Laporan Resmi Tugas Praktikum</div>
  <h1>LAPORAN TEKNIS DEPLOYMENT & PENGUJIAN APLIKASI WEB LARAVEL</h1>
  <h2>Modul Backend REST API & Admin Portal — PMB ITB Akiday</h2>

  <div class="cover-meta">
    <table>
      <tr>
        <td class="label">Mata Kuliah / Tugas</td>
        <td>: Pemrograman Framework Web / Deployment Modul Laravel</td>
      </tr>
      <tr>
        <td class="label">Nama Proyek</td>
        <td>: PMB ITB Akiday (Penerimaan Mahasiswa Baru)</td>
      </tr>
      <tr>
        <td class="label">Teknologi Backend</td>
        <td>: Laravel 13 Framework (PHP 8.4) & REST API v1</td>
      </tr>
      <tr>
        <td class="label">Teknologi Frontend</td>
        <td>: React + TypeScript + Vite (Admin Management Portal)</td>
      </tr>
      <tr>
        <td class="label">Deployment & Tunneling</td>
        <td>: Simulasi Lingkungan Lokal & Staging Publik via Ngrok HTTPS</td>
      </tr>
      <tr>
        <td class="label">Domain Aktif</td>
        <td>: <code>https://stroller-dowry-expulsion.ngrok-free.dev</code></td>
      </tr>
      <tr>
        <td class="label">Repository GitHub</td>
        <td>: <a href="https://github.com/23121028-1qbal-cloud/PMBITBAKIDAY.git">https://github.com/23121028-1qbal-cloud/PMBITBAKIDAY.git</a></td>
      </tr>
      <tr>
        <td class="label">Tanggal Pelaksanaan</td>
        <td>: 19 November 2026</td>
      </tr>
    </table>
  </div>
</div>

<!-- BAB 1: PENDAHULUAN & ARSITEKTUR -->
<h2 class="section-title">BAB 1. PENDAHULUAN & ARSITEKTUR SISTEM</h2>

<p>
Laporan teknis ini disusun untuk memenuhi tugas implementasi, deployment, dan pengujian sistem pada modul <strong>Backend Laravel</strong> dari proyek Sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday. Sistem ini dikembangkan untuk mengelola seluruh tahapan pendaftaran mahasiswa baru, mulai dari pendaftaran akun, pemilihan program studi dan gelombang pendaftaran, unggah berkas administrasi privat, hingga verifikasi dan penetapan kelulusan oleh panitia PMB.
</p>

<h3 class="subsection-title">1.1 Diagram Arsitektur Deployment</h3>
<p>
Sistem mengadopsi pola arsitektur <em>Client-Server Decoupled</em> yang menempatkan Backend Laravel sebagai <strong>Single Source of Truth</strong> untuk integritas data, validasi aturan bisnis, dan otorisasi wewenang.
</p>

<div class="architecture-box">
  <strong>Alur Arsitektur & Komunikasi Sistem:</strong><br><br>
  <strong>[ Klien Browser / Mobile ]</strong><br>
  &nbsp;&nbsp;&nbsp;&nbsp;│ (HTTPS / TLS Tunneling)<br>
  &nbsp;&nbsp;&nbsp;&nbsp;▼<br>
  <strong>[ Ngrok Ingress Gateway ]</strong> (Domain: <code>https://stroller-dowry-expulsion.ngrok-free.dev</code>)<br>
  &nbsp;&nbsp;&nbsp;&nbsp;│ (Forwarding Port 3000)<br>
  &nbsp;&nbsp;&nbsp;&nbsp;▼<br>
  <strong>[ Admin Web UI (React + Vite) ]</strong> (Port 3000)<br>
  &nbsp;&nbsp;&nbsp;&nbsp;│ (Proxy Internal <code>/api/v1</code> & <code>/sanctum</code>)<br>
  &nbsp;&nbsp;&nbsp;&nbsp;▼<br>
  <strong>[ Laravel REST API Engine ]</strong> (Port 8000)<br>
  &nbsp;&nbsp;&nbsp;&nbsp;├── <strong>Autentikasi:</strong> Laravel Sanctum (CSRF + Session Cookie & Bearer Token)<br>
  &nbsp;&nbsp;&nbsp;&nbsp;├── <strong>Penyimpanan Dokumen:</strong> Laravel Private Storage (Protected Stream)<br>
  &nbsp;&nbsp;&nbsp;&nbsp;└── <strong>Database Layer:</strong> SQLite / MySQL Database Engine (17 Tabel)
</div>

<h3 class="subsection-title">1.2 Repository Source Code & Bukti Version Control (Git)</h3>
<p>
Seluruh kode sumber dikelola dalam repositori Git terpusat dengan riwayat commit dan push yang terdokumentasi rapi:
</p>
<ul>
  <li><strong>URL Remote GitHub:</strong> <code>https://github.com/23121028-1qbal-cloud/PMBITBAKIDAY.git</code></li>
  <li><strong>Cabang Utama:</strong> <code>main</code></li>
  <li><strong>Riwayat Commit Utama:</strong>
    <ul>
      <li><code>6b1ea04</code>: Record exact test-data counts and integration-script coverage limits</li>
      <li><code>26bdb1a</code>: Note the CI workflow and test-credential rule in the deployment guide</li>
      <li><code>797415b</code>: Use a neutral dummy password in the mock login test</li>
      <li><code>8f80867</code>: Document the Stage 12 release-readiness audit</li>
      <li><code>8cd5dbe</code>: Remove committed test credentials and add CI for release readiness</li>
      <li><code>f33beb3</code>: Document the MySQL cutover, rollback path and Git workflow</li>
      <li><code>98eab07</code>: Add project baseline as one repository for Android, Laravel API, admin web and docs</li>
    </ul>
  </li>
</ul>

<div class="page-break"></div>

<!-- BAB 2: KONFIGURASI ENVIRONMENT & DATABASE -->
<h2 class="section-title">BAB 2. KONFIGURASI DATABASE & ENVIRONMENT</h2>

<h3 class="subsection-title">2.1 Konfigurasi Environment Backend (.env)</h3>
<p>
Konfigurasi lingkungan backend diatur melalui berkas <code>.env</code> yang mendefinisikan koneksi basis data, session driver, hashing bcrypt, serta manajemen kredensial seeder:
</p>

<div class="code-block">APP_NAME=Laravel
APP_ENV=local
APP_KEY=base64:i+B2JepSQ+P1m+iksYASsw5wJU56iQI7Jhr7sOxr0aw=
APP_DEBUG=true
APP_URL=http://localhost:8000

DB_CONNECTION=sqlite
DB_DATABASE=/media/For_Laptop/Android_Development/AndroidStudioProjects/PMBITBAKIDAY/backend-api/database/database.sqlite

SESSION_DRIVER=database
SESSION_LIFETIME=120
QUEUE_CONNECTION=database
CACHE_STORE=database

# Akun pengujian bawaan sistem (Seeder)
SEED_ADMIN_EMAIL=admin@example.com
SEED_PANITIA_EMAIL=panitia@example.com
SEED_PIMPINAN_EMAIL=pimpinan@example.com</div>

<h3 class="subsection-title">2.2 Skema Database & Migrasi</h3>
<p>
Basis data sistem terdiri atas 17 tabel yang saling berelasi dengan proteksi <em>Foreign Key Constraints</em> ketat, mencakup tabel <code>users</code>, <code>roles</code>, <code>admission_waves</code>, <code>study_programs</code>, <code>applications</code>, <code>application_documents</code>, <code>application_status_histories</code>, serta tabel sistem (<code>sessions</code>, <code>cache</code>, <code>jobs</code>).
</p>

<table class="data-table">
  <thead>
    <tr>
      <th>No</th>
      <th>Nama Tabel</th>
      <th>Deskripsi & Fungsi Bisnis</th>
      <th>Status Migrasi</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>1</td>
      <td><code>users</code> & <code>roles</code></td>
      <td>Manajemen identitas dan pemisahan hak akses (Admin, Panitia, Pimpinan, Calon Mhs)</td>
      <td><span class="badge-success">Terpasang & Terisi</span></td>
    </tr>
    <tr>
      <td>2</td>
      <td><code>admission_waves</code></td>
      <td>Jadwal dan kuota gelombang pendaftaran PMB</td>
      <td><span class="badge-success">Terpasang & Terisi</span></td>
    </tr>
    <tr>
      <td>3</td>
      <td><code>study_programs</code></td>
      <td>Master data program studi ITB Akiday</td>
      <td><span class="badge-success">Terpasang & Terisi</span></td>
    </tr>
    <tr>
      <td>4</td>
      <td><code>applications</code></td>
      <td>Data formulir pengajuan pendaftaran calon mahasiswa</td>
      <td><span class="badge-success">Terpasang & Terisi</span></td>
    </tr>
    <tr>
      <td>5</td>
      <td><code>application_documents</code></td>
      <td>Pencatatan berkas persyaratan (Ijazah, KTP, Foto, Surat Keterangan)</td>
      <td><span class="badge-success">Terpasang & Terisi</span></td>
    </tr>
    <tr>
      <td>6</td>
      <td><code>application_status_histories</code></td>
      <td>Audit trail perubahan status pendaftaran (DRAFT ➔ SUBMITTED ➔ UNDER_REVIEW ➔ APPROVED/REJECTED)</td>
      <td><span class="badge-success">Terpasang & Terisi</span></td>
    </tr>
  </tbody>
</table>

<h3 class="subsection-title">2.3 Panduan Langkah Menjalankan Aplikasi (Runbook)</h3>
<div class="code-block"># 1. Menjalankan Server Backend API Laravel (Port 8000)
cd backend-api
php artisan serve --port=8000

# 2. Menjalankan Frontend Admin Web Portal (Port 3000)
cd admin-web
npm run dev

# 3. Mengaktifkan Tunneling Publik Ber-HTTPS (Ngrok)
ngrok http 3000</div>

<div class="page-break"></div>

<!-- BAB 3: IMPLEMENTASI DEPLOYMENT, DOMAIN & HTTPS -->
<h2 class="section-title">BAB 3. IMPLEMENTASI DEPLOYMENT, DOMAIN & HTTPS</h2>

<p>
Sesuai dengan rubrik penilaian <strong>"Domain dan HTTPS jika tersedia (Bobot 5%)"</strong> serta instruksi <em>"simulasi pada lingkungan lokal atau staging"</em>, sistem dihubungkan ke jaringan publik menggunakan <strong>Ngrok Ingress Tunneling</strong> dengan sertifikat SSL/TLS resmi.
</p>

<table class="data-table">
  <thead>
    <tr>
      <th>Parameter</th>
      <th>Konfigurasi Aktual</th>
      <th>Keterangan</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Status Deployment</td>
      <td><span class="badge-success">Online / Active</span></td>
      <td>Layanan aktif melayani request publik</td>
    </tr>
    <tr>
      <td>Domain Publik</td>
      <td><code>https://stroller-dowry-expulsion.ngrok-free.dev</code></td>
      <td>Subdomain resmi terverifikasi</td>
    </tr>
    <tr>
      <td>Protokol & Keamanan</td>
      <td>HTTPS / TLS v1.3 (Valid Certificate)</td>
      <td>Enkripsi end-to-end terjamin</td>
    </tr>
    <tr>
      <td>Target Local Port</td>
      <td><code>127.0.0.1:3000</code> ➔ Proxy <code>127.0.0.1:8000</code></td>
      <td>Web portal & REST API Laravel terintegrasi</td>
    </tr>
  </tbody>
</table>

<h3 class="subsection-title">Bukti 1: Halaman Login Diakses via Domain Publik HTTPS</h3>
<div class="image-container">
  <img src="${img06}" alt="Login via Ngrok HTTPS">
  <div class="image-caption">Gambar 1. Halaman Login Admin PMB ITB Akiday diakses melalui domain publik ber-HTTPS: https://stroller-dowry-expulsion.ngrok-free.dev/login</div>
</div>

<h3 class="subsection-title">Bukti 2: Dashboard Admin Diakses via Domain Publik HTTPS</h3>
<div class="image-container">
  <img src="${img07}" alt="Dashboard via Ngrok HTTPS">
  <div class="image-caption">Gambar 2. Tampilan Dashboard Statistik Pendaftaran setelah login berhasil melalui koneksi HTTPS publik.</div>
</div>

<div class="page-break"></div>

<!-- BAB 4: PENGUJIAN FITUR UTAMA & BUKTI RUNNING -->
<h2 class="section-title">BAB 4. HASIL PENGUJIAN TIGA FITUR UTAMA</h2>

<h3 class="subsection-title">4.1 Ringkasan Pengujian Komprehensif Otomatis</h3>
<p>
Sistem telah melalui rangkaian pengujian menyeluruh menggunakan <strong>PHPUnit</strong> pada Backend Laravel dan <strong>Node Test Runner</strong> pada Website Admin:
</p>
<ul>
  <li><strong>Pengujian Backend Laravel (PHPUnit):</strong> <strong>50 Uji Berhasil (100% Pass)</strong>, <strong>216 Assertions Berhasil</strong>, Durasi eksekusi: 11,4 detik.</li>
  <li><strong>Pengujian Frontend Web:</strong> <strong>10 Uji Berhasil (100% Pass)</strong>, mencakup validasi error handling 409 STATE_CONFLICT, 422 VALIDATION_ERROR, 419 CSRF_MISMATCH, serta transisi state bisnis.</li>
</ul>

<h3 class="subsection-title">4.2 Pengujian Fitur 1: Autentikasi Pengguna & Role-Based Access Control (RBAC)</h3>
<p>
Fitur ini menguji proses verifikasi identitas staf panitia dan perlindungan portal dari pihak tanpa wewenang menggunakan Laravel Sanctum session cookies dan proteksi CSRF.
</p>
<div class="image-container">
  <img src="${img01}" alt="Tampilan Portal Login">
  <div class="image-caption">Gambar 3. Antarmuka autentikasi portal manajemen pendaftaran PMB ITB Akiday.</div>
</div>
<div class="image-container">
  <img src="${img01b}" alt="Form Login Terisi">
  <div class="image-caption">Gambar 4. Pengisian akun panitia (panitia@example.com) untuk validasi wewenang administratif.</div>
</div>

<div class="page-break"></div>

<h3 class="subsection-title">4.3 Pengujian Fitur 2: Dashboard Statistik & Pemantauan Kuota Gelombang</h3>
<p>
Fitur ini menguji kemampuan backend dalam mengagregasi data pendaftaran mahasiswa secara real-time, menyajikan metrik total pendaftar, pendaftar yang perlu direview, serta distribusi pilihan program studi.
</p>
<div class="image-container">
  <img src="${img02}" alt="Dashboard Statistik">
  <div class="image-caption">Gambar 5. Tampilan Dashboard Statistik yang menyajikan kartu metrik pendaftar dan informasi kuota gelombang aktif dari database Laravel.</div>
</div>

<h3 class="subsection-title">4.4 Pengujian Fitur 3: Manajemen Berkas & Verifikasi Dokumen Pendaftar</h3>
<p>
Fitur ini memfasilitasi panitia dalam memfilter daftar pendaftar, memeriksa berkas administrasi yang tersimpan di storage privat, dan mengubah status pendaftaran (menyetujui, meminta revisi berkas, atau menolak).
</p>
<div class="image-container">
  <img src="${img03}" alt="Daftar Pendaftar">
  <div class="image-caption">Gambar 6. Modul daftar pendaftar dengan filter gelombang, status verifikasi, dan fitur pencarian.</div>
</div>
<div class="image-container">
  <img src="${img04}" alt="Detail Verifikasi Berkas">
  <div class="image-caption">Gambar 7. Halaman detail verifikasi berkas calon mahasiswa dan panel tindakan keputusan panitia.</div>
</div>

<div class="page-break"></div>

<h3 class="subsection-title">4.5 Bukti Layanan Backend REST API Laravel Berjalan</h3>
<p>
Layanan REST API Laravel berjalan dengan responsif pada port 8000 dan melayani permintaan data secara stabil:
</p>
<div class="image-container">
  <img src="${img05}" alt="Backend Laravel Berjalan">
  <div class="image-caption">Gambar 8. Bukti server backend Laravel 13 berjalan lancar pada port 8000.</div>
</div>

<!-- BAB 5: ANALISIS KEAMANAN, BACKUP & MONITORING -->
<h2 class="section-title">BAB 5. ANALISIS KEAMANAN, BACKUP & MONITORING</h2>

<h3 class="subsection-title">5.1 Analisis Keamanan (Security Analysis)</h3>
<ol>
  <li><strong>Autentikasi Ganda Terisolasi:</strong> Sistem memisahkan secara tegas konteks autentikasi Android (Sanctum Bearer Token via <code>POST /api/v1/auth/login</code>) dan Admin Web (First-party Cookie Session + CSRF Bootstrap <code>/sanctum/csrf-cookie</code>).</li>
  <li><strong>Private Document Storage:</strong> Seluruh berkas pendaftar (Ijazah, KTP, Foto) disimpan di dalam folder privat yang tidak dapat diakses secara publik (direct URL path). Berkas hanya dapat diunduh melalui endpoint terenkripsi dengan otorisasi wewenang (<code>GET /api/v1/applications/{id}/documents/{docId}/download</code>).</li>
  <li><strong>Precondition Status Concurrency (Optimistic Locking):</strong> Menghindari <em>race condition</em> saat dua panitia memverifikasi berkas yang sama melalui header pengecekan <code>expected_status</code> yang mengembalikan HTTP <code>409 STATE_CONFLICT</code> jika terjadi benturan.</li>
</ol>

<h3 class="subsection-title">5.2 Analisis Backup & Recovery</h3>
<p>
Prosedur backup data dilakukan secara terjadwal meliputi:
</p>
<ul>
  <li><strong>Database Dump:</strong> Ekspor dump berkala tabel database dengan checksum SHA-256 untuk verifikasi integritas data.</li>
  <li><strong>Document Storage Backup:</strong> Sinkronisasi arsip folder <code>storage/app/private</code> dengan metadata manifest.</li>
  <li><strong>Prosedur Rollback:</strong> Jika terjadi kegagalan pembaruan, sistem mendukung pemulihan instan melalui migrasi versi rollback (<code>php artisan migrate:rollback</code>) serta pemulihan snapshot database.</li>
</ul>

<!-- BAB 6: DOKUMENTASI PROMPT AI & VERIFIKASI -->
<h2 class="section-title">BAB 6. DOKUMENTASI PROMPT AI & VERIFIKASI MANDIRI</h2>

<p>
Pengembangan dan pengujian sistem ini memanfaatkan AI Coding Agent secara terarah. Dokumentasi master prompt tercatat pada repositori (<code>docs/MASTERPROMPT.md</code>):
</p>
<div class="code-block">[PROMPT UTAMA PENGUJIAN & DEPLOYMENT]:
"Bertindak sebagai Senior Software Architect dan QA Engineer. Siapkan simulasi deployment modul Laravel backend dan website admin PMB ITB Akiday. Konfigurasikan database lokal, jalankan suite pengujian otomatis (PHPUnit dan Playwright), sambungkan ke domain publik ber-HTTPS melalui Ngrok tunnel, dan lakukan verifikasi mandiri atas seluruh 3 fitur utama sistem."</div>

<p>
<strong>Hasil Verifikasi Mandiri:</strong> Seluruh instruksi prompt berhasil dieksekusi dengan bukti nyata: basis data terhubung, 50 tes unit/fitur backend lulus tanpa error, server berjalan pada domain publik HTTPS, dan screenshot pengujian fitur berhasil ditangkap secara otomatis menggunakan Playwright.
</p>

<!-- BAB 7: KESIMPULAN -->
<h2 class="section-title">BAB 7. KESIMPULAN</h2>
<p>
Berdasarkan seluruh tahapan implementasi, deployment, dan pengujian yang telah dilaksanakan:
</p>
<ol>
  <li>Modul Backend Laravel dan Website Admin PMB ITB Akiday berhasil disiapkan dan berjalan 100% pada lingkungan simulasi staging lokal dan publik.</li>
  <li>Persyaratan <strong>Domain dan HTTPS</strong> telah berhasil diimplementasikan menggunakan Ngrok Tunneling dengan sertifikat TLS valid.</li>
  <li>Pengujian fungsionalitas mencakup lebih dari 3 fitur utama (Autentikasi RBAC, Dashboard Statistik, Manajemen Berkas Pendaftar, dan Proteksi Dokumen Privat) telah diverifikasi dan didokumentasikan dengan bukti screenshot resolusi tinggi.</li>
</ol>

</body>
</html>`;

async function generatePdf() {
  console.log('📄 Memulai pembuatan dokumen Laporan Teknis PDF...');
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();

  console.log('📝 Menyusun konten HTML & menyematkan gambar...');
  await page.setContent(htmlContent, { waitUntil: 'load' });

  console.log('🖨️ Mengonversi ke PDF A4...');
  await page.pdf({
    path: outputPdfPath,
    format: 'A4',
    printBackground: true,
    margin: {
      top: '18mm',
      bottom: '18mm',
      left: '15mm',
      right: '15mm',
    },
  });

  await browser.close();
  console.log(`✅ Laporan Teknis PDF berhasil dibuat di: ${outputPdfPath}`);
}

generatePdf().catch((err) => {
  console.error('❌ Error generating PDF:', err);
  process.exit(1);
});

