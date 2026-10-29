## # Admin Guide

## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Dokumen|Admin Guide|
|Nama Proyek|PMB ITB Akiday|
|Versi Dokumen|1.0|
|Status Dokumen|Draft|
|Jenis Dokumen|Panduan Administrator|
|Pengguna|Admin, Panitia, dan Pimpinan sesuai hak akses|
|Platform|Website Admin|
|Backend|Laravel|
|Database|MySQL|
|Referensi|[[User Manual]], [[Access Control Matrix]], [[Security Specification]], [[Module Specification]]|

> **Catatan:** Panduan ini menjelaskan alur penggunaan yang direncanakan. Nama menu dan tampilan aktual harus disesuaikan dengan website admin yang sudah diimplementasikan.

---

## 2. Tujuan

Admin Guide dibuat untuk membantu pengelola sistem memahami cara menggunakan website administrasi PMB ITB Akiday. Dokumen ini mencakup proses login, pengelolaan pengguna, pengaturan gelombang penerimaan, pengelolaan program studi, pemeriksaan pendaftaran, penanganan revisi, penetapan keputusan, serta pengelolaan laporan.

Panduan ini juga menjelaskan batasan hak akses setiap peran agar data calon mahasiswa tetap terlindungi dan tindakan administratif dapat dilakukan oleh pengguna yang berwenang.

## 3. Gambaran Umum Sistem

PMB ITB Akiday merupakan sistem penerimaan mahasiswa baru yang mengintegrasikan pendaftaran calon mahasiswa melalui aplikasi Android dengan pengelolaan administrasi melalui website.

Website admin digunakan untuk mendukung proses berikut:

1. Mengelola akun dan hak akses pengguna.
    
2. Mengatur gelombang penerimaan.
    
3. Mengelola daftar program studi.
    
4. Melihat dan memeriksa pendaftaran.
    
5. Memeriksa kelengkapan dokumen.
    
6. Meminta revisi jika terdapat data atau dokumen yang perlu diperbaiki.
    
7. Mencatat keputusan penerimaan.
    
8. Memantau riwayat perubahan status.
    
9. Melihat statistik dan mengunduh laporan.
    

Pemeriksaan kelengkapan otomatis hanya digunakan untuk memvalidasi data, format, dan dokumen yang diwajibkan. Keputusan akademik diterima atau ditolak tetap dilakukan oleh panitia atau petugas yang memiliki kewenangan.

## 4. Peran Pengguna dan Hak Akses

Sistem memiliki empat peran utama.

|Peran|Tanggung Jawab|
|---|---|
|Calon Mahasiswa|Mengisi pendaftaran, mengunggah dokumen, memperbaiki data, dan memantau status pendaftaran melalui aplikasi Android.|
|Admin|Mengelola konfigurasi operasional, akun pengguna, gelombang penerimaan, program studi, serta administrasi sistem sesuai kewenangan.|
|Panitia|Memeriksa data dan dokumen pendaftaran, meminta revisi, serta memproses keputusan jika memiliki kewenangan tersebut.|
|Pimpinan|Memantau statistik dan laporan penerimaan sesuai hak akses yang diberikan.|

Hak akses harus mengikuti [[Access Control Matrix]]. Tidak semua pengguna admin memiliki kewenangan yang sama, sehingga hak untuk mengubah data, mengakses dokumen, atau menetapkan keputusan harus dibatasi berdasarkan peran dan penugasan.

## 5. Persiapan Sebelum Menggunakan Website

Sebelum masuk ke website admin, pastikan:

- Memiliki akun yang telah dibuat atau diaktifkan oleh pihak berwenang.
    
- Menggunakan alamat website yang benar.
    
- Menggunakan browser yang didukung.
    
- Memiliki koneksi ke server PMB ITB Akiday.
    
- Mengetahui tugas dan batas kewenangan akun.
    
- Tidak menggunakan akun milik pengguna lain.
    

Untuk lingkungan lokal, alamat website mengikuti konfigurasi deployment proyek. Pada tahap pengembangan, website mungkin dijalankan melalui server lokal. Alamat tersebut bukan alamat produksi dan tidak boleh diasumsikan dapat diakses dari internet.

## 6. Login ke Website Admin

### 6.1 Langkah Login

1. Buka website admin PMB ITB Akiday.
    
2. Masukkan email yang terdaftar.
    
3. Masukkan kata sandi.
    
4. Klik tombol **Login**.
    
5. Tunggu sistem memeriksa kredensial.
    
6. Jika berhasil, sistem menampilkan halaman awal sesuai hak akses akun.
    

### 6.2 Jika Login Gagal

Jika login gagal, lakukan langkah berikut:

1. Pastikan email dan kata sandi benar.
    
2. Periksa apakah akun sudah diaktifkan.
    
3. Pastikan server dapat diakses.
    
4. Jika masih gagal, hubungi administrator yang berwenang.
    

Sistem seharusnya menampilkan pesan kesalahan yang aman dan tidak membocorkan apakah informasi akun tertentu tersedia secara internal.

Jangan membagikan kata sandi atau token autentikasi kepada pihak lain.

## 7. Dashboard Admin

Dashboard menampilkan ringkasan informasi penerimaan yang relevan dengan hak akses pengguna.

Informasi yang direncanakan meliputi:

- Jumlah pendaftar.
    
- Jumlah pendaftaran yang telah dikirim.
    
- Jumlah pendaftaran yang sedang diperiksa.
    
- Jumlah pendaftaran yang memerlukan revisi.
    
- Jumlah pendaftar yang diterima.
    
- Jumlah pendaftar yang ditolak.
    
- Statistik berdasarkan gelombang penerimaan.
    
- Statistik berdasarkan program studi.
    

Angka pada dashboard harus berasal dari data yang tersimpan di database. Admin perlu memperhatikan filter gelombang, periode, dan program studi agar tidak salah menafsirkan hasil.

Pimpinan hanya dapat melihat informasi sesuai kewenangannya. Akses ke dokumen pribadi atau data rinci calon mahasiswa tidak diberikan secara otomatis hanya karena pengguna memiliki akses ke dashboard.

## 8. Mengelola Pengguna

Fitur pengelolaan pengguna digunakan oleh admin yang memiliki kewenangan untuk mengatur akun.

### 8.1 Melihat Daftar Pengguna

1. Buka menu pengelolaan pengguna.
    
2. Cari pengguna berdasarkan informasi yang tersedia.
    
3. Periksa identitas akun dan role.
    
4. Buka detail pengguna jika diperlukan dan diizinkan.
    

### 8.2 Membuat atau Mengubah Akun

Jika fitur tersedia dan admin berwenang:

1. Buka halaman pengelolaan pengguna.
    
2. Pilih tindakan tambah atau ubah akun.
    
3. Isi informasi yang diwajibkan.
    
4. Tentukan role sesuai tanggung jawab pengguna.
    
5. Periksa kembali data yang dimasukkan.
    
6. Simpan perubahan.
    
7. Pastikan sistem memberikan konfirmasi.
    

Jangan memberikan role admin kepada pengguna yang hanya membutuhkan akses panitia atau pimpinan. Hak akses yang terlalu luas dapat menyebabkan penyalahgunaan atau kebocoran data.

### 8.3 Menonaktifkan Akun

Jika akun tidak lagi boleh digunakan dan fitur tersebut tersedia:

1. Periksa identitas pengguna.
    
2. Pastikan penonaktifan memang diperlukan.
    
3. Lakukan penonaktifan sesuai prosedur.
    
4. Pastikan akun tidak dapat melakukan tindakan yang dilarang setelah dinonaktifkan.
    

Perubahan status akun tidak boleh menghapus riwayat pendaftaran atau catatan audit yang masih diperlukan.

## 9. Mengelola Gelombang Penerimaan

Gelombang penerimaan digunakan untuk menentukan periode pendaftaran yang tersedia.

### 9.1 Membuat Gelombang

1. Buka menu gelombang penerimaan.
    
2. Pilih tambah gelombang.
    
3. Masukkan nama gelombang.
    
4. Tentukan tanggal mulai dan tanggal berakhir.
    
5. Atur status aktif sesuai ketentuan operasional.
    
6. Periksa kembali rentang tanggal.
    
7. Simpan perubahan.
    

### 9.2 Memperbarui Gelombang

Sebelum mengubah tanggal atau status gelombang, admin harus memastikan dampaknya terhadap calon mahasiswa yang sedang mendaftar.

Perubahan jadwal perlu dikomunikasikan melalui saluran resmi yang ditetapkan pengelola.

### 9.3 Menonaktifkan Gelombang

Sebelum menonaktifkan gelombang, periksa apakah masih ada proses pendaftaran yang harus diselesaikan. Menonaktifkan gelombang tidak boleh secara otomatis menghapus data pendaftaran yang sudah tersimpan.

Aturan mengenai pengajuan setelah batas waktu, perpanjangan, dan perubahan jadwal harus mengikuti kebijakan resmi penerimaan.

## 10. Mengelola Program Studi

Menu program studi digunakan untuk mengatur pilihan program yang tersedia pada formulir pendaftaran.

### 10.1 Menambahkan Program Studi

1. Buka menu program studi.
    
2. Pilih tambah program studi.
    
3. Isi kode dan nama program studi.
    
4. Periksa apakah kode sudah digunakan.
    
5. Tentukan status aktif.
    
6. Simpan perubahan.
    

### 10.2 Memperbarui Program Studi

Pastikan perubahan nama atau kode tidak menyebabkan ketidaksesuaian pada laporan dan data pendaftaran sebelumnya.

Jika program studi tidak lagi menerima pendaftar, pertimbangkan untuk menonaktifkannya daripada menghapus data yang sudah memiliki hubungan dengan pendaftaran.

**Penting:** Daftar Informatika, Sistem Informasi, Teknik Industri, dan Manajemen yang digunakan pada prototipe merupakan data contoh. Admin harus memastikan daftar program studi sesuai dengan informasi resmi sebelum sistem digunakan secara nyata.

## 11. Memeriksa Pendaftaran

Pemeriksaan pendaftaran merupakan salah satu proses utama website admin.

### 11.1 Membuka Daftar Pendaftaran

1. Masuk menggunakan akun yang memiliki hak akses pemeriksaan.
    
2. Buka menu pendaftaran.
    
3. Gunakan filter gelombang, status, atau program studi jika tersedia.
    
4. Pilih pendaftaran yang perlu diperiksa.
    
5. Buka detail pendaftaran.
    

### 11.2 Memeriksa Data Calon Mahasiswa

Periksa informasi yang relevan, antara lain:

- Nama lengkap.
    
- NIK.
    
- Tempat dan tanggal lahir.
    
- Jenis kelamin.
    
- Email dan nomor telepon.
    
- Alamat.
    
- Program studi pilihan.
    
- Gelombang penerimaan.
    

Pastikan pemeriksaan dilakukan sesuai prosedur dan hanya untuk tujuan administrasi penerimaan.

### 11.3 Memeriksa Dokumen

Periksa dokumen yang diwajibkan, seperti:

- Foto calon mahasiswa.
    
- Ijazah atau Surat Keterangan Lulus (SKL).
    

Pastikan dokumen dapat dibuka, terbaca, dan sesuai dengan ketentuan penerimaan. Jika terdapat ketidaksesuaian, catat alasan yang jelas untuk ditindaklanjuti.

Pemeriksaan format dan kelengkapan oleh sistem tidak menggantikan penilaian manusia terhadap kesesuaian dokumen.

## 12. Meminta Revisi Pendaftaran

Jika terdapat data atau dokumen yang harus diperbaiki, panitia dapat meminta revisi apabila memiliki kewenangan tersebut.

Langkah-langkah:

1. Buka detail pendaftaran.
    
2. Periksa bagian yang perlu diperbaiki.
    
3. Pilih tindakan permintaan revisi.
    
4. Tuliskan alasan atau instruksi yang jelas.
    
5. Simpan permintaan revisi.
    
6. Pastikan status berubah menjadi `revision`.
    
7. Pastikan catatan perubahan tersimpan pada riwayat status.
    

Instruksi revisi harus spesifik. Misalnya, panitia perlu menyebutkan dokumen yang tidak terbaca atau data yang harus diperbaiki, bukan hanya menuliskan “perbaiki data”.

Calon mahasiswa kemudian memperbaiki informasi melalui aplikasi Android sesuai fitur yang tersedia. Setelah perbaikan dikirim, panitia memeriksa kembali perubahan tersebut.

Batas waktu dan jumlah revisi harus mengikuti kebijakan yang ditetapkan pengelola penerimaan.

## 13. Menetapkan Keputusan Penerimaan

Keputusan akhir harus ditetapkan oleh panitia atau petugas yang diberi kewenangan secara eksplisit.

### 13.1 Prasyarat Keputusan

Sebelum mengambil keputusan:

1. Pastikan identitas pendaftaran yang dibuka benar.
    
2. Pastikan data dan dokumen telah diperiksa.
    
3. Pastikan proses revisi, jika ada, sudah diselesaikan.
    
4. Pastikan pengguna memiliki kewenangan mengambil keputusan.
    
5. Pastikan status pendaftaran mengizinkan perubahan tersebut.
    
6. Periksa kembali keputusan sebelum disimpan.
    

### 13.2 Menyimpan Keputusan

Jika fitur tersedia:

1. Buka detail pendaftaran.
    
2. Pilih tindakan keputusan.
    
3. Tentukan `accepted` atau `rejected` berdasarkan hasil pemeriksaan dan ketentuan resmi.
    
4. Masukkan catatan jika diwajibkan.
    
5. Periksa kembali keputusan.
    
6. Konfirmasikan penyimpanan.
    
7. Pastikan status dan riwayat perubahan diperbarui.
    

Keputusan tidak boleh didasarkan hanya pada validasi otomatis. Sistem membantu proses administrasi, tetapi tidak menggantikan kewenangan manusia dalam penilaian penerimaan.

Setelah keputusan final disimpan, perubahan berikutnya harus mengikuti prosedur koreksi atau persetujuan yang ditetapkan institusi. Jangan mengubah keputusan secara informal atau menghapus riwayat keputusan sebelumnya.

## 14. Memahami Status Pendaftaran

Sistem menggunakan status berikut:

|Status|Arti|
|---|---|
|`draft`|Pendaftaran masih dalam tahap pengisian dan belum dikirim.|
|`submitted`|Pendaftaran telah dikirim oleh calon mahasiswa.|
|`review`|Pendaftaran sedang diperiksa.|
|`revision`|Calon mahasiswa perlu memperbaiki data atau dokumen.|
|`accepted`|Calon mahasiswa dinyatakan diterima oleh pihak yang berwenang.|
|`rejected`|Calon mahasiswa dinyatakan tidak diterima oleh pihak yang berwenang.|

Setiap perubahan status penting harus dicatat pada riwayat status. Riwayat tersebut membantu pengelola menelusuri siapa yang melakukan perubahan, kapan perubahan terjadi, dan alasan perubahan jika tersedia.

Status hanya boleh berubah melalui alur yang diizinkan. Pengguna tidak boleh memaksakan perubahan status dengan mengakses endpoint secara langsung.

## 15. Melihat Riwayat Status

Riwayat status digunakan untuk memantau perjalanan suatu pendaftaran dari awal hingga keputusan akhir.

Informasi yang perlu tersedia meliputi:

- Status sebelumnya.
    
- Status baru.
    
- Waktu perubahan.
    
- Pengguna yang melakukan perubahan.
    
- Catatan atau alasan perubahan jika ada.
    

Untuk memeriksa riwayat:

1. Buka detail pendaftaran.
    
2. Pilih bagian riwayat status.
    
3. Periksa urutan perubahan.
    
4. Cocokkan riwayat dengan tindakan yang tercatat.
    
5. Laporkan ketidaksesuaian kepada pengelola teknis.
    

Riwayat status tidak boleh diubah untuk menyembunyikan kesalahan atau menghilangkan jejak keputusan yang sudah dilakukan.

## 16. Melihat Dashboard dan Membuat Laporan

Fitur laporan digunakan untuk membantu pemantauan proses penerimaan.

### 16.1 Melihat Statistik

1. Buka dashboard atau menu laporan.
    
2. Pilih gelombang penerimaan yang ingin diperiksa.
    
3. Gunakan filter program studi jika tersedia.
    
4. Periksa jumlah pendaftaran dan distribusi status.
    
5. Pastikan periode laporan sesuai dengan kebutuhan.
    

### 16.2 Mengunduh Laporan

Jika fitur ekspor sudah tersedia:

1. Buka menu laporan.
    
2. Tentukan filter.
    
3. Periksa hasil yang ditampilkan.
    
4. Pilih opsi unduh PDF atau format lain yang disediakan.
    
5. Simpan file pada lokasi yang aman.
    
6. Periksa apakah file berisi data sesuai filter.
    

Format laporan yang direncanakan mencakup statistik dashboard dan laporan PDF berdasarkan program studi.

### 16.3 Perlindungan Data Laporan

Laporan yang memuat data pribadi harus diperlakukan sebagai informasi terbatas.

- Bagikan laporan hanya kepada pihak berwenang.
    
- Hindari mengirim file melalui saluran yang tidak aman.
    
- Gunakan data agregat jika data individual tidak diperlukan.
    
- Jangan meninggalkan file laporan pada komputer bersama.
    
- Hapus salinan sementara sesuai kebijakan penyimpanan data.
    

## 17. Pengelolaan Kesalahan

Jika website mengalami masalah, lakukan langkah berikut:

1. Catat waktu kejadian dan fitur yang bermasalah.
    
2. Catat pesan error tanpa menyebarkan informasi rahasia.
    
3. Periksa koneksi internet atau jaringan lokal.
    
4. Coba kembali setelah memastikan layanan tersedia.
    
5. Jangan mengulangi tindakan yang berpotensi membuat data ganda sebelum memeriksa status permintaan sebelumnya.
    
6. Jika masalah berlanjut, laporkan kepada pengelola teknis.
    

Jika data terlihat tidak sesuai, jangan langsung menghapus atau mengubah data tersebut. Periksa riwayat status dan laporkan masalah agar penanganannya dapat dilakukan secara terkontrol.

## 18. Praktik Keamanan Administrator

Setiap administrator dan panitia wajib menjaga keamanan akun dan data penerimaan.

1. Gunakan kata sandi yang kuat dan unik.
    
2. Jangan membagikan kredensial kepada pengguna lain.
    
3. Keluar dari akun setelah menggunakan perangkat bersama.
    
4. Jangan membuka data calon mahasiswa di luar kebutuhan pekerjaan.
    
5. Jangan mengunduh atau menyalin dokumen tanpa alasan yang sah.
    
6. Periksa kembali identitas pendaftaran sebelum mengambil tindakan.
    
7. Gunakan hak akses sesuai tugas.
    
8. Laporkan dugaan akses tidak sah atau kebocoran data.
    
9. Jangan mengubah database secara langsung tanpa prosedur yang disetujui.
    
10. Ikuti kebijakan backup dan perlindungan data yang berlaku.
    

Jika sistem mendukung pencatatan audit, tindakan penting seperti perubahan role, perubahan konfigurasi gelombang, permintaan revisi, dan keputusan penerimaan harus dapat ditelusuri.

## 19. Checklist Operasional Admin

### Pemeriksaan Awal

- Login menggunakan akun sendiri.
    
- Memastikan gelombang yang dikelola benar.
    
- Memeriksa daftar pendaftaran yang perlu ditindaklanjuti.
    
- Memastikan data yang dibuka sesuai dengan tugas.
    

### Pemeriksaan Pendaftaran

- Data calon mahasiswa diperiksa.
    
- Dokumen wajib diperiksa.
    
- Kekurangan data dicatat secara jelas.
    
- Permintaan revisi disimpan jika diperlukan.
    
- Keputusan dibuat hanya oleh pihak yang berwenang.
    
- Riwayat status diperiksa setelah perubahan.
    

### Pelaporan

- Filter gelombang dan program studi benar.
    
- Statistik sesuai dengan data yang tersedia.
    
- File laporan disimpan secara aman.
    
- Data pribadi tidak dibagikan tanpa kewenangan.
    

### Keamanan

- Akun tidak digunakan bersama.
    
- Dokumen hanya diakses sesuai kebutuhan.
    
- Kesalahan sistem dilaporkan.
    
- Perubahan penting dapat ditelusuri.
    

## 20. Batasan dan Hal yang Perlu Dikonfirmasi

Panduan ini masih perlu disesuaikan dengan implementasi aktual, terutama untuk:

- Nama dan susunan menu website admin.
    
- Prosedur pembuatan akun admin dan panitia.
    
- Pembagian kewenangan keputusan antara admin dan panitia.
    
- Kebijakan revisi dan batas waktunya.
    
- Kebijakan koreksi keputusan final.
    
- Format dan isi laporan PDF.
    
- Ketentuan resmi program studi dan gelombang penerimaan.
    
- Kebijakan retensi data dan dokumen.
    
- Prosedur audit dan penanganan insiden.
    

Fitur yang belum diimplementasikan tidak boleh dianggap tersedia hanya karena sudah dijelaskan dalam dokumen ini.

## 21. Kesimpulan

Admin Guide menjadi pedoman penggunaan website administrasi PMB ITB Akiday bagi admin, panitia, dan pimpinan sesuai hak akses masing-masing. Dengan mengikuti panduan ini, pengelola diharapkan dapat menjalankan proses administrasi secara konsisten, mulai dari pengelolaan gelombang dan program studi hingga pemeriksaan dokumen, perubahan status, keputusan penerimaan, dan pembuatan laporan.

Penggunaan sistem harus selalu memperhatikan keamanan akun, kerahasiaan dokumen, konsistensi data, serta kewenangan setiap peran. Panduan ini perlu diperbarui setelah website selesai diimplementasikan agar langkah-langkahnya sesuai dengan menu dan perilaku sistem yang sebenarnya.

---

## 22. Dokumen Terkait

- [[User Manual]]
    
- [[Release Notes]]
    
- [[Project Overview]]
    
- [[PRD Final v1.0]]
    
- [[Module Specification]]
    
- [[Access Control Matrix]]
    
- [[Security Specification]]
    
- [[API Contract]]
    
- [[Test Plan]]
    
- [[Test Results]]
    
- [[Deployment Guide]]
    
- [[Backup & Recovery]]