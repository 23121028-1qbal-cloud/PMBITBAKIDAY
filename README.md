# PMB ITB Akiday

Sistem Penerimaan Mahasiswa Baru ITB Akiday dalam satu repository:

| Folder | Isi |
|---|---|
| `android-app/` | Aplikasi calon mahasiswa — Kotlin + Jetpack Compose |
| `backend-api/` | REST API — Laravel (sumber kebenaran data & otorisasi) |
| `admin-web/` | Website admin/panitia/pimpinan — React + TypeScript + Vite |
| `docs/` | Dokumen proyek (requirement, desain, API, deployment, pengujian) |
| `scripts/` | Backup, verifikasi restore, dan migrasi database |

Database resmi adalah **MySQL 8.4** dan hanya diakses oleh backend. Android dan
website admin berkomunikasi melalui REST API `/api/v1`. Dokumen pendaftar
disimpan di private storage Laravel dan hanya dapat diunduh lewat endpoint
terotorisasi.

## Prasyarat

- PHP 8.4 + ekstensi `pdo_mysql`, Composer 2
- Node.js 20 + npm
- Docker + Docker Compose (untuk database lokal)
- JDK 17+ dan Android SDK (untuk aplikasi Android)

## Menjalankan Server Web (`backend-api` & `admin-web`)

Sistem web terdiri dari Database MySQL, Backend API (Laravel), dan Website Admin (React + Vite). Urutan menyalakan harus sesuai dependensi:
**Database MySQL ➔ Backend API (Port 8000) ➔ Admin Web (Port 3000)**.

### 1. Menyalakan Database (Prasyarat Backend)
Database lokal dijalankan melalui Docker Compose menggunakan MySQL 8.4:
```bash
# Dari root project:
docker compose up -d db

# Pastikan container berstatus healthy (tunggu ±10-30 detik pertama):
docker compose ps
```

### 2. Menghidupkan Server Backend API (`backend-api`)
Backend Laravel bertindak sebagai REST API dan sumber data (`http://127.0.0.1:8000`):
```bash
# Buka terminal dan pindah ke direktori backend:
cd backend-api

# Jika pertama kali / ada perubahan dependensi:
composer install
cp .env.example .env     # pastikan konfigurasi DB sesuai
php artisan key:generate
php artisan migrate      # jangan gunakan migrate:fresh / db:wipe

# Jalankan server backend:
php artisan serve --host=127.0.0.1 --port=8000
```
> **Tips:** Biarkan terminal ini tetap terbuka. Jika ingin backend dapat diakses oleh perangkat HP fisik di satu jaringan Wi-Fi, jalankan dengan `--host=0.0.0.0`:
> `php artisan serve --host=0.0.0.0 --port=8000`

### 3. Menghidupkan Server Website Admin (`admin-web`)
Website antarmuka admin/panitia menggunakan React + TypeScript + Vite (`http://localhost:3000`):
```bash
# Buka jendela terminal baru, lalu pindah ke direktori admin-web:
cd admin-web

# Pasang dependensi jika belum:
npm ci

# Jalankan server pengembangan Vite:
npm run dev
```
> **Catatan:** Admin web dikonfigurasi (`vite.config.ts`) untuk otomatis mem-proxy request `/api` dan `/sanctum` langsung ke `http://127.0.0.1:8000`.

### 4. Memverifikasi Server Aktif
Anda dapat memverifikasi bahwa kedua server telah melayani request:
- **Backend API:**
  ```bash
  curl -i http://127.0.0.1:8000/sanctum/csrf-cookie
  # Harapan: HTTP/1.1 204 No Content
  ```
- **Admin Web:**
  Buka peramban di [http://localhost:3000](http://localhost:3000) atau jalankan:
  ```bash
  curl -i http://localhost:3000
  # Harapan: HTTP/1.1 200 OK
  ```

### 5. Akun Login Web Admin (Data Seeder)
Untuk mengisi atau mereset akun staf (*Admin*, *Panitia*, *Pimpinan*) ke dalam database, jalankan perintah seeder di folder `backend-api`:
```bash
cd backend-api
php artisan db:seed
```
Akun login bawaan yang dibuat (dapat disesuaikan di `backend-api/.env`):
- **Admin:** `admin@example.com` | Password: `admin123` (atau sesuai `SEED_ADMIN_PASSWORD`)
- **Panitia:** `panitia@example.com` | Password: `panitia123` (atau sesuai `SEED_PANITIA_PASSWORD`)
- **Pimpinan:** `pimpinan@example.com` | Password: `pimpinan123` (atau sesuai `SEED_PIMPINAN_PASSWORD`)

### 6. Menghentikan Server
- **Backend & Admin Web:** Tekan `Ctrl + C` pada terminal masing-masing.
- **Database MySQL:**
  ```bash
  docker compose stop db
  ```
  *(Penting: Jangan gunakan `docker compose down -v` agar volume data pendaftaran dan akun tidak terhapus).*

---

## Menjalankan Aplikasi Android (`android-app`)

```bash
cd android-app
./gradlew :app:assembleDebug
# buka folder android-app/ di Android Studio, atau pasang app-debug.apk
```

Alamat API:
- **Emulator Android:** memakai `http://10.0.2.2:8000/api/v1/` (alias localhost laptop).
- **Perangkat Fisik:** ubah `BASE_URL` di `android-app/app/build.gradle.kts` menjadi IP komputer lokal Anda (misal `http://192.168.1.x:8000/api/v1/`), lalu jalankan backend dengan `--host=0.0.0.0`.
Lihat `docs/09 - Deployment/Deployment Guide.md` untuk detail lengkap.

## Pengujian

```bash
# Backend (MySQL) — pakai database uji terpisah, jangan database aktif
cd backend-api
DB_CONNECTION=mysql DB_DATABASE=pmb_itb_akiday_test php artisan test

# Admin web (unit test, typecheck, build)
cd admin-web
npm test && npm run typecheck && npm run build

# Android (unit test + APK debug)
cd android-app
./gradlew :app:testDebugUnitTest :app:assembleDebug
```

Skrip integrasi manual di `admin-web/test/*.mjs` dan live test Android
memerlukan backend yang berjalan **dan** akun staf nyata. Kredensialnya dibaca
dari environment, tanpa nilai default:

```bash
# nilainya ada di backend-api/.env (tidak dilacak Git)
set -a; . backend-api/.env; set +a
export PMB_TEST_ADMIN_EMAIL="$SEED_ADMIN_EMAIL" PMB_TEST_ADMIN_PASSWORD="$SEED_ADMIN_PASSWORD"
export PMB_TEST_PANITIA_EMAIL="$SEED_PANITIA_EMAIL" PMB_TEST_PANITIA_PASSWORD="$SEED_PANITIA_PASSWORD"
export PMB_TEST_PIMPINAN_EMAIL="$SEED_PIMPINAN_EMAIL" PMB_TEST_PIMPINAN_PASSWORD="$SEED_PIMPINAN_PASSWORD"

cd admin-web && node test/product-test-suite.mjs
cd ../android-app && PMB_LIVE_TESTS=1 ./gradlew :app:testDebugUnitTest
```

Tanpa variabel tersebut skrip berhenti dengan pesan jelas, dan live test Android
dilewati (`skipped`) — tidak pernah dilaporkan lulus. Instrumented test Android
butuh emulator/perangkat: `./gradlew :app:connectedDebugAndroidTest`.

CI: `.github/workflows/ci.yml` menjalankan backend di MySQL 8.4, admin web, dan
Android pada setiap push/PR. Workflow ini belum pernah dieksekusi karena
repository belum memiliki remote.

## Backup dan restore

```bash
scripts/backup.sh                  # database + dokumen privat + manifest + checksum
scripts/restore-verify.sh          # uji restore di lingkungan terisolasi
```

Backup ditulis ke `../pmb_backups/<timestamp>/` (di luar source code). Jangan
menjalankan `docker compose down -v`: perintah itu menghapus volume data.

## Git

Repository ini memakai satu repository untuk seluruh komponen. `main` adalah
branch utama dan **belum ada remote**. `.env`, database lokal, dokumen
pendaftar, dependency, dan hasil build tidak dilacak (lihat `.gitignore`).

## Dokumentasi

- `docs/09 - Deployment/Runbook - Menjalankan Server.md` — cara menyalakan, memeriksa, dan mematikan server
- `docs/09 - Deployment/Deployment Guide.md` — setup, Docker, cutover, rollback
- `docs/09 - Deployment/Backup & Recovery.md` — prosedur backup dan restore
- `docs/09 - Deployment/Environment Configuration.md` — konfigurasi lingkungan
- `docs/04 - API/Api Contract.md` — kontrak API
- `docs/08 - Testing/Test Results.md` — hasil pengujian (termasuk Tahap 12)
- `docs/09 - Deployment/Tahap 12 - Release Readiness Report.md` — audit rilis, CI, kredensial uji
