## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Dokumen|MySQL Cutover & Git Baseline Report|
|Nama Proyek|PMB ITB Akiday|
|Versi Dokumen|1.0|
|Status|**ACCEPTED WITH LIMITATIONS**|
|Tanggal|29 Oktober 2026|
|Lingkungan|Workstation lokal (Linux), tanpa emulator/perangkat Android|
|Referensi|[[Tahap 11 - Local Deployment & Backup Report]], [[Deployment Guide]], [[Backup & Recovery]], [[Environment Configuration]]|

> Semua angka dan status di bawah berasal dari eksekusi nyata pada lingkungan
> ini. Yang tidak dapat dijalankan ditandai `NOT RUN` beserta alasannya. Tidak
> ada password, token, atau isi `.env` yang dicatat di dokumen ini.

---

## 2. Kondisi Awal

Diverifikasi sebelum perubahan:

- Root proyek **bukan** repository Git (`.git` tidak tersedia).
- `backend-api/.env` memakai `DB_CONNECTION=sqlite`; `DB_HOST`, `DB_PORT`,
  `DB_DATABASE`, `DB_USERNAME`, `DB_PASSWORD` masih berupa komentar.
- `docker-compose.yml` menjalankan **MariaDB 11.8** (container
  `pmb-itb-akiday-db`, port `127.0.0.1:3307`, volume `pmb_mysql_data`).
- Data aplikasi di SQLite: **17 tabel**; users 10, applicant_profiles 6,
  applications 7, application_documents 5, application_status_histories 36,
  personal_access_tokens 5, roles 4, admission_waves 2, study_programs 2,
  sessions 6, cache 2. `PRAGMA integrity_check` = `ok`, `foreign_key_check` =
  tidak ada pelanggaran.
- Dokumen privat: 5 file PDF di
  `backend-api/storage/app/private/documents/<application_id>/`, seluruhnya
  cocok dengan 5 baris `application_documents`.
- Android di `android-app/`, admin web di `admin-web/`, backend Laravel 13.39.0
  dengan PHP 8.4.24.
- Tidak ada config cache di `bootstrap/cache/`.

### Backup sebelum perubahan

Backup dibuat dan diverifikasi **sebelum** cutover:

|Item|Hasil|
|---|---|
|Backup id|`20261028T234931Z`|
|Dump SQLite|196.608 byte, 17 tabel, `integrity_check=ok`|
|Dokumen|6 file (5 dokumen + `.gitignore`), checksum per file|
|Referensi dokumen|5 dirujuk, 0 hilang|
|Verifikasi restore terisolasi|12/12 pemeriksaan PASS|

Backup lain dari Tahap 11 (`20261028T233226Z`, `20261028T234031Z`,
`20261028T234536Z`) tetap tersimpan dan tidak ditimpa.

---

## 3. Keputusan Implementasi

1. **MySQL resmi, versi dipin.** Image `mysql:8.4.13` (LTS). Tag `latest`
   dihindari; patch version ditulis eksplisit agar versi server tidak berubah
   tanpa peninjauan. Kandidat 8.4 dipilih karena merupakan LTS MySQL saat ini
   dan didukung Laravel 13 serta PHP 8.4 (`pdo_mysql`/`mysqlnd`).
2. **Volume baru, volume lama dipertahankan.** MySQL memakai
   `pmb_mysql8_data`. Volume MariaDB `pmb_mysql_data` **tidak dihapus** dan tidak
   dipakai ulang; instance MariaDB dipindahkan ke profile `legacy-mariadb`.
3. **Skema dibuat oleh migration proyek**, bukan konversi DDL SQLite. Ini
   memastikan tipe kolom, index, dan foreign key sesuai yang didefinisikan
   Laravel untuk MySQL.
4. **Data disalin apa adanya** (bukan seeder, bukan regenerasi), mempertahankan
   primary key dan relasi, lalu diverifikasi per tabel.
5. **Cutover hanya mengubah `backend-api/.env`**; kontrak API dan aturan bisnis
   tidak disentuh.
6. **Repository Git tunggal di root**, dengan `.gitignore` yang mengecualikan
   rahasia, database lokal, dokumen pendaftar, dependency, dan hasil build.

---

## 4. File yang Berubah

|File|Perubahan|Dampak|Verifikasi|
|---|---|---|---|
|`docker-compose.yml`|Service `db` menjadi `mysql:8.4.13`, volume `pmb_mysql8_data`, port `127.0.0.1:3306`, healthcheck `mysqladmin ping`; MariaDB dipindah ke service `legacy-mariadb` (profile, volume lama)|Database aplikasi menjadi MySQL; data MariaDB tetap dapat diakses|`docker compose config --quiet` valid; container `healthy`|
|`.env.example`|Variabel `MYSQL_*` untuk MySQL; variabel MariaDB lama diganti prefiks `LEGACY_DB_*`|Tidak ada nilai rahasia di template|Nilai password/root kosong|
|`.env` (tidak dilacak)|Ditambah `MYSQL_*` dengan kata sandi acak|Menjadi sumber kredensial Compose|`docker compose config` valid|
|`backend-api/.env` (tidak dilacak)|`DB_CONNECTION=mysql`, `DB_HOST/PORT/DATABASE/USERNAME/PASSWORD` diisi|Aplikasi memakai MySQL|`db:show` + smoke test|
|`scripts/migrate-sqlite-to-mysql.php`|Skrip baru: salin data SQLite → MySQL, menolak target kosong/salah|Alat migrasi sekali pakai|Dijalankan, 17 tabel OK|
|`admin-web/.gitignore`|Aturan `node_modules/`, `dist/`, `.env*`|Mencegah artefak ikut Git|Ditinjau pada `git add -n`|
|`.gitignore` (root)|Ditambah `.kotlin/`|Cache Kotlin tidak dilacak|Ditinjau pada `git add -n`|
|`README.md`|Quickstart: database, backend, admin, Android, backup, Git|Dokumentasi awal repository|Ditinjau manual|
|Dokumen `docs/09`, `docs/08`|Deployment Guide, Backup & Recovery, Environment Configuration, Test Results, addendum laporan Tahap 11|Dokumentasi sesuai kondisi aktual|Ditinjau manual|

Catatan: perubahan `.env` tidak dapat dilacak Git secara sengaja (berisi
kredensial). Nilai yang dipakai didokumentasikan sebagai nama variabel saja.

---

## 5. Versi MySQL

|Atribut|Nilai|
|---|---|
|Image|`mysql:8.4.13` (resmi)|
|Versi server|`8.4.13` (MySQL Community Server)|
|Port host : container|`127.0.0.1:3306` : `3306`|
|Character set / collation|`utf8mb4` / `utf8mb4_unicode_ci` (sesuai `config/database.php`)|
|Driver Laravel|`mysql`|
|Container|`pmb-mysql` (healthy)|
|Volume|`pmb_mysql8_data`|
|Database|`pmb_itb_akiday`, plus `pmb_itb_akiday_test` untuk pengujian terisolasi|

---

## 6. Hasil Migrasi Data

Prosedur: backup terverifikasi → `php artisan migrate` pada MySQL kosong →
`scripts/migrate-sqlite-to-mysql.php` → verifikasi.

Skrip migrasi menolak berjalan bila target masih SQLite (diuji: keluar dengan
kode 4) dan bila target sudah berisi baris. Foreign key check dinonaktifkan
hanya selama proses salin, lalu diaktifkan kembali. Tabel `migrations` tidak
disalin melainkan dibandingkan (11 baris di sumber dan target, identik).

### Perbandingan jumlah tabel dan record

|Tabel|SQLite (sumber)|MySQL (target)|Status|
|---|---|---|---|
|admission_waves|2|2|OK|
|applicant_profiles|6|6|OK|
|application_documents|5|5|OK|
|application_status_histories|36|36|OK|
|applications|7|7|OK|
|cache|2|2|OK|
|cache_locks|0|0|OK|
|failed_jobs|0|0|OK|
|job_batches|0|0|OK|
|jobs|0|0|OK|
|migrations|11|11|OK|
|password_reset_tokens|0|0|OK|
|personal_access_tokens|5|5|OK|
|roles|4|4|OK|
|sessions|6|6|OK|
|study_programs|2|2|OK|
|users|10|10|OK|
|**Total**|**17 tabel**|**17 tabel**|**semua cocok**|

### Verifikasi nilai, bukan hanya jumlah

Setiap baris pada 17 tabel dibandingkan kolom demi kolom antara SQLite dan
MySQL (96 baris dibandingkan pada saat migrasi). Hasil:

- 16 tabel **identik** (nilai, `NULL`, dan primary key).
- 1 tabel berbeda pada satu kolom: `applicant_profiles.birth_date` —
  SQLite menyimpan `2005-12-04 00:00:00` (afinitas datetime), MySQL menyimpan
  `2005-12-04` (`DATE`).

Kolom tersebut di-cast `'date'` pada model dan API selalu menyerialisasikannya
dengan `->toDateString()` (`app/Http/Controllers/Api/ProfileController.php`,
`AdminApplicationController.php`), sehingga **respons API tidak berubah**
(`YYYY-MM-DD`). Test `ProfileTest` menegaskan `data.birth_date = '2007-05-10'`
dan lulus pada kedua engine. Ini perbedaan representasi penyimpanan, bukan
perubahan kontrak.

Verifikasi tambahan:

- Foreign key: `applicant_profiles→users`, `applications→applicant_profiles/
  admission_waves/study_programs`, `application_documents→applications`,
  `application_status_histories→applications/users`, `users→roles` — tidak ada
  baris yatim.
- Indeks unik ada dan tidak dilanggar: `users.email`, `roles.name`,
  `study_programs.code`, `applications.registration_number`,
  `applications(applicant_id, wave_id)`, `personal_access_tokens.token`,
  `applicant_profiles.user_id`.
- Referensi dokumen: 7 baris `application_documents` pada MySQL, seluruh file
  tersedia di private storage (0 hilang).
- File SQLite sumber **tidak diubah**: tetap 10 users, 7 applications,
  5 documents, 17 tabel.

---

## 7. Bukti Laravel Benar-Benar Memakai MySQL

1. `php artisan db:show` → `MySQL 8.4.13`, connection `mysql`, host `127.0.0.1`,
   port `3306`, database `pmb_itb_akiday`, 17 tabel.
2. `php artisan migrate:status` → seluruh 11 migration `Ran` (batch 1).
3. Bukti tulis: dua kali menjalankan suite integrasi (yang mendaftarkan akun
   calon mahasiswa, membuat pendaftaran, dan mengunggah dokumen) menaikkan
   hitungan **hanya** di MySQL, sementara SQLite tetap:

| | Sebelum suite | Sesudah suite |
|---|---|---|
|MySQL users / applications / documents|10 / 7 / 5|12 / 9 / 7|
|SQLite users / applications / documents|10 / 7 / 5|10 / 7 / 5|

4. Tidak ada config cache (`bootstrap/cache/config.php` tidak ada) dan
   `php artisan config:clear` dijalankan setelah perubahan `.env`.

---

## 8. Hasil Seluruh Test Suite

|Pengujian|Hasil|Bukti|
|---|---|---|
|Backend pada MySQL (isolated DB `pmb_itb_akiday_test`)|**Pass**|50 test, 216 assertion|
|Backend pada SQLite (baseline sebelum cutover)|**Pass**|50 test, 216 assertion|
|System integration 28 skenario (backend live, MySQL)|**Pass**|28/28, 100%|
|Admin web unit test|**Pass**|10 test, 0 gagal|
|Admin web typecheck|**Pass**|`tsc --noEmit`|
|Admin web production build|**Pass**|Vite 1.658 modul, `dist/` terbentuk|
|Android kompilasi Kotlin|**Pass**|`compileDebugKotlin`|
|Android unit test (backend live)|**Pass**|42 test, 0 gagal, 0 di-skip|
|Android APK debug|**Pass**|70.034.860 byte|
|Android instrumented test|**NOT RUN**|Tidak ada emulator/perangkat (`adb devices` kosong)|
|Backup MySQL + private storage|**Pass**|`database.sql` 34.867 byte, 8 file, status `complete`|
|Restore terisolasi pada MySQL|**Pass**|12 pemeriksaan, termasuk 401/200/404/403 pada dokumen|

Cakupan 28 skenario integrasi mencakup login session admin + CSRF, registrasi
dan login Bearer Android, profil, gelombang/prodi, draft pendaftaran, unggah
dokumen, submit, konflik status 409, daftar/detail admin, unduh dokumen privat,
review, permintaan revisi, resubmit, keputusan penerimaan, riwayat status, serta
penanganan 401/403/409/422.

Tidak ada test yang diubah atau dilemahkan agar lulus.

---

## 9. Status Docker dan Volume

|Item|Status|
|---|---|
|`pmb-mysql` (`mysql:8.4.13`)|**Up (healthy)**, `127.0.0.1:3306->3306/tcp`|
|Volume `pmb_mysql8_data`|Aktif, digunakan MySQL|
|Volume `pmb_mysql_data`|**Dipertahankan** (152,4 MB, berisi `pmb_itb_akiday` dan `pmb_itb_akiday_test` MariaDB)|
|Container `pmb-itb-akiday-db` (MariaDB)|Tidak berjalan; definisi tetap ada pada profile `legacy-mariadb`|
|`docker compose down -v`|**Tidak dijalankan**|

Catatan: saat `docker compose up -d db` pertama kali dijalankan, Compose
membuat ulang container MariaDB lama karena definisinya berubah, lalu container
tersebut tidak berjalan (profile tidak aktif). **Volume-nya tidak tersentuh** —
diperiksa langsung: 152,4 MB, direktori `pmb_itb_akiday`, `pmb_itb_akiday_test`,
`mysql`, dan `sys` masih ada. Data MariaDB tetap dapat diakses dengan
`docker compose --profile legacy-mariadb up -d legacy-mariadb`.

---

## 10. Status Backup dan Rollback

- **Backup sebelum cutover:** `20261028T234931Z`, terverifikasi (restore
  terisolasi 12/12 PASS). Backup Tahap 11 sebelumnya tidak ditimpa.
- **Backup setelah cutover (MySQL):** `20261028T235815Z`, `status=complete`,
  `database.sql` 34.867 byte (17 tabel), `documents.tar.gz` 8 file,
  7 referensi dokumen, 0 hilang.
- **Restore drill pada MySQL:** database scratch
  `pmb_restore_verify_<timestamp>` dibuat, diimpor, diverifikasi (checksum,
  jumlah baris, referensi dokumen, akses dokumen 401/200/404/403), lalu
  **dihapus**. Database aktif tidak disentuh.
- **Rollback ke SQLite:** tersedia dan terdokumentasi pada Deployment Guide
  §6B. File SQLite asli tetap ada dan tidak diubah. Rollback akan mengabaikan
  data yang ditulis ke MySQL setelah cutover — risiko ini dicatat, bukan
  disembunyikan.

---

## 11. Status Repository Git

|Item|Status|
|---|---|
|Root repository|`PMBITBAKIDAY/` (satu repository untuk seluruh komponen)|
|Branch|`main`|
|Baseline commit|`98eab07` — 260 file, 54.177 baris|
|Working tree|Bersih (0 file belum di-commit) setelah commit dokumentasi|
|Remote|**Belum ada** (tidak dikonfigurasi otomatis)|
|Push|Tidak dilakukan|
|Repository bersarang|Tidak ada (diperiksa dengan `find -name .git`)|
|`git fsck`|Tidak ada error|

Kredensial tidak pernah masuk index: `git add -n` ditinjau sebelum staging, dan
`git diff --cached` diperiksa ulang sebelum commit.

---

## 12. File yang Dilacak dan Diabaikan

**Dilacak (260 file):** `android-app/` (66 file source + Gradle), `backend-api/`
(app, config, database/migrations, routes, tests, `composer.lock`, template),
`admin-web/` (src, test, `package-lock.json`, konfigurasi Vite/TS), `docs/`,
`scripts/`, `README.md`, `docker-compose.yml`, `.gitignore`, dua `.env.example`.

**Diabaikan (contoh yang diperiksa):**

|Path|Alasan|
|---|---|
|`.env`, `backend-api/.env`|Berisi kredensial|
|`backend-api/database/database.sqlite`|Database lokal berisi data|
|`backend-api/storage/app/private/documents/*`|Dokumen pendaftar|
|`backend-api/vendor/`, `admin-web/node_modules/`|Dependency, dapat dipasang ulang|
|`admin-web/dist/`, `android-app/app/build/`, `build/`, `.gradle/`, `.kotlin/`|Hasil build|
|`android-app/local.properties`, `.idea/`|Konfigurasi lokal/IDE|
|`../pmb_backups/` (`*.sql.gz`, `*.tar.gz`, `*.dump`)|Backup|

`.env.example` hanya memuat nama variabel dengan nilai kosong; diperiksa bahwa
tidak ada password, token, atau `APP_KEY` terisi.

---

## 13. Risiko yang Masih Tersisa

|#|Tingkat|Risiko|Status / Rencana|
|---|---|---|---|
|R-1|High|Belum ada remote Git; kode hanya ada di satu mesin|Putuskan remote dan push setelah peninjauan|
|R-2|Medium|Kredensial admin lokal (`admin@example.com`) tertulis sebagai fixture di 5 file test (`admin-web/test/*.mjs`, 2 test Android). Akun ini hanya ada di data lokal, tetapi nilainya ikut ter-commit|Pindahkan ke environment variable sebelum repository dibagikan; rotasi bila repository dipublikasikan|
|R-3|Medium|Rollback ke SQLite mengabaikan data yang ditulis ke MySQL setelah cutover|Terdokumentasi di Deployment Guide §6B|
|R-4|Medium|Backup tidak dienkripsi dan berada di mesin yang sama|Enkripsi + media terpisah|
|R-5|Medium|Mount proyek `fuseblk` (NTFS) tidak menegakkan `chmod`, sehingga `.env` dan arsip backup tidak terlindungi mode file|Pindahkan secret/backup ke filesystem yang menegakkan permission|
|R-6|Low|Instance MariaDB lama tidak berjalan; data hanya dapat diakses lewat profile|Jalankan `--profile legacy-mariadb` bila perlu diperiksa|
|R-7|Low|Instrumented test Android belum dijalankan|Jalankan pada emulator/perangkat|
|R-8|Low|`.env` mengandung `APP_DEBUG=true` untuk lokal|Wajib `false` pada deployment publik|

Catatan insiden: saat menyiapkan konfigurasi, dua nilai kata sandi lokal sempat
tercetak ke output terminal karena kesalahan format perintah. Tindakan yang
diambil: kata sandi MySQL di-rotasi (`ALTER USER`) dan seluruh file `.env`
diperbarui; kata sandi MariaDB lama tidak dapat dirotasi tanpa menyalakan
container tersebut, sehingga dicatat sebagai risiko bila instance itu
diaktifkan kembali. Tidak ada nilai rahasia yang masuk Git atau dokumen.

---

## 14. Keputusan yang Belum Diselesaikan

1. URL remote Git dan kebijakan branch/push.
2. Penanganan kredensial fixture test (R-2): hapus, ganti ke environment
   variable, atau biarkan untuk pengembangan lokal.
3. Nasib instance MariaDB lama: dihapus setelah yakin, atau dipertahankan
   sebagai arsip.
4. Apakah file SQLite lokal dipertahankan permanen sebagai jalur rollback atau
   dihapus setelah periode stabil.
5. Jadwal backup, retensi, dan enkripsi.

---

## 15. Rekomendasi untuk Tahap 12

1. Tetapkan remote Git, lalu push baseline setelah peninjauan; tambahkan
   `protected branch` bila menggunakan platform hosting.
2. Bersihkan kredensial fixture test (R-2) sebelum repository dibagikan.
3. Tambahkan job CI yang menjalankan `php artisan test` pada MySQL, `npm test`,
   `tsc`, `vite build`, dan `gradlew test assembleDebug`.
4. Jalankan instrumented test Android pada emulator untuk menutup kriteria
   `NOT RUN`.
5. Finalkan kebijakan backup (jadwal, retensi, enkripsi, media terpisah) dan
   uji restore berkala.
6. Perbarui Release Notes dan User Manual berdasarkan kondisi MySQL ini.

Tahap 12 belum dimulai.
