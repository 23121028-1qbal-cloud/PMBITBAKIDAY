# Data Dictionary — PMB ITB Akiday

## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Proyek|Sistem Penerimaan Mahasiswa Baru ITB Akiday|
|Nama Dokumen|Data Dictionary|
|Versi|1.1 (usulan Tahap 3)|
|Status|REVISED DESIGN — usulan untuk implementasi; perlu verifikasi formal sebelum dijadikan baseline database|
|Tanggal Baseline|18 Oktober 2026|
|Tanggal Revisi|28 Oktober 2026|
|DBMS|MySQL|
|Backend|Laravel|
|Referensi|PRD Final v1.0, SRS.md, System Design.md, Workflow & Business Process.md, Database Model.md|

## 2. Tujuan

Data Dictionary merupakan dokumentasi yang menjelaskan struktur data yang digunakan dalam sistem PMB ITB Akiday. Dokumen ini mencakup nama tabel, nama kolom, tipe data, fungsi kolom, aturan validasi, primary key, foreign key, dan constraint.

Dokumen ini menjadi acuan saat membuat migration Laravel, model Eloquent, validasi API, serta pengujian database. Seluruh data harus dirancang agar konsisten, mudah dikelola, dan mendukung keamanan informasi calon mahasiswa.

## 3. Konvensi Database

Sistem menggunakan MySQL dengan konvensi berikut:

1. Nama tabel menggunakan huruf kecil dan bentuk jamak, misalnya `users` dan `applications`.
    
2. Nama kolom menggunakan `snake_case`, misalnya `full_name` dan `created_at`.
    
3. Primary key menggunakan kolom `id`.
    
4. Foreign key menggunakan format `<nama_entitas>_id`, misalnya `user_id`.
    
5. Waktu disimpan menggunakan `DATETIME` atau tipe waktu yang sesuai dengan kebutuhan aplikasi.
    
6. Password disimpan sebagai hash, bukan teks biasa.
    
7. Data yang memiliki hubungan wajib menggunakan foreign key.
    
8. Kolom yang tidak boleh duplikat harus memiliki unique constraint.
    
9. Kolom yang wajib diisi menggunakan `NOT NULL`, kecuali jika memang boleh kosong.
    
10. Kolom status menggunakan nilai yang telah ditetapkan agar tidak terjadi perbedaan penulisan.
    
11. Dokumen di penyimpanan file tidak disimpan sebagai file publik; database hanya menyimpan metadata dan lokasi penyimpanan.
    
12. Waktu aplikasi dan database harus menggunakan zona waktu yang ditetapkan secara konsisten.
    

Tipe data di bawah merupakan rancangan awal. Detail akhirnya perlu diselaraskan dengan versi MySQL, konfigurasi Laravel, dan migration yang digunakan.

## 4. Ringkasan Tabel

|No.|Nama Tabel|Fungsi|
|---|---|---|
|1|`roles`|Menyimpan jenis peran pengguna.|
|2|`users`|Menyimpan akun dan informasi autentikasi.|
|3|`applicant_profiles`|Menyimpan profil calon mahasiswa.|
|4|`admission_waves`|Menyimpan gelombang penerimaan.|
|5|`study_programs`|Menyimpan program studi yang tersedia.|
|6|`applications`|Menyimpan pendaftaran calon mahasiswa.|
|7|`application_documents`|Menyimpan metadata dokumen pendaftaran.|
|8|`application_status_histories`|Menyimpan riwayat perubahan status pendaftaran.|
|Usulan|`audit_logs`|Catatan audit operasional/keamanan yang terpisah dari riwayat status domain; opsional sampai cakupan dan retensi disepakati.|

Delapan tabel pertama adalah baseline domain. `audit_logs` adalah opsi tambahan hasil tinjauan Tahap 3; retensi/anonimitas masih menunggu keputusan. Tidak ada tabel notifikasi ditambahkan.

---

## 5. Data Dictionary Tabel `roles`

### 5.1. Deskripsi

Tabel `roles` menyimpan jenis peran yang digunakan untuk mengatur hak akses pengguna. Contohnya adalah calon mahasiswa, admin, panitia, dan pimpinan.

### 5.2. Struktur Kolom

|Kolom|Tipe Data|Null|Key|Keterangan|
|---|---|---|---|---|
|`id`|BIGINT UNSIGNED|Tidak|PK|Identitas unik peran.|
|`name`|VARCHAR(50)|Tidak|UNIQUE|Nama peran pengguna.|

### 5.3. Aturan Data

- `id` merupakan primary key dan dibuat otomatis.
    
- `name` wajib diisi dan tidak boleh duplikat.
    
- Nama peran harus konsisten dengan aturan otorisasi aplikasi.
    

Contoh nilai `name`:

|Nilai|Keterangan|
|---|---|
|`calon_mahasiswa`|Pengguna yang melakukan pendaftaran.|
|`admin`|Pengelola akun dan konfigurasi sistem.|
|`panitia`|Pengelola pemeriksaan pendaftaran.|
|`pimpinan`|Pengguna yang melihat dashboard dan laporan.|

Nilai tersebut merupakan kode peran yang digunakan oleh aplikasi, bukan nama tampilan untuk pengguna.

---

## 6. Data Dictionary Tabel `users`

### 6.1. Deskripsi

Tabel `users` menyimpan akun seluruh pengguna, termasuk email, password hash, peran, dan status aktif akun.

### 6.2. Struktur Kolom

|Kolom|Tipe Data|Null|Key|Keterangan|
|---|---|---|---|---|
|`id`|BIGINT UNSIGNED|Tidak|PK|Identitas unik akun.|
|`role_id`|BIGINT UNSIGNED|Tidak|FK|Referensi ke `roles.id`.|
|`email`|VARCHAR(255)|Tidak|UNIQUE|Alamat email untuk login.|
|`password`|VARCHAR(255)|Tidak|—|Password dalam bentuk hash.|
|`is_active`|BOOLEAN|Tidak|—|Status aktif akun.|
|`created_at`|TIMESTAMP|Ya|—|Waktu akun dibuat.|
|`updated_at`|TIMESTAMP|Ya|—|Waktu terakhir akun diperbarui.|

### 6.3. Aturan Data

- `role_id` harus merujuk pada peran yang tersedia.
    
- `email` wajib diisi, divalidasi formatnya, dan tidak boleh duplikat.
    
- Email sebaiknya dinormalisasi secara konsisten, misalnya dengan menghapus spasi di awal/akhir dan menyamakan huruf sebelum disimpan.
    
- `password` berisi hash yang dibuat melalui mekanisme hashing aman Laravel.
    
- `is_active` bernilai `true` jika akun aktif dan `false` jika akun dinonaktifkan.
    
- Akun yang dinonaktifkan tidak boleh melakukan autentikasi.
    
- Email digunakan sebagai identitas login; kebijakan perubahan email harus ditangani melalui proses yang tervalidasi.
    

### 6.4. Contoh Data

|id|role_id|email|is_active|
|---|---|---|---|
|1|1|[calon@example.com](mailto:calon@example.com)|1|
|2|2|[admin@example.com](mailto:admin@example.com)|1|

Data di atas hanya contoh dokumentasi, bukan akun nyata.

---

## 7. Data Dictionary Tabel `applicant_profiles`

### 7.1. Deskripsi

Tabel `applicant_profiles` menyimpan data pribadi calon mahasiswa yang terhubung dengan akun pada tabel `users`.

### 7.2. Struktur Kolom

|Kolom|Tipe Data|Null|Key|Keterangan|
|---|---|---|---|---|
|`id`|BIGINT UNSIGNED|Tidak|PK|Identitas unik profil.|
|`user_id`|BIGINT UNSIGNED|Tidak|FK, UNIQUE|Akun pemilik profil.|
|`full_name`|VARCHAR(150)|Tidak|—|Nama lengkap calon mahasiswa.|
|`nik`|VARCHAR(16)|Tidak|—|Nomor Induk Kependudukan.|
|`birth_place`|VARCHAR(100)|Tidak|—|Tempat lahir.|
|`birth_date`|DATE|Tidak|—|Tanggal lahir.|
|`gender`|VARCHAR(20)|Tidak|—|Jenis kelamin sesuai pilihan yang ditetapkan.|
|`phone`|VARCHAR(20)|Tidak|—|Nomor telepon calon mahasiswa.|
|`address`|TEXT|Tidak|—|Alamat calon mahasiswa.|
|`created_at`|TIMESTAMP|Ya|—|Waktu profil dibuat.|
|`updated_at`|TIMESTAMP|Ya|—|Waktu profil diperbarui.|

### 7.3. Aturan Data

- `user_id` harus merujuk pada akun yang valid.
    
- `user_id` diberi unique constraint agar satu akun memiliki paling banyak satu profil.
    
- `full_name`, `birth_place`, `birth_date`, `gender`, `phone`, dan `address` wajib diisi.
    
- `nik` disimpan sebagai `VARCHAR`, bukan angka, agar format identitas tidak mengalami perubahan akibat operasi numerik.
    
- Validasi format NIK dilakukan pada backend. NIK tidak boleh dianggap valid hanya karena berisi 16 karakter.
    
- Apakah NIK harus unik secara global masih menjadi keputusan yang perlu dikonfirmasi.
    
- `gender` harus menggunakan nilai yang konsisten dengan pilihan pada formulir.
    
- `phone` menggunakan teks agar simbol `+` dan format nomor telepon dapat dipertahankan.
    

### 7.4. Catatan Privasi

NIK, tanggal lahir, nomor telepon, dan alamat merupakan data pribadi. Akses ke kolom tersebut harus dibatasi sesuai peran dan kebutuhan pekerjaan. Data ini tidak boleh ditampilkan pada laporan publik atau log aplikasi tanpa alasan yang sah.

---

## 8. Data Dictionary Tabel `admission_waves`

### 8.1. Deskripsi

Tabel `admission_waves` menyimpan informasi gelombang penerimaan mahasiswa baru.

### 8.2. Struktur Kolom

|Kolom|Tipe Data|Null|Key|Keterangan|
|---|---|---|---|---|
|`id`|BIGINT UNSIGNED|Tidak|PK|Identitas unik gelombang.|
|`name`|VARCHAR(100)|Tidak|—|Nama gelombang penerimaan.|
|`start_date`|DATETIME|Tidak|—|Waktu mulai penerimaan.|
|`end_date`|DATETIME|Tidak|—|Waktu akhir penerimaan.|
|`is_active`|BOOLEAN|Tidak|—|Penanda apakah gelombang diaktifkan.|
|`created_at`|TIMESTAMP|Ya|—|Waktu data dibuat.|
|`updated_at`|TIMESTAMP|Ya|—|Waktu data diperbarui.|

### 8.3. Aturan Data

- `name` wajib diisi.
    
- `start_date` harus lebih awal daripada atau sesuai aturan batas waktu yang ditetapkan terhadap `end_date`; untuk rancangan ini, waktu mulai harus lebih awal.
    
- `is_active` menunjukkan apakah gelombang diaktifkan oleh admin.
    
- Gelombang hanya menerima pendaftaran jika status aktif dan waktu saat ini berada dalam rentang yang diizinkan.
    
- Status aktif tidak boleh menjadi satu-satunya pemeriksaan waktu. Backend tetap harus membandingkan waktu sekarang dengan jadwal gelombang.
    
- Perubahan jadwal gelombang yang sudah memiliki pendaftar harus dipertimbangkan agar tidak merugikan pengguna.
    

### 8.4. Contoh Data

|id|name|start_date|end_date|is_active|
|---|---|---|---|---|
|1|Gelombang 1|2026-11-01 00:00:00|2026-12-01 23:59:59|1|

Tanggal tersebut hanya contoh pengisian dan bukan jadwal resmi PMB ITB Akiday.

---

## 9. Data Dictionary Tabel `study_programs`

### 9.1. Deskripsi

Tabel `study_programs` menyimpan daftar program studi yang dapat dipilih calon mahasiswa saat mendaftar.

### 9.2. Struktur Kolom

|Kolom|Tipe Data|Null|Key|Keterangan|
|---|---|---|---|---|
|`id`|BIGINT UNSIGNED|Tidak|PK|Identitas unik program studi.|
|`code`|VARCHAR(20)|Tidak|UNIQUE|Kode unik program studi.|
|`name`|VARCHAR(150)|Tidak|—|Nama program studi.|
|`is_active`|BOOLEAN|Tidak|—|Status ketersediaan program studi.|
|`created_at`|TIMESTAMP|Ya|—|Waktu data dibuat.|
|`updated_at`|TIMESTAMP|Ya|—|Waktu data diperbarui.|

### 9.3. Aturan Data

- `code` harus unik dan wajib diisi.
    
- `name` wajib diisi.
    
- Program studi hanya ditampilkan untuk pendaftaran baru jika aktif dan tersedia sesuai kebijakan gelombang.
    
- Program studi yang sudah digunakan dalam pendaftaran tidak boleh dihapus sembarangan.
    
- Program studi yang tidak lagi tersedia dapat dinonaktifkan agar riwayat pendaftaran lama tetap terjaga.
    
- Daftar program studi prototipe harus diverifikasi dengan informasi resmi institusi sebelum digunakan dalam penerapan nyata.
    

---

## 10. Data Dictionary Tabel `applications`

### 10.1. Deskripsi

Tabel `applications` merupakan tabel utama yang menyimpan data pendaftaran calon mahasiswa. Tabel ini menghubungkan profil calon mahasiswa, gelombang penerimaan, dan program studi yang dipilih.

### 10.2. Struktur Kolom

|Kolom|Tipe Data|Null|Key|Keterangan|
|---|---|---|---|---|
|`id`|BIGINT UNSIGNED|Tidak|PK|Identitas unik pendaftaran.|
|`applicant_id`|BIGINT UNSIGNED|Tidak|FK|Referensi ke `applicant_profiles.id`.|
|`wave_id`|BIGINT UNSIGNED|Tidak|FK|Referensi ke `admission_waves.id`.|
|`study_program_id`|BIGINT UNSIGNED|Tidak|FK|Referensi ke `study_programs.id`.|
|`registration_number`|VARCHAR(50)|Tidak|UNIQUE|Nomor pendaftaran.|
|`status`|VARCHAR(20)|Tidak|INDEX|Status proses pendaftaran.|
|`submitted_at`|DATETIME|Ya|—|Waktu pendaftaran berhasil dikirim.|
|`created_at`|TIMESTAMP|Ya|—|Waktu data pendaftaran dibuat.|
|`updated_at`|TIMESTAMP|Ya|—|Waktu data pendaftaran diperbarui.|

### 10.3. Nilai Status

Kolom `status` hanya boleh berisi nilai berikut:

|Nilai|Makna|
|---|---|
|`draft`|Pendaftaran masih disiapkan.|
|`submitted`|Pendaftaran sudah dikirim.|
|`review`|Pendaftaran sedang diperiksa.|
|`revision`|Pendaftaran memerlukan perbaikan.|
|`accepted`|Pendaftaran dinyatakan diterima.|
|`rejected`|Pendaftaran dinyatakan ditolak.|

Validasi status dilakukan pada backend. Pembatasan nilai di database juga dapat diterapkan menggunakan mekanisme yang didukung oleh versi MySQL yang digunakan.

### 10.4. Constraint Penting

**Primary key**

- `id`
    

**Foreign key**

- `applicant_id` → `applicant_profiles.id`
    
- `wave_id` → `admission_waves.id`
    
- `study_program_id` → `study_programs.id`
    

**Unique constraint**

- `registration_number`
    
- Kombinasi `applicant_id` dan `wave_id`
    

Contoh constraint untuk mencegah pendaftaran ganda:

```
UNIQUE (applicant_id, wave_id)
```

Constraint ini berarti satu profil hanya dapat memiliki satu pendaftaran pada satu gelombang. Profil yang sama tetap dapat memiliki pendaftaran lain pada gelombang berbeda jika diperbolehkan.

### 10.5. Aturan Data

- Setiap pendaftaran harus memiliki calon mahasiswa, gelombang, dan program studi yang valid.
    
- `registration_number` harus unik.
    
- Status awal pendaftaran adalah `draft`.
    
- `submitted_at` bernilai kosong selama pendaftaran belum dikirim.
    
- Ketika pendaftaran berhasil dikirim, backend menyimpan waktu pengiriman dan mengubah status menjadi `submitted`.
    
- Status hanya dapat berubah melalui proses yang diizinkan.
    
- Proses pengiriman harus memeriksa ulang persyaratan dan mencegah duplikasi.
    
- Pendaftaran lama harus tetap dapat ditelusuri meskipun gelombang atau program studi sudah tidak aktif.
    

---

## 11. Data Dictionary Tabel `application_documents`

### 11.1. Deskripsi

Tabel `application_documents` menyimpan metadata file yang diunggah sebagai dokumen persyaratan pendaftaran.

File fisik disimpan pada private storage, sedangkan tabel ini menyimpan informasi untuk mengidentifikasi dan mengakses file melalui backend.

### 11.2. Struktur Kolom

|Kolom|Tipe Data|Null|Key|Keterangan|
|---|---|---|---|---|
|`id`|BIGINT UNSIGNED|Tidak|PK|Identitas unik dokumen.|
|`application_id`|BIGINT UNSIGNED|Tidak|FK|Referensi ke `applications.id`.|
|`document_type`|VARCHAR(30)|Tidak|—|Jenis dokumen yang diunggah.|
|`file_path`|VARCHAR(255)|Tidak|—|Lokasi relatif file pada private storage.|
|`mime_type`|VARCHAR(100)|Tidak|—|Tipe MIME file.|
|`file_size`|BIGINT UNSIGNED|Tidak|—|Ukuran file dalam byte.|
|`uploaded_at`|DATETIME|Tidak|—|Waktu file berhasil diunggah.|

### 11.3. Jenis Dokumen

Nilai awal `document_type`:

|Nilai|Makna|
|---|---|
|`photo`|Foto calon mahasiswa.|
|`diploma`|Ijazah atau dokumen kelulusan.|

Dokumen kelulusan dapat berupa ijazah atau SKL sesuai kebijakan penerimaan. Jika kebutuhan bisnis mengharuskan pembedaan keduanya, nilai `document_type` dapat dikembangkan menjadi `diploma` dan `graduation_certificate`.

### 11.4. Aturan Data

- `application_id` harus mengarah ke pendaftaran yang valid.
    
- File hanya dapat diunggah oleh pemilik pendaftaran atau petugas yang berwenang.
    
- `file_path` harus menunjuk ke lokasi penyimpanan privat, bukan URL publik yang dapat ditebak.
    
- `mime_type` harus diverifikasi backend dan tidak boleh dipercaya hanya berdasarkan nama file.
    
- `file_size` harus lebih besar dari nol dan tidak melebihi batas yang telah ditetapkan.
    
- Sistem harus menentukan apakah satu jenis dokumen hanya boleh memiliki satu file aktif atau dapat memiliki beberapa versi.
    
- Penggantian dokumen perlu mempertahankan informasi yang diperlukan untuk audit jika kebijakan mengharuskannya.
    

**Catatan desain:** tabel ini belum memiliki kolom khusus untuk status verifikasi dokumen. Jika setiap dokumen perlu ditandai `pending`, `verified`, atau `rejected`, kolom tambahan atau tabel pemeriksaan dokumen perlu dipertimbangkan pada tahap Database Model final.

---

## 12. Data Dictionary Tabel `application_status_histories`

### 12.1. Deskripsi

Tabel `application_status_histories` menyimpan catatan setiap perubahan status pendaftaran. Riwayat ini digunakan untuk pelacakan proses, transparansi kepada calon mahasiswa, dan kebutuhan audit.

### 12.2. Struktur Kolom

|Kolom|Tipe Data|Null|Key|Keterangan|
|---|---|---|---|---|
|`id`|BIGINT UNSIGNED|Tidak|PK|Identitas unik riwayat.|
|`application_id`|BIGINT UNSIGNED|Tidak|FK|Pendaftaran yang mengalami perubahan.|
|`from_status`|VARCHAR(20)|Ya|—|Status sebelum perubahan.|
|`to_status`|VARCHAR(20)|Tidak|INDEX|Status setelah perubahan.|
|`note`|TEXT|Ya|—|Catatan atau alasan perubahan.|
|`changed_by_user_id`|BIGINT UNSIGNED|Ya|FK|Pengguna yang melakukan perubahan; kosong jika perubahan dilakukan sistem.|
|`created_at`|TIMESTAMP|Tidak|—|Waktu perubahan dicatat.|

### 12.3. Aturan Data

- `application_id` harus merujuk ke pendaftaran yang valid.
    
- `from_status` boleh kosong untuk pencatatan status awal.
    
- `to_status` harus berisi status yang valid.
    
- `note` digunakan untuk mencatat alasan revisi atau keterangan penting.
    
- `changed_by_user_id` harus mengarah ke akun yang valid jika diisi.
    
- Nilai `changed_by_user_id` yang kosong hanya digunakan untuk proses sistem yang memang tidak dilakukan langsung oleh pengguna.
    
- Riwayat tidak boleh diedit sembarangan untuk menyamarkan perubahan sebelumnya.
    
- Pencatatan status dan riwayat harus dilakukan dalam satu transaksi database.
    

### 12.4. Contoh Riwayat

|application_id|from_status|to_status|note|
|---|---|---|---|
|1|NULL|`draft`|Pendaftaran dibuat.|
|1|`draft`|`submitted`|Pendaftaran dikirim.|
|1|`submitted`|`review`|Pemeriksaan dimulai.|
|1|`review`|`revision`|Dokumen kelulusan perlu diperbaiki.|

Data di atas merupakan contoh ilustrasi.

---

## 13. Relasi Antartabel

|Tabel Asal|Kolom FK|Tabel Tujuan|Kolom Referensi|Relasi|
|---|---|---|---|---|
|`users`|`role_id`|`roles`|`id`|Banyak akun dapat menggunakan satu peran.|
|`applicant_profiles`|`user_id`|`users`|`id`|Satu akun memiliki paling banyak satu profil calon mahasiswa.|
|`applications`|`applicant_id`|`applicant_profiles`|`id`|Satu profil dapat memiliki pendaftaran pada gelombang berbeda.|
|`applications`|`wave_id`|`admission_waves`|`id`|Satu gelombang dapat memiliki banyak pendaftaran.|
|`applications`|`study_program_id`|`study_programs`|`id`|Satu program studi dapat dipilih oleh banyak pendaftar.|
|`application_documents`|`application_id`|`applications`|`id`|Satu pendaftaran dapat memiliki dokumen.|
|`application_status_histories`|`application_id`|`applications`|`id`|Satu pendaftaran dapat memiliki banyak riwayat status.|
|`application_status_histories`|`changed_by_user_id`|`users`|`id`|Satu pengguna dapat melakukan banyak perubahan status; akun yang dihapus dibatasi `RESTRICT` agar jejak tidak hilang.|
|`audit_logs` (usulan)|`actor_user_id`|`users`|`id`|Satu pengguna dapat menghasilkan banyak catatan audit; jika dipilih, penghapusan user mengosongkan FK (`SET NULL`) dan mempertahankan peristiwa.|

## 14. Indeks dan Constraint yang Direkomendasikan

|Tabel|Indeks atau Constraint|Tujuan|
|---|---|---|
|`roles`|UNIQUE (`name`)|Mencegah nama peran duplikat.|
|`users`|UNIQUE (`email`)|Mencegah email akun duplikat.|
|`applicant_profiles`|UNIQUE (`user_id`)|Membatasi satu profil per akun.|
|`applications`|UNIQUE (`registration_number`)|Mencegah nomor pendaftaran duplikat.|
|`applications`|UNIQUE (`applicant_id`, `wave_id`)|Mencegah pendaftaran ganda pada gelombang yang sama.|
|`applications`|INDEX (`status`)|Membantu pencarian berdasarkan status.|
|`applications`|INDEX (`wave_id`, `status`)|Membantu rekap status berdasarkan gelombang.|
|`application_documents`|INDEX (`application_id`)|Mempercepat pencarian dokumen berdasarkan pendaftaran.|
|`application_status_histories`|INDEX (`application_id`, `created_at`)|Mempercepat pengambilan riwayat berdasarkan waktu.|
|`audit_logs` (opsional)|INDEX (`created_at`), (`actor_user_id`, `created_at`), (`subject_type`, `subject_id`, `created_at`), (`action`, `created_at`)|Usulan indeks; hanya dipilih jika tabel audit dan query terkait disetujui.|

Indeks tambahan perlu ditentukan berdasarkan query yang benar-benar digunakan. Indeks yang berlebihan dapat memperlambat operasi tulis dan menambah penggunaan penyimpanan.

## 15. Ketentuan Penghapusan Data

Penghapusan data harus mempertimbangkan hubungan antartabel dan kebutuhan audit.

1. Akun yang sudah memiliki pendaftaran tidak boleh dihapus secara sembarangan.
    
2. Gelombang dan program studi yang sudah digunakan sebaiknya dinonaktifkan daripada dihapus.
    
3. Pendaftaran yang sudah diproses perlu dipertahankan sesuai kebijakan retensi data.
    
4. Penghapusan atau penggantian dokumen harus mempertimbangkan kebutuhan riwayat.
    
5. Penghapusan data pribadi harus mengikuti kebijakan institusi dan ketentuan perlindungan data yang berlaku.
    

Perilaku foreign key seperti `RESTRICT`, `CASCADE`, atau `SET NULL` harus dipilih secara eksplisit pada migration. Jangan menggunakan cascade delete secara luas jika dapat menghapus riwayat penerimaan yang masih diperlukan.

## 16. Keamanan Data

- Password disimpan dalam bentuk hash.
    
- NIK dan data pribadi hanya dapat diakses oleh pengguna yang berwenang.
    
- Dokumen disimpan di private storage.
    
- Akses ke data pendaftaran harus memeriksa kepemilikan dan peran pengguna.
    
- Log aplikasi tidak boleh menyimpan password, token, atau isi dokumen sensitif.
    
- Database hanya dapat diakses oleh akun dan layanan yang memerlukan akses.
    
- Backup database dan file harus dilindungi dari akses tidak sah.
    
- Data pada lingkungan pengujian sebaiknya menggunakan data dummy, bukan data pribadi nyata.
    

## 17. Hal yang Masih Perlu Diputuskan

Sebelum migration final dibuat, beberapa hal perlu dikonfirmasi:

1. Apakah NIK harus unik secara global?
2. Apakah dokumen boleh memiliki beberapa versi?
3. Bagaimana status dan catatan pemeriksaan direkam untuk tiap dokumen, termasuk nilai status, aktor/waktu, dan pemeriksaan ulang? UI/workflow meminta informasi tersebut, tetapi atribut domain belum ditetapkan.
4. Apakah tabel persisten `audit_logs` disetujui, termasuk event anonim, cakupan aksi, retensi, akses dan purge?
5. Apakah zona waktu database menggunakan UTC atau zona waktu lokal yang disepakati?
6. Apakah tabel perlu menggunakan soft delete untuk entitas tertentu?
7. Berapa batas maksimum ukuran dan tipe file yang diizinkan?
8. Bagaimana kebijakan retensi dan penghapusan data?
9. Apakah nomor pendaftaran dibuat melalui format berurutan per gelombang atau format lain?
    

Keputusan ini harus diselaraskan dengan PRD dan aturan institusi sebelum implementasi final.

## 18. Kriteria Penyelesaian

- Seluruh tabel utama telah didokumentasikan.
    
- Setiap kolom memiliki tipe data dan keterangan.
    
- Primary key dan foreign key telah ditentukan.
    
- Constraint untuk mencegah data duplikat telah dicatat.
    
- Nilai status pendaftaran telah didefinisikan.
    
- Aturan penyimpanan dokumen telah dijelaskan.
    
- Keamanan dan privasi data telah dipertimbangkan.
    
- Indeks awal telah direncanakan.
    
- Keputusan yang belum pasti telah didokumentasikan.
    

## 19. Dokumen Berikutnya

Setelah menyimpan Data Dictionary, lanjutkan dengan:

1. `ERD.md` — diagram visual hubungan antartabel.
    
2. `Database Model.md` — model database yang lebih rinci beserta constraint dan relasi implementasinya.
    
3. `Migration Plan.md` — urutan pembuatan migration Laravel dan data awal.
    

Revisi Data Dictionary Tahap 3 ini merupakan proposed design, bukan persetujuan migration final. Baseline delapan tabel domain dipertahankan; `audit_logs` tetap pilihan usulan. Keputusan hasil pemeriksaan dokumen, retensi audit/PII, NIK, format file, dan versi perlu diselesaikan sebelum skema implementasi final ditetapkan.