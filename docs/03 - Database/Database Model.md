# Database Model — PMB ITB Akiday

## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Proyek|Sistem Penerimaan Mahasiswa Baru ITB Akiday|
|Nama Dokumen|Database Model|
|Versi|1.1 (usulan Tahap 3)|
|Status|REVISED DESIGN — usulan untuk implementasi; perlu verifikasi formal sebelum dijadikan baseline database|
|Tanggal Baseline|18 Oktober 2026|
|Tanggal Revisi|28 Oktober 2026|
|DBMS|MySQL|
|Backend|Laravel|
|Referensi|PRD Final v1.0, SRS.md, System Design.md, Workflow & Business Process.md, Data Dictionary.md|

## 2. Tujuan

Database Model merupakan rancangan struktur penyimpanan data untuk Sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday. Dokumen ini digunakan sebagai acuan dalam membuat migration Laravel, model Eloquent, relasi antartabel, constraint, dan pengujian integritas database.

Rancangan database harus mendukung proses registrasi akun, pengelolaan profil, pembuatan pendaftaran, pengunggahan dokumen, pemeriksaan panitia, permintaan revisi, keputusan akhir, dan pelaporan penerimaan.

Database dirancang agar data tidak mudah terduplikasi, hubungan antartabel tetap valid, dan riwayat proses penerimaan dapat ditelusuri.

## 3. Prinsip Perancangan Database

Perancangan database menggunakan prinsip berikut:

1. **Relasional:** data disimpan pada tabel yang memiliki hubungan jelas melalui primary key dan foreign key.
    
2. **Integritas data:** constraint digunakan untuk mencegah data yang tidak valid.
    
3. **Normalisasi:** informasi akun, profil, gelombang, program studi, pendaftaran, dokumen, dan riwayat dipisahkan sesuai tanggung jawabnya.
    
4. **Keamanan:** data pribadi dan dokumen hanya dapat diakses oleh pihak yang memiliki hak.
    
5. **Konsistensi:** perubahan status dan riwayatnya disimpan secara konsisten.
    
6. **Auditabilitas:** perubahan status penting memiliki catatan pelaku, waktu, dan keterangan.
    
7. **Kemudahan pemeliharaan:** struktur tabel mengikuti konvensi Laravel dan mudah dikembangkan.
    
8. **Pencegahan duplikasi:** aturan bisnis yang penting diperkuat dengan unique constraint pada database.
    

## 4. Entitas Utama

Rancangan database mempertahankan delapan tabel domain utama pada baseline dan mengusulkan satu tabel teknis pencatatan aktivitas audit karena Security Specification mewajibkan audit atas tindakan sensitif. Revised design mempunyai delapan tabel domain serta `audit_logs` sebagai tambahan opsional; `audit_logs` tetap usulan yang memerlukan persetujuan dan penetapan retensi/cakupan final.

|No.|Kelompok|Tabel|
|---|---|---|
|1–8|Entitas domain baseline|`roles`, `users`, `applicant_profiles`, `admission_waves`, `study_programs`, `applications`, `application_documents`, `application_status_histories`|
|Opsional/usulan|Audit keamanan|`audit_logs` — perubahan akun/master/keputusan, akses dokumen, ekspor dan peristiwa lain sesuai cakupan yang disetujui| 

`audit_logs` tidak menggantikan riwayat status domain. Database mengusulkan penambahannya karena Security Specification mensyaratkan audit; pemilik sistem masih perlu mengonfirmasi implementasi dan kebijakan retensi. Isi dokumen, password, token, dan data pribadi yang tidak diperlukan tidak boleh disimpan dalam log.

Database model baseline sebelum Tahap 3 memuat delapan tabel domain utama. Pada revised design ini, `audit_logs` tetap merupakan usulan tambahan, bukan skema yang telah disetujui atau diterapkan. Tidak ada tabel notifikasi ditambahkan.

## 5. Entity Relationship Diagram

```
erDiagram
    ROLES ||--o{ USERS : memiliki
    USERS ||--o| APPLICANT_PROFILES : memiliki
    APPLICANT_PROFILES ||--o{ APPLICATIONS : membuat
    ADMISSION_WAVES ||--o{ APPLICATIONS : menampung
    STUDY_PROGRAMS ||--o{ APPLICATIONS : dipilih
    APPLICATIONS ||--o{ APPLICATION_DOCUMENTS : memiliki
    APPLICATIONS ||--o{ APPLICATION_STATUS_HISTORIES : mencatat
    USERS o|--o{ APPLICATION_STATUS_HISTORIES : mengubah
    %% audit_logs adalah usulan opsional sampai cakupan dan retensi disetujui.
    USERS o|--o{ AUDIT_LOGS : melakukan

    ROLES {
        bigint id PK
        varchar name UK
    }

    USERS {
        bigint id PK
        bigint role_id FK
        varchar email UK
        varchar password
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    APPLICANT_PROFILES {
        bigint id PK
        bigint user_id FK
        varchar full_name
        varchar nik
        date birth_date
        varchar phone
    }

    ADMISSION_WAVES {
        bigint id PK
        varchar name
        datetime start_date
        datetime end_date
        boolean is_active
    }

    STUDY_PROGRAMS {
        bigint id PK
        varchar code UK
        varchar name
        boolean is_active
    }

    APPLICATIONS {
        bigint id PK
        bigint applicant_id FK
        bigint wave_id FK
        bigint study_program_id FK
        varchar registration_number UK
        varchar status
        datetime submitted_at
    }

    APPLICATION_DOCUMENTS {
        bigint id PK
        bigint application_id FK
        varchar document_type
        varchar file_path
        varchar mime_type
        bigint file_size
        datetime uploaded_at
    }

    APPLICATION_STATUS_HISTORIES {
        bigint id PK
        bigint application_id FK
        varchar from_status
        varchar to_status
        text note
        bigint changed_by_user_id FK
        timestamp created_at
    }
```

Diagram di atas dapat dirender langsung di Obsidian yang mendukung Mermaid.

## 6. Relasi Antarentitas

### 6.1. `roles` dan `users`

Satu peran dapat digunakan oleh banyak akun. Setiap akun memiliki satu peran utama melalui `users.role_id`.

Relasi:

- `roles.id` → `users.role_id`
    
- Kardinalitas: satu ke banyak.
    

Contoh: peran `panitia` dapat dimiliki oleh beberapa akun panitia.

### 6.2. `users` dan `applicant_profiles`

Satu akun calon mahasiswa memiliki paling banyak satu profil.

Relasi:

- `users.id` → `applicant_profiles.user_id`
    
- Kardinalitas: satu ke nol atau satu.
    

Unique constraint pada `applicant_profiles.user_id` mencegah satu akun memiliki beberapa profil calon mahasiswa. Akun admin, panitia, dan pimpinan tidak harus memiliki profil calon mahasiswa.

### 6.3. `applicant_profiles` dan `applications`

Satu profil dapat memiliki beberapa pendaftaran pada gelombang yang berbeda.

Relasi:

- `applicant_profiles.id` → `applications.applicant_id`
    
- Kardinalitas: satu ke banyak.
    

Aturan penting: kombinasi `applicant_id` dan `wave_id` harus unik. Dengan demikian, satu calon mahasiswa hanya memiliki satu pendaftaran pada gelombang yang sama, tetapi tetap dapat mendaftar pada gelombang lain jika diizinkan.

### 6.4. `admission_waves` dan `applications`

Satu gelombang penerimaan dapat memiliki banyak pendaftaran.

Relasi:

- `admission_waves.id` → `applications.wave_id`
    
- Kardinalitas: satu ke banyak.
    

Gelombang yang sudah digunakan sebaiknya dinonaktifkan daripada dihapus agar riwayat pendaftaran tetap terjaga.

### 6.5. `study_programs` dan `applications`

Satu program studi dapat dipilih oleh banyak calon mahasiswa.

Relasi:

- `study_programs.id` → `applications.study_program_id`
    
- Kardinalitas: satu ke banyak.
    

Setiap pendaftaran hanya memiliki satu `study_program_id`. Program studi yang sudah dipakai pada pendaftaran lama tidak boleh dihapus sembarangan.

### 6.6. `applications` dan `application_documents`

Satu pendaftaran dapat memiliki beberapa dokumen.

Relasi:

- `applications.id` → `application_documents.application_id`
    
- Kardinalitas: satu ke banyak.
    

Setiap dokumen terhubung dengan satu pendaftaran. File fisik berada di private storage, sedangkan tabel dokumen menyimpan metadata file.

### 6.7. `applications` dan `application_status_histories`

Satu pendaftaran dapat memiliki banyak riwayat perubahan status.

Relasi:

- `applications.id` → `application_status_histories.application_id`
    
- Kardinalitas: satu ke banyak.
    

Riwayat diperlukan untuk mengetahui perkembangan pendaftaran dari `draft` sampai keputusan akhir.

### 6.8. `users` dan `application_status_histories`

Satu pengguna dapat melakukan banyak perubahan status. Kolom `changed_by_user_id` dapat bernilai `NULL` jika riwayat dibuat oleh proses sistem yang sah.

Relasi:

- `users.id` → `application_status_histories.changed_by_user_id`
    
- Kardinalitas: satu pengguna ke banyak riwayat.
    

## 7. Rancangan Struktur Tabel

### 7.1. Tabel `roles`

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID peran.|
|`name`|VARCHAR(50)|NOT NULL, UNIQUE|Kode peran.|

Nilai awal yang direkomendasikan: `calon_mahasiswa`, `admin`, `panitia`, dan `pimpinan`.

### 7.2. Tabel `users`

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID akun.|
|`role_id`|BIGINT UNSIGNED|FK, NOT NULL|Peran akun.|
|`email`|VARCHAR(255)|NOT NULL, UNIQUE|Email login.|
|`password`|VARCHAR(255)|NOT NULL|Hash password.|
|`is_active`|BOOLEAN|NOT NULL, DEFAULT TRUE|Status akun.|
|`created_at`|TIMESTAMP|NULL|Waktu pembuatan.|
|`updated_at`|TIMESTAMP|NULL|Waktu pembaruan.|

Catatan: implementasi autentikasi Laravel dapat memerlukan kolom atau tabel tambahan sesuai mekanisme yang dipilih. Struktur tersebut perlu diselaraskan dengan kebutuhan framework.

### 7.3. Tabel `applicant_profiles`

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID profil.|
|`user_id`|BIGINT UNSIGNED|FK, UNIQUE, NOT NULL|Akun pemilik profil.|
|`full_name`|VARCHAR(150)|NOT NULL|Nama lengkap.|
|`nik`|VARCHAR(16)|NOT NULL|Nomor identitas.|
|`birth_place`|VARCHAR(100)|NOT NULL|Tempat lahir.|
|`birth_date`|DATE|NOT NULL|Tanggal lahir.|
|`gender`|VARCHAR(20)|NOT NULL|Jenis kelamin sesuai pilihan formulir.|
|`phone`|VARCHAR(20)|NOT NULL|Nomor telepon.|
|`address`|TEXT|NOT NULL|Alamat.|
|`created_at`|TIMESTAMP|NULL|Waktu pembuatan.|
|`updated_at`|TIMESTAMP|NULL|Waktu pembaruan.|

NIK menggunakan `VARCHAR`, bukan tipe numerik. Kebijakan keunikan NIK masih harus dikonfirmasi sebelum unique constraint diterapkan.

### 7.4. Tabel `admission_waves`

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID gelombang.|
|`name`|VARCHAR(100)|NOT NULL|Nama gelombang.|
|`start_date`|DATETIME|NOT NULL|Waktu mulai.|
|`end_date`|DATETIME|NOT NULL|Waktu berakhir.|
|`is_active`|BOOLEAN|NOT NULL, DEFAULT TRUE|Status aktif.|
|`created_at`|TIMESTAMP|NULL|Waktu pembuatan.|
|`updated_at`|TIMESTAMP|NULL|Waktu pembaruan.|

Backend harus memeriksa bahwa waktu saat ini berada pada rentang penerimaan dan gelombang diaktifkan sebelum menerima pendaftaran.

### 7.5. Tabel `study_programs`

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID program studi.|
|`code`|VARCHAR(20)|NOT NULL, UNIQUE|Kode program studi.|
|`name`|VARCHAR(150)|NOT NULL|Nama program studi.|
|`is_active`|BOOLEAN|NOT NULL, DEFAULT TRUE|Status aktif.|
|`created_at`|TIMESTAMP|NULL|Waktu pembuatan.|
|`updated_at`|TIMESTAMP|NULL|Waktu pembaruan.|

### 7.6. Tabel `applications`

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID pendaftaran.|
|`applicant_id`|BIGINT UNSIGNED|FK, NOT NULL|Profil calon mahasiswa.|
|`wave_id`|BIGINT UNSIGNED|FK, NOT NULL|Gelombang yang dipilih.|
|`study_program_id`|BIGINT UNSIGNED|FK, NOT NULL|Program studi yang dipilih.|
|`registration_number`|VARCHAR(50)|NOT NULL, UNIQUE|Nomor pendaftaran.|
|`status`|VARCHAR(20)|NOT NULL, DEFAULT `draft`|Status pendaftaran.|
|`submitted_at`|DATETIME|NULL|Waktu pengiriman.|
|`created_at`|TIMESTAMP|NULL|Waktu pembuatan.|
|`updated_at`|TIMESTAMP|NULL|Waktu pembaruan.|

Constraint unik yang wajib:

```
UNIQUE (applicant_id, wave_id)
```

Indeks tambahan yang direkomendasikan:

```
INDEX (status)
INDEX (wave_id, status)
INDEX (study_program_id, status)
```

Indeks tersebut membantu query daftar pendaftaran, pemantauan status, dan laporan berdasarkan gelombang maupun program studi.

Status yang diperbolehkan:

- `draft`
    
- `submitted`
    
- `review`
    
- `revision`
    
- `accepted`
    
- `rejected`
    

Pemeriksaan transisi status dilakukan di backend. Database constraint dapat digunakan sebagai lapisan tambahan sesuai kemampuan versi MySQL.

### 7.7. Tabel `application_documents`

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID dokumen.|
|`application_id`|BIGINT UNSIGNED|FK, NOT NULL|Pendaftaran pemilik dokumen.|
|`document_type`|VARCHAR(30)|NOT NULL|Jenis dokumen.|
|`file_path`|VARCHAR(255)|NOT NULL|Kunci/lokasi relatif di private disk, bukan URL publik.|
|`mime_type`|VARCHAR(100)|NOT NULL|Tipe media hasil inspeksi backend.|
|`file_size`|BIGINT UNSIGNED|NOT NULL|Ukuran file dalam byte.|
|`uploaded_at`|DATETIME|NOT NULL|Waktu unggah sukses.|
|`created_at`|TIMESTAMP|NULL|Waktu metadata dibuat.|
|`updated_at`|TIMESTAMP|NULL|Waktu metadata diperbarui.|

Indeks wajib `application_id`. `document_type` hanya boleh bernilai jenis yang disetujui, saat ini `photo` atau `diploma` (yang dapat mewakili ijazah/SKL). Ukuran maksimum dan MIME allowlist belum diputuskan; jangan mengunci nilai final sebelum kebijakan unggah ditetapkan.

Semantik versi masih terbuka. Desain awal memakai baris per file/unggahan; jangan menerapkan `UNIQUE(application_id, document_type)`, penghapusan otomatis, ataupun mengklaim riwayat versi sebelum aturan penggantian dokumen diputuskan. Jika diperlukan audit pergantian, tentukan desain versi dan referensi berkas terlebih dahulu.

UI menampilkan status pemeriksaan dan catatan untuk tiap dokumen, dan workflow mengharuskan panitia mencatat hasil pemeriksaan. Namun spesifikasi domain belum menetapkan daftar nilai status/atribut yang normatif. Karena itu tidak ada kolom `verification_status` atau tabel review dokumen yang ditambahkan sepihak: blocker keputusan ini dicatat di §17 dan harus diselesaikan sebelum skema pemeriksaan diimplementasikan.

### 7.8. Tabel `application_status_histories`

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID riwayat.|
|`application_id`|BIGINT UNSIGNED|FK, NOT NULL|Pendaftaran terkait.|
|`from_status`|VARCHAR(20)|NULL|Status sebelumnya; NULL hanya untuk status awal.|
|`to_status`|VARCHAR(20)|NOT NULL|Status tujuan yang telah divalidasi di backend.|
|`note`|TEXT|NULL|Catatan transisi/revisi/keputusan yang diizinkan.|
|`changed_by_user_id`|BIGINT UNSIGNED|FK, NULL|Pengguna pemicu; NULL hanya untuk proses sistem yang sah.|
|`created_at`|TIMESTAMP|NOT NULL|Waktu transisi.|

Indeks wajib `(application_id, created_at)` untuk urutan riwayat. Indeks terpisah `to_status` belum terbukti dibutuhkan dan hanya ditambahkan jika query/EXPLAIN dan workload mendukungnya. Transisi dan baris riwayat harus ditulis dalam satu transaksi; riwayat tidak dapat diedit/dihapus melalui operasi aplikasi normal. FK untuk `changed_by_user_id` sebaiknya `ON DELETE RESTRICT` guna mempertahankan audit pelaku; penonaktifan akun lebih aman daripada penghapusan akun yang memiliki audit.



### 7.9. Tabel `audit_logs` (Usulan Tahap 3)

Model rinci tambahan ini menggunakan struktur berikut:

|Kolom|Tipe Data|Constraint|Keterangan|
|---|---|---|---|
|`id`|BIGINT UNSIGNED|PK, AUTO_INCREMENT|ID log audit.|
|`actor_user_id`|BIGINT UNSIGNED|FK, NULL|Aktor terautentikasi atau NULL untuk aksi anonim/sistem yang sesuai kebijakan; `ON DELETE SET NULL`.|
|`action`|VARCHAR(100)|NOT NULL|Kode aksi terkontrol, tanpa payload bebas.|
|`subject_type`|VARCHAR(100)|NOT NULL|Jenis subjek polymorphic dari allowlist aplikasi.|
|`subject_id`|BIGINT UNSIGNED|NULL|ID objek terkait; pasangan polymorphic tidak dapat memakai FK biasa.|
|`result`|VARCHAR(20)|NOT NULL|Hasil aksi, misalnya `success`/`failure`.|
|`reason`|VARCHAR(500)|NULL|Alasan secukupnya, dipangkas/difilter dari data sensitif.|
|`ip_address`|VARCHAR(45)|NULL|Alamat IPv4/IPv6 sesuai kebutuhan dan kebijakan.|
|`user_agent`|VARCHAR(500)|NULL|Nilai terpotong jika dikumpulkan.|
|`created_at`|TIMESTAMP|NOT NULL|Waktu pencatatan; tidak ada `updated_at`.|

Indeks awal: `(created_at)`, `(actor_user_id, created_at)`, `(subject_type, subject_id, created_at)`, `(action, created_at)`. Tabel ini adalah usulan desain hasil Tahap 3 mengikuti persyaratan audit Security Specification, bukan bagian dari baseline delapan tabel. Tetapkan retensi dan audit anonim sebelum implementasi produksi; jangan simpan password, token, isi file, NIK utuh, atau payload pribadi.

## 8. Normalisasi Database

Database dirancang dengan pemisahan data sesuai fungsi tabel untuk mengurangi redundansi.

### 8.1. First Normal Form (1NF)

Setiap kolom menyimpan satu nilai yang sesuai dengan jenis datanya. Daftar dokumen tidak disimpan sebagai kumpulan nilai dalam satu kolom, tetapi setiap dokumen memiliki baris tersendiri pada `application_documents`.

### 8.2. Second Normal Form (2NF)

Setiap atribut non-key bergantung pada primary key tabelnya. Identitas akun, profil, gelombang, dan program studi dipisahkan ke tabel masing-masing sehingga informasi tersebut tidak perlu diulang pada setiap pendaftaran.

### 8.3. Third Normal Form (3NF)

Data yang tidak bergantung langsung pada identitas utama dipisahkan dari tabel pendaftaran. Contohnya, nama program studi disimpan di `study_programs`, bukan diulang sebagai teks pada setiap baris `applications`.

Pendekatan ini membantu mengurangi inkonsistensi ketika informasi program studi atau data akun diperbarui.

## 9. Strategi Integritas Data

### 9.1. Primary Key

Setiap tabel memiliki primary key `id` dengan tipe `BIGINT UNSIGNED`. Primary key digunakan untuk mengidentifikasi setiap baris secara unik.

### 9.2. Foreign Key

Foreign key menjaga agar relasi antartabel tidak menunjuk pada data yang tidak ada.

Contoh:

- `users.role_id` harus merujuk ke `roles.id`.
    
- `applications.applicant_id` harus merujuk ke `applicant_profiles.id`.
    
- `application_documents.application_id` harus merujuk ke `applications.id`.
    

### 9.3. Unique Constraint

Unique constraint diterapkan pada nilai yang tidak boleh duplikat, seperti:

- `roles.name`
    
- `users.email`
    
- `applicant_profiles.user_id`
    
- `study_programs.code`
    
- `applications.registration_number`
    
- Kombinasi `applications.applicant_id` dan `applications.wave_id`
    

Unique constraint pada pendaftaran sangat penting karena validasi aplikasi saja tidak cukup untuk mencegah dua request bersamaan membuat data duplikat.

### 9.4. Not Null

Kolom yang wajib diisi harus menggunakan `NOT NULL`. Kolom yang boleh kosong, seperti `submitted_at` sebelum pengiriman dan `from_status` untuk riwayat awal, menggunakan `NULL` sesuai kebutuhan.

### 9.5. Strategi Penghapusan Relasi

Untuk tabel yang menyimpan riwayat penerimaan, kebijakan penghapusan harus berhati-hati.

- Gunakan `RESTRICT` atau perilaku pembatasan untuk mencegah penghapusan data induk yang masih direferensikan.
    
- Jangan menghapus gelombang dan program studi yang sudah digunakan oleh pendaftaran.
    
- Pertimbangkan soft delete untuk entitas tertentu jika sesuai kebijakan.
    
- Gunakan `CASCADE` hanya jika penghapusan data turunan memang diinginkan dan tidak menghilangkan catatan yang harus dipertahankan.
    

Perilaku foreign key harus ditetapkan secara eksplisit pada migration.

## 10. Transaksi Database

Transaksi diperlukan saat satu proses mengubah beberapa data yang harus tetap konsisten.

### 10.1. Pengiriman Pendaftaran

Saat pendaftaran dikirim, backend harus:

1. Memeriksa status pendaftaran.
    
2. Memvalidasi data wajib.
    
3. Memastikan dokumen wajib tersedia.
    
4. Memastikan gelombang memenuhi aturan waktu dan status.
    
5. Memperbarui status menjadi `submitted`.
    
6. Mengisi `submitted_at`.
    
7. Menambahkan riwayat status.
    

Perubahan status, waktu pengiriman, dan riwayat harus disimpan dalam satu transaksi. Jika proses database gagal, perubahan database dibatalkan.

### 10.2. Perubahan Status

Saat panitia meminta revisi atau pihak berwenang menetapkan keputusan, backend harus:

1. Memeriksa autentikasi dan hak akses.
    
2. Membaca status saat ini.
    
3. Memastikan transisi status diperbolehkan.
    
4. Memperbarui status pendaftaran.
    
5. Menambahkan riwayat perubahan.
    
6. Melakukan commit transaksi.
    

Jika beberapa pengguna memproses pendaftaran yang sama secara bersamaan, backend perlu menggunakan strategi concurrency control yang sesuai, misalnya penguncian baris atau pembaruan bersyarat.

### 10.3. Pembuatan Pendaftaran

Pembuatan pendaftaran harus memperhatikan kemungkinan request bersamaan. Backend memeriksa pendaftaran yang sudah ada, tetapi unique constraint tetap menjadi perlindungan terakhir.

Jika database menolak data karena constraint unik, backend harus menangani konflik dan mengembalikan respons yang jelas, bukan respons kesalahan internal yang membingungkan.

## 11. Strategi Penyimpanan Dokumen

File fisik disimpan di private storage, sedangkan database menyimpan metadata file.

Alur penyimpanan:

1. Backend menerima unggahan.
    
2. Backend memvalidasi tipe file, ukuran, dan hak akses.
    
3. File disimpan dengan nama internal yang aman.
    
4. Metadata disimpan ke `application_documents`.
    
5. File hanya dapat diakses melalui proses yang memeriksa hak akses.
    

Penyimpanan file dan database tidak berada dalam satu transaksi MySQL. Karena itu, backend perlu menangani kemungkinan kegagalan parsial. Misalnya, jika penyimpanan metadata gagal setelah file berhasil disimpan, file sementara perlu dibersihkan atau ditangani melalui mekanisme pemulihan.

## 12. Strategi Query dan Pelaporan

Query harus menggunakan relasi yang sesuai dan menghindari pengambilan data yang tidak dibutuhkan.

Contoh kebutuhan query:

- Daftar pendaftaran milik calon mahasiswa.
    
- Daftar pendaftaran yang menunggu pemeriksaan.
    
- Daftar pendaftaran yang memerlukan revisi.
    
- Rekapitulasi status berdasarkan gelombang.
    
- Rekapitulasi pendaftar berdasarkan program studi.
    
- Riwayat status berdasarkan pendaftaran.
    
- Dokumen yang terkait dengan pendaftaran tertentu.
    

Untuk laporan, gunakan agregasi database yang sesuai, misalnya `COUNT` dan `GROUP BY`. Indeks dipilih berdasarkan pola query yang nyata, bukan hanya menambahkan indeks ke semua kolom.

Data sensitif seperti NIK dan alamat tidak perlu diambil jika laporan hanya membutuhkan jumlah pendaftar.

## 13. Mapping Model Eloquent

Pemetaan model Laravel yang disarankan:

|Model|Tabel|Relasi Utama|
|---|---|---|
|`Role`|`roles`|`hasMany(User::class)`|
|`User`|`users`|`belongsTo(Role::class)`, `hasOne(ApplicantProfile::class)`|
|`ApplicantProfile`|`applicant_profiles`|`belongsTo(User::class)`, `hasMany(Application::class)`|
|`AdmissionWave`|`admission_waves`|`hasMany(Application::class)`|
|`StudyProgram`|`study_programs`|`hasMany(Application::class)`|
|`Application`|`applications`|`belongsTo(ApplicantProfile::class)`, `belongsTo(AdmissionWave::class)`, `belongsTo(StudyProgram::class)`, `hasMany(ApplicationDocument::class)`, `hasMany(ApplicationStatusHistory::class)`|
|`ApplicationDocument`|`application_documents`|`belongsTo(Application::class)`|
|`ApplicationStatusHistory`|`application_status_histories`|`belongsTo(Application::class)`, `belongsTo(User::class, 'changed_by_user_id')`|
|`AuditLog` (opsional/usulan)|`audit_logs`|`belongsTo(User::class, 'actor_user_id')`; subject polymorphic terkontrol aplikasi|

Relasi Eloquent membantu mengelola data terkait, tetapi tidak menggantikan constraint database. Integritas data tetap harus dijaga oleh kedua lapisan.

## 14. Rencana Urutan Migration

Migration disusun berdasarkan urutan ketergantungan foreign key:

1. `create_roles_table`
    
2. `create_users_table`
    
3. `create_applicant_profiles_table`
    
4. `create_admission_waves_table`
    
5. `create_study_programs_table`
    
6. `create_applications_table`
    
7. `create_application_documents_table`
    
8. `create_application_status_histories_table`
    
9. `create_audit_logs_table` hanya jika tabel audit opsional disetujui, termasuk cakupan event dan retensi

Urutan tersebut memudahkan pembuatan foreign key karena tabel yang dirujuk dibuat terlebih dahulu.

Jika Laravel menggunakan migration bawaan untuk tabel autentikasi atau tabel token, migration tambahan perlu diselaraskan agar tidak membuat tabel duplikat.

## 15. Data Awal (Seeder)

Seeder dapat digunakan untuk menyiapkan data awal pada lingkungan pengembangan.

Contoh data yang dapat disiapkan:

- Peran `calon_mahasiswa`.
    
- Peran `admin`.
    
- Peran `panitia`.
    
- Peran `pimpinan`.
    
- Program studi prototipe yang telah disetujui untuk pengujian.
    
- Gelombang penerimaan dummy untuk pengujian alur pendaftaran.
    

Seeder tidak boleh membuat akun admin dengan password yang diketahui umum pada lingkungan produksi. Kredensial awal harus disiapkan secara aman dan diubah sesuai kebijakan deployment.

Program studi dan jadwal dummy harus diberi penanda yang jelas agar tidak dianggap sebagai data resmi institusi.

## 16. Pengujian Database

Pengujian minimum meliputi:

- Menolak email akun yang duplikat.
    
- Menolak kode program studi yang duplikat.
    
- Menolak nomor pendaftaran yang duplikat.
    
- Menolak pendaftaran kedua untuk calon mahasiswa pada gelombang yang sama.
    
- Mengizinkan pendaftaran pada gelombang lain jika aturan mengizinkan.
    
- Menolak foreign key yang tidak valid.
    
- Memastikan perubahan status dan riwayat tersimpan secara konsisten.
    
- Memastikan transaksi dibatalkan jika salah satu langkah database gagal.
    
- Memastikan penghapusan data tidak merusak riwayat yang diperlukan.
    
- Memastikan dokumen hanya dapat diakses oleh pihak berwenang melalui backend.
    

Hasil pengujian aktual harus dicatat dalam `08 - Testing/Test Results.md`.

## 17. Hal yang Masih Perlu Diputuskan

Sebelum database diimplementasikan secara final, hal-hal berikut perlu dikonfirmasi:

1. Kebijakan keunikan NIK.
2. Batas tipe dan ukuran dokumen.
3. Apakah setiap dokumen boleh memiliki beberapa versi; apakah `document_type` dibatasi menjadi satu unggahan aktif per aplikasi.
4. Cara pencatatan hasil/catatan pemeriksaan per dokumen. UI dan alur bisnis memerlukan status per dokumen, sedangkan PRD dan MOD-06 hanya menentukan metadata dokumen; jangan menambahkan skema hasil pemeriksaan/versi tanpa keputusan pemilik proses.
5. Jika tetap wajib dicatat, apakah login gagal, akses dokumen, ekspor, dan percobaan tanpa sesi dicatat sebagai audit persisten; retensi dan akses `audit_logs`.
6. Format nomor pendaftaran.
7. Kebijakan perubahan keputusan akhir.
8. Kebijakan soft delete dan retensi data, termasuk retention/purge audit serta taksonomi subjek polymorphic.
9. Mekanisme autentikasi Laravel yang digunakan dan kebutuhan tabel token.
10. Zona waktu yang menjadi acuan.
11. Kebijakan backup dan pemulihan data.
    

## 18. Kriteria Penyelesaian

- Seluruh entitas utama telah ditentukan.
    
- Relasi dan kardinalitas telah didokumentasikan.
    
- Primary key dan foreign key telah ditetapkan.
    
- Unique constraint untuk integritas data telah direncanakan.
    
- Strategi transaksi dan concurrency control telah dijelaskan.
    
- Strategi penyimpanan dokumen privat telah dijelaskan.
    
- Mapping model Eloquent telah disiapkan.
    
- Urutan migration telah ditentukan.
    
- Pengujian integritas database telah direncanakan.
    
- Keputusan yang belum pasti telah dicatat.
    

## 19. Dokumen Berikutnya

Setelah Database Model disimpan, lanjutkan dengan:

1. `ERD.md` — diagram relasi database dalam format yang mudah dibaca.
    
2. `Migration Plan.md` — rencana pembuatan migration Laravel secara bertahap.
    
3. `04 - API/API Contract.md` — definisi endpoint, parameter, respons, dan aturan akses.
    

Rancangan v1.1 ini adalah hasil analisis desain Tahap 3, bukan persetujuan baseline skema atau implementasi migration. Delapan tabel domain baseline tetap tidak berubah; `audit_logs` adalah opsi desain yang masih menunggu persetujuan, keputusan retensi/cakupan, dan taksonomi peristiwa. Hasil pemeriksaan dokumen per berkas juga memerlukan keputusan bisnis sebelum skema final. Tidak ada database runtime, migration, atau data yang diubah pada Tahap 3 ini.