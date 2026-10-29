# Laporan Implementasi Tahap 8 — Admin Website Development
## Proyek PMB ITB Akiday

| Informasi | Keterangan |
|---|---|
| **Tahap Proyek** | Tahap 8 — Admin Website Development |
| **Status Akhir** | `VERIFIED` |
| **Checkpoint** | `ACCEPTED` |
| **Platform** | React 18, TypeScript 5, Vite 5 SPA |
| **Backend Integrasi** | Laravel REST API v1 (`/api/v1`) |
| **Autentikasi** | Laravel Sanctum Browser Session + CSRF Protection |
| **Tanggal Pelaksanaan**| 28 Oktober 2026 |

---

## 1. Ringkasan Eksekutif

Tahap 8 berfokus pada pembangunan antarmuka situs administrasi (**Admin Website**) untuk panitia, administrator, dan pimpinan PMB ITB Akiday. Aplikasi ini dibangun secara terpisah dari peramban client-side (*Single Page Application*) yang sepenuhnya mengonsumsi REST API Laravel tanpa akses langsung ke basis data MySQL/MariaDB.

Seluruh kriteria penerimaan inti telah berhasil diimplementasikan dan diverifikasi:
1. Otentikasi sesi cookie first-party Laravel Sanctum dengan perlindungan CSRF (`/sanctum/csrf-cookie`, `POST /auth/session/login`, `POST /auth/session/logout`, `GET /auth/me`).
2. Dashboard ringkasan pendaftaran dengan metrik agregat riil dan daftar pendaftar terbaru.
3. Daftar pengajuan calon mahasiswa dengan pencarian, filter status/gelombang/prodi, pagination di server, penanganan loading, error, dan kondisi kosong.
4. Detail pengajuan calon mahasiswa memuat profil lengkap pendaftar, pilihan program studi & gelombang.
5. Akses dokumen privat pendaftar melalui endpoint unduh terotorisasi berbasis streaming blob.
6. Workflow telaah pendaftaran:
   - Mulai pemeriksaan (`POST /admin/applications/{id}/review`).
   - Permintaan revisi berkas (`POST /admin/applications/{id}/request-revision`).
   - Penetapan keputusan akhir (`POST /admin/applications/{id}/decision`).
7. Penanganan konflik status (`409 STATE_CONFLICT`) berbasis precondition `expected_status` untuk mencegah *stale writes*.
8. Riwayat perubahan status pendaftaran ditampilkan dalam garis waktu (*timeline*) kronologis.

---

## 2. Kondisi Workspace Sebelum Perubahan

1. **Struktur Direktori:** Workspace terdiri atas aplikasi Android (`app/`), backend Laravel (`backend-api/`), dan dokumentasi teknis (`docs/`). Direktori `admin-web/` belum dibuat.
2. **Toolchain Lingkungan:**
   - Node.js: `v20.20.2`
   - npm: `10.8.2`
   - PHP: `8.3` dengan ekstensi SQLite, cURL, OpenSSL.
3. **Ketersediaan Backend:** Backend Laravel pada `backend-api/` telah memiliki 48 unit & feature tests yang lulus 100%, mencakup route auth, profil, pendaftaran, dokumen, telaah berkas, dan riwayat status.

---

## 3. Teknologi dan Arsitektur Aktual

Situs admin diimplementasikan dalam direktori `admin-web/` menggunakan pendekatan arsitektur modular:
- **Framework & Runtime:** React 18.3, TypeScript 5.6, Vite 5.4.
- **Routing:** React Router DOM 6.28.
- **HTTP & State Management:** Axios dengan instance terpusat (`client.ts`), React Context API (`AuthContext.tsx`) untuk mengelola sesi pengguna dan kontrol hak akses (*role-based access control*).
- **Styling:** Design System resmi PMB ITB Akiday (`src/index.css`) berbasis CSS variables dengan palet Teal (`#0F766E`), Accent Orange (`#F97316`), Slate Background (`#F8FAFC`), dan tipografi Inter.
- **Icon Library:** Lucide React (ringan, tree-shakeable, konsisten).

---

## 4. File yang Dibuat

| Direktori / File | Peran & Deskripsi |
|---|---|
| `admin-web/package.json` | Konfigurasi dependensi React, TypeScript, Vite, Axios, Lucide React, script build dan test. |
| `admin-web/tsconfig.json` | Konfigurasi compiler TypeScript (strict mode, JSX React, module resolution bundler). |
| `admin-web/vite.config.ts` | Konfigurasi bundler Vite dan reverse-proxy rute `/sanctum` & `/api` ke backend `http://127.0.0.1:8000`. |
| `admin-web/index.html` | Entrypoint HTML peramban dengan font Google Inter. |
| `admin-web/src/index.css` | Token palet warna, tipografi, utility classes, komponen card, table, alert, timeline, modal. |
| `admin-web/src/types/index.ts` | Definisi TypeScript untuk User, Application, ApplicantProfile, ApplicationDocument, StatusHistory, ApiResponse, ApiErrorResponse. |
| `admin-web/src/api/client.ts` | Instance Axios dengan `withCredentials: true`, CSRF bootstrap, dan extractor penanganan error `extractApiError`. |
| `admin-web/src/api/auth.ts` | Klien autentikasi sesi peramban (`sessionLogin`, `sessionLogout`, `getMe`). |
| `admin-web/src/api/applications.ts` | Klien daftar pendaftar ber-pagination & detail pendaftar (`fetchAdminApplications`, `fetchAdminApplicationDetail`). |
| `admin-web/src/api/reviews.ts` | Klien alur telaah berkas (`submitStartReview`, `submitRequestRevision`, `submitDecision`). |
| `admin-web/src/api/documents.ts` | Klien berkas privat (`downloadApplicationDocument` via authorized stream blob). |
| `admin-web/src/api/references.ts` | Klien data referensi gelombang pendaftaran dan program studi. |
| `admin-web/src/context/AuthContext.tsx` | Provider sesi peramban, auto-session recovery, role checking (`hasRole`). |
| `admin-web/src/components/StatusBadge.tsx` | Badge status pendaftaran dengan label dan kontras teks yang aksesibel. |
| `admin-web/src/components/Modal.tsx` | Komponen dialog modal accessible dengan keyboard Esc handling & focus lock. |
| `admin-web/src/components/Alert.tsx` | Komponen notifikasi umpan balik aksi (danger, success, warning, info). |
| `admin-web/src/components/LoadingSpinner.tsx` | Indikator pemuatan data. |
| `admin-web/src/components/EmptyState.tsx` | Tampilan status kosong saat tidak ada data pendaftar. |
| `admin-web/src/components/ProtectedRoute.tsx` | Route guard untuk mengamankan halaman internal dari akses publik / unauthorized role. |
| `admin-web/src/components/Layout/Navbar.tsx` | Header atas dengan identitas pengguna, lencana peran, dan tombol logout. |
| `admin-web/src/components/Layout/Sidebar.tsx` | Navigasi menu utama dan indikator keamanan sesi Sanctum. |
| `admin-web/src/components/Layout/AppLayout.tsx` | Shell tata letak aplikasi admin. |
| `admin-web/src/pages/LoginPage.tsx` | Halaman login peramban (WEB-01). |
| `admin-web/src/pages/DashboardPage.tsx` | Halaman dashboard ringkasan & kartu metrik (WEB-02). |
| `admin-web/src/pages/ApplicationsPage.tsx` | Halaman daftar pendaftar dengan filter, pencarian, dan pagination (WEB-03). |
| `admin-web/src/pages/ApplicationDetailPage.tsx` | Halaman detail berkas, unduh dokumen, telaah berkas, revisi, dan keputusan (WEB-04, WEB-05, WEB-06). |
| `admin-web/src/pages/NotFoundPage.tsx` | Halaman 404 ketika rute tidak ditemukan. |
| `admin-web/src/App.tsx` | Konfigurasi routing utama aplikasi. |
| `admin-web/src/main.tsx` | Entrypoint bootstrap aplikasi React. |
| `admin-web/test/api-client.test.mjs` | Pengujian unit penanganan error API Contract (401, 403, 404, 409, 419, 422). |
| `admin-web/test/workflow-logic.test.mjs` | Pengujian unit alur transisi status, precondition check, dan matriks hak akses peran. |
| `admin-web/test/http-integration-verify.mjs` | Pengujian integrasi HTTP riil terhadap server Laravel. |
| `admin-web/README.md` | Petunjuk teknis menjalankan, mengompilasi, dan menguji website admin. |

---

## 5. Halaman dan Fitur yang Berhasil Diimplementasikan

### 1. WEB-01 — Login Admin
- Masuk menggunakan email dan password akun staf internal (`admin`, `panitia`, `pimpinan`).
- Menginisialisasi token CSRF via `GET /sanctum/csrf-cookie` sebelum mengirimkan form login.
- Menggunakan cookie first-party `POST /api/v1/auth/session/login` (`credentials: include`).
- Memvalidasi bahwa pengguna memiliki hak akses administratif; peran non-admin ditolak dengan pesan yang jelas.

### 2. WEB-02 — Dashboard
- Kartu metrik ringkasan: Total Pendaftar, Menunggu Pemeriksaan (`submitted`), Dalam Pemeriksaan (`review`), Perlu Revisi (`revision`), Diterima (`accepted`), Ditolak (`rejected`).
- Menghitung agregasi dari data riil pendaftaran yang sah (tidak menggunakan statistik fiktif).
- Tabel 10 Pendaftaran Terbaru dengan lencana status dan aksi langsung menuju detail.
- Penanganan status pemuatan, data kosong, dan penanganan kesalahan dengan tombol coba kembali (*retry*).

### 3. WEB-03 — Daftar Pendaftar
- Menampilkan nomor pendaftaran, nama calon mahasiswa, NIK, program studi, gelombang, status, dan waktu pengajuan.
- Filter terpadu: Status Pendaftaran, Gelombang Penerimaan (dimuat dinamis dari API), dan Program Studi (dimuat dinamis dari API).
- Pencarian cepat lokal berdasarkan nomor pendaftaran, nama, atau NIK.
- Indikator filter aktif beserta tombol pembersih (*Reset Filter*).
- Pagination terintegrasi dengan backend (`page`, `per_page`, `meta.total`, `meta.last_page`).

### 4. WEB-04 — Detail Pendaftar & Dokumen
- Menampilkan profil lengkap calon mahasiswa (Nama, NIK, Tempat/Tanggal Lahir, Jenis Kelamin, No. HP, Alamat Domisili).
- Menampilkan rincian pilihan program studi, gelombang, status pendaftaran saat ini, dan waktu submit.
- Bagian Berkas Dokumen Pendaftaran: Menampilkan jenis dokumen, nama berkas, ukuran (KB/MB), dan tanggal unggah.
- Pengunduhan berkas privat menggunakan endpoint terproteksi (`GET /api/v1/applications/{id}/documents/{docId}/download`) via streaming blob di peramban tanpa membocorkan path penyimpanan disk server.

### 5. WEB-05 & WEB-06 — Workflow Pemeriksaan, Revisi, dan Keputusan
- **Mulai Pemeriksaan:** Tombol *"Mulai Pemeriksaan"* muncul saat status berkas `submitted`. Mengirimkan `POST /api/v1/admin/applications/{id}/review` dengan payload `{ expected_status: 'submitted' }`.
- **Minta Revisi:** Dialog modal untuk mengisi instruksi perbaikan berkas. Mengirimkan `POST /api/v1/admin/applications/{id}/request-revision` dengan payload `{ expected_status: 'review', note: '...' }`.
- **Tetapkan Keputusan:** Dialog modal untuk memilih hasil seleksi (*Diterima* atau *Ditolak*) beserta catatan keputusan resmi. Mengirimkan `POST /api/v1/admin/applications/{id}/decision` dengan payload `{ expected_status: 'review', decision: '...', note: '...' }`.
- **Deteksi Konflik Status (409 STATE_CONFLICT):** Jika status berkas di server telah berubah oleh panitia lain, UI menangkap error 409, menampilkan peringatan informatif, dan secara otomatis memuat ulang data terbaru.
- **Garis Waktu Riwayat Status:** Menampilkan seluruh riwayat transisi status, waktu perubahan, dan catatan panitia secara kronologis.

---

## 6. Endpoint API yang Digunakan

| HTTP Method | Endpoint | Fungsi | Status Integrasi |
|---|---|---|:---:|
| `GET` | `/sanctum/csrf-cookie` | Inisialisasi cookie proteksi CSRF | Terverifikasi |
| `POST`| `/api/v1/auth/session/login` | Login sesi peramban first-party | Terverifikasi |
| `POST`| `/api/v1/auth/session/logout` | Mengakhiri sesi cookie peramban | Terverifikasi |
| `GET` | `/api/v1/auth/me` | Memeriksa dan memulihkan sesi aktif | Terverifikasi |
| `GET` | `/api/v1/admission-waves` | Memuat data referensi gelombang penerimaan | Terverifikasi |
| `GET` | `/api/v1/study-programs` | Memuat data referensi program studi | Terverifikasi |
| `GET` | `/api/v1/admin/applications` | Mengambil daftar pendaftar + filter & pagination | Terverifikasi |
| `GET` | `/api/v1/admin/applications/{id}` | Mengambil detail lengkap pendaftar & dokumen | Terverifikasi |
| `GET` | `/api/v1/applications/{id}/documents` | Mengambil metadata dokumen pendaftaran | Terverifikasi |
| `GET` | `/api/v1/applications/{id}/documents/{docId}/download` | Mengunduh file berkas privat terotorisasi | Terverifikasi |
| `POST`| `/api/v1/admin/applications/{id}/review` | Mengubah status `submitted` -> `review` | Terverifikasi |
| `POST`| `/api/v1/admin/applications/{id}/request-revision` | Mengubah status `review` -> `revision` | Terverifikasi |
| `POST`| `/api/v1/admin/applications/{id}/decision` | Menetapkan status `review` -> `accepted`/`rejected` | Terverifikasi |
| `GET` | `/api/v1/applications/{id}/status-history` | Mengambil riwayat transisi status pendaftaran | Terverifikasi |

---

## 7. Fitur yang Belum Diimplementasikan & Alasannya

1. **Manajemen Pengguna (`/admin/users`) & Peran:** Sesuai batasan Tahap 8, fokus utama adalah alur inti operasional pendaftaran (login, dashboard, daftar pendaftar, detail berkas, pemeriksaan, revisi, dan keputusan). Modul administrasi pengguna ditangguhkan sesuai `Module Specification.md` (prioritas P1).
2. **Ekspor Laporan PDF:** Memerlukan library pembangkit PDF di backend (`barryvdh/laravel-dompdf` atau sejenisnya) yang belum masuk dalam cakupan endpoint saat ini.
3. **Pemeriksaan Status Dokumen Individual:** Mengikuti catatan arsitektur `DB-01`, keputusan telaah disimpan pada level aplikasi secara utuh (`Application` status and `ApplicationStatusHistory`), bukan status terpisah per berkas dokumen sampai keputusan domain DB-01 difinalisasi.

---

## 8. Hasil Pemeriksaan & Pengujian

### A. TypeScript Check (`npm run typecheck`)
- Perintah: `tsc --noEmit`
- Hasil: **0 errors** (Lulus 100%).

### B. Produksi Build (`npm run build`)
- Perintah: `tsc -b && vite build`
- Hasil: **SUKSES** (Waktu build: 8.70s).
  - `dist/index.html`: `0.72 kB` (gzip: 0.40 kB)
  - `dist/assets/index-V7g1kBHs.css`: `6.19 kB` (gzip: 1.75 kB)
  - `dist/assets/index-DSZztIE7.js`: `279.13 kB` (gzip: 87.76 kB)

### C. Pengujian Unit & Logika Frontend (`npm test`)
- Perintah: `node --test test/*.test.mjs`
- Hasil: **10 tests PASSED, 0 failures, 0 errors** (Durasi: ~141ms).
  - 5 tests pada `api-client.test.mjs` (verifikasi mapping kode 401, 403, 404, 409 STATE_CONFLICT, 419 CSRF_MISMATCH, 422 VALIDATION_ERROR).
  - 5 tests pada `workflow-logic.test.mjs` (verifikasi validasi transisi, precondition check 409, dan matriks hak akses peran).

### D. Pengujian Integrasi HTTP Riil (`node test/http-integration-verify.mjs`)
- Server Backend: `http://127.0.0.1:8000`
- Hasil Pengujian: **100% LULUS**
  - `[1] GET /sanctum/csrf-cookie`: HTTP 204 (XSRF-TOKEN cookie diperoleh).
  - `[2] POST /api/v1/auth/session/login`: HTTP 200 (Login admin sukses, sesi cookie terbentuk).
  - `[3] GET /api/v1/auth/me`: HTTP 200 (Sesi terverifikasi untuk role `admin`).
  - `[4] GET /api/v1/admission-waves` & `/study-programs`: HTTP 200 (Data referensi berhasil diambil).
  - `[5] GET /api/v1/admin/applications`: HTTP 200 (Daftar pendaftar & metadata pagination berhasil diambil).
  - `[6] GET /api/v1/admin/applications/1`: HTTP 200 (Detail pendaftar berhasil diambil).
  - `[7] POST /api/v1/admin/applications/1/review` (dengan status tidak valid): HTTP 409 `STATE_CONFLICT` (Kesesuaian kontrak terverifikasi).
  - `[7.1] POST /api/v1/admin/applications/1/review` (status valid): HTTP 200 (Status beralih ke `review`).
  - `[7.2] POST /api/v1/admin/applications/1/request-revision`: HTTP 200 (Status beralih ke `revision`).
  - `[7.3] POST /api/v1/admin/applications/1/decision` (saat masih revisi): HTTP 409 `STATE_CONFLICT` (Dicegah di server).
  - `[7.4] GET /api/v1/applications/1/status-history`: HTTP 200 (Riwayat status tercatat secara kronologis).
  - `[8] POST /api/v1/auth/session/logout`: HTTP 200 (Sesi berhasil diakhiri).
  - `[9] GET /api/v1/auth/me` (pasca-logout): HTTP 401 `UNAUTHENTICATED` (Terverifikasi sesi berakhir).

---

## 9. Temuan Keamanan & Rekomendasi

1. **Otorisasi Server-Side Tetap Utama:** Antarmuka menyembunyikan tombol aksi mutasi untuk peran `pimpinan` dan non-staf, namun seluruh endpoint backend secara independen memvalidasi otorisasi via middleware `role:admin,panitia` dan `role:admin,panitia,pimpinan`.
2. **Perlindungan Berkas Privat:** Berkas dokumen pendaftar tidak menggunakan URL publik `/storage/...`. Dokumen dialirkan langsung melalui respons streaming bertoken sesi terotorisasi.
3. **Pencegahan Race Condition:** Mekanisme `expected_status` dan respons 409 mencegah situasi di mana dua panitia memproses satu berkas secara bersamaan tanpa sengaja menimpa status yang sudah diperbarui.

---

## 10. Rekomendasi Pekerjaan Selanjutnya

1. **Melanjutkan ke Tahap 9 — System Integration:** Mengintegrasikan seluruh komponen (Android Application, Laravel Backend API, dan Admin Website) dalam skenario end-to-end terpadu.
2. **Konfigurasi Lingkungan Produksi:** Mengatur origin CORS dan `SANCTUM_STATEFUL_DOMAINS` pada domain produksi resmi ketika domain server telah dialokasikan.
