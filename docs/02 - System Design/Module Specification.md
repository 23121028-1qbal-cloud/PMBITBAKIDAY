---

title: Module Specification  
project: PMB ITB Akiday  
version: 1.0  
status: Baseline  
created: 2026-10-18  
tags:

- PMB
    
- system-design
    
- module-specification
    

---

# Module Specification — PMB ITB Akiday

## 1. Pendahuluan

### 1.1 Tujuan Dokumen

Module Specification merupakan dokumen yang menjelaskan pembagian modul dalam sistem PMB ITB Akiday. Setiap modul memiliki tanggung jawab, fungsi, masukan, keluaran, dan hubungan dengan modul lain yang jelas.

Dokumen ini menjadi acuan bagi proses implementasi backend Laravel, aplikasi Android menggunakan Kotlin, serta website admin. Pembagian modul diharapkan mempermudah pengembangan, pengujian, pemeliharaan, dan pelacakan kesalahan.

### 1.2 Ruang Lingkup

Spesifikasi modul mencakup:

- Modul autentikasi dan otorisasi.
    
- Modul profil calon mahasiswa.
    
- Modul gelombang penerimaan.
    
- Modul program studi.
    
- Modul pendaftaran.
    
- Modul dokumen.
    
- Modul pemeriksaan dan revisi.
    
- Modul keputusan seleksi.
    
- Modul riwayat status.
    
- Modul dashboard dan laporan.
    
- Modul administrasi pengguna.
    
- Modul validasi dan penanganan kesalahan.
    

### 1.3 Dokumen Acuan

- `PRD Final v1.0.md`
    
- `Project Overview.md`
    
- `Project Plan.md`
    
- `SRS.md`
    
- `Traceability Matrix.md`
    
- `System Design.md`
    
- `Workflow & Business Process.md`
    
- `Database Model.md`
    
- `API Contract.md`
    
- `Security Specification.md`
    

## 2. Gambaran Arsitektur Modul

Sistem menggunakan arsitektur client-server. Aplikasi Android dan website admin berkomunikasi dengan backend Laravel melalui REST API. Backend menjalankan aturan bisnis dan berinteraksi dengan MySQL serta penyimpanan dokumen privat.

```
flowchart TD
    A["Aplikasi Android<br/>Calon Mahasiswa"]
    W["Website Admin<br/>Admin, Panitia, Pimpinan"]
    API["Laravel REST API"]
    AUTH["Autentikasi & Otorisasi"]
    APP["Modul Pendaftaran"]
    DOC["Modul Dokumen"]
    REV["Modul Pemeriksaan & Revisi"]
    DEC["Modul Keputusan"]
    MASTER["Modul Data Utama"]
    REP["Modul Dashboard & Laporan"]
    HIST["Modul Riwayat Status"]
    DB[("MySQL")]
    FILE[("Penyimpanan Dokumen Privat")]

    A --> API
    W --> API
    API --> AUTH
    AUTH --> APP
    AUTH --> DOC
    AUTH --> REV
    AUTH --> DEC
    AUTH --> MASTER
    AUTH --> REP
    APP --> DB
    DOC --> DB
    DOC --> FILE
    REV --> DB
    DEC --> DB
    DEC --> HIST
    APP --> HIST
    MASTER --> DB
    REP --> DB
    HIST --> DB
```

Diagram tersebut menunjukkan hubungan konseptual antarmodul. Implementasi rinci, middleware, service, dan struktur folder akan ditetapkan dalam dokumen desain teknis serta standar coding.

## 3. Prinsip Pembagian Modul

Pembagian modul mengikuti prinsip berikut:

1. **Tanggung jawab jelas:** setiap modul berfokus pada satu kelompok fungsi.
    
2. **Validasi terpusat:** aturan bisnis dijalankan di backend, bukan hanya pada antarmuka.
    
3. **Akses terkontrol:** setiap operasi memeriksa autentikasi, peran, dan kepemilikan data yang relevan.
    
4. **Integritas data:** operasi yang mengubah data terkait harus menjaga konsistensi database.
    
5. **Riwayat dapat ditelusuri:** perubahan status penting dicatat dengan informasi pelaku dan waktu.
    
6. **Antarmuka terpisah:** Android dan website tidak mengakses database secara langsung.
    
7. **Mudah diuji:** setiap modul dapat diuji secara terpisah dan melalui pengujian integrasi.
    

## 4. Daftar Modul Utama

|ID|Nama Modul|Platform Utama|Prioritas|
|---|---|---|---|
|MOD-01|Autentikasi dan Otorisasi|Backend, Android, Website|P0|
|MOD-02|Profil Calon Mahasiswa|Backend, Android|P0|
|MOD-03|Gelombang Penerimaan|Backend, Website|P0|
|MOD-04|Program Studi|Backend, Website|P0|
|MOD-05|Pendaftaran|Backend, Android|P0|
|MOD-06|Dokumen Pendaftaran|Backend, Android, Website|P0|
|MOD-07|Pemeriksaan dan Revisi|Backend, Website, Android|P0|
|MOD-08|Keputusan Seleksi|Backend, Website|P0|
|MOD-09|Riwayat Status|Backend, Android, Website|P0|
|MOD-10|Dashboard dan Laporan|Backend, Website|P1|
|MOD-11|Administrasi Pengguna|Backend, Website|P1|
|MOD-12|Validasi dan Penanganan Kesalahan|Backend, Android, Website|P0|

Keterangan prioritas:

- `P0`: wajib untuk alur inti MVP.
    
- `P1`: dibutuhkan untuk melengkapi fungsi administrasi dan pelaporan MVP.
    

## 5. Spesifikasi Setiap Modul

### 5.1 MOD-01 — Autentikasi dan Otorisasi

**Tujuan:** mengelola identitas pengguna dan memastikan setiap pengguna hanya dapat menjalankan fungsi sesuai kewenangannya.

**Fungsi utama:**

- Registrasi akun calon mahasiswa.
    
- Login dan logout.
    
- Verifikasi kredensial.
    
- Pengelolaan sesi atau token autentikasi.
    
- Pemeriksaan peran pengguna.
    
- Pemeriksaan hak akses endpoint.
    

**Input:**

- Email.
    
- Kata sandi.
    
- Token atau informasi autentikasi sesuai mekanisme yang digunakan.
    

**Output:**

- Status autentikasi.
    
- Informasi akun yang boleh ditampilkan.
    
- Hasil pemeriksaan izin akses.
    

**Ketergantungan:**

- Database pengguna dan peran.
    
- Backend Laravel.
    
- Modul validasi.
    

**Aturan penting:**

- Email harus unik.
    
- Kata sandi disimpan menggunakan hashing yang sesuai.
    
- Endpoint terlindungi harus menolak permintaan tanpa autentikasi.
    
- Otorisasi harus dilakukan di backend.
    
- Hak akses tidak boleh ditentukan hanya berdasarkan informasi peran yang dikirim klien.
    

**Kriteria penerimaan:**

- Pengguna dengan kredensial valid dapat login.
    
- Kredensial yang salah ditolak.
    
- Pengguna yang logout tidak dapat melanjutkan akses dengan kredensial sesi yang telah dicabut atau kedaluwarsa.
    
- Pengguna tidak dapat menjalankan fungsi yang berada di luar kewenangannya.
    

### 5.2 MOD-02 — Profil Calon Mahasiswa

**Tujuan:** mengelola informasi pribadi calon mahasiswa yang dibutuhkan dalam proses pendaftaran.

**Fungsi utama:**

- Menampilkan profil.
    
- Menyimpan profil.
    
- Memperbarui data profil.
    
- Memvalidasi data pribadi.
    
- Menghubungkan profil dengan akun pengguna.
    

**Data utama:**

- Nama lengkap.
    
- NIK.
    
- Tempat lahir.
    
- Tanggal lahir.
    
- Jenis kelamin.
    
- Nomor telepon.
    
- Alamat.
    
- Foto.
    

**Ketergantungan:**

- MOD-01.
    
- Database profil.
    
- MOD-06 untuk foto apabila foto dikelola sebagai dokumen unggahan.
    

**Aturan penting:**

- Setiap profil terhubung dengan akun pemiliknya.
    
- Calon mahasiswa hanya dapat mengakses dan memperbarui profil sendiri.
    
- Kebijakan keunikan NIK perlu ditetapkan sebelum implementasi final.
    
- Data wajib dan formatnya diperiksa di backend.
    

**Kriteria penerimaan:**

- Profil dengan data valid dapat disimpan.
    
- Input yang tidak memenuhi aturan ditolak dengan pesan yang sesuai.
    
- Akses ke profil pengguna lain ditolak.
    

### 5.3 MOD-03 — Gelombang Penerimaan

**Tujuan:** mengelola periode pendaftaran dan menentukan gelombang yang dapat dipilih calon mahasiswa.

**Fungsi utama:**

- Membuat gelombang.
    
- Menampilkan daftar gelombang.
    
- Memperbarui informasi gelombang.
    
- Mengaktifkan atau menonaktifkan gelombang sesuai kewenangan.
    
- Memeriksa periode pendaftaran.
    

**Data utama:**

- Nama gelombang.
    
- Tanggal mulai.
    
- Tanggal berakhir.
    
- Status aktif.
    

**Ketergantungan:**

- MOD-01.
    
- MOD-05.
    
- Database gelombang.
    

**Aturan penting:**

- Pengelolaan data gelombang dibatasi untuk pengguna berwenang.
    
- Backend harus memeriksa kelayakan gelombang ketika pendaftaran dibuat atau dikirim.
    
- Penutupan gelombang tidak boleh menghapus riwayat pendaftaran yang sudah ada.
    
- Zona waktu dan aturan batas waktu perlu ditetapkan secara konsisten.
    

**Kriteria penerimaan:**

- Calon mahasiswa dapat melihat gelombang yang tersedia.
    
- Pendaftaran baru ditolak apabila gelombang tidak memenuhi ketentuan.
    
- Data pendaftaran lama tetap tersimpan setelah gelombang ditutup.
    

### 5.4 MOD-04 — Program Studi

**Tujuan:** menyediakan daftar program studi yang dapat dipilih pada formulir pendaftaran.

**Fungsi utama:**

- Membuat data program studi.
    
- Menampilkan daftar program studi.
    
- Memperbarui informasi program studi.
    
- Mengaktifkan atau menonaktifkan pilihan program studi.
    

**Data utama:**

- Kode program studi.
    
- Nama program studi.
    
- Status aktif.
    

**Data prototipe:**

- Informatika.
    
- Sistem Informasi.
    
- Teknik Industri.
    
- Manajemen.
    

Daftar tersebut merupakan data prototipe proyek dan harus dikonfirmasi sebelum digunakan sebagai daftar resmi institusi.

**Ketergantungan:**

- MOD-01.
    
- MOD-05.
    
- Database program studi.
    

**Aturan penting:**

- Program studi yang tidak aktif tidak dapat dipilih untuk pendaftaran baru.
    
- Satu pendaftaran hanya memiliki satu program studi.
    
- Perubahan status program studi tidak boleh menghilangkan data pilihan dari pendaftaran lama.
    

**Kriteria penerimaan:**

- Daftar program studi ditampilkan dari backend.
    
- Program studi tidak aktif tidak dapat dipilih untuk pendaftaran baru.
    
- Pilihan program studi tersimpan dengan relasi yang valid.
    

### 5.5 MOD-05 — Pendaftaran

**Tujuan:** menangani proses pembuatan, penyimpanan, pengiriman, dan pemantauan data pendaftaran.

**Fungsi utama:**

- Membuat pendaftaran.
    
- Menyimpan formulir sebagai draft.
    
- Memperbarui data yang masih boleh diubah.
    
- Memvalidasi pemilik, gelombang, dan program studi.
    
- Mengirim formulir.
    
- Menghasilkan nomor registrasi.
    
- Menampilkan detail pendaftaran.
    

**Input:**

- Profil calon mahasiswa.
    
- Gelombang penerimaan.
    
- Program studi pilihan.
    
- Data formulir dan dokumen yang dipersyaratkan.
    

**Output:**

- Data pendaftaran.
    
- Nomor registrasi.
    
- Status pendaftaran.
    
- Hasil validasi.
    

**Ketergantungan:**

- MOD-01.
    
- MOD-02.
    
- MOD-03.
    
- MOD-04.
    
- MOD-06.
    
- MOD-09.
    

**Aturan penting:**

- Satu akun hanya dapat memiliki satu pendaftaran pada setiap gelombang.
    
- Akun yang sama dapat mendaftar di gelombang berbeda jika aturan penerimaan mengizinkan.
    
- Database harus menerapkan unique constraint pada kombinasi pemilik pendaftaran dan gelombang.
    
- Pemeriksaan duplikasi harus aman terhadap permintaan bersamaan.
    
- Pengiriman hanya dapat dilakukan setelah validasi berhasil.
    
- Nomor registrasi harus unik.
    
- Perubahan data dibatasi sesuai status pendaftaran.
    

**Kriteria penerimaan:**

- Calon mahasiswa dapat membuat dan menyimpan draft.
    
- Pendaftaran ganda pada gelombang yang sama ditolak.
    
- Pendaftaran pada gelombang lain dapat dibuat jika diizinkan.
    
- Formulir valid dapat dikirim.
    
- Formulir yang tidak lengkap tidak dapat dikirim.
    

### 5.6 MOD-06 — Dokumen Pendaftaran

**Tujuan:** mengelola pengunggahan, penyimpanan, pemeriksaan, dan akses dokumen persyaratan.

**Fungsi utama:**

- Mengunggah foto.
    
- Mengunggah ijazah atau SKL.
    
- Memvalidasi format dan ukuran.
    
- Menyimpan metadata dokumen.
    
- Menyediakan akses kepada pemilik dan petugas berwenang.
    
- Mengganti dokumen apabila revisi diizinkan.
    

**Data utama:**

- Pendaftaran terkait.
    
- Jenis dokumen.
    
- Lokasi berkas.
    
- Tipe media.
    
- Ukuran berkas.
    
- Waktu unggah.
    

**Ketergantungan:**

- MOD-01.
    
- MOD-05.
    
- MOD-07.
    
- Penyimpanan dokumen privat.
    

**Aturan penting:**

- Berkas harus disimpan di lokasi privat.
    
- Nama berkas dan jalur penyimpanan tidak boleh menjadi satu-satunya mekanisme keamanan.
    
- Backend harus memeriksa izin setiap kali dokumen diakses.
    
- Format, ukuran maksimum, dan kebijakan penggantian berkas harus ditetapkan.
    
- Metadata database dan operasi penyimpanan berkas harus ditangani agar kegagalan tidak meninggalkan data yang tidak konsisten.
    

**Kriteria penerimaan:**

- Berkas yang memenuhi ketentuan dapat diunggah.
    
- Berkas yang tidak memenuhi ketentuan ditolak.
    
- Dokumen dapat dilihat oleh pengguna yang berwenang.
    
- Pengguna tanpa izin tidak dapat mengakses dokumen.
    

### 5.7 MOD-07 — Pemeriksaan dan Revisi

**Tujuan:** memfasilitasi pemeriksaan data pendaftaran dan permintaan perbaikan oleh panitia.

**Fungsi utama:**

- Menampilkan antrean pendaftaran yang perlu diperiksa.
    
- Membuka detail data dan dokumen.
    
- Memeriksa kelengkapan.
    
- Memberikan catatan pemeriksaan.
    
- Meminta revisi.
    
- Memproses pengiriman ulang setelah revisi.
    

**Ketergantungan:**

- MOD-01.
    
- MOD-05.
    
- MOD-06.
    
- MOD-09.
    

**Aturan penting:**

- Hanya panitia yang berwenang dapat memeriksa pendaftaran.
    
- Permintaan revisi harus memiliki catatan yang dapat dipahami calon mahasiswa.
    
- Status hanya dapat berubah melalui transisi yang diizinkan.
    
- Setelah revisi dikirim ulang dan lolos validasi, pendaftaran kembali mengikuti alur pemeriksaan.
    
- Batas waktu dan jumlah revisi menunggu keputusan bisnis.
    

**Kriteria penerimaan:**

- Panitia dapat membuka pendaftaran yang menjadi kewenangannya.
    
- Panitia dapat meminta revisi dengan alasan.
    
- Calon mahasiswa dapat melihat alasan revisi.
    
- Riwayat perubahan status tercatat.
    

### 5.8 MOD-08 — Keputusan Seleksi

**Tujuan:** mengelola penetapan keputusan akhir pendaftaran oleh panitia yang berwenang.

**Fungsi utama:**

- Menampilkan pendaftaran yang siap diputuskan.
    
- Menetapkan keputusan diterima.
    
- Menetapkan keputusan ditolak.
    
- Menyimpan catatan keputusan.
    
- Mencatat pelaku dan waktu keputusan.
    

**Ketergantungan:**

- MOD-01.
    
- MOD-05.
    
- MOD-07.
    
- MOD-09.
    

**Aturan penting:**

- Keputusan akhir hanya dapat dibuat oleh peran yang berwenang.
    
- Keputusan dilakukan setelah pemeriksaan sesuai proses bisnis.
    
- Sistem tidak menggunakan validasi otomatis untuk memutuskan kelulusan akademik.
    
- Status akhir tidak dapat diubah kembali tanpa kebijakan resmi dan mekanisme otorisasi yang ditetapkan.
    

**Kriteria penerimaan:**

- Panitia berwenang dapat menetapkan `accepted` atau `rejected` pada kondisi yang diizinkan.
    
- Pengguna tanpa kewenangan tidak dapat membuat keputusan.
    
- Keputusan dan riwayatnya tersimpan secara konsisten.
    

### 5.9 MOD-09 — Riwayat Status

**Tujuan:** menyimpan catatan perubahan status pendaftaran agar prosesnya dapat ditelusuri.

**Fungsi utama:**

- Mencatat status awal dan status tujuan.
    
- Mencatat waktu perubahan.
    
- Mencatat pengguna yang melakukan perubahan jika tersedia.
    
- Menyimpan catatan terkait perubahan.
    
- Menampilkan riwayat sesuai hak akses.
    

**Ketergantungan:**

- MOD-05.
    
- MOD-07.
    
- MOD-08.
    
- Database riwayat status.
    

**Aturan penting:**

- Perubahan status dan pencatatan riwayat harus berada dalam transaksi database yang sesuai.
    
- Calon mahasiswa tidak boleh mengubah riwayat.
    
- Riwayat harus mencerminkan perubahan yang benar-benar berhasil.
    
- Tindakan sistem harus dapat dibedakan dari tindakan pengguna apabila diperlukan.
    

**Kriteria penerimaan:**

- Perubahan status berhasil menghasilkan catatan riwayat.
    
- Jika transaksi perubahan status gagal, sistem tidak meninggalkan riwayat sukses yang tidak sesuai.
    
- Riwayat dapat dilihat oleh pengguna yang memiliki izin.
    

### 5.10 MOD-10 — Dashboard dan Laporan

**Tujuan:** menyediakan ringkasan data penerimaan dan laporan bagi pengguna berwenang.

**Fungsi utama:**

- Menampilkan jumlah pendaftar.
    
- Mengelompokkan pendaftaran berdasarkan status.
    
- Mengelompokkan pendaftaran berdasarkan program studi.
    
- Menyediakan laporan PDF.
    
- Memproses filter laporan apabila ditetapkan dalam desain.
    

**Ketergantungan:**

- MOD-01.
    
- MOD-05.
    
- MOD-09.
    
- Database pendaftaran.
    

**Aturan penting:**

- Statistik harus berasal dari data yang tersimpan.
    
- Hak akses laporan harus diperiksa di backend.
    
- Data pribadi hanya disertakan jika diperlukan dan diizinkan.
    
- Perhitungan statistik harus konsisten dengan definisi status dalam sistem.
    

**Kriteria penerimaan:**

- Pengguna berwenang dapat melihat statistik.
    
- Pengguna dapat menghasilkan laporan yang diizinkan.
    
- Laporan tidak memuat data yang tidak boleh diakses pengguna tersebut.
    

### 5.11 MOD-11 — Administrasi Pengguna

**Tujuan:** menyediakan pengelolaan akun dan status pengguna oleh admin yang berwenang.

**Fungsi utama:**

- Melihat daftar akun.
    
- Melihat detail akun yang diperlukan untuk administrasi.
    
- Mengaktifkan atau menonaktifkan akun sesuai kebijakan.
    
- Mengelola penetapan peran sesuai kewenangan.
    

**Ketergantungan:**

- MOD-01.
    
- Database pengguna dan peran.
    

**Aturan penting:**

- Hanya admin yang memiliki izin sesuai kebijakan dapat mengelola akun.
    
- Pengelolaan peran harus dilindungi karena dapat mengubah hak akses.
    
- Akun tidak boleh memperoleh hak istimewa hanya melalui manipulasi request dari klien.
    
- Tindakan administrasi penting perlu dicatat sesuai kebutuhan audit.
    

**Kriteria penerimaan:**

- Admin berwenang dapat menjalankan fungsi administrasi.
    
- Pengguna biasa tidak dapat mengubah peran sendiri.
    
- Akun nonaktif tidak dapat melakukan login atau tindakan yang dibatasi oleh kebijakan sistem.
    

### 5.12 MOD-12 — Validasi dan Penanganan Kesalahan

**Tujuan:** memastikan input, respons API, dan kegagalan operasi ditangani secara konsisten.

**Fungsi utama:**

- Memvalidasi input.
    
- Memeriksa format dan kelengkapan.
    
- Mengembalikan kode status HTTP yang sesuai.
    
- Menyediakan pesan kesalahan yang dapat dipahami.
    
- Mencatat kesalahan teknis tanpa membocorkan informasi sensitif.
    

**Ketergantungan:**

- Seluruh modul backend.
    
- API Contract.
    
- Security Specification.
    

**Aturan penting:**

- Validasi bisnis dijalankan di backend.
    
- Pesan kesalahan tidak boleh mengungkap kata sandi, token, atau detail internal sensitif.
    
- Kegagalan database tidak boleh dilaporkan sebagai keberhasilan.
    
- Operasi yang memengaruhi beberapa data terkait harus mempertimbangkan transaksi dan pemulihan kegagalan.
    

**Kriteria penerimaan:**

- Input tidak valid menghasilkan respons kesalahan yang konsisten.
    
- Kesalahan tidak menyebabkan perubahan data parsial yang tidak diinginkan.
    
- Pesan yang ditampilkan kepada pengguna tidak memuat detail teknis sensitif.
    

## 6. Pemetaan Modul ke Tabel Database

Pemetaan berikut mengacu pada model awal database yang terdiri dari delapan tabel utama.

|Modul|Tabel Utama|Keterangan|
|---|---|---|
|MOD-01 Autentikasi dan Otorisasi|`users`, `roles`|Akun dan peran pengguna|
|MOD-02 Profil Calon Mahasiswa|`applicant_profiles`|Informasi profil|
|MOD-03 Gelombang Penerimaan|`admission_waves`|Periode pendaftaran|
|MOD-04 Program Studi|`study_programs`|Pilihan program studi|
|MOD-05 Pendaftaran|`applications`|Data inti pendaftaran|
|MOD-06 Dokumen Pendaftaran|`application_documents`|Metadata dokumen|
|MOD-07 Pemeriksaan dan Revisi|`applications`, `application_status_histories`|Status pemeriksaan dan catatan perubahan|
|MOD-08 Keputusan Seleksi|`applications`, `application_status_histories`|Keputusan akhir dan riwayat|
|MOD-09 Riwayat Status|`application_status_histories`|Perubahan status|
|MOD-10 Dashboard dan Laporan|`applications`, `admission_waves`, `study_programs`|Sumber agregasi laporan|
|MOD-11 Administrasi Pengguna|`users`, `roles`|Pengelolaan akun dan peran|
|MOD-12 Validasi dan Penanganan Kesalahan|Bergantung kebutuhan|Validasi dan penanganan operasi lintas modul|

Pemetaan ini bukan berarti setiap modul harus memiliki tabel khusus. Beberapa modul menggunakan tabel yang sama, sementara validasi dan penanganan kesalahan berlaku lintas modul.

## 7. Pemetaan Modul ke API

Nama endpoint di bawah merupakan gambaran awal. Kontrak final, parameter, bentuk response, dan kode error harus ditetapkan pada `API Contract.md`.

|Modul|Endpoint Contoh|
|---|---|
|MOD-01|`POST /api/register`, `POST /api/login`, `POST /api/logout`|
|MOD-02|`GET /api/profile`, `PUT /api/profile`|
|MOD-03|`GET /api/waves`, endpoint admin untuk mengelola gelombang|
|MOD-04|`GET /api/study-programs`, endpoint admin untuk mengelola program studi|
|MOD-05|`POST /api/applications`, `GET /api/applications`, `PUT /api/applications/{id}`, `POST /api/applications/{id}/submit`|
|MOD-06|`POST /api/applications/{id}/documents`, endpoint berizin untuk mengambil dokumen|
|MOD-07|Endpoint admin untuk daftar pemeriksaan dan permintaan revisi|
|MOD-08|`POST /api/admin/applications/{id}/decision`|
|MOD-09|`GET /api/applications/{id}/history`|
|MOD-10|`GET /api/admin/reports` dan endpoint ekspor PDF yang ditetapkan|
|MOD-11|Endpoint admin untuk mengelola pengguna|
|MOD-12|Validasi dan format error diterapkan pada seluruh endpoint|

Semua endpoint harus memeriksa autentikasi, otorisasi, dan validasi input sesuai kebutuhan.

## 8. Pola Implementasi Backend

Backend Laravel disarankan menggunakan pembagian tanggung jawab berikut:

- **Routes:** mendefinisikan endpoint.
    
- **Controllers:** menerima request dan mengembalikan response.
    
- **Form Requests atau validator:** memvalidasi input.
    
- **Policies atau middleware:** memeriksa izin akses.
    
- **Services:** menangani aturan bisnis yang memerlukan koordinasi beberapa operasi.
    
- **Models:** merepresentasikan data dan relasi database.
    
- **Database transactions:** menjaga konsistensi pada operasi yang mengubah beberapa data terkait.
    
- **Resources atau response formatter:** menjaga konsistensi format response API.
    

Contoh struktur direktori konseptual:

```
app/
├── Http/
│   ├── Controllers/
│   │   ├── AuthController.php
│   │   ├── ProfileController.php
│   │   ├── ApplicationController.php
│   │   ├── DocumentController.php
│   │   ├── Admin/
│   │   │   ├── WaveController.php
│   │   │   ├── StudyProgramController.php
│   │   │   ├── ReviewController.php
│   │   │   ├── DecisionController.php
│   │   │   └── ReportController.php
│   ├── Requests/
│   └── Resources/
├── Models/
├── Policies/
└── Services/
```

Struktur tersebut merupakan rekomendasi awal, bukan struktur wajib. Penyesuaian dapat dilakukan berdasarkan versi Laravel dan kompleksitas implementasi.

## 9. Pola Implementasi Android

Aplikasi Android menggunakan Kotlin. Antarmuka dipisahkan dari proses komunikasi API dan pengelolaan data.

Pembagian yang disarankan:

- **UI / Presentation:** layar, komponen formulir, dan tampilan status.
    
- **ViewModel:** mengelola state tampilan dan tindakan pengguna.
    
- **Repository:** menjadi perantara antara UI dan sumber data.
    
- **API Service:** menangani komunikasi HTTP dengan backend.
    
- **Data Models:** merepresentasikan request dan response API.
    
- **Authentication Manager:** mengelola informasi autentikasi sesuai mekanisme yang dipilih.
    

Contoh struktur direktori konseptual:

```
app/src/main/java/
└── [package]/
    ├── data/
    │   ├── model/
    │   ├── remote/
    │   └── repository/
    ├── ui/
    │   ├── auth/
    │   ├── profile/
    │   ├── application/
    │   ├── documents/
    │   └── status/
    └── core/
```

Nama package, library jaringan, dan pendekatan navigasi akan ditentukan dalam dokumen desain teknis Android.

## 10. Pola Implementasi Website Admin

Website Admin dibagi menjadi beberapa area antarmuka:

- Autentikasi pengelola.
    
- Dashboard.
    
- Pengelolaan akun.
    
- Pengelolaan gelombang.
    
- Pengelolaan program studi.
    
- Daftar dan detail pendaftaran.
    
- Pemeriksaan dokumen.
    
- Permintaan revisi.
    
- Penetapan keputusan.
    
- Laporan.
    

Semua tindakan administratif tetap harus diverifikasi backend. Menyembunyikan tombol berdasarkan peran pada website bukan pengganti pemeriksaan otorisasi di server.

Teknologi frontend website dapat ditetapkan pada `System Design.md` jika belum diputuskan.

## 11. Hubungan Antar Modul

Urutan proses inti adalah:

1. MOD-01 mengautentikasi calon mahasiswa.
    
2. MOD-02 menyediakan data profil.
    
3. MOD-03 dan MOD-04 menyediakan gelombang dan program studi.
    
4. MOD-05 membuat dan menyimpan pendaftaran.
    
5. MOD-06 mengelola dokumen yang diperlukan.
    
6. MOD-05 memvalidasi dan mengirim pendaftaran.
    
7. MOD-09 mencatat perubahan status.
    
8. MOD-07 memungkinkan panitia memeriksa pendaftaran dan meminta revisi.
    
9. MOD-08 menangani keputusan akhir oleh panitia berwenang.
    
10. MOD-10 menyajikan ringkasan dan laporan.
    
11. MOD-11 mengelola akun dan peran sesuai kewenangan.
    
12. MOD-12 menyediakan validasi dan penanganan kesalahan lintas modul.
    

## 12. Persyaratan Pengujian Modul

|Modul|Pengujian Minimum|
|---|---|
|MOD-01|Registrasi, login, logout, akses tanpa autentikasi, dan pembatasan peran|
|MOD-02|Penyimpanan profil, validasi input, dan pembatasan kepemilikan|
|MOD-03|Pemilihan gelombang dan penolakan gelombang yang tidak memenuhi syarat|
|MOD-04|Daftar program studi aktif dan penolakan pilihan tidak aktif|
|MOD-05|Pembuatan draft, pengiriman, pendaftaran ganda, dan permintaan bersamaan|
|MOD-06|Unggah valid, unggah tidak valid, dan akses dokumen tanpa izin|
|MOD-07|Pemeriksaan dan permintaan revisi|
|MOD-08|Keputusan akhir, pembatasan peran, dan larangan transisi yang tidak diizinkan|
|MOD-09|Kelengkapan riwayat dan konsistensi transaksi|
|MOD-10|Akurasi statistik, ekspor PDF, dan hak akses laporan|
|MOD-11|Pengelolaan akun dan pembatasan pengubahan peran|
|MOD-12|Validasi request, format error, dan penanganan kegagalan operasi|

Skenario terperinci dan hasil aktual dicatat pada `Test Cases.md` serta `Test Results.md`.

## 13. Keputusan yang Masih Terbuka

|ID|Keputusan|Status|
|---|---|---|
|OD-01|Framework frontend Website Admin|Belum ditetapkan|
|OD-02|Mekanisme autentikasi dan token API|Perlu dipastikan dalam System Design|
|OD-03|Format dan ukuran maksimum dokumen|Belum ditetapkan|
|OD-04|Batas waktu dan jumlah revisi|Belum ditetapkan|
|OD-05|Kebijakan perubahan keputusan akhir|Belum ditetapkan|
|OD-06|Kebijakan keunikan NIK|Belum ditetapkan|
|OD-07|Detail audit aktivitas selain riwayat status|Perlu ditetapkan|
|OD-08|Library dan struktur navigasi Android|Belum ditetapkan|

Keputusan terbuka harus ditinjau sebelum modul terkait diselesaikan. Keputusan teknis tidak boleh mengubah aturan bisnis yang sudah disepakati tanpa proses peninjauan kebutuhan.

## 14. Kriteria Penyelesaian Dokumen

Module Specification dianggap siap menjadi acuan implementasi apabila:

- Semua modul inti telah diidentifikasi.
    
- Setiap modul memiliki tanggung jawab yang jelas.
    
- Ketergantungan antarmodul telah dijelaskan.
    
- Data dan endpoint terkait telah dipetakan.
    
- Aturan bisnis utama telah dicantumkan.
    
- Kebutuhan pengujian tiap modul telah ditentukan.
    
- Keputusan terbuka dicatat untuk tindak lanjut.
    

## 15. Riwayat Perubahan

|Versi|Tanggal|Perubahan|
|---|---|---|
|1.0|2026-10-18|Pembuatan baseline Module Specification|

---

**Dokumen acuan:** `PRD Final v1.0.md`, `SRS.md`, dan `Traceability Matrix.md`.

**Dokumen terkait:** `System Design.md`, `Workflow & Business Process.md`, `Database Model.md`, `API Contract.md`, `Security Specification.md`, dan `Test Cases.md`.