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

## Menjalankan sistem (lokal)

```bash
# 1. Database
cp .env.example .env        # isi MYSQL_PASSWORD dan MYSQL_ROOT_PASSWORD
chmod 600 .env
docker compose up -d db
docker compose ps           # tunggu (healthy)

# 2. Backend
cd backend-api
composer install
cp .env.example .env        # isi DB_* sesuai .env root, lalu:
php artisan key:generate
php artisan migrate         # jangan pakai migrate:fresh / db:wipe
php artisan serve --host=127.0.0.1 --port=8000

# 3. Admin web
cd admin-web
npm ci
npm run dev                 # http://localhost:3000, proxy ke 127.0.0.1:8000

# 4. Android
cd android-app
./gradlew :app:assembleDebug
# buka folder android-app/ di Android Studio, atau pasang app-debug.apk
```

Alamat API: emulator memakai `http://10.0.2.2:8000/api/v1/` (default debug),
perangkat fisik memakai IP komputer pada jaringan yang sama, produksi memakai
HTTPS. Lihat `docs/09 - Deployment/Deployment Guide.md` untuk detail.

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
