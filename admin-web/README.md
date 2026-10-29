# PMB ITB Akiday — Admin Website

Portal antarmuka web untuk administrator, panitia seleksi, dan pimpinan dalam memproses penerimaan mahasiswa baru ITB Akiday. Dibangun menggunakan React, TypeScript, dan Vite yang mengonsumsi Laravel REST API via autentikasi sesi peramban first-party dan perlindungan CSRF Laravel Sanctum.

---

## 1. Stack Teknologi

- **Framework & Runtime:** React 18, TypeScript 5, Vite 5.
- **Routing:** React Router DOM v6.
- **HTTP Client:** Axios dengan `withCredentials: true`, bootstrap CSRF token via `GET /sanctum/csrf-cookie`, dan header `X-XSRF-TOKEN`.
- **Icon Library:** Lucide React.
- **Design System:** Design System PMB ITB Akiday (Teal `#0F766E`, Accent `#F97316`, Surface `#FFFFFF`, Background `#F8FAFC`).
- **Testing:** Node.js native test runner (`node:test`) & integration script.

---

## 2. Struktur Direktori

```
admin-web/
├── src/
│   ├── api/                  # Klien API terpusat
│   │   ├── client.ts         # Konfigurasi Axios, CSRF bootstrap, interceptor error
│   │   ├── auth.ts           # Sesi login, logout, getMe
│   │   ├── applications.ts   # Daftar pendaftar & detail pendaftar
│   │   ├── reviews.ts        # Workflow telaah (review, request-revision, decision)
│   │   ├── documents.ts      # Pengambilan & pengunduhan berkas privat via stream
│   │   └── references.ts     # Referensi gelombang & program studi
│   ├── components/           # Komponen UI reusable
│   │   ├── Layout/           # Navbar, Sidebar, AppLayout
│   │   ├── Alert.tsx         # Notifikasi feedback (success, danger, warning, info)
│   │   ├── EmptyState.tsx    # Tampilan data kosong
│   │   ├── LoadingSpinner.tsx# Indikator loading
│   │   ├── Modal.tsx         # Dialog interaktif accessible
│   │   ├── ProtectedRoute.tsx# Guard rute berdasarkan autentikasi dan peran
│   │   └── StatusBadge.tsx   # Badge status pendaftaran berstandar aksesibilitas
│   ├── context/
│   │   └── AuthContext.tsx   # State sesi pengguna & pengecekan peran (role-based)
│   ├── pages/
│   │   ├── LoginPage.tsx     # WEB-01: Login Admin berbasis cookie sesi Sanctum
│   │   ├── DashboardPage.tsx # WEB-02: Dashboard ringkasan & metrik agregat
│   │   ├── ApplicationsPage.tsx # WEB-03: Daftar pendaftar, pencarian, filter, pagination
│   │   ├── ApplicationDetailPage.tsx # WEB-04/05/06: Detail, berkas, review, revisi, keputusan
│   │   └── NotFoundPage.tsx  # Halaman rute 404
│   ├── types/
│   │   └── index.ts          # Definisi model TypeScript, ApiResponse, & ApiErrorResponse
│   ├── App.tsx               # Konfigurasi router SPA
│   ├── index.css             # Design System token & utility classes
│   └── main.tsx              # Entrypoint React
├── test/
│   ├── api-client.test.mjs   # Unit test penanganan error kontrak API
│   ├── workflow-logic.test.mjs # Unit test transisi status, precondition, & matriks peran
│   └── http-integration-verify.mjs # Pengujian integrasi HTTP riil terhadap Laravel backend
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

---

## 3. Menjalankan Aplikasi

### Instalasi Dependensi
```bash
npm install
```

### Mode Pengembangan (Development)
```bash
npm run dev
```
Aplikasi akan berjalan pada `http://localhost:3000`. Vite secara otomatis melakukan *reverse-proxy* permintaan rute `/sanctum` dan `/api` menuju backend Laravel di `http://127.0.0.1:8000`.

### Typecheck & Build Produksi
```bash
npm run typecheck
npm run build
```

### Menjalankan Pengujian
```bash
npm test
```

### Pengujian Integrasi HTTP Riil
Pastikan backend Laravel sedang berjalan di `http://127.0.0.1:8000`:
```bash
node test/http-integration-verify.mjs
```

---

## 4. Alur Autentikasi dan Keamanan

1. **Bootstrap CSRF:** Peramban memanggil `GET /sanctum/csrf-cookie` untuk memperoleh cookie `XSRF-TOKEN`.
2. **Session Login:** Mengirimkan `POST /api/v1/auth/session/login` dengan header `X-XSRF-TOKEN`. Server menetapkan sesi cookie first-party (`laravel_session`). Tidak ada bearer token yang disimpan di `localStorage` peramban.
3. **Restorasi Sesi:** Saat memuat ulang halaman, `AuthContext` memanggil `GET /api/v1/auth/me` untuk memverifikasi keabsahan sesi secara transparan.
4. **Penanganan 409 STATE_CONFLICT:** Setiap tindakan perubahan status (`review`, `request-revision`, `decision`) menyertakan precondition `expected_status`. Jika status di server telah berubah terlebih dahulu oleh panitia lain, sistem menangani error 409 dan memuat ulang data terbaru.
5. **Akses Berkas Privat:** Berkas dokumen pendaftar diunduh menggunakan endpoint terproteksi (`GET /api/v1/applications/{id}/documents/{docId}/download`) melalui streaming blob aman tanpa membuka URL publik permanen ke storage server.
