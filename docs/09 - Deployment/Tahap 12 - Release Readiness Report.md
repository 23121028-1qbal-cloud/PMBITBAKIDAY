# Tahap 12 — Release Readiness Report

## 1. Informasi Dokumen

|Item|Keterangan|
|---|---|
|Proyek|PMB ITB Akiday|
|Tahap|12 — Release Readiness dan Finalisasi|
|Tanggal audit & eksekusi|29 Oktober 2026|
|Lingkungan|Lokal: PHP 8.4.24, Node 20.20.2, JDK 23 (build target 17), MySQL 8.4.13 di Docker|
|Tahap sebelumnya|`MySQL Cutover & Git Baseline Report.md` — `ACCEPTED WITH LIMITATIONS`|
|Status keseluruhan|**`ACCEPTED WITH LIMITATIONS`** (lihat bagian 12)|
|Kesiapan rilis publik|**Belum siap** — instrumented test dan eksekusi CI masih `NOT RUN`|

Dokumen ini memisahkan empat hal yang sering tercampur:

- **Fakta terverifikasi** — diambil dari repository dan eksekusi nyata hari ini.
- **Perubahan yang dilakukan** — hanya yang benar-benar ditulis ke repository.
- **Rekomendasi** — usulan, belum diterapkan.
- **Belum diputuskan** — membutuhkan keputusan pengguna.

---

## 2. Kondisi Awal yang Diverifikasi

|Aspek|Kondisi aktual saat audit|Bukti|
|---|---|---|
|Branch & commit|`main`, `98eab07` (baseline 260 file) dan `f33beb3` (dokumentasi)|`git log --oneline`|
|File terlacak|262 file sebelum Tahap 12|`git ls-files \| wc -l`|
|Remote|Belum ada|`git remote -v` kosong|
|Repository bersarang|Tidak ada|`find . -mindepth 2 -name .git` kosong|
|Working tree|`M README.md` + runbook baru (hasil pekerjaan sesi sebelumnya, belum dikomit)|`git status --short`|
|CI|Belum ada workflow apa pun|`.github`, `.gitlab-ci.yml` tidak ditemukan|
|Backend|Laravel 13.39, PHP 8.4.24, `DB_CONNECTION=mysql`|`php artisan db:show` → MySQL 8.4.13, 17 tabel|
|Database lokal|`pmb_itb_akiday` (live) + `pmb_itb_akiday_test` (uji)|`information_schema`|
|Dokumen privat|9 file di `backend-api/storage/app/private/`, 8 dirujuk database|`scripts/backup.sh`|
|Android SDK|`/home/federick/Android/Sdk`: emulator + platform-tools + platform 37 + build-tools 37.0.0|`ls`|
|Emulator/perangkat|Tidak ada AVD, tidak ada system image, tidak ada `cmdline-tools`, `adb devices` kosong|audit Tahap E|

---

## 3. File yang Berubah

### 3.1 Kode dan konfigurasi

|File|Perubahan|Alasan|
|---|---|---|
|`admin-web/test/test-credentials.mjs` (baru)|Helper yang membaca kredensial dari environment dan gagal dengan pesan jelas bila kosong|Menghapus kredensial staf dari repository|
|`admin-web/test/http-integration-verify.mjs`|Memakai helper; email pembanding diambil dari variabel|Tidak ada nilai default|
|`admin-web/test/system-integration-verify.mjs`|Memakai helper; kata sandi akun uji dibuat acak|Tidak ada kata sandi tetap|
|`admin-web/test/product-test-suite.mjs`|Akun staf dari environment; kata sandi akun uji acak; probe kata sandi salah memakai turunan nilai acak|Tidak ada kata sandi tetap|
|`android-app/.../LiveBackendIntegrationTest.kt`|Kredensial dari env/system property; opt-in `PMB_LIVE_TESTS=1`; gagal jelas bila variabel tidak ada|Sama|
|`android-app/app/build.gradle.kts`|Meneruskan `PMB_LIVE_TESTS`/`PMB_TEST_ADMIN_*` ke JVM test|Daemon Gradle bisa lebih lama dari shell|
|`backend-api/database/seeders/DatabaseSeeder.php`|Seeder staf (admin, panitia, pimpinan) berbasis `SEED_*`; akun tanpa kata sandi dilewati|Rotasi kredensial tanpa menaruh rahasia di repo|
|`backend-api/.env.example`|`DB_CONNECTION=mysql` + `DB_HOST/PORT/DATABASE/USERNAME/PASSWORD`; menambah template `SEED_*` kosong|Template sebelumnya masih SQLite (tidak konsisten dengan keputusan MySQL)|
|`.github/workflows/ci.yml` (baru)|3 job: backend (MySQL 8.4), admin web, Android|CI belum ada|
|`README.md`|Bagian **Pengujian** (perintah + variabel kredensial), tautan runbook & laporan Tahap 12|Dokumentasi rilis|
|`docs/09 - Deployment/Runbook - Menjalankan Server.md` (baru)|Panduan menyalakan/memeriksa/mematikan server|Permintaan pengguna pada sesi sebelumnya|
|`docs/08 - Testing/Test Results.md`|Bagian 13: hasil pengujian Tahap 12|Bukti aktual|
|`docs/09 - Deployment/Backup & Recovery.md`|Bagian 7B: status backup setelah cutover + daftar keputusan|Kebijakan backup|
|`docs/10 - User Documentation/Release Notes.md`|Bagian 4A status implementasi, pembaruan §7, riwayat dokumen 1.1|Release notes|

### 3.2 Yang **tidak** diubah

- Skema database, migration, dan API contract.
- Middleware otorisasi, validasi, dan aturan bisnis.
- Dokumen privat, backup, `.env`, dan volume Docker.
- Riwayat Git (tidak ada penulisan ulang riwayat).

---

## 4. Temuan Audit Git dan Keamanan

|No|Temuan|Tingkat|Bukti|
|---|---|---|---|
|G-1|Tidak ada `.env`, database SQLite, dump SQL, dokumen privat, atau arsip backup yang pernah masuk riwayat|aman|`git log --all --diff-filter=A --name-only` + filter pola sensitif → kosong|
|G-2|File terlacak yang berbau rahasia hanya `.env.example` (root dan backend), isinya tanpa nilai|aman|`git ls-files \| grep -E '\.env\|secret\|\.pem'`|
|G-3|**Kredensial staf tetap** tertulis di 4 file kode: `admin@example.com`, `panitia@example.com`, `pimpinan@example.com` dengan satu kata sandi yang sama|**tinggi**|`git grep` pada kedua commit|
|G-4|Kata sandi tersebut **benar-benar valid** di database lokal saat audit (bukan dummy)|**tinggi**|`POST /api/v1/auth/login` → `200` untuk ketiga akun; setelah rotasi → `401`|
|G-5|String kata sandi lama tetap ada di riwayat commit `98eab07` dan `f33beb3`|sedang|`git grep <commit>`|
|G-6|`backend-api/.env.example` masih `DB_CONNECTION=sqlite`|sedang (fungsional)|isi file sebelum perubahan|
|G-7|Tidak ada remote, sehingga belum ada risiko publikasi tak sengaja|aman|`git remote -v`|
|G-8|`.gitignore` root dan subproyek menutup `.env`, `*.sqlite`, dokumen privat, `vendor/`, `node_modules/`, `dist/`, `build/`, `.gradle/`, `.idea/`|aman|`git check-ignore` pada path sensitif (Tahap 11) + audit Tahap 12|

**Tindakan atas G-5:** riwayat **tidak** ditulis ulang (sesuai aturan). Karena
kata sandi sudah dirotasi, nilai lama tidak dapat dipakai lagi. Jika repository
nanti dibagikan atau di-push, pertimbangkan pembersihan riwayat
(`git filter-repo`) — itu operasi berisiko dan **menunggu persetujuan pengguna**.

---

## 5. Status Penanganan Kredensial Fixture

|Hal|Kondisi|
|---|---|
|Kredensial di kode|Sudah tidak ada. Dibaca dari `PMB_TEST_*` (admin web) dan `PMB_TEST_ADMIN_*` + `PMB_LIVE_TESTS` (Android)|
|Nilai default|Tidak ada. Variabel kosong → skrip berhenti dengan pesan yang menyebut nama variabel|
|Akun uji yang dibuat skrip|Kata sandi diacak per eksekusi (42 karakter), tidak disimpan|
|Fixture backend (PHPUnit)|Email dummy (`mahasiswa@example.com`) dengan kata sandi dummy di database in-memory/uji; tidak berlaku di lingkungan lain — **tidak diubah**|
|Seeder|`SEED_ADMIN_*`, `SEED_PANITIA_*`, `SEED_PIMPINAN_*`; akun dilewati bila kata sandi kosong|
|Rotasi kata sandi lokal|**Dilakukan**: nilai acak baru hanya ada di `backend-api/.env` (tidak dilacak). Kata sandi lama → `401`, baru → `200` pada ketiga akun|
|Test setelah perubahan|Admin web: helper lolos uji tanpa env (gagal jelas) dan dengan env; `product-test-suite.mjs` 20/20 lulus. Android: `PMB_LIVE_TESTS=1` → 42/42 lulus, 0 dilewati|

---

## 6. Status CI

|Aspek|Kondisi|
|---|---|
|Workflow|`.github/workflows/ci.yml` — `push` ke `main`, `pull_request`, `workflow_dispatch`|
|Job backend|`ubuntu-latest`, PHP 8.4 + `pdo_mysql`, service `mysql:8.4.13` (health check), `composer install` dari lockfile, `php artisan test` dengan `DB_CONNECTION=mysql`|
|Job admin web|Node 20 (cache npm), `npm ci`, `typecheck`, `npm test`, `npm run build`|
|Job android|JDK 17 temurin, `android-actions/setup-android`, `./gradlew :app:testDebugUnitTest :app:assembleDebug`, unggah APK|
|Rahasia|Tidak ada secret yang dipakai. Kredensial MySQL di dalam workflow hanya berlaku di container job tersebut (`ci-app-only`, `ci-root-only`) dan bukan kredensial proyek|
|Validasi|YAML berhasil di-parse (`yaml.safe_load`), 3 job, 6 langkah per job|
|Eksekusi|**`NOT RUN`** — repository belum punya remote, workflow belum pernah dijalankan|
|Cakupan yang sengaja tidak masuk CI|Skrip integrasi `admin-web/test/*.mjs` dan live test Android: butuh database terisi gelombang/program studi dan akun staf nyata. Belum ada seeder data referensi, sehingga tidak dapat dijalankan stabil → `BLOCKED`|

---

## 7. Hasil Pengujian Aktual (29 Oktober 2026)

|Area|Perintah|Hasil|Status|
|---|---|---|---|
|Backend pada MySQL 8.4.13|`DB_CONNECTION=mysql DB_DATABASE=pmb_itb_akiday_test … php artisan test`|50 lulus, 216 assertion, 0 gagal, 7,73 dtk|**PASS**|
|Kontrol bahwa env DB dipakai|perintah sama, `DB_PORT=3399`|4 test gagal `Connection refused`|**PASS** (bukti, bukan kegagalan produk)|
|Admin typecheck|`npm run typecheck`|tanpa error|**PASS**|
|Admin unit test|`npm test`|10 lulus, 0 gagal|**PASS**|
|Admin build|`npm run build`|`✓ built in 11.22s`|**PASS**|
|Integrasi produk (manual)|`node test/product-test-suite.mjs` + `PMB_TEST_PANITIA_*`/`PMB_TEST_PIMPINAN_*`|20/20 (TR-001…TR-020)|**PASS**|
|Android unit test (default)|`./gradlew :app:testDebugUnitTest`|42 test, 0 gagal, **2 skipped** (live test)|**PASS WITH SKIPS**|
|Android unit test (live)|`PMB_LIVE_TESTS=1 PMB_TEST_ADMIN_* … ./gradlew :app:testDebugUnitTest`|42 test, 0 gagal, 0 skipped|**PASS**|
|Android build|`./gradlew :app:assembleDebug`|`BUILD SUCCESSFUL`, APK 70.034.860 byte|**PASS**|
|Android instrumented|`./gradlew :app:connectedDebugAndroidTest`|tidak dapat dijalankan|**NOT RUN**|
|CI workflow|—|belum dieksekusi|**NOT RUN**|
|Backup|`scripts/backup.sh`|`20261029T003512Z`, 17 tabel, 9 file, 8 referensi, status `complete`|**PASS**|
|Restore terisolasi|`scripts/restore-verify.sh`|12/12 pemeriksaan lulus, database scratch dihapus|**PASS**|

Angka di atas adalah hasil eksekusi hari ini. Angka Tahap 9–11 pada
`Test Results.md` bagian 1–12 tidak diubah.

Catatan cakupan: yang dijalankan ulang pada Tahap 12 adalah
`product-test-suite.mjs` (membuat akun dan pendaftarannya sendiri). Dua skrip
lain — `http-integration-verify.mjs` dan `system-integration-verify.mjs` —
**tidak** dijalankan ulang karena keduanya mengubah status pendaftaran yang
sudah ada di database dev; kredensialnya sudah dimigrasikan ke pola environment
yang sama dan lolos pemeriksaan sintaks (`node --check`), tetapi hasil
fungsionalnya tidak diklaim ulang di sini.

---

## 8. Status Backup dan Restore

|Aspek|Kondisi|Status|
|---|---|---|
|Backup terakhir|`../pmb_backups/20261029T003512Z`, dump 40.335 byte|**VERIFIED**|
|Cakupan|Database MySQL + private storage + manifest + checksum per file|**VERIFIED**|
|Konsistensi DB ↔ dokumen|`documents_referenced=8`, `documents_referenced_missing=0`, `documents_changed_during_backup=no`|**VERIFIED**|
|Restore terisolasi|Database scratch + direktori terpisah, 12 pemeriksaan termasuk 401/200/404/403|**PASS**|
|Enkripsi arsip|Belum diterapkan|**NEEDS DECISION**|
|Jadwal otomatis|Belum ada (dijalankan manual)|**NEEDS DECISION**|
|Salinan off-site|Belum ada|**NEEDS DECISION**|
|Retensi & RPO/RTO|Masih usulan dokumen (7/4/3; 24 jam / 8 jam)|**NEEDS DECISION**|
|Permission arsip|Mount `fuseblk` tidak menegakkan `chmod`; manifest mencatat `filesystem_enforces_permissions=no`|**risiko tercatat**|
|Rollback ke SQLite|Terdokumentasi, sadar bahwa data setelah cutover tidak ikut kembali|**VERIFIED (dokumentasi)**|

---

## 9. Status Android Instrumented Test

**Status: `NOT RUN`.**

Hasil pemeriksaan lingkungan:

|Yang diperiksa|Hasil|
|---|---|
|`app/src/androidTest/.../ExampleInstrumentedTest.kt`|Ada, 1 `@Test`|
|`testInstrumentationRunner`|`androidx.test.runner.AndroidJUnitRunner` (terkonfigurasi)|
|AVD|0|
|System image|0 (`Sdk/system-images` kosong)|
|`sdkmanager` / `cmdline-tools`|Tidak terpasang|
|Perangkat terhubung|Tidak ada (`adb devices` kosong)|
|`/dev/kvm`|Ada|

Perintah yang perlu dijalankan setelah emulator/perangkat tersedia:

```bash
# dengan emulator aktif atau perangkat USB (debugging aktif)
cd android-app
./gradlew :app:connectedDebugAndroidTest
```

Untuk membuat emulator terlebih dahulu (mengunduh ±1 GB, di luar repository):

```bash
sdkmanager "platform-tools" "platforms;android-37" \
           "system-images;android-36;google_apis;x86_64"
avdmanager create avd -n pmb_test -k "system-images;android-36;google_apis;x86_64"
emulator -avd pmb_test &
```

Test yang ada saat ini hanya `ExampleInstrumentedTest` (pemeriksaan package),
bukan uji alur PMB. Menambah instrumented test fungsional adalah pekerjaan
terpisah dari Tahap 12 dan **belum** dilakukan.

---

## 10. Status Repository Git

|Aspek|Kondisi|
|---|---|
|Root repository|Benar (root proyek, bukan subfolder)|
|Isi|`android-app/`, `backend-api/`, `admin-web/`, `docs/`, `scripts/`, `docker-compose.yml`, `README.md`, `.github/`|
|Commit Tahap 12|`8cd5dbe` — 14 file, 740 penyisipan, 32 penghapusan|
|File terlacak|266 (262 awal + `ci.yml` + `test-credentials.mjs` + `Runbook` + laporan ini)|
|File rahasia terlacak|Tidak ada (`.env`, database, dokumen, backup, `vendor/`, `node_modules/`, `dist/`, build)|
|Remote|Belum ada (sesuai instruksi)|
|Working tree|Bersih setelah laporan ini dikomit|
|Riwayat|Tidak ditulis ulang|

---

## 11. Risiko yang Masih Tersisa

### Tinggi

1. **CI belum pernah dieksekusi.** Syntax YAML valid, tetapi keberhasilan job
   (terutama ketersediaan `platforms;android-37` di runner dan perilaku
   `php artisan test` pada MySQL) baru terbukti saat push pertama.
2. **Instrumented test `NOT RUN`.** Satu-satunya instrumented test pun baru
   memeriksa nama package; alur PMB di perangkat nyata belum diuji otomatis.

### Sedang

3. **Tidak ada remote.** Seluruh riwayat hanya ada di satu mesin; kehilangan
   disk berarti kehilangan riwayat.
4. **Riwayat Git memuat kata sandi lama** (sudah tidak valid). Aman selama repo
   tidak dibagikan, tetapi tetap jejak yang tidak diinginkan.
5. **Backup tidak terenkripsi, tanpa jadwal, dan berada di mesin yang sama.**
   RPO/RTO belum ditetapkan.
6. **Data referensi tidak punya seeder**, sehingga skrip integrasi tidak dapat
   dijalankan di CI (`BLOCKED`).
7. **Database dev terisi akun uji** dari eksekusi berulang: per akhir Tahap 12
   tercatat 14 user, 11 pendaftaran, dan 8 dokumen (sebelumnya 10/7/5). Akun
   uji seperti `calon_a_*`, `mhs_integ_*` bukan data pendaftar nyata, tetapi
   mengotori demo. Keputusan pembersihan ada pada D-6.
8. **`APP_DEBUG=true` dan `APP_ENV=local`** pada `.env` lokal.

### Rendah

9. Mount `fuseblk` tidak menegakkan permission file.
10. Versi toolchain lokal (JDK 23) berbeda dari target build (17) dan dari CI.
11. `README`/dokumen menyebut angka pengujian yang harus terus disinkronkan
    secara manual.

---

## 12. Keputusan yang Masih Dibutuhkan Pengguna

|No|Keputusan|Dampak bila ditunda|
|---|---|---|
|D-1|URL remote dan izin `git push`|CI tidak pernah berjalan; riwayat hanya di satu mesin|
|D-2|Jadwal, retensi, lokasi off-site, enkripsi, penanggung jawab backup|Tidak ada jaminan pemulihan bencana|
|D-3|RPO/RTO resmi|Target pemulihan tidak terukur|
|D-4|Perlu/tidak menulis ulang riwayat Git untuk menghapus kata sandi lama|Jejak lama tetap ada|
|D-5|Nilai produksi `APP_ENV`, `APP_DEBUG`, `APP_URL`, `SESSION_SECURE_COOKIE`|Konfigurasi produksi belum final|
|D-6|Hapus/tidak akun uji dan database `pmb_itb_akiday_test` di lingkungan lokal|Data demo bercampur data uji|
|D-7|Perangkat/emulator untuk instrumented test|Kriteria rilis tidak lengkap|
|D-8|Nomor versi dan status rilis pada Release Notes|Release notes masih `Draft`|

---

## 13. Rekomendasi untuk Langkah Berikutnya

1. **Push ke remote setelah URL disetujui**, lalu jalankan workflow sekali secara
   manual (`workflow_dispatch`) dan perbaiki kegagalan yang muncul. Selama belum
   dijalankan, status CI tetap `NOT RUN`.
2. **Jalankan instrumented test** pada satu perangkat/emulator; tambahkan minimal
   satu instrumented test alur login.
3. **Buat seeder data referensi** (gelombang + program studi) agar skrip
   integrasi `admin-web/test/*.mjs` dapat dijalankan di CI secara stabil.
4. **Terapkan kebijakan backup** (jadwal, enkripsi, off-site, retensi) dan catat
   hasilnya di `Backup & Recovery.md` bagian 7B.
5. **Finalkan konfigurasi produksi** (`APP_ENV=production`, `APP_DEBUG=false`,
   HTTPS, cookie aman) dan dokumentasikan di `Environment Configuration.md`.
6. **Bersihkan data uji** pada database dev bila diinginkan, tanpa menyentuh data
   pendaftar nyata.
7. **Perbarui Release Notes** menjadi versi dan status rilis yang disetujui
   setelah butir 1–5 selesai.

---

## 14. Kesimpulan

Perubahan pada Tahap 12 sudah dijalankan dan diverifikasi: kredensial staf tidak
lagi tersimpan di repository, kata sandi lokal yang pernah bocor sudah dirotasi,
CI sudah disiapkan, dan seluruh pengujian yang tersedia lulus pada MySQL 8.4.13.

Proyek **belum** dapat dinyatakan siap rilis publik karena dua hal: workflow CI
belum pernah dieksekusi dan instrumented test Android belum dijalankan. Keduanya
berstatus `NOT RUN`, bukan lulus. Tahap 13 belum dimulai dan tidak ada deployment
publik yang dilakukan.
