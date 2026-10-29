## 1. Informasi Dokumen

|Item|Keterangan|
|---|---|
|Nama Sistem|PMB ITB Akiday|
|Jenis Dokumen|User Manual|
|Versi Dokumen|1.0|
|Versi Aplikasi|0.1.0-alpha (direncanakan)|
|Platform|Android|
|Pengguna Utama|Calon Mahasiswa|
|Backend|Laravel REST API|
|Database|MySQL|
|Status Dokumen|Draft|

## 2. Pendahuluan

### 2.1 Tujuan

Dokumen ini menjadi panduan bagi calon mahasiswa dalam menggunakan aplikasi PMB ITB Akiday untuk melakukan pendaftaran mahasiswa baru secara online. Panduan mencakup pembuatan akun, pengisian profil, pemilihan gelombang dan program studi, pengunggahan dokumen, pengiriman formulir, serta pemantauan status pendaftaran.

### 2.2 Gambaran Umum Sistem

PMB ITB Akiday merupakan sistem penerimaan mahasiswa baru yang dirancang untuk mengelola proses pendaftaran secara terpusat. Calon mahasiswa menggunakan aplikasi Android, sedangkan admin dan panitia menggunakan website pengelolaan.

Melalui sistem ini, calon mahasiswa dapat mengisi data pendaftaran, mengunggah dokumen persyaratan, dan melihat perkembangan proses verifikasi tanpa harus mencari informasi melalui berbagai saluran komunikasi.

### 2.3 Pengguna yang Ditujukan

Panduan ini ditujukan untuk calon mahasiswa yang akan mendaftar melalui aplikasi Android. Panduan khusus admin dan panitia dijelaskan dalam dokumen `Admin Guide.md`.

## 3. Persiapan Penggunaan

Sebelum menggunakan aplikasi, calon mahasiswa perlu menyiapkan:

- Alamat email yang dapat digunakan untuk login.
    
- Kata sandi untuk akun.
    
- Data identitas, termasuk nama lengkap dan NIK.
    
- Tempat dan tanggal lahir.
    
- Jenis kelamin, nomor telepon, dan alamat.
    
- Foto calon mahasiswa.
    
- Ijazah atau Surat Keterangan Lulus (SKL).
    
- Koneksi internet yang memadai.
    
- Informasi gelombang dan program studi yang ingin dipilih.
    

Pastikan seluruh data dan dokumen yang disiapkan benar, sesuai identitas, serta dapat dibaca dengan jelas.

## 4. Memulai Aplikasi

### 4.1 Membuka Aplikasi

1. Jalankan aplikasi PMB ITB Akiday pada perangkat Android.
    
2. Tunggu hingga halaman awal ditampilkan.
    
3. Pilih menu masuk jika sudah memiliki akun.
    
4. Jika belum memiliki akun, pilih menu pendaftaran akun apabila tersedia.
    

Nama menu dan tampilan dapat berubah mengikuti hasil implementasi antarmuka aplikasi.

### 4.2 Membuat Akun

Jika fitur pendaftaran akun tersedia, lakukan langkah berikut:

1. Buka halaman registrasi akun.
    
2. Masukkan alamat email yang valid.
    
3. Buat kata sandi sesuai ketentuan aplikasi.
    
4. Isi data tambahan yang diminta pada formulir.
    
5. Periksa kembali informasi yang dimasukkan.
    
6. Kirim formulir registrasi.
    
7. Ikuti pesan konfirmasi yang ditampilkan aplikasi.
    

Pada rancangan awal, autentikasi menggunakan email dan kata sandi. Verifikasi email belum termasuk kebutuhan MVP sehingga jangan menganggap email aktivasi pasti akan dikirim.

### 4.3 Login

1. Buka halaman login.
    
2. Masukkan alamat email yang terdaftar.
    
3. Masukkan kata sandi.
    
4. Tekan tombol **Masuk**.
    
5. Tunggu hingga aplikasi memproses permintaan.
    
6. Jika berhasil, lanjutkan ke halaman utama.
    

Jika login gagal, periksa kembali email dan kata sandi. Pastikan koneksi internet tersedia dan akun tidak dinonaktifkan.

## 5. Mengisi Profil Calon Mahasiswa

Profil berisi identitas dasar yang diperlukan untuk proses pendaftaran.

### 5.1 Data yang Diisi

|Data|Keterangan|
|---|---|
|Nama lengkap|Nama sesuai identitas|
|NIK|Nomor Induk Kependudukan, terdiri dari 16 digit|
|Tempat lahir|Tempat kelahiran|
|Tanggal lahir|Tanggal, bulan, dan tahun lahir|
|Jenis kelamin|Pilihan sesuai data yang diminta|
|Email|Alamat email akun|
|Nomor telepon|Nomor yang dapat dihubungi|
|Alamat|Alamat tempat tinggal|
|Foto|Foto calon mahasiswa sesuai persyaratan|

### 5.2 Langkah Pengisian Profil

1. Buka menu **Profil**.
    
2. Isi seluruh kolom yang diwajibkan.
    
3. Masukkan NIK dengan benar dan pastikan jumlah digitnya sesuai.
    
4. Periksa format tanggal lahir dan nomor telepon.
    
5. Unggah foto yang sesuai.
    
6. Periksa kembali semua informasi.
    
7. Tekan tombol **Simpan**.
    
8. Pastikan aplikasi menampilkan pemberitahuan bahwa data berhasil disimpan.
    

Jika terdapat kesalahan validasi, perbaiki kolom yang ditandai dan simpan kembali.

## 6. Memilih Gelombang dan Program Studi

### 6.1 Memilih Gelombang

1. Buka menu pendaftaran.
    
2. Lihat daftar gelombang yang tersedia.
    
3. Periksa tanggal mulai dan tanggal berakhir.
    
4. Pilih gelombang yang masih menerima pendaftaran.
    
5. Pastikan pilihan sesuai dengan rencana pendaftaran.
    

Gelombang yang belum dibuka atau sudah ditutup mungkin tidak dapat dipilih. Ikuti informasi resmi institusi untuk memastikan jadwal yang berlaku.

### 6.2 Memilih Program Studi

1. Buka daftar program studi pada formulir pendaftaran.
    
2. Periksa nama program studi yang tersedia.
    
3. Pilih satu program studi yang diinginkan.
    
4. Periksa kembali pilihan sebelum mengirim formulir.
    

Dalam rancangan sistem, satu pendaftaran hanya berlaku untuk satu program studi. Daftar program studi yang tampil harus mengikuti data resmi yang sudah dikonfirmasi oleh institusi.

## 7. Mengisi Formulir Pendaftaran

1. Buka menu **Pendaftaran**.
    
2. Pilih gelombang pendaftaran yang tersedia.
    
3. Pilih program studi.
    
4. Lengkapi data yang diminta pada formulir.
    
5. Periksa kelengkapan profil.
    
6. Pastikan dokumen persyaratan telah disiapkan.
    
7. Simpan sebagai draft jika fitur tersebut tersedia dan data belum siap dikirim.
    

Sebelum melanjutkan, pastikan tidak ada kesalahan penulisan nama, NIK, tanggal lahir, nomor telepon, atau informasi lainnya.

**Penting:** Satu akun hanya boleh memiliki satu pendaftaran pada gelombang yang sama. Pendaftaran pada gelombang lain dapat dilakukan apabila aturan penerimaan mengizinkannya.

## 8. Mengunggah Dokumen Persyaratan

Dokumen yang direncanakan untuk MVP meliputi:

- Foto calon mahasiswa.
    
- Ijazah atau Surat Keterangan Lulus (SKL).
    

### 8.1 Langkah Mengunggah Dokumen

1. Buka bagian dokumen pada formulir pendaftaran.
    
2. Pilih jenis dokumen yang akan diunggah.
    
3. Pilih berkas dari perangkat.
    
4. Tunggu hingga proses unggah selesai.
    
5. Periksa apakah dokumen tercatat sebagai berkas yang berhasil diunggah.
    
6. Ulangi langkah tersebut untuk dokumen wajib lainnya.
    

### 8.2 Hal yang Perlu Diperhatikan

- Gunakan dokumen yang benar dan sesuai dengan jenis dokumen yang dipilih.
    
- Pastikan teks pada dokumen dapat dibaca.
    
- Hindari berkas yang rusak atau kosong.
    
- Jangan mengunggah dokumen milik orang lain.
    
- Jangan membagikan dokumen pribadi melalui tautan publik.
    
- Ikuti batas format dan ukuran berkas yang ditetapkan aplikasi.
    

Format berkas dan batas ukuran maksimal perlu dikonfirmasi pada versi aplikasi yang sebenarnya. Jika unggahan gagal, periksa koneksi internet dan coba kembali.

## 9. Memeriksa dan Mengirim Pendaftaran

### 9.1 Pemeriksaan Sebelum Pengiriman

Sebelum mengirim formulir, pastikan:

- Profil sudah lengkap.
    
- Gelombang pendaftaran sudah benar.
    
- Program studi sudah sesuai.
    
- Seluruh kolom wajib telah diisi.
    
- Foto telah diunggah.
    
- Ijazah atau SKL telah diunggah.
    
- Semua informasi telah diperiksa kembali.
    

### 9.2 Mengirim Formulir

1. Buka ringkasan pendaftaran.
    
2. Periksa seluruh data yang ditampilkan.
    
3. Perbaiki informasi jika ditemukan kesalahan.
    
4. Tekan tombol **Kirim Pendaftaran** apabila semua persyaratan telah terpenuhi.
    
5. Tunggu hingga server memproses permintaan.
    
6. Periksa pemberitahuan keberhasilan atau pesan kesalahan.
    
7. Catat nomor registrasi jika ditampilkan.
    

Jangan menekan tombol kirim berulang kali ketika permintaan masih diproses. Periksa status pendaftaran terlebih dahulu jika koneksi terputus setelah pengiriman untuk menghindari kebingungan akibat respons yang belum diterima.

Setelah dikirim, pendaftaran akan mengikuti proses pemeriksaan sesuai alur yang ditetapkan sistem.

## 10. Memahami Status Pendaftaran

Sistem menggunakan status berikut untuk menggambarkan perkembangan pendaftaran.

|Status|Arti|Tindakan Calon Mahasiswa|
|---|---|---|
|`draft`|Data masih dalam tahap pengisian|Lengkapi data dan dokumen|
|`submitted`|Pendaftaran sudah dikirim|Tunggu proses pemeriksaan|
|`review`|Pendaftaran sedang diperiksa|Pantau informasi status|
|`revision`|Ada data atau dokumen yang perlu diperbaiki|Baca catatan revisi dan lakukan perbaikan sesuai instruksi|
|`accepted`|Pendaftaran diputuskan diterima|Baca informasi dan instruksi lanjutan|
|`rejected`|Pendaftaran diputuskan tidak diterima|Baca informasi keputusan yang tersedia|

Status `accepted` dan `rejected` ditentukan melalui keputusan panitia yang berwenang. Pemeriksaan otomatis hanya membantu memeriksa kelengkapan dan format data atau dokumen, bukan menentukan kelulusan akademik.

## 11. Memantau Status dan Riwayat

1. Login ke aplikasi.
    
2. Buka menu **Status Pendaftaran** atau menu sejenis.
    
3. Pilih pendaftaran yang ingin dilihat jika terdapat lebih dari satu gelombang.
    
4. Periksa status terkini.
    
5. Baca catatan dari panitia jika tersedia.
    
6. Buka riwayat status untuk melihat perkembangan proses.
    

Riwayat status membantu calon mahasiswa mengetahui tahapan yang telah dilalui. Ketersediaan nama menu dan rincian riwayat mengikuti implementasi aplikasi.

## 12. Memperbaiki Pendaftaran yang Direvisi

Jika status pendaftaran berubah menjadi `revision`, calon mahasiswa perlu memeriksa catatan dari panitia.

1. Login ke aplikasi.
    
2. Buka halaman status atau detail pendaftaran.
    
3. Baca alasan revisi dengan teliti.
    
4. Buka bagian data atau dokumen yang perlu diperbaiki.
    
5. Perbarui informasi atau unggah dokumen pengganti.
    
6. Periksa kembali perubahan.
    
7. Kirim ulang atau ajukan kembali untuk pemeriksaan jika fitur tersebut tersedia.
    
8. Pantau status pendaftaran setelah perbaikan.
    

Lakukan perbaikan sesuai instruksi panitia. Jika batas waktu atau jumlah revisi ditetapkan, ikuti ketentuan yang ditampilkan atau diumumkan secara resmi.

## 13. Melihat Hasil Pendaftaran

1. Login ke aplikasi.
    
2. Buka halaman status pendaftaran.
    
3. Periksa apakah status sudah menjadi `accepted` atau `rejected`.
    
4. Baca keterangan keputusan dan instruksi lanjutan yang tersedia.
    
5. Simpan atau catat informasi penting yang diperlukan.
    

Jika status belum berubah, berarti keputusan mungkin belum dicatat atau proses pemeriksaan masih berlangsung. Hubungi panitia melalui saluran resmi apabila membutuhkan klarifikasi.

## 14. Penanganan Masalah Umum

|Masalah|Kemungkinan Penyebab|Tindakan|
|---|---|---|
|Tidak bisa login|Email atau kata sandi salah|Periksa kembali kredensial|
|Data tidak tersimpan|Koneksi terputus atau validasi gagal|Periksa pesan kesalahan dan coba kembali|
|Dokumen gagal diunggah|Koneksi bermasalah, format tidak didukung, atau ukuran terlalu besar|Periksa koneksi dan ketentuan berkas|
|Pendaftaran tidak bisa dikirim|Data atau dokumen belum lengkap|Periksa bagian yang ditandai|
|Pendaftaran ditolak sistem karena duplikasi|Sudah ada pendaftaran pada gelombang yang sama|Periksa pendaftaran yang sudah dibuat|
|Gelombang tidak tersedia|Gelombang belum dibuka atau sudah ditutup|Periksa jadwal resmi|
|Status belum berubah|Pemeriksaan belum selesai atau keputusan belum dicatat|Tunggu pembaruan atau hubungi panitia|
|Aplikasi tidak dapat terhubung|Server tidak tersedia atau internet bermasalah|Periksa koneksi dan coba kembali|

Jika masalah tetap terjadi, catat waktu kejadian, pesan kesalahan, dan langkah yang dilakukan. Sampaikan informasi tersebut kepada petugas bantuan melalui saluran resmi. Hindari mengirim kata sandi atau informasi autentikasi.

## 15. Keamanan dan Privasi

Untuk menjaga keamanan akun dan data pribadi:

- Gunakan kata sandi yang kuat dan tidak mudah ditebak.
    
- Jangan membagikan kata sandi kepada siapa pun.
    
- Pastikan perangkat yang digunakan aman.
    
- Keluar dari akun jika menggunakan perangkat bersama.
    
- Jangan membagikan foto identitas atau dokumen pendaftaran kepada pihak yang tidak berwenang.
    
- Pastikan informasi yang dimasukkan benar.
    
- Laporkan aktivitas akun yang mencurigakan kepada pengelola sistem.
    

Dokumen pendaftaran merupakan informasi pribadi dan harus diakses hanya melalui mekanisme yang diizinkan sistem.

## 16. Bantuan dan Dukungan

Jika membutuhkan bantuan, calon mahasiswa disarankan untuk:

1. Membaca pesan kesalahan yang ditampilkan aplikasi.
    
2. Memeriksa kembali data dan dokumen.
    
3. Memastikan koneksi internet tersedia.
    
4. Mencoba kembali setelah memastikan masalah bukan berasal dari perangkat atau jaringan.
    
5. Menghubungi panitia melalui saluran bantuan resmi institusi apabila masalah belum terselesaikan.
    

Informasi kontak bantuan harus diisi menggunakan kontak resmi yang telah ditetapkan institusi. Dokumen ini tidak menetapkan nomor telepon atau alamat email bantuan yang belum dikonfirmasi.

## 17. Catatan Implementasi

Dokumen ini disusun berdasarkan rancangan fungsional PMB ITB Akiday. Sebelum digunakan sebagai panduan resmi, lakukan penyesuaian berdasarkan aplikasi yang benar-benar tersedia, termasuk:

- Nama dan posisi menu.
    
- Tampilan setiap halaman.
    
- Alur registrasi dan login.
    
- Format serta ukuran dokumen yang didukung.
    
- Mekanisme penyimpanan draft dan pengiriman ulang revisi.
    
- Tampilan nomor registrasi dan hasil keputusan.
    
- Informasi kontak bantuan.
    
- Aturan resmi gelombang dan program studi.
    

## 18. Riwayat Perubahan Dokumen

|Versi|Perubahan|Status|
|---|---|---|
|1.0|Penyusunan awal panduan pengguna aplikasi Android PMB ITB Akiday|Draft|
