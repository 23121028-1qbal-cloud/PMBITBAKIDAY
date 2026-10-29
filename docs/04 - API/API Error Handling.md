# API Error Handling — PMB ITB Akiday

## 1. Informasi Dokumen

|Informasi|Detail|
|---|---|
|Nama Proyek|PMB ITB Akiday|
|Nama Dokumen|API Error Handling|
|Versi|1.1 (usulan Tahap 4)|
|Status|REVISED DESIGN — standar error rancangan, belum diimplementasikan atau diuji pada backend|
|Tanggal Baseline|18 Oktober 2026|
|Tanggal Revisi|28 Oktober 2026|
|Backend|Laravel|
|Format Response|JSON|
|API Version|`/api/v1`|
|Referensi|API Contract.md, SRS.md, Database Model.md, Security Specification.md|

## 2. Tujuan

API Error Handling menjelaskan bagaimana backend PMB ITB Akiday menangani kesalahan saat aplikasi mengirim permintaan ke server. Kesalahan dapat terjadi karena input tidak valid, pengguna belum login, hak akses tidak mencukupi, data tidak ditemukan, pengajuan ganda, file tidak sesuai ketentuan, atau gangguan pada server.

Tujuan utama dokumen ini adalah memastikan seluruh endpoint memberikan respons yang konsisten, membantu pengguna memahami masalah yang terjadi, memudahkan pengembang melakukan debugging, serta mencegah informasi sensitif bocor kepada client.

## 3. Prinsip Penanganan Error

1. **Konsisten:** Format error menggunakan struktur JSON yang sama di seluruh endpoint.
    
2. **Mudah dipahami:** Pesan ditulis dalam bahasa Indonesia yang jelas.
    
3. **Aman:** Response tidak menampilkan password, token, query SQL, stack trace, atau konfigurasi internal.
    
4. **Sesuai HTTP status:** Setiap jenis error menggunakan status HTTP yang tepat.
    
5. **Dapat ditindaklanjuti:** Error validasi menyebutkan field yang perlu diperbaiki.
    
6. **Tercatat:** Error teknis dicatat pada log server dengan informasi yang cukup untuk investigasi.
    
7. **Tidak mengubah data secara parsial:** Operasi yang membutuhkan beberapa perubahan database menggunakan transaksi.
    
8. **Tidak membocorkan data pengguna:** Pengguna tidak boleh mengetahui informasi pribadi atau keberadaan data yang tidak berhak diaksesnya.
    

## 4. Format Response Error Standar

Semua response error API menggunakan format dasar berikut:

```
{
  "success": false,
  "message": "Permintaan tidak dapat diproses.",
  "error": {
    "code": "ERROR_CODE",
    "details": {}
  }
}
```

Keterangan:

|Field|Tipe|Fungsi|
|---|---|---|
|`success`|Boolean|Selalu `false` untuk response error.|
|`message`|String|Pesan yang dapat dipahami pengguna.|
|`error.code`|String|Kode error yang stabil untuk ditangani aplikasi.|
|`error.details`|Object|Detail tambahan, misalnya kesalahan validasi.|

Field `details` boleh dihilangkan jika tidak diperlukan, tetapi bentuk response harus tetap konsisten dengan kontrak API.

Client sebaiknya menentukan tindakan berdasarkan `error.code` dan status HTTP, bukan dengan mencocokkan teks `message`. Teks pesan dapat berubah tanpa mengubah kode error.

## 5. Daftar HTTP Status Code

|HTTP Status|Kode Error Umum|Kondisi|
|---|---|---|
|`400 Bad Request`|`BAD_REQUEST`|Request tidak sesuai format yang diharapkan.|
|`401 Unauthorized`|`UNAUTHENTICATED`|Pengguna belum login atau token tidak valid.|
|`403 Forbidden`|`FORBIDDEN`|Pengguna tidak memiliki hak akses.|
|`404 Not Found`|`RESOURCE_NOT_FOUND`|Resource tidak ditemukan atau tidak dapat diakses sesuai kebijakan keamanan.|
|`419 Authentication Timeout`|`CSRF_TOKEN_MISMATCH`|Cookie/header CSRF browser tidak ada atau tidak valid (exception Laravel dipetakan ke envelope JSON).|
|`409 Conflict`|`APPLICATION_ALREADY_EXISTS`, `STATE_CONFLICT`|Data bertentangan dengan kondisi saat ini atau precondition status telah basi.|
|`413 Content Too Large`|`FILE_TOO_LARGE`|Ukuran file melampaui batas.|
|`415 Unsupported Media Type`|`UNSUPPORTED_FILE_TYPE`|Tipe media tidak didukung.|
|`422 Unprocessable Entity`|`VALIDATION_ERROR`, `INVALID_STATUS_TRANSITION`|Request dapat dibaca tetapi tidak memenuhi aturan validasi atau bisnis.|
|`429 Too Many Requests`|`RATE_LIMIT_EXCEEDED`|Batas permintaan terlampaui.|
|`500 Internal Server Error`|`INTERNAL_SERVER_ERROR`|Terjadi kesalahan internal yang tidak terduga.|
|`503 Service Unavailable`|`SERVICE_UNAVAILABLE`|Layanan sementara tidak tersedia.|

Penggunaan `400` dan `422` harus konsisten: gunakan `400` untuk request yang secara struktur tidak dapat diproses, sedangkan `422` untuk data yang gagal memenuhi aturan validasi atau bisnis.

## 6. Katalog Kode Error

### 6.1. Autentikasi

|Kode|HTTP|Kondisi|Pesan|
|---|---|---|---|
|`INVALID_CREDENTIALS`|401|Email atau password salah|Email atau password tidak valid.|
|`UNAUTHENTICATED`|401|Token tidak tersedia atau tidak valid|Silakan login terlebih dahulu.|
|`ACCOUNT_INACTIVE`|403|Akun dinonaktifkan|Akun tidak aktif. Silakan hubungi administrator.|
|`FORBIDDEN`|403|Role tidak memiliki izin|Anda tidak memiliki izin untuk melakukan tindakan ini.|
|`TOKEN_EXPIRED`|401|Bearer token Android kedaluwarsa/dicabut, jika expiry diterapkan|Sesi berakhir. Silakan login kembali.|
|`STATE_CONFLICT`|409|`expected_status` tidak cocok dengan status aplikasi terkini|Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.|
|`CSRF_TOKEN_MISMATCH`|419 (exception Laravel, dipetakan ke envelope JSON)|Cookie/header CSRF browser tidak ada atau tidak cocok|Sesi keamanan tidak valid. Muat ulang halaman dan coba kembali.|

Untuk mencegah enumerasi akun, proses login sebaiknya menggunakan pesan yang sama untuk email yang tidak terdaftar dan password yang salah. Status akun nonaktif dapat ditangani sesuai kebijakan keamanan, tanpa membocorkan informasi berlebihan.

### 6.2. Validasi Data

|Kode|HTTP|Kondisi|Pesan|
|---|---|---|---|
|`VALIDATION_ERROR`|422|Field wajib kosong atau format salah|Periksa kembali data yang dimasukkan.|
|`INVALID_NIK`|422|NIK tidak sesuai format|NIK harus sesuai format yang ditentukan.|
|`INVALID_EMAIL`|422|Email tidak valid|Masukkan alamat email yang valid.|
|`INVALID_DATE`|422|Format tanggal tidak valid|Periksa kembali format tanggal.|
|`INVALID_STUDY_PROGRAM`|422|Program studi tidak valid atau tidak aktif|Program studi yang dipilih tidak tersedia.|
|`INVALID_ADMISSION_WAVE`|422|Gelombang tidak valid atau tidak mengizinkan pendaftaran|Gelombang penerimaan tidak tersedia untuk pendaftaran.|
|`INVALID_STATUS_TRANSITION`|422|Perubahan status tidak sesuai alur|Status pengajuan tidak dapat diubah ke status tersebut.|

### 6.3. Pengajuan Pendaftaran

|Kode|HTTP|Kondisi|Pesan|
|---|---|---|---|
|`APPLICATION_NOT_FOUND`|404|Pengajuan tidak ditemukan atau tidak dapat diakses|Data pengajuan tidak ditemukan.|
|`APPLICATION_ALREADY_EXISTS`|409|Pengajuan pada gelombang yang sama sudah ada|Anda sudah memiliki pengajuan pada gelombang ini.|
|`APPLICATION_NOT_EDITABLE`|409|Pengajuan tidak lagi dapat diedit|Pengajuan tidak dapat diubah pada status saat ini.|
|`APPLICATION_INCOMPLETE`|422|Profil atau dokumen wajib belum lengkap|Lengkapi data dan dokumen sebelum mengirim pendaftaran.|
|`APPLICATION_ALREADY_SUBMITTED`|409|Pengajuan sudah dikirim|Pengajuan ini sudah dikirim untuk pemeriksaan.|
|`DECISION_NOT_ALLOWED`|422|Keputusan tidak sesuai status atau kewenangan proses|Keputusan tidak dapat ditetapkan pada kondisi saat ini.|

### 6.4. Dokumen

|Kode|HTTP|Kondisi|Pesan|
|---|---|---|---|
|`DOCUMENT_NOT_FOUND`|404|Dokumen tidak ditemukan atau tidak dapat diakses|Dokumen tidak ditemukan.|
|`FILE_TOO_LARGE`|413|Ukuran file melebihi batas|Ukuran file melebihi batas yang diizinkan.|
|`UNSUPPORTED_FILE_TYPE`|415|File tidak termasuk tipe yang diizinkan|Format file tidak didukung.|
|`DOCUMENT_REQUIRED`|422|Dokumen wajib belum tersedia|Unggah seluruh dokumen yang diwajibkan.|
|`DOCUMENT_NOT_EDITABLE`|409|Dokumen tidak dapat diganti pada status saat ini|Dokumen tidak dapat diubah pada status pengajuan ini.|
|`FILE_UPLOAD_FAILED`|500|Penyimpanan file gagal|Dokumen belum berhasil disimpan. Silakan coba kembali.|

### 6.5. Sistem dan Infrastruktur

|Kode|HTTP|Kondisi|Pesan|
|---|---|---|---|
|`RESOURCE_NOT_FOUND`|404|Endpoint atau resource tidak ditemukan|Data yang diminta tidak ditemukan.|
|`RATE_LIMIT_EXCEEDED`|429|Permintaan terlalu sering|Terlalu banyak permintaan. Silakan coba kembali nanti.|
|`SERVICE_UNAVAILABLE`|503|Layanan sementara tidak tersedia|Layanan sementara tidak tersedia. Silakan coba kembali nanti.|
|`INTERNAL_SERVER_ERROR`|500|Kesalahan tidak terduga|Terjadi kesalahan pada server. Silakan coba kembali nanti.|

Kode error di atas adalah kontrak rancangan. Exception CSRF Laravel 419 harus dipetakan ke envelope JSON stabil (`CSRF_TOKEN_MISMATCH`); client admin menginisiasi ulang bootstrap CSRF/session, bukan mengulang POST mutasi secara buta. Backend memakai kode error secara konsisten. `STATE_CONFLICT` selaras dengan `API Contract.md` dan tidak boleh meninggalkan perubahan parsial.

## 7. Contoh Response Error

### 7.1. Validasi Formulir

HTTP `422 Unprocessable Entity`

```
{
  "success": false,
  "message": "Periksa kembali data yang dimasukkan.",
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {
      "full_name": [
        "Nama lengkap wajib diisi."
      ],
      "nik": [
        "NIK harus terdiri dari 16 digit."
      ]
    }
  }
}
```

Aplikasi Android dapat menampilkan pesan pada field yang sesuai. Website admin juga dapat menggunakan struktur yang sama.

### 7.2. Pengajuan Ganda

HTTP `409 Conflict`

```
{
  "success": false,
  "message": "Anda sudah memiliki pengajuan pada gelombang ini.",
  "error": {
    "code": "APPLICATION_ALREADY_EXISTS"
  }
}
```

Backend memeriksa pengajuan sebelumnya dan tetap mengandalkan constraint unik `UNIQUE(applicant_id, wave_id)` pada database. Jika dua permintaan datang bersamaan, pelanggaran constraint harus diterjemahkan menjadi response konflik ini, bukan `500`.

### 7.3. Pengajuan Belum Lengkap

HTTP `422 Unprocessable Entity`

```
{
  "success": false,
  "message": "Lengkapi data dan dokumen sebelum mengirim pendaftaran.",
  "error": {
    "code": "APPLICATION_INCOMPLETE",
    "details": {
      "missing_fields": [
        "birth_date",
        "address"
      ],
      "missing_documents": [
        "photo",
        "diploma"
      ]
    }
  }
}
```

Field yang ditampilkan hanya berisi informasi yang aman untuk dikembalikan kepada pemilik pengajuan.

### 7.4. Akses Ditolak

HTTP `403 Forbidden`

```
{
  "success": false,
  "message": "Anda tidak memiliki izin untuk melakukan tindakan ini.",
  "error": {
    "code": "FORBIDDEN"
  }
}
```

Contohnya adalah calon mahasiswa yang mencoba menetapkan keputusan penerimaan atau pengguna yang mencoba mengakses fitur khusus admin.

### 7.5. Data Tidak Ditemukan

HTTP `404 Not Found`

```
{
  "success": false,
  "message": "Data pengajuan tidak ditemukan.",
  "error": {
    "code": "APPLICATION_NOT_FOUND"
  }
}
```

Untuk data yang bukan milik pengguna, backend dapat menggunakan respons `404` yang sama agar keberadaan data tidak terungkap.

### 7.6. File Terlalu Besar

HTTP `413 Content Too Large`

```
{
  "success": false,
  "message": "Ukuran file melebihi batas yang diizinkan.",
  "error": {
    "code": "FILE_TOO_LARGE",
    "details": {
      "max_size_mb": 5
    }
  }
}
```

Angka `5 MB` pada contoh hanya ilustrasi. Batas ukuran yang sebenarnya harus ditentukan dalam konfigurasi sistem dan disamakan antara validasi Laravel, web server, dan dokumentasi pengguna.

### 7.7. Konflik Status

HTTP `409 Conflict` untuk status yang telah berubah setelah client memuat aplikasi:

```
{
  "success": false,
  "message": "Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.",
  "error": {
    "code": "STATE_CONFLICT"
  }
}
```

Tidak ada perubahan status atau history yang disimpan. Client memuat ulang resource dan meminta tindakan eksplisit kembali.

### 7.8. Kesalahan Server

HTTP `500 Internal Server Error`

```
{
  "success": false,
  "message": "Terjadi kesalahan pada server. Silakan coba kembali nanti.",
  "error": {
    "code": "INTERNAL_SERVER_ERROR"
  }
}
```

Detail penyebab dicatat pada log server dengan ID korelasi atau ID permintaan jika tersedia. Jangan mengirim stack trace kepada client.

## 8. Penanganan Error di Laravel

### 8.1. Validasi Request

Gunakan validasi Laravel pada setiap endpoint untuk memeriksa input sebelum proses bisnis dijalankan.

Contoh:

```
$request->validate([
    'email' => ['required', 'email'],
    'password' => ['required', 'string', 'min:8'],
]);
```

Untuk API, validasi harus menghasilkan response JSON dengan status `422` dan format yang sesuai dengan kontrak. Pastikan konfigurasi exception handler Laravel mempertahankan format standar tersebut.

### 8.2. Otorisasi

Gunakan middleware, policy, atau mekanisme otorisasi Laravel yang sesuai.

Pemeriksaan dilakukan pada backend untuk memastikan:

- Pengguna sudah terautentikasi.
    
- Akun masih aktif.
    
- Role memiliki izin yang diperlukan.
    
- Resource memang boleh diakses oleh pengguna tersebut.
    

Jangan mengandalkan ID yang dikirim client sebagai bukti kepemilikan data.

### 8.3. Penanganan Resource Tidak Ditemukan

Gunakan pencarian resource yang sesuai dengan lingkup akses. Jika pengguna meminta pengajuan yang bukan miliknya, respons tidak boleh membocorkan informasi pengajuan tersebut.

### 8.4. Penanganan Exception

Exception yang sudah dikenali dapat dipetakan ke kode error tertentu. Exception yang tidak dikenali harus menghasilkan respons generik dan dicatat pada log.

Hindari menangkap semua exception lalu mengembalikan `200 OK`, karena client akan menganggap operasi berhasil padahal gagal.

### 8.5. Pelanggaran Constraint Database

Jika database menolak data karena constraint unik, backend harus mengenali kondisi tersebut dan mengembalikan `409 Conflict` untuk konflik bisnis yang diketahui, misalnya pengajuan ganda.

Jangan menampilkan pesan SQL mentah atau nama tabel internal pada response.

## 9. Penanganan Error pada Aplikasi Android Kotlin

Aplikasi Android harus membedakan kegagalan jaringan, error HTTP, dan kesalahan validasi.

|Kondisi|Tindakan Client|
|---|---|
|Tidak ada koneksi|Tampilkan pesan koneksi dan sediakan opsi mencoba kembali.|
|HTTP `401`|Minta pengguna login kembali jika sesi tidak lagi valid.|
|HTTP `403`|Tampilkan pesan bahwa tindakan tidak diizinkan.|
|HTTP `404`|Tampilkan pesan data tidak ditemukan.|
|HTTP `409`|Tampilkan konflik dan arahkan pengguna memeriksa data yang sudah ada.|
|HTTP `422`|Tampilkan pesan validasi pada field yang terkait.|
|HTTP `429`|Hormati `Retry-After` jika dikirim server.|
|HTTP `500` atau `503`|Tampilkan pesan umum dan opsi mencoba kembali.|
|Timeout|Beri tahu pengguna bahwa respons server belum diterima; periksa status/resource sebelum mengulang mutasi.|
|Browser CSRF `419` / `CSRF_TOKEN_MISMATCH`|Bootstrap ulang CSRF dan sesi lalu minta tindakan pengguna mengirim ulang; jangan retry mutasi otomatis.|

Aplikasi tidak boleh menganggap permintaan gagal hanya karena timeout berarti data pasti tidak tersimpan. Untuk operasi pembuatan pengajuan, periksa kembali daftar pengajuan sebelum mengulangi permintaan agar tidak membuat tindakan duplikat.

## 10. Penanganan Error pada Website Admin

Website admin harus menggunakan kontrak error yang sama dengan aplikasi Android.

- Tampilkan pesan validasi di dekat field yang bermasalah.
    
- Pertahankan input yang aman ketika validasi gagal.
    
- Tampilkan notifikasi untuk konflik status atau data.
    
- Jangan menampilkan stack trace atau detail database.
    
- Arahkan pengguna kembali ke login jika sesi tidak valid.
    
- Nonaktifkan tombol submit selama permintaan berjalan untuk mengurangi klik berulang, tanpa mengandalkannya sebagai perlindungan utama dari duplikasi.
    
- Pastikan keputusan akhir menampilkan konfirmasi yang jelas sebelum dikirim ke backend.
    

## 11. Logging dan Monitoring

Log server digunakan untuk membantu pengembang mengetahui penyebab error tanpa membocorkan detail internal kepada pengguna.

Informasi yang dapat dicatat jika tersedia:

- Waktu kejadian.
    
- ID permintaan atau correlation ID.
    
- Nama endpoint dan metode HTTP.
    
- Kode status HTTP.
    
- Kode error internal.
    
- ID pengguna yang sudah terautentikasi, jika relevan.
    
- Ringkasan exception teknis.
    
- Durasi permintaan.
    

Jangan mencatat:

- Password atau password confirmation.
    
- Token autentikasi.
    
- Isi lengkap dokumen identitas.
    
- Isi file yang diunggah.
    
- Data pribadi lengkap yang tidak diperlukan untuk debugging.
    

Log harus memiliki pembatasan akses dan kebijakan retensi. Error yang mengindikasikan penyalahgunaan atau kegagalan berulang dapat digunakan untuk investigasi lebih lanjut.

## 12. Retry dan Pencegahan Duplikasi

Retry dapat membantu ketika terjadi gangguan jaringan sementara, tetapi tidak semua operasi aman untuk diulang.

Aturan awal:

1. Permintaan `GET` umumnya dapat dicoba kembali jika diperlukan.
    
2. Permintaan `POST` tidak boleh diulang secara otomatis tanpa mempertimbangkan apakah operasi pertama sudah berhasil.
    
3. Pengajuan harus dilindungi oleh constraint unik `(applicant_id, wave_id)`.
    
4. Setelah timeout pada pembuatan pengajuan, client sebaiknya memeriksa daftar pengajuan sebelum mencoba kembali.
    
5. Sampai idempotency key disepakati dan diimplementasikan, mutasi `POST` status tidak di-retry otomatis; gunakan `expected_status` untuk mendeteksi status stale dan ambil ulang resource setelah timeout.
    
6. Retry harus memiliki batas jumlah percobaan dan jeda yang sesuai agar tidak membebani server.
    

## 13. Keamanan dan Privasi

1. Gunakan HTTPS pada deployment produksi.
    
2. Jangan mengembalikan password atau token pada response selain token yang memang diperlukan saat autentikasi.
    
3. Jangan menampilkan exception, SQL query, atau stack trace kepada client.
    
4. Gunakan validasi server-side untuk seluruh input.
    
5. Terapkan pembatasan akses berbasis role dan kepemilikan data.
    
6. Lindungi endpoint login dari percobaan berulang dengan rate limiting.
    
7. Validasi file yang diunggah dan simpan di lokasi privat.
    
8. Jangan menampilkan data pendaftar lain melalui response error.
    
9. Pastikan pesan error tidak mengungkap apakah email terdaftar saat login gagal.
    
10. Sanitasi informasi yang masuk ke log agar rahasia dan data pribadi tidak ikut tercatat.
    

## 14. Skenario Pengujian Error

|ID|Skenario|Hasil yang Diharapkan|
|---|---|---|
|ERR-01|Login menggunakan password salah|HTTP `401`, kode `INVALID_CREDENTIALS`.|
|ERR-02|Mengakses endpoint terlindungi tanpa token|HTTP `401`, kode `UNAUTHENTICATED`.|
|ERR-03|Calon mahasiswa mengakses fitur keputusan|HTTP `403`, kode `FORBIDDEN`.|
|ERR-04|Mengirim profil dengan field wajib kosong|HTTP `422`, kode `VALIDATION_ERROR`.|
|ERR-05|Membuat pengajuan kedua pada gelombang yang sama|HTTP `409`, kode `APPLICATION_ALREADY_EXISTS`.|
|ERR-06|Mengirim pengajuan tanpa dokumen wajib|HTTP `422`, kode `APPLICATION_INCOMPLETE`.|
|ERR-07|Mengunggah file terlalu besar|HTTP `413`, kode `FILE_TOO_LARGE`.|
|ERR-08|Mengunggah file dengan tipe yang dilarang|HTTP `415`, kode `UNSUPPORTED_FILE_TYPE`.|
|ERR-09|Mengubah status dari status yang tidak mengizinkan transisi|HTTP `422`, kode `INVALID_STATUS_TRANSITION`.|
|ERR-10|Mengakses pengajuan milik pengguna lain|HTTP `404` atau respons kebijakan akses yang konsisten, tanpa membocorkan data.|
|ERR-11|Terjadi exception internal|HTTP `500`, kode `INTERNAL_SERVER_ERROR`, detail tercatat pada log.|
|ERR-12|Mengirim permintaan berlebihan|HTTP `429`, kode `RATE_LIMIT_EXCEEDED`.|
|ERR-13|Mengalami timeout setelah mengirim pengajuan|Client memeriksa status pengajuan sebelum mengulang tindakan.|
|ERR-14|Database menolak pengajuan ganda akibat permintaan bersamaan|Backend memetakan konflik constraint menjadi HTTP `409`.|
|ERR-15|Aksi status memakai `expected_status` yang sudah stale|HTTP `409`, kode `STATE_CONFLICT`, tanpa perubahan parsial/history.|
|ERR-16|Request mutasi admin tidak memiliki CSRF yang valid|Laravel `419` dipetakan ke envelope JSON stabil `CSRF_TOKEN_MISMATCH`; client bootstrap ulang, tidak mengulang mutasi otomatis.|

## 15. Checklist Implementasi

- Semua response error menggunakan format JSON yang konsisten.
    
- Setiap error memiliki kode stabil.
    
- HTTP status sesuai jenis kegagalan.
    
- Validasi Laravel menghasilkan format yang disepakati.
    
- Middleware dan policy mengendalikan akses.
    
- Konflik constraint unik dipetakan dengan benar.
    
- Error internal tidak membocorkan detail teknis.
    
- Log tidak menyimpan password, token, atau data sensitif berlebihan.
    
- Android Kotlin dapat memproses error jaringan dan HTTP.
    
- Website admin menampilkan error dengan jelas.
    
- Retry tidak menimbulkan duplikasi operasi.
    
- Pengujian error dijalankan pada lingkungan pengembangan.
    
- Batas ukuran file dan rate limiting ditetapkan dalam konfigurasi.
    
- Kontrak ini konsisten dengan `API Contract.md`.
    

## 16. Keputusan yang Masih Terbuka

1. Apakah idempotency key diperlukan untuk operasi pembuatan pengajuan?
    
2. Berapa batas permintaan untuk login, unggah dokumen, dan endpoint lainnya?
    
3. Berapa ukuran file maksimum dan tipe file yang diizinkan?
    
4. Berapa lama log aplikasi disimpan?
    
5. Apakah correlation ID akan digunakan pada semua request?
    
6. Apakah seluruh pesan error menggunakan bahasa Indonesia pada MVP?
    
7. Bagaimana mekanisme notifikasi jika server mengalami gangguan berkepanjangan?
    

## 17. Kesimpulan dan Status Tahap 4

API Error Handling versi usulan Tahap 4 menetapkan envelope error, kode HTTP, respons autentikasi/validasi/otorisasi, konflik duplikasi dan status, CSRF browser, retry aman, serta pembatasan data pada log untuk Android dan admin web.

Dokumen ini merupakan **kontrak desain**, bukan bukti implementasi. Handler Laravel, middleware Sanctum/CSRF, mapping exception, serta pengujian API belum tersedia dan diverifikasi. DB-01 Tahap 3 masih memblokir payload hasil pemeriksaan per dokumen; skenario error fitur tersebut belum dapat dibakukan. Penerapan dan pengujian standar ini dilakukan pada pekerjaan backend setelah database test dan schema implementable tersedia.

