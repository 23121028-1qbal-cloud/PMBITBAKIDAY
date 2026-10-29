## 1. Informasi Dokumen

|Informasi|Keterangan|
|---|---|
|Nama Proyek|PMB ITB Akiday|
|Nama Dokumen|Test Cases|
|Versi|1.0|
|Status|Draft untuk Pengujian|
|Platform|Android, Backend API, Website Admin|
|Backend|Laravel|
|Database|MySQL|
|Metode Pengujian|Black Box Testing, Integration Testing, Security Testing|
|Referensi|SRS, API Contract, Database Model, Security Specification, Module Specification|

## 2. Tujuan Pengujian

Dokumen Test Cases digunakan sebagai panduan untuk memeriksa apakah sistem PMB ITB Akiday telah berjalan sesuai kebutuhan yang ditentukan. Pengujian dilakukan untuk memastikan calon mahasiswa dapat membuat akun, melengkapi profil, melakukan pendaftaran, mengunggah dokumen, dan memantau status pendaftaran. Selain itu, pengujian juga mencakup fungsi admin dan panitia dalam memeriksa data pendaftaran, meminta revisi, memberikan keputusan, serta menghasilkan laporan.

Pengujian tidak hanya berfokus pada fungsi normal, tetapi juga mencakup kondisi ketika pengguna memasukkan data yang salah, mengunggah dokumen yang tidak sesuai, mengakses data milik pengguna lain, atau mengirimkan permintaan yang sama secara berulang.

Hasil pengujian digunakan untuk menemukan kesalahan, memastikan konsistensi data, dan menilai kesiapan sistem sebelum digunakan.

## 3. Ruang Lingkup Pengujian

Pengujian dibagi menjadi beberapa kelompok.

|Kode|Area Pengujian|Fokus|
|---|---|---|
|TC-AUTH|Autentikasi|Registrasi akun, login, logout, dan validasi kredensial|
|TC-PROFILE|Profil Calon Mahasiswa|Pengisian dan perubahan data profil|
|TC-WAVE|Gelombang Pendaftaran|Penampilan dan validasi gelombang|
|TC-APP|Pendaftaran|Pembuatan, penyimpanan, dan pengiriman pendaftaran|
|TC-DOC|Dokumen|Unggah, validasi, akses, dan pengunduhan dokumen|
|TC-STATUS|Status Pendaftaran|Pemeriksaan status dan riwayat perubahan|
|TC-REVIEW|Pemeriksaan Panitia|Pemeriksaan pendaftaran dan permintaan revisi|
|TC-DECISION|Keputusan|Penetapan diterima atau ditolak oleh petugas berwenang|
|TC-ADMIN|Administrasi|Pengelolaan akun, gelombang, dan program studi|
|TC-REPORT|Laporan|Dashboard, statistik, dan unduhan laporan|
|TC-SEC|Keamanan|Autentikasi, otorisasi, dan perlindungan data|
|TC-DB|Database|Integritas relasi dan pencegahan data duplikat|
|TC-API|REST API|Validasi request dan penanganan error|
|TC-UI|Antarmuka|Tampilan dan navigasi aplikasi|

## 4. Prioritas Pengujian

|Prioritas|Arti|
|---|---|
|P1 — Tinggi|Fitur kritis yang berpengaruh terhadap keamanan, integritas data, pendaftaran, atau keputusan penerimaan.|
|P2 — Sedang|Fitur penting yang mendukung proses utama, tetapi tidak secara langsung menyebabkan kegagalan keseluruhan sistem.|
|P3 — Rendah|Fitur pendukung atau aspek tampilan yang tidak menghambat proses utama.|

## 5. Lingkungan Pengujian

Lingkungan berikut direkomendasikan untuk pengujian awal.

|Komponen|Konfigurasi|
|---|---|
|Sistem Operasi Pengembangan|Windows atau Linux|
|Android|Emulator Android atau perangkat fisik|
|Backend|Laravel pada server lokal|
|Database|MySQL|
|API Testing|Postman atau alat sejenis|
|Android Testing|Android Studio|
|Browser Admin|Chrome atau browser modern yang mendukung aplikasi|
|Repository|Git|
|Data Pengujian|Data dummy, bukan data calon mahasiswa asli|

Contoh alamat backend lokal:

```
http://localhost:8000/api/v1
```

Untuk Android Emulator standar yang mengakses server pada komputer host:

```
http://10.0.2.2:8000/api/v1
```

Alamat tersebut hanya contoh untuk pengembangan lokal. Pengujian produksi harus menggunakan alamat HTTPS yang telah ditetapkan.

## 6. Data Pengujian

Gunakan data dummy berikut sebagai acuan awal. Seluruh nama, identitas, dan alamat dalam tabel ini merupakan data fiktif untuk pengujian.

### 6.1 Akun Pengguna

|Kode|Role|Email|Password|Kondisi|
|---|---|---|---|---|
|D-001|Calon Mahasiswa|`mahasiswa1@example.test`|Password dummy yang valid|Akun aktif|
|D-002|Calon Mahasiswa|`mahasiswa2@example.test`|Password dummy yang valid|Akun aktif|
|D-003|Admin|`admin@example.test`|Password dummy yang valid|Akun aktif|
|D-004|Panitia|`panitia@example.test`|Password dummy yang valid|Akun aktif|
|D-005|Pimpinan|`pimpinan@example.test`|Password dummy yang valid|Akun aktif|
|D-006|Calon Mahasiswa|`nonaktif@example.test`|Password dummy yang valid|Akun nonaktif|

**Catatan:** Email dan password pada tabel adalah data untuk menyusun skenario, bukan kredensial akun yang sudah dibuat. Password sebenarnya harus diatur melalui mekanisme pengembangan yang aman dan tidak disimpan dalam repository.

### 6.2 Data Gelombang

|Kode|Nama Gelombang|Kondisi|
|---|---|---|
|W-001|Gelombang 1|Aktif dan berada dalam periode pendaftaran|
|W-002|Gelombang 2|Belum dibuka|
|W-003|Gelombang 3|Periode pendaftaran telah berakhir|

Data tanggal harus dikonfigurasi sesuai kebutuhan lingkungan pengujian agar kondisi aktif, belum dibuka, dan sudah berakhir dapat diuji secara konsisten.

### 6.3 Data Program Studi

|Kode|Nama Program Studi|
|---|---|
|PS-001|Informatika|
|PS-002|Sistem Informasi|
|PS-003|Teknik Industri|
|PS-004|Manajemen|

Nama program studi di atas merupakan data prototipe. Data tersebut harus diverifikasi dengan daftar resmi sebelum sistem digunakan untuk penerimaan mahasiswa yang sebenarnya.

### 6.4 Data Dokumen

Siapkan beberapa berkas dummy:

- Dokumen foto berformat gambar yang diizinkan.
- Dokumen ijazah atau SKL dummy dengan format yang diizinkan.
- File berformat tidak didukung untuk pengujian negatif.
- File yang ukurannya melebihi batas konfigurasi.
- File kosong atau file yang rusak untuk pengujian validasi.

Jenis file dan batas ukuran harus mengikuti konfigurasi yang akhirnya ditetapkan dalam sistem.

---

## 7. Test Cases Autentikasi

### TC-AUTH-001 — Registrasi Akun Berhasil

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Pengguna belum memiliki akun dengan email yang digunakan.|

**Langkah Pengujian:**

1. Buka halaman registrasi.
2. Masukkan email yang valid.
3. Masukkan password sesuai kebijakan sistem.
4. Masukkan konfirmasi password jika tersedia.
5. Kirim formulir registrasi.

**Hasil yang Diharapkan:**

- Sistem menerima data yang valid.
- Akun calon mahasiswa berhasil dibuat.
- Password disimpan dalam bentuk hash, bukan teks biasa.
- Pengguna memperoleh respons yang sesuai.
- Pengguna tidak memperoleh hak akses admin atau panitia secara otomatis.

### TC-AUTH-002 — Registrasi dengan Email Duplikat

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Negative Testing|
|Prasyarat|Email sudah terdaftar.|

**Langkah Pengujian:**

1. Buka halaman registrasi.
2. Masukkan email yang sudah digunakan.
3. Masukkan password yang valid.
4. Kirim formulir.

**Hasil yang Diharapkan:**

- Sistem menolak registrasi.
- Sistem memberikan pesan bahwa email tidak dapat digunakan.
- Tidak terbentuk akun duplikat.

### TC-AUTH-003 — Login dengan Kredensial Valid

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Akun tersedia dan aktif.|

**Langkah Pengujian:**

1. Buka halaman login.
2. Masukkan email dan password yang benar.
3. Tekan tombol login.

**Hasil yang Diharapkan:**

- Sistem memverifikasi kredensial.
- Pengguna berhasil masuk.
- Sistem memberikan sesi atau token autentikasi sesuai mekanisme yang digunakan.
- Pengguna diarahkan ke halaman sesuai role.

### TC-AUTH-004 — Login dengan Password Salah

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Negative Testing|

**Langkah Pengujian:**

1. Masukkan email yang terdaftar.
2. Masukkan password yang salah.
3. Tekan tombol login.

**Hasil yang Diharapkan:**

- Login ditolak.
- Sistem tidak memberikan token autentikasi yang valid.
- Pesan kesalahan tidak membocorkan informasi sensitif.
- Percobaan login mengikuti kebijakan rate limiting yang ditetapkan.

### TC-AUTH-005 — Login dengan Akun Nonaktif

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Negative Testing|
|Prasyarat|Akun sudah dinonaktifkan oleh pihak berwenang.|

**Langkah Pengujian:**

1. Masukkan kredensial akun nonaktif.
2. Tekan tombol login.

**Hasil yang Diharapkan:**

- Pengguna tidak dapat mengakses fitur yang memerlukan autentikasi.
- Sistem memberikan respons yang sesuai.
- Akun nonaktif tidak memperoleh sesi atau token aktif baru.

### TC-AUTH-006 — Logout

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Functional Testing|
|Prasyarat|Pengguna sudah login.|

**Langkah Pengujian:**

1. Tekan tombol logout.
2. Coba akses kembali endpoint yang membutuhkan autentikasi menggunakan kredensial sesi atau token sebelumnya.

**Hasil yang Diharapkan:**

- Sesi atau token tidak lagi dapat digunakan sesuai mekanisme autentikasi.
- Pengguna harus melakukan autentikasi kembali untuk mengakses fitur yang dilindungi.

---

## 8. Test Cases Profil Calon Mahasiswa

### TC-PROFILE-001 — Melengkapi Profil

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Calon mahasiswa sudah login.|

**Langkah Pengujian:**

1. Buka halaman profil.
2. Isi nama lengkap, NIK, tempat dan tanggal lahir, jenis kelamin, nomor telepon, serta alamat.
3. Simpan profil.

**Hasil yang Diharapkan:**

- Data valid berhasil disimpan.
- Data tetap tersedia ketika profil dibuka kembali.
- Data tersimpan pada profil yang terhubung dengan akun pengguna.

### TC-PROFILE-002 — Menyimpan Profil dengan Field Wajib Kosong

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Negative Testing|

**Langkah Pengujian:**

1. Buka formulir profil.
2. Kosongkan salah satu field yang wajib diisi.
3. Tekan tombol simpan.

**Hasil yang Diharapkan:**

- Sistem menolak data yang tidak lengkap.
- Pesan validasi menunjukkan field yang harus diperbaiki.
- Data valid yang telah tersimpan sebelumnya tidak rusak akibat request yang gagal.

### TC-PROFILE-003 — Validasi Format NIK

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Boundary/Negative Testing|

**Langkah Pengujian:**

1. Masukkan NIK dengan jumlah karakter yang tidak sesuai.
2. Masukkan NIK yang mengandung huruf atau karakter yang tidak diizinkan.
3. Coba simpan profil.

**Hasil yang Diharapkan:**

- Sistem menolak NIK yang tidak memenuhi aturan format.
- NIK diproses sebagai string agar nilai dengan angka nol di awal tidak berubah.
- Pesan validasi diberikan kepada pengguna.

**Catatan:** Format NIK perlu dikonfirmasi sesuai kebutuhan sistem. Jika sistem menetapkan 16 digit numerik, validasi harus memeriksa panjang dan karakter tersebut tanpa menganggap NIK sebagai bilangan untuk perhitungan.

### TC-PROFILE-004 — Mengakses Profil Pengguna Lain

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Security Testing|
|Prasyarat|Tersedia dua akun calon mahasiswa.|

**Langkah Pengujian:**

1. Login menggunakan akun pertama.
2. Coba mengakses atau mengubah profil akun kedua melalui request API yang dimodifikasi.

**Hasil yang Diharapkan:**

- Sistem menolak akses yang tidak berhak.
- Data profil akun kedua tidak ditampilkan atau diubah.
- Pemeriksaan kepemilikan dilakukan pada backend, bukan hanya pada antarmuka aplikasi.

---

## 9. Test Cases Gelombang Pendaftaran

### TC-WAVE-001 — Menampilkan Gelombang Aktif

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Functional Testing|

**Langkah Pengujian:**

1. Login sebagai calon mahasiswa.
2. Buka halaman pendaftaran.
3. Lihat daftar gelombang.

**Hasil yang Diharapkan:**

- Sistem menampilkan gelombang yang dapat dipilih.
- Informasi nama dan periode pendaftaran sesuai data.
- Gelombang yang belum dibuka atau sudah berakhir tidak dapat dipilih untuk pengiriman pendaftaran.

### TC-WAVE-002 — Mendaftar pada Gelombang yang Belum Dibuka

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Negative Testing|

**Langkah Pengujian:**

1. Pilih atau kirim request untuk gelombang yang belum dibuka.
2. Coba membuat atau mengirim pendaftaran.

**Hasil yang Diharapkan:**

- Sistem menolak tindakan yang tidak sesuai periode.
- Data tidak berpindah ke status `submitted`.
- Validasi periode dilakukan pada backend.

### TC-WAVE-003 — Mendaftar Setelah Periode Berakhir

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Negative Testing|

**Langkah Pengujian:**

1. Gunakan gelombang yang sudah melewati tanggal akhir.
2. Coba mengirim pendaftaran melalui aplikasi atau request API.

**Hasil yang Diharapkan:**

- Sistem menolak pengiriman baru untuk gelombang yang telah berakhir.
- Pengguna menerima pesan yang sesuai.
- Validasi tidak hanya bergantung pada status tombol di aplikasi Android.

---

## 10. Test Cases Pendaftaran

### TC-APP-001 — Membuat Pendaftaran Baru

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Calon mahasiswa login, profil valid, dan gelombang pendaftaran tersedia.|

**Langkah Pengujian:**

1. Buka menu pendaftaran.
2. Pilih gelombang aktif.
3. Pilih satu program studi.
4. Simpan pendaftaran sebagai draft jika fitur draft tersedia.

**Hasil yang Diharapkan:**

- Pendaftaran dibuat dan terhubung dengan akun yang benar.
- Gelombang dan program studi tersimpan sesuai pilihan.
- Status awal adalah `draft` jika sistem mendukung penyimpanan draft.
- Nomor pendaftaran dan waktu pengiriman mengikuti aturan sistem yang ditetapkan.

### TC-APP-002 — Mengirim Pendaftaran yang Lengkap

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Profil dan dokumen wajib sudah lengkap serta gelombang masih terbuka.|

**Langkah Pengujian:**

1. Buka pendaftaran yang sudah lengkap.
2. Tekan tombol kirim pendaftaran.
3. Tunggu respons dari server.
4. Buka kembali detail pendaftaran.

**Hasil yang Diharapkan:**

- Backend memvalidasi seluruh persyaratan.
- Status berubah menjadi `submitted` jika semua ketentuan terpenuhi.
- Waktu pengiriman tercatat.
- Riwayat perubahan status dibuat.
- Pendaftaran dapat dilihat pada daftar pendaftaran panitia.

### TC-APP-003 — Mengirim Pendaftaran dengan Data Belum Lengkap

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Negative Testing|

**Langkah Pengujian:**

1. Buat pendaftaran tanpa melengkapi salah satu persyaratan wajib.
2. Tekan tombol kirim pendaftaran.

**Hasil yang Diharapkan:**

- Sistem menolak pengiriman.
- Status tidak berubah menjadi `submitted`.
- Sistem menjelaskan persyaratan yang belum dipenuhi.
- Tidak tercipta riwayat status palsu yang menyatakan pengiriman berhasil.

### TC-APP-004 — Mencegah Pendaftaran Duplikat dalam Gelombang yang Sama

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Integration Testing|
|Prasyarat|Akun sudah memiliki pendaftaran pada gelombang tertentu.|

**Langkah Pengujian:**

1. Buat pendaftaran pada Gelombang 1.
2. Kirim request kedua dengan akun dan gelombang yang sama.
3. Ulangi request melalui API untuk memastikan validasi tidak hanya bergantung pada UI.

**Hasil yang Diharapkan:**

- Sistem menolak pendaftaran kedua.
- Tidak terbentuk dua baris pendaftaran untuk kombinasi akun dan gelombang yang sama.
- Sistem memberikan respons konflik yang sesuai, misalnya HTTP `409 Conflict`.
- Database mempertahankan unique constraint pada pasangan `applicant_id` dan `wave_id`.

### TC-APP-005 — Mengirim Dua Request Pendaftaran secara Bersamaan

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Concurrency Testing|
|Prasyarat|Akun belum memiliki pendaftaran pada gelombang yang diuji.|

**Langkah Pengujian:**

1. Siapkan dua request untuk membuat pendaftaran dengan akun dan gelombang yang sama.
2. Kirim kedua request hampir bersamaan menggunakan alat pengujian API.
3. Periksa respons dan data di database.

**Hasil yang Diharapkan:**

- Maksimal satu pendaftaran berhasil dibuat untuk pasangan akun dan gelombang tersebut.
- Request lainnya ditolak atau ditangani sebagai konflik.
- Database tidak menyimpan dua pendaftaran yang melanggar aturan unik.
- Jika terjadi konflik database, backend menangani kesalahan tersebut tanpa menampilkan detail internal kepada pengguna.

### TC-APP-006 — Mendaftar pada Gelombang Berbeda

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Akun memiliki pendaftaran pada satu gelombang dan gelombang lain tersedia sesuai kebijakan sistem.|

**Langkah Pengujian:**

1. Login menggunakan akun yang sudah mempunyai pendaftaran pada Gelombang 1.
2. Buat pendaftaran pada Gelombang 2 jika gelombang tersebut sudah dibuka dan pengguna memenuhi syarat.
3. Periksa data pendaftaran.

**Hasil yang Diharapkan:**

- Sistem mengizinkan pendaftaran pada gelombang berbeda sesuai aturan.
- Constraint duplikasi hanya mencegah pendaftaran ganda pada gelombang yang sama.
- Data kedua pendaftaran tetap terhubung dengan akun calon mahasiswa yang benar.

### TC-APP-007 — Nomor Pendaftaran Unik

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Data Integrity Testing|

**Langkah Pengujian:**

1. Buat beberapa pendaftaran yang valid.
2. Periksa nomor pendaftaran yang dihasilkan.
3. Bandingkan nomor seluruh pendaftaran tersebut.

**Hasil yang Diharapkan:**

- Setiap pendaftaran memiliki nomor yang unik.
- Nomor pendaftaran tersimpan sesuai format yang ditetapkan.
- Sistem menangani potensi benturan nomor tanpa menghasilkan nomor duplikat.

---

## 11. Test Cases Dokumen

### TC-DOC-001 — Unggah Dokumen yang Valid

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Calon mahasiswa login dan memiliki pendaftaran.|

**Langkah Pengujian:**

1. Buka bagian dokumen pendaftaran.
2. Pilih file dummy dengan format yang diizinkan.
3. Unggah dokumen.
4. Buka kembali daftar dokumen.

**Hasil yang Diharapkan:**

- Dokumen berhasil diunggah.
- Metadata dokumen tersimpan.
- Dokumen terhubung dengan pendaftaran yang benar.
- File disimpan di lokasi privat dan tidak dapat diakses publik secara langsung.

### TC-DOC-002 — Unggah Format File yang Tidak Didukung

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Negative Testing|

**Langkah Pengujian:**

1. Pilih file dengan format yang tidak diizinkan.
2. Coba unggah file tersebut.

**Hasil yang Diharapkan:**

- Sistem menolak file.
- Sistem memberikan pesan kesalahan yang jelas.
- File tidak disimpan sebagai dokumen pendaftaran yang valid.

### TC-DOC-003 — Unggah File Melebihi Batas Ukuran

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Boundary Testing|

**Langkah Pengujian:**

1. Siapkan file yang ukurannya melebihi batas konfigurasi.
2. Coba unggah file tersebut.

**Hasil yang Diharapkan:**

- Sistem menolak file.
- Pesan kesalahan menjelaskan batas ukuran sesuai konfigurasi.
- Server tidak memproses file secara berlebihan.
- Respons mengikuti kontrak API, misalnya HTTP `413` jika digunakan untuk payload yang terlalu besar.

### TC-DOC-004 — Mengakses Dokumen Milik Pengguna Lain

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Security Testing|

**Langkah Pengujian:**

1. Login sebagai calon mahasiswa pertama.
2. Dapatkan ID dokumen milik calon mahasiswa kedua melalui data pengujian.
3. Coba mengunduh dokumen tersebut menggunakan akun pertama.

**Hasil yang Diharapkan:**

- Sistem menolak akses yang tidak berhak.
- Isi dokumen tidak dikirim kepada pengguna pertama.
- Pemeriksaan kepemilikan dilakukan pada backend.

### TC-DOC-005 — Mengunduh Dokumen oleh Panitia Berwenang

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Authorization Testing|
|Prasyarat|Panitia sudah login dan memiliki kewenangan memeriksa pendaftaran terkait.|

**Langkah Pengujian:**

1. Login sebagai panitia.
2. Buka detail pendaftaran yang boleh diperiksa.
3. Pilih dokumen yang diperlukan.
4. Unduh dokumen.

**Hasil yang Diharapkan:**

- Panitia yang berwenang dapat mengakses dokumen.
- Dokumen yang diunduh sesuai dengan pendaftaran yang diperiksa.
- Akses dapat dicatat sesuai kebijakan audit sistem.

### TC-DOC-006 — Mengunggah Dokumen dengan Ekstensi Palsu

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Security Testing|

**Langkah Pengujian:**

1. Siapkan file yang isi sebenarnya tidak sesuai dengan ekstensi yang digunakan.
2. Ubah nama file agar terlihat seperti format yang diizinkan.
3. Coba unggah file tersebut.

**Hasil yang Diharapkan:**

- Sistem tidak hanya mengandalkan ekstensi nama file.
- Validasi memeriksa tipe file aktual menggunakan mekanisme yang sesuai.
- File yang berbahaya atau tidak valid ditolak.
- File yang diterima tetap disimpan secara privat.

---

## 12. Test Cases Status dan Riwayat

### TC-STATUS-001 — Melihat Status Pendaftaran Sendiri

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Functional Testing|

**Langkah Pengujian:**

1. Login sebagai calon mahasiswa.
2. Buka halaman status pendaftaran.

**Hasil yang Diharapkan:**

- Status pendaftaran ditampilkan dengan benar.
- Informasi status sesuai dengan data backend.
- Pengguna hanya melihat pendaftaran miliknya sendiri.

### TC-STATUS-002 — Melihat Riwayat Status

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Functional Testing|

**Langkah Pengujian:**

1. Buka detail pendaftaran.
2. Pilih bagian riwayat status.

**Hasil yang Diharapkan:**

- Sistem menampilkan riwayat perubahan status yang tersedia.
- Riwayat mencatat status sebelumnya, status baru, waktu perubahan, dan catatan jika ada.
- Riwayat ditampilkan sesuai urutan waktu yang konsisten.

### TC-STATUS-003 — Mencegah Perubahan Status yang Tidak Valid

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Business Rule Testing|

**Langkah Pengujian:**

1. Gunakan akun atau request yang mencoba mengubah status secara langsung ke status akhir tanpa melalui proses yang diizinkan.
2. Periksa respons backend dan data database.

**Hasil yang Diharapkan:**

- Backend menolak perubahan status yang tidak diizinkan.
- Status lama tetap terjaga.
- Tidak ada riwayat palsu yang tercatat.
- Transisi status hanya dapat dilakukan oleh role yang berwenang sesuai aturan bisnis.

### TC-STATUS-004 — Riwayat Status Tidak Dapat Diubah oleh Calon Mahasiswa

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Security Testing|

**Langkah Pengujian:**

1. Login sebagai calon mahasiswa.
2. Coba mengubah atau menghapus riwayat status melalui request API.

**Hasil yang Diharapkan:**

- Sistem menolak perubahan.
- Riwayat yang sudah tersimpan tidak berubah.
- Perubahan status hanya dilakukan melalui alur bisnis yang sah.

---

## 13. Test Cases Pemeriksaan Panitia

### TC-REVIEW-001 — Panitia Melihat Daftar Pendaftaran

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Functional Testing|
|Prasyarat|Panitia sudah login dan memiliki akses ke daftar pendaftaran.|

**Langkah Pengujian:**

1. Buka halaman daftar pendaftaran.
2. Periksa daftar yang ditampilkan.
3. Buka salah satu detail pendaftaran.

**Hasil yang Diharapkan:**

- Daftar pendaftaran ditampilkan sesuai hak akses.
- Informasi utama dapat dilihat.
- Detail yang ditampilkan sesuai dengan pendaftaran yang dipilih.

### TC-REVIEW-002 — Panitia Memulai Pemeriksaan

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|

**Langkah Pengujian:**

1. Pilih pendaftaran berstatus `submitted`.
2. Jalankan tindakan untuk memulai pemeriksaan.

**Hasil yang Diharapkan:**

- Status berubah menjadi `review` jika transisi tersebut diizinkan.
- Perubahan tercatat pada riwayat status.
- Identitas pengguna yang melakukan perubahan dicatat sesuai mekanisme audit.

### TC-REVIEW-003 — Meminta Revisi Dokumen atau Data

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|

**Langkah Pengujian:**

1. Buka pendaftaran yang sedang diperiksa.
2. Pilih tindakan meminta revisi.
3. Masukkan catatan revisi.
4. Kirim tindakan tersebut.

**Hasil yang Diharapkan:**

- Status berubah menjadi `revision` jika sesuai aturan transisi.
- Catatan revisi tersimpan.
- Riwayat status tercatat.
- Calon mahasiswa dapat melihat informasi revisi pada pendaftarannya.

### TC-REVIEW-004 — Meminta Revisi tanpa Catatan yang Wajib

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Negative Testing|

**Langkah Pengujian:**

1. Pilih tindakan meminta revisi.
2. Kosongkan catatan jika catatan diwajibkan.
3. Kirim permintaan.

**Hasil yang Diharapkan:**

- Sistem menolak permintaan yang tidak memenuhi validasi.
- Status tidak berubah.
- Pesan validasi ditampilkan.

### TC-REVIEW-005 — Calon Mahasiswa Tidak Dapat Melakukan Review Panitia

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Security Testing|

**Langkah Pengujian:**

1. Login sebagai calon mahasiswa.
2. Coba memanggil endpoint pemeriksaan pendaftaran.
3. Coba mengubah status pendaftaran.

**Hasil yang Diharapkan:**

- Sistem menolak akses yang tidak berhak.
- Data pendaftaran tidak berubah.
- Endpoint tetap terlindungi meskipun dipanggil langsung tanpa melalui UI.

---

## 14. Test Cases Keputusan Penerimaan

### TC-DECISION-001 — Panitia Berwenang Menetapkan Diterima

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Pendaftaran telah melalui tahapan yang diperlukan dan pengguna berwenang mengambil keputusan.|

**Langkah Pengujian:**

1. Login menggunakan akun dengan kewenangan mengambil keputusan.
2. Buka pendaftaran yang memenuhi persyaratan proses.
3. Pilih keputusan diterima.
4. Konfirmasi tindakan jika tersedia.

**Hasil yang Diharapkan:**

- Status berubah menjadi `accepted` jika transisi diperbolehkan.
- Keputusan dan waktu perubahan tersimpan.
- Riwayat status dibuat.
- Keputusan dilakukan oleh pengguna berwenang, bukan oleh proses otomatis penilaian akademik.

### TC-DECISION-002 — Panitia Berwenang Menetapkan Ditolak

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Positive Testing|
|Prasyarat|Pendaftaran memenuhi kondisi proses yang memungkinkan keputusan penolakan.|

**Langkah Pengujian:**

1. Login menggunakan akun yang berwenang mengambil keputusan.
2. Buka pendaftaran yang akan diputuskan.
3. Pilih keputusan ditolak.
4. Konfirmasi tindakan.

**Hasil yang Diharapkan:**

- Status berubah menjadi `rejected` jika transisi diizinkan.
- Riwayat keputusan tercatat.
- Keputusan tidak dapat dilakukan oleh role yang tidak memiliki kewenangan.

### TC-DECISION-003 — Role Tidak Berwenang Mengubah Keputusan

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Security Testing|

**Langkah Pengujian:**

1. Login sebagai calon mahasiswa atau role lain yang tidak memiliki kewenangan mengambil keputusan.
2. Kirim request untuk menetapkan status `accepted` atau `rejected`.

**Hasil yang Diharapkan:**

- Backend menolak request.
- Status pendaftaran tidak berubah.
- Tidak ada riwayat keputusan palsu.

### TC-DECISION-004 — Mengubah Keputusan yang Sudah Final

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Business Rule Testing|

**Langkah Pengujian:**

1. Siapkan pendaftaran berstatus `accepted` atau `rejected`.
2. Coba ubah keputusan tersebut menjadi status final yang berlawanan.
3. Periksa respons backend.

**Hasil yang Diharapkan:**

- Sistem mengikuti kebijakan perubahan keputusan yang telah ditetapkan.
- Jika perubahan keputusan tidak diizinkan, request ditolak.
- Jika perubahan diperbolehkan melalui prosedur khusus, perubahan hanya dilakukan oleh role berwenang dan tercatat lengkap dalam audit.
- Keputusan final tidak dapat diubah hanya melalui manipulasi request biasa.

**Catatan:** Kebijakan resmi mengenai perubahan keputusan final masih perlu ditetapkan oleh pengelola PMB.

---

## 15. Test Cases Admin

### TC-ADMIN-001 — Admin Mengelola Gelombang

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Functional Testing|

**Langkah Pengujian:**

1. Login sebagai admin.
2. Buka pengelolaan gelombang.
3. Tambah atau ubah gelombang sesuai kewenangan.
4. Simpan perubahan.

**Hasil yang Diharapkan:**

- Perubahan tersimpan jika valid.
- Data gelombang ditampilkan sesuai perubahan.
- Periode yang tidak valid ditolak.
- Perubahan memengaruhi pilihan gelombang sesuai aturan sistem.

### TC-ADMIN-002 — Admin Mengelola Program Studi

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Functional Testing|

**Langkah Pengujian:**

1. Login sebagai admin.
2. Buka pengelolaan program studi.
3. Tambah atau ubah program studi.
4. Simpan perubahan.

**Hasil yang Diharapkan:**

- Program studi tersimpan jika datanya valid.
- Program studi aktif dapat ditampilkan kepada calon mahasiswa sesuai aturan.
- Program studi yang dinonaktifkan tidak dapat dipilih untuk pendaftaran baru jika kebijakan sistem mengharuskannya.

### TC-ADMIN-003 — Admin Mengelola Akun Pengguna

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Authorization Testing|

**Langkah Pengujian:**

1. Login sebagai admin.
2. Buka pengelolaan pengguna.
3. Lakukan tindakan yang diizinkan, misalnya melihat atau menonaktifkan akun.
4. Periksa perubahan.

**Hasil yang Diharapkan:**

- Tindakan yang diizinkan dapat dilakukan.
- Perubahan status akun tersimpan.
- Sistem tidak memberikan hak akses melebihi kewenangan admin yang ditetapkan.

### TC-ADMIN-004 — Calon Mahasiswa Mengakses Menu Admin

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Security Testing|

**Langkah Pengujian:**

1. Login sebagai calon mahasiswa.
2. Coba membuka halaman admin.
3. Coba memanggil endpoint admin secara langsung.

**Hasil yang Diharapkan:**

- Akses ditolak.
- Data administratif tidak ditampilkan.
- Backend memeriksa role pada setiap request yang dilindungi.

---

## 16. Test Cases Dashboard dan Laporan

### TC-REPORT-001 — Menampilkan Statistik Pendaftaran

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Functional Testing|

**Langkah Pengujian:**

1. Login menggunakan akun yang berhak melihat dashboard.
2. Buka halaman dashboard.
3. Bandingkan angka statistik dengan data pengujian di database.

**Hasil yang Diharapkan:**

- Statistik sesuai dengan data sumber.
- Perhitungan berdasarkan kriteria yang ditentukan.
- Data tidak dihitung ganda.
- Role yang tidak berhak tidak dapat mengakses dashboard.

### TC-REPORT-002 — Filter Laporan Berdasarkan Gelombang

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Functional Testing|

**Langkah Pengujian:**

1. Buka halaman laporan.
2. Pilih gelombang tertentu.
3. Terapkan filter.
4. Bandingkan hasil dengan data yang tersedia.

**Hasil yang Diharapkan:**

- Laporan hanya menampilkan data sesuai gelombang yang dipilih.
- Statistik mengikuti filter.
- Data dari gelombang lain tidak tercampur.

### TC-REPORT-003 — Unduh Laporan PDF

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Functional Testing|

**Langkah Pengujian:**

1. Buka halaman laporan.
2. Pilih kriteria laporan.
3. Tekan tombol unduh PDF.
4. Buka file yang dihasilkan.

**Hasil yang Diharapkan:**

- File PDF berhasil dihasilkan.
- Isi laporan sesuai filter dan data.
- Format file dapat dibuka.
- Informasi sensitif hanya disertakan sesuai kewenangan dan kebutuhan laporan.

### TC-REPORT-004 — Pimpinan Mengakses Laporan

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Authorization Testing|

**Langkah Pengujian:**

1. Login sebagai pimpinan.
2. Buka dashboard dan laporan.
3. Coba mengakses dokumen pribadi calon mahasiswa jika akses tersebut tidak diberikan kepada role pimpinan.

**Hasil yang Diharapkan:**

- Pimpinan dapat melihat ringkasan dan laporan sesuai kewenangannya.
- Akses ke dokumen pribadi ditolak jika tidak memiliki izin khusus.
- Hak akses tidak otomatis meluas hanya karena pengguna memiliki role pimpinan.

---

## 17. Test Cases REST API

### TC-API-001 — Request Tanpa Token Autentikasi

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|API Security Testing|

**Langkah Pengujian:**

1. Kirim request ke endpoint yang membutuhkan autentikasi tanpa token.
2. Periksa respons.

**Hasil yang Diharapkan:**

- Server menolak request.
- Respons menggunakan HTTP `401 Unauthorized` sesuai kontrak API.
- Data yang dilindungi tidak dikirim.

### TC-API-002 — Request dengan Data Tidak Valid

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|API Validation Testing|

**Langkah Pengujian:**

1. Kirim request dengan field wajib yang hilang.
2. Kirim request dengan tipe data yang salah.
3. Periksa respons API.

**Hasil yang Diharapkan:**

- Backend melakukan validasi.
- Respons berisi kode status dan format error sesuai kontrak API.
- Data tidak valid tidak disimpan.
- Detail internal server tidak ditampilkan kepada pengguna.

### TC-API-003 — Request dengan ID Tidak Ditemukan

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Negative Testing|

**Langkah Pengujian:**

1. Kirim request untuk mengakses ID pendaftaran yang tidak tersedia.
2. Periksa respons.

**Hasil yang Diharapkan:**

- Sistem memberikan respons yang sesuai, misalnya HTTP `404 Not Found`.
- Tidak terjadi perubahan database.
- Informasi internal tidak bocor.

### TC-API-004 — Rate Limiting

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Security/Resilience Testing|

**Langkah Pengujian:**

1. Kirim request berulang ke endpoint yang memiliki rate limiting.
2. Lanjutkan hingga melewati batas yang dikonfigurasi.
3. Periksa respons.

**Hasil yang Diharapkan:**

- Request yang melebihi batas ditangani sesuai konfigurasi.
- Sistem mengembalikan respons seperti HTTP `429 Too Many Requests` jika berlaku.
- Request normal tetap dapat diproses setelah periode pembatasan berakhir.

### TC-API-005 — Format Respons Error Konsisten

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|API Contract Testing|

**Langkah Pengujian:**

1. Jalankan beberapa request yang menghasilkan kesalahan berbeda.
2. Periksa struktur JSON respons.

**Hasil yang Diharapkan:**

- Respons mengikuti format error yang ditetapkan pada API Contract.
- Kode error dapat digunakan aplikasi Android untuk menangani kondisi yang terjadi.
- Pesan error tidak mengandung stack trace, password, token, atau query database.

Contoh format yang diharapkan:

```
{
  "success": false,
  "message": "Permintaan tidak dapat diproses.",
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {}
  }
}
```

Contoh tersebut merupakan acuan format, bukan jaminan bahwa implementasi saat ini sudah menghasilkan respons tersebut.

---

## 18. Test Cases Database dan Integritas Data

### TC-DB-001 — Integritas Foreign Key

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Database Testing|

**Langkah Pengujian:**

1. Coba membuat pendaftaran dengan ID calon mahasiswa yang tidak tersedia.
2. Coba menggunakan ID gelombang yang tidak tersedia.
3. Coba menggunakan ID program studi yang tidak tersedia.

**Hasil yang Diharapkan:**

- Data yang mengacu pada entitas yang tidak tersedia ditolak.
- Database mempertahankan integritas relasi.
- Tidak tercipta pendaftaran dengan referensi yang tidak valid.

### TC-DB-002 — Integritas Unique Constraint

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Database Testing|

**Langkah Pengujian:**

1. Buat pendaftaran dengan pasangan `applicant_id` dan `wave_id` tertentu.
2. Coba membuat baris kedua dengan pasangan yang sama.
3. Periksa hasilnya.

**Hasil yang Diharapkan:**

- Database menolak data duplikat.
- Constraint unik berlaku meskipun request berasal dari jalur yang berbeda.
- Backend menangani kesalahan database dengan aman.

### TC-DB-003 — Transaksi Pengiriman Pendaftaran

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Transaction Testing|

**Langkah Pengujian:**

1. Jalankan proses pengiriman pendaftaran yang memerlukan perubahan status dan pencatatan riwayat.
2. Simulasikan kegagalan pada salah satu langkah penyimpanan.
3. Periksa data setelah transaksi selesai atau gagal.

**Hasil yang Diharapkan:**

- Operasi yang seharusnya atomik tidak meninggalkan perubahan parsial.
- Jika transaksi gagal, perubahan terkait dibatalkan.
- Jika transaksi berhasil, data pendaftaran dan riwayat status konsisten.

### TC-DB-004 — Konsistensi Riwayat Status

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Data Integrity Testing|

**Langkah Pengujian:**

1. Lakukan perubahan status melalui alur aplikasi yang valid.
2. Periksa status terkini pada tabel `applications`.
3. Periksa riwayat pada tabel `application_status_histories`.

**Hasil yang Diharapkan:**

- Status terkini sesuai dengan perubahan yang berhasil.
- Riwayat mencatat transisi status yang benar.
- Riwayat tidak dibuat untuk perubahan yang gagal.

### TC-DB-005 — Penghapusan Data yang Memiliki Riwayat

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Data Integrity Testing|

**Langkah Pengujian:**

1. Siapkan pendaftaran yang memiliki riwayat status.
2. Uji tindakan penghapusan melalui mekanisme administrasi yang tersedia.

**Hasil yang Diharapkan:**

- Penghapusan mengikuti kebijakan retensi dan audit.
- Riwayat penting tidak terhapus tanpa sengaja.
- Relasi database tetap konsisten.

**Catatan:** Kebijakan penghapusan permanen, soft delete, dan masa retensi harus ditentukan sebelum pengujian ini dijadikan kriteria final.

---

## 19. Test Cases Antarmuka Pengguna

### TC-UI-001 — Validasi Formulir

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|UI Testing|

**Langkah Pengujian:**

1. Buka formulir pendaftaran.
2. Kosongkan field wajib.
3. Tekan tombol kirim.

**Hasil yang Diharapkan:**

- Field yang bermasalah ditandai dengan jelas.
- Pesan validasi mudah dipahami.
- Pengguna dapat memperbaiki input tanpa kehilangan data yang sudah diisi jika memungkinkan.

### TC-UI-002 — Navigasi Halaman

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|UI Testing|

**Langkah Pengujian:**

1. Login sebagai calon mahasiswa.
2. Buka dashboard.
3. Navigasikan ke profil, pendaftaran, dokumen, dan status.
4. Kembali ke dashboard.

**Hasil yang Diharapkan:**

- Setiap menu membuka halaman yang sesuai.
- Tidak ada halaman kosong akibat navigasi normal.
- Pengguna dapat kembali tanpa kehilangan informasi penting yang belum disimpan, jika fitur mendukungnya.

### TC-UI-003 — Tampilan pada Ukuran Layar Berbeda

|Atribut|Keterangan|
|---|---|
|Prioritas|P3|
|Jenis|Compatibility Testing|

**Langkah Pengujian:**

1. Jalankan aplikasi pada beberapa ukuran layar.
2. Buka halaman login, formulir, unggah dokumen, dan status.
3. Periksa tata letak dan keterbacaan.

**Hasil yang Diharapkan:**

- Teks dan tombol dapat dibaca serta digunakan.
- Elemen penting tidak terpotong.
- Pengguna tetap dapat menyelesaikan proses utama pada ukuran layar yang diuji.

### TC-UI-004 — Tampilan Status Pendaftaran

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|UI/Functional Testing|

**Langkah Pengujian:**

1. Siapkan pendaftaran dengan status yang berbeda.
2. Login sebagai pemilik pendaftaran.
3. Buka halaman status.

**Hasil yang Diharapkan:**

- Status yang tampil sesuai data backend.
- Informasi status tidak hanya dibedakan dengan warna, tetapi juga dengan teks.
- Informasi revisi atau keputusan ditampilkan sesuai data yang tersedia.

---

## 20. Test Cases Pemulihan dan Koneksi

### TC-RES-001 — Koneksi Internet Terputus saat Pengiriman

|Atribut|Keterangan|
|---|---|
|Prioritas|P1|
|Jenis|Resilience Testing|

**Langkah Pengujian:**

1. Lengkapi formulir pendaftaran.
2. Putuskan koneksi jaringan sebelum atau saat request dikirim.
3. Periksa respons aplikasi.
4. Pulihkan koneksi dan periksa status pendaftaran pada server.

**Hasil yang Diharapkan:**

- Aplikasi menampilkan informasi bahwa request gagal atau hasilnya belum dapat dipastikan.
- Aplikasi tidak menyatakan pendaftaran berhasil tanpa konfirmasi server.
- Setelah koneksi pulih, pengguna dapat memeriksa status aktual.
- Percobaan ulang tidak menghasilkan pendaftaran ganda.

**Catatan:** Jika server sudah memproses request tetapi respons tidak sampai ke aplikasi, pengguna dapat mengalami kondisi hasil yang tidak pasti. Sistem perlu memeriksa data yang sudah tersimpan sebelum mengulangi pengiriman.

### TC-RES-002 — Server Tidak Tersedia

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Resilience Testing|

**Langkah Pengujian:**

1. Hentikan server backend lokal.
2. Buka aplikasi Android.
3. Coba memuat daftar gelombang atau status pendaftaran.

**Hasil yang Diharapkan:**

- Aplikasi menampilkan pesan kegagalan koneksi yang mudah dipahami.
- Aplikasi tidak mengalami crash.
- Pengguna dapat mencoba kembali setelah server aktif.

### TC-RES-003 — Respons Server Terlambat

|Atribut|Keterangan|
|---|---|
|Prioritas|P2|
|Jenis|Resilience Testing|

**Langkah Pengujian:**

1. Simulasikan respons API yang lambat.
2. Jalankan permintaan melalui aplikasi.
3. Amati tampilan dan perilaku aplikasi.

**Hasil yang Diharapkan:**

- Aplikasi memberikan indikator proses ketika diperlukan.
- Request mengikuti batas waktu yang dikonfigurasi.
- Pesan kegagalan ditampilkan jika batas waktu terlampaui.
- Aplikasi tidak membuat pengiriman berulang tanpa kendali.

---

## 21. Matriks Keterlacakan Pengujian

Matriks berikut menghubungkan kelompok kebutuhan utama dengan test case yang mengujinya.

|Kebutuhan|Deskripsi|Test Case|
|---|---|---|
|FR-01|Registrasi dan login|TC-AUTH-001 sampai TC-AUTH-006|
|FR-02|Pengelolaan profil|TC-PROFILE-001 sampai TC-PROFILE-004|
|FR-03|Informasi gelombang|TC-WAVE-001 sampai TC-WAVE-003|
|FR-04|Pembuatan dan pengiriman pendaftaran|TC-APP-001 sampai TC-APP-007|
|FR-05|Unggah dan akses dokumen|TC-DOC-001 sampai TC-DOC-006|
|FR-06|Pemantauan status|TC-STATUS-001 sampai TC-STATUS-004|
|FR-07|Pemeriksaan pendaftaran|TC-REVIEW-001 sampai TC-REVIEW-005|
|FR-08|Keputusan penerimaan|TC-DECISION-001 sampai TC-DECISION-004|
|FR-09|Pengelolaan data administratif|TC-ADMIN-001 sampai TC-ADMIN-004|
|FR-10|Dashboard dan laporan|TC-REPORT-001 sampai TC-REPORT-004|
|NFR-01|Keamanan dan kontrol akses|TC-PROFILE-004, TC-DOC-004, TC-DECISION-003, TC-ADMIN-004, TC-API-001|
|NFR-02|Integritas data|TC-APP-004, TC-APP-005, TC-DB-001 sampai TC-DB-004|
|NFR-03|Ketahanan koneksi|TC-RES-001 sampai TC-RES-003|
|NFR-04|Konsistensi kontrak API|TC-API-002 sampai TC-API-005|

**Catatan:** Kode FR dan NFR di atas adalah label pemetaan awal. Samakan kode tersebut dengan ID kebutuhan yang sebenarnya pada `SRS.md` dan `Traceability Matrix.md` sebelum laporan pengujian final dibuat.

---

## 22. Format Pencatatan Hasil Pengujian

Gunakan tabel berikut untuk mencatat hasil setiap pengujian yang benar-benar dijalankan.

|Test Case ID|Tanggal|Penguji|Hasil Aktual|Status|Bukti/Catatan|
|---|---|---|---|---|---|
|TC-AUTH-001|Belum diuji|-|-|Not Run|-|
|TC-AUTH-003|Belum diuji|-|-|Not Run|-|
|TC-APP-002|Belum diuji|-|-|Not Run|-|
|TC-APP-004|Belum diuji|-|-|Not Run|-|
|TC-DOC-001|Belum diuji|-|-|Not Run|-|
|TC-STATUS-001|Belum diuji|-|-|Not Run|-|
|TC-REVIEW-003|Belum diuji|-|-|Not Run|-|
|TC-DECISION-001|Belum diuji|-|-|Not Run|-|
|TC-REPORT-003|Belum diuji|-|-|Not Run|-|
|TC-SEC-001|Belum diuji|-|-|Not Run|-|

Status pengujian yang digunakan:

- **Not Run:** Belum dijalankan.
- **Pass:** Hasil aktual sesuai dengan hasil yang diharapkan.
- **Fail:** Hasil aktual tidak sesuai dengan hasil yang diharapkan.
- **Blocked:** Pengujian tidak dapat dilanjutkan karena ada ketergantungan atau masalah lain.

`TC-SEC-001` pada tabel pencatatan merupakan placeholder untuk pengujian keamanan tambahan. Jika digunakan, definisikan skenario lengkapnya atau gunakan ID keamanan yang sudah ada, seperti `TC-API-001` dan `TC-PROFILE-004`.

Hasil aktual dan status hanya boleh diisi setelah pengujian benar-benar dilakukan. Jangan menetapkan `Pass` berdasarkan perkiraan atau hanya karena skenario pengujian sudah ditulis.

---

## 23. Kriteria Kelulusan Pengujian

Sistem dapat dipertimbangkan siap untuk tahap pengujian berikutnya apabila:

1. Seluruh test case prioritas P1 telah dijalankan.
2. Seluruh pengujian kritis keamanan dan integritas data lulus.
3. Tidak ada bug kritis yang belum diselesaikan.
4. Proses registrasi, pengiriman pendaftaran, unggah dokumen, dan pemantauan status berjalan sesuai kebutuhan.
5. Sistem mencegah pendaftaran duplikat dalam gelombang yang sama, termasuk pada request bersamaan.
6. Hak akses setiap role berjalan sesuai matriks akses.
7. Perubahan status dan keputusan dicatat secara konsisten.
8. Pengujian integrasi antara Android, backend, dan database berhasil.
9. Test case yang gagal telah dianalisis dan ditindaklanjuti.
10. Bukti pengujian tersedia untuk mendukung laporan hasil pengujian.

Persentase kelulusan dapat dihitung menggunakan rumus:

**Persentase kelulusan = (Jumlah test case berstatus Pass ÷ Jumlah test case yang sudah dijalankan) × 100%**

Test case berstatus `Not Run` dan `Blocked` tidak boleh dihitung sebagai lulus. Persentase yang tinggi juga tidak otomatis menunjukkan sistem aman jika masih terdapat kegagalan kritis.

## 24. Kesimpulan

Test Cases PMB ITB Akiday menjadi acuan untuk memeriksa fungsi utama sistem, mulai dari autentikasi, pengelolaan profil, pendaftaran, dokumen, pemeriksaan panitia, keputusan penerimaan, hingga laporan. Pengujian negatif dan keamanan juga diperlukan agar sistem tidak hanya berjalan pada kondisi normal, tetapi mampu menolak input tidak valid dan akses yang tidak berwenang.

Perhatian khusus diberikan pada pencegahan pendaftaran ganda dalam gelombang yang sama, penanganan request bersamaan, perlindungan dokumen pribadi, dan konsistensi riwayat status. Seluruh hasil pengujian harus dicatat berdasarkan pelaksanaan nyata agar dapat menjadi dasar evaluasi dan perbaikan sebelum sistem digunakan.