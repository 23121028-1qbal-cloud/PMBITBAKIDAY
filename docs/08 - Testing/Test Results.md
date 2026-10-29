|Atribut|Keterangan|
|---|---|
|Nama Dokumen|Test Results|
|Nama Proyek|PMB ITB Akiday|
|Versi Dokumen|2.0 (Verified Final)|
|Status Dokumen|Verified|
|Jenis Dokumen|Dokumentasi Hasil Pengujian Komprehensif (Tahap 10)|
|Referensi Pengujian|[[Test Plan]] dan [[Test Cases]]|
|Lingkungan Pengujian|Lokal (Linux, PHP 8.3 / Laravel 12, Node.js 20, Android Gradle 9.6.0)|
|Tanggal Pengujian|29 Oktober 2026|
|Penguji|Senior System Integration & Automated Testing Engineer|

---

## 2. Tujuan

Dokumen Test Results dibuat untuk mencatat hasil pengujian resmi pada sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday pada **Tahap 10 — Testing**. Pengujian dilakukan secara komprehensif untuk memastikan bahwa:
1. Seluruh 20 skenario uji produk (`TR-001` s/d `TR-020`) berjalan sesuai spesifikasi dan kontrak API.
2. Data tersimpan secara konsisten dengan Backend Laravel sebagai *Single Source of Truth*.
3. Mekanisme keamanan, autentikasi ganda (Sanctum Bearer Token untuk Android dan Session Cookies + CSRF untuk Admin Web), serta role-based access control (`calon_mahasiswa`, `admin`, `panitia`, `pimpinan`) terisolasi ketat.
4. Pencegahan pendaftaran ganda pada gelombang yang sama berfungsi deterministik.
5. Dokumen pendaftaran privat terlindungi dari akses tanpa wewenang.

---

## 3. Lingkup Pengujian

Pengujian mencakup 12 area fungsi utama:

1. Registrasi dan login pengguna (Android Bearer & Admin Web Cookie Session).
2. Pengelolaan profil calon mahasiswa dan validasi format NIK 16-digit.
3. Pengelolaan gelombang penerimaan dan pendaftaran multi-gelombang independen.
4. Penampilan program studi konsisten antar-klien.
5. Pembuatan draft dan pengiriman pendaftaran dengan pengecekan kelengkapan berkas wajib.
6. Unggah, validasi format berkas (PDF/JPG/PNG), dan proteksi unduh dokumen privat.
7. Pemantauan status pendaftaran dan timeline riwayat status kronologis.
8. Pemeriksaan berkas dan instruksi revisi oleh panitia.
9. Penetapan keputusan penerimaan oleh pihak berwenang.
10. Pengelolaan pengguna, isolasi data pribadi pendaftar lain, dan pembatasan wewenang teknis.
11. Dashboard statistik dan pemantauan laporan oleh pimpinan.
12. Pencegahan konflik status konkuren (precondition check `409 STATE_CONFLICT`).

---

## 4. Lingkungan Pengujian Aktual

Pengujian dieksekusi secara otomatis dan langsung pada terminal Linux lingkungan lokal:

| Komponen / Tool | Konfigurasi Aktual | Hasil Verifikasi |
|---|---|---|
| Sistem Operasi | Linux 6.6.137+ x86_64 | Berfungsi stabil |
| Android Toolchain | Gradle 9.6.0, JDK 23-ea, Android SDK 37.0.0 | `./gradlew compileDebugKotlin` BUILD SUCCESSFUL |
| Android Test Suite | JUnit 4, Kotlin Coroutines Test, MockWebServer | `./gradlew testDebugUnitTest` — 42 passed, 0 failed |
| Backend Runtime | PHP 8.3, Laravel 12.x, SQLite DB (Foreign Keys ON) | Server aktif pada `http://127.0.0.1:8000` |
| Backend Test Suite | PHPUnit 11 | `php artisan test` — 48 passed, 206 assertions, 0 failed |
| Admin Web Runtime | Node.js v20.19.5, Vite 5.4.21, React 18, TypeScript | `npm run build` — Berhasil membangun aset bundle |
| Admin Web Test Suite | Node Native Test Runner | `npm test` — 10 passed, 0 failed |
| Live Product Test Suite | Script Automated Runner (`product-test-suite.mjs`) | 20 skenario produk (`TR-001` - `TR-020`) — 100% Pass |
| System Integration Suite | Script Automated Runner (`system-integration-verify.mjs`) | 28 skenario integrasi (`SI-01` - `SI-28`) — 100% Pass |

---

## 5. Status Pengujian

Status yang digunakan dalam dokumen ini:
- **Pass**: Hasil aktual sesuai dengan hasil yang diharapkan secara penuh dengan bukti terverifikasi.
- **Fail**: Hasil aktual tidak sesuai dengan hasil yang diharapkan.
- **Blocked**: Pengujian belum dapat dilaksanakan karena ada kendala dependensi.

---

## 6. Ringkasan Hasil Pengujian

| Kategori Pengujian | Total Skenario | Lulus (Pass) | Gagal (Fail) | Persentase Kelulusan |
|---|---|---|---|---|
| **Product Test Cases (TR-001 s/d TR-020)** | **20** | **20** | **0** | **100.0%** |
| **System Integration Scenarios (SI-01 s/d SI-28)** | **28** | **28** | **0** | **100.0%** |
| **Backend Unit & Feature Tests (PHPUnit)** | **48** | **48** | **0** | **100.0%** |
| **Android Unit & Live Integration Tests** | **42** | **42** | **0** | **100.0%** |
| **Admin Web Contract & Workflow Tests** | **10** | **10** | **0** | **100.0%** |
| **Total Seluruh Pengujian Proyek PMB** | **148** | **148** | **0** | **100.0%** |

---

## 7. Detail Hasil Pengujian Produk (TR-001 s/d TR-020)

### 7.1 Pengujian Registrasi dan Login

| ID | Skenario Pengujian | Hasil yang Diharapkan | Hasil Aktual | Status |
|---|---|---|---|---|
| **TR-001** | Pengguna melakukan registrasi dengan data valid. | Akun berhasil dibuat, tersimpan di DB, dan otomatis mendapatkan peran `calon_mahasiswa`. | HTTP 201 Created. Akun baru berhasil dibuat dengan `role: "calon_mahasiswa"`, envelope respons standar `success: true`. | **Pass** |
| **TR-002** | Pengguna login dengan email dan kata sandi yang benar. | Pengguna berhasil masuk sesuai hak aksesnya, token/sesi diterbitkan. | HTTP 200 OK. Sanctum Bearer Token berhasil diterbitkan untuk klien Android, dan Sesi Cookie first-party berhasil dibentuk untuk Admin Web. | **Pass** |
| **TR-003** | Pengguna login dengan kata sandi yang salah. | Sistem menolak login dan menampilkan pesan kesalahan yang aman tanpa membocorkan info internal. | HTTP 422 Unprocessable Content. Sistem menolak dengan pesan generik: *"Kredensial yang diberikan tidak cocok dengan catatan kami."* | **Pass** |

### 7.2 Pengujian Profil Calon Mahasiswa

| ID | Skenario Pengujian | Hasil yang Diharapkan | Hasil Aktual | Status |
|---|---|---|---|---|
| **TR-004** | Calon mahasiswa melengkapi profil dengan data valid. | Profil berhasil disimpan dan terhubung ke `user_id` yang sesuai. | HTTP 200 OK. Data nama, NIK 16-digit, tempat/tanggal lahir, gender, telepon, dan alamat tersimpan pada tabel `applicant_profiles`. | **Pass** |
| **TR-005** | Calon mahasiswa mengirim data dengan format yang tidak valid (NIK < 16 digit, nama kosong). | Sistem menolak data dan memberikan rincian field validasi yang gagal. | HTTP 422 VALIDATION_ERROR. Sistem menolak input dan mengembalikan pesan rincian validasi per-field. | **Pass** |

### 7.3 Pengujian Pendaftaran

| ID | Skenario Pengujian | Hasil yang Diharapkan | Hasil Aktual | Status |
|---|---|---|---|---|
| **TR-006** | Calon mahasiswa membuat pendaftaran pada gelombang aktif. | Pendaftaran berhasil dibuat dengan status `draft` dan nomor registrasi otomatis. | HTTP 201 Created. Pendaftaran dibuat dengan format registrasi `PMB-YYYY-XXXXXX` dan status awal `draft`. | **Pass** |
| **TR-007** | Calon mahasiswa mengirim pendaftaran dengan dokumen wajib yang belum diunggah. | Sistem menolak pengiriman dan memberitahukan dokumen persyaratan wajib belum lengkap. | HTTP 422 APPLICATION_INCOMPLETE. Sistem menolak transisi dan menampilkan pesan: *"Dokumen persyaratan wajib diunggah sebelum pengajuan dikirim."* | **Pass** |
| **TR-008** | Calon mahasiswa mengirim pendaftaran yang sudah lengkap. | Pendaftaran tersimpan, status berubah menjadi `submitted`, dan waktu pengiriman tercatat. | HTTP 200 OK. Status berubah menjadi `submitted`, kolom `submitted_at` terisi timestamp server, dan riwayat status tercatat. | **Pass** |
| **TR-009** | Calon mahasiswa mencoba membuat pendaftaran kedua pada gelombang yang sama. | Sistem mencegah pendaftaran ganda pada gelombang yang sama. | HTTP 409 APPLICATION_ALREADY_EXISTS. Backend menolak pembuatan draft kedua dengan pesan: *"Anda sudah memiliki pengajuan pada gelombang ini."* | **Pass** |
| **TR-010** | Calon mahasiswa membuat pendaftaran pada gelombang berbeda yang aktif. | Pendaftaran baru dapat dibuat tanpa mengubah atau menimpa pendaftaran pada gelombang sebelumnya. | HTTP 201 Created. Pendaftaran pada gelombang 2 berhasil dibuat secara independen dengan ID baru tanpa mengganggu gelombang 1. | **Pass** |

### 7.4 Pengujian Dokumen

| ID | Skenario Pengujian | Hasil yang Diharapkan | Hasil Aktual | Status |
|---|---|---|---|---|
| **TR-011** | Calon mahasiswa mengunggah dokumen dengan format yang diizinkan (PDF/JPG/PNG). | Dokumen berhasil diunggah, tersimpan di direktori privat, dan metadata tercatat di database. | HTTP 201 Created. Berkas PDF berhasil disimpan pada storage privat `documents/{appId}/{fileName}` dengan metadata utuh. | **Pass** |
| **TR-012** | Calon mahasiswa mengunggah file dengan format yang tidak diizinkan (.sh / biner bebas). | Sistem menolak file dan memberikan pesan validasi tipe berkas. | HTTP 422 VALIDATION_ERROR. Berkas biner/skrip ditolak dengan pesan: *"Format file harus berupa PDF, JPG, JPEG, atau PNG."* | **Pass** |
| **TR-013** | Pengguna lain mencoba mengakses dokumen milik pendaftar lain. | Sistem menolak akses tanpa membocorkan isi berkas maupun path server. | HTTP 404 RESOURCE_NOT_FOUND / 403 FORBIDDEN. Akses streaming ditolak secara aman oleh otorisasi kepemilikan backend. | **Pass** |

### 7.5 Pengujian Pemeriksaan dan Status

| ID | Skenario Pengujian | Hasil yang Diharapkan | Hasil Aktual | Status |
|---|---|---|---|---|
| **TR-014** | Panitia membuka pendaftaran yang menjadi kewenangannya. | Panitia dapat melihat data identitas dan dokumen persyaratan untuk pemeriksaan. | HTTP 200 OK. Panitia dapat mengakses data lengkap formulir, metadata dokumen, dan unduh berkas via endpoint `/admin/applications/{id}`. | **Pass** |
| **TR-015** | Panitia meminta revisi atas dokumen atau data pendaftaran. | Status berubah menjadi `revision` dan catatan revisi tersimpan pada timeline riwayat. | HTTP 200 OK. Status pendaftaran berubah menjadi `revision`, catatan instruksi perbaikan tersimpan pada tabel `application_status_histories`. | **Pass** |
| **TR-016** | Calon mahasiswa memperbaiki data atau dokumen yang diminta. | Dokumen perbaikan tersimpan dan pendaftaran diajukan ulang ke status `review`. | HTTP 200 OK via endpoint `/resubmit`. Dokumen terbarui dan status pendaftaran kembali ke `review` untuk diperiksa panitia. | **Pass** |
| **TR-017** | Panitia yang memiliki kewenangan menetapkan keputusan penerimaan. | Keputusan tersimpan permanen dan status berubah menjadi `accepted` atau `rejected`. | HTTP 200 OK. Keputusan seleksi `accepted` tersimpan bersama catatan panitia dan kolom `decided_at`. | **Pass** |

### 7.6 Pengujian Hak Akses dan Laporan

| ID | Skenario Pengujian | Hasil yang Diharapkan | Hasil Aktual | Status |
|---|---|---|---|---|
| **TR-018** | Pengguna yang tidak memiliki kewenangan mencoba menetapkan keputusan seleksi. | Sistem menolak tindakan tersebut dengan proteksi role-based access control. | HTTP 403 FORBIDDEN. Akun pimpinan atau calon mahasiswa ditolak saat mengakses endpoint `POST /admin/applications/{id}/decision`. | **Pass** |
| **TR-019** | Calon mahasiswa mencoba membuka data pendaftaran milik orang lain. | Sistem menolak akses dan hanya mengizinkan pembacaan pendaftaran miliknya. | HTTP 404 RESOURCE_NOT_FOUND. Akun B ditolak saat mencoba membaca pendaftaran Akun A (`GET /applications/{id_A}`). | **Pass** |
| **TR-020** | Pengguna pimpinan membuka dashboard dan laporan sesuai kewenangannya. | Sistem menampilkan statistik dan daftar pendaftaran sesuai hak akses pimpinan. | HTTP 200 OK. Akun pimpinan dapat memantau seluruh pendaftaran, distribusi status (`submitted`, `review`, `accepted`), dan rekap PMB. | **Pass** |

---

## 8. Pencatatan Bug atau Temuan & Perbaikan

| Bug ID | Test Case ID | Deskripsi Masalah | Severity | Status Perbaikan | Solusi / Perbaikan yang Diterapkan |
|---|---|---|---|---|---|
| **BUG-001** | TR-002, TR-016 | Deserialisasi Gson pada Android mengalami `IllegalStateException` saat parsing `ApiResponse<Unit>` karena backend mengembalikan `"data": []`. | High | **Resolved** | Menambahkan TypeAdapter Gson `Unit::class.java` pada `RetrofitClient.kt` serta mengubah respons kosong backend menjadi `null` (`"data": null`) pada `ApiErrorResponse.php`, `AuthController.php`, dan `DocumentController.php`. |
| **BUG-002** | TR-002, TR-003 | Batasan rate-limiting Laravel `throttle:10,1` pada login memicu HTTP 429 saat pengujian otomatis berulang dalam interval 60 detik. | Medium | **Resolved** | Merestrukturisasi alur runner live integration test agar menggunakan satu sesi terautentikasi dan merangkai uji negatif login secara terukur. |

---

## 9. Bukti Pengujian (Evidence Registry)

| Evidence ID | Test Case ID | Jenis Bukti | Lokasi Bukti / Endpoint | Keterangan |
|---|---|---|---|---|
| **EV-001** | TR-001 | JSON Response HTTP 201 | `POST /api/v1/auth/register` | Envelope `success: true`, User ID terbit, `role: calon_mahasiswa`. |
| **EV-002** | TR-002 | JSON Response HTTP 200 | `POST /api/v1/auth/login` | Bearer token Sanctum didapatkan dan disimpan di Keystore Android. |
| **EV-003** | TR-003 | JSON Response HTTP 422 | `POST /api/v1/auth/login` | Penolakan kredensial salah dengan pesan aman tanpa leak hash. |
| **EV-004** | TR-004 | Database Record & JSON 200 | `PUT /api/v1/applicant/profile` | Record tersimpan di tabel `applicant_profiles` dengan relasi `user_id`. |
| **EV-005** | TR-005 | JSON Response HTTP 422 | `PUT /api/v1/applicant/profile` | Rincian validasi per field: NIK minimal 16 digit. |
| **EV-006** | TR-006 | JSON Response HTTP 201 | `POST /api/v1/applications` | Nomor registrasi `PMB-2026-000003` diterbitkan pada status `draft`. |
| **EV-007** | TR-007 | JSON Response HTTP 422 | `POST /api/v1/applications/{id}/submit` | Error `APPLICATION_INCOMPLETE` saat dokumen belum diunggah. |
| **EV-008** | TR-008 | Database History & JSON 200 | `POST /api/v1/applications/{id}/submit` | Status berpindah ke `submitted`, `submitted_at` tersimpan. |
| **EV-009** | TR-009 | JSON Response HTTP 409 | `POST /api/v1/applications` | Error `APPLICATION_ALREADY_EXISTS` mencegah pendaftaran ganda gelombang sama. |
| **EV-010** | TR-010 | JSON Response HTTP 201 | `POST /api/v1/applications` | Pendaftaran gelombang 2 berhasil terbit tanpa mengubah gelombang 1. |
| **EV-011** | TR-011 | File Storage & JSON 201 | `POST /api/v1/applications/{id}/documents` | Berkas KTP PDF tersimpan di storage privat `documents/4/doc_ktp_*.pdf`. |
| **EV-012** | TR-012 | JSON Response HTTP 422 | `POST /api/v1/applications/{id}/documents` | Validasi ekstensi dan MIME menolak script biner berbahaya. |
| **EV-013** | TR-013 | JSON Response HTTP 404/403 | `GET /api/v1/applications/{id}/documents/{docId}/download` | Percobaan unduh oleh user lain diblokir sistem. |
| **EV-014** | TR-014 | JSON Response HTTP 200 | `GET /api/v1/admin/applications/{id}` | Panitia membaca seluruh berkas dan data diri pendaftar. |
| **EV-015** | TR-015 | Database Record & JSON 200 | `POST /api/v1/admin/applications/{id}/request-revision` | Status beralih ke `revision`, instruksi revisi tercatat di riwayat. |
| **EV-016** | TR-016 | JSON Response HTTP 200 | `POST /api/v1/applications/{id}/resubmit` | Berkas revisi tersimpan, status beralih kembali ke `review`. |
| **EV-017** | TR-017 | Database Record & JSON 200 | `POST /api/v1/admin/applications/{id}/decision` | Status akhir `accepted` tersimpan bersama tanggal `decided_at`. |
| **EV-018** | TR-018 | JSON Response HTTP 403 | `POST /api/v1/admin/applications/{id}/decision` | Percobaan penetapan keputusan oleh pimpinan/mhs diblokir middleware role. |
| **EV-019** | TR-019 | JSON Response HTTP 404 | `GET /api/v1/applications/{otherId}` | Akses calon mahasiswa ke pendaftaran milik akun lain ditolak. |
| **EV-020** | TR-020 | JSON Response HTTP 200 | `GET /api/v1/admin/applications` | Pimpinan menerima rekapitulasi data pendaftar dan metrik seleksi PMB. |

---

## 10. Evaluasi Hasil Pengujian

1. **Fungsi Utama:** Seluruh fitur registrasi, profil, pendaftaran, unggah berkas, pemeriksaan panitia, revisi, dan keputusan seleksi terbukti berfungsi tanpa kendala.
2. **Konsistensi Data:** Backend Laravel menjadi *Single Source of Truth*. Data pendaftaran pada SQLite lokal Android dan panel Admin Web selalu tersinkronisasi secara akurat melalui REST API.
3. **Pencegahan Duplikasi:** Sistem berhasil mencegah pendaftaran ganda pada gelombang yang sama, dan secara bersamaan mendukung pendaftaran independen pada gelombang berbeda.
4. **Keamanan & Otorisasi:** Pemisahan token Bearer (Android) dan Sesi Cookie + CSRF (Admin Web) berfungsi sempurna. Berkas privat tidak dapat diakses tanpa hak kepemilikan atau wewenang panitia.
5. **Konkurensi & Precondition:** Perlindungan `409 STATE_CONFLICT` mencegah modifikasi konkuren jika status pendaftaran telah berubah di server.

---

## 11. Kriteria Penyelesaian Pengujian

- [x] Seluruh 20 test case prioritas tinggi (`TR-001` s/d `TR-020`) telah dijalankan dan berstatus **Pass** (100%).
- [x] Tidak ada bug Critical maupun High yang belum diselesaikan. Seluruh bug integrasi telah diatasi.
- [x] Pengujian hak akses dan kepemilikan data berhasil melindungi data pribadi.
- [x] Pencegahan pendaftaran ganda pada gelombang yang sama terverifikasi berhasil.
- [x] Data dan garis waktu kronologis riwayat perubahan status konsisten lintas-klien.
- [x] Seluruh bukti pengujian terdokumentasi lengkap pada Evidence Registry.

---

## 12. Kesimpulan & Rekomendasi

Tahap 10 — Testing telah diselesaikan dengan status **VERIFIED** dan tingkat kelulusan **100% (148 dari 148 pengujian lulus)**, mencakup:
- 20 Product Test Cases (TR-001 s/d TR-020).
- 28 System Integration Scenarios (SI-01 s/d SI-28).
- 48 Backend PHPUnit Tests.
- 42 Android Unit & Integration Tests.
- 10 Admin Web Tests & Production Build.

Sistem PMB ITB Akiday dinyatakan stabil, aman, dan telah memenuhi seluruh kriteria kelulusan untuk melanjutkan ke **Tahap 11 — Local Deployment & Backup**.

---

## 13. Hasil Pengujian Regresi Tahap 11 — Local Deployment & Backup

Pengujian dijalankan ulang setelah perubahan Tahap 11. Rincian lengkap ada pada
[[Tahap 11 - Local Deployment & Backup Report]].

**Perubahan lingkungan yang memengaruhi hasil:** backend diuji pada **dua** engine
database (SQLite seperti konfigurasi live, dan MariaDB 11.8 di Docker). Pada
MariaDB ditemukan bug nyata yang tidak terlihat di SQLite: factory menghasilkan
NIK 18 digit untuk kolom `string(16)`, menyebabkan `SQLSTATE[22001]`. Factory
diperbaiki (skema tidak diubah).

| Pengujian | Hasil | Bukti |
|---|---|---|
| Laravel test suite (SQLite) | **Pass** | 50 test, 216 assertion |
| Laravel test suite (MariaDB) | **Pass** | 50 test, 216 assertion |
| Admin Web test suite | **Pass** | 10 test, 0 gagal |
| Admin Web typecheck | **Pass** | `tsc --noEmit` |
| Admin Web production build | **Pass** | Vite, 1.658 modul |
| Android kompilasi | **Pass** | `:app:compileDebugKotlin` |
| Android unit test | **Pass** | 42 test, 0 gagal (2 test live memerlukan backend aktif) |
| Android APK debug | **Pass** | `app-debug.apk` 70.034.860 byte |
| Android instrumented test | **Not Run** | Tidak ada emulator/perangkat |
| System integration (28 skenario) | **Pass** | 28/28 |
| Validasi unggah dokumen (format & ukuran) | **Pass** | 2 test baru, 422 `VALIDATION_ERROR`, tanpa sisa baris/file |
| Docker Compose config | **Pass** | `docker compose config --quiet` |
| Persistensi database container | **Pass** | Data bertahan setelah `restart` dan `down`+`up` |
| Backup database + dokumen | **Pass** | `integrity_check=ok`, checksum per file |
| Restore terisolasi | **Pass** | 12 pemeriksaan lulus, termasuk otorisasi dokumen |
| Skenario backup tidak valid | **Pass** | Dump rusak, arsip terpotong, manifest `failed` ditolak |

**Catatan angka:** jumlah test backend naik dari 48 menjadi 50 karena dua test
validasi unggah dokumen ditambahkan pada Tahap 11. Seluruh pengujian Tahap 10
tetap lulus; tidak ada test yang diubah agar lulus.

---

## 14. Pengujian Setelah Cutover MySQL (29 Oktober 2026)

Seluruh pengujian dijalankan ulang setelah database aplikasi dipindahkan dari
SQLite ke MySQL 8.4.13. Rincian pada
[[MySQL Cutover & Git Baseline Report]].

| Pengujian | Hasil | Bukti |
|---|---|---|
| Laravel test suite (MySQL, isolated DB) | **Pass** | 50 test, 216 assertion |
| System integration (28 skenario, MySQL) | **Pass** | 28/28, 100% |
| Admin Web test suite | **Pass** | 10 test, 0 gagal |
| Admin Web typecheck + production build | **Pass** | `tsc --noEmit`, Vite 1.658 modul |
| Android unit test (backend live) | **Pass** | 42 test, 0 gagal, 0 di-skip |
| Android APK debug | **Pass** | `app-debug.apk` 70.034.860 byte |
| Android instrumented test | **Not Run** | Tidak ada emulator/perangkat |
| Backup MySQL + private storage | **Pass** | `database.sql` 34.867 byte, 8 file dokumen |
| Restore terisolasi (MySQL) | **Pass** | 12 pemeriksaan, termasuk otorisasi dokumen |
| Migrasi data SQLite → MySQL | **Pass** | 17 tabel, baris & nilai identik |

Catatan: `applicant_profiles.birth_date` tersimpan sebagai `DATE` di MySQL dan
`datetime` di SQLite. Respons API tidak berubah karena selalu diserialisasi
dengan `toDateString()` menjadi `YYYY-MM-DD`.

---

## 13. Hasil Pengujian Tahap 12 (29 Oktober 2026)

Bagian ini mencatat pengujian yang benar-benar dijalankan pada Tahap 12.
Angka pada bagian 1–12 di atas **tidak diubah**: bagian tersebut adalah riwayat
pengujian Tahap 9–11. Status di bawah memakai `PASS`, `FAIL`, `NOT RUN`,
`BLOCKED`, atau `NEEDS DECISION`.

### 13.1 Ringkasan

|Area|Perintah|Hasil|Status|
|---|---|---|---|
|Backend (MySQL 8.4.13)|`DB_CONNECTION=mysql DB_DATABASE=pmb_itb_akiday_test php artisan test`|50 lulus, 216 assertion, 7,73 dtk, 0 gagal|**PASS**|
|Kontrol koneksi DB|perintah sama dengan `DB_PORT=3399` (tidak ada server)|4 test gagal `Connection refused`|**PASS** (bukti env DB benar-benar dipakai)|
|Admin web – typecheck|`npm run typecheck`|tanpa error|**PASS**|
|Admin web – unit test|`npm test`|10 lulus, 0 gagal, 0 dilewati|**PASS**|
|Admin web – build produksi|`npm run build`|`✓ built in 11.22s`|**PASS**|
|Integrasi produk (manual)|`node test/product-test-suite.mjs` dengan kredensial dari environment|20 lulus (TR-001…TR-020), 0 gagal|**PASS**|
|Android – unit test (tanpa live)|`./gradlew :app:testDebugUnitTest`|42 test, 0 gagal, 2 dilewati|**PASS WITH SKIPS**|
|Android – unit test (live)|`PMB_LIVE_TESTS=1 … ./gradlew :app:testDebugUnitTest`|42 test, 0 gagal, 0 dilewati|**PASS**|
|Android – build debug|`./gradlew :app:assembleDebug`|`BUILD SUCCESSFUL`, APK 70.034.860 byte|**PASS**|
|Android – instrumented test|`./gradlew :app:connectedDebugAndroidTest`|tidak ada emulator/perangkat|**NOT RUN**|
|CI GitHub Actions|`.github/workflows/ci.yml`|YAML valid; belum pernah dieksekusi (repo tanpa remote)|**NOT RUN**|
|Backup + restore|`scripts/backup.sh`, `scripts/restore-verify.sh`|backup `20261029T003512Z`; 12/12 pemeriksaan lulus|**PASS**|

### 13.2 Perubahan konfigurasi kredensial pengujian

Sebelum Tahap 12, lima berkas memakai kredensial staf tetap yang juga valid di
database lokal. Kredensial tersebut kini dibaca dari environment:

|Variabel|Dipakai oleh|
|---|---|
|`PMB_TEST_ADMIN_EMAIL`, `PMB_TEST_ADMIN_PASSWORD`|`admin-web/test/http-integration-verify.mjs`, `system-integration-verify.mjs`, `LiveBackendIntegrationTest.kt`|
|`PMB_TEST_PANITIA_EMAIL` / `_PASSWORD`|`admin-web/test/product-test-suite.mjs`|
|`PMB_TEST_PIMPINAN_EMAIL` / `_PASSWORD`|`admin-web/test/product-test-suite.mjs`|
|`PMB_LIVE_TESTS=1`|mengaktifkan live test Android|

Tidak ada nilai default. Tanpa variabel tersebut, skrip berhenti dengan pesan yang
menyebut nama variabel yang kurang (diverifikasi: kode keluar 1 dan pesan
`Konfigurasi kredensial test tidak lengkap: PMB_TEST_ADMIN_EMAIL belum diisi.`).
Kata sandi untuk akun yang dibuat sendiri oleh skrip (calon mahasiswa) diacak
per eksekusi, sehingga tidak ada kata sandi tetap yang tersisa di repository.

Kredensial staf lama yang pernah tertulis di repository sudah **di-rotasi** pada
database lokal; nilai baru hanya tersimpan di `backend-api/.env` (tidak dilacak
Git). Bukti: login dengan kata sandi lama → `401`, dengan kata sandi baru → `200`
untuk `admin@example.com`, `panitia@example.com`, dan `pimpinan@example.com`.

### 13.3 Catatan penting

1. Live test Android hanya berjalan bila `PMB_LIVE_TESTS=1` **dan** backend lokal
   aktif. Tanpa itu test dilewati dengan pesan eksplisit, bukan dinyatakan lulus.
2. `npm test` hanya menjalankan unit test (`test/*.test.mjs`). Skrip integrasi
   manual butuh database terisi dan akun staf nyata, sehingga tidak dijalankan
   otomatis oleh CI.
3. Angka 42 test Android di atas adalah hasil eksekusi ulang hari ini, bukan
   salinan angka dari Tahap 10.
