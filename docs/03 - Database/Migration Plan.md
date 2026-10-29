# Migration Plan — PMB ITB Akiday

## 1. Informasi Dokumen

|Informasi|Detail|
|---|---|
|Nama Proyek|PMB ITB Akiday|
|Nama Dokumen|Migration Plan|
|Versi|1.1 (usulan Tahap 3)|
|Status|REVISED DESIGN — rencana migration, bukan migration yang telah dijalankan|
|Tanggal Baseline|18 Oktober 2026|
|Tanggal Revisi|28 Oktober 2026|
|Backend|Laravel|
|DBMS|MySQL|
|Referensi|Database Model.md, ERD.md, Data Dictionary.md|

---

## 2. Tujuan

Migration Plan merupakan dokumen yang menjelaskan tahapan pembuatan dan perubahan struktur database menggunakan fitur migration pada Laravel. Migration digunakan agar struktur database dapat dibuat secara teratur, konsisten, dan dapat dikelola melalui kode program.

Pada sistem PMB ITB Akiday, migration digunakan untuk membuat tabel pengguna, profil calon mahasiswa, gelombang penerimaan, program studi, pengajuan pendaftaran, dokumen, dan riwayat perubahan status.

Dengan migration, pengembangan database dapat dilakukan oleh beberapa anggota tim dengan struktur yang sama. Perubahan skema juga dapat dilacak melalui Git sehingga lebih mudah diuji dan diterapkan pada lingkungan pengembangan maupun produksi.

## 3. Ruang Lingkup

Rencana ini mencakup delapan tabel domain baseline serta opsi tabel audit `audit_logs` usulan Tahap 3; opsi tabel audit bukan persetujuan pembuatan migration.

1. Penentuan primary key dan foreign key.
2. Penerapan constraint unik dan indeks yang relevan.
3. Penentuan tipe data serta aturan `NULL`.
4. Seeder data awal yang disepakati.
5. Pengujian migration dan integritas data.
6. Strategi rollback dan penerapan skema.
7. Menahan implementasi final atribut hasil pemeriksaan dokumen sampai aturan domain diputuskan.
    

Migration tidak mencakup penyimpanan isi file dokumen langsung di database. File foto dan ijazah/SKL disimpan di penyimpanan privat server, sedangkan database menyimpan metadata dan lokasi file.

## 4. Daftar Tabel

|No.|Tabel|Fungsi|
|---|---|---|
|1|`roles`|Menyimpan peran pengguna.|
|2|`users`|Menyimpan akun dan informasi autentikasi.|
|3|`applicant_profiles`|Menyimpan data pribadi calon mahasiswa.|
|4|`admission_waves`|Menyimpan data gelombang penerimaan.|
|5|`study_programs`|Menyimpan program studi yang tersedia.|
|6|`applications`|Menyimpan pengajuan pendaftaran.|
|7|`application_documents`|Menyimpan metadata dokumen pendaftaran.|
|8|`application_status_histories`|Menyimpan riwayat perubahan status.|
|Usulan|`audit_logs` (opsional)|Menyimpan aktivitas keamanan/operasional sesuai cakupan yang disetujui; retensi masih terbuka.|

## 5. Urutan Migration

Urutan pembuatan tabel harus mempertimbangkan ketergantungan foreign key. Tabel induk dibuat terlebih dahulu, baru kemudian tabel yang mengacu padanya.

|Urutan|Tabel|Alasan|
|---|---|---|
|1|`roles`|Menjadi referensi peran untuk akun pengguna.|
|2|`users`|Menjadi referensi pemilik profil dan pelaku perubahan status.|
|3|`applicant_profiles`|Mengacu pada tabel `users`.|
|4|`admission_waves`|Menjadi referensi gelombang pengajuan.|
|5|`study_programs`|Menjadi referensi program studi pilihan.|
|6|`applications`|Mengacu pada profil, gelombang, dan program studi.|
|7|`application_documents`|Mengacu pada pengajuan pendaftaran.|
|8|`application_status_histories`|Mengacu pada pengajuan dan pengguna yang melakukan perubahan.|
|Opsional|`audit_logs`|Mengacu nullable ke aktor `users` (`ON DELETE SET NULL`) jika dibuat; subject polymorphic diverifikasi pada aplikasi.|

Tabel `roles` harus dibuat sebelum `users`, sedangkan `users` harus dibuat sebelum `applicant_profiles`. Tabel `applications` dibuat setelah seluruh tabel induknya tersedia.

## 6. Konvensi Struktur Database

Konvensi yang digunakan dalam migration:

- Nama tabel menggunakan bentuk jamak dan `snake_case`.
    
- Primary key menggunakan `id` dengan tipe `BIGINT` auto-increment.
    
- Foreign key mengikuti format `<nama_entitas>_id`.
    
- Kolom waktu menggunakan `timestamps()` jika tabel membutuhkan `created_at` dan `updated_at`.
    
- Kolom yang boleh kosong harus didefinisikan dengan `nullable()`.
    
- Kolom yang harus memiliki nilai wajib tidak menggunakan `nullable()`.
    
- Kata sandi hanya menyimpan hash.
    
- Nilai status harus dibatasi sesuai alur bisnis.
    
- Nama dan panjang kolom harus konsisten dengan `Data Dictionary.md`.
    

Tipe data dan panjang kolom akhir harus ditetapkan saat implementasi dan diuji terhadap versi MySQL yang digunakan.

## 7. Rancangan Migration per Tabel

### 7.1. Tabel `roles`

Kolom utama:

|Kolom|Tipe|Aturan|
|---|---|---|
|`id`|BIGINT|Primary key, auto-increment|
|`name`|VARCHAR|Wajib, unik|
|`created_at`|TIMESTAMP|Otomatis|
|`updated_at`|TIMESTAMP|Otomatis|

Tabel ini diisi dengan peran yang dibutuhkan, seperti `calon_mahasiswa`, `admin`, `panitia`, dan `pimpinan`.

### 7.2. Tabel `users`

Kolom utama:

|Kolom|Tipe|Aturan|
|---|---|---|
|`id`|BIGINT|Primary key|
|`role_id`|BIGINT|Foreign key ke `roles.id`|
|`email`|VARCHAR|Wajib, unik|
|`password`|VARCHAR|Wajib, menyimpan hash|
|`is_active`|BOOLEAN|Wajib, default `true`|
|`created_at`|TIMESTAMP|Otomatis|
|`updated_at`|TIMESTAMP|Otomatis|

Email harus unik untuk mencegah beberapa akun menggunakan alamat email yang sama.

### 7.3. Tabel `applicant_profiles`

Kolom utama:

|Kolom|Tipe|Aturan|
|---|---|---|
|`id`|BIGINT|Primary key|
|`user_id`|BIGINT|Foreign key dan unik|
|`full_name`|VARCHAR|Wajib|
|`nik`|VARCHAR(16)|Wajib sesuai aturan awal|
|`birth_place`|VARCHAR|Wajib|
|`birth_date`|DATE|Wajib|
|`gender`|VARCHAR|Wajib|
|`phone`|VARCHAR|Wajib|
|`address`|TEXT|Wajib|
|`created_at`|TIMESTAMP|Otomatis|
|`updated_at`|TIMESTAMP|Otomatis|

NIK disimpan sebagai teks, bukan angka, agar format identitas tidak bergantung pada operasi numerik. Kebijakan apakah NIK harus unik masih perlu dikonfirmasi sebelum constraint unik diterapkan.

Constraint `UNIQUE(user_id)` memastikan satu akun memiliki maksimal satu profil calon mahasiswa.

### 7.4. Tabel `admission_waves`

Kolom utama:

|Kolom|Tipe|Aturan|
|---|---|---|
|`id`|BIGINT|Primary key|
|`name`|VARCHAR|Wajib|
|`start_date`|DATETIME|Wajib|
|`end_date`|DATETIME|Wajib|
|`is_active`|BOOLEAN|Wajib, default `true`|
|`created_at`|TIMESTAMP|Otomatis|
|`updated_at`|TIMESTAMP|Otomatis|

Validasi aplikasi harus memastikan `end_date` tidak lebih awal daripada `start_date`. Aturan tanggal pendaftaran perlu diperiksa kembali di backend ketika calon mahasiswa mengirim pengajuan.

### 7.5. Tabel `study_programs`

Kolom utama:

|Kolom|Tipe|Aturan|
|---|---|---|
|`id`|BIGINT|Primary key|
|`code`|VARCHAR|Wajib, unik|
|`name`|VARCHAR|Wajib|
|`is_active`|BOOLEAN|Wajib, default `true`|
|`created_at`|TIMESTAMP|Otomatis|
|`updated_at`|TIMESTAMP|Otomatis|

Data program studi awal dimasukkan melalui seeder. Data prototipe harus diverifikasi sebelum digunakan sebagai daftar resmi.

### 7.6. Tabel `applications`

Kolom utama:

|Kolom|Tipe|Aturan|
|---|---|---|
|`id`|BIGINT|Primary key|
|`applicant_id`|BIGINT|FK ke `applicant_profiles.id`|
|`wave_id`|BIGINT|FK ke `admission_waves.id`|
|`study_program_id`|BIGINT|FK ke `study_programs.id`|
|`registration_number`|VARCHAR|Wajib, unik|
|`status`|VARCHAR|Wajib, default `draft`|
|`submitted_at`|DATETIME|Boleh kosong|
|`created_at`|TIMESTAMP|Otomatis|
|`updated_at`|TIMESTAMP|Otomatis|

Constraint paling penting:

`UNIQUE (applicant_id, wave_id)`

Constraint tersebut memastikan satu calon mahasiswa tidak dapat memiliki dua pengajuan pada gelombang yang sama. Pengajuan pada gelombang berbeda tetap diperbolehkan.

Status yang diizinkan:

- `draft`
    
- `submitted`
    
- `review`
    
- `revision`
    
- `accepted`
    
- `rejected`
    

Penerapan status dapat menggunakan `VARCHAR` dengan validasi aplikasi atau `ENUM` pada MySQL. Untuk fleksibilitas perubahan alur, rancangan ini memilih `VARCHAR` dengan validasi ketat di backend dan pengujian otomatis.

### 7.7. Tabel `application_documents`

Kolom utama:

|Kolom|Tipe|Aturan|
|---|---|---|
|`id`|BIGINT|Primary key|
|`application_id`|BIGINT|FK ke `applications.id`|
|`document_type`|VARCHAR|Wajib|
|`file_path`|VARCHAR|Wajib|
|`mime_type`|VARCHAR|Wajib|
|`file_size`|BIGINT|Wajib|
|`uploaded_at`|DATETIME|Wajib|

Jenis dokumen awal adalah `photo` dan `diploma`; daftar format/ukuran upload belum ditetapkan. Satu row mewakili satu unggahan. Jumlah/versi per jenis belum diputuskan, sehingga jangan menambahkan unique `(application_id, document_type)` atau menghapus file lama secara implisit. Status dan catatan pemeriksaan per dokumen muncul dalam alur/UI, tetapi nilai/keharusan/domain penyimpanannya belum diputuskan; migration status dokumen tertahan sampai pemilik proses menetapkan model tersebut. Ijazah dan SKL tetap satu kategori sampai ada keputusan pemisahan.

Berkas tidak disimpan sebagai BLOB dalam database. File disimpan di disk privat, dan `file_path` mencatat lokasi relatif yang dikelola oleh aplikasi.

### 7.8. Tabel `application_status_histories`

Kolom utama:

|Kolom|Tipe|Aturan|
|---|---|---|
|`id`|BIGINT|Primary key|
|`application_id`|BIGINT|FK ke `applications.id`|
|`from_status`|VARCHAR|Boleh kosong untuk status awal|
|`to_status`|VARCHAR|Wajib|
|`note`|TEXT|Boleh kosong|
|`changed_by_user_id`|BIGINT|FK ke `users.id`, boleh kosong|
|`created_at`|TIMESTAMP|Waktu pencatatan|

Tabel ini digunakan sebagai audit trail. Setiap perubahan status harus mencatat status sebelumnya, status baru, waktu perubahan, dan pengguna yang melakukan perubahan jika tersedia.

### 7.9. Tabel usulan `audit_logs`

Kolom: `id` BIGINT UNSIGNED PK, `actor_user_id` nullable FK, `action` VARCHAR(100) NOT NULL, `subject_type` VARCHAR(100) NOT NULL (allowlist aplikasi), `subject_id` nullable BIGINT UNSIGNED, `result` VARCHAR(20) NOT NULL, `reason` nullable VARCHAR(500), `ip_address` nullable VARCHAR(45), `user_agent` nullable VARCHAR(500), `created_at` TIMESTAMP NOT NULL. `actor_user_id` memakai `ON DELETE SET NULL`, audit tidak mempunyai `updated_at`; operasi aplikasi hanya insert. Pasangan subject polymorphic tidak mempunyai FK database dan harus divalidasi di backend. Indeks usulan: `created_at`, `(actor_user_id, created_at)`, `(subject_type, subject_id, created_at)`, `(action, created_at)`. Audit tidak boleh mengandung password/token/isi dokumen/NIK lengkap.

Retensi/purge/arsip, audit kegagalan login/aksi anonim, dan akses khusus audit belum ditetapkan. Karena itu tabel di atas adalah usulan desain, bukan persetujuan data-retention final.

## 8. Penerapan Foreign Key dan Penghapusan Data

Foreign key digunakan untuk memastikan data yang saling terhubung tidak menjadi referensi yang tidak valid.

Kebijakan awal:

- `users.role_id` mengacu pada `roles.id`.
    
- `applicant_profiles.user_id` mengacu pada `users.id`.
    
- `applications.applicant_id` mengacu pada `applicant_profiles.id`.
    
- `applications.wave_id` mengacu pada `admission_waves.id`.
    
- `applications.study_program_id` mengacu pada `study_programs.id`.
    
- `application_documents.application_id` mengacu pada `applications.id`.
    
- `application_status_histories.application_id` mengacu pada `applications.id`.
    
- `application_status_histories.changed_by_user_id` mengacu pada `users.id` dan boleh `NULL`.
    

Untuk data pendaftaran yang sudah diproses, gunakan pendekatan pengarsipan atau penonaktifan apabila sesuai kebijakan. Hindari `cascade delete` yang dapat menghapus pengajuan, dokumen, atau riwayat penting secara tidak sengaja. Akun yang direferensikan pada `application_status_histories.changed_by_user_id` menggunakan `RESTRICT`; nonaktifkan akun untuk mempertahankan identitas pelaku. Jika `audit_logs` opsional disetujui, `actor_user_id` menggunakan `SET NULL` agar penghapusan akun tidak menghapus baris audit.

Pada migration, aturan `onDelete` harus ditentukan secara eksplisit dan konsisten dengan kebijakan retensi data. Penghapusan pengguna yang tercatat pada riwayat status harus dirancang dengan hati-hati agar audit trail tetap dapat dipertahankan.

## 9. Seeder Data Awal

Seeder digunakan untuk memasukkan data master yang diperlukan agar sistem dapat dijalankan dan diuji.

### 9.1. `RoleSeeder`

Memasukkan peran:

- `calon_mahasiswa`
    
- `admin`
    
- `panitia`
    
- `pimpinan`
    

Seeder harus idempotent, misalnya menggunakan `updateOrCreate`, agar dapat dijalankan ulang tanpa menghasilkan data peran ganda.

### 9.2. `StudyProgramSeeder`

Memasukkan data program studi untuk lingkungan pengembangan, misalnya:

- Informatika
    
- Sistem Informasi
    
- Teknik Industri
    
- Manajemen
    

Data ini merupakan data prototipe dan harus diperiksa kembali dengan pihak institusi.

### 9.3. `AdmissionWaveSeeder`

Data gelombang dapat dibuat sebagai data contoh di lingkungan pengembangan. Tanggal dan status aktifnya tidak boleh dianggap sebagai jadwal resmi.

### 9.4. `AdminUserSeeder`

Akun admin untuk pengujian harus menggunakan email dan kata sandi khusus lingkungan lokal. Kata sandi harus di-hash menggunakan mekanisme Laravel.

Jangan menyimpan kata sandi produksi dalam kode sumber, repository Git, atau seeder yang digunakan bersama.

## 10. Prosedur Implementasi Laravel

### Langkah 1 — Konfigurasi database

Atur koneksi database MySQL pada file `.env` lokal, lalu pastikan database dan akun database telah tersedia dengan hak akses yang sesuai.

Contoh variabel konfigurasi:

```
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=pmb_itb_akiday
DB_USERNAME=your_local_db_user
DB_PASSWORD=your_local_db_password
```

Nilai di atas merupakan contoh konfigurasi. Ganti nama database dan kredensial sesuai lingkungan masing-masing. Jangan mengunggah file `.env` ke Git.

### Langkah 2 — Buat file migration

Gunakan Artisan untuk membuat migration:

```
php artisan make:migration create_roles_table
php artisan make:migration create_users_table
php artisan make:migration create_applicant_profiles_table
php artisan make:migration create_admission_waves_table
php artisan make:migration create_study_programs_table
php artisan make:migration create_applications_table
php artisan make:migration create_application_documents_table
php artisan make:migration create_application_status_histories_table
# create_audit_logs_table hanya setelah usulan audit disetujui
```

Nama file yang dihasilkan Laravel dilengkapi timestamp. Pastikan urutan timestamp sesuai dengan ketergantungan foreign key.

### Langkah 3 — Definisikan struktur tabel

Setiap file migration berisi metode `up()` untuk membuat atau mengubah struktur database dan `down()` untuk membatalkan perubahan tersebut.

Contoh pola migration untuk tabel `applications`:

```
Schema::create('applications', function (Blueprint $table) {
    $table->id();

    $table->foreignId('applicant_id')
        ->constrained('applicant_profiles')
        ->restrictOnDelete();

    $table->foreignId('wave_id')
        ->constrained('admission_waves')
        ->restrictOnDelete();

    $table->foreignId('study_program_id')
        ->constrained('study_programs')
        ->restrictOnDelete();

    $table->string('registration_number')->unique();

    $table->string('status')->default('draft');

    $table->dateTime('submitted_at')->nullable();

    $table->timestamps();

    $table->unique(
        ['applicant_id', 'wave_id'],
        'applications_applicant_wave_unique'
    );

    $table->index('status');
    $table->index(['wave_id', 'study_program_id']);
});
```

Contoh tersebut menggambarkan pola inti. Sesuaikan aturan penghapusan dengan kebijakan retensi data yang ditetapkan untuk seluruh tabel.

### Langkah 4 — Jalankan migration

Untuk database pengembangan yang masih kosong:

```
php artisan migrate
```

Periksa hasilnya pada database dan pastikan seluruh tabel terbentuk sesuai ERD.

**Peringatan:** Jangan menjalankan `php artisan migrate:fresh` pada database yang berisi data penting karena perintah tersebut menghapus seluruh tabel sebelum membuat ulang struktur database.

### Langkah 5 — Buat dan jalankan seeder

Buat seeder menggunakan Artisan:

```
php artisan make:seeder RoleSeeder
php artisan make:seeder StudyProgramSeeder
php artisan make:seeder AdmissionWaveSeeder
php artisan make:seeder AdminUserSeeder
```

Setelah seeder dimasukkan ke `DatabaseSeeder`, jalankan:

```
php artisan db:seed
```

Untuk pengembangan, data contoh harus dapat dibuat ulang dengan aman tanpa menimbulkan duplikasi.

## 11. Pengujian Migration

Migration tidak cukup hanya berhasil dijalankan. Struktur dan constraint juga harus diuji.

|ID|Skenario|Hasil yang Diharapkan|
|---|---|---|
|MIG-01|Menjalankan migration pada database kosong|Semua tabel berhasil dibuat.|
|MIG-02|Menjalankan migration kembali setelah semua migration tercatat|Tidak ada tabel yang dibuat ganda.|
|MIG-03|Memasukkan email pengguna yang sama dua kali|Database menolak duplikasi email.|
|MIG-04|Memasukkan kode program studi yang sama dua kali|Database menolak duplikasi kode.|
|MIG-05|Membuat dua pengajuan untuk profil dan gelombang yang sama|Pengajuan kedua ditolak oleh constraint unik.|
|MIG-06|Membuat pengajuan untuk profil yang sama pada gelombang berbeda|Diperbolehkan jika aturan gelombang mengizinkan.|
|MIG-07|Membuat pengajuan dengan foreign key yang tidak valid|Database menolak data tersebut.|
|MIG-08|Menyimpan status di luar daftar yang diizinkan|Validasi backend menolak status.|
|MIG-09|Menyimpan riwayat perubahan status|Catatan riwayat terhubung dengan pengajuan yang benar.|
|MIG-10|Menjalankan rollback pada database pengujian|Perubahan dibatalkan sesuai definisi `down()`.|
|MIG-11|Menjalankan seeder berulang kali|Tidak menghasilkan data master ganda.|

Untuk pengujian otomatis, gunakan Laravel PHPUnit atau Pest sesuai konfigurasi proyek. Gunakan database pengujian terpisah dan jangan menjalankan pengujian destruktif terhadap data lokal yang dibutuhkan.

## 12. Penanganan Risiko Konsistensi Data

Risiko penting pada sistem ini adalah dua permintaan pengajuan masuk secara bersamaan. Jika aplikasi hanya memeriksa keberadaan pengajuan sebelum menyimpan, kedua permintaan dapat lolos pemeriksaan dan menghasilkan duplikasi.

Pencegahannya:

1. Terapkan `UNIQUE(applicant_id, wave_id)` pada database.
    
2. Lakukan pemeriksaan awal pada backend untuk memberikan pesan yang mudah dipahami.
    
3. Tangani pelanggaran constraint unik sebagai respons konflik yang sesuai, bukan sebagai kesalahan server yang tidak tertangani.
    
4. Gunakan transaksi database saat menyimpan pengajuan dan riwayat status awal.
    
5. Uji skenario dua permintaan bersamaan untuk memastikan hanya satu pengajuan yang tersimpan.
    

Constraint database menjadi perlindungan terakhir karena validasi aplikasi saja tidak cukup untuk mencegah kondisi balapan (_race condition_).

## 13. Strategi Rollback dan Perubahan Skema

Setiap perubahan struktur database harus dilakukan melalui migration baru, bukan dengan mengubah migration lama yang sudah diterapkan pada lingkungan bersama atau produksi.

Prosedur perubahan:

1. Identifikasi kebutuhan perubahan.
    
2. Perbarui dokumen `Database Model.md`, `ERD.md`, dan `Data Dictionary.md` jika diperlukan.
    
3. Buat migration baru.
    
4. Uji migration pada database pengembangan.
    
5. Periksa dampak perubahan terhadap data lama, API, model, dan aplikasi Android.
    
6. Siapkan backup sebelum menerapkan perubahan berisiko.
    
7. Terapkan migration pada lingkungan target.
    
8. Verifikasi struktur dan fungsi aplikasi setelah migration.
    

Rollback harus diuji terlebih dahulu. Untuk perubahan yang berpotensi menghilangkan data, jangan mengandalkan rollback otomatis tanpa backup dan rencana pemulihan.

## 14. Checklist Sebelum Migration Dianggap Selesai

- Delapan tabel domain baseline dibuat; `audit_logs` hanya bila pemilik proyek menyetujui tabel, cakupan event, dan retensi.
    
- Primary key dan foreign key telah ditentukan.
    
- Urutan migration mengikuti ketergantungan antartabel.
    
- Constraint unik email, kode program studi, nomor pendaftaran, dan pasangan calon mahasiswa-gelombang sudah diterapkan.
    
- Status pengajuan divalidasi sesuai alur bisnis.
    
- Kebijakan `NULL` dan penghapusan data sudah ditetapkan.
    
- Seeder peran dan program studi telah diuji.
    
- File dokumen disimpan di penyimpanan privat.
    
- Migration berhasil dijalankan pada database kosong.
    
- Rollback diuji pada database pengujian.
    
- Pengujian foreign key dan pengajuan ganda berhasil.
    
- Kredensial database tidak masuk ke repository.
    
- Struktur database sesuai dengan ERD dan Data Dictionary.
    

## 15. Keputusan yang Masih Perlu Dikonfirmasi

Sebelum migration dianggap final, beberapa keputusan berikut masih perlu disepakati:

1. Apakah NIK wajib unik pada seluruh sistem?
    
2. Apakah ijazah dan SKL disimpan sebagai dua jenis dokumen terpisah?
    
3. Berapa batas ukuran dan format file yang diperbolehkan?
    
4. Berapa lama data pendaftaran dan riwayat harus disimpan?
    
5. Apakah data pengguna yang dinonaktifkan tetap dipertahankan secara permanen?
    
6. Bagaimana aturan perubahan keputusan penerimaan setelah status akhir ditetapkan?
    
7. Apakah seluruh jadwal dan daftar program studi sudah sesuai dengan data resmi institusi?
8. Bagaimana status/catatan pemeriksaan dicatat untuk tiap dokumen, termasuk status yang diizinkan, pelaku/waktu, dan pemeriksaan ulang?
9. Apakah tabel audit `audit_logs` diperlukan, dan cakupan event, retensi, purge serta aksi anonim apa yang berlaku?
    

Keputusan tersebut memengaruhi constraint, validasi, penyimpanan dokumen, dan kebijakan penghapusan data.

## 16. Kesimpulan

Migration Plan PMB ITB Akiday menjadi pedoman untuk menerapkan desain ERD ke dalam database MySQL menggunakan Laravel. Pembuatan tabel dilakukan secara berurutan berdasarkan hubungan foreign key, dilengkapi constraint unik, indeks, seeder, serta pengujian integritas data.

Aturan unik pada kombinasi `applicant_id` dan `wave_id` menjadi salah satu bagian terpenting karena mencegah pengajuan ganda pada gelombang yang sama, tanpa menghalangi calon mahasiswa untuk mendaftar pada gelombang berbeda. Dengan migration yang terstruktur dan pengujian yang memadai, database dapat dikembangkan secara konsisten dan lebih aman.