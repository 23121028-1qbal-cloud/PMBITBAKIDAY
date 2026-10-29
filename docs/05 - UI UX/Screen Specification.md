# Screen Specification — PMB ITB Akiday

**Nama Proyek:** Sistem Informasi Penerimaan Mahasiswa Baru ITB Akiday  
**Versi Dokumen:** 1.0  
**Status:** Draft untuk Implementasi  
**Platform:** Android (Kotlin), Website Admin  
**Backend:** Laravel REST API  
**Database:** MySQL

---

## 1. Tujuan Dokumen

Dokumen Screen Specification menjelaskan rancangan setiap layar yang dibutuhkan dalam sistem PMB ITB Akiday. Dokumen ini menjadi acuan bagi pengembang dan perancang antarmuka ketika membuat tampilan aplikasi, menghubungkan antarmuka dengan API, serta menguji fungsi setiap layar.

Setiap spesifikasi layar mencakup:

- ID dan nama layar.
    
- Tujuan layar.
    
- Elemen antarmuka.
    
- Aksi yang dapat dilakukan pengguna.
    
- Validasi dan aturan bisnis.
    
- Kondisi loading, kosong, berhasil, dan gagal.
    
- Kebutuhan akses berdasarkan role.
    

Dokumen ini menjelaskan spesifikasi perilaku dan struktur layar, bukan desain piksel final. Detail visual mengikuti dokumen [[Design System]] dan [[UI UX Design]].

## 2. Ruang Lingkup

Spesifikasi dibagi menjadi dua platform.

### 2.1 Aplikasi Android Calon Mahasiswa

Aplikasi Android digunakan oleh calon mahasiswa untuk membuat akun, melengkapi profil, memilih gelombang dan program studi, mengunggah dokumen, mengirim pendaftaran, serta memantau status pendaftaran.

### 2.2 Website Admin

Website admin digunakan oleh admin, panitia, dan pimpinan untuk mengelola data sesuai kewenangan masing-masing, memeriksa pendaftaran, meminta revisi, menetapkan keputusan penerimaan, serta melihat laporan.

## 3. Konvensi dan Aturan Umum

### 3.1 Role Pengguna

|Role|Kode|Tanggung Jawab Utama|
|---|---|---|
|Calon Mahasiswa|`calon_mahasiswa`|Mengelola profil dan pendaftaran miliknya|
|Admin|`admin`|Mengelola pengguna dan data operasional sesuai izin|
|Panitia|`panitia`|Memeriksa dokumen dan memproses pendaftaran sesuai izin|
|Pimpinan|`pimpinan`|Melihat ringkasan dan laporan sesuai izin|

Tampilan menu harus mengikuti hak akses pengguna. Menyembunyikan menu tidak menggantikan pemeriksaan otorisasi di backend.

### 3.2 Status Pendaftaran

|Kode Status|Label Tampilan|Makna|
|---|---|---|
|`draft`|Draft|Pendaftaran belum dikirim|
|`submitted`|Menunggu Pemeriksaan|Pendaftaran sudah dikirim|
|`review`|Sedang Ditinjau|Pendaftaran sedang diperiksa|
|`revision`|Perlu Revisi|Calon mahasiswa perlu memperbaiki data atau dokumen|
|`accepted`|Diterima|Pendaftaran diputuskan diterima oleh panitia berwenang|
|`rejected`|Tidak Diterima|Pendaftaran diputuskan tidak diterima oleh panitia berwenang|

Label pada antarmuka boleh menggunakan bahasa yang lebih mudah dipahami, tetapi kode status yang digunakan aplikasi harus konsisten dengan backend.

### 3.3 Aturan Pendaftaran

1. Calon mahasiswa harus memiliki akun untuk membuat pendaftaran.
    
2. Setiap pendaftaran terhubung ke satu gelombang dan satu program studi.
    
3. Satu akun hanya dapat memiliki satu pendaftaran pada gelombang yang sama.
    
4. Pendaftaran pada gelombang berbeda mengikuti jadwal dan kebijakan gelombang tersebut.
    
5. Dokumen wajib harus tersedia sebelum pendaftaran dapat dikirim.
    
6. Validasi otomatis hanya memeriksa kelengkapan, format, dan persyaratan teknis data atau dokumen.
    
7. Keputusan diterima atau tidak diterima ditetapkan oleh panitia berwenang, bukan oleh algoritma otomatis.
    
8. Calon mahasiswa hanya dapat melihat dan mengubah data pendaftarannya sendiri sesuai status dan aturan sistem.
    

### 3.4 Kondisi Standar Setiap Layar

Setiap layar yang mengambil atau mengirim data ke backend harus menangani kondisi berikut:

- **Loading:** proses sedang berlangsung.
    
- **Success:** permintaan berhasil dan hasil telah dikonfirmasi backend.
    
- **Empty:** belum ada data yang tersedia.
    
- **Error:** permintaan gagal atau terjadi masalah.
    
- **Unauthorized:** pengguna belum masuk atau sesi tidak lagi valid.
    
- **Forbidden:** pengguna tidak memiliki hak untuk melakukan tindakan tersebut.
    

---

# 4. Screen Specification — Aplikasi Android

## 4.1 Daftar Layar Android

|ID|Nama Layar|Fungsi Utama|
|---|---|---|
|AND-01|Splash Screen|Menampilkan identitas aplikasi saat dibuka|
|AND-02|Login|Masuk menggunakan email dan password|
|AND-03|Registrasi Akun|Membuat akun calon mahasiswa|
|AND-04|Beranda|Menampilkan ringkasan pendaftaran|
|AND-05|Profil Calon Mahasiswa|Melengkapi data pribadi|
|AND-06|Daftar Gelombang|Melihat gelombang yang tersedia|
|AND-07|Formulir Pendaftaran|Membuat dan melengkapi pendaftaran|
|AND-08|Unggah Dokumen|Mengunggah foto dan ijazah/SKL|
|AND-09|Pemeriksaan Akhir|Memeriksa data sebelum dikirim|
|AND-10|Detail Pendaftaran|Melihat informasi lengkap pendaftaran|
|AND-11|Status Pendaftaran|Memantau status dan riwayat|
|AND-12|Profil dan Pengaturan|Mengelola akun dan keluar|

## 4.2 AND-01 — Splash Screen

**Tujuan:** Menampilkan identitas PMB ITB Akiday saat aplikasi pertama kali dibuka.

**Elemen UI:**

- Logo atau identitas aplikasi.
    
- Nama aplikasi.
    
- Indikator loading jika diperlukan.
    

**Perilaku:**

1. Aplikasi memeriksa status sesi autentikasi.
    
2. Jika pengguna belum masuk, arahkan ke Login.
    
3. Jika pengguna sudah masuk dan sesi masih valid, arahkan ke Beranda.
    
4. Jika pemeriksaan sesi membutuhkan jaringan dan gagal, tampilkan penanganan yang sesuai tanpa menganggap pengguna otomatis terautentikasi.
    

**Kriteria penerimaan:**

- Splash screen tidak tertahan tanpa batas.
    
- Pengguna tidak dapat melewati autentikasi hanya dengan membuka aplikasi.
    
- Token dan status sesi diperiksa menggunakan mekanisme yang aman.
    

## 4.3 AND-02 — Login

**Tujuan:** Mengautentikasi calon mahasiswa sebelum mengakses fitur pendaftaran.

**Elemen UI:**

- Judul halaman.
    
- Input email.
    
- Input password.
    
- Tombol tampil/sembunyikan password.
    
- Tombol Masuk.
    
- Tautan Buat Akun.
    
- Area pesan kesalahan.
    

**Aksi pengguna:**

- Mengisi email dan password.
    
- Menekan tombol Masuk.
    
- Berpindah ke Registrasi Akun.
    

**Validasi:**

- Email wajib diisi dan memiliki format yang valid.
    
- Password wajib diisi.
    
- Tombol Masuk menunjukkan loading selama proses berlangsung.
    
- Permintaan login tidak dikirim berulang kali selama permintaan sebelumnya masih diproses.
    

**Integrasi API:**

- `POST /api/v1/auth/login`
    

**Respons UI:**

- Berhasil: simpan kredensial sesi menggunakan penyimpanan yang aman dan arahkan ke Beranda.
    
- Kredensial salah: tampilkan pesan umum seperti `Email atau password tidak sesuai.`
    
- Jaringan gagal: tampilkan pesan bahwa proses belum berhasil.
    
- Akun tidak aktif: tampilkan pesan sesuai kebijakan backend tanpa membocorkan informasi yang tidak diperlukan.
    

**Kriteria penerimaan:**

- Password tidak ditampilkan sebagai teks biasa secara default.
    
- Password dan token tidak dicatat ke log.
    
- Pesan error tidak menampilkan detail internal server.
    

## 4.4 AND-03 — Registrasi Akun

**Tujuan:** Membuat akun calon mahasiswa.

**Elemen UI:**

- Input email.
    
- Input password.
    
- Input konfirmasi password.
    
- Tombol Buat Akun.
    
- Tautan kembali ke Login.
    
- Pesan validasi.
    

**Aksi pengguna:**

- Mengisi informasi akun.
    
- Mengirim formulir registrasi.
    
- Kembali ke halaman Login.
    

**Validasi:**

- Email wajib diisi dengan format yang benar.
    
- Password harus memenuhi kebijakan password sistem.
    
- Konfirmasi password harus sama dengan password.
    
- Email yang sudah digunakan tidak dapat didaftarkan kembali.
    

**Integrasi API:**

- `POST /api/v1/auth/register`
    

**Respons UI:**

- Berhasil: tampilkan konfirmasi akun berhasil dibuat dan arahkan pengguna sesuai alur autentikasi yang diterapkan.
    
- Validasi gagal: tampilkan pesan pada kolom terkait.
    
- Email sudah terdaftar: arahkan pengguna untuk mencoba login.
    
- Koneksi gagal: pertahankan data formulir yang aman.
    

**Catatan:** Verifikasi email tidak termasuk MVP. Jika kebijakan ini berubah, alur registrasi harus diperbarui.

## 4.5 AND-04 — Beranda

**Tujuan:** Memberikan ringkasan informasi pendaftaran kepada calon mahasiswa.

**Elemen UI:**

- Sapaan pengguna.
    
- Ringkasan status pendaftaran.
    
- Informasi gelombang yang tersedia.
    
- Tombol Mulai Pendaftaran jika pengguna memenuhi syarat.
    
- Tombol Lihat Status jika sudah memiliki pendaftaran.
    
- Informasi atau pengumuman jika tersedia.
    

**Aksi pengguna:**

- Membuka formulir pendaftaran.
    
- Melihat detail pendaftaran.
    
- Melihat status terbaru.
    
- Membuka profil.
    

**Aturan bisnis:**

- Jika belum memiliki pendaftaran pada gelombang yang tersedia, tampilkan ajakan memulai pendaftaran.
    
- Jika sudah memiliki pendaftaran pada gelombang tersebut, tampilkan pendaftaran yang ada dan jangan menawarkan pembuatan duplikat.
    
- Jika tidak ada gelombang aktif, tampilkan pesan bahwa pendaftaran belum tersedia.
    
- Gelombang dan program studi harus berasal dari data backend.
    

**Integrasi API:**

- `GET /api/v1/auth/me`
    
- `GET /api/v1/admission-waves`
    
- `GET /api/v1/applications` jika endpoint daftar pendaftaran tersedia dalam implementasi API.
    

**Kondisi kosong:** `Belum ada pendaftaran. Periksa gelombang yang tersedia untuk memulai.`

**Kriteria penerimaan:**

- Ringkasan mencerminkan data terbaru yang diterima dari backend.
    
- Tombol tindakan menyesuaikan status pendaftaran.
    
- Data milik pengguna lain tidak pernah ditampilkan.
    

## 4.6 AND-05 — Profil Calon Mahasiswa

**Tujuan:** Mengumpulkan dan mengelola data pribadi calon mahasiswa.

**Elemen UI:**

- Nama lengkap.
    
- NIK.
    
- Tempat lahir.
    
- Tanggal lahir.
    
- Jenis kelamin.
    
- Email akun, ditampilkan sesuai kebijakan perubahan email.
    
- Nomor telepon.
    
- Alamat.
    
- Tombol Simpan Perubahan.
    

**Aksi pengguna:**

- Mengisi atau memperbarui data.
    
- Menyimpan perubahan.
    
- Kembali ke Beranda.
    

**Validasi:**

- Nama lengkap wajib diisi.
    
- NIK harus berisi 16 digit jika aturan tersebut ditetapkan.
    
- Tempat lahir, tanggal lahir, dan jenis kelamin mengikuti aturan field yang ditentukan.
    
- Nomor telepon harus mengikuti format yang diterima sistem.
    
- Alamat wajib diisi apabila ditetapkan sebagai persyaratan pendaftaran.
    

**Integrasi API:**

- `GET /api/v1/applicant/profile`
    
- `PUT /api/v1/applicant/profile`
    

**Respons UI:**

- Berhasil: `Profil berhasil disimpan.`
    
- Validasi gagal: tampilkan kesalahan di dekat input.
    
- Konflik perubahan: muat ulang data terbaru dan minta pengguna meninjau perubahan.
    

**Kriteria penerimaan:**

- Data tersimpan pada akun yang benar.
    
- Perubahan hanya diperbolehkan sesuai hak akses dan status pendaftaran.
    
- Kebijakan keunikan NIK mengikuti keputusan sistem yang telah disepakati.
    

## 4.7 AND-06 — Daftar Gelombang

**Tujuan:** Menampilkan gelombang penerimaan dan status ketersediaannya.

**Elemen UI:**

- Nama gelombang.
    
- Tanggal mulai dan berakhir.
    
- Status gelombang.
    
- Tombol Lihat Detail atau Pilih Gelombang.
    

**Aksi pengguna:**

- Melihat daftar gelombang.
    
- Memilih gelombang yang tersedia.
    
- Melihat informasi program studi jika disediakan.
    

**Aturan bisnis:**

- Gelombang yang sudah berakhir tidak dapat digunakan untuk membuat pendaftaran baru.
    
- Gelombang yang belum dibuka tidak dapat dipilih.
    
- Status aktif pada database tidak otomatis berarti tanggal pendaftaran sedang terbuka; backend harus memeriksa jadwal dan kebijakan yang berlaku.
    
- Pengguna tidak boleh membuat pendaftaran kedua pada gelombang yang sama.
    

**Integrasi API:**

- `GET /api/v1/admission-waves`
    

**Kondisi kosong:** `Belum ada gelombang penerimaan yang tersedia.`

## 4.8 AND-07 — Formulir Pendaftaran

**Tujuan:** Membuat satu pendaftaran untuk satu gelombang dan satu program studi.

**Elemen UI:**

- Pilihan gelombang.
    
- Pilihan program studi.
    
- Ringkasan profil calon mahasiswa.
    
- Informasi persyaratan.
    
- Tombol Simpan Draft.
    
- Tombol Lanjut ke Dokumen.
    
- Pesan validasi.
    

**Aksi pengguna:**

- Memilih gelombang.
    
- Memilih satu program studi.
    
- Memeriksa informasi profil.
    
- Menyimpan draft.
    
- Melanjutkan ke pengunggahan dokumen.
    

**Validasi:**

- Gelombang harus tersedia dan menerima pendaftaran.
    
- Program studi harus aktif dan tersedia untuk gelombang yang dipilih sesuai aturan sistem.
    
- Hanya satu program studi dapat dipilih pada satu pendaftaran.
    
- Profil wajib harus lengkap sebelum submit.
    
- Sistem mencegah pendaftaran duplikat pada gelombang yang sama.
    

**Integrasi API:**

- `GET /api/v1/admission-waves`
    
- `GET /api/v1/study-programs`
    
- `POST /api/v1/applications`
    
- `GET /api/v1/applications/{id}` jika diperlukan untuk melanjutkan draft.
    

**Aturan teknis:** Pencegahan duplikasi harus dilakukan oleh backend dan database, bukan hanya dengan menonaktifkan tombol di Android.

**Kriteria penerimaan:**

- Draft dapat dilanjutkan sesuai aturan sistem.
    
- Satu pendaftaran hanya terhubung ke satu program studi.
    
- Konflik pendaftaran ditangani dengan pesan yang mudah dipahami.
    

## 4.9 AND-08 — Unggah Dokumen

**Tujuan:** Mengunggah dokumen wajib untuk pendaftaran.

**Elemen UI:**

- Bagian Foto.
    
- Bagian Ijazah/SKL.
    
- Status setiap dokumen.
    
- Tombol Pilih Berkas.
    
- Nama dan ukuran berkas.
    
- Tombol Ganti Berkas jika diperbolehkan.
    
- Indikator progres unggah.
    
- Pesan validasi.
    

**Aksi pengguna:**

- Memilih foto dari perangkat.
    
- Memilih berkas ijazah atau SKL.
    
- Mengunggah dokumen.
    
- Mengganti dokumen jika diperbolehkan.
    

**Validasi:**

- Jenis dokumen harus sesuai.
    
- Format file dan ukuran maksimum mengikuti konfigurasi backend.
    
- Berkas yang rusak atau tidak valid harus ditolak.
    
- Nama berkas tidak boleh menjadi satu-satunya dasar untuk menentukan jenis atau keamanan file.
    
- Dokumen wajib harus berhasil diunggah sebelum pendaftaran dapat dikirim.
    

**Integrasi API:**

- `POST /api/v1/applications/{id}/documents`
    
- `GET /api/v1/applications/{id}/documents`
    

Endpoint penggantian atau penghapusan dokumen harus mengikuti kontrak API yang benar-benar diimplementasikan.

**Respons UI:**

- Berhasil: tampilkan nama berkas dan status unggah berhasil.
    
- Gagal: tampilkan alasan yang dapat ditindaklanjuti.
    
- Koneksi terputus: jangan menandai berkas sebagai berhasil jika server belum mengonfirmasi penyimpanan.
    

**Keamanan:**

- Simpan dokumen dalam private storage.
    
- Akses unduh harus melewati autentikasi dan otorisasi.
    
- Jangan menampilkan path internal penyimpanan kepada pengguna.
    

## 4.10 AND-09 — Pemeriksaan Akhir

**Tujuan:** Memungkinkan calon mahasiswa memeriksa data sebelum mengirim pendaftaran.

**Elemen UI:**

- Ringkasan data pribadi.
    
- Gelombang yang dipilih.
    
- Program studi yang dipilih.
    
- Daftar dokumen beserta statusnya.
    
- Tombol Kembali untuk memperbaiki data.
    
- Tombol Kirim Pendaftaran.
    
- Dialog konfirmasi pengiriman.
    

**Aksi pengguna:**

- Memeriksa seluruh informasi.
    
- Kembali ke bagian yang perlu diperbaiki.
    
- Mengonfirmasi pengiriman pendaftaran.
    

**Validasi:**

- Profil wajib lengkap.
    
- Pilihan gelombang dan program studi valid.
    
- Semua dokumen wajib telah berhasil diunggah.
    
- Pendaftaran belum dikirim sebelumnya.
    
- Backend harus memeriksa ulang kelengkapan dan status gelombang ketika permintaan diterima.
    

**Integrasi API:**

- `GET /api/v1/applications/{id}`
    
- `POST /api/v1/applications/{id}/submit`
    

**Respons UI:**

- Berhasil: tampilkan nomor pendaftaran dan status `submitted`.
    
- Belum lengkap: tampilkan daftar persyaratan yang belum terpenuhi.
    
- Konflik duplikasi: muat data pendaftaran yang sudah ada.
    
- Koneksi gagal atau timeout: periksa status pendaftaran sebelum pengguna mencoba mengirim kembali.
    

**Kriteria penerimaan:**

- Pengguna mendapatkan konfirmasi hanya setelah backend menyatakan pengiriman berhasil.
    
- Pengiriman berulang tidak menghasilkan pendaftaran duplikat.
    
- Status dan riwayat diperbarui secara konsisten.
    

## 4.11 AND-10 — Detail Pendaftaran

**Tujuan:** Menampilkan rincian satu pendaftaran milik calon mahasiswa.

**Elemen UI:**

- Nomor pendaftaran.
    
- Gelombang penerimaan.
    
- Program studi.
    
- Tanggal pengiriman.
    
- Status terkini.
    
- Daftar dokumen.
    
- Catatan revisi jika ada.
    
- Riwayat perubahan status.
    
- Tindakan yang tersedia sesuai status.
    

**Aksi pengguna:**

- Melihat rincian.
    
- Memperbaiki data atau dokumen jika diminta.
    
- Membuka riwayat status.
    

**Aturan bisnis:**

- Hanya pendaftaran milik pengguna yang boleh ditampilkan.
    
- Data final yang tidak boleh diubah harus ditampilkan dalam mode baca saja.
    
- Catatan revisi harus berasal dari panitia atau backend.
    
- Jangan menampilkan tombol revisi jika status tidak mengizinkannya.
    

**Integrasi API:**

- `GET /api/v1/applications/{id}`
    
- `GET /api/v1/applications/{id}/status-history`
    

## 4.12 AND-11 — Status Pendaftaran

**Tujuan:** Menampilkan status terkini dan perkembangan pemeriksaan pendaftaran.

**Elemen UI:**

- Nomor pendaftaran.
    
- Badge status.
    
- Ringkasan tahap proses.
    
- Tanggal pembaruan terakhir.
    
- Catatan revisi jika ada.
    
- Daftar riwayat status.
    

**Aksi pengguna:**

- Memperbarui tampilan status.
    
- Membaca catatan revisi.
    
- Membuka detail pendaftaran.
    

**Aturan bisnis:**

- Status berasal dari backend.
    
- Riwayat status ditampilkan berurutan berdasarkan waktu.
    
- Perubahan status penting harus tercatat di backend.
    
- Keputusan diterima atau tidak diterima harus berasal dari keputusan panitia yang berwenang.
    

**Integrasi API:**

- `GET /api/v1/applications/{id}`
    
- `GET /api/v1/applications/{id}/status-history`
    

**Kondisi kosong:** Jika belum ada pendaftaran, tampilkan petunjuk untuk memulai pendaftaran apabila gelombang tersedia.

## 4.13 AND-12 — Profil dan Pengaturan

**Tujuan:** Memberikan akses ke informasi akun dan tindakan terkait sesi.

**Elemen UI:**

- Nama pengguna.
    
- Email akun.
    
- Menu Profil.
    
- Menu Pengaturan jika tersedia.
    
- Tombol Keluar.
    

**Aksi pengguna:**

- Membuka profil.
    
- Keluar dari akun.
    
- Melihat informasi akun.
    

**Integrasi API:**

- `GET /api/v1/auth/me`
    
- `POST /api/v1/auth/logout`
    

**Aturan bisnis:**

- Pengguna harus mengonfirmasi logout jika diperlukan oleh desain.
    
- Kredensial sesi lokal harus dihapus setelah logout sesuai mekanisme autentikasi.
    
- Jika logout gagal karena jaringan, aplikasi tetap harus menghindari penggunaan sesi yang sudah dihapus secara lokal dan menangani status server dengan benar.
    

---

# 5. Screen Specification — Website Admin

## 5.1 Daftar Layar Website

|ID|Nama Layar|Fungsi Utama|
|---|---|---|
|WEB-01|Login Admin|Mengautentikasi pengguna internal|
|WEB-02|Dashboard|Menampilkan ringkasan penerimaan|
|WEB-03|Daftar Pendaftar|Melihat dan mencari pendaftaran|
|WEB-04|Detail Pendaftar|Melihat data dan dokumen pendaftar|
|WEB-05|Pemeriksaan Pendaftaran|Memeriksa kelengkapan dan memproses revisi|
|WEB-06|Keputusan Penerimaan|Menetapkan hasil penerimaan sesuai kewenangan|
|WEB-07|Manajemen Gelombang|Mengelola jadwal gelombang|
|WEB-08|Manajemen Program Studi|Mengelola pilihan program studi|
|WEB-09|Manajemen Pengguna|Mengelola akun dan role|
|WEB-10|Laporan|Melihat dan mengunduh laporan|
|WEB-11|Profil Admin|Melihat informasi akun admin|

## 5.2 WEB-01 — Login Admin

**Tujuan:** Memberikan akses ke website sesuai role pengguna internal.

**Elemen UI:**

- Input email.
    
- Input password.
    
- Tombol Masuk.
    
- Pesan kesalahan.
    

**Validasi:**

- Email dan password wajib diisi.
    
- Backend harus memeriksa kredensial dan status akun.
    
- Hak akses ditentukan dari role dan kebijakan otorisasi server.
    

**Integrasi API:**

- `POST /api/v1/auth/login`
    
- `GET /api/v1/auth/me`
    

**Respons UI:**

- Berhasil: arahkan ke Dashboard.
    
- Tidak berwenang: tampilkan pesan akses ditolak.
    
- Kredensial salah: tampilkan pesan umum.
    
- Sesi kedaluwarsa: arahkan kembali ke Login setelah menangani data yang belum disimpan.
    

**Kriteria penerimaan:**

- Calon mahasiswa tidak mendapatkan akses ke halaman internal.
    
- Hak akses tetap diverifikasi oleh backend.
    

## 5.3 WEB-02 — Dashboard

**Tujuan:** Memberikan gambaran umum proses penerimaan mahasiswa baru.

**Elemen UI:**

- Ringkasan jumlah pendaftar.
    
- Jumlah pendaftaran per status.
    
- Ringkasan per gelombang.
    
- Ringkasan per program studi.
    
- Tabel pendaftaran terbaru jika diperlukan.
    
- Filter gelombang dan periode jika tersedia.
    

**Aksi pengguna:**

- Memilih filter.
    
- Membuka daftar pendaftar.
    
- Membuka laporan.
    
- Melihat detail pendaftaran sesuai izin.
    

**Aturan bisnis:**

- Angka dashboard harus berasal dari data backend.
    
- Perhitungan harus menggunakan definisi yang konsisten, misalnya apakah statistik menghitung semua pendaftaran atau hanya gelombang tertentu.
    
- Pimpinan hanya dapat melihat data sesuai hak aksesnya.
    
- Dashboard tidak boleh menampilkan data sensitif secara berlebihan.
    

**Integrasi API:**

- Endpoint dashboard yang direncanakan, misalnya `GET /api/v1/admin/dashboard`.
    

**Kondisi UI:**

- Loading saat mengambil data.
    
- Pesan kosong jika belum ada pendaftaran.
    
- Pesan gagal jika statistik tidak berhasil dimuat.
    

## 5.4 WEB-03 — Daftar Pendaftar

**Tujuan:** Menampilkan daftar pendaftaran untuk kebutuhan pemeriksaan dan pemantauan.

**Elemen UI:**

- Tabel pendaftar.
    
- Kolom nomor pendaftaran.
    
- Nama calon mahasiswa.
    
- Gelombang.
    
- Program studi.
    
- Tanggal pengiriman.
    
- Status.
    
- Kolom pencarian.
    
- Filter status.
    
- Filter gelombang.
    
- Tombol Lihat Detail.
    
- Navigasi halaman jika data banyak.
    

**Aksi pengguna:**

- Mencari pendaftar.
    
- Memfilter data.
    
- Membuka detail.
    
- Mengurutkan kolom yang didukung.
    
- Berpindah halaman.
    

**Aturan bisnis:**

- Hanya role yang berwenang dapat membuka halaman.
    
- Pencarian dan filter harus diterapkan secara konsisten dengan backend.
    
- Data sensitif yang tidak dibutuhkan tidak perlu ditampilkan dalam tabel.
    
- Pagination digunakan agar pengambilan data tidak membebani sistem.
    

**Integrasi API:**

- `GET /api/v1/admin/applications`
    

Parameter pencarian, filter, dan pagination harus mengikuti kontrak API yang diterapkan.

**Kondisi kosong:** `Tidak ada pendaftaran yang sesuai dengan pencarian.`

## 5.5 WEB-04 — Detail Pendaftar

**Tujuan:** Menampilkan seluruh informasi yang dibutuhkan untuk memahami satu pendaftaran.

**Elemen UI:**

- Identitas dan nomor pendaftaran.
    
- Data pribadi.
    
- Gelombang dan program studi.
    
- Daftar dokumen.
    
- Status pendaftaran.
    
- Riwayat status.
    
- Catatan pemeriksaan.
    
- Tombol tindakan sesuai role dan status.
    

**Aksi pengguna:**

- Melihat data.
    
- Memeriksa dokumen.
    
- Membuka layar Pemeriksaan Pendaftaran.
    
- Membuka layar Keputusan Penerimaan jika memiliki izin.
    

**Aturan bisnis dan keamanan:**

- Backend memeriksa hak akses sebelum mengembalikan data.
    
- Dokumen hanya dapat dibuka oleh pengguna yang berwenang.
    
- Akses dokumen harus melalui endpoint terproteksi atau mekanisme akses sementara yang aman.
    
- Setiap tindakan penting harus tercatat pada riwayat yang sesuai.
    

**Integrasi API:**

- `GET /api/v1/admin/applications/{id}`
    
- Endpoint dokumen dan riwayat sesuai kontrak implementasi.
    

## 5.6 WEB-05 — Pemeriksaan Pendaftaran

**Tujuan:** Membantu panitia memeriksa kelengkapan data dan dokumen calon mahasiswa.

**Elemen UI:**

- Ringkasan data pendaftar.
    
- Daftar persyaratan.
    
- Status setiap dokumen.
    
- Area pratinjau dokumen jika didukung.
    
- Catatan pemeriksaan.
    
- Tombol Mulai Pemeriksaan.
    
- Tombol Minta Revisi.
    
- Tombol Selesai Memeriksa atau tindakan lanjutan sesuai alur yang ditetapkan.
    

**Aksi pengguna:**

- Memeriksa data.
    
- Membuka dokumen.
    
- Menambahkan catatan.
    
- Meminta revisi.
    
- Menandai pemeriksaan selesai.
    

**Validasi:**

- Pendaftaran harus berada pada status yang sesuai.
    
- Catatan revisi harus jelas dan terkait dengan masalah yang ditemukan.
    
- Tindakan hanya dapat dilakukan oleh panitia atau role lain yang diberi izin.
    
- Backend harus memeriksa status terkini untuk mencegah perubahan yang bertentangan.
    

**Integrasi API:**

- `POST /api/v1/admin/applications/{id}/review`
    
- `POST /api/v1/admin/applications/{id}/request-revision`
    

Nama endpoint merupakan rancangan awal dan harus diselaraskan dengan implementasi final.

**Respons UI:**

- Berhasil: tampilkan status terbaru dan catatan tindakan.
    
- Konflik status: muat ulang data dan beri tahu pengguna bahwa pendaftaran telah berubah.
    
- Gagal: pertahankan catatan yang belum terkirim apabila aman.
    

**Kriteria penerimaan:**

- Sistem tidak memutuskan penerimaan akademik secara otomatis.
    
- Catatan revisi dapat dipahami oleh calon mahasiswa.
    
- Perubahan status dan pelakunya tercatat.
    

## 5.7 WEB-06 — Keputusan Penerimaan

**Tujuan:** Memfasilitasi keputusan akhir diterima atau tidak diterima oleh panitia yang berwenang.

**Elemen UI:**

- Ringkasan pendaftar.
    
- Hasil pemeriksaan.
    
- Status pendaftaran saat ini.
    
- Pilihan keputusan.
    
- Catatan keputusan.
    
- Dialog konfirmasi.
    
- Tombol Simpan Keputusan.
    

**Aksi pengguna:**

- Memilih Diterima atau Tidak Diterima.
    
- Menambahkan catatan sesuai kebijakan.
    
- Memeriksa ringkasan.
    
- Mengonfirmasi keputusan.
    

**Aturan bisnis:**

- Keputusan hanya dapat dilakukan oleh role yang diberi kewenangan.
    
- Sistem tidak boleh menetapkan keputusan final berdasarkan validasi otomatis.
    
- Status pendaftaran harus memenuhi aturan transisi yang ditetapkan.
    
- Keputusan harus dicatat dengan pelaku dan waktu perubahan.
    
- Perubahan keputusan final setelah disimpan harus mengikuti kebijakan khusus dan tidak boleh dilakukan secara diam-diam.
    

**Integrasi API:**

- `POST /api/v1/admin/applications/{id}/decision`
    

**Respons UI:**

- Berhasil: tampilkan keputusan terbaru.
    
- Konflik status: minta pengguna memuat ulang data.
    
- Tidak berwenang: tampilkan akses ditolak.
    
- Gagal: jangan tampilkan keputusan sebagai berhasil.
    

**Kriteria penerimaan:**

- Setiap keputusan dapat ditelusuri.
    
- Keputusan tidak dibuat otomatis oleh sistem.
    
- Hak akses diperiksa di backend.
    

## 5.8 WEB-07 — Manajemen Gelombang

**Tujuan:** Mengelola jadwal dan status gelombang penerimaan.

**Elemen UI:**

- Tabel gelombang.
    
- Nama gelombang.
    
- Tanggal mulai.
    
- Tanggal berakhir.
    
- Status aktif.
    
- Tombol Tambah Gelombang.
    
- Tombol Edit.
    
- Formulir tambah/edit.
    

**Aksi pengguna:**

- Membuat gelombang.
    
- Mengubah informasi gelombang.
    
- Mengaktifkan atau menonaktifkan gelombang sesuai izin.
    

**Validasi:**

- Nama gelombang wajib diisi.
    
- Tanggal mulai harus sesuai dengan aturan jadwal.
    
- Tanggal berakhir tidak boleh lebih awal daripada tanggal mulai.
    
- Perubahan gelombang yang sudah digunakan harus memperhatikan pendaftaran yang ada.
    
- Sistem harus memiliki aturan untuk menangani gelombang yang tanggalnya bertumpang tindih.
    

**Integrasi API:**

- `GET /api/v1/admin/admission-waves`
    
- `POST /api/v1/admin/admission-waves`
    
- `PUT /api/v1/admin/admission-waves/{id}`
    

**Kriteria penerimaan:**

- Gelombang tidak dapat digunakan di luar jadwal yang diizinkan.
    
- Perubahan tidak menghapus riwayat pendaftaran secara tidak sengaja.
    

## 5.9 WEB-08 — Manajemen Program Studi

**Tujuan:** Mengelola pilihan program studi yang tersedia pada sistem.

**Elemen UI:**

- Tabel program studi.
    
- Kode program studi.
    
- Nama program studi.
    
- Status aktif.
    
- Tombol Tambah.
    
- Tombol Edit.
    
- Formulir program studi.
    

**Aksi pengguna:**

- Menambah program studi.
    
- Mengubah nama atau informasi yang diizinkan.
    
- Mengaktifkan atau menonaktifkan program studi.
    

**Validasi:**

- Kode program studi harus unik.
    
- Nama wajib diisi.
    
- Program studi yang telah digunakan dalam pendaftaran harus tetap memiliki riwayat yang konsisten.
    
- Menonaktifkan program studi tidak boleh menghapus pendaftaran yang sudah terhubung dengannya.
    

**Integrasi API:**

- `GET /api/v1/admin/study-programs`
    
- `POST /api/v1/admin/study-programs`
    
- `PUT /api/v1/admin/study-programs/{id}`
    

**Catatan:** Daftar Informatika, Sistem Informasi, Teknik Industri, dan Manajemen hanya data prototipe sampai daftar resmi ITB Akiday diverifikasi.

## 5.10 WEB-09 — Manajemen Pengguna

**Tujuan:** Mengelola akun dan role pengguna sesuai kewenangan admin.

**Elemen UI:**

- Tabel pengguna.
    
- Nama atau identitas akun.
    
- Email.
    
- Role.
    
- Status akun.
    
- Kolom pencarian.
    
- Filter role.
    
- Tombol Detail.
    
- Tombol Edit atau Nonaktifkan sesuai izin.
    

**Aksi pengguna:**

- Mencari akun.
    
- Melihat detail akun.
    
- Mengubah role jika diizinkan.
    
- Menonaktifkan atau mengaktifkan akun sesuai kebijakan.
    

**Validasi dan keamanan:**

- Hanya role berwenang yang dapat mengelola pengguna.
    
- Perubahan role harus diperiksa di backend.
    
- Jangan menampilkan password atau token.
    
- Tindakan penting harus tercatat.
    
- Sistem harus mencegah kondisi yang dapat menghilangkan seluruh akses administrator secara tidak sengaja, sesuai kebijakan yang ditetapkan.
    

**Integrasi API:**

- Endpoint manajemen pengguna di bawah `/api/v1/admin/users`, sesuai kontrak final.
    

## 5.11 WEB-10 — Laporan

**Tujuan:** Menampilkan dan mengunduh ringkasan data penerimaan mahasiswa baru.

**Elemen UI:**

- Filter gelombang.
    
- Filter periode.
    
- Filter program studi.
    
- Ringkasan statistik.
    
- Tabel hasil.
    
- Tombol Unduh PDF.
    
- Indikator proses ekspor.
    

**Jenis laporan:**

- Jumlah pendaftar.
    
- Rekap pendaftaran berdasarkan status.
    
- Rekap berdasarkan gelombang.
    
- Rekap berdasarkan program studi.
    

**Aksi pengguna:**

- Mengatur filter.
    
- Melihat ringkasan.
    
- Mengunduh laporan.
    

**Aturan bisnis:**

- Data laporan harus sesuai dengan filter yang dipilih.
    
- Hak akses diterapkan pada data dan fungsi ekspor.
    
- File hasil ekspor hanya boleh memuat informasi yang dibutuhkan.
    
- Proses ekspor harus memberikan feedback selama berlangsung.
    

**Integrasi API:**

- Endpoint laporan dan ekspor PDF yang ditetapkan dalam kontrak API.
    

**Kriteria penerimaan:**

- Hasil laporan konsisten dengan sumber data.
    
- Filter yang dipilih tercermin pada hasil.
    
- Pengguna tanpa izin tidak dapat mengunduh laporan terproteksi.
    

## 5.12 WEB-11 — Profil Admin

**Tujuan:** Menampilkan informasi akun pengguna internal.

**Elemen UI:**

- Nama atau identitas akun.
    
- Email.
    
- Role.
    
- Informasi akun yang diizinkan untuk diubah.
    
- Tombol Keluar.
    

**Aksi pengguna:**

- Melihat informasi akun.
    
- Mengubah informasi yang diperbolehkan.
    
- Keluar dari sistem.
    

**Aturan bisnis:**

- Pengguna tidak dapat mengubah role sendiri melalui halaman profil biasa.
    
- Informasi sensitif tidak ditampilkan.
    
- Setelah logout, sesi harus diakhiri sesuai mekanisme autentikasi.
    

---

# 6. Alur Navigasi Utama

## 6.1 Alur Calon Mahasiswa

1. Membuka aplikasi.
    
2. Login atau membuat akun.
    
3. Melihat Beranda.
    
4. Melengkapi Profil Calon Mahasiswa.
    
5. Memilih gelombang yang tersedia.
    
6. Memilih satu program studi.
    
7. Mengisi Formulir Pendaftaran.
    
8. Mengunggah foto dan ijazah/SKL.
    
9. Memeriksa seluruh data.
    
10. Mengirim pendaftaran.
    
11. Melihat nomor dan status pendaftaran.
    
12. Memperbaiki data atau dokumen jika diminta revisi.
    
13. Memantau keputusan akhir.
    

## 6.2 Alur Panitia

1. Login ke website admin.
    
2. Membuka Dashboard.
    
3. Membuka Daftar Pendaftar.
    
4. Memilih pendaftaran yang akan diperiksa.
    
5. Memeriksa data dan dokumen.
    
6. Jika ada kekurangan, meminta revisi dengan catatan yang jelas.
    
7. Jika persyaratan telah ditangani, melanjutkan proses pemeriksaan sesuai alur.
    
8. Menetapkan keputusan akhir apabila memiliki kewenangan.
    
9. Memastikan status dan riwayat perubahan tercatat.
    

## 6.3 Alur Pimpinan

1. Login ke website admin.
    
2. Membuka Dashboard.
    
3. Melihat ringkasan pendaftaran.
    
4. Membuka Laporan.
    
5. Memilih filter gelombang, periode, atau program studi.
    
6. Mengunduh laporan jika memiliki izin.
    

Pimpinan tidak otomatis memiliki kewenangan untuk mengubah data operasional atau keputusan penerimaan. Hak akses aktual mengikuti kebijakan yang ditetapkan.

---

# 7. Matriks Hak Akses Layar

|Layar/Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Login|Ya|Ya|Ya|Ya|
|Beranda calon mahasiswa|Ya|Tidak|Tidak|Tidak|
|Profil calon mahasiswa|Profil sendiri|Sesuai izin|Sesuai izin|Tidak secara default|
|Membuat pendaftaran|Ya|Tidak|Tidak|Tidak|
|Melihat pendaftaran|Milik sendiri|Sesuai izin|Sesuai izin|Ringkasan sesuai izin|
|Memeriksa dokumen|Dokumen sendiri|Sesuai izin|Ya|Tidak secara default|
|Meminta revisi|Tidak|Sesuai izin|Ya|Tidak|
|Menetapkan keputusan|Tidak|Jika diberi izin|Jika diberi izin|Tidak secara default|
|Mengelola gelombang|Tidak|Ya|Sesuai izin|Tidak|
|Mengelola program studi|Tidak|Ya|Sesuai izin|Tidak|
|Mengelola pengguna|Tidak|Ya|Tidak secara default|Tidak|
|Melihat laporan|Status sendiri|Ya|Sesuai izin|Ya|
|Mengunduh laporan|Tidak|Ya|Sesuai izin|Ya jika diizinkan|

Matriks ini merupakan usulan awal. Hak akses final harus diselaraskan dengan Access Control Matrix dan Security Specification.

---

# 8. Standar Pesan dan Penanganan Kondisi

|Kondisi|Contoh Pesan|Tindakan UI|
|---|---|---|
|Loading|Memuat data...|Tampilkan indikator loading|
|Data kosong|Belum ada data yang tersedia.|Tampilkan penjelasan dan tindakan yang relevan|
|Validasi gagal|Periksa kembali data yang ditandai.|Tampilkan kesalahan pada kolom terkait|
|Tidak terautentikasi|Sesi Anda telah berakhir. Silakan masuk kembali.|Arahkan ke Login sesuai kondisi|
|Tidak berwenang|Anda tidak memiliki akses ke fitur ini.|Hentikan tindakan dan jangan tampilkan data terlarang|
|Data tidak ditemukan|Data yang diminta tidak ditemukan.|Tampilkan opsi kembali|
|Konflik data|Data telah berubah. Muat ulang sebelum melanjutkan.|Muat ulang data terbaru|
|Koneksi gagal|Permintaan belum berhasil diproses. Periksa koneksi dan coba kembali.|Pertahankan input yang aman|
|Berhasil|Perubahan berhasil disimpan.|Perbarui tampilan dengan respons backend|

Jangan menganggap setiap timeout berarti transaksi gagal. Untuk pengiriman pendaftaran atau keputusan, periksa status terbaru sebelum pengguna mencoba kembali.

---

# 9. Ketergantungan dengan API

Setiap layar yang menggunakan data backend harus mengikuti API Contract dan API Error Handling.

|Kebutuhan|Sumber API|
|---|---|
|Autentikasi|Auth API|
|Profil calon mahasiswa|Applicant Profile API|
|Gelombang penerimaan|Admission Waves API|
|Program studi|Study Programs API|
|Membuat dan mengirim pendaftaran|Applications API|
|Dokumen pendaftaran|Application Documents API|
|Riwayat status|Status History API|
|Pemeriksaan dan keputusan|Admin Applications API|
|Dashboard dan laporan|Dashboard and Reports API|
|Pengelolaan akun|User Administration API|

Nama endpoint pada dokumen ini merupakan rancangan. Sebelum implementasi, seluruh endpoint, metode HTTP, parameter, dan respons harus diselaraskan dengan API Contract final.

---

# 10. Kriteria Penerimaan Keseluruhan

- Setiap layar memiliki tujuan dan fungsi yang jelas.
    
- Navigasi Android sesuai kebutuhan calon mahasiswa.
    
- Navigasi website menyesuaikan role pengguna.
    
- Semua formulir memiliki label dan validasi yang jelas.
    
- Data yang ditampilkan sesuai respons backend.
    
- Semua layar yang mengambil data menangani loading, empty, dan error.
    
- Pengguna hanya dapat mengakses pendaftaran yang menjadi kewenangannya.
    
- Dokumen pendaftaran tidak dapat diakses tanpa otorisasi.
    
- Sistem mencegah pendaftaran duplikat pada gelombang yang sama.
    
- Pengiriman pendaftaran tidak dianggap berhasil sebelum dikonfirmasi backend.
    
- Keputusan akhir dilakukan oleh pihak yang berwenang.
    
- Status dan riwayat perubahan tetap konsisten.
    
- Tampilan mengikuti Design System.
    
- Antarmuka diuji pada ukuran layar yang didukung.
    

---

# 11. Hal yang Masih Perlu Diputuskan

1. Desain visual final dan logo resmi.
    
2. Ukuran minimum Android yang didukung.
    
3. Format serta ukuran maksimum dokumen.
    
4. Aturan penggantian dokumen setelah submit.
    
5. Batas waktu dan jumlah kesempatan revisi.
    
6. Apakah calon mahasiswa dapat melihat atau mengunduh bukti pendaftaran.
    
7. Kebijakan perubahan keputusan final.
    
8. Hak akses rinci setiap role admin, panitia, dan pimpinan.
    
9. Mekanisme notifikasi ketika status berubah.
    
10. Endpoint final setelah kontrak API disepakati.
    

## 12. Kesimpulan

Screen Specification menjadi acuan implementasi setiap layar PMB ITB Akiday agar fungsi, validasi, navigasi, dan hak akses berjalan secara konsisten. Dengan spesifikasi ini, pengembangan Android dan website admin dapat dilakukan berdasarkan kebutuhan yang jelas serta diuji menggunakan kriteria penerimaan yang terukur. Seluruh detail yang masih berupa usulan perlu dikonfirmasi sebelum implementasi final.

---

**Dokumen terkait:**

- [[UI UX Design]]
    
- [[Design System]]
    
- [[Module Specification]]
    
- [[API Contract]]
    
- [[API Error Handling]]
    
- [[Security Specification]]
    
- [[Access Control Matrix]]