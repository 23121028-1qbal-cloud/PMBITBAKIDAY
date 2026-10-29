---

title: Traceability Matrix  
project: PMB ITB Akiday  
version: 1.0  
status: Baseline  
created: 2026-10-18  
tags:

- PMB
    
- requirements
    
- traceability
    

---

# Traceability Matrix — PMB ITB Akiday

## 1. Tujuan Dokumen

Traceability Matrix merupakan dokumen yang digunakan untuk melacak hubungan antara kebutuhan proyek dengan rancangan sistem, modul implementasi, dan skenario pengujian.

Dokumen ini memastikan bahwa kebutuhan yang telah ditetapkan dalam `PRD Final v1.0.md` dan `SRS.md` tidak terlewat selama proses pengembangan. Dokumen ini juga membantu mengidentifikasi dampak perubahan kebutuhan terhadap database, API, antarmuka, serta pengujian.

## 2. Ruang Lingkup

Traceability Matrix mencakup:

- Kebutuhan fungsional (`FR`).
    
- Kebutuhan nonfungsional (`NFR`).
    
- Aturan bisnis (`BR`).
    
- Dokumen desain yang terkait.
    
- Modul yang perlu diimplementasikan.
    
- Test case untuk memverifikasi kebutuhan.
    
- Status implementasi dan pengujian.
    

Dokumen ini merupakan baseline awal. Status implementasi dan hasil pengujian harus diperbarui berdasarkan pekerjaan yang benar-benar telah dilakukan.

## 3. Referensi Dokumen

|Kode|Dokumen|Fungsi|
|---|---|---|
|REF-01|`PRD Final v1.0.md`|Acuan kebutuhan produk dan aturan bisnis|
|REF-02|`SRS.md`|Spesifikasi kebutuhan perangkat lunak|
|REF-03|`Project Overview.md`|Gambaran umum dan ruang lingkup proyek|
|REF-04|`Project Plan.md`|Jadwal dan tahapan pengerjaan|
|REF-05|`Workflow & Business Process.md`|Alur proses bisnis|
|REF-06|`System Design.md`|Arsitektur dan rancangan sistem|
|REF-07|`Database Model.md`|Struktur dan relasi database|
|REF-08|`API Contract.md`|Kontrak komunikasi API|
|REF-09|`UI UX Design.md`|Rancangan antarmuka|
|REF-10|`Security Specification.md`|Persyaratan keamanan|
|REF-11|`Test Plan.md`|Strategi pengujian|
|REF-12|`Test Cases.md`|Skenario pengujian|
|REF-13|`Test Results.md`|Hasil pengujian|

## 4. Keterangan Status

|Status|Arti|
|---|---|
|`Planned`|Direncanakan, belum dikerjakan|
|`In Progress`|Sedang dikerjakan|
|`Implemented`|Implementasi telah selesai|
|`Passed`|Pengujian terkait telah berhasil|
|`Failed`|Pengujian terkait gagal|
|`Blocked`|Terhambat oleh dependensi atau masalah|
|`N/A`|Tidak berlaku untuk kebutuhan tersebut|

Status pada tabel awal di bawah menggunakan `Planned` karena dokumen ini disiapkan sebagai acuan pelaksanaan. Ubah status sesuai bukti implementasi dan pengujian aktual.

## 5. Traceability Kebutuhan Fungsional

|ID|Kebutuhan|Dokumen Desain|Modul Implementasi|Test Case|Status|
|---|---|---|---|---|---|
|FR-01|Registrasi akun|System Design, Database Model, API Contract, UI UX Design|Autentikasi dan Registrasi|TC-AUTH-01, TC-AUTH-02|Planned|
|FR-02|Login dan logout|System Design, API Contract, Security Specification|Autentikasi dan Manajemen Sesi|TC-AUTH-03, TC-AUTH-04|Planned|
|FR-03|Pengelolaan profil calon mahasiswa|Database Model, API Contract, UI UX Design|Profil Calon Mahasiswa|TC-PROF-01, TC-PROF-02|Planned|
|FR-04|Melihat gelombang penerimaan|Database Model, API Contract, UI UX Design|Pengelolaan Gelombang|TC-WAVE-01, TC-WAVE-02|Planned|
|FR-05|Melihat program studi|Database Model, API Contract, UI UX Design|Pengelolaan Program Studi|TC-PROG-01, TC-PROG-02|Planned|
|FR-06|Membuat dan menyimpan pendaftaran|Workflow, Database Model, API Contract|Modul Pendaftaran|TC-APP-01, TC-APP-02, TC-APP-03|Planned|
|FR-07|Mengunggah dokumen persyaratan|Database Model, API Contract, Security Specification, UI UX Design|Modul Dokumen|TC-DOC-01, TC-DOC-02, TC-DOC-03|Planned|
|FR-08|Validasi dan pengiriman pendaftaran|Workflow, Database Model, API Contract|Validasi dan Pengiriman Pendaftaran|TC-APP-04, TC-APP-05, TC-APP-06|Planned|
|FR-09|Melihat status pendaftaran|Workflow, API Contract, UI UX Design|Pemantauan Pendaftaran|TC-STAT-01, TC-STAT-02|Planned|
|FR-10|Melihat riwayat status|Database Model, API Contract, Security Specification|Riwayat Status|TC-STAT-03, TC-STAT-04|Planned|
|FR-11|Melihat daftar pendaftaran|System Design, API Contract, UI UX Design|Dashboard Panitia|TC-ADM-01, TC-ADM-02|Planned|
|FR-12|Memeriksa data dan dokumen|Workflow, API Contract, Security Specification|Pemeriksaan Pendaftaran|TC-ADM-03, TC-ADM-04|Planned|
|FR-13|Meminta revisi|Workflow, Database Model, API Contract|Revisi Pendaftaran|TC-REV-01, TC-REV-02|Planned|
|FR-14|Menetapkan keputusan akhir|Workflow, API Contract, Security Specification|Keputusan Seleksi|TC-DEC-01, TC-DEC-02, TC-DEC-03|Planned|
|FR-15|Mengelola data utama|Database Model, API Contract, Security Specification|Administrasi Sistem|TC-ADM-05, TC-ADM-06|Planned|
|FR-16|Melihat dashboard statistik|Database Model, API Contract, UI UX Design|Dashboard Laporan|TC-REP-01, TC-REP-02|Planned|
|FR-17|Menghasilkan laporan PDF|API Contract, UI UX Design, Security Specification|Ekspor Laporan|TC-REP-03, TC-REP-04|Planned|

## 6. Traceability Kebutuhan Nonfungsional

|ID|Kebutuhan|Dokumen Pendukung|Area Implementasi|Verifikasi|Status|
|---|---|---|---|---|---|
|NFR-01|Keamanan autentikasi|Security Specification, API Contract|Autentikasi dan penyimpanan kredensial|TC-SEC-01, TC-SEC-02|Planned|
|NFR-02|Kontrol akses|Security Specification, Access Control Matrix|Middleware dan otorisasi backend|TC-SEC-03, TC-SEC-04|Planned|
|NFR-03|Privasi data|Security Specification, Database Model|Pembatasan akses data dan dokumen|TC-SEC-05, TC-SEC-06|Planned|
|NFR-04|Validasi backend|API Contract, Module Specification|Validasi input dan berkas|TC-VAL-01, TC-VAL-02|Planned|
|NFR-05|Integritas data|Database Model, Migration Plan|Constraint dan transaksi database|TC-DB-01, TC-DB-02|Planned|
|NFR-06|Pencegahan duplikasi|Database Model, API Contract|Validasi backend dan unique constraint|TC-DB-03, TC-DB-04|Planned|
|NFR-07|Kemudahan penggunaan|UI UX Design, Design System|Formulir, navigasi, dan pesan kesalahan|TC-UX-01, TC-UX-02|Planned|
|NFR-08|Konsistensi antarmuka|UI UX Design, Screen Specification|Istilah, status, dan komponen antarmuka|TC-UX-03|Planned|
|NFR-09|Kemudahan pemeliharaan|System Design, Coding Standards, Repository Guide|Struktur kode dan dokumentasi|Review kode dan dokumentasi|Planned|
|NFR-10|Kompatibilitas lingkungan|Environment Configuration, Deployment Guide|Konfigurasi Android, backend, dan database|TC-ENV-01, TC-ENV-02|Planned|
|NFR-11|Pencatatan aktivitas|Database Model, Security Specification|Riwayat status dan audit aktivitas yang ditetapkan|TC-AUD-01, TC-AUD-02|Planned|
|NFR-12|Pemulihan data|Backup & Recovery, Deployment Guide|Backup dan restore database serta berkas|TC-REC-01, TC-REC-02|Planned|

## 7. Traceability Aturan Bisnis

|ID|Aturan Bisnis|Implementasi yang Dibutuhkan|Test Case|Status|
|---|---|---|---|---|
|BR-01|Pendaftaran gratis|Tidak ada alur pembayaran pada MVP|TC-BR-01|Planned|
|BR-02|Satu pendaftaran hanya memilih satu program studi|Relasi pendaftaran ke satu program studi|TC-BR-02|Planned|
|BR-03|Satu akun hanya memiliki satu pendaftaran per gelombang|Validasi backend dan unique constraint database|TC-BR-03|Planned|
|BR-04|Akun dapat mendaftar di gelombang berbeda jika diizinkan|Validasi berdasarkan gelombang dan kebijakan penerimaan|TC-BR-04|Planned|
|BR-05|Pendaftaran harus lolos validasi sebelum dikirim|Validasi data dan dokumen di backend|TC-BR-05|Planned|
|BR-06|Keputusan akhir dibuat panitia berwenang|Otorisasi dan validasi transisi status|TC-BR-06|Planned|
|BR-07|Calon mahasiswa hanya mengakses pendaftarannya sendiri|Pemeriksaan kepemilikan data di backend|TC-BR-07|Planned|
|BR-08|Status harus mengikuti transisi yang diizinkan|Validasi transisi status di backend|TC-BR-08|Planned|
|BR-09|Perubahan status tercatat dalam riwayat|Pencatatan riwayat perubahan|TC-BR-09|Planned|
|BR-10|Dokumen disimpan secara privat|Penyimpanan privat dan akses melalui backend|TC-BR-10|Planned|
|BR-11|Tindakan dibatasi berdasarkan peran|RBAC dan pemeriksaan otorisasi|TC-BR-11|Planned|
|BR-12|Data program studi prototipe bukan daftar resmi|Konfirmasi data sebelum penggunaan resmi|Review data konfigurasi|Planned|

## 8. Daftar Awal Test Case

ID pada tabel traceability merupakan referensi awal. Skenario dan langkah rinci akan dijabarkan dalam `Test Cases.md`.

### 8.1 Autentikasi

|ID|Skenario|Hasil yang Diharapkan|
|---|---|---|
|TC-AUTH-01|Registrasi dengan data valid|Akun berhasil dibuat|
|TC-AUTH-02|Registrasi menggunakan email yang sudah terdaftar|Registrasi ditolak dengan pesan yang sesuai|
|TC-AUTH-03|Login dengan kredensial valid|Pengguna berhasil login|
|TC-AUTH-04|Mengakses fitur terlindungi setelah logout|Akses ditolak sampai autentikasi dilakukan kembali|

### 8.2 Profil dan Pendaftaran

|ID|Skenario|Hasil yang Diharapkan|
|---|---|---|
|TC-PROF-01|Menyimpan profil dengan data valid|Profil berhasil disimpan|
|TC-PROF-02|Mengakses profil pengguna lain|Akses ditolak|
|TC-APP-01|Membuat pendaftaran pada gelombang yang memenuhi syarat|Pendaftaran berhasil dibuat|
|TC-APP-02|Membuat pendaftaran kedua pada gelombang yang sama|Pendaftaran kedua ditolak|
|TC-APP-03|Membuat pendaftaran pada gelombang berbeda yang diizinkan|Pendaftaran dapat dibuat|
|TC-APP-04|Mengirim formulir dengan data wajib belum lengkap|Pengiriman ditolak|
|TC-APP-05|Mengirim formulir dengan data dan dokumen valid|Status berubah menjadi `submitted`|
|TC-APP-06|Dua permintaan pendaftaran untuk akun dan gelombang yang sama terjadi bersamaan|Hanya satu pendaftaran yang berhasil tersimpan|

### 8.3 Dokumen

|ID|Skenario|Hasil yang Diharapkan|
|---|---|---|
|TC-DOC-01|Mengunggah dokumen dengan format dan ukuran yang diizinkan|Dokumen berhasil disimpan|
|TC-DOC-02|Mengunggah dokumen yang tidak memenuhi ketentuan|Dokumen ditolak|
|TC-DOC-03|Pengguna mencoba membuka dokumen pendaftar lain|Akses ditolak|

### 8.4 Status dan Pemeriksaan

|ID|Skenario|Hasil yang Diharapkan|
|---|---|---|
|TC-STAT-01|Calon mahasiswa melihat status pendaftarannya|Status ditampilkan sesuai data server|
|TC-STAT-02|Calon mahasiswa mencoba melihat status pendaftar lain|Akses ditolak|
|TC-STAT-03|Status pendaftaran berubah|Riwayat perubahan tercatat|
|TC-STAT-04|Calon mahasiswa mencoba mengubah riwayat status|Perubahan ditolak|
|TC-ADM-01|Panitia membuka daftar pendaftaran|Daftar tampil sesuai kewenangan|
|TC-ADM-02|Pengguna tanpa kewenangan mencoba membuka daftar panitia|Akses ditolak|
|TC-ADM-03|Panitia memeriksa data dan dokumen|Detail pendaftaran dapat ditinjau sesuai izin|
|TC-ADM-04|Pengguna tanpa izin mencoba membuka dokumen|Akses ditolak|
|TC-REV-01|Panitia meminta revisi dengan alasan|Status berubah menjadi `revision` dan alasan tercatat|
|TC-REV-02|Calon mahasiswa mengirim ulang setelah revisi valid|Status berubah sesuai alur pengiriman ulang|
|TC-DEC-01|Panitia berwenang menetapkan diterima dari status `review`|Status menjadi `accepted` dan riwayat tercatat|
|TC-DEC-02|Panitia berwenang menetapkan ditolak dari status `review`|Status menjadi `rejected` dan riwayat tercatat|
|TC-DEC-03|Pengguna tanpa kewenangan mencoba menetapkan keputusan|Tindakan ditolak|

### 8.5 Database, Keamanan, dan Pelaporan

|ID|Skenario|Hasil yang Diharapkan|
|---|---|---|
|TC-DB-01|Menyimpan pendaftaran dengan relasi yang valid|Data tersimpan konsisten|
|TC-DB-02|Menyimpan data dengan referensi yang tidak valid|Database atau backend menolak operasi|
|TC-DB-03|Membuat dua pendaftaran dengan pemilik dan gelombang yang sama|Duplikasi ditolak|
|TC-DB-04|Mengirim dua permintaan pendaftaran secara bersamaan|Constraint database menjaga keunikan|
|TC-REP-01|Pengguna berwenang membuka dashboard|Statistik tampil sesuai data|
|TC-REP-02|Pengguna tanpa izin membuka dashboard terbatas|Akses ditolak|
|TC-REP-03|Pengguna berwenang membuat laporan PDF|Laporan dihasilkan dengan data yang sesuai|
|TC-REP-04|Pengguna tanpa izin meminta laporan|Permintaan ditolak|
|TC-SEC-01|Memeriksa penyimpanan kata sandi|Kata sandi tidak tersimpan sebagai teks asli|
|TC-SEC-02|Menggunakan kredensial yang salah|Login ditolak|
|TC-SEC-03|Mengakses endpoint tanpa autentikasi|Akses ditolak|
|TC-SEC-04|Mengakses fungsi dengan peran yang tidak sesuai|Akses ditolak|
|TC-SEC-05|Mengakses data pendaftar lain|Akses ditolak|
|TC-SEC-06|Mencoba mengakses berkas privat tanpa izin|Akses ditolak|
|TC-REC-01|Membuat backup database dan dokumen|Backup tersedia dan dapat diverifikasi|
|TC-REC-02|Melakukan pemulihan dari backup|Data dapat dipulihkan dan diverifikasi|

Catatan: daftar tersebut merupakan skenario awal, bukan bukti bahwa pengujian sudah dijalankan. Test case harus diperinci dan disesuaikan dengan kontrak API, aturan validasi, serta implementasi aktual.

## 9. Prosedur Pemeliharaan Traceability

Traceability Matrix harus diperbarui apabila:

1. Kebutuhan baru ditambahkan ke PRD atau SRS.
    
2. Kebutuhan diubah atau dihapus.
    
3. Desain database, API, workflow, atau UI/UX berubah.
    
4. Modul implementasi mengalami perubahan signifikan.
    
5. Test case ditambahkan, diubah, atau dihapus.
    
6. Hasil pengujian menunjukkan kebutuhan belum terpenuhi.
    

Setiap perubahan perlu diperiksa dampaknya terhadap dokumen dan modul yang terkait. Kebutuhan tidak boleh ditandai selesai hanya karena desain atau kode sudah dibuat; status keberhasilan harus didukung oleh hasil verifikasi yang sesuai.

## 10. Kriteria Kelengkapan

Traceability Matrix dianggap lengkap untuk satu kebutuhan apabila:

- Kebutuhan memiliki ID unik.
    
- Kebutuhan memiliki sumber yang jelas di PRD atau SRS.
    
- Dokumen desain yang terkait telah diidentifikasi.
    
- Modul implementasi telah ditentukan.
    
- Test case yang sesuai telah dibuat.
    
- Status implementasi dan pengujian diperbarui berdasarkan bukti aktual.
    

## 11. Riwayat Perubahan

|Versi|Tanggal|Perubahan|
|---|---|---|
|1.0|2026-10-18|Pembuatan baseline Traceability Matrix PMB ITB Akiday|

---

**Dokumen acuan utama:** `PRD Final v1.0.md` dan `SRS.md`.

**Dokumen lanjutan:** `Workflow & Business Process.md`, `System Design.md`, `Database Model.md`, `API Contract.md`, `Security Specification.md`, `Test Plan.md`, dan `Test Cases.md`.