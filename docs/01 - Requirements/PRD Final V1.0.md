
title: "PRD Final v1.0 — Sistem Penerimaan Mahasiswa Baru ITB Akiday"  
project: "PMB ITB Akiday"  
document_type: "Product Requirements Document"  
version: "1.0"  
status: "Baseline Disetujui"  
created: 2026-10-16  
platform:

- Android
    
- Web Admin  
    backend: Laravel  
    database: MySQL
    

---

# PRODUCT REQUIREMENTS DOCUMENT (PRD)

## Sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday

|Informasi|Keterangan|
|---|---|
|Nama Proyek|Sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday|
|Versi Dokumen|1.0|
|Status|Baseline Disetujui|
|Tanggal|16 Oktober 2026|
|Platform|Android dan Website Admin|
|Bahasa Pemrograman Android|Kotlin|
|Backend|Laravel|
|Database|MySQL|
|Deployment Awal|Lingkungan lokal|

---

# 1. Pendahuluan

## 1.1 Latar Belakang

Proses penerimaan mahasiswa baru membutuhkan pengelolaan data calon mahasiswa, dokumen persyaratan, pilihan program studi, serta hasil pemeriksaan administrasi. Jika proses tersebut dilakukan melalui berbagai media secara terpisah, pengelolaan data berpotensi menjadi kurang teratur, pemeriksaan dokumen membutuhkan waktu lebih lama, dan calon mahasiswa kesulitan mengetahui perkembangan pendaftarannya.

Oleh karena itu, diperlukan sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday yang mengintegrasikan proses pendaftaran melalui aplikasi Android dan pengelolaan administrasi melalui website.

Sistem ini dirancang agar calon mahasiswa dapat membuat akun, mengisi formulir, mengunggah dokumen, serta memantau status pendaftaran secara online. Sementara itu, admin dapat mengelola konfigurasi penerimaan dan panitia dapat memeriksa dokumen, meminta perbaikan, serta menetapkan keputusan penerimaan sesuai kewenangannya.

## 1.2 Tujuan Produk

Tujuan pengembangan sistem PMB ITB Akiday adalah:

1. Mempermudah calon mahasiswa melakukan pendaftaran secara online.
    
2. Memusatkan data calon mahasiswa dan dokumen persyaratan dalam satu sistem.
    
3. Membantu panitia melakukan pemeriksaan administrasi secara terstruktur.
    
4. Menyediakan informasi status pendaftaran yang dapat dipantau calon mahasiswa.
    
5. Meminimalkan pendaftaran ganda dalam gelombang yang sama.
    
6. Menyediakan dashboard dan laporan untuk membantu pemantauan proses penerimaan.
    
7. Menjaga keamanan, konsistensi, dan keterlacakan data pendaftaran.
    

## 1.3 Ruang Lingkup Dokumen

Dokumen ini menjadi acuan kebutuhan produk dan rancangan teknis awal untuk pengembangan PMB ITB Akiday. Dokumen mencakup kebutuhan fungsional, kebutuhan nonfungsional, aturan bisnis, hak akses, alur pendaftaran, arsitektur sistem, rancangan database, API, serta kriteria penerimaan.

---

# 2. Deskripsi Produk

## 2.1 Gambaran Umum

PMB ITB Akiday merupakan sistem pendaftaran mahasiswa baru dengan dua antarmuka utama:

- **Aplikasi Android:** digunakan calon mahasiswa untuk melakukan pendaftaran dan memantau status.
    
- **Website Admin:** digunakan admin, panitia, dan pimpinan untuk mengelola atau memantau proses penerimaan berdasarkan hak akses masing-masing.
    

Kedua antarmuka terhubung dengan backend Laravel melalui API. Data terstruktur disimpan pada MySQL, sedangkan dokumen persyaratan disimpan dalam penyimpanan file yang dilindungi.

## 2.2 Target Pengguna

Sistem memiliki empat kelompok pengguna:

1. **Calon mahasiswa:** pengguna yang mengajukan pendaftaran.
    
2. **Admin:** pengguna yang mengelola konfigurasi sistem dan data referensi.
    
3. **Panitia:** pengguna yang memeriksa pendaftaran dan menentukan hasil akhir sesuai kewenangan.
    
4. **Pimpinan:** pengguna yang memantau statistik dan laporan penerimaan.
    

## 2.3 Platform dan Teknologi

|Komponen|Teknologi|Fungsi|
|---|---|---|
|Aplikasi calon mahasiswa|Android Kotlin|Formulir, dokumen, status, riwayat|
|Website administrasi|Web berbasis Laravel|Pengelolaan dan pemeriksaan pendaftaran| Android dan web admin tidak boleh mengakses MySQL secara langsung.
|Backend|Laravel|API, validasi, autentikasi, aturan bisnis|
|Database|MySQL|Penyimpanan data relasional|
|Komunikasi|REST API dan JSON|Pertukaran data antara klien dan backend|
|Dokumen|Penyimpanan file privat|Menyimpan foto dan dokumen persyaratan|
|Laporan|PDF dan dashboard|Penyajian hasil rekap penerimaan|
|Version control|Git/GitHub|Pengelolaan versi kode|

Versi teknologi, konfigurasi server, dan lingkungan pengembangan terperinci ditetapkan pada tahap implementasi.

---

# 3. Ruang Lingkup Produk

## 3.1 Fitur yang Termasuk dalam MVP

Versi awal sistem mencakup:

1. Registrasi akun dan login calon mahasiswa.
    
2. Pengisian formulir pendaftaran.
    
3. Pemilihan gelombang dan satu program studi per pendaftaran.
    
4. Penyimpanan draft pendaftaran.
    
5. Pengunggahan pas foto dan ijazah atau surat keterangan lulus.
    
6. Validasi kelengkapan data dan format dokumen.
    
7. Pengiriman formulir pendaftaran.
    
8. Pemantauan status dan riwayat pendaftaran.
    
9. Pengelolaan akun, peran, program studi, dan gelombang.
    
10. Pemeriksaan berkas oleh panitia.
    
11. Permintaan revisi dan pengiriman ulang.
    
12. Penetapan keputusan diterima atau ditolak oleh panitia berwenang.
    
13. Dashboard statistik dan laporan PDF.
    
14. Unduh bukti pendaftaran.
    
15. Pencatatan riwayat perubahan status.
    

## 3.2 Fitur di Luar Ruang Lingkup MVP

Fitur berikut belum termasuk dalam versi awal kecuali ditetapkan kemudian:

- Pembayaran biaya pendaftaran karena pendaftaran dirancang gratis.
    
- Integrasi payment gateway.
    
- Seleksi akademik otomatis menggunakan AI.
    
- Penentuan kelulusan otomatis oleh sistem.
    
- Integrasi dengan sistem akademik eksternal.
    
- Notifikasi melalui SMS atau WhatsApp.
    
- Integrasi verifikasi email.
    
- Analitik lanjutan dan prediksi penerimaan.
    
- Deployment produksi publik sebelum infrastruktur dan kebutuhan operasional ditentukan.
    

---

# 4. Peran dan Hak Akses

## 4.1 Calon Mahasiswa

Calon mahasiswa dapat:

- Membuat akun dan login.
    
- Mengisi serta menyimpan draft formulir.
    
- Memilih satu program studi pada setiap pendaftaran.
    
- Memilih gelombang yang tersedia.
    
- Mengunggah dokumen persyaratan.
    
- Mengirim formulir.
    
- Memperbaiki data ketika diminta revisi.
    
- Melihat status dan riwayat pendaftaran.
    
- Mengunduh bukti pendaftaran jika tersedia.
    

Calon mahasiswa hanya boleh melihat dan mengelola data pendaftarannya sendiri.

## 4.2 Admin

Admin dapat:

- Mengelola akun pengguna.
    
- Mengelola peran dan hak akses.
    
- Mengelola data program studi.
    
- Mengelola gelombang dan jadwal pendaftaran.
    
- Melihat data pendaftaran sesuai kewenangannya.
    
- Memantau statistik operasional.
    

Admin tidak secara otomatis memiliki kewenangan menetapkan keputusan penerimaan. Penetapan keputusan akhir dilakukan oleh panitia yang berwenang.

## 4.3 Panitia

Panitia dapat:

- Melihat daftar pendaftaran yang menjadi tanggung jawabnya.
    
- Memeriksa data calon mahasiswa.
    
- Melihat dokumen persyaratan melalui akses yang terotorisasi.
    
- Meminta revisi dengan menyertakan alasan.
    
- Memeriksa data setelah revisi dikirim.
    
- Menetapkan keputusan diterima atau ditolak sesuai kewenangan.
    
- Melihat riwayat pemeriksaan dan perubahan status.
    

## 4.4 Pimpinan

Pimpinan dapat:

- Melihat dashboard statistik penerimaan.
    
- Melihat rekap pendaftar per gelombang.
    
- Melihat rekap pendaftar per program studi.
    
- Melihat laporan PDF sesuai hak akses.
    

Pimpinan tidak dapat mengubah data pendaftaran atau menetapkan keputusan melalui hak akses pelaporan.

## 4.5 Matriks Hak Akses

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Mengelola akun sendiri|Ya|Ya|Ya|Ya|
|Mengisi pendaftaran|Ya|Tidak|Tidak|Tidak|
|Mengunggah dokumen pendaftaran|Ya|Tidak|Tidak|Tidak|
|Mengelola gelombang|Tidak|Ya|Tidak|Tidak|
|Mengelola program studi|Tidak|Ya|Tidak|Tidak|
|Mengelola akun dan peran|Tidak|Ya|Tidak|Tidak|
|Memeriksa dokumen pendaftar|Tidak|Sesuai izin|Ya|Tidak|
|Meminta revisi|Tidak|Tidak secara default|Ya|Tidak|
|Menetapkan keputusan akhir|Tidak|Tidak secara default|Ya, jika berwenang|Tidak|
|Melihat status pendaftaran sendiri|Ya|Sesuai izin|Sesuai tugas|Tidak secara individual|
|Melihat laporan|Tidak|Sesuai izin|Sesuai izin|Ya|

---

# 5. Aturan Bisnis

|ID|Aturan Bisnis|
|---|---|
|BR-01|Pendaftaran dilakukan melalui sistem secara online.|
|BR-02|Calon mahasiswa harus memiliki akun untuk mengakses pendaftaran.|
|BR-03|Autentikasi menggunakan email dan password tanpa verifikasi email pada MVP.|
|BR-04|Pendaftaran tidak dikenakan biaya.|
|BR-05|Setiap pendaftaran hanya dapat memilih satu program studi.|
|BR-06|Sistem mendukung beberapa gelombang pendaftaran.|
|BR-07|Satu akun hanya boleh memiliki maksimal satu pendaftaran pada gelombang yang sama.|
|BR-08|Akun yang sama dapat membuat pendaftaran pada gelombang lain jika gelombang tersebut dibuka dan aturan penerimaan mengizinkan.|
|BR-09|Pendaftaran harus memenuhi validasi data dan dokumen sebelum dikirim.|
|BR-10|Validasi otomatis hanya memeriksa kelengkapan dan format, bukan menentukan kelulusan.|
|BR-11|Panitia memeriksa dokumen dan menetapkan keputusan akhir sesuai kewenangannya.|
|BR-12|Permintaan revisi harus menyertakan alasan yang dapat dilihat calon mahasiswa.|
|BR-13|Setiap perubahan status harus dicatat dalam riwayat.|
|BR-14|Nomor pendaftaran harus unik.|
|BR-15|Calon mahasiswa hanya dapat mengakses data pendaftarannya sendiri.|
|BR-16|File dokumen harus disimpan dan diakses secara aman.|
|BR-17|Perubahan status harus mengikuti alur yang diizinkan sistem.|
|BR-18|Keputusan akhir tidak boleh diubah sembarangan; prosedur perubahan harus ditentukan oleh kebijakan operasional.|

## 5.1 Program Studi Prototipe

Data awal yang digunakan untuk pengembangan dan demonstrasi:

|Kode|Nama Program Studi|
|---|---|
|PRD-01|Informatika|
|PRD-02|Sistem Informasi|
|PRD-03|Teknik Industri|
|PRD-04|Manajemen|

**Catatan:** daftar ini merupakan data prototipe untuk proyek, bukan konfirmasi bahwa seluruh program studi tersebut merupakan program studi resmi ITB Akiday. Data resmi harus diverifikasi sebelum digunakan dalam penerimaan nyata.

---

# 6. Kebutuhan Formulir Pendaftaran

## 6.1 Data Calon Mahasiswa

Formulir pendaftaran memuat:

|Data|Keterangan|
|---|---|
|Nama lengkap|Nama calon mahasiswa|
|NIK|Nomor Induk Kependudukan|
|Tempat lahir|Tempat kelahiran|
|Tanggal lahir|Tanggal kelahiran|
|Jenis kelamin|Pilihan sesuai rancangan formulir|
|Email|Menggunakan email akun atau mekanisme sinkronisasi yang ditetapkan|
|Nomor HP|Nomor telepon aktif|
|Alamat lengkap|Alamat tempat tinggal|
|Program studi|Satu pilihan per pendaftaran|
|Gelombang|Gelombang pendaftaran aktif|
|Pas foto|Dokumen foto calon mahasiswa|
|Ijazah/SKL|Ijazah atau surat keterangan lulus|

## 6.2 Ketentuan Formulir

1. Kolom wajib tidak boleh kosong ketika formulir dikirim.
    
2. Format data diperiksa oleh backend.
    
3. Email digunakan sebagai identitas login dan harus dikelola secara konsisten.
    
4. NIK diperiksa berdasarkan aturan validasi yang nantinya ditetapkan.
    
5. Dokumen harus memenuhi format dan ukuran yang diizinkan.
    
6. Data draft dapat diperbarui sebelum dikirim.
    
7. Perubahan setelah pengiriman hanya diperbolehkan sesuai alur revisi dan kewenangan.
    
8. Dokumen tidak boleh dapat diakses publik hanya dengan menebak URL file.
    

---

# 7. Alur Proses Pendaftaran

## 7.1 Alur Utama

1. Calon mahasiswa membuat akun.
    
2. Calon mahasiswa login.
    
3. Sistem menampilkan gelombang yang tersedia.
    
4. Calon mahasiswa memilih gelombang dan satu program studi.
    
5. Calon mahasiswa mengisi formulir.
    
6. Calon mahasiswa mengunggah dokumen.
    
7. Sistem menyimpan data sebagai draft.
    
8. Ketika calon mahasiswa mengirim formulir, backend memvalidasi data dan dokumen.
    
9. Jika valid, sistem mengubah status menjadi `submitted`.
    
10. Pendaftaran masuk ke tahap `review`.
    
11. Panitia memeriksa data dan dokumen.
    
12. Jika diperlukan, panitia meminta revisi.
    
13. Calon mahasiswa memperbaiki data dan mengirim ulang.
    
14. Panitia memeriksa kembali pendaftaran.
    
15. Panitia menetapkan status akhir `accepted` atau `rejected`.
    
16. Calon mahasiswa dapat melihat status dan riwayatnya.
    

## 7.2 Definisi Status

|Status|Arti|
|---|---|
|`draft`|Formulir sedang diisi dan belum dikirim.|
|`submitted`|Formulir telah dikirim dan lolos validasi awal.|
|`review`|Pendaftaran sedang diperiksa panitia.|
|`revision`|Pendaftaran memerlukan perbaikan dari calon mahasiswa.|
|`accepted`|Calon mahasiswa dinyatakan diterima oleh panitia berwenang.|
|`rejected`|Calon mahasiswa dinyatakan ditolak oleh panitia berwenang.|

## 7.3 Transisi Status

|Status Awal|Status Tujuan|Pemicu|
|---|---|---|
|`draft`|`submitted`|Calon mahasiswa mengirim formulir dan validasi berhasil.|
|`submitted`|`review`|Pendaftaran masuk antrean pemeriksaan.|
|`review`|`revision`|Panitia meminta perbaikan.|
|`revision`|`submitted`|Calon mahasiswa mengirim ulang hasil revisi.|
|`review`|`accepted`|Panitia menetapkan diterima.|
|`review`|`rejected`|Panitia menetapkan ditolak.|

Transisi harus divalidasi di backend. Pengiriman ulang dari status `revision` akan kembali melalui tahap `submitted`, lalu masuk ke `review`.

---

# 8. Kebutuhan Fungsional

|ID|Kebutuhan|Prioritas|
|---|---|---|
|FR-01|Sistem menyediakan registrasi, login, dan logout.|Must|
|FR-02|Sistem memvalidasi data akun dan menyimpan password dalam bentuk hash.|Must|
|FR-03|Calon mahasiswa dapat menyimpan draft pendaftaran.|Must|
|FR-04|Calon mahasiswa dapat memilih satu program studi per pendaftaran.|Must|
|FR-05|Calon mahasiswa dapat mengunggah dokumen persyaratan.|Must|
|FR-06|Backend memvalidasi kolom wajib dan format dokumen.|Must|
|FR-07|Sistem membuat nomor pendaftaran yang unik.|Must|
|FR-08|Calon mahasiswa dapat melihat status dan riwayat pendaftaran.|Must|
|FR-09|Admin dapat mengelola akun, peran, program studi, dan gelombang.|Must|
|FR-10|Panitia dapat memeriksa data dan dokumen pendaftaran.|Must|
|FR-11|Panitia dapat meminta revisi dan menetapkan keputusan akhir.|Must|
|FR-12|Sistem mencatat perubahan status beserta waktu dan pelakunya.|Must|
|FR-13|Sistem menyediakan dashboard statistik penerimaan.|Should|
|FR-14|Sistem dapat menghasilkan laporan PDF.|Should|
|FR-15|Pimpinan dapat melihat laporan sesuai hak akses.|Should|
|FR-16|Calon mahasiswa dapat mengunduh bukti pendaftaran.|Should|
|FR-17|Admin dapat mengatur periode pembukaan dan penutupan gelombang.|Must|

---

# 9. Kebutuhan Nonfungsional

|ID|Kebutuhan|Kriteria|
|---|---|---|
|NFR-01|Keamanan autentikasi|Password tidak disimpan dalam bentuk teks biasa.|
|NFR-02|Otorisasi|Backend memeriksa hak akses pada setiap operasi yang dilindungi.|
|NFR-03|Keamanan data|Data calon mahasiswa hanya dapat diakses oleh pihak berwenang.|
|NFR-04|Keamanan dokumen|File disimpan pada lokasi privat dan diunduh melalui pemeriksaan izin.|
|NFR-05|Integritas data|Foreign key, unique constraint, dan transaksi digunakan sesuai kebutuhan.|
|NFR-06|Konsistensi status|Perubahan status harus mengikuti transisi yang diperbolehkan.|
|NFR-07|Auditabilitas|Perubahan status dan keputusan dapat ditelusuri.|
|NFR-08|Usability|Formulir memiliki label, validasi, dan pesan kesalahan yang mudah dipahami.|
|NFR-09|Maintainability|Backend, antarmuka, dan database memiliki pemisahan tanggung jawab yang jelas.|
|NFR-10|Backup|Strategi backup dan pemulihan ditetapkan sebelum penggunaan operasional.|
|NFR-11|Performa|Target waktu respons ditentukan pada tahap pengujian dan desain teknis.|
|NFR-12|Kompatibilitas|Versi minimum Android ditentukan sebelum rilis aplikasi.|

---

# 10. User Stories dan Acceptance Criteria

## US-01 — Registrasi Akun

**Sebagai calon mahasiswa, saya ingin membuat akun agar dapat mengakses formulir PMB.**

Kriteria penerimaan:

- Sistem menerima data registrasi yang valid.
    
- Email yang sudah digunakan tidak dapat dipakai untuk membuat akun baru.
    
- Password disimpan dalam bentuk hash.
    
- Pengguna dapat login dengan kredensial yang benar.
    

## US-02 — Mengisi Formulir

**Sebagai calon mahasiswa, saya ingin mengisi dan menyimpan formulir agar dapat melengkapi pendaftaran secara bertahap.**

Kriteria penerimaan:

- Data draft dapat disimpan.
    
- Kolom wajib diperiksa sebelum pengiriman.
    
- Sistem menampilkan pesan untuk data yang tidak valid.
    
- Program studi yang dipilih hanya satu untuk setiap pendaftaran.
    

## US-03 — Mencegah Pendaftaran Ganda

**Sebagai pengelola sistem, saya ingin mencegah akun membuat lebih dari satu pendaftaran pada gelombang yang sama agar data tidak terduplikasi.**

Kriteria penerimaan:

- Sistem menolak pendaftaran kedua dengan kombinasi akun dan gelombang yang sama.
    
- Aturan berlaku meskipun dua permintaan dikirim hampir bersamaan.
    
- Pendaftaran pada gelombang berbeda diperbolehkan jika aturan penerimaan mengizinkan.
    
- Database memiliki constraint unik pada kombinasi calon mahasiswa dan gelombang.
    

## US-04 — Pemeriksaan Dokumen

**Sebagai panitia, saya ingin memeriksa dokumen calon mahasiswa agar dapat memastikan persyaratan administrasi terpenuhi.**

Kriteria penerimaan:

- Panitia dapat melihat data dan dokumen sesuai hak akses.
    
- Panitia dapat meminta revisi disertai alasan.
    
- Calon mahasiswa dapat melihat alasan revisi.
    
- Sistem mencatat tindakan panitia dalam riwayat.
    

## US-05 — Pemantauan Status

**Sebagai calon mahasiswa, saya ingin melihat status pendaftaran agar mengetahui perkembangan proses penerimaan.**

Kriteria penerimaan:

- Status terbaru dapat dilihat setelah login.
    
- Riwayat status ditampilkan secara berurutan.
    
- Calon mahasiswa hanya dapat melihat pendaftarannya sendiri.
    
- Status akhir sesuai keputusan panitia berwenang.
    

## US-06 — Laporan Penerimaan

**Sebagai pimpinan, saya ingin melihat statistik dan laporan agar dapat memantau proses penerimaan mahasiswa baru.**

Kriteria penerimaan:

- Dashboard menampilkan data sesuai hak akses.
    
- Laporan dapat dikelompokkan berdasarkan program studi.
    
- Sistem dapat menghasilkan laporan PDF jika fitur laporan diaktifkan.
    
- Pengguna yang tidak berwenang tidak dapat mengakses data laporan yang dilindungi.
    

---

# 11. System Design

## 11.1 Arsitektur Sistem

Arsitektur terdiri atas tiga lapisan utama:

1. **Presentation Layer:** aplikasi Android Kotlin dan website admin.
    
2. **Application Layer:** backend Laravel yang menyediakan API, autentikasi, validasi, otorisasi, dan aturan bisnis.
    
3. **Data Layer:** MySQL untuk data relasional serta penyimpanan privat untuk dokumen.
    

Alur komunikasi:

- Android berkomunikasi dengan Laravel melalui REST API.
    
- Website admin menggunakan backend Laravel.
    
- Laravel berkomunikasi dengan MySQL.
    
- Laravel mengelola penyimpanan dan akses dokumen.
    

Aplikasi Android dan website tidak boleh mengakses MySQL secara langsung.

## 11.2 Prinsip Arsitektur

- Validasi penting dilakukan di backend.
    
- Hak akses diperiksa pada setiap permintaan yang dilindungi.
    
- Data relasional menggunakan foreign key dan constraint.
    
- Operasi yang saling berkaitan menggunakan transaksi database.
    
- Dokumen persyaratan tidak disimpan di direktori publik.
    
- Riwayat status disimpan untuk kebutuhan audit.
    
- Alamat server lokal dikonfigurasi sesuai lingkungan pengembangan dan pengujian.
    

---

# 12. Database Design

## 12.1 Daftar Tabel Utama

Rancangan awal terdiri atas delapan tabel utama.

### 1. `roles`

Menyimpan daftar peran pengguna.

Kolom utama:

- `id` — Primary Key
    
- `name` — nama peran, unik
    
- `created_at`
    
- `updated_at`
    

Contoh peran: calon mahasiswa, admin, panitia, pimpinan.

### 2. `users`

Menyimpan data akun dan autentikasi.

Kolom utama:

- `id` — Primary Key
    
- `role_id` — Foreign Key ke `roles.id`
    
- `email` — unik
    
- `password` — hash password
    
- `is_active`
    
- `created_at`
    
- `updated_at`
    

### 3. `applicant_profiles`

Menyimpan data profil calon mahasiswa.

Kolom utama:

- `id` — Primary Key
    
- `user_id` — Foreign Key unik ke `users.id`
    
- `full_name`
    
- `nik`
    
- `birth_place`
    
- `birth_date`
    
- `gender`
    
- `phone`
    
- `address`
    
- `created_at`
    
- `updated_at`
    

Kebijakan keunikan NIK dan penanganan data duplikat perlu ditetapkan secara resmi sebelum implementasi final.

### 4. `admission_waves`

Menyimpan informasi gelombang pendaftaran.

Kolom utama:

- `id` — Primary Key
    
- `name`
    
- `start_date`
    
- `end_date`
    
- `is_active`
    
- `created_at`
    
- `updated_at`
    

Backend memeriksa jadwal dan status gelombang sebelum menerima pendaftaran.

### 5. `study_programs`

Menyimpan data program studi.

Kolom utama:

- `id` — Primary Key
    
- `code` — unik
    
- `name`
    
- `is_active`
    
- `created_at`
    
- `updated_at`
    

Data awal berisi empat program studi prototipe.

### 6. `applications`

Menyimpan data utama pendaftaran.

Kolom utama:

- `id` — Primary Key
    
- `applicant_id` — Foreign Key ke profil calon mahasiswa
    
- `wave_id` — Foreign Key ke `admission_waves.id`
    
- `study_program_id` — Foreign Key ke `study_programs.id`
    
- `registration_number` — unik
    
- `status`
    
- `submitted_at`
    
- `created_at`
    
- `updated_at`
    

Constraint penting:

`UNIQUE (applicant_id, wave_id)`

Constraint tersebut memastikan satu profil calon mahasiswa hanya memiliki satu pendaftaran pada gelombang yang sama.

### 7. `application_documents`

Menyimpan metadata dokumen pendaftaran.

Kolom utama:

- `id` — Primary Key
    
- `application_id` — Foreign Key ke `applications.id`
    
- `document_type`
    
- `file_path`
    
- `mime_type`
    
- `file_size`
    
- `uploaded_at`
    
- `created_at`
    
- `updated_at`
    

Jenis dokumen awal: pas foto dan ijazah atau surat keterangan lulus.

### 8. `application_status_histories`

Menyimpan riwayat perubahan status pendaftaran.

Kolom utama:

- `id` — Primary Key
    
- `application_id` — Foreign Key ke `applications.id`
    
- `from_status`
    
- `to_status`
    
- `note`
    
- `changed_by_user_id` — Foreign Key ke `users.id`, dapat kosong jika perubahan dilakukan oleh sistem
    
- `created_at`
    

Riwayat digunakan untuk mencatat pengiriman, permintaan revisi, pengiriman ulang, dan keputusan akhir.

## 12.2 Relasi Antar Tabel

|Relasi|Kardinalitas|
|---|---|
|`roles` → `users`|One-to-Many|
|`users` → `applicant_profiles`|One-to-Zero-or-One|
|`applicant_profiles` → `applications`|One-to-Many|
|`admission_waves` → `applications`|One-to-Many|
|`study_programs` → `applications`|One-to-Many|
|`applications` → `application_documents`|One-to-Many|
|`applications` → `application_status_histories`|One-to-Many|
|`users` → `application_status_histories`|One-to-Many|

## 12.3 Aturan Integritas Database

1. Semua primary key dan foreign key harus didefinisikan sesuai relasi.
    
2. Email akun dan nomor pendaftaran harus unik.
    
3. Kombinasi profil calon mahasiswa dan gelombang harus unik.
    
4. Perubahan status dan penambahan riwayat harus dilakukan secara konsisten dalam transaksi.
    
5. Penghapusan data yang memiliki relasi harus mengikuti kebijakan retensi data.
    
6. Dokumen dan metadata file harus dikelola secara konsisten.
    
7. Status pendaftaran harus dibatasi pada nilai yang diperbolehkan.
    
8. Validasi di backend tidak menggantikan constraint database untuk aturan integritas.
    

**Catatan:** tipe data, panjang kolom, strategi indeks, aturan penghapusan, dan migration Laravel lengkap akan ditentukan pada tahap implementasi database.

---

# 13. Rancangan REST API

Endpoint berikut merupakan rancangan awal.

|Method|Endpoint|Fungsi|
|---|---|---|
|POST|`/api/register`|Membuat akun|
|POST|`/api/login`|Login|
|POST|`/api/logout`|Logout|
|GET|`/api/waves`|Melihat gelombang aktif|
|GET|`/api/study-programs`|Melihat program studi|
|POST|`/api/applications`|Membuat pendaftaran|
|GET|`/api/applications`|Melihat pendaftaran milik pengguna|
|PUT|`/api/applications/{id}`|Memperbarui draft atau data revisi|
|POST|`/api/applications/{id}/submit`|Mengirim pendaftaran|
|POST|`/api/applications/{id}/documents`|Mengunggah dokumen|
|GET|`/api/applications/{id}/history`|Melihat riwayat status|
|GET|`/api/admin/applications`|Melihat pendaftar sesuai izin|
|POST|`/api/admin/applications/{id}/revision`|Meminta revisi|
|POST|`/api/admin/applications/{id}/decision`|Menetapkan keputusan|
|GET|`/api/admin/reports`|Mengakses data laporan|

Seluruh endpoint yang membutuhkan autentikasi harus memeriksa identitas, hak akses, kepemilikan data, dan validitas permintaan. Endpoint yang mengubah status harus menerapkan aturan transisi status.

---

# 14. Keamanan dan Privasi

Persyaratan keamanan awal:

1. Password disimpan menggunakan mekanisme hashing yang aman.
    
2. Akses endpoint dilindungi autentikasi.
    
3. Hak akses diterapkan melalui Role-Based Access Control (RBAC).
    
4. Backend memeriksa kepemilikan pendaftaran.
    
5. Upload file membatasi ekstensi, MIME type, dan ukuran.
    
6. Nama file yang disimpan tidak boleh menjadi sumber path traversal.
    
7. Dokumen disimpan di lokasi privat dan tidak diakses langsung secara publik.
    
8. Akses terhadap NIK dan dokumen dibatasi sesuai tugas.
    
9. Riwayat keputusan dan perubahan status dicatat.
    
10. Database dan dokumen memiliki strategi backup serta pemulihan.
    
11. Data pribadi hanya digunakan untuk keperluan penerimaan sesuai kebijakan yang berlaku.
    
12. Kebijakan retensi, penghapusan, dan perlindungan data perlu ditentukan sebelum sistem digunakan secara operasional.
    

---

# 15. Risiko dan Mitigasi

|Risiko|Dampak|Mitigasi|
|---|---|---|
|Pendaftaran ganda|Data duplikat|Unique constraint dan validasi backend|
|Dua permintaan bersamaan|Konflik data|Constraint database dan transaksi|
|Dokumen tidak valid|Proses pemeriksaan terganggu|Validasi file dan pesan kesalahan|
|Akses tanpa izin|Kebocoran data|Autentikasi, RBAC, dan pemeriksaan kepemilikan|
|Status tidak konsisten|Informasi pendaftaran tidak akurat|Transisi status terkontrol dan transaksi|
|Riwayat tidak lengkap|Sulit melakukan audit|Pencatatan status dan pelaku dalam transaksi|
|Server lokal tidak terjangkau|Android gagal berkomunikasi|Konfigurasi alamat server dan jaringan pengujian|
|Kehilangan data|Gangguan operasional|Backup dan uji pemulihan|
|Ketidaksesuaian data institusi|Informasi penerimaan salah|Verifikasi data resmi sebelum produksi|

---

# 16. Hal yang Masih Perlu Ditetapkan

Bagian ini tidak membatalkan persetujuan baseline PRD. Item berikut menjadi pekerjaan lanjutan sebelum implementasi atau penggunaan operasional.

|ID|Hal yang Belum Ditentukan|Tahap Penyelesaian|
|---|---|---|
|OPEN-01|Jadwal resmi setiap gelombang|Konfigurasi operasional|
|OPEN-02|Aturan validasi dan keunikan NIK|Analisis kebutuhan dan database|
|OPEN-03|Format serta batas ukuran file|Spesifikasi teknis|
|OPEN-04|Batas waktu revisi dan jumlah revisi|Aturan bisnis|
|OPEN-05|Prosedur perubahan keputusan akhir|Kebijakan operasional|
|OPEN-06|Isi dan format bukti pendaftaran|UI/UX dan implementasi|
|OPEN-07|Versi minimum Android|Spesifikasi teknis|
|OPEN-08|Target performa dan kapasitas pengguna|Pengujian performa|
|OPEN-09|Retensi dan penghapusan data|Kebijakan data|
|OPEN-10|Konfigurasi server dan jaringan lokal|Deployment|
|OPEN-11|Jadwal serta prosedur backup|Operasional|
|OPEN-12|Daftar program studi resmi|Verifikasi pihak institusi|

---

# 17. Kriteria Penyelesaian Produk

MVP dapat dinyatakan memenuhi kebutuhan dasar apabila:

- Calon mahasiswa dapat membuat akun dan login.
    
- Calon mahasiswa dapat mengisi dan menyimpan draft.
    
- Sistem mendukung gelombang dan program studi.
    
- Sistem mencegah pendaftaran ganda pada gelombang yang sama.
    
- Calon mahasiswa dapat mengunggah dokumen yang valid.
    
- Backend memvalidasi kelengkapan sebelum pengiriman.
    
- Panitia dapat memeriksa dokumen dan meminta revisi.
    
- Calon mahasiswa dapat mengirim ulang hasil revisi.
    
- Panitia berwenang dapat menetapkan keputusan akhir.
    
- Status dan riwayat pendaftaran tersimpan konsisten.
    
- Pengguna hanya dapat mengakses data sesuai haknya.
    
- Dashboard dan laporan berjalan sesuai cakupan MVP.
    
- Sistem lulus pengujian keamanan dan fungsional yang ditetapkan.
    
- Backup dan pemulihan data telah diuji sebelum penggunaan operasional.
    

---

# 18. Status Persetujuan Dokumen

**Nama dokumen:** PRD Final v1.0 — Sistem PMB ITB Akiday

**Status:** Baseline kebutuhan disetujui berdasarkan hasil review Tahap 5.

Hal-hal yang telah disepakati meliputi:

1. Platform Android Kotlin dan website admin.
    
2. Backend Laravel dan database MySQL.
    
3. Peran calon mahasiswa, admin, panitia, dan pimpinan.
    
4. Fitur utama MVP.
    
5. Aturan maksimal satu pendaftaran per akun pada setiap gelombang.
    
6. Pemisahan validasi otomatis dan keputusan panitia.
    
7. Alur revisi dan pencatatan riwayat status.
    
8. Penanganan detail teknis tersisa pada tahap desain dan implementasi.
    

Persetujuan ini merupakan persetujuan baseline untuk proyek dan bukan pengesahan resmi dari pihak institusi.

---

# 19. Tahap Selanjutnya

Setelah PRD disetujui, pekerjaan pengembangan dilanjutkan melalui urutan berikut:

1. **Finalisasi System Design:** menentukan komponen, modul, dan alur komunikasi secara rinci.
    
2. **Finalisasi Database Design:** menyelesaikan ERD, tipe data, indeks, constraint, dan aturan relasi.
    
3. **Pembuatan Migration Laravel:** membangun skema database berdasarkan rancangan.
    
4. **UI/UX Design:** membuat wireframe dan desain layar Android serta website admin.
    
5. **Implementasi Backend API:** autentikasi, pendaftaran, upload, status, dan laporan.
    
6. **Implementasi Android:** membangun antarmuka calon mahasiswa.
    
7. **Implementasi Website Admin:** membangun pengelolaan dan pemeriksaan pendaftaran.
    
8. **Testing:** pengujian fungsional, integritas database, keamanan, dan performa.
    
9. **Deployment:** menjalankan sistem pada lingkungan yang ditentukan.
    

---

**Akhir Dokumen PRD Final v1.0**

Dokumen ini menjadi acuan kebutuhan produk dan rancangan awal. Perubahan ruang lingkup atau aturan bisnis setelah baseline disetujui harus dicatat sebagai revisi dokumen agar kebutuhan, database, dan implementasi tetap selaras.