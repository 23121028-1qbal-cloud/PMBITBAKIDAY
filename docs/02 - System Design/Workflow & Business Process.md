# Workflow & Business Process — PMB ITB Akiday

## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Proyek|Sistem Penerimaan Mahasiswa Baru ITB Akiday|
|Nama Dokumen|Workflow & Business Process|
|Versi|1.0|
|Status|Baseline|
|Tanggal|18 Oktober 2026|
|Platform|Android dan Website Admin|
|Backend|Laravel REST API|
|Database|MySQL|

## 2. Tujuan Dokumen

Dokumen ini menjelaskan proses bisnis dan alur kerja Sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday. Tujuannya agar proses pendaftaran, pemeriksaan dokumen, revisi, penetapan keputusan, dan pelaporan memiliki aturan yang jelas serta dapat diterapkan secara konsisten pada aplikasi Android, website admin, backend, dan database.

Dokumen ini juga menjadi acuan untuk membuat activity diagram, merancang endpoint API, menyusun test case, dan menentukan hak akses setiap aktor.

## 3. Aktor Sistem

Sistem memiliki empat aktor utama.

|Aktor|Tanggung Jawab|
|---|---|
|Calon Mahasiswa|Membuat akun, mengisi data diri, memilih gelombang dan program studi, mengunggah dokumen, mengirim pendaftaran, serta memantau status.|
|Admin|Mengelola akun, gelombang penerimaan, program studi, dan konfigurasi sistem sesuai kewenangannya.|
|Panitia|Memeriksa data dan dokumen, meminta revisi, serta memproses keputusan penerimaan sesuai kewenangan.|
|Pimpinan|Memantau dashboard dan melihat laporan penerimaan untuk mendukung pengambilan keputusan.|

Hak akses ditentukan melalui Role-Based Access Control (RBAC). Setiap aktor hanya dapat menjalankan fungsi yang diizinkan untuk perannya.

## 4. Gambaran Umum Proses Bisnis

Proses PMB dimulai ketika admin menyiapkan gelombang penerimaan dan data program studi. Calon mahasiswa kemudian membuat akun, mengisi profil, memilih gelombang serta satu program studi, melengkapi formulir, dan mengunggah dokumen yang diwajibkan.

Sebelum pendaftaran dikirim, sistem memeriksa kelengkapan dan format data secara otomatis. Pendaftaran yang berhasil dikirim akan masuk ke proses pemeriksaan panitia. Jika ada data atau dokumen yang perlu diperbaiki, panitia dapat meminta revisi. Setelah pemeriksaan selesai, pihak yang berwenang menetapkan keputusan akhir. Calon mahasiswa dapat memantau status pendaftaran melalui aplikasi Android, sedangkan pimpinan dapat melihat rekapitulasi penerimaan melalui dashboard dan laporan.

## 5. Alur Utama Sistem

```
flowchart TD
    A["Admin menyiapkan gelombang dan program studi"] --> B["Calon mahasiswa membuat akun"]
    B --> C["Login dan melengkapi profil"]
    C --> D["Memilih gelombang dan program studi"]
    D --> E["Mengisi formulir pendaftaran"]
    E --> F["Mengunggah dokumen"]
    F --> G{"Validasi otomatis berhasil?"}
    G -->|Tidak| H["Sistem menampilkan kesalahan"]
    H --> E
    G -->|Ya| I["Calon mahasiswa mengirim pendaftaran"]
    I --> J["Status submitted"]
    J --> K["Panitia memeriksa pendaftaran"]
    K --> L["Status review"]
    L --> M{"Perlu revisi?"}
    M -->|Ya| N["Status revision"]
    N --> O["Calon mahasiswa memperbaiki data atau dokumen"]
    O --> G
    M -->|Tidak| P["Pihak berwenang menetapkan keputusan"]
    P --> Q{"Keputusan akhir"}
    Q -->|Diterima| R["Status accepted"]
    Q -->|Ditolak| S["Status rejected"]
    R --> T["Calon mahasiswa melihat hasil"]
    S --> T
    T --> U["Pimpinan melihat laporan penerimaan"]
```

**Catatan:** diagram ini menggambarkan alur bisnis umum. Detail transisi status, kewenangan penetapan keputusan, serta kebijakan revisi perlu mengikuti aturan institusi yang telah disepakati.

## 6. Workflow Calon Mahasiswa

### 6.1. Registrasi Akun

**Tujuan:** memberikan akses kepada calon mahasiswa untuk menggunakan aplikasi.

Langkah-langkah:

1. Calon mahasiswa membuka aplikasi Android.
    
2. Calon mahasiswa mengisi email dan password.
    
3. Sistem memvalidasi format email dan persyaratan password.
    
4. Sistem memeriksa apakah email sudah digunakan.
    
5. Jika valid, sistem menyimpan akun dan password dalam bentuk hash.
    
6. Calon mahasiswa dapat login menggunakan akun tersebut.
    

**Aturan bisnis:**

- Email harus memenuhi format yang valid dan tidak boleh duplikat.
    
- Password tidak disimpan sebagai teks biasa.
    
- Akun yang tidak aktif tidak dapat digunakan untuk login.
    
- Verifikasi email tidak termasuk dalam MVP awal.
    

**Hasil:** akun calon mahasiswa berhasil dibuat.

### 6.2. Melengkapi Profil

Setelah login, calon mahasiswa melengkapi data diri.

Data profil mencakup:

- Nama lengkap.
    
- NIK.
    
- Tempat lahir.
    
- Tanggal lahir.
    
- Jenis kelamin.
    
- Email.
    
- Nomor telepon.
    
- Alamat.
    
- Foto calon mahasiswa.
    

Sistem memeriksa data wajib dan format setiap nilai. Data yang tidak valid akan ditandai agar pengguna dapat memperbaikinya.

**Hasil:** profil calon mahasiswa tersimpan dan siap digunakan dalam proses pendaftaran.

### 6.3. Membuat Pendaftaran

Langkah-langkah:

1. Calon mahasiswa membuka menu pendaftaran.
    
2. Sistem menampilkan gelombang penerimaan yang tersedia.
    
3. Calon mahasiswa memilih satu gelombang.
    
4. Calon mahasiswa memilih satu program studi.
    
5. Sistem memeriksa apakah gelombang menerima pendaftaran.
    
6. Sistem memeriksa apakah calon mahasiswa sudah memiliki pendaftaran pada gelombang tersebut.
    
7. Jika belum ada, sistem membuat data pendaftaran dengan status `draft`.
    
8. Calon mahasiswa melengkapi data pendaftaran dan dokumen yang diwajibkan.
    

**Aturan bisnis:**

- Satu pendaftaran hanya memilih satu program studi.
    
- Satu akun dapat memiliki satu pendaftaran untuk setiap gelombang.
    
- Calon mahasiswa dapat mendaftar pada gelombang lain jika gelombang tersebut tersedia dan aturan institusi mengizinkannya.
    
- Sistem tidak boleh membuat pendaftaran ganda untuk calon mahasiswa dan gelombang yang sama.
    
- Pendaftaran hanya dapat dikirim jika persyaratan terpenuhi.
    

**Hasil:** data pendaftaran tersimpan dalam status `draft`.

### 6.4. Mengunggah Dokumen

Dokumen wajib pada baseline awal:

1. Foto calon mahasiswa.
    
2. Ijazah atau Surat Keterangan Lulus (SKL).
    

Langkah-langkah:

1. Calon mahasiswa memilih dokumen dari perangkat.
    
2. Aplikasi mengirim file ke backend.
    
3. Backend memeriksa autentikasi dan hak akses.
    
4. Backend memvalidasi tipe file, ukuran, dan kelengkapan metadata.
    
5. File disimpan di private storage.
    
6. Metadata file disimpan pada database.
    
7. Sistem menampilkan informasi bahwa unggahan berhasil atau gagal.
    

Jika unggahan gagal, sistem memberikan pesan yang jelas dan memungkinkan pengguna mencoba kembali.

**Catatan:** daftar tipe file yang diizinkan dan batas ukuran maksimum harus ditetapkan sebelum implementasi final.

### 6.5. Mengirim Pendaftaran

Langkah-langkah:

1. Calon mahasiswa membuka ringkasan pendaftaran.
    
2. Sistem menampilkan data dan dokumen yang telah diisi.
    
3. Calon mahasiswa memeriksa kembali informasi tersebut.
    
4. Calon mahasiswa menekan tombol kirim.
    
5. Backend memvalidasi ulang data wajib, dokumen, status gelombang, dan aturan pendaftaran.
    
6. Backend memastikan tidak ada pendaftaran lain untuk akun dan gelombang yang sama.
    
7. Jika valid, sistem mengubah status menjadi `submitted`.
    
8. Sistem menyimpan waktu pengiriman dan riwayat perubahan status.
    
9. Aplikasi menampilkan konfirmasi pengiriman.
    

**Hasil:** pendaftaran siap diperiksa panitia.

Pengiriman harus diproses secara konsisten agar permintaan berulang tidak membuat pendaftaran atau riwayat pengiriman yang tidak semestinya.

### 6.6. Memantau Status

Calon mahasiswa dapat melihat:

- Nomor pendaftaran.
    
- Gelombang yang dipilih.
    
- Program studi yang dipilih.
    
- Status terbaru.
    
- Catatan revisi jika ada.
    
- Riwayat perubahan status yang boleh dilihat oleh calon mahasiswa.
    

Calon mahasiswa hanya boleh mengakses pendaftaran miliknya sendiri.

**Hasil:** calon mahasiswa dapat mengetahui perkembangan proses tanpa harus bertanya langsung kepada panitia.

## 7. Workflow Admin

### 7.1. Mengelola Gelombang Penerimaan

Langkah-langkah:

1. Admin login ke website.
    
2. Admin membuka menu gelombang penerimaan.
    
3. Admin membuat atau memperbarui nama dan tanggal gelombang.
    
4. Sistem memvalidasi data gelombang.
    
5. Admin mengaktifkan atau menonaktifkan gelombang sesuai kewenangan.
    
6. Sistem menyimpan perubahan.
    

**Aturan bisnis:**

- Tanggal mulai tidak boleh melewati tanggal akhir.
    
- Gelombang harus tersedia dan memenuhi ketentuan waktu sebelum menerima pendaftaran.
    
- Gelombang yang sudah memiliki data pendaftaran perlu ditangani secara hati-hati sebelum dinonaktifkan atau diubah.
    
- Perubahan konfigurasi tidak boleh menghapus riwayat pendaftaran yang sudah ada.
    

### 7.2. Mengelola Program Studi

Langkah-langkah:

1. Admin membuka menu program studi.
    
2. Admin menambahkan atau memperbarui kode dan nama program studi.
    
3. Sistem memvalidasi data dan memastikan kode tidak duplikat.
    
4. Sistem menyimpan perubahan.
    

Program studi yang telah digunakan oleh pendaftaran sebaiknya dinonaktifkan daripada dihapus secara langsung jika penghapusan dapat merusak riwayat.

Daftar program studi pada prototipe harus diverifikasi terhadap data resmi institusi sebelum digunakan dalam penerapan nyata.

### 7.3. Mengelola Akun

Admin dapat melihat dan mengelola akun sesuai kewenangannya.

Fungsi utama:

- Melihat daftar pengguna.
    
- Mengubah status aktif akun.
    
- Mengatur peran pengguna internal sesuai kebijakan.
    
- Memeriksa informasi akun yang diperlukan untuk operasional.
    

Admin tidak boleh melihat password pengguna dalam bentuk asli. Perubahan hak akses harus dilakukan melalui mekanisme yang tervalidasi.

## 8. Workflow Panitia

### 8.1. Memeriksa Pendaftaran

Langkah-langkah:

1. Panitia login ke website.
    
2. Sistem menampilkan daftar pendaftaran yang dapat diperiksa.
    
3. Panitia memilih satu pendaftaran.
    
4. Sistem menampilkan data calon mahasiswa dan dokumen yang dapat diakses panitia.
    
5. Panitia memeriksa kesesuaian data dan dokumen.
    
6. Panitia mencatat hasil pemeriksaan.
    
7. Panitia memutuskan apakah pendaftaran perlu direvisi atau dapat diteruskan ke keputusan akhir.
    

Pemeriksaan otomatis hanya membantu memvalidasi kelengkapan dan format. Penilaian substantif dan keputusan penerimaan tetap dilakukan oleh manusia yang berwenang.

### 8.2. Meminta Revisi

Jika data atau dokumen perlu diperbaiki, panitia dapat meminta revisi.

Langkah-langkah:

1. Panitia membuka detail pendaftaran.
    
2. Panitia memilih tindakan meminta revisi.
    
3. Panitia memasukkan catatan yang menjelaskan bagian yang perlu diperbaiki.
    
4. Backend memvalidasi kewenangan panitia dan status pendaftaran.
    
5. Sistem mengubah status menjadi `revision`.
    
6. Sistem menyimpan catatan serta riwayat perubahan status.
    
7. Calon mahasiswa dapat melihat catatan tersebut di aplikasi.
    

**Aturan bisnis:**

- Catatan revisi harus jelas dan dapat ditindaklanjuti.
    
- Hanya pengguna yang berwenang yang dapat meminta revisi.
    
- Revisi harus dikaitkan dengan pendaftaran yang benar.
    
- Batas jumlah revisi dan tenggat waktu perbaikan masih perlu ditentukan.
    

### 8.3. Memeriksa Ulang Hasil Revisi

Setelah calon mahasiswa memperbaiki data atau dokumen:

1. Calon mahasiswa mengirimkan perbaikan.
    
2. Backend memvalidasi data dan file yang diperbarui.
    
3. Panitia memeriksa kembali bagian yang direvisi.
    
4. Jika masih belum sesuai, panitia dapat meminta revisi kembali sesuai kebijakan.
    
5. Jika sudah sesuai, pendaftaran diteruskan ke proses keputusan.
    

Status setelah perbaikan harus mengikuti aturan transisi yang ditetapkan. Sistem tidak boleh mengubah status menjadi diterima secara otomatis hanya karena semua file berhasil diunggah.

### 8.4. Menetapkan Keputusan Akhir

Setelah pemeriksaan selesai, pihak yang berwenang menetapkan hasil akhir.

Pilihan keputusan:

- `accepted`: pendaftaran dinyatakan diterima.
    
- `rejected`: pendaftaran dinyatakan ditolak.
    

Langkah-langkah:

1. Pengguna yang memiliki kewenangan membuka pendaftaran yang telah diperiksa.
    
2. Pengguna meninjau data dan hasil pemeriksaan.
    
3. Pengguna memilih keputusan.
    
4. Backend memeriksa hak akses dan memastikan status pendaftaran memungkinkan keputusan tersebut.
    
5. Backend menyimpan keputusan dan riwayat status.
    
6. Sistem menampilkan status terbaru kepada calon mahasiswa.
    

**Aturan bisnis:**

- Keputusan tidak boleh ditetapkan oleh pengguna tanpa kewenangan.
    
- Keputusan harus terkait dengan pendaftaran yang benar.
    
- Perubahan keputusan setelah final hanya dapat dilakukan jika kebijakan institusi mengizinkan.
    
- Jika keputusan diubah, alasan, pelaku, waktu, dan perubahan status harus tercatat.
    
- Sistem tidak menetapkan keputusan akademik secara otomatis.
    

## 9. Workflow Pimpinan

Pimpinan menggunakan website untuk memantau pelaksanaan penerimaan.

Langkah-langkah:

1. Pimpinan login.
    
2. Sistem memeriksa hak akses.
    
3. Pimpinan membuka dashboard.
    
4. Sistem menampilkan ringkasan data pendaftaran.
    
5. Pimpinan dapat memfilter laporan berdasarkan gelombang atau program studi.
    
6. Pimpinan dapat mengunduh laporan sesuai izin akses.
    

Informasi yang disediakan dapat mencakup:

- Total pendaftar.
    
- Jumlah pendaftaran berdasarkan status.
    
- Jumlah pendaftar per gelombang.
    
- Jumlah pendaftar per program studi.
    
- Jumlah pendaftaran yang masih dalam pemeriksaan.
    
- Jumlah pendaftaran yang memerlukan revisi.
    
- Rekapitulasi keputusan akhir.
    

Pimpinan tidak otomatis memiliki izin untuk mengubah data pendaftaran. Hak perubahan data dan keputusan harus mengikuti kebijakan institusi.

## 10. Siklus Status Pendaftaran

### 10.1. Diagram Transisi Status

```
stateDiagram-v2
    [*] --> draft
    draft --> submitted: Pendaftaran dikirim
    submitted --> review: Pemeriksaan dimulai
    review --> revision: Perlu perbaikan
    revision --> review: Perbaikan dikirim dan diperiksa
    review --> accepted: Keputusan diterima
    review --> rejected: Keputusan ditolak
    accepted --> [*]
    rejected --> [*]
```

Diagram tersebut merupakan alur dasar. Transisi dari `accepted` atau `rejected` ke status lain tidak dibuka secara otomatis. Jika kebijakan institusi memperbolehkan perubahan keputusan, transisi khusus harus dirancang dan dicatat melalui proses yang terotorisasi.

### 10.2. Definisi Status

|Status|Aktor atau proses pemicu|Arti|
|---|---|---|
|`draft`|Sistem saat pendaftaran dibuat|Pendaftaran masih disiapkan.|
|`submitted`|Calon mahasiswa mengirim pendaftaran|Pendaftaran sudah dikirim.|
|`review`|Panitia memulai pemeriksaan|Pendaftaran sedang diperiksa.|
|`revision`|Panitia meminta perbaikan|Data atau dokumen perlu diperbaiki.|
|`accepted`|Pihak berwenang menetapkan keputusan|Pendaftaran dinyatakan diterima.|
|`rejected`|Pihak berwenang menetapkan keputusan|Pendaftaran dinyatakan ditolak.|

### 10.3. Pencatatan Riwayat

Setiap perubahan status perlu mencatat sekurang-kurangnya:

- ID pendaftaran.
    
- Status sebelumnya.
    
- Status baru.
    
- Waktu perubahan.
    
- ID pengguna yang melakukan perubahan, jika perubahan dilakukan oleh pengguna.
    
- Catatan atau alasan jika diperlukan.
    

Penyimpanan perubahan status dan riwayatnya dilakukan dalam satu transaksi database agar keduanya tidak berbeda jika proses gagal di tengah jalan.

## 11. Aturan Validasi dan Konsistensi

Sistem harus menerapkan aturan berikut:

1. Email akun harus unik.
    
2. Setiap profil calon mahasiswa terhubung dengan akun yang sesuai.
    
3. Satu pendaftaran hanya memilih satu gelombang dan satu program studi.
    
4. Satu calon mahasiswa hanya boleh memiliki satu pendaftaran pada gelombang yang sama.
    
5. Gelombang harus memenuhi ketentuan ketersediaan sebelum pendaftaran dikirim.
    
6. Dokumen wajib harus lengkap sebelum pendaftaran dikirim.
    
7. File harus lolos validasi sebelum disimpan dan digunakan.
    
8. Perubahan status harus mengikuti transisi yang diperbolehkan.
    
9. Calon mahasiswa hanya dapat melihat atau mengubah data miliknya sendiri.
    
10. Pengguna internal hanya dapat menjalankan tindakan sesuai hak akses.
    
11. Perubahan status harus memiliki riwayat yang dapat ditelusuri.
    
12. Permintaan yang diulang tidak boleh menghasilkan data ganda.
    

Aturan kritis harus divalidasi di backend dan diperkuat dengan constraint database jika memungkinkan. Validasi di aplikasi Android atau website saja tidak cukup karena permintaan dapat dikirim di luar antarmuka tersebut.

## 12. Penanganan Kondisi Khusus

|Kondisi|Respons Sistem|
|---|---|
|Email sudah digunakan|Menolak registrasi dan meminta pengguna menggunakan email lain atau login.|
|Profil belum lengkap|Menampilkan informasi data yang perlu dilengkapi.|
|Gelombang tidak tersedia|Menolak pembuatan atau pengiriman pendaftaran sesuai aturan.|
|Pendaftaran pada gelombang yang sama sudah ada|Menolak duplikasi dan menampilkan pendaftaran yang sudah ada jika diizinkan.|
|File terlalu besar atau tipe tidak diizinkan|Menolak unggahan dan menjelaskan persyaratan file.|
|Jaringan terputus saat unggah|Menampilkan kegagalan dan memungkinkan percobaan ulang tanpa menganggap unggahan berhasil.|
|Pengguna mengakses pendaftaran orang lain|Menolak permintaan dengan respons otorisasi yang sesuai.|
|Panitia mengubah status yang tidak diizinkan|Menolak perubahan dan mempertahankan status sebelumnya.|
|Database gagal saat menyimpan perubahan status|Membatalkan transaksi agar perubahan dan riwayat tidak tersimpan sebagian.|
|Pengguna mengirim permintaan berulang|Memeriksa kondisi data agar tidak membuat pendaftaran ganda.|

## 13. Integrasi Workflow dengan API

Endpoint berikut merupakan rancangan awal dan perlu diselaraskan dengan `API Contract.md`.

|Proses|Endpoint Rancangan|Keterangan|
|---|---|---|
|Registrasi akun|`POST /api/register`|Membuat akun calon mahasiswa.|
|Login|`POST /api/login`|Memulai sesi atau memperoleh token.|
|Melihat profil|`GET /api/profile`|Mengambil profil pengguna.|
|Memperbarui profil|`PUT /api/profile`|Memperbarui data profil.|
|Melihat gelombang|`GET /api/waves`|Mengambil gelombang yang tersedia.|
|Melihat program studi|`GET /api/study-programs`|Mengambil daftar program studi.|
|Membuat pendaftaran|`POST /api/applications`|Membuat pendaftaran berstatus `draft`.|
|Melihat pendaftaran|`GET /api/applications`|Mengambil pendaftaran sesuai hak akses.|
|Mengunggah dokumen|`POST /api/applications/{id}/documents`|Mengunggah dokumen pendaftaran.|
|Mengirim pendaftaran|`POST /api/applications/{id}/submit`|Memvalidasi dan mengirim pendaftaran.|
|Memulai pemeriksaan|Endpoint admin/panitia|Mengubah status ke `review` sesuai aturan.|
|Meminta revisi|Endpoint admin/panitia|Menyimpan catatan dan status revisi.|
|Menetapkan keputusan|Endpoint keputusan|Menyimpan keputusan dari pihak berwenang.|
|Melihat laporan|Endpoint laporan|Mengambil ringkasan sesuai hak akses.|

Endpoint untuk proses pemeriksaan, revisi, dan keputusan masih perlu ditetapkan secara rinci dalam dokumen API Contract. Nama di atas tidak berarti endpoint tersebut sudah diimplementasikan.

## 14. Acceptance Criteria Workflow

### AC-WF-01 — Pendaftaran Baru

**Given** calon mahasiswa sudah login dan gelombang tersedia,

**When** calon mahasiswa mengisi data wajib, memilih satu program studi, mengunggah dokumen wajib, lalu mengirim pendaftaran,

**Then** sistem memvalidasi data dan menyimpan pendaftaran dengan status `submitted` jika seluruh persyaratan terpenuhi.

### AC-WF-02 — Pencegahan Pendaftaran Ganda

**Given** calon mahasiswa sudah memiliki pendaftaran pada suatu gelombang,

**When** calon mahasiswa mencoba membuat pendaftaran kedua pada gelombang yang sama,

**Then** sistem menolak duplikasi dan tidak membuat data pendaftaran kedua.

### AC-WF-03 — Permintaan Revisi

**Given** panitia memiliki hak akses dan pendaftaran sedang diperiksa,

**When** panitia meminta revisi dengan catatan yang sesuai,

**Then** sistem mengubah status menjadi `revision`, menyimpan catatan, dan mencatat riwayat perubahan.

### AC-WF-04 — Keputusan Akhir

**Given** pendaftaran telah melalui pemeriksaan dan pengguna memiliki kewenangan,

**When** pengguna menetapkan keputusan akhir,

**Then** sistem menyimpan status `accepted` atau `rejected` beserta riwayat perubahan dan menampilkan hasil kepada calon mahasiswa.

### AC-WF-05 — Perlindungan Data Pendaftaran

**Given** calon mahasiswa A sudah login,

**When** calon mahasiswa A mencoba mengakses pendaftaran milik calon mahasiswa B,

**Then** backend menolak akses dan tidak membocorkan data atau dokumen milik calon mahasiswa B.

### AC-WF-06 — Konsistensi Riwayat

**Given** pendaftaran sedang diproses untuk perubahan status,

**When** penyimpanan perubahan status gagal,

**Then** transaksi dibatalkan sehingga status dan riwayat tidak menjadi tidak sinkron.

## 15. Hal yang Masih Perlu Diputuskan

Hal-hal berikut perlu dikonfirmasi sebelum implementasi final:

- Jadwal resmi setiap gelombang penerimaan.
    
- Batas jumlah revisi dan tenggat waktu perbaikan.
    
- Pihak yang berwenang menetapkan keputusan akhir.
    
- Apakah keputusan akhir dapat diubah dan bagaimana prosedurnya.
    
- Persyaratan tipe dan ukuran dokumen.
    
- Kebijakan keunikan NIK.
    
- Format nomor pendaftaran.
    
- Notifikasi kepada calon mahasiswa, misalnya melalui email atau notifikasi aplikasi.
    
- Apakah pimpinan hanya melihat laporan atau memiliki kewenangan tambahan.
    

Keputusan yang belum ditetapkan tidak boleh dianggap sebagai kebijakan resmi. Seluruh perubahan aturan harus diselaraskan dengan PRD dan SRS.

## 16. Kriteria Penyelesaian Dokumen

- Seluruh aktor dan tanggung jawabnya telah dijelaskan.
    
- Alur pendaftaran calon mahasiswa telah didokumentasikan.
    
- Proses admin, panitia, dan pimpinan telah dijelaskan.
    
- Alur revisi dan keputusan akhir telah ditentukan secara konseptual.
    
- Transisi status dan aturan konsistensi telah dicatat.
    
- Kondisi khusus dan penanganan kesalahan telah diidentifikasi.
    
- Workflow telah dipetakan ke endpoint API rancangan.
    
- Acceptance criteria telah disiapkan untuk pengujian.
    
- Keputusan yang belum pasti telah dicatat untuk dikonfirmasi.
    

## 17. Dokumen Berikutnya

Setelah dokumen ini disimpan, urutan yang disarankan:

1. `03 - Database/Database Model.md`
    
2. `03 - Database/ERD.md`
    
3. `03 - Database/Data Dictionary.md`
    
4. `03 - Database/Migration Plan.md`
    
5. `04 - API/API Contract.md`
    

Dokumen Database Model menjadi langkah berikutnya karena workflow sudah menjelaskan data apa yang dibuat, siapa yang mengubahnya, serta bagaimana status dan riwayat pendaftaran harus dijaga. Rancangan tabel kemudian dapat dibuat dengan lebih konsisten terhadap proses bisnis.