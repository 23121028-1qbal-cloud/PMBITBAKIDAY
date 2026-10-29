  

# System Design — PMB ITB Akiday

## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Proyek|Sistem Penerimaan Mahasiswa Baru ITB Akiday|
|Nama Dokumen|System Design|
|Versi|1.0|
|Status|Baseline|
|Tanggal|18 Oktober 2026|
|Platform|Android dan Website Admin|
|Backend|Laravel REST API|
|Database|MySQL|
|Deployment Awal|Local Development|

## 2. Tujuan Dokumen

Dokumen System Design menjelaskan rancangan teknis Sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday sebagai pedoman pengembangan aplikasi. Dokumen ini menjadi penghubung antara kebutuhan yang telah ditetapkan pada PRD dan SRS dengan implementasi program, database, API, antarmuka, keamanan, serta pengujian sistem.

Sistem dikembangkan dengan dua antarmuka utama, yaitu aplikasi Android untuk calon mahasiswa dan website untuk pengelolaan penerimaan mahasiswa baru oleh admin, panitia, dan pimpinan. Kedua antarmuka menggunakan backend Laravel sebagai pusat pemrosesan data dan MySQL sebagai penyimpanan data utama.

## 3. Ruang Lingkup Sistem

Sistem mencakup fungsi berikut:

1. Registrasi akun dan login pengguna.
    
2. Pengelolaan profil calon mahasiswa.
    
3. Pengelolaan gelombang penerimaan.
    
4. Penyediaan informasi program studi.
    
5. Pengisian dan pengiriman formulir pendaftaran.
    
6. Pengunggahan dokumen persyaratan.
    
7. Pemeriksaan kelengkapan dan validasi data.
    
8. Pemeriksaan pendaftaran oleh panitia.
    
9. Permintaan revisi dokumen atau data.
    
10. Penetapan keputusan diterima atau ditolak oleh pihak berwenang.
    
11. Pemantauan status pendaftaran oleh calon mahasiswa.
    
12. Pengelolaan akun dan hak akses.
    
13. Penyajian dashboard dan laporan penerimaan.
    
14. Pencatatan riwayat perubahan status pendaftaran.
    

Sistem tidak menentukan kelulusan akademik secara otomatis. Sistem hanya membantu memvalidasi kelengkapan data, sedangkan keputusan akhir tetap dilakukan oleh panitia atau pihak berwenang.

## 4. Arsitektur Sistem

### 4.1. Pendekatan Arsitektur

Sistem menggunakan arsitektur client-server dengan pendekatan three-tier architecture yang terdiri dari:

- **Presentation Layer:** aplikasi Android dan website admin.
    
- **Application Layer:** backend Laravel yang menyediakan REST API, validasi, autentikasi, otorisasi, serta proses bisnis.
    
- **Data Layer:** MySQL dan penyimpanan dokumen pendaftaran.
    

Pemisahan ini memungkinkan aplikasi Android dan website menggunakan aturan bisnis serta sumber data yang sama. Perubahan proses bisnis dapat dilakukan di backend tanpa harus menduplikasi seluruh logika pada setiap antarmuka.

### 4.2. Diagram Arsitektur

```
flowchart TB
    subgraph CLIENT["Presentation Layer"]
        A["Aplikasi Android<br/>Calon Mahasiswa"]
        W["Website Admin<br/>Admin, Panitia, Pimpinan"]
    end

    subgraph APP["Application Layer"]
        API["Laravel REST API"]
        AUTH["Authentication & Authorization"]
        PROFILE["Profile & User Management"]
        REG["Application Management"]
        DOC["Document Management"]
        REVIEW["Review, Revision & Decision"]
        REPORT["Dashboard & Reporting"]
    end

    subgraph DATA["Data Layer"]
        DB[("MySQL Database")]
        FS[("Private Document Storage")]
    end

    A -->|"HTTPS / JSON"| API
    W -->|"HTTPS / JSON"| API

    API --> AUTH
    API --> PROFILE
    API --> REG
    API --> DOC
    API --> REVIEW
    API --> REPORT

    AUTH --> DB
    PROFILE --> DB
    REG --> DB
    REVIEW --> DB
    REPORT --> DB
    DOC --> DB
    DOC --> FS
```

**Penjelasan:** Aplikasi Android dan website admin tidak mengakses MySQL secara langsung. Semua permintaan dikirim ke Laravel melalui REST API. Backend memeriksa identitas, hak akses, validitas data, dan aturan bisnis sebelum membaca atau mengubah data.

Untuk pengembangan lokal, seluruh komponen dapat dijalankan pada komputer pengembang atau jaringan lokal yang telah dikonfigurasi. Ketika sistem dipublikasikan, konfigurasi server, domain, HTTPS, dan penyimpanan harus disesuaikan dengan lingkungan produksi.

## 5. Komponen Utama Sistem

### 5.1. Aplikasi Android

Aplikasi Android dikembangkan menggunakan Kotlin dan digunakan oleh calon mahasiswa.

Tanggung jawab utama:

- Registrasi akun dan login.
    
- Melihat serta mengubah profil.
    
- Melihat gelombang penerimaan yang tersedia.
    
- Melihat daftar program studi.
    
- Membuat dan mengisi pendaftaran.
    
- Mengunggah foto dan ijazah atau Surat Keterangan Lulus (SKL).
    
- Mengirim pendaftaran.
    
- Melihat status dan riwayat pendaftaran.
    
- Melihat permintaan revisi dari panitia.
    

Aplikasi Android hanya menangani tampilan, interaksi pengguna, dan pengiriman permintaan ke API. Validasi penting dan penentuan hak akses tetap dilakukan oleh backend.

### 5.2. Website Admin

Website admin digunakan oleh tiga jenis pengguna internal: admin, panitia, dan pimpinan.

**Admin**

- Mengelola akun dan status aktif pengguna.
    
- Mengelola gelombang penerimaan.
    
- Mengelola program studi.
    
- Mengatur akses sesuai peran.
    

**Panitia**

- Melihat pendaftaran yang perlu diperiksa.
    
- Memeriksa data dan dokumen.
    
- Meminta revisi.
    
- Memperbarui hasil pemeriksaan.
    
- Menetapkan keputusan sesuai kewenangannya.
    
- Melihat data pendaftaran untuk kebutuhan operasional.
    

**Pimpinan**

- Melihat dashboard dan ringkasan penerimaan.
    
- Melihat laporan berdasarkan gelombang dan program studi.
    
- Mengunduh laporan yang diizinkan.
    

Hak akses setiap peran harus diterapkan di backend, bukan hanya dengan menyembunyikan menu pada website.

### 5.3. Backend Laravel

Laravel menjadi pusat logika bisnis dan penyedia REST API.

Tanggung jawabnya meliputi:

- Autentikasi dan otorisasi.
    
- Validasi permintaan.
    
- Pengelolaan data pengguna dan profil.
    
- Pengelolaan gelombang serta program studi.
    
- Pengelolaan pendaftaran.
    
- Validasi dan penyimpanan dokumen.
    
- Pengelolaan proses pemeriksaan, revisi, dan keputusan.
    
- Pencatatan riwayat status.
    
- Pembuatan laporan.
    
- Penanganan kesalahan dan transaksi database.
    

Struktur backend disarankan menggunakan pemisahan route, controller, request validation, policy, service, model, dan API resource sesuai kebutuhan.

### 5.4. Database MySQL

MySQL menjadi sumber data utama untuk menyimpan akun, profil, gelombang, program studi, pendaftaran, dokumen, serta riwayat status.

Database harus menggunakan primary key, foreign key, unique constraint, dan transaksi pada proses yang memerlukan konsistensi data.

### 5.5. Penyimpanan Dokumen

File foto dan ijazah/SKL disimpan dalam penyimpanan privat, bukan sebagai file publik yang dapat diakses siapa pun melalui URL langsung.

Database menyimpan metadata dokumen, seperti jenis dokumen, lokasi penyimpanan, tipe MIME, ukuran file, dan waktu unggah. File diakses melalui backend setelah pemeriksaan autentikasi dan hak akses.

## 6. Desain Alur Komunikasi

### 6.1. Pola Request dan Response

Komunikasi antara client dan backend menggunakan REST API dengan format JSON. Pengiriman dokumen menggunakan `multipart/form-data`.

Contoh permintaan untuk melihat status pendaftaran:

```
GET /api/applications
Accept: application/json
Authorization: Bearer <access_token>
```

Contoh respons:

```
{
  "success": true,
  "message": "Data pendaftaran berhasil diambil.",
  "data": [
    {
      "registration_number": "PMB-2026-0001",
      "status": "review",
      "wave": "Gelombang 1",
      "study_program": "Informatika"
    }
  ]
}
```

Nomor pendaftaran di atas hanya contoh format, bukan nomor pendaftaran aktual.

### 6.2. Alur Permintaan API

```
flowchart TD
    A["Client mengirim request"] --> B["Laravel menerima request"]
    B --> C{"Autentikasi valid?"}
    C -->|Tidak| D["Respons 401 Unauthorized"]
    C -->|Ya| E{"Hak akses sesuai?"}
    E -->|Tidak| F["Respons 403 Forbidden"]
    E -->|Ya| G["Validasi input"]
    G --> H{"Input valid?"}
    H -->|Tidak| I["Respons 422 Unprocessable Entity"]
    H -->|Ya| J["Jalankan proses bisnis"]
    J --> K{"Proses berhasil?"}
    K -->|Tidak| L["Respons error yang sesuai"]
    K -->|Ya| M["Kirim respons JSON"]
```

Tidak semua endpoint harus memerlukan autentikasi. Endpoint informasi publik, seperti daftar gelombang yang dipublikasikan, dapat diakses tanpa login jika diizinkan oleh aturan sistem.

## 7. Desain Database

### 7.1. Tabel Utama

Rancangan awal menggunakan tabel berikut:

|Tabel|Fungsi|
|---|---|
|`roles`|Menyimpan jenis peran pengguna.|
|`users`|Menyimpan akun, email, password hash, dan status aktif.|
|`applicant_profiles`|Menyimpan data pribadi calon mahasiswa.|
|`admission_waves`|Menyimpan informasi gelombang penerimaan.|
|`study_programs`|Menyimpan daftar program studi.|
|`applications`|Menyimpan data pendaftaran calon mahasiswa.|
|`application_documents`|Menyimpan metadata dokumen persyaratan.|
|`application_status_histories`|Menyimpan riwayat perubahan status.|

Rancangan tersebut merupakan baseline awal. Detail kolom, indeks, constraint, serta kemungkinan kebutuhan tabel tambahan dijelaskan lebih lanjut pada dokumen Database Model dan Data Dictionary.

### 7.2. Relasi Utama

```
erDiagram
    ROLES ||--o{ USERS : memiliki
    USERS ||--o| APPLICANT_PROFILES : memiliki
    APPLICANT_PROFILES ||--o{ APPLICATIONS : membuat
    ADMISSION_WAVES ||--o{ APPLICATIONS : menampung
    STUDY_PROGRAMS ||--o{ APPLICATIONS : dipilih
    APPLICATIONS ||--o{ APPLICATION_DOCUMENTS : memiliki
    APPLICATIONS ||--o{ APPLICATION_STATUS_HISTORIES : mencatat
    USERS o|--o{ APPLICATION_STATUS_HISTORIES : mengubah
```

Relasi tersebut menunjukkan bahwa satu profil calon mahasiswa dapat memiliki beberapa pendaftaran pada gelombang yang berbeda. Namun, satu calon mahasiswa hanya boleh memiliki satu pendaftaran pada gelombang yang sama.

Aturan tersebut wajib diperkuat dengan constraint database:

```
UNIQUE (applicant_id, wave_id)
```

Constraint ini mencegah dua permintaan yang datang hampir bersamaan membuat pendaftaran ganda untuk calon mahasiswa dan gelombang yang sama. Pemeriksaan di backend tetap diperlukan agar pengguna mendapatkan pesan kesalahan yang jelas.

### 7.3. Konsistensi Data

Untuk menjaga konsistensi:

1. Gunakan foreign key untuk menjaga hubungan antartabel.
    
2. Gunakan unique constraint untuk email, nomor pendaftaran, kode program studi, dan kombinasi pendaftaran per gelombang sesuai aturan.
    
3. Gunakan transaksi database saat satu proses mengubah beberapa data yang saling bergantung.
    
4. Simpan riwayat perubahan status bersama perubahan status yang bersangkutan.
    
5. Terapkan validasi status agar perubahan hanya terjadi melalui alur yang diperbolehkan.
    
6. Jangan menyimpan password dalam bentuk teks biasa.
    
7. Hindari menghapus data yang masih dibutuhkan sebagai riwayat audit.
    

## 8. Desain Proses Pendaftaran

### 8.1. Alur Calon Mahasiswa

```
flowchart TD
    A["Registrasi akun"] --> B["Login"]
    B --> C["Lengkapi profil"]
    C --> D["Pilih gelombang"]
    D --> E["Pilih program studi"]
    E --> F["Isi formulir"]
    F --> G["Unggah dokumen"]
    G --> H["Validasi data dan dokumen"]
    H --> I{"Persyaratan lengkap?"}
    I -->|Tidak| J["Tampilkan kesalahan"]
    J --> F
    I -->|Ya| K["Kirim pendaftaran"]
    K --> L["Status submitted"]
    L --> M["Panitia memeriksa"]
    M --> N["Status review"]
    N --> O{"Hasil pemeriksaan"}
    O -->|Perlu perbaikan| P["Status revision"]
    P --> Q["Calon mahasiswa memperbaiki data"]
    Q --> H
    O -->|Memenuhi persyaratan| R["Keputusan diterima"]
    O -->|Tidak memenuhi persyaratan| S["Keputusan ditolak"]
```

### 8.2. Aturan Status

Status pendaftaran yang digunakan:

|Status|Makna|
|---|---|
|`draft`|Pendaftaran masih disiapkan dan belum dikirim.|
|`submitted`|Pendaftaran sudah dikirim untuk diperiksa.|
|`review`|Panitia sedang memeriksa pendaftaran.|
|`revision`|Calon mahasiswa perlu memperbaiki data atau dokumen.|
|`accepted`|Pendaftaran dinyatakan diterima oleh pihak berwenang.|
|`rejected`|Pendaftaran dinyatakan ditolak oleh pihak berwenang.|

Backend harus membatasi transisi status sesuai aturan bisnis. Sebagai contoh, pendaftaran yang masih berstatus `draft` tidak boleh langsung ditetapkan menjadi `accepted` tanpa melewati proses pemeriksaan dan keputusan yang sah.

Setiap perubahan status harus mencatat status sebelumnya, status baru, waktu perubahan, pelaku perubahan, dan catatan jika diperlukan. Perubahan keputusan akhir hanya dapat dilakukan jika kebijakan institusi mengizinkannya dan harus tercatat dalam riwayat.

## 9. Desain Autentikasi dan Otorisasi

### 9.1. Autentikasi

Pengguna masuk menggunakan email dan password. Password disimpan menggunakan mekanisme hashing yang aman dari Laravel, bukan enkripsi yang dapat dibalik atau teks biasa.

Setelah login, backend memberikan mekanisme autentikasi untuk permintaan berikutnya. Untuk API, penggunaan Laravel Sanctum dapat dipertimbangkan sebagai pilihan awal, dengan konfigurasi yang sesuai untuk aplikasi Android dan website admin.

Token atau kredensial autentikasi harus disimpan secara aman pada client. Token tidak boleh ditampilkan dalam log, pesan kesalahan, atau laporan.

### 9.2. Role-Based Access Control (RBAC)

Sistem menggunakan RBAC untuk membatasi akses berdasarkan peran.

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Mengelola profil sendiri|Ya|Sesuai kebutuhan|Tidak secara default|Tidak|
|Membuat pendaftaran|Ya|Tidak secara default|Tidak|Tidak|
|Melihat pendaftaran sendiri|Ya|Sesuai kewenangan|Sesuai tugas|Ringkasan|
|Mengelola akun|Tidak|Ya|Tidak secara default|Tidak|
|Mengelola gelombang|Tidak|Ya|Sesuai kewenangan|Tidak|
|Mengelola program studi|Tidak|Ya|Sesuai kewenangan|Tidak|
|Memeriksa dokumen|Tidak|Sesuai kewenangan|Ya|Tidak|
|Meminta revisi|Tidak|Sesuai kewenangan|Ya|Tidak|
|Menetapkan keputusan|Tidak|Sesuai kebijakan|Sesuai kewenangan|Sesuai kebijakan|
|Melihat laporan|Riwayat sendiri|Ya|Sesuai tugas|Ya|

Kewenangan penetapan keputusan harus dikonfirmasi berdasarkan kebijakan institusi. Tabel ini merupakan rancangan awal, bukan penetapan struktur kewenangan resmi.

### 9.3. Perlindungan Data

- Calon mahasiswa hanya boleh melihat dan mengubah pendaftaran miliknya.
    
- Pengambilan data berdasarkan ID harus tetap memeriksa kepemilikan atau hak akses.
    
- Dokumen privat tidak boleh diakses tanpa otorisasi.
    
- Input harus divalidasi pada backend.
    
- Website harus menerapkan perlindungan terhadap CSRF jika menggunakan autentikasi berbasis cookie.
    
- Endpoint harus menerapkan pembatasan percobaan login dan perlindungan terhadap penyalahgunaan.
    
- Data pribadi hanya ditampilkan kepada pengguna yang memiliki hak akses.
    
- Aktivitas penting seperti keputusan, perubahan status, dan pengelolaan akun harus dapat ditelusuri.
    

## 10. Desain Validasi Dokumen

Dokumen yang diwajibkan pada baseline awal:

1. Foto calon mahasiswa.
    
2. Ijazah atau Surat Keterangan Lulus (SKL).
    

Pemeriksaan dilakukan dalam dua tingkat.

**Validasi otomatis**

- Memeriksa apakah file tersedia.
    
- Memeriksa ekstensi dan tipe file yang diizinkan.
    
- Memeriksa ukuran file.
    
- Memeriksa apakah file dapat diproses.
    
- Memeriksa apakah seluruh dokumen wajib telah diunggah.
    

**Pemeriksaan manual**

- Panitia menilai apakah dokumen sesuai persyaratan.
    
- Panitia meminta revisi jika dokumen tidak jelas atau tidak sesuai.
    
- Panitia melakukan pemeriksaan substantif sesuai kebijakan penerimaan.
    

Daftar tipe file dan batas ukuran maksimum masih perlu ditetapkan sebelum implementasi final. File sebaiknya diberi nama penyimpanan yang tidak mudah ditebak, tidak dieksekusi sebagai program, dan ditempatkan di luar direktori publik.

## 11. Desain Laporan

Sistem menyediakan dashboard dan laporan untuk mendukung pemantauan penerimaan.

Informasi yang direncanakan:

- Total pendaftar.
    
- Jumlah pendaftaran berdasarkan status.
    
- Jumlah pendaftar per gelombang.
    
- Jumlah pendaftar per program studi.
    
- Daftar pendaftaran yang sedang diperiksa.
    
- Daftar pendaftaran yang memerlukan revisi.
    
- Rekapitulasi keputusan penerimaan.
    

Laporan dapat difilter berdasarkan gelombang dan program studi. Pengunduhan PDF harus mengikuti hak akses pengguna dan menggunakan data yang diambil dari backend.

Angka dashboard harus berasal dari data database yang konsisten. Jika beberapa nilai dihitung dari query berbeda, aturan filter dan waktu pengambilan data harus dibuat konsisten agar hasilnya tidak saling bertentangan.

## 12. Penanganan Kesalahan

Backend menggunakan kode HTTP sesuai jenis hasil permintaan.

|HTTP Status|Penggunaan|
|---|---|
|`200 OK`|Permintaan berhasil.|
|`201 Created`|Data baru berhasil dibuat.|
|`400 Bad Request`|Format permintaan tidak sesuai.|
|`401 Unauthorized`|Pengguna belum terautentikasi atau autentikasi tidak valid.|
|`403 Forbidden`|Pengguna tidak memiliki hak akses.|
|`404 Not Found`|Data atau endpoint tidak ditemukan.|
|`409 Conflict`|Terjadi konflik data, misalnya pendaftaran ganda.|
|`422 Unprocessable Entity`|Data tidak lolos validasi.|
|`429 Too Many Requests`|Permintaan melewati batas yang diizinkan.|
|`500 Internal Server Error`|Terjadi kesalahan internal yang tidak terduga.|

Respons kesalahan tidak boleh membocorkan password, token, detail query SQL, lokasi file sensitif, atau stack trace kepada pengguna. Detail teknis dicatat pada log server dengan pengamanan yang sesuai.

## 13. Struktur Implementasi Backend

Struktur Laravel yang disarankan:

```
app/
├── Http/
│   ├── Controllers/
│   │   └── Api/
│   ├── Requests/
│   ├── Resources/
│   └── Middleware/
├── Models/
├── Policies/
├── Services/
└── Exceptions/

database/
├── migrations/
├── factories/
└── seeders/

routes/
└── api.php

storage/
└── app/
    └── private/
```

Keterangan:

- **Controllers:** menerima permintaan dan mengatur respons.
    
- **Requests:** memvalidasi input.
    
- **Resources:** menentukan format data respons API.
    
- **Middleware:** menangani autentikasi dan pemeriksaan akses umum.
    
- **Models:** merepresentasikan entitas database.
    
- **Policies:** memeriksa hak akses terhadap sumber daya tertentu.
    
- **Services:** menampung proses bisnis yang kompleks atau digunakan bersama.
    
- **Migrations:** mendefinisikan perubahan struktur database.
    
- **Seeders:** menyiapkan data awal untuk pengembangan dan pengujian.
    
- **Private storage:** menyimpan dokumen yang tidak boleh diakses secara publik.
    

Struktur ini adalah pedoman, bukan kewajiban untuk membuat seluruh file sejak awal. Komponen dibuat sesuai kebutuhan agar implementasi tetap sederhana dan mudah diuji.

## 14. Struktur Implementasi Android

Struktur Android berbasis Kotlin disarankan menggunakan pemisahan UI, ViewModel, Repository, layanan API, dan model data.

```
app/
└── src/main/java/
    └── .../
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
        ├── viewmodel/
        └── util/
```

Tanggung jawab utama:

- **UI:** menampilkan halaman dan menerima interaksi pengguna.
    
- **ViewModel:** mengelola state tampilan dan proses UI.
    
- **Repository:** menjadi penghubung antara UI dan sumber data.
    
- **Remote/API service:** mengirim permintaan ke Laravel.
    
- **Model:** merepresentasikan data yang diterima atau dikirim.
    
- **Util:** menyimpan fungsi pendukung yang digunakan bersama.
    

Pilihan pustaka HTTP, navigasi, dependency injection, dan penyimpanan token ditentukan pada tahap implementasi sesuai kebutuhan proyek.

## 15. Desain Pengujian

Pengujian harus dilakukan pada beberapa tingkat.

### 15.1. Unit Testing

Menguji fungsi terpisah, misalnya:

- Validasi data profil.
    
- Aturan transisi status.
    
- Validasi dokumen wajib.
    
- Perhitungan ringkasan laporan.
    

### 15.2. Integration Testing

Menguji hubungan antarbagian:

- Registrasi dan login dengan database.
    
- Pembuatan pendaftaran dengan constraint unik.
    
- Pengunggahan dokumen dan penyimpanan metadata.
    
- Perubahan status dan pencatatan riwayat.
    
- Pengambilan data melalui API.
    

### 15.3. API Testing

Memastikan endpoint memberikan status HTTP, struktur JSON, validasi, dan kontrol akses yang sesuai.

### 15.4. End-to-End Testing

Menguji alur lengkap dari registrasi calon mahasiswa sampai panitia memeriksa pendaftaran dan pengguna melihat hasilnya.

### 15.5. Pengujian Keamanan

Menguji akses tanpa login, akses lintas akun, manipulasi ID pendaftaran, akses dokumen privat, dan penggunaan endpoint oleh peran yang tidak berwenang.

Hasil pengujian aktual harus dicatat pada `Test Results.md`. Dokumen desain ini tidak menyatakan bahwa pengujian telah berhasil dijalankan.

## 16. Deployment Awal

Pada tahap awal, sistem dijalankan di lingkungan lokal.

Komponen yang perlu dikonfigurasi:

- Android Studio untuk aplikasi Android.
    
- PHP dan Composer untuk Laravel.
    
- MySQL untuk database.
    
- Konfigurasi `.env` untuk koneksi database dan aplikasi.
    
- Penyimpanan privat untuk dokumen.
    
- Alamat base URL API yang dapat dijangkau oleh client.
    

**Catatan penting:** `localhost` pada Android emulator atau perangkat fisik tidak otomatis mengarah ke komputer yang menjalankan Laravel. Untuk emulator maupun perangkat fisik, base URL harus disesuaikan dengan lingkungan pengujian. Jika menggunakan perangkat fisik, perangkat harus dapat menjangkau komputer server melalui jaringan yang sesuai.

File `.env`, password database, kredensial, dan token tidak boleh dimasukkan ke repository Git. Saat dipindahkan ke server produksi, sistem perlu dikonfigurasi dengan HTTPS, akses database yang dibatasi, backup, log, dan izin penyimpanan yang tepat.

Panduan langkah operasional lebih rinci akan ditempatkan pada `09 - Deployment/Deployment Guide.md` dan `Environment Configuration.md`.

## 17. Risiko Teknis dan Mitigasi

|Risiko|Dampak|Mitigasi|
|---|---|---|
|Pendaftaran ganda akibat permintaan bersamaan|Data pendaftaran tidak konsisten|Unique constraint pada kombinasi calon mahasiswa dan gelombang, validasi backend, serta penanganan konflik.|
|Dokumen diakses pengguna yang tidak berwenang|Kebocoran data pribadi|Private storage, pemeriksaan hak akses, dan pengiriman file melalui backend.|
|Perubahan status tidak tercatat|Sulit melakukan audit|Catat perubahan status dalam transaksi database.|
|Akses data lintas akun|Pelanggaran privasi|Pemeriksaan kepemilikan data pada setiap endpoint terkait.|
|File berbahaya diunggah|Risiko keamanan server|Validasi tipe dan ukuran, nama file aman, serta penyimpanan nonpublik.|
|Konfigurasi API lokal salah|Client gagal terhubung|Gunakan base URL sesuai emulator, perangkat, atau jaringan lokal.|
|Kehilangan database|Hilangnya data pendaftaran|Siapkan backup dan prosedur pemulihan yang diuji.|
|Laporan menampilkan angka berbeda|Informasi penerimaan tidak akurat|Samakan aturan filter, query, dan definisi setiap metrik.|

## 18. Keputusan Desain

Keputusan desain baseline:

1. Menggunakan arsitektur client-server.
    
2. Menggunakan aplikasi Android Kotlin untuk calon mahasiswa.
    
3. Menggunakan website untuk admin, panitia, dan pimpinan.
    
4. Menggunakan Laravel sebagai backend REST API.
    
5. Menggunakan MySQL sebagai database utama.
    
6. Menggunakan JSON untuk komunikasi API, kecuali unggah file yang menggunakan `multipart/form-data`.
    
7. Menggunakan RBAC untuk membatasi hak akses.
    
8. Menyimpan dokumen dalam private storage.
    
9. Menyimpan riwayat perubahan status pendaftaran.
    
10. Menggunakan unique constraint untuk mencegah pendaftaran ganda pada gelombang yang sama.
    
11. Menjalankan validasi data otomatis, sedangkan keputusan akhir penerimaan dilakukan manusia yang berwenang.
    
12. Memulai pengembangan dan pengujian di lingkungan lokal.
    

## 19. Hal yang Masih Perlu Diputuskan

Hal-hal berikut harus ditetapkan sebelum implementasi final:

- Framework website admin.
    
- Mekanisme autentikasi API yang digunakan secara final.
    
- Versi minimum Android.
    
- Tipe dan ukuran maksimum dokumen.
    
- Kebijakan revisi dan tenggat waktunya.
    
- Pihak yang berwenang menetapkan atau mengubah keputusan akhir.
    
- Kebijakan keunikan NIK dan penanganan data ganda.
    
- Format nomor pendaftaran.
    
- Konfigurasi server lokal dan jaringan pengujian.
    
- Jadwal backup, masa retensi data, dan prosedur pemulihan.
    
- Daftar resmi program studi dan gelombang penerimaan.
    

Nilai program studi dan konfigurasi gelombang pada data prototipe harus diverifikasi sebelum digunakan sebagai data resmi institusi.

## 20. Kriteria Penyelesaian System Design

Dokumen System Design dianggap cukup untuk menjadi dasar implementasi awal apabila:

- Arsitektur aplikasi dan komunikasi antarkomponen telah dijelaskan.
    
- Peran setiap komponen utama telah ditentukan.
    
- Alur pendaftaran dan status telah didokumentasikan.
    
- Rancangan database awal telah ditetapkan.
    
- Mekanisme autentikasi, otorisasi, dan penyimpanan dokumen telah dijelaskan.
    
- Penanganan kesalahan dan risiko teknis telah diidentifikasi.
    
- Struktur implementasi Android dan backend telah dirancang.
    
- Rencana pengujian dan deployment lokal telah disiapkan.
    
- Keputusan yang masih terbuka telah dicatat.
    

---

## 21. Dokumen Berikutnya

Dokumen ini menjadi acuan untuk menyusun dokumen teknis yang lebih rinci:

1. `Workflow & Business Process.md` — menjelaskan alur bisnis dan interaksi pengguna secara lebih terperinci.
    
2. `Database Model.md` — merinci tabel, kolom, relasi, dan constraint.
    
3. `ERD.md` — memvisualisasikan hubungan antartabel.
    
4. `Data Dictionary.md` — menjelaskan arti, tipe, dan aturan setiap kolom.
    
5. `API Contract.md` — menetapkan endpoint, parameter, autentikasi, dan respons API.
    

System Design merupakan rancangan teknis awal. Implementasi dapat berkembang, tetapi perubahan yang memengaruhi kebutuhan, aturan bisnis, keamanan, atau konsistensi data harus diselaraskan kembali dengan PRD dan SRS.