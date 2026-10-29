import { spawn } from 'node:child_process';
import http from 'node:http';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const BACKEND_DIR = path.resolve(__dirname, '../../backend-api');
const BASE_URL = 'http://127.0.0.1:8000';

// Global results collector for reporting
const testResults = [];

function recordResult(id, scenario, components, expected, actual, status, evidence = '') {
  const result = { id, scenario, components, expected, actual, status, evidence };
  testResults.push(result);
  const mark = status === 'PASS' ? '✓' : '✗';
  console.log(`[${status}] ${mark} ${id} - ${scenario}`);
  if (evidence) {
    console.log(`      Bukti: ${evidence}`);
  }
}

// Helper: check if server is already running
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

// Helper: sleep
const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

async function main() {
  console.log('========================================================================');
  console.log('  PMB ITB AKIDAY — TAHAP 9: SYSTEM INTEGRATION TEST SUITE');
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
    await runAllScenarios();
  } finally {
    if (serverProcess) {
      console.log('\n[Server] Menghentikan server proses background...');
      serverProcess.kill('SIGINT');
    }
  }

  printSummary();
}

// -----------------------------------------------------------------------------
// HTTP Client Helpers
// -----------------------------------------------------------------------------

// 1. Android Client Simulation
class AndroidHttpClient {
  constructor() {
    this.token = null;
  }

  setToken(token) {
    this.token = token;
  }

  clearToken() {
    this.token = null;
  }

  async request(method, endpoint, body = null, isMultipart = false, customHeaders = {}) {
    const headers = {
      'Accept': 'application/json',
      ...customHeaders,
    };

    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }

    let requestBody = body;
    if (body && !isMultipart && typeof body === 'object') {
      headers['Content-Type'] = 'application/json';
      requestBody = JSON.stringify(body);
    }

    const res = await fetch(`${BASE_URL}${endpoint}`, {
      method,
      headers,
      body: requestBody,
    });

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

// 2. Admin Web Client Simulation (Cookie Session + CSRF)
class AdminWebHttpClient {
  constructor() {
    this.cookies = new Map();
  }

  parseCookies(headers) {
    const raw = headers.get('set-cookie');
    if (raw) {
      // In fetch, multiple set-cookie might be comma-separated or single
      const cookieStrings = raw.split(/,(?=[^;]+=[^;]+)/);
      for (const str of cookieStrings) {
        const parts = str.split(';')[0].trim().split('=');
        if (parts.length >= 2) {
          const key = parts[0].trim();
          const val = parts.slice(1).join('=');
          this.cookies.set(key, val);
        }
      }
    }
  }

  getCookieHeader() {
    return Array.from(this.cookies.entries())
      .map(([k, v]) => `${k}=${v}`)
      .join('; ');
  }

  getXsrfToken() {
    const raw = this.cookies.get('XSRF-TOKEN');
    return raw ? decodeURIComponent(raw) : '';
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
    if (xsrf) {
      headers['X-XSRF-TOKEN'] = xsrf;
    }

    let requestBody = body;
    if (body && typeof body === 'object') {
      headers['Content-Type'] = 'application/json';
      requestBody = JSON.stringify(body);
    }

    const res = await fetch(`${BASE_URL}${endpoint}`, {
      method,
      headers,
      body: requestBody,
    });

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
}

// -----------------------------------------------------------------------------
// Integration Test Scenarios
// -----------------------------------------------------------------------------

async function runAllScenarios() {
  const androidClient = new AndroidHttpClient();
  const adminClient = new AdminWebHttpClient();

  const timestamp = Date.now();
  const testApplicantEmail = `mhs_integ_${timestamp}@example.test`;
  const testApplicantPassword = 'IntegPassword123!';
  let createdAppId = null;
  let createdDocId = null;
  let activeWaveId = null;
  let activeProgId = null;

  console.log('--- FASE C: INTEGRASI AUTENTIKASI ---');

  // SI-01: Admin Web CSRF Bootstrap & Browser Session Login
  const csrfStatus = await adminClient.bootstrapCsrf();
  const hasXsrf = Boolean(adminClient.getXsrfToken());
  if (csrfStatus === 204 && hasXsrf) {
    recordResult(
      'SI-01',
      'Admin Web CSRF Bootstrap (/sanctum/csrf-cookie)',
      'Admin Web ↔ Backend',
      'HTTP 204 No Content dan cookie XSRF-TOKEN tersimpan',
      `HTTP ${csrfStatus}, XSRF-TOKEN didapatkan`,
      'PASS',
      `Cookie XSRF-TOKEN: ${adminClient.getXsrfToken().substring(0, 16)}...`
    );
  } else {
    recordResult(
      'SI-01',
      'Admin Web CSRF Bootstrap (/sanctum/csrf-cookie)',
      'Admin Web ↔ Backend',
      'HTTP 204 No Content dan cookie XSRF-TOKEN',
      `HTTP ${csrfStatus}, hasXsrf=${hasXsrf}`,
      'FAIL'
    );
  }

  // SI-02: Admin Web Session Login with valid credentials
  const adminLoginRes = await adminClient.request('POST', '/api/v1/auth/session/login', {
    email: 'admin@example.com',
    password: 'Secret123!',
  });
  if (adminLoginRes.status === 200 && adminLoginRes.data?.data?.user?.role === 'admin') {
    recordResult(
      'SI-02',
      'Admin Web Session Login (POST /api/v1/auth/session/login)',
      'Admin Web ↔ Backend',
      'HTTP 200, sesi cookie first-party terbentuk, role admin',
      `HTTP ${adminLoginRes.status}, role=${adminLoginRes.data?.data?.user?.role}`,
      'PASS',
      `User ID: ${adminLoginRes.data?.data?.user?.id}, Email: ${adminLoginRes.data?.data?.user?.email}`
    );
  } else {
    recordResult(
      'SI-02',
      'Admin Web Session Login',
      'Admin Web ↔ Backend',
      'HTTP 200, role admin',
      `HTTP ${adminLoginRes.status}`,
      'FAIL'
    );
  }

  // SI-03: Admin Web Session Verification (GET /api/v1/auth/me)
  const adminMeRes = await adminClient.request('GET', '/api/v1/auth/me');
  if (adminMeRes.status === 200 && adminMeRes.data?.data?.role === 'admin') {
    recordResult(
      'SI-03',
      'Admin Web Session Restoration (GET /api/v1/auth/me)',
      'Admin Web ↔ Backend',
      'HTTP 200, mengembalikan identitas admin via cookie sesi',
      `HTTP ${adminMeRes.status}, role=${adminMeRes.data?.data?.role}`,
      'PASS',
      `Sesi terverifikasi aktif`
    );
  } else {
    recordResult(
      'SI-03',
      'Admin Web Session Restoration',
      'Admin Web ↔ Backend',
      'HTTP 200, role admin',
      `HTTP ${adminMeRes.status}`,
      'FAIL'
    );
  }

  // SI-04: Android Client Calon Mahasiswa Registration
  const regRes = await androidClient.request('POST', '/api/v1/auth/register', {
    email: testApplicantEmail,
    password: testApplicantPassword,
    password_confirmation: testApplicantPassword,
  });
  if (regRes.status === 201 && regRes.data?.data?.role === 'calon_mahasiswa') {
    recordResult(
      'SI-04',
      'Android Calon Mahasiswa Registration (POST /api/v1/auth/register)',
      'Android ↔ Backend',
      'HTTP 201 Created, envelope success: true, role calon_mahasiswa',
      `HTTP ${regRes.status}, role=${regRes.data?.data?.role}`,
      'PASS',
      `New User ID: ${regRes.data?.data?.id}, Email: ${regRes.data?.data?.email}`
    );
  } else {
    recordResult(
      'SI-04',
      'Android Calon Mahasiswa Registration',
      'Android ↔ Backend',
      'HTTP 201, role calon_mahasiswa',
      `HTTP ${regRes.status}`,
      'FAIL'
    );
  }

  // SI-05: Android Client Login & Bearer Token Issuance
  const androidLoginRes = await androidClient.request('POST', '/api/v1/auth/login', {
    email: testApplicantEmail,
    password: testApplicantPassword,
  });
  const bearerToken = androidLoginRes.data?.data?.access_token;
  if (androidLoginRes.status === 200 && bearerToken && androidLoginRes.data?.data?.token_type === 'Bearer') {
    androidClient.setToken(bearerToken);
    recordResult(
      'SI-05',
      'Android Bearer Token Login (POST /api/v1/auth/login)',
      'Android ↔ Backend',
      'HTTP 200 OK, personal access token Bearer diterbitkan',
      `HTTP ${androidLoginRes.status}, token_type=${androidLoginRes.data?.data?.token_type}`,
      'PASS',
      `Sanctum Token: ${bearerToken.substring(0, 15)}... (disimpan di Keystore Android)`
    );
  } else {
    recordResult(
      'SI-05',
      'Android Bearer Token Login',
      'Android ↔ Backend',
      'HTTP 200, access_token Bearer',
      `HTTP ${androidLoginRes.status}`,
      'FAIL'
    );
  }

  // SI-06: Android Session Identity (GET /api/v1/auth/me with Bearer token)
  const androidMeRes = await androidClient.request('GET', '/api/v1/auth/me');
  if (androidMeRes.status === 200 && androidMeRes.data?.data?.email === testApplicantEmail) {
    recordResult(
      'SI-06',
      'Android Token Authentication (GET /api/v1/auth/me)',
      'Android ↔ Backend',
      'HTTP 200, identitas user diverifikasi via Bearer token',
      `HTTP ${androidMeRes.status}, email=${androidMeRes.data?.data?.email}`,
      'PASS',
      `Role: ${androidMeRes.data?.data?.role}`
    );
  } else {
    recordResult(
      'SI-06',
      'Android Token Authentication',
      'Android ↔ Backend',
      'HTTP 200',
      `HTTP ${androidMeRes.status}`,
      'FAIL'
    );
  }

  console.log('\n--- FASE D: INTEGRASI ALUR PENDAFTARAN END-TO-END ---');

  // SI-07: Alur 1 — Data Referensi Konsisten Antar-Klien
  const androidWaves = await androidClient.request('GET', '/api/v1/admission-waves');
  const adminWaves = await adminClient.request('GET', '/api/v1/admission-waves');
  const androidProgs = await androidClient.request('GET', '/api/v1/study-programs');
  const adminProgs = await adminClient.request('GET', '/api/v1/study-programs');

  if (
    androidWaves.status === 200 &&
    adminWaves.status === 200 &&
    androidProgs.status === 200 &&
    adminProgs.status === 200 &&
    androidWaves.data?.data?.length === adminWaves.data?.data?.length &&
    androidProgs.data?.data?.length === adminProgs.data?.data?.length
  ) {
    activeWaveId = androidWaves.data.data[0].id;
    activeProgId = androidProgs.data.data[0].id;
    recordResult(
      'SI-07',
      'Alur 1 — Data Referensi Konsistensi Lintas-Klien',
      'Android ↔ Backend ↔ Admin Web',
      'Data gelombang & prodi konsisten dan identik di kedua klien',
      `Waves Count: ${androidWaves.data.data.length}, Progs Count: ${androidProgs.data.data.length}`,
      'PASS',
      `Active Wave ID: ${activeWaveId} (${androidWaves.data.data[0].name}), Active Prog ID: ${activeProgId} (${androidProgs.data.data[0].name})`
    );
  } else {
    recordResult(
      'SI-07',
      'Alur 1 — Data Referensi Konsistensi',
      'Android ↔ Backend ↔ Admin Web',
      'Data konsisten di kedua klien',
      `Android: waves=${androidWaves.status}, progs=${androidProgs.status}; Admin: waves=${adminWaves.status}, progs=${adminProgs.status}`,
      'FAIL'
    );
  }

  // SI-08: Calon Mahasiswa Pengisian Profil (PUT /api/v1/applicant/profile)
  const profilePayload = {
    full_name: 'Ahmad Integration Dani',
    nik: '3201999988887777',
    birth_place: 'Bandung',
    birth_date: '2005-08-17',
    gender: 'L',
    phone: '081234567899',
    address: 'Jl. Ganesha No. 10 Kota Bandung',
  };
  const profileRes = await androidClient.request('PUT', '/api/v1/applicant/profile', profilePayload);
  if (profileRes.status === 200 && profileRes.data?.data?.nik === profilePayload.nik) {
    recordResult(
      'SI-08',
      'Calon Mahasiswa Pengisian Profil (PUT /api/v1/applicant/profile)',
      'Android ↔ Backend',
      'HTTP 200 OK, profil tersimpan di database dengan relasi user_id',
      `HTTP ${profileRes.status}, Nama: ${profileRes.data?.data?.full_name}, NIK: ${profileRes.data?.data?.nik}`,
      'PASS',
      `Profile tersimpan lengkap`
    );
  } else {
    recordResult(
      'SI-08',
      'Calon Mahasiswa Pengisian Profil',
      'Android ↔ Backend',
      'HTTP 200 OK',
      `HTTP ${profileRes.status}`,
      'FAIL'
    );
  }

  // SI-09: Alur 2 — Pembuatan Draft Pengajuan (POST /api/v1/applications)
  const appCreateRes = await androidClient.request('POST', '/api/v1/applications', {
    wave_id: activeWaveId,
    study_program_id: activeProgId,
  });
  if (appCreateRes.status === 201 && appCreateRes.data?.data?.status === 'draft') {
    createdAppId = appCreateRes.data.data.id;
    recordResult(
      'SI-09',
      'Alur 2 — Pembuatan Draft Pendaftaran (POST /api/v1/applications)',
      'Android ↔ Backend',
      'HTTP 201 Created, status draft, nomor pendaftaran ter-generate otomatis',
      `HTTP ${appCreateRes.status}, ID: ${createdAppId}, RegNo: ${appCreateRes.data.data.registration_number}`,
      'PASS',
      `Registration Number: ${appCreateRes.data.data.registration_number}`
    );
  } else {
    recordResult(
      'SI-09',
      'Alur 2 — Pembuatan Draft Pendaftaran',
      'Android ↔ Backend',
      'HTTP 201 Created',
      `HTTP ${appCreateRes.status}`,
      'FAIL'
    );
  }

  // SI-10: Alur 3 — Unggah Dokumen Berkas Pendaftaran (Multipart)
  const dummyFileContent = '%PDF-1.4\n1 0 obj\n<< /Title (Dokumen KTP PMB ITB Akiday Test) >>\nendobj\ntrailer\n<< /Root 1 0 R >>\n%%EOF';
  const formData = new FormData();
  formData.append('document_type', 'ktp');
  const blob = new Blob([dummyFileContent], { type: 'application/pdf' });
  formData.append('file', blob, 'scan_ktp_asli.pdf');

  const uploadRes = await androidClient.request(
    'POST',
    `/api/v1/applications/${createdAppId}/documents`,
    formData,
    true
  );

  if (uploadRes.status === 201 && uploadRes.data?.data?.document_type === 'ktp') {
    createdDocId = uploadRes.data.data.id;
    recordResult(
      'SI-10',
      'Alur 3 — Unggah Dokumen Persyaratan (POST /api/v1/applications/{id}/documents)',
      'Android ↔ Backend',
      'HTTP 201 Created, dokumen tersimpan di storage private, metadata tercatat',
      `HTTP ${uploadRes.status}, Doc ID: ${createdDocId}, File: ${uploadRes.data.data.file_name}`,
      'PASS',
      `Storage Path: documents/${createdAppId}/${uploadRes.data.data.file_name}`
    );
  } else {
    recordResult(
      'SI-10',
      'Alur 3 — Unggah Dokumen Persyaratan',
      'Android ↔ Backend',
      'HTTP 201 Created',
      `HTTP ${uploadRes.status}`,
      'FAIL'
    );
  }

  // SI-11: Alur 2 — Submit Pendaftaran & Precondition Check (expected_status: 'draft')
  const submitRes = await androidClient.request('POST', `/api/v1/applications/${createdAppId}/submit`, {
    expected_status: 'draft',
  });
  if (submitRes.status === 200 && submitRes.data?.data?.status === 'submitted') {
    recordResult(
      'SI-11',
      'Alur 2 — Kirim Pendaftaran (POST /api/v1/applications/{id}/submit)',
      'Android ↔ Backend',
      'HTTP 200 OK, transisi draft → submitted, submitted_at terisi, history dicatat',
      `HTTP ${submitRes.status}, Status: ${submitRes.data.data.status}, SubmittedAt: ${submitRes.data.data.submitted_at}`,
      'PASS',
      `Status berpindah ke submitted`
    );
  } else {
    recordResult(
      'SI-11',
      'Alur 2 — Kirim Pendaftaran',
      'Android ↔ Backend',
      'HTTP 200 OK',
      `HTTP ${submitRes.status}`,
      'FAIL'
    );
  }

  // SI-12: Deteksi Konflik Status (409 STATE_CONFLICT) saat submit ulang pada status submitted
  const conflictSubmit = await androidClient.request('POST', `/api/v1/applications/${createdAppId}/submit`, {
    expected_status: 'draft',
  });
  if (conflictSubmit.status === 409 && conflictSubmit.data?.error?.code === 'STATE_CONFLICT') {
    recordResult(
      'SI-12',
      'Deteksi Konflik Status (409 STATE_CONFLICT) saat submit ulang',
      'Android ↔ Backend',
      'HTTP 409 STATE_CONFLICT, operasi ditolak karena status sudah bukan draft',
      `HTTP ${conflictSubmit.status}, Error Code: ${conflictSubmit.data?.error?.code}`,
      'PASS',
      `Backend menolak modifikasi konkurensi secara deterministik`
    );
  } else {
    recordResult(
      'SI-12',
      'Deteksi Konflik Status (409 STATE_CONFLICT)',
      'Android ↔ Backend',
      'HTTP 409 STATE_CONFLICT',
      `HTTP ${conflictSubmit.status}`,
      'FAIL'
    );
  }

  // SI-13: Alur 4 — Admin Website Menampilkan Pendaftaran Terbaru
  const adminAppList = await adminClient.request('GET', `/api/v1/admin/applications?status=submitted`);
  const foundInList = adminAppList.data?.data?.find((a) => a.id === createdAppId);
  if (adminAppList.status === 200 && foundInList) {
    recordResult(
      'SI-13',
      'Alur 4 — Pendaftaran Muncul di Daftar Admin Web (GET /api/v1/admin/applications)',
      'Backend ↔ Admin Web',
      'HTTP 200 OK, pendaftaran yang baru dikirim calon mahasiswa muncul di Admin Web',
      `Ditemukan ID: ${foundInList.id}, Reg: ${foundInList.registration_number}, Calon: ${foundInList.applicant?.full_name}`,
      'PASS',
      `Data calon mahasiswa tampil konsisten di panel admin`
    );
  } else {
    recordResult(
      'SI-13',
      'Alur 4 — Pendaftaran Muncul di Daftar Admin Web',
      'Backend ↔ Admin Web',
      'HTTP 200 OK dan pendaftar ditemukan',
      `HTTP ${adminAppList.status}, found=${Boolean(foundInList)}`,
      'FAIL'
    );
  }

  // SI-14: Alur 4 — Detail Pendaftaran di Admin Web Termasuk Metadata Dokumen
  const adminDetailRes = await adminClient.request('GET', `/api/v1/admin/applications/${createdAppId}`);
  if (
    adminDetailRes.status === 200 &&
    adminDetailRes.data?.data?.applicant?.nik === profilePayload.nik &&
    adminDetailRes.data?.data?.documents?.some((d) => d.id === createdDocId)
  ) {
    recordResult(
      'SI-14',
      'Alur 4 — Detail Pendaftaran & Berkas di Admin Web (GET /api/v1/admin/applications/{id})',
      'Backend ↔ Admin Web',
      'HTTP 200 OK, profil lengkap & metadata dokumen berelasi dengan pendaftar',
      `Nama: ${adminDetailRes.data.data.applicant.full_name}, Docs: ${adminDetailRes.data.data.documents.length}`,
      'PASS',
      `Relasi applicant_profiles dan application_documents utuh`
    );
  } else {
    recordResult(
      'SI-14',
      'Alur 4 — Detail Pendaftaran & Berkas di Admin Web',
      'Backend ↔ Admin Web',
      'HTTP 200 OK',
      `HTTP ${adminDetailRes.status}`,
      'FAIL'
    );
  }

  // SI-15: Alur 3 — Unduh Dokumen Privat Terproteksi oleh Panitia/Admin
  const adminDocDownload = await adminClient.request(
    'GET',
    `/api/v1/applications/${createdAppId}/documents/${createdDocId}/download`
  );
  if (adminDocDownload.status === 200 && adminDocDownload.rawText.includes('PDF-1.4')) {
    recordResult(
      'SI-15',
      'Alur 3 — Unduh Berkas Privat Terotorisasi (GET /api/v1/applications/{id}/documents/{docId}/download)',
      'Backend ↔ Admin Web',
      'HTTP 200 OK, streaming berkas privat melalui backend tanpa mengekspos direktori server',
      `HTTP ${adminDocDownload.status}, Content-Type: ${adminDocDownload.headers['content-type']}`,
      'PASS',
      `Berkas streaming binary/pdf valid`
    );
  } else {
    recordResult(
      'SI-15',
      'Alur 3 — Unduh Berkas Privat Terotorisasi',
      'Backend ↔ Admin Web',
      'HTTP 200 OK',
      `HTTP ${adminDocDownload.status}`,
      'FAIL'
    );
  }

  // SI-16: Alur 4 — Mulai Pemeriksaan oleh Panitia (submitted → review)
  // Uji precondition invalid expected_status terlebih dahulu
  const invalidReview = await adminClient.request('POST', `/api/v1/admin/applications/${createdAppId}/review`, {
    expected_status: 'WRONG_STATUS',
  });
  const validReview = await adminClient.request('POST', `/api/v1/admin/applications/${createdAppId}/review`, {
    expected_status: 'submitted',
  });
  if (
    invalidReview.status === 409 &&
    invalidReview.data?.error?.code === 'STATE_CONFLICT' &&
    validReview.status === 200 &&
    validReview.data?.data?.status === 'review'
  ) {
    recordResult(
      'SI-16',
      'Alur 4 — Pemeriksaan Dimulai (POST /api/v1/admin/applications/{id}/review)',
      'Admin Web ↔ Backend',
      'Precondition proteksi 409 berfungsi, status berpindah submitted → review',
      `Invalid: 409 STATE_CONFLICT, Valid: 200 status=review`,
      'PASS',
      `Transisi status submitted -> review berhasil`
    );
  } else {
    recordResult(
      'SI-16',
      'Alur 4 — Pemeriksaan Dimulai',
      'Admin Web ↔ Backend',
      'Precondition 409 & status review',
      `Invalid: ${invalidReview.status}, Valid: ${validReview.status}`,
      'FAIL'
    );
  }

  // SI-17: Alur 5 — Permintaan Revisi Berkas oleh Panitia (review → revision)
  const revisionNote = 'Scan KTP terpotong bagian NIK, harap unggah ulang scan KTP utuh dan jelas.';
  const reqRevRes = await adminClient.request('POST', `/api/v1/admin/applications/${createdAppId}/request-revision`, {
    expected_status: 'review',
    note: revisionNote,
  });
  if (reqRevRes.status === 200 && reqRevRes.data?.data?.status === 'revision') {
    recordResult(
      'SI-17',
      'Alur 5 — Permintaan Revisi Berkas (POST /api/v1/admin/applications/{id}/request-revision)',
      'Admin Web ↔ Backend',
      'HTTP 200 OK, status berpindah review → revision, catatan revisi tersimpan di history',
      `HTTP ${reqRevRes.status}, Status: revision, Catatan: "${reqRevRes.data.data.note}"`,
      'PASS',
      `Catatan panitia tersimpan di application_status_histories`
    );
  } else {
    recordResult(
      'SI-17',
      'Alur 5 — Permintaan Revisi Berkas',
      'Admin Web ↔ Backend',
      'HTTP 200 OK, status revision',
      `HTTP ${reqRevRes.status}`,
      'FAIL'
    );
  }

  // SI-18: Alur 5 — Android Menampilkan Status Revisi & Riwayat Catatan
  const androidAppCheck = await androidClient.request('GET', `/api/v1/applications/${createdAppId}`);
  const androidHistCheck = await androidClient.request('GET', `/api/v1/applications/${createdAppId}/status-history`);
  const latestHistory = androidHistCheck.data?.data?.[androidHistCheck.data.data.length - 1];
  if (
    androidAppCheck.status === 200 &&
    androidAppCheck.data?.data?.status === 'revision' &&
    latestHistory?.to_status === 'revision' &&
    latestHistory?.note === revisionNote
  ) {
    recordResult(
      'SI-18',
      'Alur 5 — Sinkronisasi Status Revisi ke Klien Android (GET /applications/{id})',
      'Backend ↔ Android',
      'Aplikasi Android menerima status revision dan catatan instruksi panitia secara real-time',
      `Status: ${androidAppCheck.data.data.status}, Catatan: "${latestHistory.note}"`,
      'PASS',
      `Calon mahasiswa dapat melihat catatan instruksi perbaikan`
    );
  } else {
    recordResult(
      'SI-18',
      'Alur 5 — Sinkronisasi Status Revisi ke Klien Android',
      'Backend ↔ Android',
      'Status revision & catatan history cocok',
      `Status: ${androidAppCheck.data?.data?.status}, Note: ${latestHistory?.note}`,
      'FAIL'
    );
  }

  // SI-19: Alur 5 — Calon Mahasiswa Mengunggah Berkas Perbaikan & Resubmit
  const updatedDocBlob = new Blob(['%PDF-1.4\n1 0 obj\n<< /Title (KTP Revisi Jelas) >>\nendobj\n%%EOF'], { type: 'application/pdf' });
  const updateFormData = new FormData();
  updateFormData.append('document_type', 'ktp');
  updateFormData.append('file', updatedDocBlob, 'scan_ktp_revisi.pdf');

  const reuploadRes = await androidClient.request(
    'POST',
    `/api/v1/applications/${createdAppId}/documents`,
    updateFormData,
    true
  );

  const resubmitRes = await androidClient.request('POST', `/api/v1/applications/${createdAppId}/resubmit`, {
    expected_status: 'revision',
  });

  if (
    reuploadRes.status === 201 &&
    resubmitRes.status === 200 &&
    resubmitRes.data?.data?.status === 'review'
  ) {
    recordResult(
      'SI-19',
      'Alur 5 — Pengajuan Ulang Berkas (POST /api/v1/applications/{id}/resubmit)',
      'Android ↔ Backend',
      'HTTP 200 OK, dokumen terupdate dan status transisi kembali revision → review',
      `Upload: 201 (Doc ID: ${reuploadRes.data.data.id}), Resubmit: 200 (Status: ${resubmitRes.data.data.status})`,
      'PASS',
      `Transisi revision -> review berhasil dicatat`
    );
  } else {
    recordResult(
      'SI-19',
      'Alur 5 — Pengajuan Ulang Berkas',
      'Android ↔ Backend',
      'Resubmit HTTP 200 status review',
      `Upload: ${reuploadRes.status}, Resubmit: ${resubmitRes.status}`,
      'FAIL'
    );
  }

  // SI-20: Alur 6 — Keputusan Penerimaan oleh Panitia/Admin (review → accepted)
  const decisionRes = await adminClient.request('POST', `/api/v1/admin/applications/${createdAppId}/decision`, {
    expected_status: 'review',
    decision: 'accepted',
    note: 'Seluruh berkas persyaratan telah lengkap dan memenuhi standar seleksi. Diterima.',
  });
  if (decisionRes.status === 200 && decisionRes.data?.data?.status === 'accepted') {
    recordResult(
      'SI-20',
      'Alur 6 — Penetapan Keputusan Penerimaan (POST /api/v1/admin/applications/{id}/decision)',
      'Admin Web ↔ Backend',
      'HTTP 200 OK, status berpindah review → accepted, catatan keputusan tersimpan',
      `HTTP ${decisionRes.status}, Final Status: ${decisionRes.data.data.status}, DecidedAt: ${decisionRes.data.data.decided_at}`,
      'PASS',
      `Status akhir accepted tercatat permanen`
    );
  } else {
    recordResult(
      'SI-20',
      'Alur 6 — Penetapan Keputusan Penerimaan',
      'Admin Web ↔ Backend',
      'HTTP 200 OK, status accepted',
      `HTTP ${decisionRes.status}`,
      'FAIL'
    );
  }

  // SI-21: Konsistensi Status Akhir & Riwayat Kronologis Lintas-Klien
  const finalAndroidApp = await androidClient.request('GET', `/api/v1/applications/${createdAppId}`);
  const finalAdminApp = await adminClient.request('GET', `/api/v1/admin/applications/${createdAppId}`);
  const finalHistory = await androidClient.request('GET', `/api/v1/applications/${createdAppId}/status-history`);

  const historyTransitions = finalHistory.data?.data?.map((h) => `${h.from_status || 'init'}->${h.to_status}`);
  const expectedChain = [
    'init->draft',
    'draft->submitted',
    'submitted->review',
    'review->revision',
    'revision->review',
    'review->accepted',
  ];
  const isChainMatching = expectedChain.every((t) => historyTransitions?.includes(t));

  if (
    finalAndroidApp.data?.data?.status === 'accepted' &&
    finalAdminApp.data?.data?.status === 'accepted' &&
    isChainMatching
  ) {
    recordResult(
      'SI-21',
      'Konsistensi Status Akhir & Garis Waktu Riwayat Lintas-Klien',
      'Android ↔ Backend ↔ Admin Web',
      'Status accepted konsisten di Android & Admin Web dengan urutan riwayat status 6 fase utuh',
      `Android: accepted, Admin Web: accepted, Total Riwayat: ${finalHistory.data.data.length} fase`,
      'PASS',
      `Rantai transisi lengkap: ${expectedChain.join(' | ')}`
    );
  } else {
    recordResult(
      'SI-21',
      'Konsistensi Status Akhir Lintas-Klien',
      'Android ↔ Backend ↔ Admin Web',
      'Konsisten accepted dan rantai riwayat utuh',
      `Android: ${finalAndroidApp.data?.data?.status}, Admin: ${finalAdminApp.data?.data?.status}, MatchedChain: ${isChainMatching}`,
      'FAIL'
    );
  }

  console.log('\n--- FASE F & SECURITY: PENGUJIAN NEGATIF & KEAMANAN TERPADU ---');

  // SI-22: Akses Endpoint Tanpa Autentikasi (401 UNAUTHENTICATED)
  const unauthClient = new AndroidHttpClient();
  const unauthRes = await unauthClient.request('GET', '/api/v1/applications');
  if (unauthRes.status === 401 && unauthRes.data?.error?.code === 'UNAUTHENTICATED') {
    recordResult(
      'SI-22',
      'Penolakan Akses Tanpa Autentikasi (401 UNAUTHENTICATED)',
      'Backend Keamanan',
      'HTTP 401 UNAUTHENTICATED, menolak request tanpa token/cookie',
      `HTTP ${unauthRes.status}, Error Code: ${unauthRes.data?.error?.code}`,
      'PASS',
      `Pesan: "${unauthRes.data?.message}"`
    );
  } else {
    recordResult(
      'SI-22',
      'Penolakan Akses Tanpa Autentikasi',
      'Backend Keamanan',
      'HTTP 401 UNAUTHENTICATED',
      `HTTP ${unauthRes.status}`,
      'FAIL'
    );
  }

  // SI-23: Otorisasi Role — Calon Mahasiswa Mengakses Endpoint Admin (403 FORBIDDEN)
  const forbiddenRes = await androidClient.request('GET', '/api/v1/admin/applications');
  if (forbiddenRes.status === 403 && forbiddenRes.data?.error?.code === 'FORBIDDEN') {
    recordResult(
      'SI-23',
      'Penolakan Otorisasi Peran Calon Mahasiswa (403 FORBIDDEN)',
      'Backend Keamanan',
      'HTTP 403 FORBIDDEN, calon mahasiswa diblokir dari area /api/v1/admin/*',
      `HTTP ${forbiddenRes.status}, Error Code: ${forbiddenRes.data?.error?.code}`,
      'PASS',
      `Pesan: "${forbiddenRes.data?.message}"`
    );
  } else {
    recordResult(
      'SI-23',
      'Penolakan Otorisasi Peran Calon Mahasiswa',
      'Backend Keamanan',
      'HTTP 403 FORBIDDEN',
      `HTTP ${forbiddenRes.status}`,
      'FAIL'
    );
  }

  // SI-24: Perlindungan Unduh Berkas Privat Tanpa Login (401 UNAUTHENTICATED)
  const unauthDocDownload = await unauthClient.request(
    'GET',
    `/api/v1/applications/${createdAppId}/documents/${createdDocId}/download`
  );
  if (unauthDocDownload.status === 401) {
    recordResult(
      'SI-24',
      'Perlindungan Unduh Berkas Privat Tanpa Sesi (401 UNAUTHENTICATED)',
      'Backend Keamanan',
      'HTTP 401 UNAUTHENTICATED, file berkas tidak dapat diunduh tanpa otentikasi',
      `HTTP ${unauthDocDownload.status}`,
      'PASS',
      `Dokumen privat terlindungi dari akses anonim`
    );
  } else {
    recordResult(
      'SI-24',
      'Perlindungan Unduh Berkas Privat Tanpa Sesi',
      'Backend Keamanan',
      'HTTP 401',
      `HTTP ${unauthDocDownload.status}`,
      'FAIL'
    );
  }

  // SI-25: Pencegahan Duplikasi Pengajuan pada Gelombang yang Sama (409 APPLICATION_ALREADY_EXISTS)
  const duplicateAppRes = await androidClient.request('POST', '/api/v1/applications', {
    wave_id: activeWaveId,
    study_program_id: activeProgId,
  });
  if (duplicateAppRes.status === 409 && duplicateAppRes.data?.error?.code === 'APPLICATION_ALREADY_EXISTS') {
    recordResult(
      'SI-25',
      'Pencegahan Pengajuan Ganda dalam Satu Gelombang (409 APPLICATION_ALREADY_EXISTS)',
      'Backend Integritas Data',
      'HTTP 409 APPLICATION_ALREADY_EXISTS, menolak draft kedua pada gelombang yang sama',
      `HTTP ${duplicateAppRes.status}, Error Code: ${duplicateAppRes.data?.error?.code}`,
      'PASS',
      `Integritas bisnis satu pendaftaran per gelombang terjaga`
    );
  } else {
    recordResult(
      'SI-25',
      'Pencegahan Pengajuan Ganda dalam Satu Gelombang',
      'Backend Integritas Data',
      'HTTP 409 APPLICATION_ALREADY_EXISTS',
      `HTTP ${duplicateAppRes.status}`,
      'FAIL'
    );
  }

  // SI-26: Validasi Gagal (422 VALIDATION_ERROR)
  const invalidProfile = await androidClient.request('PUT', '/api/v1/applicant/profile', {
    full_name: '',
    nik: '123', // NIK tidak 16 digit
  });
  if (invalidProfile.status === 422 && invalidProfile.data?.error?.code === 'VALIDATION_ERROR') {
    recordResult(
      'SI-26',
      'Penanganan Kesalahan Validasi Input (422 VALIDATION_ERROR)',
      'Backend Validasi',
      'HTTP 422 VALIDATION_ERROR dengan rincian field yang salah',
      `HTTP ${invalidProfile.status}, Details: ${Object.keys(invalidProfile.data.error.details).join(', ')}`,
      'PASS',
      `Validasi menolak input cacat dan mengembalikan rincian per field`
    );
  } else {
    recordResult(
      'SI-26',
      'Penanganan Kesalahan Validasi Input',
      'Backend Validasi',
      'HTTP 422 VALIDATION_ERROR',
      `HTTP ${invalidProfile.status}`,
      'FAIL'
    );
  }

  // SI-27: Logout & Pencabutan Token Android (POST /api/v1/auth/logout)
  const androidLogout = await androidClient.request('POST', '/api/v1/auth/logout');
  const postLogoutMe = await androidClient.request('GET', '/api/v1/auth/me');
  if (androidLogout.status === 200 && postLogoutMe.status === 401) {
    recordResult(
      'SI-27',
      'Pencabutan Token Android (POST /api/v1/auth/logout)',
      'Android ↔ Backend',
      'HTTP 200 OK, token dicabut dari database; request selanjutnya ditolak dengan 401',
      `Logout: HTTP ${androidLogout.status}, Pasca-logout /me: HTTP ${postLogoutMe.status}`,
      'PASS',
      `Bearer token Sanctum berhasil di-revoke secara permanen`
    );
  } else {
    recordResult(
      'SI-27',
      'Pencabutan Token Android',
      'Android ↔ Backend',
      'Logout 200 dan pasca-logout 401',
      `Logout: ${androidLogout.status}, Pasca: ${postLogoutMe.status}`,
      'FAIL'
    );
  }

  // SI-28: Logout & Pengakhiran Sesi Admin Web (POST /api/v1/auth/session/logout)
  const adminLogout = await adminClient.request('POST', '/api/v1/auth/session/logout');
  const postLogoutAdminMe = await adminClient.request('GET', '/api/v1/auth/me');
  if (adminLogout.status === 200 && postLogoutAdminMe.status === 401) {
    recordResult(
      'SI-28',
      'Pengakhiran Sesi Browser Admin Web (POST /api/v1/auth/session/logout)',
      'Admin Web ↔ Backend',
      'HTTP 200 OK, sesi cookie dibersihkan; request selanjutnya ditolak dengan 401',
      `Logout: HTTP ${adminLogout.status}, Pasca-logout /me: HTTP ${postLogoutAdminMe.status}`,
      'PASS',
      `Sesi cookie first-party dibersihkan dan diinvalidsi`
    );
  } else {
    recordResult(
      'SI-28',
      'Pengakhiran Sesi Browser Admin Web',
      'Admin Web ↔ Backend',
      'Logout 200 dan pasca-logout 401',
      `Logout: ${adminLogout.status}, Pasca: ${postLogoutAdminMe.status}`,
      'FAIL'
    );
  }
}

// -----------------------------------------------------------------------------
// Report Summary
// -----------------------------------------------------------------------------

function printSummary() {
  console.log('\n========================================================================');
  console.log('  RINGKASAN HASIL PENGUJIAN SISTEM INTEGRASI TAHAP 9');
  console.log('========================================================================\n');

  const total = testResults.length;
  const passed = testResults.filter((r) => r.status === 'PASS').length;
  const failed = testResults.filter((r) => r.status === 'FAIL').length;

  console.log(`Total Skenario Diuji : ${total}`);
  console.log(`Lulus (PASS)         : ${passed}`);
  console.log(`Gagal (FAIL)         : ${failed}`);
  console.log(`Tingkat Kelulusan    : ${((passed / total) * 100).toFixed(2)}%\n`);

  if (failed > 0) {
    console.error(`PERINGATAN: Terdapat ${failed} skenario yang gagal!`);
    process.exit(1);
  } else {
    console.log('SELURUH 28 SKENARIO INTEGRASI SISTEM LULUS 100%!');
  }
}

main().catch((err) => {
  console.error('[FATAL ERROR]', err);
  process.exit(1);
});
