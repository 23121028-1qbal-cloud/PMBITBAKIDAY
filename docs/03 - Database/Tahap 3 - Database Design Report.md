# Tahap 3 — Database Design Report

## 1. Ringkasan

Audit Tahap 3 meninjau PRD Final v1.0 (Baseline Disetujui), SRS, Security Specification, Access Control Matrix, Module Specification, Workflow & Business Process, Database Model, Data Dictionary, ERD, Migration Plan, dan test cases terkait. Delapan tabel domain baseline dipertahankan. Dokumen usulan desain v1.1 menjelaskan constraint, penghapusan/retensi dan proteksi PII, indeks sesuai query, serta konflik data verifikasi per dokumen. Penambahan `audit_logs` didokumentasikan sebagai usulan berbasis persyaratan audit, belum keputusan implementasi.

## 2. Tahap dan Cakupan

- Tahap resmi: **Tahap 3 — Database Design**.
- Cakupan: audit skema dan dokumen; model relasional/constraint, ERD, data dictionary, dan migration plan.
- Tidak termasuk: membuat proyek Laravel, menjalankan migration, mengubah database yang berjalan, atau membangun seeders/runtime.
- Urutan roadmap resmi tetap: Tahap 2 → Tahap 3 → Tahap 4 → Tahap 5 dan seterusnya.

## 3. Kondisi Awal

- Workspace berisi proyek Android Gradle/Kotlin; `database/`, `backend-api/`, serta backend Laravel tidak tersedia.
- Tidak ditemukan repository metadata `.git` pada root workspace; baseline file dan status perubahan lokal tidak dapat diperiksa melalui Git.
- MySQL/MariaDB tidak merespons ping lokal default sebelumnya; database pengembangan/test dan izin akses tidak terverifikasi.
- Dokumen desain database baseline telah tersedia dan menyebut delapan tabel domain.

## 4. File Baru

- `docs/03 - Database/Tahap 3 - Database Design Report.md` — laporan tahap ini.

## 5. File yang Diubah

- `docs/07 - Development/Repository Guide.md` — keputusan frontend admin.
- `docs/09 - Deployment/Environment Configuration.md` — temuan SDK/environment dan keputusan frontend admin.
- `docs/03 - Database/Database Model.md` — usulan model v1.1 dan decision register.
- `docs/03 - Database/Data Dictionary.md` — revised design, hubungan dan indeks `audit_logs` usulan.
- `docs/03 - Database/ERD.md` — ERD usulan dengan aktor audit.
- `docs/03 - Database/Migration Plan.md` — rencana revisi, urutan migration, dan blocker pemeriksaan dokumen.
- `docs/08 - Testing/Test Results.md` — hasil verifikasi Android SDK/build lokal.

`PRD Final V1.0.md` tidak diubah: baseline yang telah disetujui tetap dipertahankan. `audit_logs` juga belum diberlakukan sebagai keputusan resmi; ia merupakan usulan desain.

## 6. Implementasi/desain

- Kandidat domain dipertahankan sesuai baseline: `roles`, `users`, `applicant_profiles`, `admission_waves`, `study_programs`, `applications`, `application_documents`, `application_status_histories`.
- Unique `applications(applicant_id, wave_id)` dan foreign key tetap mewakili pencegahan pendaftaran ganda serta integritas referensial.
- Indeks harus mengikuti query yang didokumentasikan; tidak menambahkan indeks `to_status` tanpa kebutuhan query yang terbukti.
- Aturan referensial yang disarankan: data historis bukan dihapus berantai; `application_status_histories.changed_by_user_id` menghalangi penghapusan aktor (`RESTRICT`), sedangkan usulan `audit_logs.actor_user_id` memakai `SET NULL` sambil mempertahankan peristiwa.
- Dokumen tetap berkas privat di luar MySQL; database hanya memuat metadata.
- Tidak dibuat migration/seed/data DB karena kode backend dan database lokal tidak tersedia.

## 7. Pengujian dan Hasil Aktual

Tidak ada database migration/test yang dapat dijalankan tanpa backend Laravel dan layanan database.

Verifikasi lingkungan yang mendukung Tahap 3:

- Android SDK dari `local.properties`: direktori ada; `platforms/android-37.0` revision 2, `build-tools/37.0.0`, dan tools `aapt2`, `d8`, `zipalign`, `apksigner` tersedia.
- `<sdk>/platform-tools/adb version` berhasil (ADB 1.0.41, version 37.0.0-14910828); `adb devices -l` berhasil tanpa emulator/perangkat tersambung. `sdkmanager` tidak ditemukan pada SDK atau `PATH`.
- `./gradlew :app:testDebugUnitTest :app:assembleDebug`: BUILD SUCCESSFUL; 1 contoh unit test, 0 gagal/error/skipped dan APK debug berhasil dibuat. Ini hanya verifikasi Android; bukan uji database/produk PMB.
- Git status/diff tidak tersedia karena `.git` tidak ditemukan.

Lihat `08 - Testing/Test Results.md` untuk rincian dan batas cakupan.

## 8. Kegagalan dan Perbaikan

- Tidak ada kegagalan unit test/build yang dilaporkan oleh perintah Gradle di atas.
- Tidak tersedia target migrasi/test MySQL; uji migration, FK, uniqueness, transaksi, rollback, seeder, dan audit masih belum dijalankan.
- Tidak tersedia emulator atau perangkat Android yang terhubung.
- Konflik stack admin diselesaikan untuk build berdasarkan arahan pengguna, tetapi PRD baseline tidak diubah; statusnya dicatat terpisah di §9.

## 9. Keputusan yang Digunakan

### Fakta dan keputusan implementasi saat ini

- Keputusan stack build yang berlaku: React + TypeScript + Vite sebagai klien admin terpisah, Laravel sebagai REST API, MySQL hanya dapat diakses backend; Android dan admin mengakses data melalui API. Keputusan dicatat pada Repository Guide dan Environment Configuration.
- PRD Final v1.0 berstatus Baseline Disetujui dan berisi frasa `Web berbasis Laravel`; file baseline tidak diubah. Keputusan build eksplisit mengarahkan implementasi ke depan tanpa menghapus baseline historis. Pemilik proyek perlu menyelaraskan dan merilis revisi formal PRD; revisi itu tidak menghalangi pekerjaan build yang mengikuti keputusan stack.
- Database menggunakan MySQL menurut sumber kebutuhan.
- Delapan tabel domain baseline tetap tidak berubah.

### Usulan desain / asumsi yang tidak dijadikan keputusan resmi

- `audit_logs` adalah usulan tambahan opsional untuk memenuhi audit Security Specification; persetujuan membuat tabel, retensi, cakupan anonim, akses, dan penghapusan menunggu keputusan.
- Penghapusan histori status memakai RESTRICT; actor FK audit memakai SET NULL pada usulan.
- Tidak ada unique constraint global pada NIK karena kebijakan keunikan NIK belum ditetapkan.
- Tidak ada unique constraint pada `(application_id, document_type)` dan tidak ditentukan versi file sampai aturan penggantian disepakati.

## 10. Keputusan Terbuka / Blocker

|ID|Pertanyaan|Dampak/komponen|Pilihan/rekomendasi|Yang tetap dapat dilanjutkan|
|---|---|---|---|---|
|DB-01|Bagaimana status dan catatan pemeriksaan per dokumen disimpan dan apakah pemeriksaan per berkas memerlukan riwayat?|`application_documents`, `application_status_histories`, workflow/API/admin|Tetapkan enum/status, pelaku/waktu/catatan, siklus/review ulang; bandingkan kolom satu baris terkini dengan tabel history terpisah. Rekomendasi: putuskan aturan domain sebelum migrasi.|Delapan entitas baseline dan integritas dasar.|
|DB-02|Apakah `audit_logs` wajib sebagai tabel persisten? Apa ruang lingkup peristiwa anonim, retensi, akses, arsip/purge?|Desain tabel audit, backend, operasional/privacy|Konfirmasi append-only table dan kebijakan penyimpanan dari pemilik keamanan/data.|Semua constraint domain dasar dapat dirancang terpisah.|
|DB-03|Apakah NIK harus unik secara global?|Profil/validasi dan privasi|Uniqueness dapat ditambahkan setelah dasar bisnis dan legal dikonfirmasi; jangan pasang unique diam-diam.|`VARCHAR(16)` dan validasi backend tetap.|
|DB-04|Apakah upload foto/dokumen boleh memiliki beberapa versi dan berapa unggahan aktif per jenis?|Dokumen/backup|Pilih replace-in-place atau versioned rows serta aturan review dokumen lama.|Tipe dokumen baseline dan penyimpanan privat.|
|DB-05|Berapa batas ukuran dan allowlist tipe/MIME berkas?|Upload/API/backend|Tetapkan berdasarkan kebijakan institusi dan kapasitas penyimpanan sebelum migration/validasi final.|Metadata path, MIME hasil inspeksi, ukuran.|
|DB-06|Apa format nomor pendaftaran dan aturan perubahan hasil final?|Aplikasi/status/audit|Tetapkan generator unik yang sesuai requirement, serta prosedur koreksi keputusan.|Unique registration number dan transisi status terdokumentasi.|
|DB-07|Apa strategi zona waktu, soft-delete, retensi pengajuan, dan backup?|Seluruh schema/ops|Setujui satu kebijakan waktu dan retensi sebelum skema final dan deployment.|Constraint FK serta kebutuhan backup sudah tercatat.|
|ENV-01|Layanan MySQL dan backend untuk pengujian belum ada; koneksi/versi server tidak diketahui.|Verifikasi migrasi, constraint engine/version.|Sediakan database test terisolasi saat backend tersedia; hindari menjalankan migrasi destruktif pada DB data penting.|Desain konseptual tanpa mengeksekusi SQL.|
|ENV-02|Tidak ada metadata `.git` lokal pada root workspace.|Review perubahan dan jejak baseline.|Tidak menginisialisasi/memindahkan repository tanpa persetujuan dan akses repository resmi.|Perubahan dan laporan lokal dapat diperiksa langsung.|
|ENV-03|SDK command-line tools `sdkmanager` tidak tersedia; tidak ada emulator atau perangkat terhubung.|Menambah paket platform SDK dan menjalankan instrumented/device test.|Sediakan Android SDK Command-line Tools dan emulator/device ketika pengujian instrumentasi diperlukan.|SDK platform API 37, Build Tools, ADB, unit test, dan build lokal terverifikasi.|

## 11. Risiko

- `audit_logs` berpotensi menampung PII/metadata jaringan serta menambah kewajiban retensi; minimalkan payload, batasi akses dan setujui retensi.
- Menambahkan model hasil pemeriksaan dokumen tanpa keputusan dapat mengunci workflow yang belum disetujui; migration fitur tersebut diblokir.
- Tanpa MySQL test, kompatibilitas constraint/check/index dan rollback belum dibuktikan pada engine/version deployment.
- Data prototipe program studi bukan master resmi.
- Tidak ada uji restore database/file yang dilakukan pada Tahap 3.

## 12. Dokumentasi yang Diperbarui

Repository Guide; Environment Configuration; Database Model; Data Dictionary; ERD; Migration Plan; Test Results; laporan Tahap 3 ini. Baseline PRD final tetap tidak berubah.

## 13. Status Implementasi

**DOCUMENTED DESIGN — DATABASE IMPLEMENTATION NOT STARTED** (review dan penyelarasan konseptual didokumentasikan; belum ada migration/runtime).

## 14. Status Checkpoint dan Penerimaan

- Status checkpoint desain dokumentasi: **ACCEPTED WITH LIMITATIONS** — audit dan rekomendasi skema konseptual terdokumentasi, dengan keputusan DB terbuka dicatat.
- Status checkpoint implementasi Tahap 3: **BLOCKED / NOT COMPLETE** — MASTERPROMPT mensyaratkan migration dijalankan pada database pengujian, relasi/constraint diverifikasi, dan kesesuaian desain-implementasi didokumentasikan; backend dan MySQL test belum tersedia. Karena itu ini bukan penerimaan Tahap 3 secara penuh.
- Tidak ada database aktif yang diubah.

## 15. Langkah Berikutnya

1. Pemilik proses menetapkan DB-01 (hasil review tiap dokumen) dan keputusan domain/operasional yang masih dibutuhkan untuk skema final; putuskan DB-02 bila tabel audit persisten akan digunakan.
2. Selaraskan Database Model, Data Dictionary, ERD, dan Migration Plan setelah keputusan tersebut; tetapkan versi baseline skema yang disetujui.
3. Sebelum menutup Tahap 3 implementasi, sediakan backend Laravel dan database MySQL test terisolasi; buat migration/seed hanya berdasarkan keputusan disetujui, lalu verifikasi fresh migration, rollback, foreign key, unique constraint, transaksi dan ERD.
4. Sesudah keluaran desain diterima sesuai kriteria MASTERPROMPT, lanjutkan Tahap 4/API Design berdasarkan roadmap dan catat dependency hasil pemeriksaan dokumen serta audit.
