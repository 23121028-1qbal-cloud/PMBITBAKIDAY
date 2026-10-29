## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Dokumen|Deployment Guide|
|Nama Proyek|PMB ITB Akiday|
|Versi Dokumen|1.0|
|Status Dokumen|Draft|
|Backend|Laravel|
|Database|MySQL|
|Aplikasi Mobile|Android Kotlin|
|Antarmuka Admin|Website|
|API|REST API JSON|
|Lingkungan Target|Lokal|
|Referensi|[[Environment Configuration]], [[Backup & Recovery]], [[Security Specification]]|

> **Catatan:** Panduan ini merupakan rancangan deployment awal. Versi Laravel, PHP, MySQL, Android SDK, dan dependensi lainnya harus disesuaikan dengan versi yang benar-benar digunakan pada proyek.

---

## 2. Tujuan

Deployment Guide dibuat sebagai panduan untuk menyiapkan dan menjalankan sistem PMB ITB Akiday pada lingkungan lokal. Panduan ini mencakup persiapan perangkat lunak, konfigurasi database, instalasi backend, pengujian API, pengaturan aplikasi Android, serta pemeriksaan akhir setelah sistem berjalan.

Dokumen ini juga membantu tim mengurangi kesalahan konfigurasi dan memastikan bahwa backend, database, aplikasi calon mahasiswa, serta website admin dapat saling terhubung.

## 3. Arsitektur Deployment

Sistem PMB ITB Akiday menggunakan arsitektur client-server. Aplikasi Android dan website admin mengirimkan permintaan ke backend melalui REST API, sedangkan backend memproses data dan berkomunikasi dengan database MySQL serta penyimpanan dokumen privat.

```
flowchart TD
    A["Aplikasi Android Kotlin"] -->|"HTTPS / HTTP lokal"| C["Laravel REST API"]
    B["Website Admin"] -->|"HTTP / HTTPS"| C
    C --> D[("MySQL Database")]
    C --> E["Private Document Storage"]
    C --> F["Validasi dan Otorisasi"]
```

Komponen utama:

- **Aplikasi Android:** digunakan calon mahasiswa untuk mengisi pendaftaran, mengunggah dokumen, dan memantau status.
    
- **Website admin:** digunakan admin, panitia, dan pimpinan sesuai hak aksesnya.
    
- **Backend Laravel:** menangani autentikasi, validasi, proses pendaftaran, pemeriksaan, status, dan laporan.
    
- **MySQL:** menyimpan akun, profil, gelombang, program studi, pendaftaran, metadata dokumen, serta riwayat status.
    
- **Private Document Storage:** menyimpan file dokumen yang tidak boleh diakses secara publik.
    

## 4. Persyaratan Sistem

### 4.1 Perangkat Lunak

|Komponen|Kebutuhan|
|---|---|
|Sistem operasi|Windows atau Linux|
|PHP|Versi yang didukung oleh versi Laravel yang digunakan|
|Composer|Pengelola dependensi PHP|
|MySQL|Versi yang kompatibel dengan Laravel dan aplikasi|
|Node.js dan npm|Diperlukan jika website menggunakan proses build aset frontend|
|Git|Pengelolaan versi kode|
|Android Studio|Pengembangan dan pengujian aplikasi Android|
|Android SDK|Sesuai konfigurasi proyek Android|
|Postman|Pengujian endpoint API|
|Browser|Chrome, Firefox, atau browser modern lainnya|

Pastikan ekstensi PHP yang dibutuhkan Laravel dan driver MySQL tersedia. Kebutuhan tepatnya mengikuti `composer.json`, versi Laravel, dan konfigurasi proyek.

### 4.2 Persyaratan Perangkat

Untuk lingkungan pengembangan, gunakan komputer yang mampu menjalankan server Laravel, MySQL, browser, dan Android Studio atau emulator secara bersamaan. Kebutuhan RAM dan ruang penyimpanan menyesuaikan ukuran proyek dan perangkat yang digunakan.

## 5. Persiapan Kode Sumber

Pastikan kode sumber backend, website admin, dan aplikasi Android sudah tersedia di komputer pengembangan.

Jika proyek dikelola menggunakan Git, jalankan:

```
git clone URL_REPOSITORY
```

Ganti `URL_REPOSITORY` dengan alamat repository yang benar. Jika kode sumber sudah tersedia secara lokal, langkah clone tidak diperlukan.

Masuk ke direktori backend:

```
cd NAMA_FOLDER_BACKEND
```

Periksa struktur direktori dan pastikan proyek Laravel memiliki file yang diperlukan, seperti:

- `composer.json`
    
- `.env.example`
    
- `artisan`
    
- Direktori `app/`
    
- Direktori `bootstrap/`
    
- Direktori `config/`
    
- Direktori `database/`
    
- Direktori `routes/`
    

Struktur sebenarnya dapat berbeda sesuai versi dan pengaturan proyek.

## 6. Konfigurasi Database MySQL

### 6.1 Membuat Database

Buat database khusus untuk PMB ITB Akiday melalui MySQL client atau alat administrasi database.

Contoh:

```
CREATE DATABASE pmb_itb_akiday
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Gunakan akun database khusus aplikasi dengan hak akses minimum yang dibutuhkan. Hindari penggunaan akun root untuk koneksi aplikasi sehari-hari.

### 6.2 Konfigurasi Koneksi

Atur konfigurasi database pada file `.env` backend Laravel.

Contoh konfigurasi lokal:

```
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=pmb_itb_akiday
DB_USERNAME=USER_DATABASE
DB_PASSWORD=PASSWORD_DATABASE
```

Ganti placeholder dengan akun database yang benar. Jangan mengunggah `.env` ke repository publik.

### 6.3 Pemeriksaan Koneksi

Pastikan:

1. Layanan MySQL berjalan.
    
2. Database `pmb_itb_akiday` berhasil dibuat.
    
3. Akun database memiliki hak akses yang diperlukan.
    
4. Konfigurasi `.env` sesuai dengan database.
    
5. Backend dapat terhubung tanpa error.
    

## 6A. Database Lokal dengan Docker Compose (Terverifikasi Tahap 11)

Database lokal dijalankan di Docker, sedangkan Laravel dan website admin tetap
berjalan langsung di komputer host.

Menjalankan database:

```
cp .env.example .env      # isi DB_PASSWORD dan DB_ROOT_PASSWORD, jangan commit
chmod 600 .env
docker compose up -d db
docker compose ps         # tunggu status (healthy)
docker compose logs db
```

Nilai yang dipakai Laravel, karena Laravel berjalan di host:

```
DB_CONNECTION=mariadb
DB_HOST=127.0.0.1
DB_PORT=3307
DB_DATABASE=pmb_itb_akiday
DB_USERNAME=pmb_app
DB_PASSWORD=<nilai dari .env root>
```

Hal yang perlu diperhatikan:

- `127.0.0.1` pada `backend-api/.env` berarti komputer host. Di dalam container,
  alamat tersebut menunjuk container itu sendiri. Nama service `db` dan port
  `3306` hanya dipakai bila Laravel ikut dijalankan dalam jaringan Docker.
- Port database hanya dipublikasikan ke `127.0.0.1`, sehingga tidak terbuka ke
  jaringan lain.
- Data disimpan pada named volume `pmb_mysql_data`. `docker compose stop` dan
  `docker compose down` tidak menghapus data. Jangan memakai
  `docker compose down -v` kecuali data memang akan dihapus.
- Jika volume sudah berisi data, jangan menginisialisasi ulang.
- Database kosong diinisialisasi dari `backend-api/` dengan `php artisan migrate`.
  Jangan memakai `migrate:fresh` atau `db:wipe`.
- Backup dan verifikasi restore: lihat [[Backup & Recovery]] serta
  `scripts/backup.sh` dan `scripts/restore-verify.sh`.

## 7. Instalasi Backend Laravel

### 7.1 Instal Dependensi

Dari direktori backend, jalankan:

```
composer install
```

Perintah tersebut memasang dependensi sesuai file konfigurasi proyek.

### 7.2 Siapkan File Environment

Jika `.env` belum tersedia, salin `.env.example` menjadi `.env` menggunakan metode yang sesuai dengan sistem operasi.

Contoh pada Linux:

```
cp .env.example .env
```

Pada Windows, file dapat disalin melalui File Explorer atau perintah terminal yang sesuai.

Lengkapi konfigurasi database, aplikasi, dan penyimpanan berdasarkan lingkungan lokal.

### 7.3 Buat Application Key

Jalankan:

```
php artisan key:generate
```

Perintah ini digunakan untuk membuat application key Laravel. Jangan mengganti key secara sembarangan pada instalasi yang sudah menyimpan data terenkripsi karena hal tersebut dapat memengaruhi kemampuan membaca data tersebut.

### 7.4 Jalankan Migration

Setelah memastikan konfigurasi database benar, jalankan:

```
php artisan migrate
```

Migration membuat struktur tabel sesuai file migration yang tersedia.

Sebelum menjalankan migration pada database yang sudah berisi data penting, lakukan backup dan periksa perubahan skema terlebih dahulu.

Jika proyek menyediakan seeder untuk role atau data awal, jalankan hanya setelah memeriksa isi dan dampaknya:

```
php artisan db:seed
```

Seeder program studi harus menggunakan data yang sudah diverifikasi. Daftar program studi pada prototipe belum tentu merupakan daftar resmi institusi.

### 7.5 Siapkan Penyimpanan Dokumen

Pastikan direktori penyimpanan dokumen tersedia dan backend memiliki izin baca-tulis yang diperlukan.

Dokumen pendaftaran harus disimpan secara privat. Jangan menjalankan `php artisan storage:link` untuk direktori dokumen sensitif apabila hal tersebut membuat file dapat diakses secara publik. Jika aplikasi menggunakan public storage link untuk aset nonrahasia, pisahkan aset tersebut dari dokumen pendaftaran.

### 7.6 Jalankan Server Lokal

Untuk pengembangan lokal, jalankan:

```
php artisan serve --host=0.0.0.0 --port=8000
```

Server API dapat diakses dari komputer yang sama melalui:

`http://127.0.0.1:8000`

Opsi `0.0.0.0` memungkinkan server mendengarkan pada antarmuka jaringan yang tersedia. Gunakan hanya pada jaringan pengembangan yang dipercaya dan jangan mengekspos server development langsung ke internet.

Perintah `artisan serve` ditujukan untuk pengembangan, bukan deployment produksi.

## 8. Konfigurasi API

Prefix API yang direncanakan untuk proyek ini adalah:

`/api/v1`

Contoh base URL lokal:

```
http://127.0.0.1:8000/api/v1
```

Endpoint yang direncanakan meliputi:

|Modul|Contoh Endpoint|
|---|---|
|Autentikasi|`/auth/login`|
|Data pengguna|`/auth/me`|
|Profil calon mahasiswa|`/applicant/profile`|
|Gelombang penerimaan|`/admission-waves`|
|Program studi|`/study-programs`|
|Pendaftaran|`/applications`|
|Riwayat status|`/applications/{id}/status-history`|
|Administrasi pendaftaran|`/admin/applications`|
|Dashboard|`/admin/dashboard`|
|Laporan|`/admin/reports`|

Endpoint di atas adalah rancangan kontrak API. Pastikan route benar-benar telah dibuat sebelum melakukan pengujian.

### 8.1 Pengujian API

Gunakan Postman untuk memeriksa endpoint yang sudah tersedia.

Langkah pengujian:

1. Jalankan backend.
    
2. Kirim request ke endpoint yang sesuai.
    
3. Periksa HTTP status code dan isi respons JSON.
    
4. Uji request valid dan tidak valid.
    
5. Uji endpoint yang memerlukan autentikasi.
    
6. Uji pembatasan hak akses dan kepemilikan data.
    
7. Periksa apakah perubahan data tersimpan sesuai harapan.
    

Respons API harus mengikuti struktur yang telah ditetapkan pada [[API Contract]] dan [[API Error Handling]].

## 9. Deployment Website Admin

Jika website admin berada dalam repository atau direktori terpisah, ikuti petunjuk instalasi yang disediakan proyek tersebut.

Jika website menggunakan frontend yang dibangun dengan Node.js, jalankan perintah instalasi dan build sesuai `package.json`, misalnya:

```
npm install
npm run build
```

Perintah tersebut hanya berlaku jika proyek memang menggunakan proses build npm. Jika antarmuka admin menggunakan Blade Laravel tanpa build terpisah, langkah ini dapat tidak diperlukan.

Pastikan konfigurasi website mengarah ke base URL backend yang benar. Jangan mengasumsikan bahwa endpoint admin sudah tersedia hanya karena desain antarmukanya selesai dibuat.

## 10. Konfigurasi Aplikasi Android

### 10.1 Membuka Proyek

1. Buka Android Studio.
    
2. Pilih direktori proyek Android.
    
3. Tunggu proses Gradle Sync selesai.
    
4. Pastikan SDK dan dependensi yang dibutuhkan tersedia.
    
5. Perbaiki error build sebelum menjalankan aplikasi.
    

### 10.2 Base URL API

Alamat API harus disesuaikan dengan perangkat yang menjalankan aplikasi.

|Lingkungan|Contoh Base URL|
|---|---|
|Backend dan klien pada komputer yang sama|`http://127.0.0.1:8000/api/v1`|
|Android Emulator|`http://10.0.2.2:8000/api/v1`|
|Perangkat Android fisik di jaringan lokal|`http://ALAMAT_IP_KOMPUTER:8000/api/v1`|
|Server produksi|`https://DOMAIN_API/api/v1`|

Pada Android Emulator, `127.0.0.1` mengarah ke emulator itu sendiri. Alamat `10.0.2.2` merupakan alamat khusus emulator untuk mengakses host komputer.

Untuk perangkat fisik, gunakan alamat IP komputer yang dapat dijangkau dari jaringan yang sama. Pastikan firewall mengizinkan koneksi pada port yang diperlukan.

### 10.3 Konfigurasi Keamanan

- Gunakan HTTPS pada deployment produksi.
    
- Jangan menonaktifkan validasi sertifikat TLS.
    
- Jangan menyimpan token autentikasi dalam plaintext yang mudah diakses.
    
- Gunakan penyimpanan token yang sesuai, misalnya mekanisme berbasis Android Keystore.
    
- Pastikan aplikasi menangani error jaringan dan respons API dengan benar.
    
- Hindari menyimpan dokumen sensitif ke lokasi publik tanpa perlindungan.
    

Jika HTTP digunakan untuk pengembangan lokal, batasi penggunaan pada lingkungan pengujian dan konfigurasi aplikasi secara eksplisit sesuai kebutuhan. Jangan menjadikan konfigurasi HTTP lokal sebagai konfigurasi produksi.

## 11. Pengujian Setelah Deployment

Setelah backend, database, website admin, dan aplikasi Android disiapkan, lakukan pemeriksaan berikut.

|ID|Pemeriksaan|Hasil yang Diharapkan|
|---|---|---|
|DEP-001|Backend dapat dijalankan.|Server berjalan tanpa error kritis.|
|DEP-002|Koneksi database.|Laravel dapat mengakses database yang benar.|
|DEP-003|Migration.|Tabel yang diperlukan tersedia.|
|DEP-004|Endpoint autentikasi.|Request diproses sesuai kontrak API.|
|DEP-005|Registrasi dan login.|Pengguna dapat mengakses sistem sesuai hak aksesnya.|
|DEP-006|Pembuatan pendaftaran.|Data tersimpan dengan benar.|
|DEP-007|Unggah dokumen.|File tersimpan secara privat dan metadata sesuai.|
|DEP-008|Pembatasan akses.|Pengguna tidak dapat membuka data milik pengguna lain.|
|DEP-009|Status pendaftaran.|Perubahan status dan riwayatnya konsisten.|
|DEP-010|Dashboard dan laporan.|Data laporan sesuai dengan data sumber.|
|DEP-011|Backup dan restore.|Backup dapat dipulihkan pada lingkungan pengujian.|

Catat hasil aktual, bukti, serta masalah yang ditemukan dalam [[Test Results]]. Jangan menandai pemeriksaan sebagai berhasil sebelum benar-benar dijalankan.

## 12. Pemeriksaan Keamanan Sebelum Penggunaan

Sebelum sistem digunakan oleh pengguna lain, pastikan:

- `APP_DEBUG` dinonaktifkan pada deployment yang tidak bersifat pengembangan.
    
- Kredensial database tidak ditulis langsung di kode sumber.
    
- File `.env` tidak dapat diakses melalui web.
    
- Dokumen pendaftaran tidak tersedia untuk akses publik.
    
- Setiap endpoint sensitif memeriksa autentikasi dan otorisasi di backend.
    
- Calon mahasiswa hanya dapat mengakses pendaftaran miliknya sendiri.
    
- Keputusan penerimaan hanya dapat dilakukan oleh pengguna yang berwenang.
    
- Validasi file dilakukan di server.
    
- Penanganan error tidak membocorkan stack trace atau informasi database.
    
- Backup dibuat dan diuji sesuai [[Backup & Recovery]].
    

## 13. Backup Sebelum Perubahan Besar

Sebelum migration besar, pembaruan dependensi, atau perubahan konfigurasi yang berisiko:

1. Periksa kondisi aplikasi dan database.
    
2. Buat backup database.
    
3. Buat backup dokumen pendaftaran.
    
4. Catat versi kode yang sedang berjalan.
    
5. Verifikasi backup sebelum melanjutkan.
    
6. Terapkan perubahan pada lingkungan pengujian terlebih dahulu jika memungkinkan.
    
7. Jalankan test case yang relevan setelah perubahan.
    

Jika perubahan gagal, lakukan evaluasi dan ikuti prosedur pemulihan pada [[Backup & Recovery]]. Jangan melakukan restore secara langsung tanpa memastikan backup dan target database sudah benar.

## 14. Troubleshooting

|Masalah|Kemungkinan Penyebab|Penanganan|
|---|---|---|
|Laravel tidak dapat dijalankan|Dependensi belum terpasang atau konfigurasi salah|Periksa Composer, versi PHP, dan log aplikasi|
|Koneksi database gagal|MySQL berhenti atau kredensial salah|Periksa layanan MySQL dan konfigurasi `.env`|
|Tabel tidak ditemukan|Migration belum dijalankan atau database salah|Verifikasi koneksi dan migration|
|Android gagal mengakses API|Base URL salah atau koneksi terhalang firewall|Periksa alamat host, port, dan jaringan|
|Unggah dokumen gagal|Izin direktori, ukuran file, atau validasi tidak sesuai|Periksa log dan konfigurasi penyimpanan|
|Login berhasil tetapi endpoint ditolak|Token tidak terkirim atau hak akses tidak sesuai|Periksa mekanisme autentikasi dan otorisasi|
|Laporan tidak sesuai|Query, filter, atau data sumber bermasalah|Periksa query dan validasi hasil laporan|
|Backup gagal|Ruang penyimpanan atau izin tidak memadai|Periksa pesan error, ruang, dan hak akses|

Gunakan log untuk membantu diagnosis. Hindari menampilkan informasi rahasia kepada pengguna akhir.

## 15. Checklist Deployment

### Persiapan

- Kode sumber tersedia.
    
- Versi PHP dan dependensi sesuai.
    
- MySQL telah disiapkan.
    
- File environment telah dikonfigurasi.
    
- Kredensial tidak tersimpan di repository publik.
    

### Backend dan Database

- Dependensi backend berhasil dipasang.
    
- Database dapat diakses.
    
- Migration telah dijalankan dengan benar.
    
- Data awal yang diperlukan telah disiapkan.
    
- Penyimpanan dokumen privat tersedia.
    
- Backend dapat dijalankan.
    

### Klien

- Website admin mengarah ke API yang benar.
    
- Aplikasi Android dapat dijalankan.
    
- Base URL Android telah disesuaikan dengan lingkungan.
    
- Login dan alur pendaftaran telah diuji.
    

### Keamanan dan Operasional

- Hak akses setiap role telah diuji.
    
- Dokumen pribadi tidak dapat diakses publik.
    
- Backup telah dibuat dan diverifikasi.
    
- Prosedur restore telah diuji.
    
- Test Results telah diperbarui.
    
- Masalah kritis telah diselesaikan atau deployment ditunda.
    

## 16. Kriteria Keberhasilan Deployment

Deployment lokal dapat dinyatakan berhasil apabila backend berjalan, koneksi database berfungsi, aplikasi klien dapat mengakses API, dan fitur utama lolos pemeriksaan fungsional serta keamanan.

Keberhasilan deployment tidak hanya diukur dari aplikasi yang berhasil dibuka. Sistem juga harus menjaga konsistensi data, membatasi akses sesuai role, melindungi dokumen pendaftaran, dan memiliki prosedur backup serta recovery yang dapat digunakan.

Untuk deployment produksi, diperlukan persiapan tambahan seperti web server produksi, HTTPS, konfigurasi domain, pengelolaan rahasia, monitoring, backup otomatis, dan prosedur pemulihan yang telah diuji.

## 17. Keputusan yang Belum Ditetapkan

Hal berikut perlu dipastikan sebelum deployment final:

- Versi Laravel, PHP, dan MySQL yang digunakan.
    
- Struktur repository backend dan website admin.
    
- Mekanisme autentikasi API yang benar-benar diimplementasikan.
    
- Server web dan konfigurasi deployment produksi.
    
- Domain dan sertifikat HTTPS jika akan dipublikasikan.
    
- Konfigurasi jaringan dan firewall.
    
- Lokasi serta izin penyimpanan dokumen.
    
- Jadwal backup dan target pemulihan.
    
- Prosedur update dan rollback aplikasi.
    

## 18. Kesimpulan

Deployment Guide menjadi pedoman untuk menyiapkan sistem PMB ITB Akiday agar komponen aplikasi, backend, database, dan penyimpanan dokumen dapat berjalan secara terintegrasi. Proses deployment harus dilakukan secara bertahap, mulai dari pemeriksaan dependensi dan konfigurasi hingga pengujian fungsi serta keamanan.

Dokumen ini masih berstatus draft sampai konfigurasi aktual proyek diverifikasi dan seluruh langkah yang relevan telah dijalankan. Setelah pengujian berhasil, panduan dapat diperbarui menjadi prosedur deployment yang sesuai dengan kondisi sistem sebenarnya.

---

## 19. Dokumen Terkait

- [[Environment Configuration]]
    
- [[Backup & Recovery]]
    
- [[Security Specification]]
    
- [[Access Control Matrix]]
    
- [[API Contract]]
    
- [[API Error Handling]]
    
- [[Database Model]]
    
- [[Migration Plan]]
    
- [[Test Plan]]
    
- [[Test Cases]]
    
- [[Test Results]]