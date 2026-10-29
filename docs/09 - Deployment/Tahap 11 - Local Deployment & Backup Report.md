## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Dokumen|Tahap 11 — Local Deployment & Backup Report|
|Nama Proyek|PMB ITB Akiday|
|Versi Dokumen|1.0|
|Status|ACCEPTED WITH LIMITATIONS|
|Tanggal Pengerjaan|29 Oktober 2026|
|Lingkungan|Workstation lokal (Linux), tanpa device/emulator Android|
|Referensi|[[Deployment Guide]], [[Backup & Recovery]], [[Environment Configuration]], [[Tahap 9 - System Integration Report]]|

> Dokumen ini mencatat hasil **aktual** pekerjaan Tahap 11. Setiap angka, status,
> dan perintah di bawah berasal dari eksekusi nyata pada lingkungan ini. Bagian
> yang tidak dapat dijalankan ditandai `NOT RUN` beserta alasannya.

---

## 2. Ringkasan

- **Status akhir: `ACCEPTED WITH LIMITATIONS`.**
- **Tanggal pengerjaan:** 29 Oktober 2026.
- **Kondisi awal repository:**
  - Root workspace bukan repository Git (`.git` tidak ada), sehingga tidak ada
    riwayat versi, branch, maupun remote.
  - Android sudah berada di `android-app/` (pemisahan dari root selesai).
  - `backend-api/` berisi Laravel 13.39.0 yang berjalan di atas **SQLite**
    (`DB_CONNECTION=sqlite`, `database/database.sqlite`), bukan MySQL.
  - `admin-web/` berisi React + TypeScript + Vite yang memakai proxy ke
    `http://127.0.0.1:8000`.
  - Tidak ada file Docker maupun `docker-compose.yml`.
  - Tidak ada mekanisme backup/restore yang dapat dijalankan.
  - Dokumen privat sudah tersimpan di `backend-api/storage/app/private`
    (4 dokumen, cocok dengan 4 baris `application_documents`).
- **Hasil utama:**
  - Database lokal MariaDB 11.8 berjalan di Docker dengan named volume
    persisten dan port terikat ke loopback; Laravel terbukti dapat terhubung.
  - Seluruh 11 migration berhasil dijalankan pada MariaDB tanpa error.
  - Backup database + dokumen privat + manifest + checksum dapat dibuat dan
    diverifikasi ulang; restore diuji pada lingkungan terisolasi dan lulus.
  - Backend 50 test lulus pada SQLite **dan** MariaDB; admin-web 10 test,
    typecheck, dan production build lulus; Android dikompilasi, 42 unit test
    dijalankan, dan APK debug dibuat.
  - Smoke test integrasi 28 skenario lulus 100%.

---

## 3. Hasil Audit

| Area | Kondisi Awal | Temuan | Tindakan |
|---|---|---|---|
| Android | Proyek Kotlin/Compose di `android-app/` | `settings.gradle.kts` benar, wrapper Gradle 9.6.0, `compileSdk 37`/`minSdk 33`. Base URL debug `http://10.0.2.2:8000/api/v1/` dari `buildConfigField`, release memakai HTTPS; `network_security_config.xml` hanya mengizinkan cleartext untuk `10.0.2.2`, `localhost`, `127.0.0.1`. Tidak ada emulator/perangkat (`adb devices` kosong). | Tidak ada perubahan struktur. Dijalankan compile, unit test, dan `assembleDebug`. |
| Laravel | Laravel 13.39.0, PHP 8.4.24, `DB_CONNECTION=sqlite` | `composer.json` `php ^8.3`; driver `mysql`/`mariadb` tersedia di `config/database.php`; ekstensi `pdo_mysql` aktif. Disk `local` sudah menunjuk `storage/app/private`. `sanctum` stateful API aktif. | Menambah override `PRIVATE_STORAGE_PATH` (default tidak berubah). Memperbaiki bug panjang NIK pada factory. Menambah 2 test validasi unggah. |
| Admin Website | React 18 + TS 5.6 + Vite 5, `node_modules` ada | `src/api/client.ts` memakai `VITE_API_BASE_URL` (default `/api/v1`), `withCredentials: true`, dan `GET /sanctum/csrf-cookie`. Proxy dev ke `127.0.0.1:8000`. | Tidak ada perubahan kode. Dijalankan test, typecheck, build, dan smoke test. |
| MySQL | Tidak ada server MySQL; hanya klien MariaDB 11.8.5 | Docker 28.5.2 tersedia dan daemon berjalan; port 3306/3307 kosong. | Menambah `docker-compose.yml` (MariaDB 11.8, port `127.0.0.1:3307`, volume `pmb_mysql_data`). |
| Private Storage | `storage/app/private/documents/<application_id>/` | 4 file merujuk 4 baris DB; `public/` tidak memiliki symlink `storage`. Route `GET /storage/{path}` memakai `ServeFile` yang mensyaratkan signed URL untuk disk privat. | Diverifikasi: akses langsung mengembalikan 403, akses tanpa autentikasi ke endpoint unduh 401. Tidak diubah. |
| Docker | Tidak ada konfigurasi Docker | Volume `pmb_mysql_data` belum ada. | Dibuat; persistensi diuji dengan `restart` dan `down`+`up`. |
| Backup | Tidak ada | Tidak ada skrip, manifest, maupun uji restore. Dokumen `Backup & Recovery` masih usulan. | Dibuat `scripts/backup.sh`, `scripts/restore-verify.sh`, `scripts/stage11-db.php`; prosedur diuji. |

### VERIFIED

- Laravel 13.39.0 / PHP 8.4.24 / Composer 2.10.3 / Node 20.20.2 / npm 10.8.2.
- Docker 28.5.2, Docker Compose 2.40.3, MariaDB server 11.8.9 (container),
  klien MariaDB 11.8.5 di host.
- Gradle 9.6.0 (wrapper), JDK 23 terpasang; toolchain Gradle meminta versi 25.
- Repository bukan Git; `android-app/`, `backend-api/`, `admin-web/`, `docs/`
  berada di satu root tanpa repository bersarang.
- Data lokal yang harus dipertahankan: 10 user, 6 profil, 7 pendaftaran,
  5 dokumen (file nyata di disk), 36 riwayat status, 2 gelombang, 2 prodi.

### NEEDS ACTION

- Runtime masih SQLite walaupun arsitektur dan dokumen menetapkan MySQL/MariaDB.
- Tidak ada repository Git sehingga tidak ada rollback berbasis versi.
- Belum ada penjadwalan backup otomatis, retensi, maupun salinan off-site.

### NEEDS DECISION

- Pemindahan data live dari SQLite ke MariaDB (lihat Bagian 8).
- Lokasi backup final, enkripsi arsip, retensi, dan target RPO/RTO.

### RISK

- Tanpa Git, kesalahan edit tidak dapat dibatalkan lewat version control.
- Backup hanya berada di mesin yang sama bila `PMB_BACKUP_ROOT` tidak diarahkan
  ke media terpisah.

Tidak ditemukan risiko yang mengharuskan persetujuan pengguna sebelum
implementasi; seluruh perubahan bersifat aditif dan tidak menghapus data.

---

## 4. Perubahan Implementasi

### 4.1 `docker-compose.yml` (baru)

- **Alasan:** menyediakan MySQL/MariaDB lokal yang konsisten dan dapat dijalankan
  ulang tanpa kehilangan data.
- **Implementasi:** service `db` memakai image `mariadb:11.8` (selaras dengan
  klien MariaDB 11.8.5 yang tersedia), `MARIADB_DATABASE`/`MARIADB_USER`/
  `MARIADB_PASSWORD`/`MARIADB_ROOT_PASSWORD` dari root `.env`, port
  `127.0.0.1:${DB_FORWARD_PORT:-3307}:3306`, named volume `pmb_mysql_data`, dan
  healthcheck `healthcheck.sh --connect --innodb_initialized`.
- **Dampak:** hanya menambah layanan baru; tidak menyentuh Laravel, Android, atau
  admin-web. Tidak ada database yang ditimpa.
- **Verifikasi:** `docker compose config --quiet` valid; container `healthy`.

### 4.2 `.env.example` (root, baru) dan `.env` (root, lokal)

- **Alasan:** konfigurasi database melalui environment variable, tanpa kredensial
  di file yang dapat dilacak.
- **Implementasi:** template berisi `DB_FORWARD_PORT`, `DB_DATABASE`,
  `DB_USERNAME`, `DB_PASSWORD`, `DB_ROOT_PASSWORD` (kosong). `.env` lokal dibuat
  dengan kata sandi acak (`openssl rand`) dan `chmod 600` — catatan: mode ini
  tidak ditegakkan oleh mount `fuseblk` pada mesin ini (lihat M-4).
- **Dampak:** `.env` hanya dipakai Docker Compose; `backend-api/.env` tidak diubah.
- **Verifikasi:** `docker compose config` menampilkan nilai terisi (disamarkan di
  laporan); `.env` tidak pernah dicetak.

### 4.3 `.gitignore` (root, baru)

- **Alasan:** root belum memiliki `.gitignore`; jika repository Git dibuat nanti,
  rahasia dan backup tidak boleh ikut terlacak.
- **Implementasi:** mengabaikan `.env` (kecuali `.env.example`), `backups/`,
  `*.dump`, `*.sql.gz`, `*.tar.gz`, `local.properties`, `vendor/`,
  `node_modules/`, `dist/`, `database/*.sqlite*`, serta state IDE.
- **Dampak:** tidak ada; repository Git belum ada sehingga tidak ada file
  terlacak yang berubah status.
- **Verifikasi:** diperiksa manual terhadap daftar artefak yang benar-benar ada.

### 4.4 `backend-api/config/filesystems.php`

- **Alasan:** agar restore dapat diuji pada direktori terisolasi tanpa menyentuh
  storage aktif, dan agar lokasi storage dapat dipindah saat deployment.
- **Implementasi:** `'root' => env('PRIVATE_STORAGE_PATH', storage_path('app/private'))`.
  Nilai default identik dengan perilaku sebelumnya.
- **Dampak:** tidak ada perubahan perilaku bila variabel tidak diset. Tidak ada
  dokumen yang dipindahkan; `documents/*` tidak berubah.
- **Verifikasi:** `stage11-db.php info` menampilkan path normal; dengan
  `PRIVATE_STORAGE_PATH=/tmp/x` menampilkan path override.

### 4.5 `scripts/stage11-db.php` (baru)

- **Alasan:** satu sumber kebenaran untuk driver, path storage, dan kredensial
  tanpa mencetak rahasia.
- **Implementasi:** perintah `info`, `dump`, `counts`, `document-refs`,
  `http-fixtures`, `mint-token`, `creds`, `admin-creds`, `env-for`.
  Dump SQLite memakai `VACUUM INTO` (salinan konsisten). Dump MySQL/MariaDB
  memakai `mysqldump` dengan `--defaults-extra-file` sementara (0600) sehingga
  kata sandi tidak muncul di argumen proses.
- **Dampak:** hanya alat operasional; tidak dipanggil aplikasi.
- **Verifikasi:** `php -l` bersih; dipakai nyata oleh backup dan restore drill.

### 4.6 `scripts/backup.sh` (baru)

- **Alasan:** menyediakan backup yang mencakup database, dokumen privat,
  manifest, dan checksum sekaligus.
- **Implementasi:** membuat satu direktori bertimestamp di luar source tree
  (default `../pmb_backups`) berisi `database.sqlite`/`database.sql`,
  `documents.tar.gz`, `documents.sha256`, `db-counts.json`, `manifest.txt`, dan
  `SHA256SUMS`. Memeriksa exit code, ukuran tidak nol, `PRAGMA integrity_check`
  (SQLite) atau penanda `CREATE TABLE`/`Dump completed` (MySQL), jumlah entri
  arsip sama dengan jumlah file sumber, dan mendeteksi perubahan dokumen selama
  proses. `umask 077`, direktori `700`.
- **Dampak:** read-only terhadap data sumber; tidak menghapus file apa pun.
- **Verifikasi:** dijalankan terhadap data live (SQLite) dan terhadap MariaDB;
  `sha256sum -c` lulus.

### 4.7 `scripts/restore-verify.sh` (baru)

- **Alasan:** backup belum sah sebelum pemulihannya terbukti.
- **Implementasi:** 7 tahap — verifikasi `SHA256SUMS`, workspace terisolasi di
  `$TMPDIR`, restore database (salinan SQLite atau database scratch MySQL yang
  dibuat dan dihapus kembali), restore dokumen, perbandingan row count dengan
  manifest, pengecekan setiap `file_path` terhadap file hasil restore, lalu uji
  HTTP pada salinan hasil restore memakai backend sementara di port 8011.
- **Dampak:** tidak pernah menulis ke database aktif atau ke storage aktif.
- **Verifikasi:** lihat Bagian 6.

### 4.8 `backend-api/database/factories/ApplicantProfileFactory.php`

- **Alasan:** suite test gagal di MariaDB dengan
  `SQLSTATE[22001] Data too long for column 'nik'`. Factory menghasilkan 18 digit
  sedangkan kolom `nik` adalah `string(16)`. SQLite tidak menegakkan batas
  panjang sehingga bug ini tidak pernah terlihat.
- **Implementasi:** `numerify('32##############')` (2 + 14 = 16 digit) sesuai
  format NIK. Skema tidak diubah.
- **Dampak:** hanya data uji. Tidak ada perubahan kontrak API atau aturan bisnis.
- **Verifikasi:** suite gagal 21 test sebelum perbaikan; 50 test lulus sesudahnya
  pada SQLite maupun MariaDB.

### 4.9 `backend-api/tests/Feature/DocumentTest.php`

- **Alasan:** belum ada test yang membuktikan penolakan format dan ukuran file,
  padahal Tahap 11 mewajibkannya.
- **Implementasi:** dua test baru — unggah `text/plain` ditolak 422
  `VALIDATION_ERROR`, dan unggah 11 MB (batas 10 MB) ditolak 422. Keduanya juga
  memastikan tidak ada baris `application_documents` maupun file tersisa.
- **Dampak:** menambah cakupan; tidak mengubah kode aplikasi.
- **Verifikasi:** `DocumentTest` 8 test lulus.

### 4.10 Dokumentasi

- `docs/09 - Deployment/Tahap 11 - Local Deployment & Backup Report.md` (dokumen ini).
- `docs/09 - Deployment/Deployment Guide.md` — ditambah bagian Docker Compose,
  Base URL, dan backup yang terverifikasi.
- `docs/09 - Deployment/Backup & Recovery.md` — ditambah prosedur aktual,
  lokasi backup, batasan, dan status pengujian BR-001…BR-007.
- `docs/09 - Deployment/Environment Configuration.md` — bagian "Kondisi Lokal
  yang Terverifikasi" diperbarui (sebelumnya menyatakan `backend-api/`,
  `admin-web/`, dan Android di root belum/tidak ada).

---

## 5. Hasil Deployment Lokal

| Komponen | Status | Bukti |
|---|---|---|
| Database | `VERIFIED` | Container `pmb-itb-akiday-db` (mariadb:11.8) `Up (healthy)`, `127.0.0.1:3307->3306/tcp`; volume `pmb_mysql_data`. |
| Koneksi Laravel → MariaDB | `VERIFIED` | `php artisan db:show`: MariaDB 11.8.9, connection `mariadb`, host `127.0.0.1`, port 3307, database `pmb_itb_akiday`. 11 migration berjalan tanpa error. |
| Admin Website | `VERIFIED` | `npm run build` sukses (`dist/assets/index-*.js` 279.146 byte); dev proxy ke `127.0.0.1:8000` sesuai konfigurasi. |
| Build Android | `VERIFIED` | `:app:assembleDebug` sukses; `app-debug.apk` 70.034.860 byte. |
| Koneksi API | `VERIFIED` | 28/28 skenario integrasi lulus terhadap backend live; 2 test live Android→Laravel lulus saat backend berjalan. |
| Private storage | `VERIFIED` | URL publik langsung → 403 (butuh signed URL); unduh tanpa autentikasi → 401; pemilik → 200 dengan byte identik. |
| Runtime DB aplikasi | `NEEDS DECISION` | `backend-api/.env` masih `DB_CONNECTION=sqlite`; pemindahan ke MariaDB belum dilakukan (lihat Bagian 8). |

Catatan: alamat API untuk emulator tetap `http://10.0.2.2:8000/api/v1/`
(`buildConfigField` debug) dan release memakai HTTPS. Tidak ada alamat
development yang ditanam sebagai alamat production.

---

## 6. Hasil Backup dan Restore

- **Lokasi backup:** direktori di luar source tree, default
  `<root proyek>/../pmb_backups/<timestamp-UTC>/` (pada mesin ini
  `…/AndroidStudioProjects/pmb_backups/`). Skrip menerapkan `umask 077` dan
  `chmod 700/600`, tetapi pada mesin ini mount proyek bertipe `fuseblk` (NTFS)
  yang **tidak menegakkan** permission Unix: `stat` membaca `777` untuk file yang
  di-`chmod 600`. Skrip mencetak peringatan dan mencatat
  `filesystem_enforces_permissions=no` pada manifest.
- **Backup terakhir yang diverifikasi:** `20261028T234536Z`
  (`status=complete`, `documents_referenced=5`, `documents_referenced_missing=0`).

| Item | Nilai |
|---|---|
| Database dump | `database.sqlite`, 196.608 byte, 17 tabel |
| Arsip dokumen | `documents.tar.gz`, 6 file (5 dokumen + `.gitignore`), 321 byte |
| Dokumen dirujuk DB | 5, hilang: 0 |
| Dokumen berubah saat backup | tidak |
| Checksum | `SHA256SUMS` + `documents.sha256` (per file) |
| Manifest | waktu, host, versi framework/PHP, driver, jumlah tabel, jumlah file, status |

**Bukti konsistensi database dan dokumen:** setiap `application_documents.file_path`
diperiksa keberadaannya terhadap storage; hasil 5/5 ditemukan. Setelah restore,
pemeriksaan yang sama diulang pada direktori hasil restore dan seluruhnya
ditemukan.

**Hasil restore test (terisolasi, database aktif tidak disentuh):**

| Tahap | Hasil |
|---|---|
| Verifikasi `SHA256SUMS` | PASS |
| Workspace terisolasi (`/tmp/pmb-restore-work/<ts>`) | PASS |
| Restore database + `integrity_check` | PASS (`ok`) |
| Ekstraksi dokumen | PASS |
| Checksum per file hasil restore | PASS (byte-identik) |
| Row count vs manifest | PASS (identik) |
| Referensi dokumen resolve | PASS (5/5) |
| Unduh tanpa autentikasi | PASS (401) |
| Unduh oleh pemilik | PASS (200) |
| Byte hasil unduhan vs checksum backup | PASS (identik) |
| Unduh oleh calon mahasiswa lain | PASS (404) |
| Token calon mahasiswa ke endpoint admin | PASS (403) |

**Keterbatasan yang diketahui:**

1. Backup dan restore diuji pada mesin yang sama; belum ada salinan off-site.
2. Arsip backup tidak dienkripsi (hanya permission `700`/`600`).
3. Konsistensi antara dump database dan arsip dokumen bersifat *best effort*:
   dump diambil lebih dulu, lalu arsip dokumen; perubahan file selama proses
   dideteksi dan dicatat (`documents_changed_during_backup`) tetapi tidak dapat
   dibatalkan secara transaksional. Tidak ada maintenance window otomatis.
4. Restore MySQL memerlukan akun administratif
   (`PMB_RESTORE_DB_ADMIN_USER` / `PMB_RESTORE_DB_ADMIN_PASSWORD`) untuk membuat
   database scratch. Tanpa itu skrip melaporkan `NOT RUN` dan tidak menyentuh
   database aktif.
5. Belum ada penjadwalan otomatis maupun retensi.
6. Permission Unix tidak berlaku pada mount proyek ini (fuseblk/NTFS). Perlindungan
   `.env` dan arsip backup karenanya bergantung pada kontrol akses drive, bukan
   pada mode file.

---

## 7. Hasil Pengujian

| Pengujian | Hasil | Bukti | Catatan |
|---|---|---|---|
| Docker Compose validation | PASS | `docker compose config --quiet` | Nilai rahasia disamarkan saat ditampilkan. |
| Database connection | PASS | `php artisan db:show` | MariaDB 11.8.9, port 3307. |
| Database persistence | PASS | Marker `survives-restart` bertahan setelah `docker compose restart db` dan setelah `down` + `up`; 18 tabel utuh | Tabel penanda dibuat lalu dihapus setelah uji. |
| Laravel tests (SQLite) | PASS | `Tests: 50 passed (216 assertions)` | Konfigurasi live. |
| Laravel tests (MariaDB) | PASS | `Tests: 50 passed (216 assertions)` | Database `pmb_itb_akiday_test` terisolasi. |
| Admin tests | PASS | `# tests 10 / # pass 10 / # fail 0` | `npm test`. |
| Admin typecheck | PASS | `tsc --noEmit` | Tanpa error. |
| Admin production build | PASS | `vite build` → 1.658 modul | `dist/` terbentuk. |
| Android compilation | PASS | `:app:compileDebugKotlin` (hanya warning deprecation) | — |
| Android unit tests | PASS | 42 test, 0 gagal | 2 di antaranya (`LiveBackendIntegrationTest`) berjalan dan lulus saat backend aktif; tanpa backend keduanya di-skip eksplisit. |
| Android APK build | PASS | `app-debug.apk` 70.034.860 byte | Debug build. |
| Android instrumented test | `NOT RUN` | `adb devices` kosong | Tidak ada emulator/perangkat pada lingkungan ini. |
| Private storage authorization | PASS | 403 untuk URL publik; 401 tanpa auth; 200 untuk pemilik; 404 untuk pemilik lain; 403 untuk endpoint admin | Diuji pada backend live dan pada hasil restore. |
| Upload format/size rejection | PASS | 2 test baru di `DocumentTest` | 422 `VALIDATION_ERROR`, tidak ada baris/file sisa. |
| Backup database | PASS | `20261028T234536Z`, 196.608 byte, 17 tabel, `integrity_check=ok` | — |
| Backup private storage | PASS | `documents.tar.gz`, 6 file, checksum per file | — |
| Restore test (SQLite) | PASS | 12 pemeriksaan PASS | Terisolasi di `/tmp`. |
| Restore test (MariaDB) | PASS | Dump diimpor ke `pmb_restore_verify_*`, row count identik, scratch DB dihapus | Dijalankan dengan akun admin lokal. |
| Integration smoke tests | PASS | 28/28 skenario | Mencakup login, profil, pendaftaran, unggah, submit, review, revisi, keputusan, riwayat status, otorisasi, dan penanganan error. |

Catatan: `migrate:fresh`, `db:wipe`, penghapusan volume, dan operasi destruktif
lain **tidak** dijalankan. Test suite memakai `RefreshDatabase` hanya pada
database uji terpisah.

---

## 8. Masalah dan Risiko

### Critical

Tidak ada.

### High

| # | Masalah | Status | Rencana |
|---|---|---|---|
| H-1 | Root workspace bukan repository Git: tidak ada riwayat, rollback, atau remote. | Terbuka | Putuskan inisialisasi repository dan remote; jangan dilakukan tanpa persetujuan karena menyangkut struktur proyek. |
| H-2 | Runtime aplikasi masih SQLite sementara arsitektur dan dokumen menetapkan MySQL/MariaDB. | Terbuka (NEEDS DECISION) | Siapkan prosedur cutover: backup SQLite, buat database MariaDB, impor data, uji, lalu ubah `.env`. Belum dijalankan karena mengubah sumber data aktif. |

### Medium

| # | Masalah | Status | Rencana |
|---|---|---|---|
| M-1 | Backup hanya pada mesin yang sama dan tidak dienkripsi. | Terbuka | Arahkan `PMB_BACKUP_ROOT` ke media terpisah dan tambahkan enkripsi arsip. |
| M-2 | Belum ada jadwal backup otomatis, retensi, atau notifikasi kegagalan. | Terbuka | Tambahkan cron/systemd timer serta kebijakan retensi. |
| M-3 | Konsistensi dump database dan arsip dokumen bersifat best effort. | Terdokumentasi | Gunakan maintenance window singkat saat backup untuk data produksi. |
| M-4 | Mount proyek `fuseblk` (NTFS) tidak menegakkan permission Unix, sehingga `.env` dan arsip backup tidak terlindungi oleh mode `600`/`700`. | Terbuka | Pindahkan `.env` dan `PMB_BACKUP_ROOT` ke filesystem yang menegakkan permission (ext4), atau enkripsi arsip backup. |

### Low

| # | Masalah | Status | Rencana |
|---|---|---|---|
| L-1 | Restore MySQL memerlukan kredensial administratif tambahan. | Terdokumentasi | Sudah dilaporkan sebagai `NOT RUN` bila tidak tersedia. |
| L-2 | Warning deprecation Compose (`Icons.Filled.ArrowBack`, `Modifier.menuAnchor`) saat kompilasi Android. | Terbuka | Perbaiki pada tahap pemeliharaan UI, bukan bagian Tahap 11. |
| L-3 | `APP_DEBUG=true` pada `.env` lokal. | Dapat diterima untuk lokal | Wajib `false` pada deployment yang dapat diakses publik. |

---

## 9. Keputusan yang Masih Diperlukan

1. **Cutover SQLite → MariaDB.** Perlu persetujuan karena mengubah sumber data
   aktif (10 user, 7 pendaftaran, 5 dokumen). Opsi: (a) tetap SQLite untuk
   pengembangan dan gunakan MariaDB hanya untuk staging/produksi, (b) migrasikan
   sekarang dengan backup penuh lebih dulu, (c) mulai ulang dari skema kosong
   untuk lingkungan demo.
2. **Versi database resmi.** MariaDB 11.8 dipilih karena klien lokal 11.8.5 dan
   driver `mariadb` tersedia di Laravel. Jika tim menetapkan MySQL 8.4, cukup
   ubah image dan `DB_CONNECTION`.
3. **Repository Git** dan strategi branch/remote.
4. **Lokasi backup final**, enkripsi, retensi, jadwal, serta target RPO/RTO.
5. **Akun database** untuk tiap lingkungan (pemisahan akun aplikasi dan admin).

---

## 10. Status Kriteria Penerimaan

| Kriteria | Status | Keterangan |
|---|---|---|
| Struktur repository telah diaudit | `VERIFIED` | Bagian 3. |
| Perubahan lokal pengguna tetap terjaga | `VERIFIED` | Tidak ada file pengguna ditimpa; `backend-api/.env` tidak diubah. |
| Docker Compose valid jika digunakan | `VERIFIED` | `docker compose config` valid. |
| Database dapat dijalankan dengan konfigurasi terdokumentasi | `VERIFIED` | MariaDB 11.8 healthy. |
| Data database menggunakan penyimpanan persisten | `VERIFIED` | Volume `pmb_mysql_data`, diuji restart dan recreate. |
| Laravel dapat terhubung ke database | `VERIFIED` | `db:show` + migrasi + 50 test di MariaDB. |
| Migration ditangani tanpa kehilangan data | `VERIFIED` | Migrasi hanya dijalankan pada database baru/kosong; SQLite live tidak disentuh. |
| Admin Website dapat dibangun dan dijalankan | `VERIFIED` | Build + test + typecheck + smoke test. |
| Android dapat dikompilasi dan dibuat APK-nya | `VERIFIED` | `assembleDebug` sukses. |
| Alamat API telah disesuaikan dengan lingkungan | `VERIFIED` | Debug `10.0.2.2`, release HTTPS, override runtime tersedia. |
| Private storage telah diperiksa dan dilindungi | `VERIFIED` | 403/401/200 sesuai aturan akses. |
| Backup database berhasil dibuat dan divalidasi | `VERIFIED` | `integrity_check=ok`, checksum lulus. |
| Backup dokumen berhasil dibuat dan divalidasi | `VERIFIED` | 6 file, checksum per file, jumlah entri cocok. |
| Restore diuji pada lingkungan terisolasi | `VERIFIED` | 12 pemeriksaan PASS (SQLite) + drill MySQL PASS. |
| Smoke test sistem dijalankan | `VERIFIED` | 28/28 skenario. |
| Test suite relevan dijalankan dan dicatat | `VERIFIED` | Bagian 7. |
| Dokumentasi deployment dan backup diperbarui | `VERIFIED` | Bagian 4.10. |
| Tidak ada secret yang dimasukkan ke source control | `VERIFIED` | Tidak ada repository Git; kredensial hanya di `.env` (600, di-ignore). |
| Semua keterbatasan dan keputusan dicatat | `VERIFIED` | Bagian 8 dan 9. |
| Android instrumented test | `NOT RUN` | Tidak ada perangkat/emulator. |
| Runtime aplikasi memakai MySQL/MariaDB | `NEEDS DECISION` | Masih SQLite (H-2). |

**Status akhir: `ACCEPTED WITH LIMITATIONS`.** Seluruh kriteria inti terpenuhi
dan diverifikasi; dua butir di atas (instrumented test dan cutover database)
tidak dapat diselesaikan pada lingkungan ini tanpa keputusan pemilik proyek.

---

## 11. Rekomendasi Tahap 12

1. **Tetapkan keputusan cutover database lebih dulu.** Release notes dan
   dokumentasi final harus menyebut database yang benar-benar dipakai; saat ini
   dokumen menyebut MySQL sedangkan runtime memakai SQLite.
2. **Buat repository Git sebelum pekerjaan rilis**, dengan `.gitignore` root yang
   sudah tersedia, lalu tandai baseline rilis.
3. **Finalkan `APP_ENV`/`APP_DEBUG`** dan pastikan `APP_DEBUG=false` pada
   konfigurasi yang dapat diakses publik.
4. **Jadwalkan backup** (cron/timer), tetapkan retensi, dan arahkan salinan ke
   media terpisah; dokumentasikan prosedur restore yang sudah teruji ini.
5. **Jalankan instrumented test Android** pada perangkat/emulator nyata sebelum
   rilis.
6. **Perbarui `Test Results.md`** dengan angka aktual dari dokumen ini.
7. **Putuskan cakupan rilis**: catat bahwa smoke test memakai data uji lokal dan
   bukan data pendaftar sebenarnya.

Tahap 12 belum dimulai.
