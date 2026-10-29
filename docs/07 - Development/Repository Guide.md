
|Informasi|Keterangan|
|---|---|
|Nama Proyek|PMB ITB Akiday|
|Dokumen|Repository Guide|
|Versi|1.0|
|Status|Draft untuk implementasi|
|Platform|Android, Backend API, Website Admin|
|Backend|Laravel|
|Database|MySQL|
|Mobile|Kotlin|
|Version Control|Git|
|Repository Hosting|GitHub atau platform Git sejenis|

---

## 1. Tujuan

Repository Guide merupakan panduan untuk mengatur penyimpanan kode sumber dan dokumentasi proyek PMB ITB Akiday. Panduan ini dibuat agar seluruh bagian sistem memiliki struktur yang jelas, perubahan kode dapat dilacak, dan proses pengembangan dapat dilakukan secara teratur.

Proyek PMB ITB Akiday terdiri dari aplikasi Android untuk calon mahasiswa, backend API untuk memproses data, dan website admin untuk mengelola proses penerimaan mahasiswa baru. Karena setiap bagian memiliki kebutuhan dan teknologi yang berbeda, struktur repository harus memudahkan pengembang dalam menemukan file, memperbaiki kesalahan, menambahkan fitur, dan melakukan pengujian.

Dokumen ini juga menjadi acuan dalam penggunaan Git, penamaan branch, penulisan commit, pengelolaan konfigurasi, serta proses penggabungan perubahan kode.

## 2. Ruang Lingkup Repository

Repository proyek mencakup komponen berikut.

|Komponen|Teknologi|Fungsi|
|---|---|---|
|Android App|Kotlin|Digunakan calon mahasiswa untuk mendaftar, mengunggah dokumen, dan memantau status pendaftaran.|
|Backend API|Laravel|Memproses autentikasi, validasi, pendaftaran, dokumen, status, dan komunikasi dengan database.|
|Admin Website|React + TypeScript + Vite; klien terpisah yang mengonsumsi REST API Laravel|Digunakan admin, panitia, dan pimpinan sesuai izin. Berlaku sebagai keputusan stack untuk build; PRD Final v1.0 tetap menjadi baseline kebutuhan historis sampai diterbitkan revisi formal.|
|Database|MySQL|Menyimpan akun, profil calon mahasiswa, gelombang, program studi, pendaftaran, dokumen, dan riwayat status.|
|Documentation|Markdown|Menyimpan kebutuhan, desain sistem, kontrak API, panduan pengembangan, dan dokumentasi pengujian.|

**Keputusan stack admin untuk build:** gunakan React + TypeScript + Vite sebagai klien admin terpisah, Laravel sebagai backend REST API, dan MySQL hanya diakses oleh backend; Android juga berkomunikasi melalui API. Keputusan ini mengikuti arahan eksplisit pengguna pada 28 Oktober 2026 dan menjadi stack implementasi yang mengatur pekerjaan build. Konflik dokumentasi dicatat transparan: PRD Final v1.0 baseline yang disetujui tetap tidak diubah dan menyebut web berbasis Laravel; keputusan build tidak mengubah kebutuhan historis tersebut. Pemilik proyek perlu merilis revisi formal PRD untuk menyelaraskan baseline, tetapi revisi itu bukan blocker untuk pekerjaan build yang mengikuti keputusan stack eksplisit.

## 3. Strategi Repository

Untuk tahap awal, proyek menggunakan pendekatan **monorepo**, yaitu menyimpan beberapa komponen aplikasi dalam satu repository Git.

Pendekatan ini dipilih agar aplikasi Android, backend API, website admin, dan dokumentasi proyek dapat dikelola dalam satu tempat. Dengan demikian, perubahan pada kontrak API dan implementasi aplikasi lebih mudah ditinjau secara bersamaan.

Struktur repository yang direkomendasikan:

```
pmb-itb-akiday/
├── android-app/
├── admin-web/
├── backend-api/
├── docs/
├── database/
├── scripts/
├── .gitignore
├── .editorconfig
├── README.md
└── CONTRIBUTING.md
```

Penjelasan direktori:

|Direktori/File|Kegunaan|
|---|---|
|`android-app/`|Kode sumber aplikasi Android berbasis Kotlin.|
|`backend-api/`|Aplikasi Laravel yang menangani REST API, aturan bisnis, akses database, dan penyimpanan dokumen privat.|
|`docs/`|Dokumentasi teknis proyek dalam format Markdown.|
|`database/`|Dokumentasi ERD, data dictionary, dan rancangan database.|
|`scripts/`|Skrip pendukung pengembangan yang aman untuk dibagikan.|
|`.gitignore`|Menentukan file dan direktori yang tidak boleh masuk ke Git.|
|`.editorconfig`|Membantu menjaga konsistensi format file.|
|`README.md`|Pengenalan proyek dan petunjuk awal menjalankan aplikasi.|
|`CONTRIBUTING.md`|Aturan kontribusi dan proses perubahan kode.|

Folder `docs/` dan `database/` dapat disesuaikan dengan struktur vault Obsidian. Dokumentasi yang sudah disimpan di Obsidian tidak otomatis tersinkron dengan repository; file Markdown perlu disalin atau dikelola melalui folder yang memang berada dalam repository.

### 3.1 Struktur Backend

Direktori `backend-api/` mengikuti struktur standar Laravel.

```
backend-api/
├── app/
│   ├── Http/
│   │   ├── Controllers/
│   │   ├── Middleware/
│   │   └── Requests/
│   ├── Models/
│   ├── Policies/
│   ├── Services/
│   └── Providers/
├── bootstrap/
├── config/
├── database/
│   ├── factories/
│   ├── migrations/
│   └── seeders/
├── public/
├── resources/
│   ├── css/
│   ├── js/
│   └── views/
├── routes/
│   ├── api.php
│   └── web.php
├── storage/
├── tests/
│   ├── Feature/
│   └── Unit/
├── .env.example
├── artisan
├── composer.json
└── composer.lock
```

Fungsi direktori penting:

- `Controllers/`: menerima request dan mengatur alur pemrosesan.
- `Requests/`: menangani validasi input request.
- `Models/`: merepresentasikan entitas dan relasi database.
- `Policies/`: mengatur otorisasi terhadap objek tertentu.
- `Services/`: menempatkan logika bisnis yang perlu dipisahkan dari controller.
- `migrations/`: menyimpan perubahan struktur database.
- `seeders/`: menyediakan data awal, seperti role dan program studi contoh.
- `routes/api.php`: mendefinisikan endpoint REST API.
- `routes/web.php`: mendefinisikan route web Laravel jika diperlukan oleh backend; website admin React tersendiri menggunakan API backend.
- `tests/`: menyimpan pengujian otomatis.

Struktur tersebut merupakan rekomendasi awal. Direktori dapat disederhanakan atau ditambahkan sesuai kebutuhan implementasi.

### 3.2 Struktur Android

Direktori `android-app/` berisi proyek Android berbasis Kotlin.

```
android-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── id/
│   │   │   │       └── ac/
│   │   │   │           └── pmbitbakiday/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   └── androidTest/
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── .gitignore
```

Struktur package Kotlin dapat dikembangkan berdasarkan tanggung jawab fitur, misalnya:

```
id.ac.pmbitbakiday/
├── data/
│   ├── api/
│   ├── repository/
│   └── model/
├── domain/
├── ui/
│   ├── auth/
│   ├── dashboard/
│   ├── profile/
│   ├── application/
│   ├── documents/
│   └── status/
└── utils/
```

Struktur package tersebut merupakan rancangan awal, bukan kewajiban menggunakan arsitektur tertentu. Pemisahan kode harus tetap menjaga tanggung jawab setiap komponen agar mudah dipahami dan diuji.

## 4. Aturan Penamaan File dan Kode

Penamaan file dan kode harus konsisten agar pengembang dapat memahami fungsi setiap komponen.

### 4.1 Backend Laravel

|Elemen|Aturan|Contoh|
|---|---|---|
|Model|PascalCase, bentuk tunggal|`Application.php`|
|Controller|PascalCase dengan akhiran `Controller`|`ApplicationController.php`|
|Form Request|PascalCase dengan akhiran `Request`|`SubmitApplicationRequest.php`|
|Service|PascalCase dengan akhiran `Service`|`ApplicationService.php`|
|Policy|PascalCase dengan akhiran `Policy`|`ApplicationPolicy.php`|
|Migration|Mengikuti format nama migration Laravel|`create_applications_table.php`|
|Seeder|PascalCase dengan akhiran `Seeder`|`RoleSeeder.php`|
|Route|Menggunakan pola URL yang konsisten|`/api/v1/applications`|

Nama class mengikuti konvensi PascalCase, sedangkan nama method dan variabel PHP menggunakan camelCase.

### 4.2 Android Kotlin

|Elemen|Aturan|Contoh|
|---|---|---|
|Class|PascalCase|`ApplicationRepository`|
|Function|camelCase|`submitApplication()`|
|Variable|camelCase|`registrationNumber`|
|Constant|UPPER_SNAKE_CASE jika sesuai konvensi|`MAX_UPLOAD_SIZE`|
|Layout XML|lowercase dengan underscore|`activity_dashboard.xml`|
|Resource ID|lowercase dengan underscore|`btn_submit_application`|
|Test class|Nama class yang diuji ditambah `Test`|`ApplicationRepositoryTest`|

Nama variabel harus menjelaskan informasi yang disimpan. Hindari nama yang tidak bermakna seperti `data1`, `temp2`, atau `x` jika terdapat nama yang lebih jelas.

### 4.3 Dokumentasi

File dokumentasi menggunakan nama yang menjelaskan isinya.

Contoh:

```
README.md
CONTRIBUTING.md
API Contract.md
Database Model.md
Repository Guide.md
```

Nama file dalam repository sebaiknya konsisten. Jika memungkinkan, gunakan nama file tanpa spasi untuk mempermudah penggunaan command line, misalnya `api-contract.md`. Apabila mengikuti struktur vault Obsidian yang sudah ditentukan, nama file dengan spasi dapat dipertahankan.

## 5. Penggunaan Git

Git digunakan untuk mencatat perubahan kode dan memungkinkan pengembang meninjau, membandingkan, serta memulihkan perubahan jika diperlukan.

Setiap pengembang harus menggunakan Git sejak awal pengembangan, bukan hanya ketika aplikasi selesai dibuat.

### 5.1 Inisialisasi Repository

Jika repository belum dibuat:

```
git init
git branch -M main
```

Setelah repository GitHub dibuat, hubungkan repository lokal dengan remote:

```
git remote add origin <URL_REPOSITORY>
git add .
git commit -m "chore: initial project structure"
git push -u origin main
```

Ganti `<URL_REPOSITORY>` dengan alamat repository yang sebenarnya.

Jika repository sudah tersedia di GitHub, lebih baik clone repository tersebut daripada membuat repository baru secara terpisah.

```
git clone <URL_REPOSITORY>
cd pmb-itb-akiday
```

### 5.2 Branch Utama

Proyek menggunakan branch berikut sebagai rekomendasi awal.

|Branch|Fungsi|
|---|---|
|`main`|Menyimpan versi stabil yang sudah melewati pemeriksaan.|
|`develop`|Mengintegrasikan fitur yang sedang dikembangkan sebelum masuk ke `main`.|
|`feature/*`|Mengembangkan fitur baru.|
|`fix/*`|Memperbaiki kesalahan yang ditemukan selama pengembangan.|
|`hotfix/*`|Memperbaiki masalah mendesak pada versi stabil.|

Untuk proyek dengan tim kecil, branch `develop` dapat digunakan jika proses integrasi memang membutuhkannya. Jika pengembangan dilakukan sendiri atau sederhana, penggunaan `main` dan branch fitur sudah dapat mencukupi.

Contoh nama branch:

```
feature/applicant-registration
feature/document-upload
feature/application-status
feature/admin-dashboard
fix/application-validation
fix/document-download
hotfix/authentication-error
```

Nama branch harus singkat, jelas, dan menggunakan huruf kecil serta tanda hubung.

### 5.3 Alur Kerja Branch

Alur pengembangan yang direkomendasikan:

1. Pastikan branch dasar sudah terbaru.
2. Buat branch baru untuk fitur atau perbaikan.
3. Lakukan perubahan kode sesuai kebutuhan.
4. Jalankan pemeriksaan dan pengujian yang relevan.
5. Simpan perubahan dengan commit yang jelas.
6. Push branch ke repository remote.
7. Ajukan Pull Request untuk ditinjau.
8. Perbaiki temuan review jika ada.
9. Gabungkan perubahan setelah pemeriksaan selesai.

Contoh:

```
git switch develop
git pull origin develop

git switch -c feature/applicant-registration
```

Setelah fitur selesai:

```
git add .
git commit -m "feat: add applicant registration"
git push -u origin feature/applicant-registration
```

Perintah di atas mengasumsikan branch `develop` sudah tersedia. Jika belum, gunakan branch dasar yang ditetapkan dalam repository.

## 6. Standar Commit

Commit harus menjelaskan perubahan yang dilakukan, bukan hanya menyatakan bahwa pengembang telah mengubah kode.

Proyek menggunakan pola **Conventional Commits** sebagai standar penamaan pesan commit.

Format:

```
<type>: <deskripsi singkat>
```

Jenis commit yang digunakan:

|Type|Penggunaan|Contoh|
|---|---|---|
|`feat`|Menambahkan fitur|`feat: add applicant registration`|
|`fix`|Memperbaiki bug|`fix: prevent duplicate applications`|
|`docs`|Mengubah dokumentasi|`docs: update repository guide`|
|`refactor`|Merapikan kode tanpa mengubah perilaku utama|`refactor: simplify application service`|
|`test`|Menambah atau memperbaiki pengujian|`test: add application validation tests`|
|`chore`|Perubahan konfigurasi atau pemeliharaan|`chore: update development dependencies`|
|`style`|Perubahan format kode tanpa mengubah logika|`style: format Kotlin source files`|

Ketentuan commit:

- Satu commit sebaiknya berisi perubahan yang saling berkaitan.
- Hindari menggabungkan banyak fitur yang tidak berhubungan dalam satu commit.
- Jangan menggunakan pesan seperti `update`, `fix`, atau `perbaikan` tanpa penjelasan tambahan.
- Jangan memasukkan password, token, atau file rahasia ke dalam commit.
- Pastikan perubahan penting sudah diuji sebelum di-commit.

Contoh commit yang baik:

```
feat: add application status history
fix: validate uploaded diploma files
docs: document application API endpoints
test: cover duplicate application submission
```

## 7. Pengelolaan File dan Konfigurasi

Tidak semua file yang ada di komputer pengembang boleh dimasukkan ke repository. File kode sumber dan konfigurasi contoh perlu dipisahkan dari file sementara, hasil build, serta informasi rahasia.

### 7.1 File yang Tidak Boleh Di-commit

File berikut tidak boleh dimasukkan ke repository:

- File `.env` yang berisi konfigurasi lokal atau rahasia.
- Password database dan kredensial akun.
- Token akses dan API key.
- File build hasil kompilasi yang tidak diperlukan.
- Direktori cache dan file sementara.
- File upload calon mahasiswa yang berisi data pribadi.
- Salinan database lokal yang berisi data pribadi atau data pengujian sensitif.
- Kunci penandatanganan aplikasi Android dan file keystore.
- Log aplikasi yang mengandung data sensitif.

Gunakan `.gitignore` untuk mengecualikan file tersebut.

### 7.2 Contoh `.gitignore` di Root

```
# Environment files
.env
.env.*
!.env.example

# Editor and operating system
.vscode/
.idea/
.DS_Store
Thumbs.db

# Temporary files
*.log
*.tmp
*.bak

# Secrets and private keys
*.pem
*.key
*.p12
*.jks
*.keystore

# Database dumps
*.sql
*.sqlite
*.sqlite3

# Uploaded private documents
private-uploads/
```

**Perhatian:** Pola `.gitignore` harus disesuaikan dengan kebutuhan proyek. Jangan mengecualikan seluruh file SQL jika proyek menyimpan migration atau skrip database yang memang harus dikelola di Git. Contoh di atas mengecualikan dump database berakhiran `.sql`; aturan dapat diperketat dengan pola direktori jika migration atau skrip SQL dibutuhkan dalam repository.

Untuk file sensitif yang sudah pernah di-commit, menambahkannya ke `.gitignore` tidak otomatis menghapusnya dari riwayat Git. File harus ditangani secara terpisah dan kredensial yang terlanjur terekspos harus segera diganti.

### 7.3 Konfigurasi Backend

Backend Laravel menggunakan `.env` untuk menyimpan konfigurasi lingkungan lokal, seperti koneksi database dan pengaturan aplikasi.

Contoh `.env.example` untuk backend (dibuat di `backend-api/` saat backend disiapkan):

```
APP_NAME="PMB ITB Akiday"
APP_ENV=local
APP_DEBUG=true
APP_URL=http://localhost:8000

DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=pmb_itb_akiday
DB_USERNAME=
DB_PASSWORD=
```

File `.env.example` hanya berisi contoh konfigurasi, bukan kredensial aktif.

Setiap pengembang membuat file `.env` lokal berdasarkan contoh tersebut dan mengisi nilai yang sesuai dengan lingkungan masing-masing.

Untuk lingkungan produksi, `APP_DEBUG` harus dinonaktifkan dan konfigurasi rahasia harus dikelola secara aman.

### 7.4 Konfigurasi Android

Alamat backend untuk pengembangan lokal harus dibedakan dari alamat server produksi.

Contoh alamat yang digunakan:

|Lingkungan|Alamat|
|---|---|
|Backend lokal di komputer pengembang|`http://localhost:8000/api/v1`|
|Android Emulator yang mengakses komputer host|`http://10.0.2.2:8000/api/v1`|
|Server produksi|Alamat HTTPS yang ditetapkan saat deployment|

`localhost` pada Android Emulator menunjuk ke emulator itu sendiri, bukan langsung ke komputer yang menjalankan Laravel. Karena itu, alamat `10.0.2.2` dapat digunakan untuk mengakses layanan pada komputer host dari Android Emulator standar.

Alamat server tidak boleh tersebar secara tidak terkontrol di banyak bagian kode. Gunakan konfigurasi lingkungan atau mekanisme konfigurasi aplikasi yang konsisten.

Untuk produksi, gunakan HTTPS dan jangan menonaktifkan pemeriksaan sertifikat TLS demi mempermudah koneksi.

## 8. Sinkronisasi Struktur Database

Perubahan struktur database harus dicatat menggunakan migration Laravel.

Pengembang tidak boleh hanya mengubah tabel secara manual di database lokal tanpa menyediakan migration yang diperlukan oleh anggota tim lainnya.

Contoh pembuatan migration:

```
php artisan make:migration create_applications_table
```

Setelah migration dibuat dan diperiksa:

```
php artisan migrate
```

Untuk data awal yang dibutuhkan aplikasi, gunakan seeder.

```
php artisan make:seeder RoleSeeder
php artisan db:seed
```

Ketentuan database:

1. Setiap perubahan struktur tabel harus tercatat dalam migration.
2. Migration harus memiliki nama yang jelas dan urutan yang sesuai.
3. Relasi foreign key harus didefinisikan sesuai rancangan database.
4. Constraint penting, termasuk unique constraint, harus dibuat pada database.
5. Seeder hanya boleh memasukkan data awal yang memang dibutuhkan.
6. Data pribadi calon mahasiswa tidak boleh dimasukkan ke seeder repository.
7. Migration yang sudah digunakan bersama atau pada lingkungan yang memiliki data penting tidak boleh diubah sembarangan; buat migration baru untuk perubahan berikutnya.
8. Perubahan skema harus ditinjau dampaknya terhadap API dan aplikasi Android.

Untuk proyek PMB ITB Akiday, constraint unik pada pasangan `applicant_id` dan `wave_id` harus dipertahankan agar satu akun tidak dapat membuat lebih dari satu pendaftaran pada gelombang yang sama.

Aturan tersebut tidak berarti satu akun hanya boleh mendaftar sekali sepanjang waktu. Akun dapat mengajukan pendaftaran pada gelombang lain sesuai ketentuan sistem.

## 9. Pengujian Sebelum Perubahan Digabungkan

Setiap perubahan harus diuji sesuai komponen yang terdampak.

### 9.1 Backend

Jalankan pengujian Laravel:

```
cd backend-api
php artisan test
```

Jika pengujian menggunakan konfigurasi database tertentu, pastikan konfigurasi pengujian sudah disiapkan agar tidak memengaruhi database pengembangan atau produksi.

Periksa juga:

- Validasi request.
- Autentikasi dan otorisasi.
- Hak akses berdasarkan role.
- Kepemilikan data pendaftaran.
- Transisi status pendaftaran.
- Pengunggahan dan pengunduhan dokumen.
- Penanganan duplikasi pendaftaran.
- Pencatatan riwayat status.

### 9.2 Android

Pengujian dapat dijalankan melalui Android Studio atau Gradle.

Contoh:

```
./gradlew test
```

Pada Windows, gunakan:

```
gradlew.bat test
```

Pengujian harus disesuaikan dengan konfigurasi Gradle dan jenis test yang tersedia.

Periksa:

- Validasi input formulir.
- Login dan logout.
- Komunikasi dengan API.
- Penanganan koneksi gagal.
- Pengunggahan dokumen.
- Penampilan status pendaftaran.
- Penanganan respons error dari backend.

### 9.3 Pemeriksaan Manual

Sebelum perubahan digabungkan, lakukan pemeriksaan manual jika fitur berhubungan dengan alur penting.

Contoh skenario:

1. Calon mahasiswa login menggunakan akun yang valid.
2. Calon mahasiswa melengkapi profil.
3. Calon mahasiswa memilih gelombang yang tersedia.
4. Calon mahasiswa memilih satu program studi.
5. Calon mahasiswa mengunggah dokumen yang diperlukan.
6. Sistem memvalidasi data sebelum pendaftaran dikirim.
7. Sistem menolak pendaftaran kedua pada gelombang yang sama.
8. Panitia memeriksa pendaftaran dan memperbarui status sesuai kewenangan.
9. Calon mahasiswa hanya dapat melihat pendaftaran miliknya sendiri.
10. Sistem mencatat perubahan status dalam riwayat.

Pendaftaran yang dikirim harus diproses secara konsisten. Validasi pada aplikasi Android tidak menggantikan validasi backend dan constraint database.

## 10. Pull Request dan Code Review

Pull Request digunakan untuk meninjau perubahan sebelum digabungkan ke branch tujuan.

Setiap Pull Request sebaiknya mencakup:

- Judul yang menjelaskan fitur atau perbaikan.
- Deskripsi perubahan.
- Daftar file atau komponen utama yang berubah.
- Hasil pengujian.
- Catatan konfigurasi atau migration yang perlu dijalankan.
- Screenshot jika perubahan berhubungan dengan tampilan.
- Risiko atau pekerjaan lanjutan jika masih ada.

Contoh judul:

```
feat: add applicant registration flow
fix: prevent duplicate applications in one wave
docs: complete database documentation
```

### Checklist Review

Sebelum Pull Request disetujui, periksa hal berikut:

- [ ] Perubahan sesuai kebutuhan fitur.
- [ ] Struktur kode mengikuti standar proyek.
- [ ] Tidak ada kredensial atau data pribadi yang ikut terunggah.
- [ ] Validasi dilakukan pada backend.
- [ ] Hak akses dan kepemilikan data diperiksa.
- [ ] Perubahan database memiliki migration yang sesuai.
- [ ] Pengujian yang relevan sudah dijalankan.
- [ ] Error ditangani dengan benar.
- [ ] Dokumentasi diperbarui jika diperlukan.
- [ ] Tidak ada perubahan yang tidak berkaitan dengan tujuan Pull Request.

Untuk proyek yang dikerjakan sendiri, code review dapat dilakukan melalui pemeriksaan mandiri sebelum perubahan digabungkan. Jika dikerjakan bersama tim, sebaiknya perubahan ditinjau anggota lain.

## 11. Pengelolaan Dokumentasi

Dokumentasi proyek harus diperbarui ketika terdapat perubahan yang memengaruhi kebutuhan, desain, implementasi, atau pengujian.

Dokumentasi yang perlu diperhatikan meliputi:

|Dokumen|Diperbarui ketika|
|---|---|
|`README.md`|Cara menjalankan proyek, kebutuhan sistem, atau struktur utama berubah.|
|`SRS.md`|Kebutuhan fungsional atau nonfungsional berubah.|
|`Database Model.md`|Entitas, atribut, relasi, atau constraint database berubah.|
|`ERD.md`|Struktur atau hubungan antartabel berubah.|
|`Data Dictionary.md`|Definisi kolom atau aturan data berubah.|
|`API Contract.md`|Endpoint, request, response, atau aturan autentikasi berubah.|
|`Security Specification.md`|Ketentuan keamanan atau kontrol akses berubah.|
|`Test Plan.md`|Strategi dan cakupan pengujian berubah.|
|`Deployment Guide.md`|Proses deployment atau konfigurasi server berubah.|

Jika dokumentasi disimpan di vault Obsidian, tentukan satu lokasi utama untuk setiap dokumen agar tidak muncul beberapa versi yang berbeda. Salinan yang dikelola dalam repository harus disinkronkan sesuai prosedur tim.

## 12. README Repository

File `README.md` menjadi halaman awal bagi siapa pun yang membuka repository proyek.

Contoh isi awal:

```
# PMB ITB Akiday

Sistem Penerimaan Mahasiswa Baru ITB Akiday.

## Komponen

- Android App: Kotlin
- Backend API: Laravel
- Admin Website: Laravel
- Database: MySQL

## Fitur Utama

- Registrasi dan login calon mahasiswa
- Pengelolaan profil calon mahasiswa
- Pendaftaran berdasarkan gelombang
- Pemilihan program studi
- Unggah dokumen persyaratan
- Pemantauan status pendaftaran
- Verifikasi dan keputusan oleh panitia
- Dashboard dan laporan penerimaan

## Struktur Repository

- `android-app/`: aplikasi Android
- `admin-web/`: website admin React + TypeScript
- `backend-api/`: backend API Laravel
- `docs/`: dokumentasi teknis
- `database/`: dokumentasi database
- `scripts/`: skrip pendukung

## Status

Proyek dalam tahap pengembangan.

## Dokumentasi

Lihat direktori `docs/` untuk dokumentasi kebutuhan,
desain sistem, API, database, keamanan, dan pengujian.
```

README tersebut merupakan template awal. Petunjuk instalasi, versi perangkat lunak, dan perintah menjalankan proyek harus diperbarui setelah lingkungan pengembangan benar-benar ditetapkan.

## 13. Pembagian Tanggung Jawab

Pembagian tugas dapat disesuaikan dengan jumlah anggota tim.

|Peran|Tanggung Jawab|
|---|---|
|Android Developer|Mengembangkan UI Android, validasi tampilan, komunikasi API, dan pengujian aplikasi.|
|Backend Developer|Mengembangkan endpoint API, logika bisnis, autentikasi, validasi, dan integrasi database.|
|Admin Website Developer|Mengembangkan dashboard admin, pengelolaan pendaftaran, dan laporan.|
|Database Designer|Menjaga konsistensi ERD, migration, relasi, dan constraint database.|
|Tester|Menyusun skenario pengujian, menjalankan test, dan mencatat bug.|
|Project Coordinator|Mengelola pembagian tugas, dokumentasi, dan integrasi perubahan.|

Satu orang dapat menjalankan beberapa peran jika ukuran tim terbatas. Meskipun demikian, setiap perubahan tetap perlu diperiksa terhadap kebutuhan sistem dan standar proyek.

## 14. Prosedur Ketika Terjadi Konflik Git

Konflik Git dapat terjadi ketika beberapa perubahan menyentuh bagian file yang sama.

Langkah penanganannya:

1. Simpan atau commit perubahan lokal yang sudah siap.
2. Ambil perubahan terbaru dari branch tujuan.
3. Lakukan merge atau rebase sesuai kebijakan tim.
4. Buka file yang mengalami konflik.
5. Periksa isi perubahan dari kedua sisi.
6. Gabungkan perubahan dengan mempertahankan perilaku yang benar.
7. Hapus penanda konflik Git.
8. Jalankan kembali pengujian yang relevan.
9. Commit hasil penyelesaian konflik.

Contoh penanda konflik:

```
<<<<<<< HEAD
Kode dari branch saat ini
=======
Kode dari branch yang digabungkan
>>>>>>> feature/nama-fitur
```

Penanda tersebut harus diselesaikan dan dihapus sebelum perubahan disimpan sebagai hasil akhir.

Untuk file migration, konfigurasi, dan kontrak API, konflik perlu diperiksa dengan hati-hati karena penyelesaian yang salah dapat menyebabkan ketidaksesuaian antara backend, database, dan aplikasi Android.

## 15. Aturan Keamanan Repository

Karena sistem PMB menangani data pribadi calon mahasiswa, repository harus dikelola dengan memperhatikan keamanan.

Aturan utama:

1. Jangan mengunggah NIK, alamat, nomor telepon, foto, atau dokumen asli calon mahasiswa.
2. Jangan menyimpan password, token, private key, dan kredensial aktif di repository.
3. Jangan memberikan hak akses repository kepada pihak yang tidak berkepentingan.
4. Jangan menempatkan dokumen pendaftaran pada direktori publik hanya demi memudahkan pengujian.
5. Gunakan data dummy untuk pengembangan dan pengujian.
6. Jangan menonaktifkan pemeriksaan keamanan hanya untuk membuat fitur bekerja.
7. Periksa perubahan file sebelum menjalankan `git push`.
8. Jika rahasia terlanjur terunggah, segera cabut atau rotasi rahasia tersebut dan tangani riwayat Git sesuai kebutuhan.
9. Pastikan konfigurasi produksi berbeda dari konfigurasi lokal.
10. Hindari menampilkan data sensitif dalam log, pesan error, dan contoh dokumentasi.

Repository privat membantu membatasi akses, tetapi tidak menggantikan praktik pengelolaan rahasia dan data pribadi yang benar.

## 16. Alur Pengembangan Fitur

Contoh alur pengembangan fitur pendaftaran calon mahasiswa:

```
Analisis Kebutuhan
        |
        v
Perbarui Dokumentasi
        |
        v
Buat Branch Fitur
        |
        v
Implementasi Backend dan Database
        |
        v
Implementasi Aplikasi Android
        |
        v
Pengujian API dan Aplikasi
        |
        v
Code Review
        |
        v
Merge ke Branch Integrasi
        |
        v
Uji Integrasi
        |
        v
Merge ke Main Setelah Stabil
```

Urutan tersebut dapat disesuaikan dengan kebutuhan fitur. Untuk fitur yang bergantung pada API, kontrak API sebaiknya disepakati terlebih dahulu agar backend dan Android dapat dikembangkan dengan acuan yang sama.

## 17. Kriteria Repository Siap Digunakan

Repository dapat dianggap siap digunakan untuk pengembangan apabila:

- [ ] Struktur direktori utama sudah tersedia.
- [ ] File README menjelaskan tujuan dan komponen proyek.
- [ ] `.gitignore` sudah dikonfigurasi.
- [ ] File `.env.example` tersedia tanpa rahasia aktif.
- [ ] Aplikasi Laravel dapat disiapkan mengikuti dokumentasi.
- [ ] Proyek Android dapat dibuka melalui Android Studio.
- [ ] Migration database tersedia sesuai model data.
- [ ] Aturan branch dan commit sudah ditetapkan.
- [ ] Dokumentasi utama dapat ditemukan dengan mudah.
- [ ] Pengujian dasar dapat dijalankan.
- [ ] Tidak ada data pribadi atau kredensial yang masuk ke repository.
- [ ] Alur kontribusi dan pemeriksaan perubahan sudah dipahami.

Kriteria tersebut harus diverifikasi secara langsung setelah repository dibuat dan kode sumber mulai diimplementasikan. Dokumen ini sendiri belum membuktikan bahwa repository atau aplikasinya sudah berjalan.

## 18. Keputusan yang Masih Perlu Ditetapkan

Beberapa keputusan teknis perlu dikonfirmasi ketika implementasi dimulai.

|No.|Keputusan|Status|
|---|---|---|
|1|Platform hosting repository|Belum ditetapkan secara final|
|2|Monorepo atau repository terpisah|Rekomendasi awal: monorepo|
|3|Penggunaan branch `develop`|Disesuaikan dengan alur kerja tim|
|4|Teknologi dan batas frontend website admin|Diputuskan untuk build pada 28 Oktober 2026: React + TypeScript + Vite terpisah, menggunakan REST API Laravel; PRD Final v1.0 tetap sebagai baseline historis dan perlu revisi formal pemilik proyek untuk konsistensi, tetapi tidak memblokir build yang mengikuti keputusan ini|
|5|Versi PHP, Laravel, Kotlin, dan Android SDK|Perlu ditetapkan berdasarkan lingkungan implementasi|
|6|Metode pemeriksaan otomatis pada Pull Request|Perlu ditentukan|
|7|Kebijakan review dan persetujuan merge|Disesuaikan dengan jumlah anggota tim|
|8|Lokasi utama dokumentasi Obsidian dan repository|Perlu disinkronkan|
|9|Strategi pengelolaan file rahasia dan konfigurasi deployment|Perlu ditetapkan sebelum deployment|

## 19. Kesimpulan

Repository Guide menjadi pedoman untuk menjaga kode sumber dan dokumentasi PMB ITB Akiday tetap terorganisasi selama pengembangan. Penggunaan struktur direktori yang jelas, Git, standar commit, migration database, pengujian, dan code review membantu mengurangi kesalahan serta memudahkan kolaborasi.

Aturan keamanan juga menjadi bagian penting karena sistem mengelola data pribadi calon mahasiswa. Oleh karena itu, perubahan kode harus tetap memperhatikan validasi, hak akses, konsistensi database, dan perlindungan dokumen pendaftaran.

Dengan menerapkan panduan ini secara konsisten, pengembangan aplikasi Android, backend Laravel, dan website admin dapat dilakukan secara lebih terstruktur dan mudah dipelihara.