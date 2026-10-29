---

title: Project Plan  
project: PMB ITB Akiday  
version: 1.0  
status: Baseline  
created: 2026-10-16  
tags:

- PMB
    
- project-plan
    
- project-management
    

---

# Project Plan — PMB ITB Akiday

## 1. Informasi Proyek

|Informasi|Keterangan|
|---|---|
|Nama Proyek|Sistem Penerimaan Mahasiswa Baru ITB Akiday|
|Versi Dokumen|1.0|
|Status|Perencanaan proyek|
|Dokumen Acuan|PRD Final v1.0.md|
|Platform|Android dan Website Admin|
|Backend|Laravel REST API|
|Database|MySQL|
|Bahasa Android|Kotlin|
|Metode Pengembangan|Iteratif dan bertahap|
|Lingkungan Pengembangan|Lokal|

## 2. Tujuan Perencanaan

Project Plan dibuat sebagai panduan pelaksanaan pengembangan sistem PMB ITB Akiday. Dokumen ini menjelaskan tahapan pekerjaan, urutan pengerjaan, target hasil, dependensi antarpekerjaan, dan kriteria penyelesaian setiap tahap.

Perencanaan ini bertujuan agar pengembangan dilakukan secara terarah dan setiap komponen yang dibuat tetap sesuai dengan kebutuhan dalam PRD.

## 3. Ruang Lingkup Pekerjaan

Pekerjaan proyek dibagi menjadi beberapa kelompok utama:

1. Analisis kebutuhan dan perencanaan.
    
2. Perancangan proses bisnis dan arsitektur sistem.
    
3. Perancangan database dan API.
    
4. Perancangan UI/UX.
    
5. Implementasi backend Laravel.
    
6. Implementasi website admin.
    
7. Implementasi aplikasi Android.
    
8. Pengujian sistem.
    
9. Deployment dan pengujian pada lingkungan lokal.
    
10. Penyusunan dokumentasi dan evaluasi akhir.
    

Fitur yang dikembangkan mengikuti ruang lingkup MVP pada PRD. Fitur di luar MVP tidak dikerjakan sebelum ada perubahan kebutuhan yang disetujui.

## 4. Strategi Pengembangan

Proyek menggunakan pendekatan iteratif, yaitu sistem dikembangkan melalui beberapa tahap dengan hasil yang dapat ditinjau dan diperbaiki secara berkala.

Setiap tahap memiliki keluaran yang menjadi dasar tahap berikutnya. Perubahan kebutuhan harus dicatat dan diperiksa dampaknya terhadap desain, database, API, antarmuka, serta pengujian.

Pengembangan dilakukan dengan prinsip berikut:

- PRD menjadi acuan utama kebutuhan dan aturan bisnis.
    
- Desain sistem diselesaikan sebelum implementasi modul terkait.
    
- Database dan API dirancang agar dapat digunakan oleh Android dan website admin secara konsisten.
    
- Setiap fitur diuji setelah diimplementasikan.
    
- Perubahan kode dikelola menggunakan Git.
    
- Pengujian awal dilakukan di lingkungan lokal.
    
- Dokumentasi diperbarui apabila terdapat perubahan desain atau implementasi.
    

## 5. Tahapan dan Jadwal Proyek

Jadwal berikut merupakan estimasi awal. Durasi aktual dapat disesuaikan dengan jumlah anggota tim, kompleksitas implementasi, dan ketersediaan waktu.

|Tahap|Aktivitas Utama|Estimasi|Keluaran|
|---|---|---|---|
|1. Analisis Kebutuhan|Meninjau PRD, kebutuhan pengguna, dan aturan bisnis|2–3 hari|PRD final dan Project Overview|
|2. Perencanaan Proyek|Menentukan prioritas, jadwal, dan pembagian pekerjaan|1–2 hari|Project Plan|
|3. Perancangan Proses|Menyusun workflow dan business process|2–3 hari|Workflow & Business Process|
|4. System Design|Menentukan arsitektur, komponen, dan alur komunikasi|2–3 hari|System Design dan Module Specification|
|5. Database Design|Menyusun ERD, struktur tabel, relasi, dan migrasi|2–3 hari|Database Model, ERD, Data Dictionary|
|6. API Design|Menentukan endpoint, request, response, dan error|2–3 hari|API Contract dan API Error Handling|
|7. UI/UX Design|Membuat alur layar, wireframe, dan desain visual|3–5 hari|UI UX Design, Design System, Screen Specification|
|8. Security Design|Menentukan autentikasi, otorisasi, dan perlindungan data|1–2 hari|Security Specification dan Access Control Matrix|
|9. Backend Development|Membangun API, autentikasi, logika bisnis, dan database|5–8 hari|Backend Laravel|
|10. Website Admin Development|Membangun antarmuka pengelolaan PMB|5–8 hari|Website Admin|
|11. Android Development|Membangun aplikasi calon mahasiswa|5–8 hari|Aplikasi Android|
|12. Integration & Testing|Mengintegrasikan komponen dan menguji fitur|4–6 hari|Test Results dan daftar perbaikan|
|13. Local Deployment|Menyiapkan konfigurasi dan menjalankan sistem lokal|1–2 hari|Deployment Guide dan Environment Configuration|
|14. Dokumentasi Akhir|Menyusun panduan pengguna dan evaluasi proyek|2–3 hari|User Manual, Admin Guide, Release Notes|

**Catatan:** Estimasi pada tabel merupakan hari kerja dan bukan jadwal yang telah disepakati secara final. Beberapa aktivitas, seperti desain keamanan, UI/UX, dan penyusunan test case, dapat dilakukan bersamaan jika tidak menghambat dependensi pekerjaan.

## 6. Prioritas Implementasi

Implementasi dibagi menjadi tiga prioritas.

### Prioritas 1 — Fondasi Sistem

- Struktur repository dan konfigurasi lingkungan lokal.
    
- Database dan migration.
    
- Registrasi dan login.
    
- Autentikasi serta kontrol akses berbasis peran.
    
- Pengelolaan gelombang dan program studi.
    
- Struktur dasar REST API.
    

### Prioritas 2 — Fitur Inti PMB

- Pengisian profil dan formulir pendaftaran.
    
- Pengunggahan dokumen.
    
- Validasi data dan dokumen.
    
- Pencegahan pendaftaran ganda pada gelombang yang sama.
    
- Pengiriman formulir.
    
- Pemeriksaan pendaftaran dan permintaan revisi.
    
- Penetapan keputusan akhir oleh panitia.
    
- Pemantauan status dan riwayat pendaftaran.
    

### Prioritas 3 — Pelaporan dan Penyelesaian

- Dashboard statistik.
    
- Laporan PDF.
    
- Penyempurnaan antarmuka.
    
- Pengujian keamanan dan hak akses.
    
- Pengujian alur lengkap.
    
- Perbaikan bug.
    
- Dokumentasi dan persiapan demonstrasi proyek.
    

## 7. Rencana Sprint

Sprint berikut menggunakan durasi estimasi satu minggu. Durasi dapat disesuaikan dengan kebutuhan akademik dan kapasitas tim.

|Sprint|Fokus|Target Hasil|
|---|---|---|
|Sprint 1|Fondasi proyek dan database|Repository, konfigurasi lokal, migration, autentikasi dasar|
|Sprint 2|Pendaftaran calon mahasiswa|Profil, formulir, pilihan gelombang dan program studi, upload dokumen|
|Sprint 3|Proses pemeriksaan|Daftar pendaftaran, pemeriksaan panitia, revisi, perubahan status|
|Sprint 4|Website admin dan Android|Integrasi antarmuka dengan API, pemantauan status, pengelolaan data|
|Sprint 5|Laporan dan pengujian|Dashboard, laporan PDF, pengujian integrasi, perbaikan bug|
|Sprint 6|Deployment dan dokumentasi|Sistem berjalan lokal, panduan penggunaan, evaluasi akhir|

Sprint di atas merupakan rencana awal. Jika waktu proyek terbatas, prioritas pertama adalah menyelesaikan alur pendaftaran dari awal sampai keputusan akhir sebelum menyempurnakan fitur tambahan.

## 8. Dependensi Antarpekerjaan

Urutan dependensi utama adalah sebagai berikut:

1. PRD menjadi dasar penentuan kebutuhan sistem.
    
2. Workflow menjelaskan proses bisnis dan transisi status.
    
3. System Design menetapkan arsitektur serta pembagian komponen.
    
4. Database Model menetapkan struktur data dan relasi.
    
5. API Contract menetapkan komunikasi backend dengan klien.
    
6. UI/UX Design menetapkan alur layar dan kebutuhan interaksi.
    
7. Implementasi backend, website admin, dan Android mengikuti desain yang telah disepakati.
    
8. Pengujian memvalidasi kesesuaian implementasi dengan kebutuhan.
    
9. Deployment mempersiapkan sistem agar dapat dijalankan di lingkungan lokal.
    
10. Dokumentasi akhir menjelaskan penggunaan dan hasil sistem yang benar-benar diimplementasikan.
    

Beberapa aktivitas dapat berjalan paralel setelah kontrak dan dependensi utamanya jelas. Contohnya, pengembangan UI Android dapat dimulai menggunakan API yang telah disepakati, sementara backend mengimplementasikan endpoint tersebut.

## 9. Pembagian Tanggung Jawab

Pembagian berikut dapat digunakan untuk tim kecil maupun proyek individu.

|Peran Tim|Tanggung Jawab|
|---|---|
|Project Manager|Mengatur prioritas, jadwal, progres, dan risiko|
|System Analyst|Memastikan kebutuhan, aturan bisnis, dan workflow konsisten|
|System & Database Designer|Merancang arsitektur, ERD, struktur tabel, dan relasi|
|Backend Developer|Mengimplementasikan Laravel API, validasi, autentikasi, dan logika bisnis|
|Android Developer|Mengimplementasikan aplikasi Android dengan Kotlin|
|Web Developer|Mengimplementasikan website admin|
|UI/UX Designer|Merancang alur pengguna dan tampilan aplikasi|
|QA/Tester|Menyusun test case, melakukan pengujian, dan mencatat bug|
|Technical Writer|Menyusun dokumentasi teknis dan panduan pengguna|

Satu orang dapat menjalankan beberapa peran sekaligus. Pembagian tersebut merupakan daftar tanggung jawab, bukan ketentuan jumlah anggota tim.

## 10. Strategi Version Control

Git digunakan untuk mengelola perubahan kode dan dokumentasi.

Rekomendasi awal:

- `main`: versi yang dianggap stabil.
    
- `develop`: integrasi pengembangan, jika diperlukan.
    
- `feature/nama-fitur`: pengembangan fitur tertentu.
    
- `fix/nama-perbaikan`: perbaikan bug.
    

Contoh nama branch:

- `feature/auth-api`
    
- `feature/application-form`
    
- `feature/document-upload`
    
- `feature/admin-review`
    
- `feature/application-report`
    

Contoh format commit:

- `feat: add applicant registration`
    
- `feat: implement document upload`
    
- `fix: prevent duplicate application per wave`
    
- `test: add application status test`
    
- `docs: update api contract`
    

File konfigurasi yang berisi password, token, atau informasi rahasia tidak boleh dimasukkan ke repository.

## 11. Rencana Pengujian

Pengujian dilakukan secara bertahap.

### 11.1 Unit Testing

Menguji fungsi individual, seperti validasi formulir, pembuatan nomor registrasi, dan aturan transisi status.

### 11.2 Integration Testing

Menguji komunikasi antara aplikasi Android, website admin, backend Laravel, database MySQL, dan penyimpanan dokumen.

### 11.3 Functional Testing

Memastikan fitur sesuai dengan kebutuhan dalam PRD, termasuk registrasi, pengiriman formulir, revisi, keputusan akhir, dan pelaporan.

### 11.4 Security Testing

Menguji autentikasi, pembatasan akses berdasarkan peran, akses terhadap data pendaftar lain, validasi berkas, serta perlindungan endpoint.

### 11.5 Acceptance Testing

Memastikan alur utama dapat digunakan sesuai kriteria penerimaan, dari calon mahasiswa membuat pendaftaran sampai panitia menetapkan hasil seleksi.

Rincian skenario dan hasil pengujian dicatat pada `Test Plan.md`, `Test Cases.md`, dan `Test Results.md`.

## 12. Risiko Proyek dan Mitigasi

|Risiko|Dampak|Mitigasi|
|---|---|---|
|Perubahan kebutuhan|Desain dan implementasi harus diubah|Catat perubahan dan tinjau dokumen yang terdampak|
|Ketidaksesuaian database dan API|Fitur gagal berkomunikasi|Tetapkan model data dan API contract sebelum integrasi|
|Pendaftaran ganda|Data pendaftaran tidak konsisten|Gunakan validasi backend dan unique constraint pada database|
|Akses tidak sah|Kebocoran data pendaftar|Terapkan autentikasi, RBAC, dan pemeriksaan kepemilikan data|
|Upload dokumen bermasalah|Dokumen gagal tersimpan atau tidak aman|Validasi berkas dan gunakan penyimpanan privat|
|Koneksi lokal gagal|Android atau website tidak dapat mengakses API|Periksa alamat server, jaringan, port, dan firewall|
|Waktu pengembangan terbatas|Fitur MVP tidak selesai|Dahulukan alur pendaftaran inti dan keputusan seleksi|
|Kehilangan data lokal|Data pengujian atau dokumen hilang|Lakukan backup database dan berkas secara berkala|
|Kesalahan perubahan status|Keputusan atau riwayat tidak konsisten|Terapkan aturan transisi status dan pencatatan riwayat|

## 13. Kriteria Penyelesaian Proyek

Proyek dinyatakan siap untuk evaluasi MVP apabila:

- Fitur yang termasuk dalam ruang lingkup MVP telah diimplementasikan atau memiliki status pengecualian yang disetujui.
    
- Calon mahasiswa dapat melakukan pendaftaran dan melihat statusnya sendiri.
    
- Panitia dapat memeriksa pendaftaran, meminta revisi, dan menetapkan keputusan akhir.
    
- Admin dapat mengelola data yang menjadi kewenangannya.
    
- Pimpinan dapat mengakses laporan sesuai hak akses.
    
- Pencegahan pendaftaran ganda per gelombang berjalan.
    
- Hak akses dan perlindungan dokumen telah diuji.
    
- Pengujian alur utama telah dilakukan dan hasilnya terdokumentasi.
    
- Aplikasi dapat dijalankan di lingkungan lokal sesuai panduan.
    
- Dokumen teknis dan panduan pengguna telah diperbarui sesuai implementasi aktual.
    

## 14. Pemantauan Progres

Progres dapat dicatat menggunakan status berikut:

- `Not Started` — pekerjaan belum dimulai.
    
- `In Progress` — pekerjaan sedang dikerjakan.
    
- `In Review` — hasil pekerjaan sedang ditinjau.
    
- `Blocked` — pekerjaan terhambat oleh dependensi atau masalah.
    
- `Done` — pekerjaan selesai dan memenuhi kriteria penerimaan.
    

Setiap pekerjaan sebaiknya memiliki penanggung jawab, target waktu, dependensi, dan bukti hasil. Pekerjaan baru dianggap selesai apabila hasilnya telah diperiksa, bukan hanya karena kode atau dokumennya sudah dibuat.

## 15. Catatan Perubahan

|Versi|Tanggal|Perubahan|
|---|---|---|
|1.0|2026-10-16|Pembuatan rencana awal pengembangan PMB ITB Akiday|

---

**Dokumen acuan:** `PRD Final v1.0.md`

**Dokumen terkait:** `Project Overview.md`, `Workflow & Business Process.md`, `System Design.md`, `Database Model.md`, dan `API Contract.md`.

**Catatan akhir:** Jadwal, sprint, dan pembagian tanggung jawab dalam dokumen ini masih berupa baseline perencanaan. Sesuaikan setelah jumlah anggota tim, tenggat akademik, dan kapasitas pengerjaan ditentukan.