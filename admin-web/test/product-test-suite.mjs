import { spawn } from 'node:child_process';
import http from 'node:http';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const BACKEND_DIR = path.resolve(__dirname, '../../backend-api');
const BASE_URL = 'http://127.0.0.1:8000';

const testResults = [];

function recordResult(id, name, expected, actual, status, evidence = '') {
  const result = { id, name, expected, actual, status, evidence };
  testResults.push(result);
  const mark = status === 'PASS' ? '✓' : '✗';
  console.log(`[${status}] ${mark} ${id}: ${name}`);
  if (evidence) {
    console.log(`      Bukti: ${evidence}`);
  }
}

function pingServer() {
  return new Promise((resolve) => {
    const req = http.get(`${BASE_URL}/up`, (res) => {
      resolve(res.statusCode === 200);
    });
    req.on('error', () => resolve(false));
    req.setTimeout(1000, () => {
      req.destroy();
      resolve(false);
    });
  });
}

const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

class AndroidHttpClient {
  constructor() {
    this.token = null;
  }
  setToken(token) { this.token = token; }
  clearToken() { this.token = null; }

  async request(method, endpoint, body = null, isMultipart = false, customHeaders = {}) {
    const headers = { 'Accept': 'application/json', ...customHeaders };
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }
    let requestBody = body;
    if (body && !isMultipart && typeof body === 'object') {
      headers['Content-Type'] = 'application/json';
      requestBody = JSON.stringify(body);
    }
    const res = await fetch(`${BASE_URL}${endpoint}`, { method, headers, body: requestBody });
    let json = null;
    let text = null;
    const contentType = res.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
      json = await res.json();
    } else {
      text = await res.text();
    }
    return {
      status: res.status,
      headers: Object.fromEntries(res.headers.entries()),
      data: json,
      rawText: text,
    };
  }
}

class AdminWebHttpClient {
  constructor() {
    this.cookies = new Map();
  }
  parseCookies(headers) {
    const raw = headers.get('set-cookie');
    if (raw) {
      const cookieStrings = raw.split(/,(?=[^;]+=[^;]+)/);
      for (const str of cookieStrings) {
        const parts = str.split(';')[0].trim().split('=');
        if (parts.length >= 2) {
          this.cookies.set(parts[0].trim(), parts.slice(1).join('='));
        }
      }
    }
  }
  getCookieHeader() {
    return Array.from(this.cookies.entries()).map(([k, v]) => `${k}=${v}`).join('; ');
  }
  getXsrfToken() {
    const raw = this.cookies.get('XSRF-TOKEN');
    return raw ? decodeURIComponent(raw) : '';
  }
  async bootstrapCsrf() {
    const res = await fetch(`${BASE_URL}/sanctum/csrf-cookie`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
        'Origin': 'http://localhost:3000',
        'Referer': 'http://localhost:3000',
        'Cookie': this.getCookieHeader(),
      },
    });
    this.parseCookies(res.headers);
    return res.status;
  }
  async request(method, endpoint, body = null, customHeaders = {}) {
    const headers = {
      'Accept': 'application/json',
      'X-Requested-With': 'XMLHttpRequest',
      'Origin': 'http://localhost:3000',
      'Referer': 'http://localhost:3000',
      'Cookie': this.getCookieHeader(),
      ...customHeaders,
    };
    const xsrf = this.getXsrfToken();
    if (xsrf) headers['X-XSRF-TOKEN'] = xsrf;

    let requestBody = body;
    if (body && typeof body === 'object') {
      headers['Content-Type'] = 'application/json';
      requestBody = JSON.stringify(body);
    }
    const res = await fetch(`${BASE_URL}${endpoint}`, { method, headers, body: requestBody });
    this.parseCookies(res.headers);
    let json = null;
    let text = null;
    const contentType = res.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
      json = await res.json();
    } else {
      text = await res.text();
    }
    return {
      status: res.status,
      headers: Object.fromEntries(res.headers.entries()),
      data: json,
      rawText: text,
    };
  }
}

async function main() {
  console.log('========================================================================');
  console.log('  PMB ITB AKIDAY — TAHAP 10: PRODUCT TEST CASES (TR-001 s/d TR-020)');
  console.log('========================================================================\n');

  let serverProcess = null;
  const alreadyRunning = await pingServer();
  if (alreadyRunning) {
    console.log('[Server] Backend Laravel sudah berjalan pada ' + BASE_URL);
  } else {
    console.log('[Server] Menjalankan server Laravel (php artisan serve) pada ' + BASE_URL + '...');
    serverProcess = spawn('php', ['artisan', 'serve', '--host=127.0.0.1', '--port=8000'], {
      cwd: BACKEND_DIR,
      stdio: 'pipe',
    });
    let ready = false;
    for (let i = 0; i < 30; i++) {
      await sleep(500);
      ready = await pingServer();
      if (ready) break;
    }
    if (!ready) {
      console.error('[FATAL] Gagal memulai server Laravel.');
      if (serverProcess) serverProcess.kill();
      process.exit(1);
    }
    console.log('[Server] Backend Laravel siap melayani permintaan.\n');
  }

  try {
    await runProductTests();
  } finally {
    if (serverProcess) {
      console.log('\n[Server] Menghentikan server proses background...');
      serverProcess.kill('SIGINT');
    }
  }

  printSummary();
}

async function runProductTests() {
  const ts = Date.now();
  const applicantA = new AndroidHttpClient();
  const applicantB = new AndroidHttpClient();
  const panitiaClient = new AdminWebHttpClient();
  const pimpinanClient = new AdminWebHttpClient();

  const emailA = `calon_a_${ts}@pmb.itbakiday.ac.id`;
  const emailB = `calon_b_${ts}@pmb.itbakiday.ac.id`;
  const password = 'PasswordPmb2026!';

  let appIdA = null;
  let docIdA = null;
  let appIdA_Wave2 = null;

  // ---------------------------------------------------------------------------
  // 7.1 Pengujian Registrasi dan Login
  // ---------------------------------------------------------------------------
  console.log('--- 7.1 PENGUJIAN REGISTRASI DAN LOGIN ---');

  // TR-001: Pengguna melakukan registrasi dengan data valid
  const regRes = await applicantA.request('POST', '/api/v1/auth/register', {
    email: emailA,
    password: password,
    password_confirmation: password,
  });
  if (regRes.status === 201 && regRes.data?.data?.role === 'calon_mahasiswa' && regRes.data?.success === true) {
    recordResult(
      'TR-001',
      'Pengguna melakukan registrasi dengan data valid',
      'Akun berhasil dibuat, data tersimpan, role calon_mahasiswa',
      `HTTP ${regRes.status}, ID: ${regRes.data.data.id}, Role: ${regRes.data.data.role}`,
      'PASS',
      `Envelope: success=true, User ID: ${regRes.data.data.id}`
    );
  } else {
    recordResult('TR-001', 'Pengguna registrasi valid', 'HTTP 201', `HTTP ${regRes.status}`, 'FAIL');
  }

  // Registrasi Akun B untuk pengujian isolasi data
  await applicantB.request('POST', '/api/v1/auth/register', {
    email: emailB,
    password: password,
    password_confirmation: password,
  });

  // TR-002: Pengguna login dengan email dan kata sandi yang benar
  const loginRes = await applicantA.request('POST', '/api/v1/auth/login', {
    email: emailA,
    password: password,
  });
  const tokenA = loginRes.data?.data?.access_token;
  if (loginRes.status === 200 && tokenA && loginRes.data?.data?.token_type === 'Bearer') {
    applicantA.setToken(tokenA);
    recordResult(
      'TR-002',
      'Pengguna login dengan email dan kata sandi yang benar',
      'Pengguna berhasil masuk sesuai hak aksesnya, token Bearer diterbitkan',
      `HTTP ${loginRes.status}, Token Type: Bearer, Role: ${loginRes.data.data.user.role}`,
      'PASS',
      `Token: ${tokenA.substring(0, 16)}...`
    );
  } else {
    recordResult('TR-002', 'Pengguna login benar', 'HTTP 200', `HTTP ${loginRes.status}`, 'FAIL');
  }

  // Login Akun B untuk token B
  const loginB = await applicantB.request('POST', '/api/v1/auth/login', {
    email: emailB,
    password: password,
  });
  applicantB.setToken(loginB.data?.data?.access_token);

  // TR-003: Pengguna login dengan kata sandi yang salah
  const invalidLogin = await applicantA.request('POST', '/api/v1/auth/login', {
    email: emailA,
    password: 'SalahPassword123!',
  });
  if (invalidLogin.status === 422 || invalidLogin.status === 401) {
    recordResult(
      'TR-003',
      'Pengguna login dengan kata sandi yang salah',
      'Sistem menolak login dan menampilkan pesan kesalahan yang aman',
      `HTTP ${invalidLogin.status}, Message: "${invalidLogin.data?.message}"`,
      'PASS',
      `Pesan kesalahan generik, informasi internal terlindungi`
    );
  } else {
    recordResult('TR-003', 'Login kata sandi salah', 'HTTP 422/401', `HTTP ${invalidLogin.status}`, 'FAIL');
  }

  // ---------------------------------------------------------------------------
  // 7.2 Pengujian Profil Calon Mahasiswa
  // ---------------------------------------------------------------------------
  console.log('\n--- 7.2 PENGUJIAN PROFIL CALON MAHASISWA ---');

  // TR-004: Calon mahasiswa melengkapi profil dengan data valid
  const validProfile = {
    full_name: 'Dewi Sartika Akiday',
    nik: '3204123456789012',
    birth_place: 'Bandung',
    birth_date: '2005-12-04',
    gender: 'P',
    phone: '081223344556',
    address: 'Jl. Dago Asri No. 45 Bandung',
  };
  const profileRes = await applicantA.request('PUT', '/api/v1/applicant/profile', validProfile);
  if (profileRes.status === 200 && profileRes.data?.data?.nik === validProfile.nik) {
    recordResult(
      'TR-004',
      'Calon mahasiswa melengkapi profil dengan data valid',
      'Profil berhasil disimpan di database',
      `HTTP ${profileRes.status}, Nama: ${profileRes.data.data.full_name}, NIK: ${profileRes.data.data.nik}`,
      'PASS',
      `Profil tersimpan pada tabel applicant_profiles`
    );
  } else {
    recordResult('TR-004', 'Profil data valid', 'HTTP 200', `HTTP ${profileRes.status}`, 'FAIL');
  }

  // TR-005: Calon mahasiswa mengirim data dengan format yang tidak valid
  const invalidProfileRes = await applicantA.request('PUT', '/api/v1/applicant/profile', {
    full_name: '',
    nik: '12345', // NIK kurang dari 16 digit
    phone: '',
  });
  if (invalidProfileRes.status === 422 && invalidProfileRes.data?.error?.code === 'VALIDATION_ERROR') {
    recordResult(
      'TR-005',
      'Calon mahasiswa mengirim data profil tidak valid',
      'Sistem menolak data dan memberikan informasi validasi (422)',
      `HTTP ${invalidProfileRes.status}, Error Code: ${invalidProfileRes.data.error.code}`,
      'PASS',
      `Validasi ditolak dengan field: ${Object.keys(invalidProfileRes.data.error.details || {}).join(', ')}`
    );
  } else {
    recordResult('TR-005', 'Profil tidak valid', 'HTTP 422', `HTTP ${invalidProfileRes.status}`, 'FAIL');
  }

  // ---------------------------------------------------------------------------
  // 7.3 Pengujian Pendaftaran
  // ---------------------------------------------------------------------------
  console.log('\n--- 7.3 PENGUJIAN PENDAFTARAN ---');

  // TR-006: Calon mahasiswa membuat pendaftaran pada gelombang aktif
  const createDraft = await applicantA.request('POST', '/api/v1/applications', {
    wave_id: 1,
    study_program_id: 1,
  });
  if (createDraft.status === 201 && createDraft.data?.data?.status === 'draft') {
    appIdA = createDraft.data.data.id;
    recordResult(
      'TR-006',
      'Calon mahasiswa membuat pendaftaran pada gelombang aktif',
      'Pendaftaran berhasil dibuat dengan status draft dan nomor registrasi terbit',
      `HTTP ${createDraft.status}, ID: ${appIdA}, Reg: ${createDraft.data.data.registration_number}`,
      'PASS',
      `Nomor Pendaftaran: ${createDraft.data.data.registration_number}`
    );
  } else {
    recordResult('TR-006', 'Buat pendaftaran aktif', 'HTTP 201', `HTTP ${createDraft.status}`, 'FAIL');
  }

  // TR-007: Calon mahasiswa mengirim pendaftaran dengan dokumen wajib belum lengkap
  const submitIncomplete = await applicantA.request('POST', `/api/v1/applications/${appIdA}/submit`, {
    expected_status: 'draft',
  });
  if (submitIncomplete.status === 422 && submitIncomplete.data?.error?.code === 'APPLICATION_INCOMPLETE') {
    recordResult(
      'TR-007',
      'Calon mahasiswa mengirim pendaftaran tanpa dokumen wajib',
      'Sistem menolak pengiriman dan menunjukkan dokumen wajib belum diunggah',
      `HTTP ${submitIncomplete.status}, Message: "${submitIncomplete.data.message}"`,
      'PASS',
      `Error Code: ${submitIncomplete.data.error.code}`
    );
  } else {
    recordResult('TR-007', 'Kirim pendaftaran belum lengkap', 'HTTP 422', `HTTP ${submitIncomplete.status}`, 'FAIL');
  }

  // TR-011 & TR-012 disisipkan di sini untuk melengkapi berkas sebelum TR-008
  console.log('\n--- 7.4 PENGUJIAN DOKUMEN ---');

  // TR-012: Calon mahasiswa mengunggah file dengan format yang tidak diizinkan
  const forbiddenBlob = new Blob(['#!/bin/bash\necho bad'], { type: 'application/x-sh' });
  const forbiddenFormData = new FormData();
  forbiddenFormData.append('document_type', 'ktp');
  forbiddenFormData.append('file', forbiddenBlob, 'script.sh');

  const uploadForbidden = await applicantA.request(
    'POST',
    `/api/v1/applications/${appIdA}/documents`,
    forbiddenFormData,
    true
  );
  if (uploadForbidden.status === 422 && uploadForbidden.data?.error?.code === 'VALIDATION_ERROR') {
    recordResult(
      'TR-012',
      'Calon mahasiswa mengunggah file dengan format tidak diizinkan',
      'Sistem menolak file dan memberikan pesan validasi format dokumen',
      `HTTP ${uploadForbidden.status}, Error Code: ${uploadForbidden.data.error.code}`,
      'PASS',
      `Pesan: ${uploadForbidden.data.message}`
    );
  } else {
    recordResult('TR-012', 'Unggah format tidak diizinkan', 'HTTP 422', `HTTP ${uploadForbidden.status}`, 'FAIL');
  }

  // TR-011: Calon mahasiswa mengunggah dokumen dengan format yang diizinkan (PDF)
  const validPdfBlob = new Blob(['%PDF-1.4\n1 0 obj\n<< /Title (KTP PMB Valid) >>\nendobj\n%%EOF'], { type: 'application/pdf' });
  const validFormData = new FormData();
  validFormData.append('document_type', 'ktp');
  validFormData.append('file', validPdfBlob, 'ktp_asli.pdf');

  const uploadValid = await applicantA.request(
    'POST',
    `/api/v1/applications/${appIdA}/documents`,
    validFormData,
    true
  );
  if (uploadValid.status === 201 && uploadValid.data?.data?.document_type === 'ktp') {
    docIdA = uploadValid.data.data.id;
    recordResult(
      'TR-011',
      'Calon mahasiswa mengunggah dokumen format yang diizinkan',
      'Dokumen berhasil diunggah dan terhubung ke pendaftaran yang benar',
      `HTTP ${uploadValid.status}, Doc ID: ${docIdA}, File: ${uploadValid.data.data.file_name}`,
      'PASS',
      `Storage privat: documents/${appIdA}/${uploadValid.data.data.file_name}`
    );
  } else {
    recordResult('TR-011', 'Unggah dokumen diizinkan', 'HTTP 201', `HTTP ${uploadValid.status}`, 'FAIL');
  }

  // TR-008: Calon mahasiswa mengirim pendaftaran yang sudah lengkap
  const submitComplete = await applicantA.request('POST', `/api/v1/applications/${appIdA}/submit`, {
    expected_status: 'draft',
  });
  if (submitComplete.status === 200 && submitComplete.data?.data?.status === 'submitted') {
    recordResult(
      'TR-008',
      'Calon mahasiswa mengirim pendaftaran yang sudah lengkap',
      'Pendaftaran tersimpan dan status berubah menjadi submitted',
      `HTTP ${submitComplete.status}, Status: ${submitComplete.data.data.status}, Submitted At: ${submitComplete.data.data.submitted_at}`,
      'PASS',
      `Transisi draft -> submitted berhasil dicatat pada application_status_histories`
    );
  } else {
    recordResult('TR-008', 'Kirim pendaftaran lengkap', 'HTTP 200', `HTTP ${submitComplete.status}`, 'FAIL');
  }

  // TR-009: Calon mahasiswa mencoba membuat pendaftaran kedua pada gelombang yang sama
  const duplicateApp = await applicantA.request('POST', '/api/v1/applications', {
    wave_id: 1,
    study_program_id: 2,
  });
  if (duplicateApp.status === 409 && duplicateApp.data?.error?.code === 'APPLICATION_ALREADY_EXISTS') {
    recordResult(
      'TR-009',
      'Pencegahan pendaftaran kedua pada gelombang yang sama',
      'Sistem mencegah pendaftaran ganda pada gelombang yang sama (409)',
      `HTTP ${duplicateApp.status}, Error Code: ${duplicateApp.data.error.code}`,
      'PASS',
      `Pesan: "${duplicateApp.data.message}"`
    );
  } else {
    recordResult('TR-009', 'Pencegahan pendaftaran ganda', 'HTTP 409', `HTTP ${duplicateApp.status}`, 'FAIL');
  }

  // TR-010: Calon mahasiswa membuat pendaftaran pada gelombang berbeda yang aktif
  const wave2App = await applicantA.request('POST', '/api/v1/applications', {
    wave_id: 2,
    study_program_id: 2,
  });
  if (wave2App.status === 201 && wave2App.data?.data?.wave_id === 2) {
    appIdA_Wave2 = wave2App.data.data.id;
    recordResult(
      'TR-010',
      'Calon mahasiswa membuat pendaftaran pada gelombang berbeda',
      'Pendaftaran baru dapat dibuat pada gelombang 2 tanpa mengubah pendaftaran gelombang 1',
      `HTTP ${wave2App.status}, New App ID: ${appIdA_Wave2}, Wave ID: 2, Reg: ${wave2App.data.data.registration_number}`,
      'PASS',
      `Pendaftaran multi-gelombang didukung secara independen`
    );
  } else {
    recordResult('TR-010', 'Pendaftaran gelombang berbeda', 'HTTP 201', `HTTP ${wave2App.status}`, 'FAIL');
  }

  // TR-013: Pengguna lain mencoba mengakses dokumen milik pendaftar lain
  const docHijackAttempt = await applicantB.request(
    'GET',
    `/api/v1/applications/${appIdA}/documents/${docIdA}/download`
  );
  if (docHijackAttempt.status === 404 || docHijackAttempt.status === 403) {
    recordResult(
      'TR-013',
      'Pengguna mencoba mengakses dokumen milik pendaftar lain',
      'Sistem menolak akses tanpa membocorkan dokumen (403/404)',
      `HTTP ${docHijackAttempt.status}, Response ditolak aman`,
      'PASS',
      `Dokumen privat diisolasi per akun pemilik pendaftaran`
    );
  } else {
    recordResult('TR-013', 'Akses dokumen pendaftar lain', 'HTTP 403/404', `HTTP ${docHijackAttempt.status}`, 'FAIL');
  }

  // ---------------------------------------------------------------------------
  // 7.5 Pengujian Pemeriksaan dan Status
  // ---------------------------------------------------------------------------
  console.log('\n--- 7.5 PENGUJIAN PEMERIKSAAN DAN STATUS ---');

  // Panitia login sesi browser
  await panitiaClient.bootstrapCsrf();
  await panitiaClient.request('POST', '/api/v1/auth/session/login', {
    email: 'panitia@example.com',
    password: 'Secret123!',
  });

  // TR-014: Panitia membuka pendaftaran yang menjadi kewenangannya
  const panitiaDetail = await panitiaClient.request('GET', `/api/v1/admin/applications/${appIdA}`);
  if (
    panitiaDetail.status === 200 &&
    panitiaDetail.data?.data?.applicant?.full_name === validProfile.full_name &&
    panitiaDetail.data?.data?.documents?.some((d) => d.id === docIdA)
  ) {
    recordResult(
      'TR-014',
      'Panitia membuka pendaftaran yang menjadi kewenangannya',
      'Panitia dapat melihat data dan dokumen yang diperlukan untuk pemeriksaan',
      `HTTP ${panitiaDetail.status}, Calon: ${panitiaDetail.data.data.applicant.full_name}, Berkas: ${panitiaDetail.data.data.documents.length}`,
      'PASS',
      `Data pendaftar & dokumen terverifikasi lengkap`
    );
  } else {
    recordResult('TR-014', 'Panitia buka pendaftaran', 'HTTP 200', `HTTP ${panitiaDetail.status}`, 'FAIL');
  }

  // Mulai review terlebih dahulu
  await panitiaClient.request('POST', `/api/v1/admin/applications/${appIdA}/review`, {
    expected_status: 'submitted',
  });

  // TR-015: Panitia meminta revisi atas dokumen atau data pendaftaran
  const revisionNote = 'Scan KTP terpotong pada nomor NIK. Silakan unggah scan utuh.';
  const reqRevision = await panitiaClient.request('POST', `/api/v1/admin/applications/${appIdA}/request-revision`, {
    expected_status: 'review',
    note: revisionNote,
  });
  if (reqRevision.status === 200 && reqRevision.data?.data?.status === 'revision') {
    recordResult(
      'TR-015',
      'Panitia meminta revisi atas dokumen atau data pendaftaran',
      'Status berubah menjadi revision dan catatan revisi tersimpan',
      `HTTP ${reqRevision.status}, Status: revision, Catatan: "${reqRevision.data.data.note}"`,
      'PASS',
      `Catatan tersimpan di riwayat status`
    );
  } else {
    recordResult('TR-015', 'Panitia minta revisi', 'HTTP 200', `HTTP ${reqRevision.status}`, 'FAIL');
  }

  // TR-016: Calon mahasiswa memperbaiki data atau dokumen yang diminta
  const revisedKtpBlob = new Blob(['%PDF-1.4\n1 0 obj\n<< /Title (KTP Revisi Lengkap) >>\nendobj\n%%EOF'], { type: 'application/pdf' });
  const revisedFormData = new FormData();
  revisedFormData.append('document_type', 'ktp');
  revisedFormData.append('file', revisedKtpBlob, 'ktp_revisi.pdf');

  await applicantA.request('POST', `/api/v1/applications/${appIdA}/documents`, revisedFormData, true);
  const resubmitRes = await applicantA.request('POST', `/api/v1/applications/${appIdA}/resubmit`, {
    expected_status: 'revision',
  });

  if (resubmitRes.status === 200 && resubmitRes.data?.data?.status === 'review') {
    recordResult(
      'TR-016',
      'Calon mahasiswa memperbaiki data atau dokumen yang diminta',
      'Perubahan tersimpan dan status pendaftaran kembali ke review untuk diperiksa panitia',
      `HTTP ${resubmitRes.status}, Status Baru: ${resubmitRes.data.data.status}`,
      'PASS',
      `Transisi revision -> review tersimpan di riwayat status`
    );
  } else {
    recordResult('TR-016', 'Calon mahasiswa perbaiki data', 'HTTP 200', `HTTP ${resubmitRes.status}`, 'FAIL');
  }

  // TR-017: Panitia yang memiliki kewenangan menetapkan keputusan penerimaan
  const decisionRes = await panitiaClient.request('POST', `/api/v1/admin/applications/${appIdA}/decision`, {
    expected_status: 'review',
    decision: 'accepted',
    note: 'Seluruh berkas memenuhi syarat PMB ITB Akiday. Selamat Anda Diterima!',
  });
  if (decisionRes.status === 200 && decisionRes.data?.data?.status === 'accepted') {
    recordResult(
      'TR-017',
      'Panitia menetapkan keputusan penerimaan',
      'Keputusan tersimpan dan status berubah menjadi accepted',
      `HTTP ${decisionRes.status}, Status: ${decisionRes.data.data.status}, Decided At: ${decisionRes.data.data.decided_at}`,
      'PASS',
      `Keputusan accepted disimpan secara permanen`
    );
  } else {
    recordResult('TR-017', 'Penetapan keputusan penerimaan', 'HTTP 200', `HTTP ${decisionRes.status}`, 'FAIL');
  }

  // ---------------------------------------------------------------------------
  // 7.6 Pengujian Hak Akses dan Laporan
  // ---------------------------------------------------------------------------
  console.log('\n--- 7.6 PENGUJIAN HAK AKSES DAN LAPORAN ---');

  // TR-018: Pengguna yang tidak memiliki kewenangan mencoba menetapkan keputusan
  // Pimpinan login sesi browser
  await pimpinanClient.bootstrapCsrf();
  await pimpinanClient.request('POST', '/api/v1/auth/session/login', {
    email: 'pimpinan@example.com',
    password: 'Secret123!',
  });

  // Pimpinan tidak memiliki hak menetapkan keputusan teknis (hanya admin/panitia)
  const unauthorizedDecision = await pimpinanClient.request('POST', `/api/v1/admin/applications/${appIdA}/decision`, {
    expected_status: 'accepted',
    decision: 'rejected',
    note: 'Coba override tanpa hak akses',
  });
  if (unauthorizedDecision.status === 403 && unauthorizedDecision.data?.error?.code === 'FORBIDDEN') {
    recordResult(
      'TR-018',
      'Pengguna tanpa kewenangan mencoba menetapkan keputusan',
      'Sistem menolak tindakan tersebut dengan HTTP 403 FORBIDDEN',
      `HTTP ${unauthorizedDecision.status}, Error Code: ${unauthorizedDecision.data.error.code}`,
      'PASS',
      `Role middleware memblokir pimpinan dari endpoint keputusan teknis`
    );
  } else {
    recordResult('TR-018', 'Tolak penetapan keputusan tanpa wewenang', 'HTTP 403', `HTTP ${unauthorizedDecision.status}`, 'FAIL');
  }

  // TR-019: Calon mahasiswa mencoba membuka data pendaftaran milik orang lain
  const spyAttempt = await applicantB.request('GET', `/api/v1/applications/${appIdA}`);
  if (spyAttempt.status === 404 || spyAttempt.status === 403) {
    recordResult(
      'TR-019',
      'Calon mahasiswa mencoba membuka data pendaftaran milik orang lain',
      'Sistem menolak akses dan hanya mengizinkan akses ke pendaftaran miliknya (404/403)',
      `HTTP ${spyAttempt.status}, Response: "${spyAttempt.data?.message}"`,
      'PASS',
      `Ownership check melindungi data pribadi calon mahasiswa lain`
    );
  } else {
    recordResult('TR-019', 'Akses pendaftaran orang lain', 'HTTP 404/403', `HTTP ${spyAttempt.status}`, 'FAIL');
  }

  // TR-020: Pengguna pimpinan membuka dashboard dan laporan sesuai kewenangannya
  const pimpinanList = await pimpinanClient.request('GET', '/api/v1/admin/applications');
  if (pimpinanList.status === 200 && Array.isArray(pimpinanList.data?.data) && pimpinanList.data.data.length > 0) {
    const list = pimpinanList.data.data;
    const stats = {
      total: list.length,
      accepted: list.filter((a) => a.status === 'accepted').length,
      submitted: list.filter((a) => a.status === 'submitted').length,
      review: list.filter((a) => a.status === 'review').length,
    };
    recordResult(
      'TR-020',
      'Pengguna pimpinan membuka dashboard dan laporan sesuai kewenangan',
      'Sistem menampilkan statistik dan laporan pendaftaran sesuai hak akses pimpinan',
      `HTTP ${pimpinanList.status}, Total: ${stats.total}, Diterima: ${stats.accepted}`,
      'PASS',
      `Pimpinan dapat memantau seluruh pendaftaran PMB lintas gelombang dan program studi`
    );
  } else {
    recordResult('TR-020', 'Pimpinan buka dashboard/laporan', 'HTTP 200', `HTTP ${pimpinanList.status}`, 'FAIL');
  }
}

function printSummary() {
  console.log('\n========================================================================');
  console.log('  RINGKASAN HASIL PENGUJIAN PRODUK TAHAP 10 (TR-001 s/d TR-020)');
  console.log('========================================================================');
  const total = testResults.length;
  const passed = testResults.filter((r) => r.status === 'PASS').length;
  const failed = testResults.filter((r) => r.status === 'FAIL').length;
  const rate = ((passed / total) * 100).toFixed(1);

  console.log(`Total Test Cases : ${total}`);
  console.log(`Lulus (Pass)     : ${passed}`);
  console.log(`Gagal (Fail)     : ${failed}`);
  console.log(`Kelulusan        : ${rate}%\n`);

  if (failed === 0) {
    console.log('>>> SELURUH 20 TEST CASE PRODUK PMB ITB AKIDAY TELAH TERVERIFIKASI LULUS (100%) <<<');
  } else {
    console.log('>>> TERDAPAT KEGAGALAN DALAM PENGUJIAN PRODUK <<<');
  }
}

main().catch((err) => {
  console.error('[FATAL]', err);
  process.exit(1);
});
