# API Contract — PMB ITB Akiday

## 1. Informasi Dokumen

|Informasi|Detail|
|---|---|
|Nama Proyek|PMB ITB Akiday|
|Nama Dokumen|API Contract|
|Versi|1.1 (usulan Tahap 4)|
|Status|REVISED DESIGN — kontrak rancangan, belum diimplementasikan atau diverifikasi terhadap backend|
|Tanggal Baseline|18 Oktober 2026|
|Tanggal Revisi|28 Oktober 2026|
|Backend|Laravel|
|Database|MySQL|
|Client|Android Kotlin dan Website Admin React + TypeScript + Vite|
|Format Komunikasi|REST API, JSON|
|Autentikasi|Laravel Sanctum dengan alur browser cookie/session + CSRF untuk admin web dan bearer token untuk Android (keputusan Tahap 4)|
|Referensi|SRS.md, Module Specification.md, Database Model.md, ERD.md|

**Status Tahap 4:** kontrak ini merupakan revised design untuk stack build yang telah diputuskan, bukan endpoint yang tersedia dalam kode. Pengguna memilih Laravel Sanctum dual-flow: admin browser memakai cookie sesi Secure/HttpOnly dan CSRF, sedangkan Android memakai bearer token terlindungi. CORS/credentials harus menggunakan origin allowlist eksplisit; tidak memakai wildcard dengan credentials. Versi Laravel, expiry/rotasi/revokasi token, domain web, dan origin final ditetapkan saat implementasi. PRD v1.0 tetap baseline historis; keputusan stack build dicatat sebagai addendum pelaksana, bukan perubahan diam-diam ke PRD.

## 2. Tujuan

API Contract mendefinisikan cara aplikasi Android dan website admin berkomunikasi dengan backend PMB ITB Akiday. Dokumen ini menjelaskan endpoint, metode HTTP, autentikasi, parameter, format request dan response, serta aturan akses setiap fitur.

Dengan kontrak yang jelas, pengembangan frontend dan backend dapat dilakukan secara terpisah tanpa perbedaan pemahaman mengenai nama endpoint, struktur data, atau aturan bisnis.

## 3. Konvensi API

### 3.1. Base URL

Untuk lingkungan lokal, base URL mengikuti alamat server yang digunakan.

```
http://localhost:8000/api/v1
```

Pada Android Emulator, `localhost` mengarah ke emulator itu sendiri, bukan komputer host. Untuk Android Emulator standar, alamat host biasanya dapat diakses melalui `10.0.2.2`, sedangkan perangkat fisik memerlukan alamat IP komputer server yang dapat dijangkau.

Contoh base URL untuk Android Emulator:

```
http://10.0.2.2:8000/api/v1
```

Alamat tersebut hanya untuk pengembangan lokal. Untuk produksi, gunakan HTTPS dan domain server yang telah dikonfigurasi.

### 3.2. Format Request dan Response

Request dan response umum menggunakan JSON dengan header:

```
Accept: application/json
Content-Type: application/json
```

Untuk unggah dokumen, gunakan `multipart/form-data`.

### 3.3. Standar Penamaan

- Endpoint menggunakan bentuk jamak untuk resource, misalnya `/applications`.
    
- ID menggunakan format numerik sesuai rancangan database.
    
- Nama properti JSON menggunakan `snake_case`.
    
- Tanggal dan waktu API menggunakan ISO 8601 dengan zona waktu yang ditentukan sistem.
    
- Status pengajuan menggunakan nilai yang konsisten dengan database.
    
- Semua endpoint yang memerlukan login harus memvalidasi identitas dan hak akses pengguna.
    

### 3.4. Format Waktu

Contoh waktu:

```
{
  "submitted_at": "2026-10-18T09:30:00+07:00"
}
```

Backend harus konsisten dalam menyimpan waktu dan mengubahnya ke zona waktu yang digunakan saat menampilkan data.

## 4. Autentikasi dan Otorisasi

Sistem menggunakan Laravel Sanctum dengan dua mekanisme autentikasi klien:

- **Admin web (browser first-party):** cookie sesi wajib `Secure`, `HttpOnly`, dan dikonfigurasi `SameSite`/domain sesuai deployment HTTPS. Cookie `XSRF-TOKEN` mengikuti protokol Laravel dan harus dapat dibaca client untuk dikirim kembali melalui header `X-XSRF-TOKEN` (cookie CSRF ini bukan `HttpOnly`). Browser bootstrap melalui `GET /sanctum/csrf-cookie`, di luar prefix resource `/api/v1`. `credentials: include` hanya ke origin API tepercaya. CORS memakai allowlist origin admin eksplisit; jangan gunakan wildcard bersama credentials. Session/token tidak disimpan di `localStorage`/`sessionStorage`.
- **Android:** login memakai bearer token personal Sanctum pada `Authorization: Bearer`; simpan token memakai penyimpanan terlindungi yang memanfaatkan Android Keystore, hapus saat logout, dan jangan masukkan ke URL/log.

Endpoint resource dan auth aplikasi ada di `/api/v1`, dengan otorisasi server-side yang sama. Route session browser bersifat first-party stateful, memerlukan cookie + CSRF, dan tidak mengembalikan bearer token. Android tidak menggunakan sesi cookie browser.

### 4.1. Header Autentikasi

- Android token mode: `Authorization: Bearer <access_token>` pada endpoint resource terlindungi.
- Admin browser mode: cookie sesi Sanctum dikirim oleh browser (`credentials: include`) dan request mutasi mengirim token CSRF sesuai protokol Laravel/Sanctum; browser tidak mengirim bearer token.
- Kedua mode tidak boleh dicampur dalam satu sesi client. Request tanpa autentikasi yang valid menerima `401 UNAUTHENTICATED`.

### 4.2. Role Pengguna

|Role|Hak Akses Utama|
|---|---|
|`calon_mahasiswa`|Mengelola profil sendiri, membuat pengajuan, mengunggah dokumen, melihat status dan riwayat sendiri.|
|`admin`|Mengelola akun, data master, pengajuan, penugasan, serta laporan sesuai kebijakan sistem.|
|`panitia`|Memeriksa berkas, meminta revisi, dan menetapkan keputusan sesuai kewenangan yang diberikan.|
|`pimpinan`|Melihat dashboard dan laporan sesuai hak akses.|

Backend harus memeriksa role dan kepemilikan data. Menyembunyikan tombol pada antarmuka saja tidak cukup untuk membatasi akses.

## 5. Daftar Endpoint

### 5.1. Autentikasi

|Method|Endpoint|Fungsi|Akses|
|---|---|---|---|
|POST|`/auth/register`|Mendaftarkan akun calon mahasiswa|Publik|
|POST|`/auth/login`|Login Android dan membuat bearer token personal Sanctum|Publik|
|POST|`/auth/session/login`|Login admin browser dan membentuk sesi cookie first-party|Publik + CSRF/stateful session|
|POST|`/auth/logout`|Mencabut bearer token Android yang digunakan|Bearer token|
|POST|`/auth/session/logout`|Mengakhiri sesi cookie admin browser|Session cookie + CSRF|
|GET|`/auth/me`|Mengambil informasi akun aktif untuk salah satu konteks auth Sanctum yang valid|Login|

`GET /sanctum/csrf-cookie` dipanggil sebelum login/mutasi browser; route session browser memakai middleware stateful/session dan CSRF. Login Android memakai `POST /api/v1/auth/login` serta bearer token. Resource endpoints menerima satu konteks auth sesuai client; kedua mode tidak boleh dicampur.

### 5.2. Profil Calon Mahasiswa

|Method|Endpoint|Fungsi|Akses|
|---|---|---|---|
|GET|`/applicant/profile`|Melihat profil sendiri|Calon mahasiswa|
|PUT|`/applicant/profile`|Membuat atau memperbarui profil|Calon mahasiswa|

### 5.3. Data Master

|Method|Endpoint|Fungsi|Akses|
|---|---|---|---|
|GET|`/admission-waves`|Melihat gelombang penerimaan|Login|
|GET|`/study-programs`|Melihat program studi aktif|Login|
|GET|`/admin/admission-waves`|Melihat seluruh gelombang|Admin|
|POST|`/admin/admission-waves`|Membuat gelombang|Admin|
|PUT|`/admin/admission-waves/{id}`|Memperbarui gelombang|Admin|
|GET|`/admin/study-programs`|Melihat seluruh program studi|Admin|
|POST|`/admin/study-programs`|Menambah program studi|Admin|
|PUT|`/admin/study-programs/{id}`|Memperbarui program studi|Admin|

### 5.4. Pengajuan Pendaftaran

|Method|Endpoint|Fungsi|Akses|
|---|---|---|---|
|POST|`/applications`|Membuat draft pengajuan|Calon mahasiswa|
|GET|`/applications`|Melihat daftar pengajuan sendiri|Calon mahasiswa|
|GET|`/applications/{id}`|Melihat detail pengajuan|Pemilik atau petugas berwenang|
|PUT|`/applications/{id}`|Memperbarui draft pengajuan|Pemilik, sesuai aturan status|
|POST|`/applications/{id}/submit`|Mengirim pengajuan untuk diperiksa|Pemilik|
|GET|`/applications/{id}/status-history`|Melihat riwayat status|Pemilik atau petugas berwenang|

Satu akun calon mahasiswa hanya boleh mempunyai satu pengajuan pada gelombang yang sama. Pengajuan pada gelombang berbeda diperbolehkan sesuai ketentuan penerimaan.

### 5.5. Dokumen Pendaftaran

|Method|Endpoint|Fungsi|Akses|
|---|---|---|---|
|POST|`/applications/{id}/documents`|Mengunggah dokumen|Pemilik, sesuai aturan status|
|GET|`/applications/{id}/documents`|Melihat metadata dokumen|Pemilik atau petugas berwenang|
|GET|`/applications/{id}/documents/{document_id}/download`|Mengunduh dokumen privat melalui backend terotorisasi|Pemilik atau petugas berwenang|
|DELETE|`/applications/{id}/documents/{document_id}`|Menghapus dokumen yang diizinkan|Pemilik, sesuai aturan status|
|POST|`/applications/{id}/resubmit`|Mengirim perbaikan setelah status `revision`, kembali ke `review` dan mencatat history|Pemilik aplikasi|

Penghapusan dokumen harus dibatasi sesuai status pengajuan. Dokumen yang sedang diperiksa atau dibutuhkan sebagai bukti keputusan tidak boleh dihapus sembarangan.

### 5.6. Pemeriksaan dan Keputusan

|Method|Endpoint|Fungsi|Akses|
|---|---|---|---|
|GET|`/admin/applications`|Melihat daftar pengajuan|Admin/panitia sesuai lingkup tugas|
|GET|`/admin/applications/{id}`|Melihat detail pengajuan dan metadata dokumen|Admin/panitia sesuai lingkup tugas|
|POST|`/admin/applications/{id}/review`|Memulai pemeriksaan aplikasi, transisi `submitted` → `review`|Panitia dengan izin review|
|POST|`/admin/applications/{id}/request-revision`|Meminta perbaikan, transisi `review` → `revision`|Panitia dengan izin review|
|POST|`/admin/applications/{id}/decision`|Menetapkan `accepted`/`rejected` dari `review`|Role keputusan yang diberi izin secara eksplisit; keputusan role masih perlu konfirmasi institusi|
|POST|`/admin/applications/{id}/assign`|Menugaskan pemeriksaan|Admin, hanya jika penugasan diputuskan termasuk MVP|

`review` pada rancangan ini mengubah status aplikasi dan mencatat `application_status_histories`; endpoint ini **belum** menyimpan hasil review/catatan untuk setiap dokumen. DB-01 pada laporan Tahap 3 memblokir payload/status review per dokumen. Jangan menafsirkan endpoint `review` sebagai mekanisme review detail berkas sampai keputusan domain tersebut disetujui.

Keputusan `accepted` atau `rejected` harus dilakukan oleh petugas yang berwenang, bukan ditetapkan otomatis oleh validasi kelengkapan dokumen.

### 5.7. Dashboard dan Laporan

|Method|Endpoint|Fungsi|Akses|
|---|---|---|---|
|GET|`/admin/dashboard`|Melihat ringkasan data|Admin/panitia/pimpinan sesuai izin|
|GET|`/admin/reports/applications`|Mengambil laporan pendaftaran|Sesuai izin laporan|
|GET|`/admin/reports/applications/export?format=pdf`|Mengunduh laporan PDF|Sesuai izin laporan|

Parameter dan format ekspor dapat disesuaikan saat implementasi. Endpoint ekspor harus memvalidasi format dan hak akses pengguna.

### 5.8. Administrasi Pengguna

|Method|Endpoint|Fungsi|Akses|
|---|---|---|---|
|GET|`/admin/users`|Melihat daftar pengguna (pagination/filter; field PII dibatasi)|Admin|
|GET|`/admin/users/{id}`|Melihat detail pengguna yang diperlukan operasional|Admin|
|PUT|`/admin/users/{id}/status`|Mengaktifkan atau menonaktifkan akun|Admin dengan izin pengelolaan akun|

Pembuatan akun internal dan perubahan role harus dibatasi kepada admin yang memiliki kewenangan khusus; endpoint untuk tindakan tersebut tidak termasuk kontrak MVP saat ini.

## 6. Detail Request dan Response

### 6.1. Registrasi Akun

**Endpoint:** `POST /auth/register`

Request:

```
{
  "email": "mahasiswa@example.com",
  "password": "ContohPasswordAman123",
  "password_confirmation": "ContohPasswordAman123"
}
```

Response sukses `201 Created`:

```
{
  "success": true,
  "message": "Akun berhasil dibuat.",
  "data": {
    "id": 101,
    "email": "mahasiswa@example.com",
    "role": "calon_mahasiswa"
  }
}
```

Catatan:

- Password hanya digunakan untuk proses pembuatan akun dan tidak boleh dikembalikan dalam response.
    
- Backend melakukan hashing password.
    
- Email harus unik.
    
- Setelah registrasi, pengguna dapat login menggunakan akun yang dibuat.
    
- Verifikasi email tidak termasuk MVP berdasarkan baseline kebutuhan saat ini.
    

### 6.2. Login

**Endpoint Android:** `POST /auth/login` (bearer token). Website admin menggunakan `POST /auth/session/login` dengan CSRF dan cookie sesi; endpoint browser tidak pernah mengembalikan bearer token.

Browser mengirim `Accept: application/json`, cookie dengan `credentials: include`, dan header `X-XSRF-TOKEN` pada mutasi setelah bootstrap CSRF. Cookie sesi HttpOnly; cookie `XSRF-TOKEN` dibaca sesuai protokol Laravel.

Request:

```
{
  "email": "mahasiswa@example.com",
  "password": "ContohPasswordAman123"
}
```

Response sukses Android `200 OK`:

```
{
  "success": true,
  "message": "Login berhasil.",
  "data": {
    "user": {
      "id": 101,
      "email": "mahasiswa@example.com",
      "role": "calon_mahasiswa"
    },
    "access_token": "<access_token> (placeholder)",
    "token_type": "Bearer"
  }
}
```

Token pada contoh hanya placeholder; token asli hanya dikembalikan untuk endpoint login Android dan disimpan dengan aman pada Android. Login browser mengembalikan identitas user dalam envelope JSON, menetapkan cookie sesi melalui `Set-Cookie`, serta tidak mengembalikan `access_token`; cookie sesi `Secure`/`HttpOnly` dan cookie/header CSRF diproses sesuai konfigurasi browser/Sanctum.

Jika login gagal, gunakan pesan yang tidak membocorkan apakah suatu email terdaftar atau tidak.

### 6.3. Melihat Profil

**Endpoint:** `GET /applicant/profile`

Header:

```
Authorization: Bearer <access_token>
Accept: application/json
```

Response sukses:

```
{
  "success": true,
  "message": "Profil berhasil diambil.",
  "data": {
    "user_id": 101,
    "full_name": "Nama Contoh",
    "nik": "3200000000000001",
    "birth_place": "Bandung",
    "birth_date": "2007-05-10",
    "gender": "L",
    "phone": "081234567890",
    "address": "Alamat contoh"
  }
}
```

Data pada contoh hanya untuk ilustrasi. API harus mengembalikan profil milik akun yang sedang login, bukan profil berdasarkan ID yang dapat dipilih bebas oleh client.

### 6.4. Memperbarui Profil

**Endpoint:** `PUT /applicant/profile`

Request:

```
{
  "full_name": "Nama Contoh",
  "nik": "3200000000000001",
  "birth_place": "Bandung",
  "birth_date": "2007-05-10",
  "gender": "L",
  "phone": "081234567890",
  "address": "Alamat tempat tinggal"
}
```

Response sukses:

```
{
  "success": true,
  "message": "Profil berhasil disimpan.",
  "data": {
    "full_name": "Nama Contoh",
    "nik": "3200000000000001",
    "birth_place": "Bandung",
    "birth_date": "2007-05-10",
    "gender": "L",
    "phone": "081234567890",
    "address": "Alamat tempat tinggal"
  }
}
```

Validasi format dan kelengkapan dilakukan backend. Kebijakan keunikan NIK masih perlu diputuskan sebelum diterapkan sebagai constraint database.

### 6.5. Melihat Gelombang Penerimaan

**Endpoint:** `GET /admission-waves`

Response sukses:

```
{
  "success": true,
  "message": "Data gelombang berhasil diambil.",
  "data": [
    {
      "id": 1,
      "name": "Gelombang 1",
      "start_date": "2026-11-01T00:00:00+07:00",
      "end_date": "2026-12-15T23:59:59+07:00",
      "is_active": true
    }
  ]
}
```

Tanggal di atas adalah data ilustrasi, bukan jadwal resmi. API publik bagi calon mahasiswa sebaiknya hanya menampilkan gelombang yang boleh dilihat dan digunakan untuk mendaftar.

### 6.6. Melihat Program Studi

**Endpoint:** `GET /study-programs`

Response sukses:

```
{
  "success": true,
  "message": "Data program studi berhasil diambil.",
  "data": [
    {
      "id": 1,
      "code": "IF",
      "name": "Informatika"
    },
    {
      "id": 2,
      "code": "SI",
      "name": "Sistem Informasi"
    }
  ]
}
```

Contoh hanya menampilkan sebagian data prototipe. Backend harus memfilter program studi aktif dan menggunakan daftar yang telah diverifikasi sebelum sistem dipakai secara resmi.

### 6.7. Membuat Pengajuan

**Endpoint:** `POST /applications`

Request:

```
{
  "wave_id": 1,
  "study_program_id": 1
}
```

Response sukses `201 Created`:

```
{
  "success": true,
  "message": "Draft pendaftaran berhasil dibuat.",
  "data": {
    "id": 501,
    "registration_number": "PMB-2026-000501",
    "wave_id": 1,
    "study_program_id": 1,
    "status": "draft",
    "submitted_at": null
  }
}
```

Aturan backend:

1. Pengguna harus login dengan role `calon_mahasiswa`.
    
2. Profil calon mahasiswa harus tersedia.
    
3. Gelombang harus sesuai aturan pendaftaran.
    
4. Program studi harus valid dan aktif.
    
5. Backend harus memeriksa pengajuan yang sudah ada.
    
6. Database tetap menerapkan `UNIQUE(applicant_id, wave_id)` untuk mencegah duplikasi saat terjadi permintaan bersamaan.
    
7. Nomor pendaftaran dibuat backend dan tidak boleh berasal dari nilai yang dipercaya begitu saja dari client.
    

Jika pengajuan pada gelombang tersebut sudah ada, API mengembalikan konflik dengan kode `APPLICATION_ALREADY_EXISTS`.

### 6.8. Melihat Daftar Pengajuan

**Endpoint:** `GET /applications`

Collection endpoint ini memakai pagination (`page`, `per_page`) dan response `{success, message, data, meta}`; scope tetap hanya pengajuan milik pemohon yang terautentikasi.

Response sukses:

```
{
  "success": true,
  "message": "Daftar pengajuan berhasil diambil.",
  "data": [
    {
      "id": 501,
      "registration_number": "PMB-2026-000501",
      "wave": {
        "id": 1,
        "name": "Gelombang 1"
      },
      "study_program": {
        "id": 1,
        "name": "Informatika"
      },
      "status": "draft",
      "submitted_at": null
    }
  ]
}
```

Endpoint ini hanya mengembalikan pengajuan milik calon mahasiswa yang sedang login. Collection response menggunakan envelope `{success, message, data, meta}` dengan metadata pagination (§10).

### 6.9. Mengirim Pengajuan

**Endpoint:** `POST /applications/{id}/submit` (hanya `draft` → `submitted`; pengiriman revisi menggunakan endpoint kanonik tersendiri)

Request:

```
{
  "expected_status": "draft"
}
```

Response sukses:

```
{
  "success": true,
  "message": "Pendaftaran berhasil dikirim untuk pemeriksaan.",
  "data": {
    "id": 501,
    "registration_number": "PMB-2026-000501",
    "status": "submitted",
    "submitted_at": "2026-11-10T10:30:00+07:00"
  }
}
```

Sebelum status diubah menjadi `submitted`, backend harus memastikan:

- Profil wajib sudah lengkap.
    
- Dokumen yang diwajibkan sudah diunggah.
    
- Format dan ukuran file memenuhi ketentuan.
    
- Pengajuan masih boleh dikirim sesuai aturan gelombang.
    
- Status sebelumnya memungkinkan pengiriman.
    
- Perubahan status dan pencatatan riwayat dilakukan secara konsisten.
    

Jika syarat belum terpenuhi, API mengembalikan `422 Unprocessable Entity` dengan rincian validasi.

### 6.9a. Mengirim Ulang Perbaikan

**Endpoint:** `POST /applications/{id}/resubmit`

Request wajib menyertakan `expected_status: "revision"` sebagai precondition.

Endpoint ini berbeda dari `/submit`: hanya untuk aplikasi berstatus `revision` dan mengembalikannya ke `review` setelah pemilik memperbaiki data/dokumen yang diminta. Request tidak menerima status tujuan bebas.

Bila file/dokumen diperbarui untuk revisi, unggah metadata baru sesuai aturan yang disepakati; kebijakan versi/replace per jenis dokumen masih menunggu DB-04. Jangan menghapus versi sebelumnya secara implisit.

Request:

```
{
  "expected_status": "revision"
}
```

Response sukses:

```
{
  "success": true,
  "message": "Perbaikan berhasil dikirim untuk diperiksa kembali.",
  "data": {
    "id": 501,
    "status": "review",
    "submitted_at": "2026-11-10T10:30:00+07:00"
  }
}
```

Backend memeriksa kepemilikan, memastikan pembaruan yang diminta telah diberikan, dan secara atomik mengubah status `revision` → `review` serta menambahkan status history. Jika `expected_status` tidak lagi sama dengan status saat ini, respons `409 STATE_CONFLICT`; detail semantik pemeriksaan ulang per dokumen tetap bergantung keputusan DB-01.

### 6.10. Mengunggah Dokumen

**Endpoint:** `POST /applications/{id}/documents`

Header:

```
Authorization: Bearer <access_token>
Accept: application/json
Content-Type: multipart/form-data
```

Field multipart:

|Field|Tipe|Wajib|Keterangan|
|---|---|---|---|
|`document_type`|String|Ya|Misalnya `photo` atau `diploma`.|
|`file`|File|Ya|Berkas yang akan diunggah.|

Response sukses `201 Created`:

```
{
  "success": true,
  "message": "Dokumen berhasil diunggah.",
  "data": {
    "id": 9001,
    "application_id": 501,
    "document_type": "diploma",
    "file_name": "ijazah.pdf",
    "file_size": 245760,
    "uploaded_at": "2026-11-10T10:15:00+07:00"
  }
}
```

Keamanan unggah dokumen:

- Validasi ekstensi, MIME type, dan ukuran file dilakukan di server.
    
- Jangan mempercayai nama file atau MIME type dari client tanpa pemeriksaan.
    
- Nama file penyimpanan dibuat oleh server.
    
- File disimpan pada disk privat, bukan direktori publik yang dapat diakses langsung.
    
- Unduhan harus melewati pemeriksaan autentikasi, kepemilikan, dan otorisasi.
    
- Path internal file tidak boleh dikembalikan jika dapat membocorkan struktur penyimpanan.
    

Batas ukuran dan format file harus ditetapkan sebelum implementasi final.

### 6.11. Melihat Status dan Riwayat

**Endpoint:** `GET /applications/{id}/status-history`

Response sukses:

```
{
  "success": true,
  "message": "Riwayat status berhasil diambil.",
  "data": [
    {
      "from_status": null,
      "to_status": "draft",
      "note": "Pengajuan dibuat.",
      "changed_at": "2026-11-10T09:45:00+07:00"
    },
    {
      "from_status": "draft",
      "to_status": "submitted",
      "note": "Pengajuan dikirim.",
      "changed_at": "2026-11-10T10:30:00+07:00"
    }
  ]
}
```

Riwayat diurutkan dari yang paling lama ke paling baru, kecuali kontrak endpoint menetapkan urutan lain secara eksplisit. Pengguna hanya dapat melihat riwayat pengajuan miliknya sendiri.

### 6.12. Meminta Revisi

**Endpoint:** `POST /admin/applications/{id}/request-revision`

Request:

```
{
  "expected_status": "review",
  "note": "Mohon unggah ulang ijazah dengan hasil pemindaian yang lebih jelas."
}
```

Response sukses:

```
{
  "success": true,
  "message": "Permintaan revisi berhasil dicatat.",
  "data": {
    "id": 501,
    "status": "revision",
    "note": "Mohon unggah ulang ijazah dengan hasil pemindaian yang lebih jelas."
  }
}
```

Hanya panitia yang memiliki kewenangan yang boleh meminta revisi. Catatan revisi wajib menjelaskan hal yang perlu diperbaiki. Perubahan status harus dicatat dalam `application_status_histories`.

### 6.13. Menetapkan Keputusan Akhir

**Endpoint:** `POST /admin/applications/{id}/decision`

Request untuk keputusan diterima:

```
{
  "expected_status": "review",
  "decision": "accepted",
  "note": "Berkas telah diperiksa dan keputusan ditetapkan oleh panitia."
}
```

Request untuk keputusan ditolak:

```
{
  "expected_status": "review",
  "decision": "rejected",
  "note": "Persyaratan pendaftaran tidak terpenuhi sesuai hasil pemeriksaan panitia."
}
```

Response sukses:

```
{
  "success": true,
  "message": "Keputusan pendaftaran berhasil disimpan.",
  "data": {
    "id": 501,
    "status": "accepted",
    "decided_at": "2026-11-20T14:00:00+07:00"
  }
}
```

Aturan:

- Hanya pengguna yang memiliki kewenangan penetapan keputusan yang dapat menjalankan endpoint ini.
    
- Nilai `decision` hanya boleh `accepted` atau `rejected`.
    
- Keputusan akhir hanya dapat dilakukan dari status yang diizinkan dalam alur bisnis.
    
- Catatan dan pelaku keputusan harus dapat ditelusuri.
    
- Perubahan status dan riwayat harus disimpan secara atomik.
    
- Perubahan keputusan setelah finalisasi memerlukan kebijakan khusus dan pencatatan audit.
    

### 6.14. Memulai Pemeriksaan Aplikasi

**Endpoint:** `POST /admin/applications/{id}/review`

Request:

```
{
  "expected_status": "submitted"
}
```

Aksi ini hanya mencatat transisi aplikasi `submitted` → `review` serta status history. Aksi ini tidak mencatat hasil atau catatan pemeriksaan masing-masing dokumen; definisi itu menunggu keputusan DB-01. Status `review` bukan bukti bahwa semua dokumen lolos pemeriksaan.

### 6.15. Error Konflik Status

Jika `expected_status` berbeda dari status aplikasi terkini, server mengembalikan HTTP `409 Conflict` tanpa perubahan aplikasi atau history:

```
{
  "success": false,
  "message": "Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.",
  "error": {
    "code": "STATE_CONFLICT"
  }
}
```

## 7. Format Response Standar

### 7.1. Response Sukses

```
{
  "success": true,
  "message": "Permintaan berhasil diproses.",
  "data": {}
}
```

### 7.2. Response Validasi Gagal

HTTP `422 Unprocessable Entity`:

```
{
  "success": false,
  "message": "Data yang dikirim belum valid.",
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {
      "nik": [
        "NIK harus terdiri dari 16 digit."
      ]
    }
  }
}
```

### 7.3. Response Tidak Terautentikasi

HTTP `401 Unauthorized`:

```
{
  "success": false,
  "message": "Autentikasi diperlukan.",
  "error": {
    "code": "UNAUTHENTICATED"
  }
}
```

### 7.4. Response Tidak Diizinkan

HTTP `403 Forbidden`:

```
{
  "success": false,
  "message": "Anda tidak memiliki izin untuk melakukan tindakan ini.",
  "error": {
    "code": "FORBIDDEN"
  }
}
```

### 7.5. Resource Tidak Ditemukan

HTTP `404 Not Found`:

```
{
  "success": false,
  "message": "Data tidak ditemukan.",
  "error": {
    "code": "RESOURCE_NOT_FOUND"
  }
}
```

Gunakan respons yang tidak membocorkan informasi pribadi atau keberadaan data yang tidak berhak diakses oleh pengguna.

## 8. HTTP Status Code

|Status Code|Penggunaan|
|---|---|
|`200 OK`|Permintaan berhasil.|
|`201 Created`|Resource baru berhasil dibuat.|
|`204 No Content`|Operasi berhasil tanpa response body.|
|`400 Bad Request`|Request tidak sesuai format yang diharapkan.|
|`401 Unauthorized`|Token tidak tersedia atau tidak valid.|
|`403 Forbidden`|Pengguna tidak memiliki izin.|
|`404 Not Found`|Resource tidak ditemukan atau tidak dapat diakses sesuai kebijakan keamanan.|
|`409 Conflict`|Terjadi konflik, misalnya pengajuan pada gelombang yang sama sudah ada (`APPLICATION_ALREADY_EXISTS`) atau precondition status basi (`STATE_CONFLICT`).|
|`413 Content Too Large`|Ukuran unggahan melebihi batas.|
|`419 Authentication Timeout`|Cookie/header CSRF browser hilang atau tidak valid; body tetap menggunakan envelope error JSON dan kode `CSRF_TOKEN_MISMATCH`.|
|`413 Content Too Large`|Ukuran unggahan melebihi batas.|
|`415 Unsupported Media Type`|Tipe media tidak didukung.|
|`422 Unprocessable Entity`|Validasi data gagal.|
|`429 Too Many Requests`|Batas permintaan terlampaui.|
|`500 Internal Server Error`|Terjadi kesalahan internal yang tidak terduga.|

Respons `500` tidak boleh menampilkan stack trace, kredensial, query database, atau detail internal server kepada client.

## 9. Validasi dan Transisi Status

Status pengajuan yang diizinkan:

`draft`, `submitted`, `review`, `revision`, `accepted`, `rejected`.

Rancangan transisi awal:

|Status Saat Ini|Status Berikutnya|Pelaku|
|---|---|---|
|`draft`|`submitted`|Calon mahasiswa|
|`submitted`|`review`|Panitia|
|`review`|`revision`|Panitia|
|`revision`|`review`|Calon mahasiswa pemilik setelah perbaikan dan pengiriman ulang (`POST /applications/{id}/resubmit`)|
|`review`|`accepted`|Panitia berwenang|
|`review`|`rejected`|Panitia berwenang|

Transisi tersebut diselaraskan dengan `Workflow & Business Process.md`: revisi yang dikirim ulang kembali ke `review`, bukan ke `submitted`. Setiap endpoint tindakan menetapkan transisi tetap; client tidak mengirim status arbitrer. DB-01 tetap memblokir rancangan final status/catatan per dokumen, bukan endpoint revisi pada tingkat aplikasi. Untuk mencegah stale writes, tindakan perubahan status mengirim precondition `expected_status`; mismatch dikembalikan sebagai `409 STATE_CONFLICT`.

## 10. Pagination, Filter, dan Pengurutan

Endpoint daftar pengajuan pemilik dan admin/panitia perlu mendukung pagination; endpoint operasional admin/panitia juga mendukung filter agar efisien saat jumlah pendaftar bertambah. Default `per_page=20`, batas maksimum rancangan `100`; client tidak boleh mengandalkan nilai lebih besar. Sort hanya berdasarkan allowlist. Nama query/filter final dapat ditetapkan saat implementasi tanpa mengubah batas akses.

Contoh:

```
GET /admin/applications?page=1&per_page=20&status=submitted&wave_id=1&study_program_id=1&sort=submitted_at&direction=desc
```

Parameter yang direncanakan:

|Parameter|Fungsi|
|---|---|
|`page`|Nomor halaman; default `1`.|
|`per_page`|Jumlah data per halaman; default `20`, maksimum `100`.|
|`status`|Memfilter status pengajuan.|
|`wave_id`|Memfilter gelombang penerimaan.|
|`study_program_id`|Memfilter program studi.|
|`search`|Mencari nomor pendaftaran atau nama sesuai hak akses.|
|`sort`|Memilih kolom pengurutan yang diizinkan.|
|`direction`|`asc` atau `desc`; default `desc` jika sort eksplisit, sesuai kolom allowlist.|
|`from` / `to`|Rentang tanggal pengiriman untuk laporan/list jika diberikan; format tanggal ISO 8601, tervalidasi dan dibatasi rentangnya.|
|`cursor`|Tidak digunakan pada rancangan awal; pagination memakai `page`/`per_page`.|

Backend harus menggunakan allowlist kolom sort, bukan memasukkan parameter sort langsung ke SQL.

Aturan konsistensi endpoint list:

- Semua collection response memakai `{success, message, data, meta}` dengan pagination metadata yang sama; endpoint tanpa pagination dapat menghilangkan `meta`.
- `per_page` default 20 dan maksimum 100; nilai di luar rentang menerima `422 VALIDATION_ERROR`.
- `search`, status, ID filter, date range, sort, dan direction divalidasi server-side; sort wajib allowlist.
- Scope otorisasi diterapkan sebelum filter/pagination: pemohon hanya melihat milik sendiri, panitia hanya data yang boleh diproses, pimpinan hanya agregat/laporan sesuai izin.
- Query `GET` tidak mengubah state; pagination/filter tidak pernah memperluas akses.

## 10.1. Precondition dan Konflik Status

Untuk semua endpoint yang mengubah status aplikasi (`submit`, `resubmit`, `review`, `request-revision`, `decision`), request menyertakan `expected_status` sama dengan status yang dilihat client. Server memeriksa status saat ini dan kewenangan dalam transaksi sebelum menulis perubahan dan history. Jika berbeda, server mengembalikan `409 Conflict` dengan `error.code = STATE_CONFLICT` tanpa perubahan parsial. Ini adalah optimistic concurrency check tingkat aplikasi; implementasi boleh menggunakan lock row/transaksi untuk race condition, dan wajib memiliki constraint/integritas sesuai Tahap 3.

Response status action selalu menyertakan status baru dan waktu transisi; ID history disertakan bila field tersedia pada skema yang disetujui.

Contoh response pagination:

```
{
  "success": true,
  "message": "Data pengajuan berhasil diambil.",
  "data": [],
  "meta": {
    "current_page": 1,
    "per_page": 20,
    "total": 0,
    "last_page": 1
  }
}
```

## 11. Transaksi dan Konsistensi Data

Operasi berikut harus menggunakan transaksi database jika melibatkan beberapa perubahan yang harus berhasil bersama:

1. Pembuatan pengajuan dan pencatatan status awal.
    
2. Pengiriman pengajuan dan pencatatan riwayat status.
    
3. Perubahan status oleh panitia dan pencatatan pelaku perubahan.
    
4. Penetapan keputusan akhir dan penyimpanan riwayat keputusan.
    

Contoh pola transaksi Laravel:

```
DB::transaction(function () use ($application, $newStatus, $userId) {
    $oldStatus = $application->status;

    $application->update([
        'status' => $newStatus,
    ]);

    ApplicationStatusHistory::create([
        'application_id' => $application->id,
        'from_status' => $oldStatus,
        'to_status' => $newStatus,
        'changed_by_user_id' => $userId,
    ]);
});
```

Contoh ini menunjukkan pola dasar. Implementasi final harus memuat validasi transisi status, pemeriksaan otorisasi, dan perlindungan terhadap perubahan bersamaan. Untuk operasi yang berpotensi dijalankan secara bersamaan, pertimbangkan penguncian baris (`lockForUpdate`) di dalam transaksi dan tetap gunakan constraint database.

Jika proses unggah file berhasil tetapi transaksi database gagal, backend harus menangani file sementara atau menghapus file yang tidak lagi memiliki metadata. Operasi file dan database tidak menjadi satu transaksi atomik secara otomatis.

## 12. Keamanan API

1. Gunakan HTTPS pada lingkungan produksi.
    
2. Hash password menggunakan mekanisme aman yang disediakan Laravel.
    
3. Validasi autentikasi dan role pada setiap endpoint terlindungi.
    
4. Pastikan calon mahasiswa hanya dapat mengakses profil, pengajuan, dokumen, dan riwayat miliknya.
    
5. Gunakan validasi request server-side.
    
6. Batasi ukuran dan tipe file yang dapat diunggah.
    
7. Simpan dokumen pada penyimpanan privat.
    
8. Jangan mengirim password, token, atau informasi rahasia dalam log; gunakan cookie Sanctum + CSRF pada browser dan bearer token tersimpan aman pada Android.
    
9. Terapkan rate limiting terutama pada login, registrasi, dan endpoint unggah dokumen.
    
10. Gunakan pesan error yang tidak membocorkan detail internal.
    
11. Catat aktivitas penting seperti keputusan akhir dan perubahan role.
    
12. Batasi data pribadi yang ditampilkan pada dashboard dan laporan berdasarkan kebutuhan akses.
    

## 13. Pemetaan API ke Modul Sistem

|Modul|Endpoint Utama|
|---|---|
|MOD-01 Auth & Authorization|`/auth/register`, `/auth/login`, `/auth/session/login`, `/auth/logout`, `/auth/session/logout`, `/auth/me`|
|MOD-02 Applicant Profile|`/applicant/profile`|
|MOD-03 Admission Waves|`/admission-waves`, `/admin/admission-waves`|
|MOD-04 Study Programs|`/study-programs`, `/admin/study-programs`|
|MOD-05 Application|`/applications`|
|MOD-06 Application Documents|`/applications/{id}/documents`|
|MOD-07 Review & Revision|`/admin/applications/{id}/review`, `/admin/applications/{id}/request-revision`, `/applications/{id}/resubmit`|
|MOD-08 Admissions Decision|`/admin/applications/{id}/decision`|
|MOD-09 Status History|`/applications/{id}/status-history`|
|MOD-10 Dashboard & Reports|`/admin/dashboard`, `/admin/reports/applications`|
|MOD-11 User Administration|`/admin/users`|
|MOD-12 Validation & Error Handling|Berlaku untuk seluruh endpoint|

Pemetaan ini menghubungkan kontrak API dengan spesifikasi modul agar implementasi dan pengujian lebih mudah ditelusuri.

## 14. Checklist Implementasi

- Semua resource/auth application API menggunakan prefix versi `/api/v1`; bootstrap `GET /sanctum/csrf-cookie` adalah endpoint infrastruktur Sanctum di luar prefix.
    
- Format JSON konsisten.
    
- Auth Sanctum browser memakai session cookie + CSRF dan Android memakai bearer token; logout tiap mode mengakhiri kredensial yang benar.
    
- Role dan kepemilikan data divalidasi di backend.
    
- Validasi profil dan dokumen diterapkan.
    
- Pengajuan ganda pada gelombang yang sama dicegah.
    
- Transisi status divalidasi di backend.
    
- Perubahan status dan riwayat disimpan secara konsisten.
    
- Unggah dan unduh dokumen menggunakan penyimpanan privat.
    
- Pagination dan filter daftar admin tersedia.
    
- Error response mengikuti kontrak.
    
- Rate limiting diterapkan pada endpoint yang berisiko.
    
- Endpoint diuji menggunakan Postman atau pengujian otomatis.
    
- Dokumentasi diperbarui jika implementasi berbeda dari rancangan.
    

## 15. Keputusan yang Masih Terbuka

1. Versi Laravel/Sanctum dan nilai token TTL/expiry, rotasi, pencabutan serta kebijakan sesi browser.
    
2. Domain admin dan origin CORS yang diizinkan per environment.
    
3. Batas ukuran dan format file.
    
4. Aturan revisi, termasuk batas jumlah revisi dan tenggat waktu; DB-01 juga menentukan hasil pemeriksaan per dokumen.
    
5. Matriks hak akses detail admin, panitia, dan pimpinan; siapa yang memegang izin keputusan akhir.
    
6. Apakah penugasan pengajuan kepada panitia diperlukan dalam MVP.
    
7. Format final nomor pendaftaran.
    
8. Filter final laporan PDF dan kebijakan data PII yang boleh disertakan.
    
9. Target performa/pagination di atas batas awal per_page 100 jika dibutuhkan.
    
10. Kebijakan perubahan keputusan akhir.
    
11. Konfigurasi domain, HTTPS, CORS, cookie, dan server untuk tiap environment.
    
12. Apakah operasi POST tertentu memerlukan Idempotency-Key untuk retry setelah timeout; constraint unik tetap wajib untuk aplikasi per gelombang.
    
13. Kolom/tabel history yang tersedia untuk mengembalikan ID event pada response status.
    

---

## 16. Kesimpulan dan Status Tahap 4

API Contract Tahap 4 mendefinisikan komunikasi antara Android Kotlin dan admin React + TypeScript + Vite melalui Laravel REST API berbasis JSON. Rancangan menyelaraskan autentikasi Sanctum per klien, endpoint, otorisasi, pagination, transisi/revisi, konflik status, transaksi, dan format error.

Dokumen ini **proposed API design**, bukan implementasi terverifikasi. Backend belum tersedia. DB-01 (hasil review per dokumen), DB-02 (audit persisten/retensi), kewenangan keputusan, batas file, token expiry, origin CORS, dan kebijakan retry/idempotency masih menjadi dependency bagi sebagian implementasi. Endpoint review tingkat aplikasi tidak mengklaim menyimpan hasil review tiap dokumen sampai DB-01 diputuskan.

**Checkpoint API Design: ACCEPTED WITH LIMITATIONS** sebagai kontrak konseptual. Backend/auth, koneksi database, dan runtime API belum dibuat atau diuji. MASTERPROMPT memberi label Tahap 4 pada Backend Foundation & Auth, sedangkan roadmap Build menyebut Tahap 4 sebagai API Design; pekerjaan ini hanya menyelesaikan deliverable API Design, bukan backend foundation.
