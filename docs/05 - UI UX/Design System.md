# Design System — PMB ITB Akiday

**Nama Proyek:** Sistem Informasi Penerimaan Mahasiswa Baru ITB Akiday  
**Versi Dokumen:** 1.0  
**Status:** Draft untuk Implementasi  
**Platform:** Android (Kotlin) dan Admin Website  
**Tema Visual:** Minimalis, modern, profesional  
**Warna Utama:** Teal dan Orange

---

## 1. Tujuan Design System

Design System merupakan pedoman yang digunakan untuk menjaga konsistensi tampilan dan interaksi pada seluruh bagian aplikasi PMB ITB Akiday. Pedoman ini mencakup warna, tipografi, ukuran komponen, tombol, formulir, notifikasi, navigasi, serta aturan penggunaan elemen visual.

Dengan adanya Design System, aplikasi Android untuk calon mahasiswa dan website admin dapat memiliki identitas visual yang sama meskipun digunakan pada platform yang berbeda. Selain itu, pedoman ini membantu proses pengembangan, mengurangi perbedaan tampilan ant halaman, dan memudahkan pemeliharaan antarmuka.

## 2. Prinsip Desain

### 2.1 Minimalis

Antarmuka hanya menampilkan informasi dan fitur yang dibutuhkan pengguna. Elemen dekoratif yang tidak memiliki fungsi sebaiknya dihindari.

### 2.2 Mudah Dipahami

Bahasa pada tombol, formulir, status pendaftaran, dan pesan kesalahan harus menggunakan istilah yang jelas serta mudah dipahami oleh calon mahasiswa maupun petugas.

### 2.3 Konsisten

Warna, ukuran teks, bentuk tombol, jarak antarelemen, dan pola navigasi harus digunakan secara konsisten.

### 2.4 Responsif

Website admin harus dapat menyesuaikan tampilan dengan ukuran layar yang berbeda. Aplikasi Android harus tetap nyaman digunakan pada berbagai ukuran perangkat yang didukung.

### 2.5 Aksesibel

Teks harus mudah dibaca, kontras warna memadai, dan status pendaftaran tidak boleh dibedakan berdasarkan warna saja. Setiap status perlu disertai label teks yang jelas.

---

## 3. Identitas Visual

Identitas visual PMB ITB Akiday menggunakan perpaduan warna teal dan orange. Teal berfungsi sebagai warna utama yang memberikan kesan profesional, sedangkan orange digunakan sebagai warna aksen untuk menonjolkan tindakan atau informasi tertentu.

### 3.1 Palet Warna

|Token|Kode Warna|Penggunaan|
|---|---|---|
|Primary|`#0F766E`|Tombol utama, tautan aktif, elemen identitas|
|Primary Dark|`#115E59`|Hover website dan penekanan elemen utama|
|Primary Light|`#CCFBF1`|Latar elemen teal yang lembut|
|Accent|`#F97316`|Aksen, penanda perhatian, elemen promosi|
|Accent Light|`#FFEDD5`|Latar aksen orange|
|Background|`#F8FAFC`|Latar utama halaman|
|Surface|`#FFFFFF`|Kartu, modal, dan formulir|
|Text Primary|`#0F172A`|Judul dan teks utama|
|Text Secondary|`#475569`|Deskripsi dan teks pendukung|
|Border|`#E2E8F0`|Garis batas komponen|
|Success|`#15803D`|Pesan berhasil|
|Warning|`#B45309`|Peringatan|
|Danger|`#B91C1C`|Kesalahan dan tindakan berisiko|
|Info|`#0369A1`|Informasi umum|

**Aturan penggunaan warna:**

- Gunakan `Primary` untuk tombol utama seperti **Masuk**, **Simpan**, dan **Kirim Pendaftaran**.
    
- Gunakan `Accent` secara terbatas agar elemen penting terlihat menonjol.
    
- Gunakan `Danger` untuk pesan kesalahan atau tindakan berisiko, bukan sebagai dekorasi.
    
- Pastikan teks di atas warna latar memiliki kontras yang memadai. Teks putih pada warna orange tidak boleh digunakan secara otomatis tanpa pemeriksaan kontras.
    
- Status pendaftaran harus memiliki teks, bukan hanya indikator warna.
    

Kode warna di atas merupakan usulan awal dan perlu divalidasi melalui pengujian aksesibilitas.

## 4. Tipografi

Gunakan **Inter** sebagai font utama jika tersedia. Pada Android, gunakan font yang dibundel dalam aplikasi atau fallback sistem yang sesuai. Pada website, gunakan font dengan fallback sans-serif.

### 4.1 Skala Tipografi

|Token|Ukuran|Ketebalan|Penggunaan|
|---|---|---|---|
|Display|32 px|Bold|Judul utama halaman pembuka|
|Heading 1|28 px|Bold|Judul halaman penting|
|Heading 2|24 px|Semi-bold|Judul bagian|
|Heading 3|20 px|Semi-bold|Judul kartu atau kelompok|
|Body Large|18 px|Regular|Informasi penting|
|Body|16 px|Regular|Isi utama dan formulir|
|Body Small|14 px|Regular|Keterangan tambahan|
|Caption|12 px|Regular|Metadata atau informasi singkat|
|Button|16 px|Semi-bold|Teks tombol|

Ukuran di atas menggunakan satuan desain berbasis piksel. Pada implementasi Android, gunakan `sp` untuk ukuran teks agar dapat mengikuti pengaturan ukuran font perangkat.

**Aturan tipografi:**

1. Gunakan ukuran 16 px atau setara sebagai ukuran standar teks isi.
    
2. Hindari paragraf panjang dengan ukuran teks terlalu kecil.
    
3. Gunakan ketebalan font untuk membedakan hierarki informasi.
    
4. Jangan menggunakan terlalu banyak variasi ukuran font pada satu halaman.
    
5. Label formulir harus tetap terbaca saat pengguna memperbesar ukuran teks.
    

## 5. Sistem Spacing

Sistem spacing menggunakan kelipatan 4 agar jarak antarelemen lebih konsisten.

|Token|Nilai|Penggunaan|
|---|---|---|
|`space-1`|4 px|Jarak sangat kecil|
|`space-2`|8 px|Jarak label dan teks pendukung|
|`space-3`|12 px|Jarak elemen dalam kelompok|
|`space-4`|16 px|Padding standar|
|`space-5`|20 px|Jarak antarkomponen|
|`space-6`|24 px|Jarak antarkelompok|
|`space-8`|32 px|Jarak antarbagiannya|
|`space-12`|48 px|Jarak bagian utama|

**Penerapan:**

- Padding isi kartu: 16–24 px.
    
- Jarak label dengan input: 8 px.
    
- Jarak antara dua input: 16 px.
    
- Jarak antara bagian formulir: 24–32 px.
    
- Padding halaman: 16 px pada layar sempit dan dapat ditingkatkan pada layar yang lebih lebar.
    

Nilai tersebut merupakan acuan awal dan dapat disesuaikan berdasarkan pengujian pada perangkat sebenarnya.

## 6. Border Radius dan Elevation

### 6.1 Border Radius

|Token|Nilai|Penggunaan|
|---|---|---|
|`radius-sm`|4 px|Label kecil dan elemen sederhana|
|`radius-md`|8 px|Input dan tombol|
|`radius-lg`|12 px|Kartu informasi|
|`radius-xl`|16 px|Panel utama atau modal besar|
|`radius-full`|999 px|Badge berbentuk pil atau avatar bulat|

Gunakan radius secara konsisten. Tidak semua elemen perlu dibuat sangat membulat.

### 6.2 Elevation dan Shadow

- Kartu standar menggunakan border tipis dan bayangan ringan bila diperlukan.
    
- Modal harus terlihat berada di atas konten utama.
    
- Input tidak perlu memiliki bayangan.
    
- Website admin sebaiknya mengutamakan pembatas dan jarak yang jelas daripada bayangan berlebihan.
    

---

## 7. Komponen Antarmuka

### 7.1 Button

Tombol harus menggunakan label yang menjelaskan tindakan yang akan dilakukan.

|Jenis|Tampilan|Contoh|
|---|---|---|
|Primary|Latar teal, teks kontras|Masuk, Kirim Pendaftaran|
|Secondary|Latar netral atau putih dengan border|Batal, Kembali|
|Tertiary|Tanpa latar menonjol|Lihat Detail|
|Danger|Latar atau teks merah|Nonaktifkan Akun|
|Disabled|Kontras visual lebih rendah|Tombol saat formulir belum valid|

**Aturan tombol:**

- Tombol utama harus paling menonjol pada satu kelompok tindakan.
    
- Tombol yang sedang memproses permintaan harus menunjukkan status loading.
    
- Hindari mengirim permintaan berulang ketika proses sebelumnya masih berjalan.
    
- Tombol berbahaya harus disertai konfirmasi apabila tindakannya berdampak besar.
    
- Gunakan label yang spesifik, seperti **Simpan Perubahan**, bukan hanya **OK**, jika konteksnya membutuhkan kejelasan.
    

### 7.2 Text Input

Komponen input digunakan untuk nama lengkap, NIK, tempat lahir, nomor telepon, email, dan alamat.

Setiap input harus memiliki:

- Label yang selalu terlihat.
    
- Placeholder sebagai contoh format, bukan pengganti label.
    
- Pesan kesalahan di dekat input terkait.
    
- Indikator wajib diisi bila diperlukan.
    
- Nilai yang sudah dimasukkan tetap tersedia ketika validasi gagal, selama aman untuk dipertahankan.
    

Contoh:

- Label: `NIK`
    
- Placeholder: `Masukkan 16 digit NIK`
    
- Pesan kesalahan: `NIK harus terdiri dari 16 digit.`
    

Validasi di antarmuka membantu pengguna, tetapi backend Laravel tetap menjadi pihak yang menentukan apakah data valid.

### 7.3 Dropdown dan Selection

Dropdown digunakan untuk pilihan seperti jenis kelamin, gelombang penerimaan, dan program studi apabila pilihan tersebut tersedia.

Ketentuan:

- Tampilkan pilihan yang aktif saja untuk pendaftaran baru.
    
- Jangan menampilkan nilai yang belum dikonfirmasi sebagai program studi resmi.
    
- Berikan informasi jika tidak ada pilihan yang tersedia.
    
- Backend harus memvalidasi kembali pilihan yang dikirim oleh aplikasi.
    

### 7.4 File Upload

Komponen upload digunakan untuk foto dan dokumen ijazah atau Surat Keterangan Lulus (SKL).

Elemen yang ditampilkan:

- Nama jenis dokumen.
    
- Status kelengkapan.
    
- Tombol pilih atau unggah berkas.
    
- Nama berkas yang dipilih.
    
- Informasi format dan ukuran maksimum setelah kebijakan ditetapkan.
    
- Tombol untuk mengganti berkas jika penggantian diizinkan.
    

Contoh status:

- `Belum diunggah`
    
- `Berkas dipilih`
    
- `Berhasil diunggah`
    
- `Unggahan gagal`
    

Jangan menampilkan status berhasil sebelum server mengonfirmasi bahwa berkas sudah tersimpan. Dokumen harus disimpan di penyimpanan privat dan tidak boleh menggunakan tautan publik tanpa kontrol akses.

### 7.5 Card

Kartu digunakan untuk menampilkan ringkasan pendaftaran, status, jadwal gelombang, dan informasi yang berkaitan.

Aturan:

- Gunakan latar putih di atas latar halaman yang netral.
    
- Tampilkan judul, informasi inti, lalu tindakan yang relevan.
    
- Hindari memasukkan terlalu banyak informasi dalam satu kartu.
    
- Pada website admin, gunakan susunan kartu yang mendukung pemindaian informasi dengan cepat.
    

### 7.6 Badge Status

Badge menunjukkan status pendaftaran.

|Status|Label Tampilan|Warna yang Disarankan|
|---|---|---|
|`draft`|Draft|Netral|
|`submitted`|Menunggu Pemeriksaan|Biru|
|`review`|Sedang Ditinjau|Teal|
|`revision`|Perlu Revisi|Orange|
|`accepted`|Diterima|Hijau|
|`rejected`|Tidak Diterima|Merah|

Label di atas adalah teks yang ditampilkan pada antarmuka, sedangkan kode di kolom pertama digunakan oleh sistem.

Warna badge merupakan penanda tambahan. Pengguna tetap harus dapat memahami status tanpa bergantung pada warna saja.

### 7.7 Alert dan Feedback

Gunakan komponen feedback sesuai hasil tindakan.

- **Success:** Data berhasil disimpan.
    
- **Info:** Informasi atau petunjuk tambahan.
    
- **Warning:** Ada hal yang perlu diperhatikan sebelum melanjutkan.
    
- **Error:** Permintaan gagal atau terdapat kesalahan yang harus diperbaiki.
    

Contoh pesan:

- Berhasil: `Data pendaftaran berhasil disimpan.`
    
- Validasi: `Lengkapi data yang masih wajib diisi.`
    
- Koneksi: `Permintaan belum berhasil diproses. Periksa koneksi dan coba kembali.`
    
- Konflik: `Anda sudah memiliki pendaftaran pada gelombang ini.`
    

Pesan kesalahan harus membantu pengguna menentukan tindakan berikutnya tanpa menampilkan stack trace, query database, atau informasi teknis internal.

### 7.8 Modal dan Confirmation Dialog

Modal digunakan untuk tindakan yang membutuhkan perhatian khusus, misalnya konfirmasi keputusan penerimaan atau penonaktifan akun.

Isi modal meliputi:

- Judul yang jelas.
    
- Ringkasan tindakan.
    
- Dampak tindakan bila relevan.
    
- Tombol utama untuk melanjutkan.
    
- Tombol sekunder untuk membatalkan.
    

Jangan menggunakan modal untuk setiap informasi kecil karena dapat mengganggu alur kerja.

---

## 8. Pola Navigasi

### 8.1 Aplikasi Android Calon Mahasiswa

Navigasi utama dirancang sederhana agar pengguna dapat menemukan fungsi penting tanpa banyak langkah.

Menu utama yang disarankan:

1. **Beranda** — ringkasan gelombang dan status pendaftaran.
    
2. **Pendaftaran** — membuat dan melengkapi formulir.
    
3. **Status** — melihat perkembangan pendaftaran.
    
4. **Profil** — melihat dan mengubah data akun yang diizinkan.
    

Halaman login dan registrasi akun berada di luar navigasi utama sebelum pengguna masuk.

Menu dapat menggunakan bottom navigation jika sesuai dengan jumlah dan prioritas fitur. Jika ruang layar terbatas, menu dapat disederhanakan agar tetap mudah digunakan.

### 8.2 Website Admin

Website admin menggunakan sidebar di layar desktop dan pola navigasi yang disesuaikan untuk layar sempit.

Menu berdasarkan kewenangan:

- **Dashboard**
    
- **Data Pendaftar**
    
- **Pemeriksaan Dokumen**
    
- **Gelombang Penerimaan**
    
- **Program Studi**
    
- **Pengguna**
    
- **Laporan**
    
- **Pengaturan Akun**
    

Menu yang tampil harus disesuaikan dengan role. Panitia tidak otomatis mendapatkan akses administrasi pengguna, sedangkan pimpinan tidak otomatis memperoleh hak untuk mengubah keputusan atau data operasional.

---

## 9. Aturan Formulir Pendaftaran

Formulir pendaftaran harus mengutamakan kejelasan, kelengkapan, dan pencegahan kesalahan.

### 9.1 Pengelompokan Formulir

Formulir dibagi menjadi beberapa kelompok:

1. **Data Pribadi** — nama lengkap, NIK, tempat dan tanggal lahir, jenis kelamin, nomor telepon, dan alamat.
    
2. **Pilihan Pendaftaran** — gelombang penerimaan dan satu program studi yang dipilih.
    
3. **Dokumen** — foto serta ijazah atau SKL.
    
4. **Pemeriksaan Akhir** — ringkasan data sebelum dikirim.
    

### 9.2 Validasi Formulir

- Tampilkan pesan kesalahan di dekat kolom terkait.
    
- Periksa format data sebelum mengirim permintaan.
    
- Backend harus memvalidasi seluruh data kembali.
    
- Beri penanda yang jelas pada kolom wajib.
    
- Jika pengiriman gagal, pertahankan input yang aman agar pengguna tidak harus mengisi ulang semuanya.
    
- Sebelum pengiriman final, tampilkan konfirmasi yang menjelaskan bahwa data akan dikirim untuk diperiksa.
    

### 9.3 Status Draft dan Submit

Status `draft` menunjukkan bahwa pendaftaran belum dikirim. Status `submitted` menunjukkan bahwa data sudah dikirim dan menunggu proses pemeriksaan.

Tombol **Kirim Pendaftaran** hanya dapat digunakan apabila persyaratan wajib telah terpenuhi. Jika proses berhasil, tampilkan nomor pendaftaran dan status terbaru yang dikonfirmasi oleh backend.

Satu akun hanya dapat memiliki satu pendaftaran pada gelombang yang sama. Pendaftaran pada gelombang berbeda mengikuti aturan gelombang dan kebijakan sistem.

---

## 10. Konsistensi Android dan Website

Walaupun dibangun menggunakan teknologi berbeda, kedua platform harus menggunakan istilah, warna, dan aturan bisnis yang sama.

|Elemen|Android|Website Admin|
|---|---|---|
|Warna utama|Teal|Teal|
|Warna aksen|Orange|Orange|
|Font|Inter atau fallback sistem|Inter atau sans-serif|
|Komponen utama|Input, tombol, kartu, badge|Input, tombol, tabel, kartu, badge|
|Status pendaftaran|Label dan badge|Label dan badge|
|Validasi|Validasi lokal dan backend|Validasi formulir dan backend|
|Hak akses|Berdasarkan akun calon mahasiswa|Berdasarkan role admin, panitia, atau pimpinan|
|Pesan error|Singkat dan mudah dipahami|Singkat dan mudah dipahami|

Komponen tidak harus identik secara teknis. Implementasi native Android dan komponen web boleh berbeda, tetapi makna, perilaku, dan identitas visualnya harus konsisten.

---

## 11. Aksesibilitas dan Responsivitas

### 11.1 Aksesibilitas

- Gunakan kontras teks dan latar yang memadai.
    
- Jangan menyampaikan informasi hanya melalui warna.
    
- Pastikan ikon penting memiliki label atau deskripsi yang dapat dibaca teknologi bantu.
    
- Gunakan label yang jelas untuk tombol dan input.
    
- Pastikan ukuran area sentuh nyaman digunakan pada perangkat mobile.
    
- Perhatikan pembesaran teks dan urutan fokus keyboard.
    
- Jangan menyampaikan pesan kesalahan hanya melalui ikon.
    

### 11.2 Responsivitas Website

- Pada layar desktop, tabel data pendaftar dapat menampilkan beberapa kolom sekaligus.
    
- Pada layar tablet, susunan kolom dan sidebar dapat disesuaikan.
    
- Pada layar sempit, prioritaskan informasi penting dan hindari pemotongan teks.
    
- Sediakan cara untuk melihat detail data yang tidak muat dalam tabel.
    
- Formulir dan dialog harus tetap dapat digunakan tanpa scroll horizontal yang tidak diperlukan.
    

### 11.3 Kondisi Antarmuka

Setiap halaman yang mengambil data dari backend harus memiliki kondisi tampilan yang jelas:

- **Loading:** data sedang dimuat.
    
- **Success:** data berhasil ditampilkan atau tindakan berhasil.
    
- **Empty:** belum ada data yang tersedia.
    
- **Error:** data gagal dimuat atau tindakan gagal.
    
- **Disabled:** tindakan tidak dapat digunakan pada kondisi saat ini.
    

Kondisi kosong bukan berarti terjadi kesalahan. Contohnya, tabel pendaftar yang belum memiliki data harus menampilkan informasi bahwa belum ada pendaftar, bukan halaman kosong tanpa penjelasan.

---

## 12. Aturan Bahasa dan Microcopy

Gunakan bahasa Indonesia yang sederhana, sopan, dan konsisten.

|Konteks|Teks yang Disarankan|
|---|---|
|Login|Masuk|
|Registrasi akun|Buat Akun|
|Penyimpanan|Simpan Perubahan|
|Pengiriman formulir|Kirim Pendaftaran|
|Pemeriksaan|Sedang Ditinjau|
|Permintaan perbaikan|Perlu Revisi|
|Hasil diterima|Diterima|
|Hasil tidak diterima|Tidak Diterima|
|Tidak ada data|Belum ada data yang tersedia.|
|Kesalahan umum|Terjadi kendala. Silakan coba kembali.|
|Berhasil|Perubahan berhasil disimpan.|

Istilah `draft`, `submitted`, dan status internal lainnya digunakan pada kode sistem. Pada antarmuka, tampilkan label yang mudah dipahami pengguna.

---

## 13. Implementasi Teknis

### 13.1 Android Kotlin

- Simpan warna, ukuran teks, dimensi, dan bentuk komponen dalam resource terpusat.
    
- Gunakan tema dan komponen bersama agar tampilan antarlayar konsisten.
    
- Terapkan validasi input untuk membantu pengguna sebelum mengirim data.
    
- Kelola status loading, error, dan success dengan jelas.
    
- Jangan menyimpan token autentikasi atau data sensitif secara sembarangan.
    
- Pastikan pemeriksaan akses tetap dilakukan oleh backend.
    

### 13.2 Laravel dan Website Admin

- Gunakan variabel CSS atau token desain untuk warna, spacing, radius, dan tipografi.
    
- Buat komponen bersama untuk tombol, input, badge, alert, dan modal.
    
- Gunakan validasi backend dan pemeriksaan otorisasi pada setiap endpoint.
    
- Jangan mengandalkan penyembunyian tombol sebagai satu-satunya pengamanan hak akses.
    
- Pastikan file dokumen hanya dapat diakses oleh pengguna yang berwenang.
    

### 13.3 Contoh CSS Design Token

Contoh berikut merupakan acuan awal untuk website admin.

```
:root {
  --color-primary: #0F766E;
  --color-primary-dark: #115E59;
  --color-primary-light: #CCFBF1;
  --color-accent: #F97316;

  --color-background: #F8FAFC;
  --color-surface: #FFFFFF;
  --color-text-primary: #0F172A;
  --color-text-secondary: #475569;
  --color-border: #E2E8F0;

  --color-success: #15803D;
  --color-warning: #B45309;
  --color-danger: #B91C1C;
  --color-info: #0369A1;

  --space-1: 4px;
  --space-2: 8px;
  --space-3: 12px;
  --space-4: 16px;
  --space-6: 24px;
  --space-8: 32px;

  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;

  --font-family: "Inter", Arial, sans-serif;
}
```

Token di atas menjadi sumber nilai visual yang konsisten. Perubahan pada warna atau spacing dapat dilakukan secara terpusat tanpa mengubah setiap komponen satu per satu.

---

## 14. Kriteria Penerimaan Design System

Design System dinyatakan siap digunakan apabila:

- Warna utama, aksen, dan warna status telah ditetapkan.
    
- Skala tipografi dan spacing telah didokumentasikan.
    
- Komponen tombol, input, kartu, badge, alert, dan modal memiliki aturan yang jelas.
    
- Status pendaftaran menggunakan label yang konsisten di Android dan website.
    
- Tampilan loading, empty, error, dan success telah ditentukan.
    
- Formulir memiliki aturan validasi dan penanganan kesalahan.
    
- Navigasi disesuaikan dengan role pengguna.
    
- Kontras warna dan keterbacaan telah diperiksa.
    
- Website dapat digunakan pada ukuran layar yang didukung.
    
- Komponen tidak memberikan kesan bahwa tindakan berhasil sebelum backend mengonfirmasinya.
    
- Dokumen dan data pendaftaran tidak ditampilkan kepada pengguna yang tidak berwenang.
    

## 15. Hal yang Masih Perlu Diputuskan

Beberapa keputusan berikut perlu ditetapkan sebelum implementasi final:

1. Logo resmi dan aset identitas visual ITB Akiday.
    
2. Font final yang digunakan pada kedua platform.
    
3. Pilihan library komponen website admin.
    
4. Kebijakan tema gelap atau tema terang saja.
    
5. Ukuran minimum area sentuh dan target aksesibilitas.
    
6. Perilaku tampilan pada ukuran layar Android minimum yang didukung.
    
7. Hasil pengujian kontras dan usability dengan pengguna.
    

Sebelum logo dan identitas resmi dikonfirmasi, warna dan tipografi pada dokumen ini diperlakukan sebagai usulan desain proyek, bukan standar resmi institusi.

---

## 16. Kesimpulan

Design System PMB ITB Akiday menjadi acuan untuk membangun tampilan aplikasi Android dan website admin yang konsisten, sederhana, serta mudah digunakan. Penggunaan token visual, komponen bersama, pola formulir yang jelas, dan label status yang seragam diharapkan mengurangi kesalahan pengguna dan mempermudah proses pengembangan. Seluruh pedoman perlu divalidasi melalui implementasi prototipe dan pengujian sebelum ditetapkan sebagai standar final.