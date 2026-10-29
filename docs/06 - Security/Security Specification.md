**Proyek:** PMB ITB Akiday  
**Versi:** 1.0  
**Status:** Draft untuk implementasi  
**Folder Obsidian:** `06 - Security/Security Specification.md`

---

## 1. Pendahuluan

### 1.1 Tujuan

Dokumen Security Specification menjelaskan persyaratan keamanan yang harus diterapkan pada sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday. Sistem mengelola informasi calon mahasiswa, data pendaftaran, dokumen persyaratan, hasil verifikasi, dan keputusan penerimaan. Oleh karena itu, keamanan diperlukan untuk mencegah akses tanpa izin, kebocoran data pribadi, manipulasi pendaftaran, dan penyalahgunaan fitur.

Dokumen ini menjadi pedoman bagi pengembang saat menerapkan autentikasi, otorisasi, perlindungan data, keamanan API, pengamanan dokumen, pencatatan aktivitas, serta pengujian keamanan.

### 1.2 Ruang Lingkup

Persyaratan keamanan mencakup komponen berikut:

1. Aplikasi Android untuk calon mahasiswa.
2. Website administrasi untuk admin, panitia, dan pimpinan.
3. Backend API menggunakan Laravel.
4. Database MySQL.
5. Penyimpanan dokumen pendaftaran.
6. Proses autentikasi dan otorisasi.
7. Proses pendaftaran, verifikasi, revisi, dan keputusan penerimaan.
8. Dashboard, laporan, serta ekspor data.
9. Konfigurasi server dan lingkungan pengembangan.
10. Audit log, backup, dan pemulihan data.

### 1.3 Teknologi yang Digunakan

|Komponen|Teknologi|Fokus Keamanan|
|---|---|---|
|Aplikasi Android|Kotlin|Penyimpanan token, validasi input, keamanan komunikasi API|
|Website administrasi|Teknologi frontend yang dipilih|Pengelolaan sesi, perlindungan formulir, pembatasan akses|
|Backend|Laravel|Autentikasi, otorisasi, validasi, pengamanan API|
|Database|MySQL|Integritas data, hak akses database, transaksi|
|API|REST API dengan JSON|Autentikasi request, validasi data, pembatasan request|
|Penyimpanan dokumen|Private storage|Pembatasan akses dan validasi file|
|Deployment awal|Server lokal|Pembatasan akses jaringan, konfigurasi aman, backup|

Teknologi tersebut merupakan rancangan dasar. Detail implementasi harus mengikuti versi framework dan konfigurasi yang benar-benar digunakan.

---

## 2. Prinsip Keamanan

Sistem PMB ITB Akiday menerapkan prinsip berikut.

### 2.1 Confidentiality

Kerahasiaan memastikan bahwa data hanya dapat diakses oleh pengguna yang memiliki hak akses.

Contoh penerapan:

- Calon mahasiswa hanya dapat melihat data pendaftarannya sendiri.
- Dokumen pribadi tidak dapat diakses melalui URL publik.
- Pimpinan hanya melihat informasi yang diperlukan untuk pelaporan.
- Data sensitif tidak dicantumkan secara berlebihan dalam log atau laporan.

### 2.2 Integrity

Integritas memastikan bahwa data tidak dapat diubah oleh pihak yang tidak berwenang dan tetap konsisten selama proses pengolahan.

Contoh penerapan:

- Status pendaftaran hanya dapat diubah melalui proses yang diizinkan.
- Pendaftaran ganda pada gelombang yang sama dicegah oleh batasan database.
- Perubahan status dicatat dalam riwayat.
- Operasi yang saling berkaitan dijalankan menggunakan transaksi database.

### 2.3 Availability

Ketersediaan memastikan bahwa sistem dan data dapat digunakan oleh pengguna yang berhak ketika diperlukan.

Contoh penerapan:

- Melakukan backup database dan dokumen.
- Membatasi request berlebihan yang dapat membebani server.
- Menyediakan prosedur pemulihan ketika terjadi kegagalan.
- Menangani gangguan jaringan dan kegagalan unggah secara aman.

### 2.4 Least Privilege

Setiap pengguna hanya memperoleh hak akses minimum yang diperlukan untuk menjalankan tugasnya.

Admin, panitia, dan pimpinan memiliki kebutuhan yang berbeda. Oleh karena itu, sistem tidak boleh memberikan seluruh hak akses hanya karena pengguna merupakan bagian dari pengelola PMB.

### 2.5 Defense in Depth

Keamanan harus diterapkan pada beberapa lapisan, seperti aplikasi, backend, database, penyimpanan file, dan server.

Sebagai contoh, menyembunyikan tombol keputusan penerimaan dari calon mahasiswa tidak cukup. Backend juga harus memeriksa hak akses setiap kali endpoint keputusan dipanggil.

---

## 3. Identifikasi Aset dan Ancaman

### 3.1 Aset yang Harus Dilindungi

|ID|Aset|Risiko Jika Terganggu|
|---|---|---|
|AST-01|Akun pengguna|Pengambilalihan akun|
|AST-02|Password pengguna|Akses tanpa izin|
|AST-03|Data profil calon mahasiswa|Kebocoran informasi pribadi|
|AST-04|Data pendaftaran|Manipulasi atau kehilangan data|
|AST-05|Dokumen pendaftaran|Kebocoran atau perubahan dokumen|
|AST-06|Status pendaftaran|Perubahan status secara ilegal|
|AST-07|Keputusan penerimaan|Manipulasi hasil seleksi|
|AST-08|Data gelombang dan program studi|Kesalahan konfigurasi pendaftaran|
|AST-09|Database|Kehilangan atau kerusakan data|
|AST-10|Token dan sesi autentikasi|Penggunaan akun oleh pihak lain|
|AST-11|Audit log|Hilangnya bukti aktivitas|
|AST-12|File backup|Kebocoran atau kehilangan salinan data|

### 3.2 Ancaman Keamanan

|ID|Ancaman|Dampak|Mitigasi Utama|
|---|---|---|---|
|THR-01|Password lemah atau bocor|Akun diambil alih|Hash password, kebijakan password, pembatasan login|
|THR-02|Akses data pengguna lain|Kebocoran data|Pemeriksaan kepemilikan pada backend|
|THR-03|Manipulasi request API|Perubahan data ilegal|Otorisasi dan validasi server|
|THR-04|SQL Injection|Pembacaan atau perubahan database|Query terparameterisasi dan ORM|
|THR-05|Cross-Site Scripting (XSS)|Eksekusi skrip berbahaya pada browser|Output encoding dan sanitasi sesuai konteks|
|THR-06|Cross-Site Request Forgery (CSRF)|Tindakan tanpa persetujuan pengguna|Perlindungan CSRF untuk autentikasi berbasis cookie|
|THR-07|Unggahan file berbahaya|Penyalahgunaan server atau kebocoran data|Validasi file dan private storage|
|THR-08|Brute-force login|Pengambilalihan akun atau gangguan layanan|Rate limiting dan pemantauan|
|THR-09|Pendaftaran ganda|Data pendaftaran tidak konsisten|Unique constraint dan transaksi|
|THR-10|Manipulasi keputusan|Hasil penerimaan tidak sah|Pembatasan kewenangan dan audit log|
|THR-11|Kebocoran token|Penggunaan sesi tanpa izin|Penyimpanan token yang aman dan pencabutan token|
|THR-12|Kerusakan server atau database|Layanan tidak tersedia|Backup dan prosedur pemulihan|
|THR-13|Kebocoran melalui laporan|Data pribadi tersebar|Pembatasan ekspor dan minimisasi data|
|THR-14|Akses fisik atau jaringan lokal tanpa izin|Akses sistem atau database|Firewall, pembatasan jaringan, dan pengamanan server|

---

## 4. Persyaratan Autentikasi

### 4.1 Registrasi Akun

Sistem harus memastikan bahwa proses registrasi memenuhi ketentuan berikut:

1. Pengguna memasukkan email dan password sesuai format yang ditetapkan.
2. Email dinormalisasi secara konsisten sebelum diperiksa dan disimpan.
3. Sistem menolak email yang sudah digunakan sesuai aturan keunikan akun.
4. Password tidak boleh disimpan dalam bentuk teks biasa.
5. Registrasi publik hanya dapat membuat akun dengan role `calon_mahasiswa`.
6. Pengguna tidak boleh menentukan role `admin`, `panitia`, atau `pimpinan` melalui request registrasi.
7. Validasi tetap dilakukan di backend meskipun aplikasi Android telah melakukan validasi awal.

Verifikasi email belum termasuk ruang lingkup MVP. Apabila fitur tersebut ditambahkan, sistem harus menggunakan mekanisme verifikasi yang aman dan memiliki masa berlaku.

### 4.2 Login

Saat pengguna login, sistem harus:

- Memverifikasi email dan password.
- Menolak kredensial yang tidak valid.
- Memastikan akun aktif.
- Membatasi percobaan login berulang.
- Menghindari pesan kesalahan yang mengungkapkan apakah suatu email terdaftar, apabila informasi tersebut dapat disalahgunakan.
- Tidak mengembalikan password atau hash password dalam respons API.

Contoh respons kesalahan:

```
{
  "success": false,
  "message": "Email atau password tidak sesuai.",
  "error": {
    "code": "INVALID_CREDENTIALS",
    "details": {}
  }
}
```

Respons tersebut tidak membedakan antara email yang tidak terdaftar dan password yang salah.

### 4.3 Penyimpanan Password

Password harus diproses menggunakan algoritma hashing password yang sesuai, seperti Argon2id atau bcrypt melalui fasilitas Laravel yang tersedia.

Ketentuan:

1. Password asli tidak disimpan di database.
2. Password tidak dicatat dalam log.
3. Password tidak dikirim kembali dalam respons API.
4. Pengembang tidak boleh membuat algoritma hashing sendiri.
5. Proses perubahan password harus melalui mekanisme autentikasi yang aman.
6. Jika fitur reset password ditambahkan, token reset harus memiliki masa berlaku dan tidak boleh digunakan berulang kali setelah berhasil digunakan.

### 4.4 Pengelolaan Token dan Sesi

Sistem menggunakan autentikasi berbasis token atau mekanisme sesi yang dipilih saat implementasi.

Jika backend menggunakan Laravel Sanctum dengan personal access token, ketentuannya meliputi:

- Token diberikan setelah autentikasi berhasil.
- Token hanya memiliki hak akses sesuai kebutuhan.
- Token tidak disimpan dalam log atau ditampilkan kepada pengguna lain.
- Token dapat dicabut saat logout atau ketika akses perlu dihentikan.
- Token tidak dikirim melalui URL.
- Token hanya dikirim melalui header autentikasi yang sesuai.

Untuk aplikasi Android, token harus disimpan menggunakan mekanisme penyimpanan yang aman, misalnya penyimpanan terenkripsi yang memanfaatkan Android Keystore.

Untuk website administrasi, mekanisme autentikasi harus disesuaikan dengan arsitektur yang dipilih. Jika menggunakan sesi berbasis cookie, cookie harus dikonfigurasi dengan atribut keamanan yang sesuai dan proses logout harus mengakhiri sesi.

**Catatan implementasi:** Laravel Sanctum dapat digunakan dengan pola autentikasi yang berbeda. Konfigurasi token untuk Android tidak sama dengan konfigurasi sesi berbasis cookie untuk website. Pilih dan dokumentasikan mekanisme masing-masing klien sebelum implementasi.

### 4.5 Logout dan Pencabutan Akses

Ketika pengguna logout:

- Sesi harus diakhiri atau token aktif yang bersangkutan dicabut sesuai mekanisme autentikasi.
- Token yang sudah dicabut tidak boleh lagi diterima backend.
- Data sensitif yang tidak diperlukan harus dibersihkan dari penyimpanan sementara aplikasi.
- Jika akun dinonaktifkan, akses berikutnya harus ditolak dan sesi aktif harus ditangani sesuai kebijakan sistem.

---

## 5. Persyaratan Otorisasi

### 5.1 Role-Based Access Control

Sistem menggunakan empat role utama:

- `calon_mahasiswa`
- `admin`
- `panitia`
- `pimpinan`

Hak akses setiap role harus mengikuti dokumen [[Access Control Matrix]].

Backend harus memeriksa role dan izin untuk setiap tindakan yang dilindungi. Pembatasan tampilan pada aplikasi Android atau website hanya berfungsi untuk pengalaman pengguna, bukan sebagai pengamanan utama.

### 5.2 Pemeriksaan Kepemilikan Data

Calon mahasiswa hanya boleh mengakses profil, pendaftaran, dokumen, dan riwayat status miliknya sendiri.

Contoh:

- Pengguna A tidak boleh melihat pendaftaran pengguna B dengan mengganti ID pada URL.
- Pengguna A tidak boleh mengunduh dokumen pengguna B melalui endpoint API.
- Pengguna tidak boleh mengubah data pendaftaran pengguna lain dengan mengirim request manual.

Pemeriksaan kepemilikan harus dilakukan pada backend sebelum data dikembalikan atau perubahan disimpan.

### 5.3 Pembatasan Tindakan

Sistem harus membedakan izin untuk:

- Melihat data.
- Membuat data.
- Mengubah data.
- Menghapus data.
- Memverifikasi pendaftaran.
- Meminta revisi.
- Menetapkan keputusan akhir.
- Mengekspor laporan.

Hak melihat data tidak otomatis memberikan hak untuk mengubah atau menghapusnya.

### 5.4 Perlindungan Keputusan Penerimaan

Sistem boleh melakukan validasi administratif secara otomatis, seperti:

- Memastikan field wajib telah diisi.
- Memastikan format data sesuai aturan.
- Memastikan dokumen wajib tersedia.
- Memeriksa apakah pendaftaran sudah pernah dibuat pada gelombang yang sama.

Namun, sistem tidak boleh otomatis memutuskan calon mahasiswa diterima atau ditolak hanya berdasarkan pemeriksaan tersebut.

Keputusan akhir harus dibuat oleh pihak yang berwenang. Setiap keputusan harus divalidasi di backend dan dicatat dalam riwayat status serta audit log.

---

## 6. Persyaratan Keamanan API

### 6.1 Komunikasi API

Seluruh komunikasi yang melewati jaringan tidak tepercaya harus menggunakan HTTPS dengan konfigurasi TLS yang sesuai.

Untuk pengembangan lokal, HTTP dapat digunakan pada lingkungan yang benar-benar terisolasi. Penggunaan HTTP tidak boleh diteruskan ke deployment produksi yang dapat diakses melalui jaringan publik.

### 6.2 Validasi Input

Semua data yang diterima backend harus divalidasi, termasuk:

- Email.
- NIK.
- Nomor telepon.
- Tanggal lahir.
- Alamat.
- ID gelombang.
- ID program studi.
- Status pendaftaran.
- Metadata dokumen.
- Parameter pencarian dan ekspor laporan.

Validasi pada aplikasi Android tidak menggantikan validasi di Laravel.

Backend harus menolak data yang tidak sesuai format atau aturan bisnis dan memberikan pesan kesalahan yang aman serta mudah dipahami.

### 6.3 SQL Injection

Query database harus dibuat menggunakan Eloquent ORM, Query Builder, atau prepared statement yang aman.

Pengembang tidak boleh menyusun query SQL dengan menggabungkan input pengguna secara langsung ke dalam string query.

### 6.4 XSS dan Output Encoding

Website administrasi harus mencegah data pengguna dieksekusi sebagai skrip pada browser.

Penerapannya meliputi:

- Menggunakan mekanisme escaping bawaan framework frontend atau template.
- Menghindari penyisipan HTML mentah dari input pengguna.
- Memvalidasi dan membersihkan HTML jika suatu fitur memang mengizinkan HTML.
- Menampilkan catatan pendaftaran sebagai teks biasa jika format HTML tidak diperlukan.

### 6.5 CSRF

Perlindungan CSRF harus diterapkan jika website menggunakan autentikasi berbasis cookie dan sesi browser.

Jika API menggunakan token yang dikirim melalui header Authorization dan tidak mengandalkan autentikasi cookie secara otomatis, risiko CSRF berbeda. Konfigurasi akhir harus mengikuti mekanisme autentikasi yang benar-benar dipilih, bukan menonaktifkan perlindungan secara umum.

### 6.6 Rate Limiting

Sistem harus membatasi jumlah request untuk endpoint yang berisiko disalahgunakan, terutama:

- Login.
- Registrasi akun.
- Reset password jika tersedia.
- Pengiriman pendaftaran.
- Pengunggahan dokumen.
- Endpoint pencarian dan ekspor laporan.

Batas request perlu disesuaikan dengan penggunaan sistem. Jika batas tercapai, backend dapat mengembalikan HTTP `429 Too Many Requests`.

### 6.7 Penanganan Kesalahan

Respons API tidak boleh menampilkan:

- Stack trace.
- Query SQL internal.
- Password atau token.
- Informasi konfigurasi server.
- Jalur direktori server yang sensitif.
- Detail internal yang dapat membantu penyerang.

Kesalahan teknis harus dicatat pada log server dengan pengamanan yang sesuai, sedangkan pengguna menerima pesan yang aman.

---

## 7. Persyaratan Keamanan Dokumen

Dokumen pendaftaran merupakan salah satu aset paling sensitif dalam sistem.

### 7.1 Jenis Dokumen

MVP membutuhkan:

- Foto calon mahasiswa.
- Ijazah atau Surat Keterangan Lulus (SKL).

Jenis dokumen tambahan hanya boleh diterapkan setelah kebutuhan resminya dikonfirmasi.

### 7.2 Validasi Unggahan

Backend wajib memeriksa:

1. Apakah jenis dokumen diperbolehkan.
2. Apakah ekstensi file sesuai kebijakan.
3. Apakah MIME type dan isi file sesuai dengan format yang diterima.
4. Apakah ukuran file tidak melebihi batas.
5. Apakah pengguna berhak mengunggah atau mengganti dokumen tersebut.
6. Apakah status pendaftaran mengizinkan perubahan dokumen.

Ekstensi file saja tidak cukup untuk membuktikan bahwa file aman.

Format file yang diizinkan dan batas ukuran maksimum harus ditetapkan sebelum implementasi final.

### 7.3 Penyimpanan Dokumen

Dokumen harus disimpan dalam private storage, bukan direktori publik yang dapat diakses tanpa otorisasi.

Database hanya menyimpan metadata, seperti:

- ID pendaftaran.
- Jenis dokumen.
- Lokasi penyimpanan internal.
- MIME type.
- Ukuran file.
- Waktu unggah.

Database tidak perlu menyimpan isi file biner secara langsung untuk rancangan MVP ini.

### 7.4 Akses dan Unduhan

Setiap permintaan untuk melihat atau mengunduh dokumen harus:

- Memastikan pengguna telah terautentikasi jika dokumen bukan publik.
- Memeriksa role dan kepemilikan pendaftaran.
- Memastikan dokumen terkait dengan pendaftaran yang benar.
- Menolak akses apabila pengguna tidak memiliki izin.
- Tidak mengungkapkan lokasi penyimpanan internal kepada pengguna.

Jika sistem menggunakan tautan unduhan sementara, tautan tersebut harus memiliki masa berlaku yang pendek dan tidak dapat digunakan untuk mengakses dokumen lain.

### 7.5 Penggantian dan Penghapusan Dokumen

Penggantian atau penghapusan dokumen hanya boleh dilakukan pada kondisi yang diizinkan oleh aturan pendaftaran.

Dokumen yang terkait dengan pendaftaran yang telah dikirim atau diputuskan tidak boleh dihapus sembarangan. Jika penghapusan diperlukan, tindakan harus mengikuti kebijakan retensi dan mempertimbangkan kebutuhan audit.

---

## 8. Keamanan Database

### 8.1 Hak Akses Database

Akun database untuk aplikasi harus memiliki hak minimum yang dibutuhkan.

Ketentuan:

- Jangan menggunakan akun administrator database sebagai akun koneksi aplikasi.
- Batasi akses database berdasarkan lingkungan dan kebutuhan.
- Jangan membuka port MySQL ke jaringan publik tanpa alasan yang sah dan kontrol keamanan yang memadai.
- Gunakan akun terpisah untuk pengembangan, pengujian, dan produksi jika memungkinkan.

### 8.2 Perlindungan Kredensial

Informasi koneksi database, seperti username, password, dan host internal, harus disimpan dalam konfigurasi lingkungan yang tidak masuk ke repositori publik.

File `.env` tidak boleh diunggah ke GitHub.

Sebagai gantinya, repository dapat menyediakan file `.env.example` yang berisi nama variabel konfigurasi tanpa password atau rahasia yang sebenarnya.

### 8.3 Integritas Data

Database harus menerapkan:

- Primary key.
- Foreign key.
- Unique constraint.
- Not-null constraint jika diperlukan.
- Validasi status.
- Transaksi untuk operasi yang saling berkaitan.

Untuk mencegah pendaftaran ganda pada gelombang yang sama, database harus memiliki unique constraint pada kombinasi `(applicant_id, wave_id)`.

Validasi di aplikasi tetap diperlukan, tetapi tidak cukup jika tidak disertai pembatasan database.

### 8.4 Transaksi Database

Operasi berikut sebaiknya dijalankan dalam transaksi database:

- Pembuatan pendaftaran beserta nomor registrasi.
- Pengiriman pendaftaran dan pembaruan status.
- Pencatatan riwayat status.
- Penetapan keputusan dan pencatatan audit terkait.

Dengan transaksi, kegagalan pada salah satu bagian operasi dapat ditangani tanpa meninggalkan perubahan database dalam keadaan setengah selesai.

### 8.5 Perlindungan Riwayat

Riwayat status dan audit log tidak boleh diubah atau dihapus oleh pengguna biasa melalui fitur operasional.

Jika diperlukan koreksi, sistem harus mencatat tindakan koreksi secara terpisah agar perubahan tetap dapat ditelusuri.

---

## 9. Perlindungan Data Pribadi

### 9.1 Prinsip Minimalisasi Data

Sistem hanya boleh mengumpulkan dan menampilkan data yang diperlukan untuk proses PMB.

Informasi seperti NIK, alamat, tanggal lahir, nomor telepon, dan dokumen pendidikan harus diperlakukan sebagai data yang memerlukan perlindungan.

### 9.2 Pembatasan Tampilan

Data pribadi tidak boleh ditampilkan secara penuh kepada seluruh role.

Contoh:

- NIK dapat disamarkan pada tampilan daftar jika nilai lengkap tidak diperlukan.
- Laporan pimpinan sebaiknya menggunakan statistik agregat.
- Data yang diekspor harus dibatasi sesuai kebutuhan dan kewenangan.
- Log aplikasi tidak boleh memuat seluruh isi dokumen atau kredensial.

### 9.3 Retensi dan Penghapusan Data

Kebijakan penyimpanan data harus ditetapkan berdasarkan kebutuhan administrasi PMB dan kewajiban yang berlaku.

Hal yang perlu diputuskan:

- Lama penyimpanan pendaftaran.
- Lama penyimpanan dokumen.
- Lama penyimpanan audit log.
- Prosedur penghapusan data.
- Penanganan data pada backup.
- Pihak yang berwenang menyetujui penghapusan.

Data pendaftaran tidak boleh dihapus otomatis hanya karena gelombang sudah berakhir, tanpa mempertimbangkan kebutuhan administrasi dan kebijakan retensi.

### 9.4 Penggunaan Data untuk Pengembangan

Data calon mahasiswa yang sebenarnya tidak boleh digunakan secara sembarangan untuk pengujian lokal.

Gunakan data sintetis atau data yang sudah dianonimkan untuk kebutuhan pengembangan dan pengujian jika memungkinkan.

---

## 10. Keamanan Aplikasi Android

### 10.1 Penyimpanan Lokal

Aplikasi Android tidak boleh menyimpan password dalam bentuk teks biasa.

Token autentikasi dan data sensitif harus disimpan menggunakan mekanisme yang sesuai, seperti penyimpanan terenkripsi yang memanfaatkan Android Keystore.

Data pribadi yang tidak diperlukan secara offline sebaiknya tidak disimpan permanen pada perangkat.

### 10.2 Komunikasi Jaringan

Aplikasi harus:

- Menggunakan HTTPS pada deployment produksi.
- Tidak mengabaikan kesalahan sertifikat TLS.
- Tidak menonaktifkan validasi sertifikat untuk mempermudah koneksi.
- Tidak menampilkan token atau kredensial dalam log debug.
- Menangani kegagalan jaringan tanpa mengirim ulang operasi sensitif secara sembarangan.

### 10.3 Sesi dan Logout

Ketika logout, aplikasi harus membersihkan token autentikasi dan data sensitif yang tidak lagi diperlukan.

Jika token tidak valid atau telah dicabut, aplikasi harus mengarahkan pengguna untuk login kembali sesuai kebutuhan.

### 10.4 Screenshot dan Data pada Layar

Kebutuhan pembatasan screenshot untuk layar sensitif perlu dipertimbangkan berdasarkan risiko dan kebutuhan pengguna.

Pembatasan screenshot bukan pengganti otorisasi backend dan tidak dapat menjamin bahwa informasi tidak akan disalin dengan cara lain.

---

## 11. Keamanan Website Administrasi

Website administrasi digunakan untuk mengelola data calon mahasiswa, sehingga perlu perlindungan tambahan.

Persyaratan utama:

1. Halaman administrasi hanya dapat diakses oleh pengguna yang terautentikasi dan memiliki izin.
2. Menu disesuaikan dengan role, tetapi backend tetap memvalidasi setiap request.
3. Formulir menggunakan perlindungan terhadap CSRF apabila relevan dengan mekanisme autentikasi.
4. Output yang berasal dari input pengguna harus ditampilkan secara aman.
5. Sesi harus memiliki pengaturan yang sesuai dan dapat diakhiri saat logout.
6. Halaman tidak boleh menampilkan data pribadi berlebihan.
7. Ekspor laporan harus dibatasi dan dicatat sesuai kebijakan.
8. Akun internal tidak boleh dibuat melalui registrasi publik tanpa proses otorisasi.

---

## 12. Keamanan Server dan Konfigurasi

### 12.1 Lingkungan Pengembangan dan Produksi

Konfigurasi lokal harus dibedakan dari konfigurasi produksi.

Pada produksi:

- Mode debug Laravel harus dinonaktifkan.
- HTTPS harus digunakan untuk akses melalui jaringan yang tidak tepercaya.
- Kredensial rahasia harus disimpan di luar kode sumber.
- Port layanan harus dibatasi sesuai kebutuhan.
- Hak akses file dan direktori harus dikonfigurasi dengan benar.
- Dependency harus diperbarui dan dipantau terhadap kerentanan.

### 12.2 Konfigurasi Laravel

Konfigurasi yang perlu diperhatikan:

- `APP_DEBUG=false` pada produksi.
- `APP_ENV` sesuai lingkungan yang sebenarnya.
- `APP_KEY` harus dibuat dan dilindungi.
- File `.env` tidak boleh tersedia untuk diunduh dari web.
- Direktori penyimpanan dokumen pribadi tidak boleh dipublikasikan secara langsung.
- Logging tidak boleh merekam rahasia.
- Konfigurasi autentikasi, CORS, dan cookie harus mengikuti arsitektur yang digunakan.

### 12.3 Lingkungan Server Lokal

Untuk tahap awal yang dijalankan secara lokal:

- Batasi akses hanya kepada perangkat dan jaringan yang diperlukan.
- Jangan membuka layanan database kepada seluruh jaringan tanpa kebutuhan.
- Gunakan kredensial database yang tidak mudah ditebak.
- Pisahkan data pengujian dari data yang sebenarnya.
- Siapkan backup berkala.
- Dokumentasikan konfigurasi server agar sistem dapat dijalankan kembali.

---

## 13. Audit Log dan Monitoring

### 13.1 Aktivitas yang Dicatat

Audit log harus mencatat tindakan penting, seperti:

- Login yang berhasil dan kegagalan login yang relevan.
- Pembuatan akun internal.
- Perubahan role.
- Perubahan gelombang dan program studi.
- Pengiriman pendaftaran.
- Permintaan revisi.
- Perubahan status.
- Keputusan penerimaan.
- Koreksi keputusan final.
- Ekspor laporan yang berisi data pribadi.
- Aktivitas akses dokumen sensitif sesuai kebijakan.

### 13.2 Informasi pada Log

Log dapat berisi:

- Waktu kejadian.
- Identitas pengguna atau identitas teknis yang relevan.
- Jenis tindakan.
- Objek yang terpengaruh.
- Hasil tindakan.
- Kode kesalahan.
- Informasi tambahan yang diperlukan untuk investigasi.

Log tidak boleh memuat password, token autentikasi, atau isi dokumen pribadi secara tidak perlu.

### 13.3 Perlindungan Audit Log

Akses ke audit log harus dibatasi.

Pengguna biasa tidak boleh mengubah atau menghapus catatan audit melalui fitur operasional. Untuk lingkungan yang memerlukan jaminan integritas lebih tinggi, audit log dapat disalin ke penyimpanan terpisah dengan hak akses yang lebih ketat.

### 13.4 Monitoring

Pengelola sistem perlu memantau:

- Kegagalan login berulang.
- Lonjakan request yang tidak wajar.
- Kesalahan server berulang.
- Kegagalan unggah dokumen.
- Kegagalan database.
- Kegagalan proses backup.
- Percobaan akses data tanpa izin.

Mekanisme peringatan otomatis dapat ditambahkan sesuai kemampuan server dan kebutuhan operasional.

---

## 14. Backup dan Pemulihan

### 14.1 Backup

Backup harus mencakup:

- Database MySQL.
- Dokumen pendaftaran.
- File konfigurasi penting yang tidak berisi rahasia terbuka.
- Informasi yang diperlukan untuk memulihkan aplikasi.

File backup harus dilindungi dari akses tanpa izin. Jika backup berisi data pribadi, akses dan penyimpanannya harus mengikuti perlindungan yang setara dengan data sumber.

### 14.2 Jadwal Backup

Jadwal backup belum ditetapkan dan harus ditentukan berdasarkan kapasitas server serta kebutuhan PMB.

Sebagai rancangan awal, tim dapat mengevaluasi:

- Backup database harian.
- Backup dokumen berdasarkan perubahan data.
- Penyimpanan salinan backup di lokasi terpisah yang aman.
- Pengujian pemulihan secara berkala.

Frekuensi akhir harus disesuaikan dengan volume data dan seberapa banyak kehilangan data yang masih dapat diterima.

### 14.3 Pengujian Pemulihan

Backup tidak boleh dianggap berhasil hanya karena file berhasil dibuat.

Tim harus melakukan pengujian pemulihan untuk memastikan:

1. File backup dapat dibaca.
2. Database dapat dipulihkan.
3. Dokumen yang terkait dengan pendaftaran tersedia.
4. Relasi antara data pendaftaran dan dokumen tetap konsisten.
5. Aplikasi dapat dijalankan kembali setelah pemulihan.

---

## 15. Penanganan Insiden Keamanan

Jika ditemukan dugaan kebocoran data, pengambilalihan akun, atau manipulasi pendaftaran, tim harus menjalankan prosedur penanganan insiden.

### 15.1 Tahapan Penanganan

1. **Identifikasi:** menentukan gejala, waktu kejadian, dan komponen yang terdampak.
2. **Pembatasan:** menghentikan akses atau aktivitas yang sedang disalahgunakan.
3. **Investigasi:** memeriksa log, akun, request, dan perubahan data yang relevan.
4. **Pemulihan:** memperbaiki konfigurasi, mencabut token, memulihkan data, atau menutup celah keamanan.
5. **Evaluasi:** mengidentifikasi akar masalah dan menentukan tindakan pencegahan.
6. **Dokumentasi:** mencatat kejadian, dampak, tindakan, dan hasil penanganan.

### 15.2 Contoh Tindakan

|Insiden|Tindakan Awal|
|---|---|
|Akun pengguna diduga diambil alih|Cabut sesi atau token yang terdampak dan lakukan pemulihan akun|
|Dokumen diakses tanpa izin|Batasi endpoint terkait dan periksa log akses|
|Status pendaftaran dimanipulasi|Hentikan perubahan terkait, periksa audit log, dan validasi data|
|Database tidak tersedia|Periksa layanan database dan jalankan prosedur pemulihan|
|File berbahaya diunggah|Karantina atau hapus file sesuai prosedur dan periksa mekanisme validasi|
|Kredensial server bocor|Cabut atau rotasi kredensial, lalu periksa akses yang mungkin terjadi|

Jika insiden melibatkan data pribadi, pengelola harus mengevaluasi kewajiban penanganan dan pemberitahuan sesuai peraturan yang berlaku.

---

## 16. Persyaratan Pengujian Keamanan

Pengujian berikut harus dilakukan sebelum sistem dinyatakan siap digunakan.

|ID|Skenario Pengujian|Hasil yang Diharapkan|
|---|---|---|
|SEC-01|Login dengan kredensial salah|Login ditolak tanpa membocorkan detail sensitif|
|SEC-02|Login menggunakan akun nonaktif|Akses ditolak|
|SEC-03|Registrasi publik mencoba menetapkan role admin|Role istimewa tidak diberikan|
|SEC-04|Pengguna mengakses pendaftaran milik pengguna lain|Akses ditolak|
|SEC-05|Pengguna mengakses dokumen milik pengguna lain|Akses ditolak|
|SEC-06|Request berisi input tidak valid|Backend menolak input|
|SEC-07|Input mencoba melakukan SQL Injection|Query tetap aman dan tidak terjadi injeksi|
|SEC-08|Input teks berbahaya ditampilkan di website|Input tidak dieksekusi sebagai skrip|
|SEC-09|Request sensitif tanpa token valid|Request ditolak|
|SEC-10|Pengguna mengubah status melalui endpoint yang tidak diizinkan|Perubahan ditolak|
|SEC-11|Pengguna mengunggah format file yang tidak diizinkan|Unggahan ditolak|
|SEC-12|Pengguna mengunggah file melebihi batas|Unggahan ditolak|
|SEC-13|Pengguna mencoba mengakses file melalui URL publik|Dokumen tidak dapat diakses tanpa otorisasi|
|SEC-14|Request login dikirim berulang kali|Pembatasan request berjalan sesuai konfigurasi|
|SEC-15|Pengguna mencoba membuat pendaftaran ganda pada gelombang yang sama|Duplikasi ditolak|
|SEC-16|Proses pengiriman pendaftaran gagal di tengah operasi|Database tetap konsisten|
|SEC-17|Respons API mengalami kesalahan internal|Stack trace dan rahasia tidak ditampilkan|
|SEC-18|File `.env` diminta melalui web|File tidak dapat diakses|
|SEC-19|Token yang telah dicabut digunakan kembali|Request ditolak|
|SEC-20|Backup dipulihkan pada lingkungan pengujian|Data dan relasi penting dapat dipulihkan|

Pengujian harus dilaksanakan pada lingkungan yang diizinkan dan menggunakan data uji yang sesuai. Hasil setiap pengujian harus dicatat sebagai berhasil, gagal, atau belum diuji.

---

## 17. Kriteria Penerimaan Keamanan

Sistem memenuhi persyaratan keamanan awal apabila:

1. Password disimpan menggunakan hashing yang aman.
2. Endpoint privat memerlukan autentikasi.
3. Backend memeriksa role, izin, dan kepemilikan data.
4. Akun publik tidak dapat membuat role internal.
5. Dokumen pendaftaran disimpan secara privat.
6. Semua input divalidasi di backend.
7. Query database dibuat dengan mekanisme yang aman.
8. Request berisiko dibatasi menggunakan rate limiting.
9. Perubahan status dan keputusan dicatat dalam riwayat serta audit log.
10. Database mencegah pendaftaran ganda pada gelombang yang sama.
11. Kredensial tidak disimpan di repository.
12. Konfigurasi produksi tidak menampilkan debug dan rahasia internal.
13. Backup dan pemulihan data memiliki prosedur terdokumentasi.
14. Pengujian keamanan telah dilaksanakan dan hasilnya didokumentasikan.
15. Hak akses mengikuti dokumen [[Access Control Matrix]].

Kriteria ini merupakan persyaratan rancangan. Status pemenuhannya harus ditentukan berdasarkan implementasi dan hasil pengujian nyata.

---

## 18. Keputusan yang Masih Perlu Ditetapkan

Sebelum sistem masuk ke tahap implementasi final, tim perlu menetapkan:

|ID|Keputusan|Status|
|---|---|---|
|DEC-SEC-01|Mekanisme autentikasi Android dan website|Belum ditetapkan|
|DEC-SEC-02|Kebijakan kekuatan password|Perlu ditetapkan|
|DEC-SEC-03|Batas percobaan login dan rate limiting|Perlu ditetapkan|
|DEC-SEC-04|Jenis file dan ukuran maksimum dokumen|Perlu ditetapkan|
|DEC-SEC-05|Kebijakan retensi data dan dokumen|Perlu ditetapkan|
|DEC-SEC-06|Jadwal backup dan target pemulihan|Perlu ditetapkan|
|DEC-SEC-07|Kewenangan keputusan akhir penerimaan|Perlu dikonfirmasi|
|DEC-SEC-08|Masa berlaku token atau sesi|Perlu ditetapkan|
|DEC-SEC-09|Kebijakan audit log dan lama penyimpanan|Perlu ditetapkan|
|DEC-SEC-10|Konfigurasi keamanan server deployment|Menunggu konfigurasi server|

Keputusan tersebut harus diperbarui setelah tim menentukan detail implementasi dan kebutuhan operasional.

---

## 19. Kesimpulan

Security Specification menjadi pedoman teknis untuk melindungi sistem PMB ITB Akiday dari akses tanpa izin, kebocoran data, manipulasi pendaftaran, dan gangguan layanan. Perlindungan dilakukan melalui autentikasi yang aman, otorisasi berbasis role dan kepemilikan, validasi input, private storage untuk dokumen, keamanan database, audit log, serta prosedur backup dan pemulihan.

Keamanan tidak hanya menjadi tanggung jawab aplikasi Android atau website administrasi, tetapi harus diterapkan secara konsisten pada backend Laravel, database MySQL, penyimpanan dokumen, dan server.

Dokumen ini menjadi dasar implementasi dan pengujian keamanan sistem. Setiap persyaratan harus diverifikasi sebelum dinyatakan terpenuhi.

</WritingBlock>

**Simpan sebagai:** `06 - Security/Security Specification.md`
