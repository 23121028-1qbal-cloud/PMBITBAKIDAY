## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Dokumen|Environment Configuration|
|Nama Proyek|PMB ITB Akiday|
|Versi Dokumen|1.0|
|Status Dokumen|Draft|
|Backend|Laravel|
|Database|MySQL|
|Aplikasi Mobile|Android Kotlin|
|Website Admin|React + TypeScript + Vite; klien terpisah yang menggunakan REST API Laravel sesuai keputusan stack build 28 Oktober 2026; PRD Final v1.0 dipertahankan sebagai baseline historis sampai revisi formal|
|API|REST API JSON|
|Lingkungan Awal|Lokal|
|Referensi|[[Deployment Guide]], [[Security Specification]], [[API Contract]], [[Backup & Recovery]]|

> **Catatan:** Nama variabel dan nilai konfigurasi pada dokumen ini merupakan contoh rancangan. Sesuaikan dengan versi Laravel, struktur repository, mekanisme autentikasi, dan implementasi aktual proyek.

---

## 2. Tujuan

Environment Configuration bertujuan untuk mendokumentasikan pengaturan lingkungan yang diperlukan agar sistem PMB ITB Akiday dapat dijalankan dengan benar dan aman.

Konfigurasi lingkungan digunakan untuk memisahkan pengaturan aplikasi dari kode sumber. Dengan pendekatan ini, alamat server, koneksi database, konfigurasi penyimpanan, dan pengaturan layanan dapat disesuaikan tanpa mengubah kode program utama.

Dokumen ini juga menjadi acuan agar konfigurasi lokal, pengujian, dan produksi tidak tercampur serta informasi rahasia tidak disimpan di repository publik.

## 3. Lingkungan Sistem

Sistem PMB ITB Akiday direncanakan menggunakan beberapa lingkungan berikut.

|Lingkungan|Tujuan|Karakteristik|
|---|---|---|
|Local Development|Pengembangan fitur oleh tim|Menggunakan server dan database lokal|
|Testing|Pengujian fungsi dan integrasi|Menggunakan data dummy dan konfigurasi terpisah|
|Production|Penggunaan oleh pengguna sebenarnya|Menggunakan HTTPS, kredensial aman, monitoring, dan backup terjadwal|

### Kondisi Lokal yang Terverifikasi (29 Oktober 2026)

- Satu root workspace berisi `android-app/`, `backend-api/`, `admin-web/`, dan
  `docs/`. Proyek Android **sudah** berada di `android-app/`, bukan di root.
- Root workspace **bukan** repository Git (`.git` tidak tersedia). Riwayat versi,
  branch, dan remote tidak dapat diperiksa. Jangan menginisialisasi Git atau
  membuat repository baru tanpa keputusan pemilik proyek.
- `backend-api/`: Laravel 13.39.0 dengan PHP 8.4.24 dan Composer 2.10.3. Runtime
  lokal memakai **SQLite** (`DB_CONNECTION=sqlite`,
  `backend-api/database/database.sqlite`). Driver `mysql`/`mariadb` tersedia dan
  ekstensi `pdo_mysql` aktif, tetapi pemindahan data live ke MySQL/MariaDB
  **belum** dilakukan.
- `admin-web/`: React 18 + TypeScript 5.6 + Vite 5, dependency terpasang dari
  `package-lock.json`. Base URL API diambil dari `VITE_API_BASE_URL` (default
  `/api/v1`) dan proxy dev mengarah ke `http://127.0.0.1:8000`.
- Database MySQL/MariaDB lokal disediakan melalui `docker-compose.yml`
  (MariaDB 11.8, `127.0.0.1:3307`, named volume `pmb_mysql_data`). Karena Laravel
  berjalan di host, `DB_HOST=127.0.0.1` dan `DB_PORT` mengikuti port publikasi
  container. Nilai di dalam container berbeda: `localhost` menunjuk container itu
  sendiri.
- Dokumen privat tersimpan pada disk `local` di
  `backend-api/storage/app/private/documents/<application_id>/` dan hanya dapat
  diunduh melalui endpoint terotorisasi. Akses langsung ke `/storage/...`
  ditolak karena disk privat mensyaratkan signed URL.
- Backup dan verifikasi restore disediakan oleh `scripts/backup.sh` dan
  `scripts/restore-verify.sh`; lihat [[Backup & Recovery]].
- Android: Gradle wrapper 9.6.0, `compileSdk 37`, `minSdk 33`, `targetSdk 37`.
  `gradle/gradle-daemon-jvm.properties` meminta toolchain JDK 25. Base URL debug
  `http://10.0.2.2:8000/api/v1/` dan release memakai HTTPS. `adb devices` tidak
  menampilkan perangkat/emulator dan `sdkmanager` tidak ditemukan pada `PATH`,
  sehingga pengujian instrumented belum dapat dijalankan.
- `local.properties` menunjuk Android SDK lokal dan bersifat lokal; file ini tidak
  boleh ditambahkan ke Git.

Temuan di atas menjelaskan workspace yang tersedia, bukan persetujuan untuk membuat proyek komponen baru pada tahap berikutnya.


Base URL pada tabel berikut hanyalah contoh rancangan, bukan endpoint aktif yang telah diverifikasi. Website admin React + TypeScript harus mengonsumsi REST API Laravel; lihat keputusan stack dan catatan baseline pada `Repository Guide.md`.

Pada tahap awal, proyek menggunakan lingkungan lokal. Lingkungan testing dan production dapat disiapkan ketika kebutuhan deployment sudah ditetapkan.

**Prinsip utama:** database, kredensial, dan dokumen dari setiap lingkungan harus dipisahkan. Data pengujian tidak boleh tercampur dengan data pendaftaran sebenarnya.

## 4. Konfigurasi Backend Laravel

### 4.1 File Environment

Laravel menggunakan file `.env` untuk menyimpan konfigurasi lingkungan. File ini biasanya dibuat dari `.env.example`.

Contoh pada Linux:

```
cp .env.example .env
```

File `.env` hanya digunakan pada lingkungan yang sesuai dan tidak boleh dimasukkan ke repository publik.

### 4.2 Konfigurasi Dasar

Contoh konfigurasi lokal:

```
APP_NAME="PMB ITB Akiday"
APP_ENV=local
APP_KEY=
APP_DEBUG=true
APP_URL=http://127.0.0.1:8000
```

Penjelasan:

|Variabel|Fungsi|
|---|---|
|`APP_NAME`|Nama aplikasi|
|`APP_ENV`|Menentukan lingkungan aplikasi|
|`APP_KEY`|Kunci aplikasi Laravel|
|`APP_DEBUG`|Mengaktifkan atau menonaktifkan informasi debugging|
|`APP_URL`|URL utama backend|

Pada konfigurasi lokal, debugging dapat diaktifkan untuk membantu pengembangan. Pada deployment produksi, gunakan `APP_ENV=production` dan `APP_DEBUG=false`.

`APP_KEY` harus dibuat menggunakan mekanisme Laravel dan disimpan secara rahasia. Jangan menggunakan kunci contoh atau mengganti kunci pada sistem yang sudah berjalan tanpa memahami dampaknya.

### 4.3 Pembuatan Application Key

Jika aplikasi baru belum memiliki application key, jalankan:

```
php artisan key:generate
```

Pastikan perintah berhasil dan nilai `APP_KEY` tersimpan pada konfigurasi lingkungan.

Jangan membagikan nilai application key melalui screenshot, dokumentasi publik, atau repository.

## 5. Konfigurasi Database MySQL

### 5.1 Variabel Database

Contoh konfigurasi database lokal:

```
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=pmb_itb_akiday
DB_USERNAME=USER_DATABASE
DB_PASSWORD=PASSWORD_DATABASE
```

Nilai `USER_DATABASE` dan `PASSWORD_DATABASE` merupakan placeholder yang harus diganti dengan akun database lokal.

### 5.2 Ketentuan Keamanan Database

- Gunakan akun khusus aplikasi.
    
- Berikan hak akses minimum yang dibutuhkan.
    
- Hindari penggunaan akun root pada koneksi aplikasi sehari-hari.
    
- Jangan menyimpan kata sandi database langsung di source code.
    
- Jangan menggunakan kredensial production pada lingkungan testing.
    
- Batasi akses jaringan ke database.
    
- Pastikan backup database dibuat sebelum perubahan skema yang berisiko.
    

### 5.3 Pemeriksaan Koneksi

Setelah mengatur variabel database, lakukan pemeriksaan berikut:

1. Pastikan layanan MySQL berjalan.
    
2. Pastikan database yang dituju sudah dibuat.
    
3. Periksa nama pengguna dan kata sandi.
    
4. Jalankan migration pada database yang benar.
    
5. Jalankan backend dan periksa log jika koneksi gagal.
    

Jika konfigurasi `.env` berubah tetapi aplikasi masih menggunakan konfigurasi lama, periksa cache konfigurasi Laravel. Pada lingkungan lokal, perintah berikut dapat digunakan:

```
php artisan config:clear
```

Jalankan perintah tersebut dari direktori backend yang benar.

## 6. Konfigurasi Base URL API

API PMB ITB Akiday direncanakan menggunakan prefix `/api/v1`.

Alamat base URL berbeda berdasarkan perangkat yang mengakses backend.

|Lingkungan|Contoh Base URL|
|---|---|
|Backend diakses dari komputer yang sama|`http://127.0.0.1:8000/api/v1`|
|Android Emulator|`http://10.0.2.2:8000/api/v1`|
|Android fisik pada jaringan lokal|`http://ALAMAT_IP_KOMPUTER:8000/api/v1`|
|Production|`https://DOMAIN_API/api/v1`|

Base URL tersebut adalah contoh rancangan, bukan endpoint aktif yang telah diverifikasi.

**Catatan penting:** `127.0.0.1` pada Android Emulator mengarah ke emulator itu sendiri. Gunakan `10.0.2.2` untuk mengakses layanan pada komputer host melalui Android Emulator standar.

Untuk perangkat Android fisik, alamat IP komputer harus dapat dijangkau dari perangkat melalui jaringan yang sama. Firewall juga harus mengizinkan koneksi yang diperlukan.

### 6.1 Konfigurasi pada Android

Base URL sebaiknya dikelola melalui konfigurasi build atau mekanisme konfigurasi aplikasi yang sesuai, bukan disebarkan sebagai string berbeda di banyak file.

Contoh konseptual:

```
object ApiConfig {
    const val BASE_URL =
        "http://10.0.2.2:8000/api/v1/"
}
```

Contoh tersebut ditujukan untuk pengembangan pada Android Emulator. Untuk production, gunakan konfigurasi HTTPS yang sesuai.

Jangan menyimpan password, token, atau kunci rahasia di dalam `ApiConfig`.

## 7. Konfigurasi Penyimpanan Dokumen

Sistem menyimpan dokumen pendaftaran, seperti foto calon mahasiswa dan ijazah atau Surat Keterangan Lulus (SKL).

Database menyimpan metadata dokumen, sedangkan file disimpan pada media penyimpanan yang dikelola backend.

### 7.1 Ketentuan Penyimpanan

- Dokumen pendaftaran disimpan secara privat.
    
- Lokasi penyimpanan tidak boleh dapat diakses publik secara langsung.
    
- Akses file harus melewati autentikasi dan otorisasi backend.
    
- Backend harus memeriksa tipe file, ukuran, dan validitas unggahan.
    
- Nama file penyimpanan sebaiknya tidak bergantung langsung pada nama file yang dikirim pengguna.
    
- File dan metadata database harus dijaga konsistensinya.
    
- Dokumen harus termasuk dalam strategi backup.
    

### 7.2 Konfigurasi yang Perlu Ditentukan

|Pengaturan|Nilai|
|---|---|
|Disk penyimpanan dokumen|Ditentukan sesuai konfigurasi Laravel|
|Direktori dokumen|Direktori privat yang ditetapkan proyek|
|Format file yang diizinkan|Belum ditetapkan|
|Ukuran maksimum file|Belum ditetapkan|
|Masa retensi dokumen|Belum ditetapkan|
|Lokasi backup dokumen|Ditentukan pada [[Backup & Recovery]]|

Jangan menganggap direktori `public` aman untuk menyimpan ijazah atau dokumen pribadi. Penyimpanan harus dikonfigurasi sesuai kebutuhan keamanan sistem.

## 8. Konfigurasi Autentikasi dan Otorisasi

Sistem memiliki empat kelompok pengguna:

- `calon_mahasiswa`
    
- `admin`
    
- `panitia`
    
- `pimpinan`
    

Pengaturan autentikasi harus konsisten antara backend Laravel, website admin, dan aplikasi Android.

### 8.1 Ketentuan Konfigurasi

1. Pengguna harus terautentikasi sebelum mengakses endpoint privat.
    
2. Setiap endpoint sensitif harus memeriksa role dan kewenangan pengguna.
    
3. Calon mahasiswa hanya boleh mengakses profil, dokumen, dan pendaftaran miliknya sendiri.
    
4. Hak akses admin, panitia, dan pimpinan harus mengikuti Access Control Matrix.
    
5. Token atau sesi autentikasi harus dikelola sesuai mekanisme yang dipilih.
    
6. Endpoint logout harus membatalkan atau mengakhiri autentikasi sesuai mekanisme implementasi.
    

### 8.2 Pilihan Mekanisme Autentikasi

Laravel Sanctum merupakan salah satu pilihan untuk autentikasi API, tetapi mekanisme final harus mengikuti implementasi proyek. Jika Sanctum dipilih, konfigurasi token, middleware, masa berlaku token, dan pencabutan token harus ditetapkan dengan benar.

Jangan menambahkan variabel environment autentikasi yang belum digunakan oleh kode aplikasi. Semua pengaturan harus sesuai dengan implementasi sebenarnya.

## 9. Konfigurasi Email dan Layanan Eksternal

MVP PMB ITB Akiday belum mewajibkan verifikasi email. Oleh karena itu, layanan email eksternal tidak harus diaktifkan apabila tidak digunakan oleh fitur yang telah disepakati.

Jika nantinya aplikasi mengirim notifikasi email, konfigurasi mail server harus dipisahkan antara lingkungan lokal, testing, dan production.

|Pengaturan|Status|
|---|---|
|Verifikasi email saat registrasi|Tidak termasuk MVP|
|Notifikasi email|Belum ditetapkan|
|SMTP host dan port|Diisi jika layanan email digunakan|
|Kredensial SMTP|Disimpan sebagai rahasia|
|Lingkungan pengujian email|Disiapkan jika diperlukan|

Jangan memasukkan kredensial SMTP ke repository atau dokumentasi yang dapat diakses publik.

## 10. Konfigurasi Logging dan Error Handling

Logging digunakan untuk membantu pengembang mengidentifikasi kesalahan aplikasi dan gangguan operasional.

Pengaturan logging harus mengikuti kebutuhan masing-masing lingkungan.

|Aspek|Development|Production|
|---|---|---|
|Debugging|Dapat diaktifkan secara lokal|Dinonaktifkan|
|Detail error|Untuk pengembang|Tidak ditampilkan kepada pengguna|
|Log aplikasi|Digunakan saat pengembangan|Disimpan dan dilindungi|
|Informasi rahasia|Tidak boleh dicatat sembarangan|Tidak boleh dicatat sembarangan|
|Monitoring|Pemeriksaan manual|Sesuai kebutuhan operasional|

Contoh konfigurasi produksi:

```
APP_ENV=production
APP_DEBUG=false
```

Jangan mencatat password, token autentikasi aktif, kunci aplikasi, atau isi dokumen pribadi ke dalam log.

## 11. Konfigurasi Keamanan Jaringan

### 11.1 Lingkungan Lokal

Untuk pengembangan, backend dapat berjalan melalui HTTP pada jaringan lokal yang dikendalikan. Akses jaringan harus dibatasi dan tidak diekspos ke internet secara langsung.

### 11.2 Lingkungan Produksi

Untuk production:

- Gunakan HTTPS.
    
- Pastikan sertifikat TLS valid.
    
- Batasi akses ke port database.
    
- Gunakan firewall sesuai kebutuhan.
    
- Simpan rahasia di konfigurasi server yang terlindungi.
    
- Jangan aktifkan debugging untuk pengguna akhir.
    
- Terapkan pembatasan permintaan pada endpoint sensitif.
    
- Pastikan proses unggah dan unduh dokumen melalui otorisasi backend.
    

HTTP lokal hanya untuk pengembangan yang terkendali, bukan pengganti HTTPS pada deployment publik.

## 12. Pemisahan Konfigurasi Antarlingkungan

Konfigurasi setiap lingkungan harus dipisahkan agar pengujian tidak memengaruhi data pengguna sebenarnya.

|Variabel|Local|Testing|Production|
|---|---|---|---|
|`APP_ENV`|`local`|Sesuai konfigurasi testing|`production`|
|`APP_DEBUG`|`true` jika diperlukan|Disesuaikan dengan runner pengujian|`false`|
|Database|Database lokal|Database pengujian terpisah|Database production|
|API URL|Host lokal|Host pengujian|Domain HTTPS|
|Dokumen|Penyimpanan lokal privat|Dokumen dummy|Penyimpanan privat|
|Kredensial|Kredensial lokal|Kredensial khusus testing|Rahasia production|
|Backup|Sesuai kebutuhan pengembangan|Sesuai kebutuhan pengujian|Terjadwal dan diverifikasi|

Nilai pada tabel merupakan pola konfigurasi yang disarankan. Implementasi dapat menggunakan file `.env` terpisah, konfigurasi build, atau pengelolaan secret sesuai teknologi yang digunakan.

## 13. Pengelolaan File Konfigurasi dan Rahasia

Ketentuan pengelolaan konfigurasi:

1. File `.env` tidak dimasukkan ke repository publik.
    
2. File `.env.example` hanya berisi nama variabel dan nilai contoh yang tidak rahasia.
    
3. Jangan menaruh password atau token nyata dalam contoh konfigurasi.
    
4. Akses ke rahasia production hanya diberikan kepada pihak yang berwenang.
    
5. Rotasi kredensial dilakukan jika rahasia diduga bocor.
    
6. Backup yang memuat konfigurasi rahasia harus dilindungi.
    
7. Jangan menyalin konfigurasi production ke lingkungan testing tanpa meninjau dan mengganti rahasia yang digunakan.
    

Contoh struktur `.env.example`:

```
APP_NAME="PMB ITB Akiday"
APP_ENV=local
APP_DEBUG=true
APP_URL=http://127.0.0.1:8000

DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=
DB_USERNAME=
DB_PASSWORD=
```

Nilai `APP_KEY` dapat dibiarkan kosong dalam template dan dibuat menggunakan perintah Laravel saat menyiapkan lingkungan. File contoh ini tidak menggantikan pemeriksaan keamanan konfigurasi aktual.

## 14. Validasi Konfigurasi Sebelum Menjalankan Sistem

Gunakan checklist berikut untuk memastikan pengaturan lingkungan sudah sesuai.

### Backend

- Versi PHP kompatibel dengan Laravel.
    
- Dependensi Composer berhasil dipasang.
    
- File `.env` tersedia.
    
- `APP_KEY` sudah dibuat.
    
- `APP_URL` sesuai lingkungan.
    
- Konfigurasi debugging sesuai lingkungan.
    

### Database

- MySQL berjalan.
    
- Nama database benar.
    
- Kredensial valid.
    
- Akun database memiliki hak akses minimum yang dibutuhkan.
    
- Migration berhasil dijalankan pada database yang benar.
    

### API dan Android

- Base URL sesuai perangkat yang mengakses API.
    
- Endpoint yang digunakan memang sudah diimplementasikan.
    
- Autentikasi dan otorisasi berjalan.
    
- Error jaringan ditangani dengan baik.
    
- Konfigurasi HTTPS digunakan untuk production.
    

### Penyimpanan dan Keamanan

- Direktori dokumen privat tersedia.
    
- Validasi file dilakukan di backend.
    
- Dokumen tidak tersedia melalui URL publik.
    
- Rahasia tidak disimpan di repository.
    
- Log tidak membocorkan data sensitif.
    
- Backup database dan dokumen mengikuti [[Backup & Recovery]].
    

## 15. Troubleshooting

|Masalah|Kemungkinan Penyebab|Penanganan|
|---|---|---|
|Konfigurasi tidak berubah|Cache konfigurasi Laravel|Periksa `.env` dan jalankan `php artisan config:clear` pada lingkungan lokal jika diperlukan|
|Database tidak dapat diakses|Host, port, atau kredensial salah|Periksa variabel database dan status MySQL|
|Application key belum tersedia|`APP_KEY` kosong|Jalankan `php artisan key:generate` untuk aplikasi baru|
|Android tidak dapat mengakses API|Base URL salah|Sesuaikan alamat dengan emulator atau perangkat fisik|
|Dokumen gagal disimpan|Direktori atau izin penyimpanan salah|Periksa konfigurasi disk dan izin direktori|
|Error sensitif muncul di halaman|Debugging aktif pada lingkungan yang tidak sesuai|Nonaktifkan debugging dan periksa log secara aman|
|Pengguna tidak dapat membuka endpoint|Token, sesi, atau hak akses tidak sesuai|Periksa middleware dan mekanisme autentikasi|

Periksa log aplikasi untuk menemukan penyebab masalah. Jangan menyalin log yang memuat kredensial atau informasi pribadi ke laporan yang dibagikan secara umum.

## 16. Keputusan yang Belum Ditetapkan

Sebelum konfigurasi dinyatakan final, tim perlu menetapkan:

- Versi PHP, Laravel, dan MySQL yang digunakan.
    
- Struktur repository backend dan website admin.
    
- Mekanisme autentikasi yang benar-benar dipakai.
    
- Konfigurasi penyimpanan privat dan batas unggah dokumen.
    
- Nama domain dan konfigurasi HTTPS jika production diterapkan.
    
- Kebijakan log dan masa retensi.
    
- Cara mengelola secret production.
    
- Jadwal backup dan target pemulihan.
    
- Apakah layanan email atau layanan eksternal lain akan digunakan.
    

## 17. Kesimpulan

Environment Configuration menjadi acuan untuk mengatur backend Laravel, database MySQL, base URL API, aplikasi Android, penyimpanan dokumen, dan pengamanan rahasia sistem PMB ITB Akiday. Pemisahan konfigurasi lokal, testing, dan production membantu mencegah kesalahan koneksi serta mengurangi risiko data pengujian tercampur dengan data sebenarnya.

Dokumen ini perlu diperbarui sesuai konfigurasi yang benar-benar digunakan. Sebelum sistem dipublikasikan, seluruh pengaturan keamanan, autentikasi, penyimpanan dokumen, dan koneksi database harus diverifikasi melalui pengujian.

---

## 18. Dokumen Terkait

- [[Deployment Guide]]
    
- [[Backup & Recovery]]
    
- [[Security Specification]]
    
- [[Access Control Matrix]]
    
- [[API Contract]]
    
- [[API Error Handling]]
    
- [[Database Model]]
    
- [[Migration Plan]]
    
- [[Test Plan]]
    
- [[Test Results]]