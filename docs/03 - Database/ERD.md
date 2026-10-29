# ERD — PMB ITB Akiday

## 1. Informasi Dokumen

|Informasi|Detail|
|---|---|
|Nama Proyek|PMB ITB Akiday|
|Nama Dokumen|Entity Relationship Diagram (ERD)|
|Versi|1.1 (usulan Tahap 3)|
|Status|REVISED DESIGN — usulan untuk implementasi; baseline delapan tabel dipertahankan sebagai riwayat|
|Tanggal Baseline|18 Oktober 2026|
|Tanggal Revisi|28 Oktober 2026|
|DBMS|MySQL|
|Referensi|PRD Final v1.0, SRS.md, Database Model.md, Data Dictionary.md|

---

## 2. Tujuan ERD

Entity Relationship Diagram (ERD) merupakan diagram yang menggambarkan struktur basis data serta hubungan antarentitas dalam sebuah sistem. Pada proyek PMB ITB Akiday, ERD digunakan sebagai acuan untuk merancang database yang mendukung proses pendaftaran mahasiswa baru, pengunggahan dokumen, pemeriksaan berkas, penentuan hasil seleksi, dan pemantauan status pendaftaran.

ERD ini dirancang agar data tersimpan secara terstruktur, mengurangi duplikasi data, menjaga integritas hubungan antar tabel, serta memudahkan pengembangan fitur laporan dan pengelolaan pendaftaran oleh admin dan panitia.

## 3. Daftar Entitas

Baseline domain menggunakan delapan entitas utama berikut. Revised design mengusulkan entitas audit opsional `audit_logs`, bergantung keputusan cakupan dan retensi.

|No.|Entitas|Fungsi|
|---|---|---|
|1|`roles`|Menyimpan daftar peran pengguna sistem.|
|2|`users`|Menyimpan akun login calon mahasiswa dan pengguna admin.|
|3|`applicant_profiles`|Menyimpan data pribadi calon mahasiswa.|
|4|`admission_waves`|Menyimpan informasi gelombang penerimaan mahasiswa baru.|
|5|`study_programs`|Menyimpan daftar program studi yang tersedia dalam sistem.|
|6|`applications`|Menyimpan data pengajuan pendaftaran calon mahasiswa.|
|7|`application_documents`|Menyimpan metadata dokumen yang diunggah untuk pendaftaran.|
|8|`application_status_histories`|Mencatat perubahan status pendaftaran beserta keterangannya.|

**Catatan:** Daftar program studi yang digunakan pada tahap prototipe adalah Informatika, Sistem Informasi, Teknik Industri, dan Manajemen. Daftar tersebut masih perlu diverifikasi dengan informasi resmi institusi sebelum digunakan pada sistem produksi.

## 4. Entity Relationship Diagram

Diagram berikut menggunakan sintaks Mermaid sehingga dapat ditampilkan langsung di Obsidian.

```
erDiagram
    ROLES ||--o{ USERS : memiliki
    USERS ||--o| APPLICANT_PROFILES : mempunyai
    APPLICANT_PROFILES ||--o{ APPLICATIONS : mengajukan
    ADMISSION_WAVES ||--o{ APPLICATIONS : menampung
    STUDY_PROGRAMS ||--o{ APPLICATIONS : dipilih
    APPLICATIONS ||--o{ APPLICATION_DOCUMENTS : memiliki
    APPLICATIONS ||--o{ APPLICATION_STATUS_HISTORIES : mencatat
    USERS o|--o{ APPLICATION_STATUS_HISTORIES : mengubah
    %% Relasi audit_logs adalah tambahan opsional sampai cakupan dan retensi disetujui.
    USERS o|--o{ AUDIT_LOGS : menghasilkan

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
        bigint user_id FK_UK
        varchar full_name
        varchar nik
        varchar birth_place
        date birth_date
        varchar gender
        varchar phone
        text address
        timestamp created_at
        timestamp updated_at
    }

    ADMISSION_WAVES {
        bigint id PK
        varchar name
        datetime start_date
        datetime end_date
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    STUDY_PROGRAMS {
        bigint id PK
        varchar code UK
        varchar name
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    APPLICATIONS {
        bigint id PK
        bigint applicant_id FK
        bigint wave_id FK
        bigint study_program_id FK
        varchar registration_number UK
        varchar status
        datetime submitted_at
        timestamp created_at
        timestamp updated_at
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

    AUDIT_LOGS {
        bigint id PK
        bigint actor_user_id FK
        varchar action
        varchar subject_type
        bigint subject_id
        varchar result
        varchar reason
        varchar ip_address
        varchar user_agent
        timestamp created_at
    }
```

Keterangan simbol:

- `PK` (_Primary Key_): identitas unik setiap baris data.
    
- `FK` (_Foreign Key_): kolom yang mengacu pada data di tabel lain.
    
- `UK` (_Unique Key_): nilai yang tidak boleh duplikat.
    
- `FK_UK`: kolom yang berfungsi sebagai foreign key sekaligus memiliki batasan unik.
    
- `||`: tepat satu.
    
- `o|`: nol atau satu.
    
- `o{`: nol atau banyak.
    

## 5. Penjelasan Relasi Antarentitas

### 5.1. `roles` dengan `users`

**Relasi:** One-to-Many (1).

Satu peran dapat digunakan oleh banyak akun pengguna. Setiap akun memiliki satu peran yang ditentukan melalui kolom `role_id`.

Contoh peran yang digunakan adalah `admin`, `panitia`, `pimpinan`, dan `calon_mahasiswa`.

### 5.2. `users` dengan `applicant_profiles`

**Relasi:** One-to-Zero-or-One (1:0..1).

Satu akun dapat memiliki maksimal satu profil calon mahasiswa. Tidak semua akun harus mempunyai profil calon mahasiswa karena akun admin, panitia, dan pimpinan tidak membutuhkan profil pendaftar.

Kolom `applicant_profiles.user_id` menjadi foreign key sekaligus memiliki batasan unik.

### 5.3. `applicant_profiles` dengan `applications`

**Relasi:** One-to-Many (1).

Satu profil calon mahasiswa dapat memiliki beberapa pengajuan pendaftaran pada gelombang yang berbeda. Setiap pengajuan hanya dimiliki oleh satu profil calon mahasiswa.

Untuk mencegah pengajuan ganda pada gelombang yang sama, database harus menerapkan batasan unik gabungan:

`UNIQUE (applicant_id, wave_id)`

Dengan demikian, satu calon mahasiswa hanya dapat memiliki satu pengajuan pada setiap gelombang, tetapi tetap dapat mendaftar kembali pada gelombang lain sesuai ketentuan penerimaan.

### 5.4. `admission_waves` dengan `applications`

**Relasi:** One-to-Many (1).

Satu gelombang penerimaan dapat memiliki banyak pengajuan. Setiap pengajuan harus terhubung dengan satu gelombang melalui kolom `wave_id`.

Tanggal mulai, tanggal selesai, dan status aktif gelombang digunakan untuk membantu menentukan apakah pendaftaran dapat dilakukan.

### 5.5. `study_programs` dengan `applications`

**Relasi:** One-to-Many (1).

Satu program studi dapat dipilih dalam banyak pengajuan. Setiap pengajuan hanya memilih satu program studi melalui kolom `study_program_id`.

Perubahan program studi pada data master tidak boleh menyebabkan riwayat pengajuan lama kehilangan hubungan atau makna datanya.

### 5.6. `applications` dengan `application_documents`

**Relasi:** One-to-Many (1).

Satu pengajuan dapat mempunyai beberapa dokumen, misalnya foto dan ijazah atau Surat Keterangan Lulus (SKL). Setiap dokumen harus terhubung dengan satu pengajuan.

File disimpan pada penyimpanan privat server, sedangkan database menyimpan metadata seperti jenis dokumen, lokasi file, tipe MIME, ukuran, dan waktu unggah.

### 5.7. `applications` dengan `application_status_histories`

**Relasi:** One-to-Many (1).

Satu pengajuan dapat memiliki banyak catatan perubahan status. Riwayat ini digunakan untuk melacak proses pendaftaran, mulai dari pengajuan, pemeriksaan berkas, revisi, hingga keputusan akhir.

Setiap perubahan status sebaiknya dicatat dalam transaksi database yang sama dengan perubahan status pada tabel `applications`, sehingga data status terkini dan riwayatnya tetap konsisten.

### 5.8. `users` dengan `application_status_histories`

**Relasi:** Satu pengguna dapat mencatat banyak perubahan status, sedangkan satu catatan riwayat dapat memiliki nol atau satu pengguna yang melakukan perubahan.

Kolom `changed_by_user_id` dapat bernilai `NULL` apabila perubahan dilakukan oleh proses sistem otomatis. Jika perubahan dilakukan oleh admin atau panitia, kolom tersebut menyimpan ID pengguna terkait.

## 6. Aturan Integritas Data

Aturan berikut harus diterapkan pada implementasi database.

1. Setiap tabel memiliki primary key.
    
2. Setiap foreign key harus merujuk pada data yang valid.
    
3. Email pengguna harus unik.
    
4. Nama peran dan kode program studi harus unik.
    
5. Setiap profil calon mahasiswa hanya boleh terhubung dengan satu akun.
    
6. Nomor pendaftaran harus unik.
    
7. Kombinasi `applicant_id` dan `wave_id` pada tabel `applications` harus unik.
    
8. Status pengajuan hanya boleh menggunakan nilai yang diizinkan: `draft`, `submitted`, `review`, `revision`, `accepted`, atau `rejected`.
    
9. Dokumen pendaftaran harus terkait dengan pengajuan yang valid.
    
10. File dokumen harus disimpan secara privat dan tidak dapat diakses langsung oleh pengguna yang tidak berwenang.
    
11. Penghapusan data induk harus mempertimbangkan foreign key dan kebutuhan penyimpanan riwayat. Data pendaftaran yang sudah diproses sebaiknya tidak dihapus sembarangan.
    
12. Keputusan diterima atau ditolak tetap dilakukan oleh panitia yang berwenang. Validasi otomatis hanya memeriksa kelengkapan dan format data, bukan menentukan kelulusan akademik.
    

### 6.1 Tambahan Entitas Usulan `audit_logs`

`audit_logs` mencatat aksi sensitif yang diwajibkan Security Specification tetapi bukan perubahan status domain. Kolom `actor_user_id` nullable (FK `users.id`, `ON DELETE SET NULL`), `action`, `subject_type`, `subject_id` nullable, `result`, `reason` nullable, `ip_address` nullable, `user_agent` nullable, dan `created_at`. Pasangan tipe/ID subjek polymorphic tidak memiliki FK ke tiap tabel; backend wajib memakai allowlist tipe aksi dan objek serta memverifikasi relasi.

Baris audit tidak boleh diubah/dihapus melalui UI operasional. Retensi dan aksi anonim yang diaudit perlu keputusan sebelum implementasi produksi. Jangan memasukkan password, token, isi dokumen, NIK lengkap, atau payload PII.

## 7. Indeks dan Constraint yang Direkomendasikan

|Tabel|Constraint atau indeks|Tujuan|
|---|---|---|
|`users`|`UNIQUE(email)`|Mencegah akun menggunakan email yang sama.|
|`applicant_profiles`|`UNIQUE(user_id)`|Membatasi satu profil untuk satu akun.|
|`study_programs`|`UNIQUE(code)`|Mencegah kode program studi ganda.|
|`applications`|`UNIQUE(registration_number)`|Mencegah nomor pendaftaran ganda.|
|`applications`|`UNIQUE(applicant_id, wave_id)`|Mencegah pengajuan ganda pada gelombang yang sama.|
|`applications`|Indeks pada `status`|Mempercepat pencarian berdasarkan status.|
|`applications`|Indeks pada `wave_id` dan `study_program_id`|Membantu penyaringan dan laporan pendaftaran.|
|`application_documents`|Indeks pada `application_id`|Mempercepat pengambilan dokumen pengajuan.|
|`application_status_histories`|Indeks pada `application_id` dan `created_at`|Mempercepat penampilan riwayat secara kronologis.|
|`audit_logs` (usulan)|Indeks `created_at`, `(actor_user_id, created_at)`, `(subject_type, subject_id, created_at)`, `(action, created_at)`|Mendukung penelusuran audit; sesuaikan dengan workload dan kebijakan retensi.|

Constraint unik untuk pasangan `applicant_id` dan `wave_id` tetap wajib diterapkan meskipun aplikasi sudah melakukan pemeriksaan sebelum menyimpan pengajuan. Hal ini mencegah duplikasi ketika dua permintaan masuk hampir bersamaan.

## 8. Catatan Implementasi

- Tipe data dan nama kolom pada ERD merupakan rancangan logis yang perlu diselaraskan dengan migration Laravel.
    
- Tabel `application_status_histories` harus digunakan sebagai audit trail perubahan status.
    
- Kolom `password` hanya menyimpan hash kata sandi, bukan kata sandi asli.
    
- Pembatasan akses terhadap dokumen harus dilakukan melalui backend, bukan hanya dengan menyembunyikan tautan pada antarmuka.
    
- Validasi perubahan status perlu diterapkan di backend agar pengajuan tidak berpindah ke status yang tidak sesuai dengan alur bisnis.
    
- Daftar program studi, kebijakan NIK, aturan penghapusan, dan ketentuan dokumen masih perlu dikonfirmasi sebelum implementasi final.
    

## 9. Checklist Validasi ERD

- Delapan entitas domain baseline dipertahankan; entitas usulan `audit_logs` terpisah dan terdokumentasi.
    
- Setiap tabel mempunyai primary key.
    
- Seluruh foreign key sesuai dengan hubungan antartabel.
    
- Kardinalitas hubungan sudah sesuai dengan kebutuhan sistem.
    
- Satu akun calon mahasiswa dapat memiliki beberapa pengajuan pada gelombang berbeda.
    
- Pengajuan ganda pada gelombang yang sama dicegah melalui constraint unik.
    
- Setiap pengajuan dapat mempunyai beberapa dokumen dan riwayat status.
    
- Riwayat perubahan status dapat ditelusuri.
    
- Struktur ERD konsisten dengan `Database Model.md` dan `Data Dictionary.md`.
    
- Diagram berhasil dirender di Obsidian.
    
- Struktur sudah ditinjau sebelum diterjemahkan menjadi migration Laravel.
    

## 10. Dokumen Berikutnya

Setelah ERD selesai, dokumen yang disarankan untuk dilanjutkan adalah:

1. `Migration Plan.md` — menjelaskan urutan pembuatan tabel, foreign key, indeks, dan constraint menggunakan migration Laravel.
    
2. `API Contract.md` — menjelaskan endpoint REST API, request, response, autentikasi, dan format error.
    
3. `Test Plan.md` — menjelaskan pengujian integritas relasi, pencegahan pengajuan ganda, dan keamanan akses data.
    

---

**Kesimpulan:** ERD baseline mencakup delapan entitas domain untuk proses PMB. Revised design mengusulkan `audit_logs` sebagai entitas audit opsional berdasarkan Security Specification; cakupan/retensi belum diputuskan. Field status/catatan pemeriksaan per dokumen belum ditambahkan karena semantik dan kebutuhan versinya belum ditetapkan di sumber kebutuhan; hal ini menjadi blocker desain sebelum migration final. Tidak ada perubahan schema runtime dalam pekerjaan desain ini.