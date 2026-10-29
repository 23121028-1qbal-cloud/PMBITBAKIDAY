---

title: Project Overview  
project: PMB ITB Akiday  
version: 1.0  
status: Baseline  
created: 2026-10-16  
tags:

- PMB
    
- project-overview
    
- software-engineering
    

---

# Project Overview — PMB ITB Akiday

## 1. Informasi Proyek

|Informasi|Keterangan|
|---|---|
|Nama Proyek|Sistem Penerimaan Mahasiswa Baru ITB Akiday|
|Singkatan|PMB ITB Akiday|
|Versi Dokumen|1.0|
|Status Proyek|Perencanaan dan perancangan sistem|
|Platform|Android dan Website Admin|
|Backend|Laravel REST API|
|Database|MySQL|
|Bahasa Android|Kotlin|
|Format Komunikasi|JSON melalui REST API|
|Lingkungan Pengembangan|Lokal|
|Dokumen Acuan Utama|PRD Final v1.0|

## 2. Latar Belakang

Proses penerimaan mahasiswa baru membutuhkan sistem yang dapat mengelola data pendaftar, dokumen persyaratan, program studi yang dipilih, serta hasil seleksi secara terstruktur. Jika proses tersebut dilakukan secara terpisah atau masih bergantung pada pencatatan manual, pengelolaan data berpotensi menjadi kurang efisien, sulit dipantau, dan meningkatkan risiko kesalahan administrasi.

Oleh karena itu, proyek PMB ITB Akiday dirancang untuk menyediakan sistem penerimaan mahasiswa baru yang terintegrasi melalui aplikasi Android untuk calon mahasiswa dan website untuk pengelola penerimaan. Sistem ini diharapkan membantu proses pendaftaran, pemeriksaan dokumen, pemantauan status, pengambilan keputusan oleh panitia, dan penyusunan laporan penerimaan.

Nama institusi dan daftar program studi dalam dokumen ini mengikuti konteks proyek. Daftar program studi yang digunakan pada tahap prototipe belum dianggap sebagai daftar resmi institusi.

## 3. Tujuan Proyek

Tujuan pengembangan sistem PMB ITB Akiday adalah:

1. Mempermudah calon mahasiswa melakukan pendaftaran secara online.
    
2. Memfasilitasi pengunggahan dokumen persyaratan pendaftaran.
    
3. Memungkinkan calon mahasiswa memantau status dan riwayat pendaftarannya.
    
4. Membantu panitia memeriksa data, memvalidasi dokumen, meminta perbaikan, dan menetapkan hasil seleksi.
    
5. Membantu admin mengelola akun, gelombang penerimaan, program studi, dan data pendaftaran.
    
6. Menyediakan dashboard dan laporan penerimaan untuk pimpinan.
    
7. Menjaga konsistensi, keamanan, dan keteraturan data pendaftaran.
    

## 4. Ruang Lingkup Sistem

### 4.1 Fitur yang Termasuk dalam MVP

Versi awal sistem mencakup:

- Registrasi akun dan login calon mahasiswa.
    
- Pengelolaan profil calon mahasiswa.
    
- Pemilihan gelombang penerimaan dan program studi.
    
- Pengisian formulir pendaftaran.
    
- Pengunggahan foto dan ijazah atau Surat Keterangan Lulus (SKL).
    
- Validasi otomatis terhadap kelengkapan data, format, dan dokumen.
    
- Pengiriman formulir pendaftaran.
    
- Pemantauan status dan riwayat pendaftaran.
    
- Pemeriksaan dan pengelolaan pendaftaran oleh panitia.
    
- Permintaan revisi dokumen atau data.
    
- Penetapan keputusan akhir diterima atau ditolak oleh panitia yang berwenang.
    
- Pengelolaan akun, gelombang, dan program studi oleh admin.
    
- Dashboard statistik dan laporan PDF untuk pimpinan atau pengguna yang memiliki izin.
    

### 4.2 Hal di Luar Ruang Lingkup MVP

Fitur berikut belum menjadi bagian dari versi awal, kecuali ditetapkan melalui perubahan kebutuhan:

- Pembayaran biaya pendaftaran online karena pendaftaran pada MVP bersifat gratis.
    
- Seleksi akademik otomatis menggunakan kecerdasan buatan.
    
- Integrasi dengan sistem akademik eksternal.
    
- Pengiriman notifikasi melalui SMS atau WhatsApp.
    
- Deployment produksi yang dapat diakses secara publik.
    
- Integrasi dengan layanan pihak ketiga yang belum ditetapkan.
    

## 5. Pengguna dan Peran

|Peran|Tanggung Jawab Utama|
|---|---|
|Calon Mahasiswa|Membuat akun, mengisi formulir, mengunggah dokumen, dan memantau status pendaftaran sendiri.|
|Admin|Mengelola akun, data gelombang, program studi, serta konfigurasi dan kebutuhan administrasi sistem.|
|Panitia|Memeriksa pendaftaran, memvalidasi dokumen, meminta revisi, dan menetapkan hasil seleksi sesuai kewenangan.|
|Pimpinan|Melihat dashboard statistik dan laporan penerimaan sesuai hak akses.|

Setiap pengguna hanya boleh menjalankan fungsi yang sesuai dengan perannya. Akses ke data dan tindakan penting harus dikendalikan melalui mekanisme Role-Based Access Control (RBAC).

## 6. Gambaran Arsitektur

Sistem menggunakan arsitektur client-server yang memisahkan antarmuka pengguna, logika aplikasi, dan penyimpanan data.

**Komponen utama:**

1. **Aplikasi Android:** digunakan calon mahasiswa untuk melakukan pendaftaran dan memantau status.
    
2. **Website Admin:** digunakan admin, panitia, dan pimpinan sesuai hak akses.
    
3. **Backend Laravel REST API:** menangani autentikasi, validasi, aturan bisnis, pemrosesan pendaftaran, dan komunikasi dengan database.
    
4. **Database MySQL:** menyimpan akun, profil, gelombang, program studi, pendaftaran, dokumen, dan riwayat status.
    
5. **Penyimpanan dokumen privat:** menyimpan berkas persyaratan dengan akses yang dikendalikan backend.
    

Aplikasi Android dan website tidak mengakses MySQL secara langsung. Seluruh permintaan data dilakukan melalui backend API agar validasi dan kontrol akses diterapkan secara terpusat.

## 7. Aturan Bisnis Utama

1. Pendaftaran dilakukan secara online melalui sistem.
    
2. Pendaftaran pada MVP tidak dikenakan biaya.
    
3. Satu akun hanya boleh memiliki satu pendaftaran pada setiap gelombang penerimaan.
    
4. Akun yang sama dapat melakukan pendaftaran pada gelombang lain jika aturan penerimaan mengizinkannya.
    
5. Setiap pendaftaran hanya memilih satu program studi.
    
6. Sistem melakukan validasi otomatis terhadap kelengkapan dan format data serta dokumen yang diwajibkan.
    
7. Validasi otomatis tidak menentukan kelulusan calon mahasiswa.
    
8. Keputusan akhir diterima atau ditolak ditetapkan oleh panitia yang berwenang.
    
9. Calon mahasiswa hanya dapat melihat dan mengelola pendaftarannya sendiri.
    
10. Perubahan status pendaftaran harus dicatat dalam riwayat, termasuk waktu, pelaku, dan catatan jika tersedia.
    
11. Dokumen pendaftaran disimpan secara privat dan tidak boleh diakses hanya dengan menebak alamat berkas.
    
12. Daftar program studi, jadwal gelombang, dan ketentuan penerimaan harus dapat dikelola sesuai kewenangan yang ditetapkan.
    

## 8. Status Pendaftaran

Sistem menggunakan status berikut:

|Status|Keterangan|
|---|---|
|`draft`|Formulir masih dalam proses pengisian.|
|`submitted`|Pendaftaran telah dikirim dan lolos validasi awal sistem.|
|`review`|Pendaftaran sedang diperiksa panitia.|
|`revision`|Calon mahasiswa diminta memperbaiki data atau dokumen.|
|`accepted`|Calon mahasiswa dinyatakan diterima oleh panitia yang berwenang.|
|`rejected`|Calon mahasiswa dinyatakan tidak diterima oleh panitia yang berwenang.|

Perubahan status harus mengikuti alur bisnis yang ditetapkan dalam dokumen Workflow & Business Process. Sistem tidak boleh mengubah status secara sembarangan atau melewati kewenangan yang ditentukan.

## 9. Teknologi yang Digunakan

|Bagian|Teknologi|Fungsi|
|---|---|---|
|Aplikasi Mobile|Kotlin dan Android|Antarmuka calon mahasiswa|
|Website Admin|Teknologi web yang ditetapkan saat perancangan|Antarmuka pengelola sistem|
|Backend|Laravel|Logika bisnis dan REST API|
|Database|MySQL|Penyimpanan data terstruktur|
|Komunikasi|REST API dan JSON|Pertukaran data antarkomponen|
|Version Control|Git dan GitHub|Pengelolaan versi kode|
|Lingkungan awal|Komputer lokal|Pengembangan dan pengujian awal|

Versi perangkat lunak, konfigurasi server, dan dependensi akan ditentukan lebih lanjut pada dokumen System Design dan Environment Configuration.

## 10. Strategi Deployment

Pengembangan awal dilakukan di lingkungan lokal untuk mempermudah implementasi, pengujian, dan perbaikan sistem sebelum dipublikasikan.

Lingkungan lokal meliputi backend Laravel, database MySQL, website admin, serta aplikasi Android yang terhubung ke backend melalui jaringan yang dapat dijangkau.

Hal yang perlu diperhatikan:

- Alamat backend harus sesuai dengan lingkungan pengujian, baik emulator maupun perangkat fisik.
    
- `localhost` pada Android mengacu pada perangkat Android itu sendiri, bukan otomatis ke komputer pengembang.
    
- Konfigurasi jaringan, firewall, dan izin akses harus disesuaikan dengan kebutuhan pengujian.
    
- Kredensial dan konfigurasi rahasia tidak boleh disimpan langsung di repository.
    
- Backup database dan dokumen perlu direncanakan meskipun aplikasi masih berjalan secara lokal.
    
- Jika sistem dipindahkan ke server produksi, konfigurasi lingkungan harus ditinjau kembali, termasuk HTTPS, penyimpanan privat, akses jaringan, dan backup.
    

## 11. Dokumen Acuan Proyek

Dokumen berikut menjadi bagian dari dokumentasi proyek:

- `PRD Final v1.0.md` — kebutuhan dan aturan bisnis utama.
    
- `Project Plan.md` — tahapan pengerjaan, target, dan pembagian tugas.
    
- `SRS.md` — spesifikasi kebutuhan perangkat lunak.
    
- `Traceability Matrix.md` — hubungan kebutuhan dengan desain dan pengujian.
    
- `System Design.md` — arsitektur dan rancangan sistem.
    
- `Workflow & Business Process.md` — alur proses bisnis.
    
- `Module Specification.md` — spesifikasi modul aplikasi.
    
- `Database Model.md` — rancangan struktur database.
    
- `API Contract.md` — kontrak komunikasi API.
    
- `UI UX Design.md` — rancangan pengalaman dan antarmuka pengguna.
    
- `Security Specification.md` — kebutuhan keamanan.
    
- `Test Plan.md` dan `Test Cases.md` — strategi serta skenario pengujian.
    
- `Deployment Guide.md` dan `Environment Configuration.md` — panduan menjalankan sistem.
    
- `User Manual.md` dan `Admin Guide.md` — panduan penggunaan.
    

Setiap dokumen harus konsisten dengan PRD dan diperbarui jika ada perubahan kebutuhan yang telah disetujui.

## 12. Keputusan yang Masih Perlu Ditetapkan

Beberapa keputusan belum final dan perlu dikonfirmasi sebelum implementasi terkait diselesaikan:

- Jadwal mulai dan berakhirnya setiap gelombang.
    
- Kebijakan keunikan NIK.
    
- Format dan ukuran maksimum berkas.
    
- Batas waktu serta jumlah revisi pendaftaran.
    
- Kewenangan perubahan keputusan akhir.
    
- Isi bukti atau nomor registrasi pendaftaran.
    
- Versi minimum Android yang didukung.
    
- Target performa dan kapasitas pengguna.
    
- Masa retensi data dan dokumen.
    
- Konfigurasi server lokal serta strategi backup dan pemulihan.
    
- Daftar resmi program studi dan ketentuan penerimaan yang berlaku.
    

Keputusan yang belum ditetapkan tidak boleh dianggap sebagai kebijakan final. Keputusan tersebut harus dicatat dan diperbarui pada dokumen yang sesuai setelah disepakati.

## 13. Kriteria Keberhasilan Proyek

MVP dianggap memenuhi tujuan awal apabila:

1. Calon mahasiswa dapat membuat akun dan login.
    
2. Calon mahasiswa dapat mengisi, menyimpan, dan mengirim formulir pendaftaran.
    
3. Sistem dapat menerima dan menyimpan dokumen persyaratan secara aman.
    
4. Sistem mencegah pendaftaran ganda oleh akun yang sama pada gelombang yang sama.
    
5. Panitia dapat memeriksa pendaftaran, meminta revisi, dan menetapkan keputusan akhir sesuai kewenangan.
    
6. Calon mahasiswa dapat melihat status dan riwayat pendaftarannya sendiri.
    
7. Admin dapat mengelola data utama yang menjadi kewenangannya.
    
8. Pimpinan dapat melihat laporan sesuai hak akses.
    
9. Data pendaftaran dan riwayat status tetap konsisten.
    
10. Sistem dapat dijalankan dan diuji pada lingkungan lokal sesuai konfigurasi yang ditentukan.
    

## 14. Status Dokumen

|Atribut|Nilai|
|---|---|
|Dokumen|Project Overview|
|Versi|1.0|
|Status|Baseline proyek|
|Acuan kebutuhan|PRD Final v1.0|
|Lingkungan awal|Lokal|
|Perubahan|Dicatat pada pembaruan dokumen berikutnya|

**Catatan:** Project Overview ini merangkum arah, batasan, komponen, dan keputusan utama proyek. Detail teknis tetap dijelaskan pada dokumen khusus masing-masing. Jika terdapat konflik, kebutuhan dan aturan bisnis yang telah disetujui dalam PRD menjadi acuan untuk meninjau dokumen terkait.