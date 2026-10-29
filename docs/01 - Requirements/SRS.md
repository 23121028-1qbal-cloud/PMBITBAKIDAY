---

title: SRS - Software Requirements Specification  
project: PMB ITB Akiday  
version: 1.0  
status: Baseline  
created: 2026-10-17  
tags:

- PMB
    
- SRS
    
- software-requirements
    

---

# Software Requirements Specification (SRS)

## Sistem Penerimaan Mahasiswa Baru ITB Akiday

## 1. Pendahuluan

### 1.1 Tujuan Dokumen

Dokumen Software Requirements Specification (SRS) menjelaskan kebutuhan perangkat lunak untuk Sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday. Dokumen ini menjadi acuan bagi pengembang, perancang sistem, penguji, dan pihak terkait dalam memahami fungsi, batasan, serta kualitas yang harus dipenuhi oleh sistem.

SRS diturunkan dari `PRD Final v1.0.md`. PRD menjelaskan tujuan dan ruang lingkup produk, sedangkan SRS menjabarkan kebutuhan tersebut menjadi spesifikasi yang lebih terukur dan dapat digunakan sebagai dasar desain, implementasi, serta pengujian.

### 1.2 Ruang Lingkup Produk

PMB ITB Akiday merupakan sistem penerimaan mahasiswa baru yang terdiri dari aplikasi Android untuk calon mahasiswa dan website untuk pengelola penerimaan. Kedua antarmuka berkomunikasi dengan backend melalui REST API, sedangkan data terstruktur disimpan dalam MySQL.

Sistem membantu proses pembuatan akun, pengisian formulir, pengunggahan dokumen, pemeriksaan pendaftaran, permintaan revisi, pemantauan status, penetapan hasil seleksi, serta penyusunan laporan.

Pengembangan awal dilakukan pada lingkungan lokal. Akses publik melalui internet dan deployment produksi belum termasuk dalam tahap awal.

### 1.3 Istilah dan Singkatan

|Istilah|Penjelasan|
|---|---|
|PMB|Penerimaan Mahasiswa Baru|
|SRS|Software Requirements Specification|
|PRD|Product Requirements Document|
|API|Application Programming Interface|
|REST API|Antarmuka komunikasi berbasis HTTP|
|RBAC|Role-Based Access Control|
|MVP|Minimum Viable Product|
|DBMS|Database Management System|
|UI/UX|User Interface dan User Experience|
|Panitia|Pengelola proses pemeriksaan dan seleksi pendaftaran|

### 1.4 Referensi Dokumen

- `PRD Final v1.0.md`
    
- `Project Overview.md`
    
- `Project Plan.md`
    
- `Workflow & Business Process.md`
    
- `System Design.md`
    
- `Database Model.md`
    
- `API Contract.md`
    
- `Security Specification.md`
    

Dokumen yang belum selesai akan digunakan sebagai referensi lanjutan setelah disusun. Jika ditemukan perbedaan kebutuhan, PRD yang telah disetujui menjadi acuan untuk meninjau dan menyelaraskan dokumen terkait.

## 2. Gambaran Umum Sistem

### 2.1 Perspektif Produk

Sistem dibangun dengan arsitektur client-server yang terdiri dari:

1. **Aplikasi Android:** digunakan calon mahasiswa.
    
2. **Website Admin:** digunakan admin, panitia, dan pimpinan sesuai kewenangannya.
    
3. **Backend Laravel:** menangani autentikasi, validasi, aturan bisnis, dan pemrosesan data.
    
4. **Database MySQL:** menyimpan data pengguna, profil, gelombang, program studi, pendaftaran, dan riwayat status.
    
5. **Penyimpanan dokumen privat:** menyimpan berkas persyaratan pendaftaran.
    

Aplikasi Android dan website tidak diperbolehkan mengakses database secara langsung. Seluruh operasi data dilakukan melalui backend.

### 2.2 Kelas Pengguna

|Pengguna|Kebutuhan|
|---|---|
|Calon Mahasiswa|Mengelola profil, mengisi formulir, mengunggah dokumen, dan memantau status pendaftaran sendiri.|
|Admin|Mengelola akun dan data utama sistem sesuai kewenangan.|
|Panitia|Memeriksa pendaftaran, meminta revisi, serta menetapkan keputusan akhir.|
|Pimpinan|Melihat statistik dan laporan penerimaan sesuai hak akses.|

### 2.3 Lingkungan Operasional

|Komponen|Spesifikasi Baseline|
|---|---|
|Aplikasi mobile|Android menggunakan Kotlin|
|Antarmuka pengelola|Website Admin|
|Backend|Laravel REST API|
|Database|MySQL|
|Format data API|JSON|
|Lingkungan awal|Komputer lokal|
|Version control|Git|
|Penyimpanan dokumen|Penyimpanan privat di sisi server|

Versi minimum Android, versi Laravel, versi MySQL, serta kebutuhan perangkat keras server lokal perlu ditetapkan pada dokumen desain dan konfigurasi lingkungan.

### 2.4 Batasan Sistem

1. Pendaftaran pada MVP tidak dikenakan biaya.
    
2. Setiap pendaftaran hanya memilih satu program studi.
    
3. Satu akun hanya dapat memiliki satu pendaftaran pada setiap gelombang.
    
4. Akun yang sama dapat mendaftar kembali pada gelombang lain jika aturan penerimaan mengizinkan.
    
5. Validasi otomatis hanya memeriksa kelengkapan, format, dan persyaratan teknis dokumen; bukan menentukan kelulusan.
    
6. Keputusan akhir diterima atau ditolak dibuat oleh panitia yang berwenang.
    
7. Aplikasi berjalan pada lingkungan lokal selama tahap awal.
    
8. Program studi yang digunakan pada prototipe belum dianggap sebagai daftar resmi institusi.
    

## 3. Kebutuhan Fungsional

Kebutuhan fungsional diberi kode `FR` agar dapat dirujuk pada desain, implementasi, dan pengujian.

### 3.1 Autentikasi dan Akun

**FR-01 — Registrasi Akun**

Sistem harus memungkinkan calon mahasiswa membuat akun menggunakan email dan kata sandi.

Kriteria penerimaan:

- Email wajib diisi dan memiliki format yang valid.
    
- Email harus unik pada sistem.
    
- Kata sandi disimpan dalam bentuk hash, bukan teks asli.
    
- Sistem menampilkan pesan yang sesuai apabila registrasi gagal.
    

**FR-02 — Login dan Logout**

Sistem harus menyediakan fungsi login dan logout bagi pengguna yang memiliki akun.

Kriteria penerimaan:

- Pengguna harus memberikan kredensial yang valid.
    
- Pengguna yang tidak terautentikasi tidak boleh mengakses fitur yang memerlukan login.
    
- Hak akses setelah login harus sesuai dengan peran pengguna.
    
- Proses logout harus mengakhiri atau mencabut sesi/token sesuai mekanisme autentikasi yang digunakan.
    

**FR-03 — Pengelolaan Profil Calon Mahasiswa**

Sistem harus memungkinkan calon mahasiswa mengisi dan memperbarui data profil yang diizinkan.

Data profil meliputi:

- Nama lengkap.
    
- NIK.
    
- Tempat lahir.
    
- Tanggal lahir.
    
- Jenis kelamin.
    
- Nomor telepon.
    
- Alamat.
    
- Foto.
    

Kriteria penerimaan:

- Data wajib harus diperiksa oleh backend.
    
- Format data harus sesuai dengan aturan yang ditentukan.
    
- Calon mahasiswa hanya dapat mengelola profil miliknya sendiri.
    

### 3.2 Data Penerimaan

**FR-04 — Melihat Gelombang Penerimaan**

Sistem harus menampilkan gelombang penerimaan beserta jadwal dan statusnya.

Kriteria penerimaan:

- Informasi diambil dari backend.
    
- Gelombang yang tidak memenuhi ketentuan pendaftaran tidak boleh dipilih untuk membuat pendaftaran baru.
    
- Aturan tentang batas waktu dan status gelombang harus diterapkan di backend.
    

**FR-05 — Melihat Program Studi**

Sistem harus menampilkan daftar program studi yang tersedia untuk dipilih.

Kriteria penerimaan:

- Data program studi berasal dari database.
    
- Program studi yang tidak aktif tidak dapat dipilih untuk pendaftaran baru.
    
- Setiap pendaftaran hanya menyimpan satu pilihan program studi.
    

**FR-06 — Membuat dan Menyimpan Pendaftaran**

Sistem harus memungkinkan calon mahasiswa membuat dan menyimpan formulir pendaftaran.

Kriteria penerimaan:

- Pendaftaran terhubung dengan akun pemilik, gelombang, dan program studi.
    
- Sistem dapat menyimpan pendaftaran dengan status `draft` selama proses pengisian.
    
- Backend memeriksa apakah akun telah memiliki pendaftaran pada gelombang yang sama.
    
- Database menerapkan batasan unik pada kombinasi pemilik pendaftaran dan gelombang untuk mencegah duplikasi.
    

**FR-07 — Mengunggah Dokumen Persyaratan**

Sistem harus memungkinkan calon mahasiswa mengunggah foto dan ijazah atau Surat Keterangan Lulus (SKL).

Kriteria penerimaan:

- Dokumen diperiksa berdasarkan tipe, ukuran, dan persyaratan yang ditentukan.
    
- Sistem menyimpan informasi berkas dan lokasi penyimpanannya.
    
- Berkas disimpan di lokasi privat.
    
- Pengguna tidak dapat mengakses dokumen pendaftar lain tanpa kewenangan.
    

Format berkas yang diperbolehkan dan ukuran maksimum harus ditetapkan sebelum implementasi final fitur unggah.

**FR-08 — Validasi dan Pengiriman Pendaftaran**

Sistem harus memvalidasi data serta dokumen sebelum pendaftaran dikirim.

Kriteria penerimaan:

- Data wajib dan dokumen wajib harus tersedia.
    
- Format data dan berkas diperiksa oleh backend.
    
- Jika validasi gagal, sistem memberikan informasi mengenai bagian yang perlu diperbaiki.
    
- Jika validasi berhasil, pendaftaran dapat berpindah dari `draft` ke `submitted`.
    
- Validasi otomatis tidak boleh langsung mengubah pendaftaran menjadi `accepted` atau `rejected`.
    

### 3.3 Pemantauan Pendaftaran

**FR-09 — Melihat Status Pendaftaran**

Sistem harus memungkinkan calon mahasiswa melihat status pendaftarannya sendiri.

Kriteria penerimaan:

- Informasi status berasal dari backend.
    
- Pengguna hanya dapat melihat pendaftaran yang dimilikinya.
    
- Status yang ditampilkan mengikuti data terbaru yang tersedia di server.
    

**FR-10 — Melihat Riwayat Status**

Sistem harus mencatat dan menampilkan riwayat perubahan status pendaftaran sesuai hak akses.

Kriteria penerimaan:

- Riwayat menyimpan status sebelumnya dan status baru.
    
- Riwayat mencatat waktu perubahan.
    
- Riwayat mencatat pelaku perubahan jika tersedia.
    
- Catatan perubahan disimpan apabila diberikan.
    
- Riwayat tidak boleh dapat diubah secara bebas oleh calon mahasiswa.
    

### 3.4 Pemeriksaan oleh Panitia

**FR-11 — Melihat Daftar Pendaftaran**

Sistem harus menyediakan daftar pendaftaran bagi panitia yang berwenang.

Kriteria penerimaan:

- Daftar dapat menampilkan informasi yang dibutuhkan untuk pemeriksaan.
    
- Panitia dapat melihat detail pendaftaran sesuai kewenangannya.
    
- Data yang ditampilkan tidak boleh melampaui hak akses pengguna.
    

**FR-12 — Memeriksa Data dan Dokumen**

Sistem harus memungkinkan panitia memeriksa informasi dan dokumen pendaftar.

Kriteria penerimaan:

- Panitia dapat melihat data pendaftaran dan dokumen terkait.
    
- Dokumen hanya dapat dibuka oleh pengguna yang berwenang.
    
- Hasil pemeriksaan mengikuti proses bisnis yang ditentukan.
    

**FR-13 — Meminta Revisi**

Sistem harus memungkinkan panitia meminta perbaikan data atau dokumen.

Kriteria penerimaan:

- Permintaan revisi menyertakan catatan atau alasan.
    
- Status berubah dari `review` menjadi `revision` jika tindakan tersebut diizinkan.
    
- Calon mahasiswa dapat melihat catatan revisi.
    
- Perubahan status tercatat pada riwayat.
    

**FR-14 — Menetapkan Keputusan Akhir**

Sistem harus memungkinkan panitia yang memiliki kewenangan menetapkan keputusan akhir diterima atau ditolak.

Kriteria penerimaan:

- Keputusan hanya dapat dilakukan pada status dan kondisi yang diizinkan.
    
- Pengguna yang tidak berwenang tidak dapat menetapkan keputusan.
    
- Keputusan akhir mengubah status menjadi `accepted` atau `rejected`.
    
- Keputusan dicatat pada riwayat dengan pelaku, waktu, dan catatan jika tersedia.
    
- Sistem tidak membuat keputusan akademik akhir secara otomatis.
    

### 3.5 Administrasi dan Pelaporan

**FR-15 — Mengelola Data Utama**

Sistem harus menyediakan fungsi pengelolaan data utama bagi admin yang berwenang, termasuk akun, gelombang penerimaan, dan program studi.

Kriteria penerimaan:

- Tindakan pengelolaan dibatasi berdasarkan peran.
    
- Perubahan data harus divalidasi.
    
- Data yang telah digunakan oleh pendaftaran harus dikelola dengan mempertimbangkan integritas data historis.
    

**FR-16 — Melihat Dashboard Statistik**

Sistem harus menampilkan ringkasan statistik penerimaan kepada pengguna yang berwenang.

Kriteria penerimaan:

- Statistik dihitung dari data pendaftaran yang tersimpan.
    
- Statistik dapat dikelompokkan berdasarkan informasi yang disepakati, termasuk program studi dan status pendaftaran.
    
- Pengguna tanpa hak akses tidak dapat membuka dashboard yang dibatasi.
    

**FR-17 — Menghasilkan Laporan PDF**

Sistem harus menyediakan laporan penerimaan dalam format PDF kepada pengguna yang memiliki izin.

Kriteria penerimaan:

- Laporan menggunakan data yang tersedia pada sistem.
    
- Isi laporan mengikuti filter dan cakupan yang dipilih jika fitur filter tersedia.
    
- Proses pembuatan laporan memeriksa hak akses.
    
- Informasi pribadi hanya dimasukkan jika dibutuhkan dan diizinkan.
    

## 4. Aturan Bisnis

|ID|Aturan|
|---|---|
|BR-01|Pendaftaran pada MVP bersifat gratis.|
|BR-02|Setiap pendaftaran hanya memilih satu program studi.|
|BR-03|Satu akun hanya memiliki satu pendaftaran pada setiap gelombang.|
|BR-04|Akun yang sama dapat mendaftar pada gelombang berbeda jika diizinkan aturan penerimaan.|
|BR-05|Pendaftaran harus memenuhi validasi sebelum dikirim.|
|BR-06|Keputusan akhir dibuat oleh panitia yang berwenang.|
|BR-07|Calon mahasiswa hanya dapat mengakses pendaftarannya sendiri.|
|BR-08|Perubahan status harus mengikuti transisi yang diizinkan.|
|BR-09|Perubahan status penting harus tercatat dalam riwayat.|
|BR-10|Dokumen persyaratan disimpan secara privat.|
|BR-11|Pengguna hanya boleh menjalankan tindakan yang sesuai dengan perannya.|
|BR-12|Program studi prototipe tidak boleh dianggap sebagai daftar resmi tanpa konfirmasi.|

## 5. Siklus Status Pendaftaran

Status pendaftaran terdiri dari:

- `draft`
    
- `submitted`
    
- `review`
    
- `revision`
    
- `accepted`
    
- `rejected`
    

Transisi dasar yang diizinkan:

|Status Awal|Status Tujuan|Kondisi|
|---|---|---|
|`draft`|`submitted`|Validasi pengiriman berhasil|
|`submitted`|`review`|Pendaftaran mulai diperiksa|
|`review`|`revision`|Panitia meminta perbaikan|
|`revision`|`submitted`|Calon mahasiswa mengirim ulang setelah revisi dan validasi berhasil|
|`submitted`|`review`|Pendaftaran yang dikirim ulang masuk ke pemeriksaan|
|`review`|`accepted`|Panitia berwenang menetapkan diterima|
|`review`|`rejected`|Panitia berwenang menetapkan ditolak|

Transisi status harus diverifikasi di backend. Perubahan dari status akhir ke status lain belum ditetapkan dan tidak boleh dilakukan tanpa aturan resmi.

## 6. Kebutuhan Antarmuka Eksternal

### 6.1 Antarmuka Aplikasi Android

Aplikasi Android harus menyediakan antarmuka untuk:

- Registrasi dan login.
    
- Pengisian profil.
    
- Melihat gelombang dan program studi.
    
- Pengisian formulir pendaftaran.
    
- Pengunggahan dokumen.
    
- Pengiriman pendaftaran.
    
- Melihat status dan riwayat.
    
- Melihat catatan revisi.
    

### 6.2 Antarmuka Website Admin

Website Admin harus menyediakan antarmuka untuk:

- Login pengguna pengelola.
    
- Pengelolaan data utama.
    
- Daftar dan detail pendaftaran.
    
- Pemeriksaan dokumen.
    
- Permintaan revisi.
    
- Penetapan keputusan akhir.
    
- Dashboard dan laporan.
    

Tampilan dan rincian setiap halaman dijelaskan lebih lanjut dalam `UI UX Design.md` dan `Screen Specification.md`.

### 6.3 Antarmuka Perangkat Lunak

Backend menyediakan REST API untuk komunikasi dengan Android dan website.

Persyaratan:

- Request dan response menggunakan JSON kecuali untuk pengiriman berkas atau respons file yang memerlukan format lain.
    
- Endpoint yang dilindungi harus memerlukan autentikasi.
    
- Backend harus memvalidasi input dari klien.
    
- Kesalahan dikembalikan menggunakan format respons yang konsisten.
    
- Detail endpoint ditetapkan dalam `API Contract.md`.
    

### 6.4 Antarmuka Database

Sistem menggunakan MySQL sebagai DBMS utama.

Persyaratan:

- Relasi antarentitas harus menjaga integritas referensial.
    
- Kombinasi pemilik pendaftaran dan gelombang harus unik.
    
- Kata sandi tidak disimpan dalam bentuk teks asli.
    
- Perubahan status dan data penting harus diproses secara konsisten.
    
- Struktur tabel dijelaskan dalam `Database Model.md` dan `Data Dictionary.md`.
    

## 7. Kebutuhan Nonfungsional

Kebutuhan nonfungsional diberi kode `NFR`.

**NFR-01 — Keamanan Autentikasi**

Kredensial pengguna harus diproses dengan mekanisme autentikasi yang aman. Kata sandi harus disimpan menggunakan algoritma hashing yang sesuai.

**NFR-02 — Kontrol Akses**

Sistem harus menerapkan RBAC pada backend. Setiap endpoint harus memeriksa hak akses, bukan hanya menyembunyikan tombol pada antarmuka.

**NFR-03 — Privasi Data**

Calon mahasiswa hanya dapat mengakses data pendaftarannya sendiri. Data dan dokumen pribadi harus dibatasi sesuai kebutuhan serta kewenangan pengguna.

**NFR-04 — Validasi Backend**

Semua data yang diterima dari klien harus divalidasi di backend. Validasi pada antarmuka pengguna tidak boleh menjadi satu-satunya perlindungan.

**NFR-05 — Integritas Data**

Sistem harus menjaga hubungan antar data menggunakan constraint database, transaksi, dan validasi logika bisnis sesuai kebutuhan.

**NFR-06 — Pencegahan Duplikasi**

Sistem harus mencegah akun membuat lebih dari satu pendaftaran pada gelombang yang sama, termasuk ketika terdapat permintaan bersamaan.

**NFR-07 — Kemudahan Penggunaan**

Antarmuka harus menggunakan bahasa yang jelas, label yang mudah dipahami, serta pesan kesalahan yang membantu pengguna memperbaiki input.

**NFR-08 — Konsistensi Antarmuka**

Aplikasi Android dan website harus menggunakan istilah status dan aturan bisnis yang konsisten.

**NFR-09 — Kemudahan Pemeliharaan**

Kode harus dipisahkan ke dalam komponen atau modul yang jelas, menggunakan konvensi penamaan yang konsisten, dan memiliki dokumentasi teknis.

**NFR-10 — Kompatibilitas Lingkungan**

Sistem harus dapat dijalankan di lingkungan lokal sesuai konfigurasi dan petunjuk yang disediakan. Versi minimum sistem operasi dan dependensi ditetapkan dalam dokumen konfigurasi.

**NFR-11 — Pencatatan Aktivitas**

Perubahan status penting harus memiliki catatan yang memadai untuk pemeriksaan dan penelusuran masalah.

**NFR-12 — Pemulihan Data**

Prosedur backup dan pemulihan database serta dokumen harus ditentukan dan diuji sebelum sistem dinyatakan siap digunakan.

Target numerik seperti waktu respons, jumlah pengguna bersamaan, kapasitas penyimpanan, dan target pemulihan belum ditentukan. Target tersebut harus disepakati sebelum pengujian performa dan pemulihan dilakukan.

## 8. Kebutuhan Data

### 8.1 Data Utama

Sistem setidaknya perlu mengelola data berikut:

- Pengguna dan peran.
    
- Profil calon mahasiswa.
    
- Gelombang penerimaan.
    
- Program studi.
    
- Pendaftaran.
    
- Dokumen pendaftaran.
    
- Riwayat perubahan status.
    

### 8.2 Data Profil

Data profil calon mahasiswa meliputi nama lengkap, NIK, tempat dan tanggal lahir, jenis kelamin, email, nomor telepon, alamat, dan foto.

Kebijakan keunikan NIK, validasi format, serta penanganan perubahan data pribadi perlu ditetapkan dalam dokumen desain database.

### 8.3 Data Dokumen

Setiap dokumen perlu memiliki informasi seperti:

- Pemilik pendaftaran.
    
- Jenis dokumen.
    
- Lokasi penyimpanan.
    
- Tipe media.
    
- Ukuran berkas.
    
- Waktu unggah.
    

Isi berkas tidak boleh dapat diakses oleh pengguna yang tidak memiliki izin.

### 8.4 Data Riwayat

Riwayat status perlu mencatat status awal, status tujuan, waktu perubahan, pelaku perubahan jika tersedia, serta catatan yang relevan.

Struktur lengkap dan tipe data dijelaskan dalam `Database Model.md` dan `Data Dictionary.md`.

## 9. Kriteria Penerimaan Sistem

MVP dapat dinyatakan memenuhi kebutuhan utama apabila:

1. Calon mahasiswa dapat melakukan registrasi dan login.
    
2. Calon mahasiswa dapat mengisi profil dan formulir.
    
3. Sistem dapat menerima dokumen yang sesuai ketentuan.
    
4. Sistem menolak pengiriman formulir yang tidak memenuhi persyaratan.
    
5. Sistem mencegah pendaftaran ganda pada gelombang yang sama.
    
6. Calon mahasiswa dapat melihat status dan riwayat pendaftarannya sendiri.
    
7. Panitia dapat memeriksa pendaftaran dan meminta revisi.
    
8. Panitia berwenang dapat menetapkan keputusan akhir.
    
9. Admin dapat mengelola data utama sesuai kewenangan.
    
10. Pimpinan dapat melihat laporan sesuai hak akses.
    
11. Pengguna tidak dapat mengakses fungsi atau data di luar kewenangannya.
    
12. Sistem dapat dijalankan dan diuji pada lingkungan lokal.
    
13. Hasil pengujian utama tercatat dalam dokumentasi pengujian.
    

## 10. Asumsi dan Ketergantungan

### 10.1 Asumsi

- Backend Laravel menjadi pusat validasi dan aturan bisnis.
    
- MySQL menjadi sumber data utama.
    
- Android dan website mengakses backend melalui API.
    
- Pendaftaran pada MVP tidak memerlukan pembayaran.
    
- Panitia tetap bertanggung jawab atas keputusan akhir penerimaan.
    
- Data program studi prototipe akan dikonfirmasi sebelum digunakan sebagai data resmi.
    

### 10.2 Ketergantungan

- Lingkungan pengembangan harus memiliki perangkat lunak dan dependensi yang sesuai.
    
- Backend, database, dan penyimpanan dokumen harus dapat diakses dari klien pengujian.
    
- Desain database harus tersedia sebelum integrasi data dilakukan secara menyeluruh.
    
- Kontrak API harus disepakati agar pengembangan Android dan website berjalan konsisten.
    
- Ketentuan gelombang, dokumen, dan proses revisi harus dikonfirmasi sebelum aturan terkait ditetapkan secara final.
    

## 11. Keputusan Terbuka

|ID|Keputusan|Status|
|---|---|---|
|OD-01|Jadwal dan batas waktu gelombang|Belum ditetapkan|
|OD-02|Kebijakan keunikan NIK|Belum ditetapkan|
|OD-03|Format dan ukuran maksimum dokumen|Belum ditetapkan|
|OD-04|Batas waktu dan jumlah revisi|Belum ditetapkan|
|OD-05|Perubahan keputusan akhir|Belum ditetapkan|
|OD-06|Isi bukti atau nomor registrasi|Belum ditetapkan|
|OD-07|Versi minimum Android|Belum ditetapkan|
|OD-08|Target performa dan pengguna bersamaan|Belum ditetapkan|
|OD-09|Masa retensi data dan dokumen|Belum ditetapkan|
|OD-10|Konfigurasi server lokal dan backup|Belum ditetapkan|
|OD-11|Daftar resmi program studi|Perlu konfirmasi|

Keputusan terbuka harus diperbarui setelah memperoleh persetujuan. Detail implementasi tidak boleh dianggap sebagai aturan final sebelum keputusan yang relevan ditetapkan.

## 12. Riwayat Perubahan

|Versi|Tanggal|Keterangan|
|---|---|---|
|1.0|2026-10-17|Penyusunan awal SRS berdasarkan PRD Final v1.0|

---

**Dokumen acuan utama:** `PRD Final v1.0.md`

**Dokumen lanjutan:** `Traceability Matrix.md`, `Workflow & Business Process.md`, `System Design.md`, `Database Model.md`, `API Contract.md`, dan `Security Specification.md`.