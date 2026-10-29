# UI UX Design — PMB ITB Akiday

**Nama Proyek:** Sistem Informasi Penerimaan Mahasiswa Baru ITB Akiday  
**Versi Dokumen:** 1.0  
**Status:** Draft untuk Implementasi  
**Platform:** Android (Kotlin) dan Website Admin  
**Backend:** Laravel REST API  
**Database:** MySQL  
**Konsep Visual:** Minimalis, modern, profesional, dan mudah digunakan

---

## 1. Pendahuluan

### 1.1 Latar Belakang

Penerimaan mahasiswa baru merupakan proses yang melibatkan beberapa tahapan, mulai dari pembuatan akun, pengisian data pribadi, pemilihan program studi, pengunggahan dokumen, hingga pemeriksaan dan penetapan hasil penerimaan. Jika antarmuka sistem tidak dirancang dengan baik, calon mahasiswa dapat mengalami kesulitan memahami persyaratan, melakukan kesalahan saat mengisi formulir, atau tidak mengetahui perkembangan pendaftarannya.

Oleh karena itu, PMB ITB Akiday dirancang dengan memperhatikan aspek User Interface (UI) dan User Experience (UX). UI berfokus pada tampilan visual, seperti warna, tipografi, tombol, formulir, dan susunan informasi. Sementara itu, UX berfokus pada kemudahan pengguna dalam memahami sistem dan menyelesaikan tugasnya.

Sistem memiliki dua antarmuka utama, yaitu aplikasi Android untuk calon mahasiswa dan website admin untuk pengelolaan penerimaan. Keduanya menggunakan identitas visual yang konsisten, tetapi memiliki struktur navigasi yang disesuaikan dengan kebutuhan setiap pengguna.

### 1.2 Tujuan Desain

Tujuan perancangan UI/UX PMB ITB Akiday adalah:

1. Memudahkan calon mahasiswa melakukan pendaftaran secara online.
    
2. Membantu pengguna memahami persyaratan dan tahapan pendaftaran.
    
3. Mengurangi kesalahan pengisian data dan pengunggahan dokumen.
    
4. Memudahkan calon mahasiswa memantau status pendaftaran.
    
5. Membantu panitia memeriksa data dan dokumen secara terstruktur.
    
6. Memudahkan admin mengelola data operasional.
    
7. Membantu pimpinan melihat ringkasan dan laporan penerimaan.
    
8. Menjaga konsistensi tampilan pada aplikasi Android dan website admin.
    

### 1.3 Ruang Lingkup

Perancangan UI/UX mencakup:

- Struktur navigasi aplikasi Android.
    
- Struktur navigasi website admin.
    
- User flow untuk setiap role.
    
- Rancangan layout halaman utama.
    
- Pola interaksi formulir dan unggah dokumen.
    
- Tampilan status pendaftaran.
    
- Penanganan kondisi loading, kosong, berhasil, dan gagal.
    
- Prinsip aksesibilitas dan responsivitas.
    
- Rekomendasi pengujian usability.
    

Dokumen ini merupakan pedoman desain dan interaksi. Detail komponen visual mengikuti dokumen [[Design System]], sedangkan rincian setiap layar dijelaskan dalam [[Screen Specification]].

---

## 2. Pengguna dan Kebutuhan

### 2.1 Calon Mahasiswa

Calon mahasiswa merupakan pengguna utama aplikasi Android.

**Kebutuhan pengguna:**

- Membuat akun dan masuk ke sistem.
    
- Melengkapi data pribadi.
    
- Melihat gelombang penerimaan yang tersedia.
    
- Memilih satu program studi untuk setiap pendaftaran.
    
- Mengunggah dokumen persyaratan.
    
- Memeriksa data sebelum dikirim.
    
- Mendapatkan nomor pendaftaran.
    
- Melihat status dan riwayat pendaftaran.
    
- Memperbaiki data atau dokumen jika diminta revisi.
    

**Masalah yang perlu dihindari:**

- Formulir terlalu panjang tanpa pembagian bagian.
    
- Persyaratan dokumen tidak jelas.
    
- Pesan kesalahan tidak menjelaskan cara memperbaikinya.
    
- Pengguna tidak mengetahui apakah pendaftaran sudah berhasil dikirim.
    
- Pengguna dapat mengirim pendaftaran berulang kali karena tidak mendapat respons yang jelas.
    

### 2.2 Admin

Admin bertanggung jawab atas pengelolaan data operasional sesuai hak akses yang diberikan.

**Kebutuhan pengguna:**

- Mengelola akun pengguna.
    
- Mengelola gelombang penerimaan.
    
- Mengelola program studi.
    
- Memantau pendaftaran.
    
- Melihat laporan operasional sesuai kewenangan.
    

**Pendekatan desain:**

Website admin harus menekankan efisiensi kerja. Informasi disajikan melalui tabel, filter, pencarian, ringkasan, dan formulir pengelolaan data yang konsisten.

### 2.3 Panitia

Panitia berfokus pada pemeriksaan pendaftaran dan pemrosesan hasil sesuai kewenangannya.

**Kebutuhan pengguna:**

- Melihat daftar pendaftaran yang perlu diperiksa.
    
- Membaca data calon mahasiswa.
    
- Membuka dokumen persyaratan.
    
- Memberikan catatan revisi.
    
- Memperbarui status pemeriksaan.
    
- Menetapkan keputusan jika memiliki kewenangan tersebut.
    

**Pendekatan desain:**

Informasi identitas, dokumen, status, dan catatan pemeriksaan perlu ditampilkan secara terstruktur agar panitia tidak harus berpindah halaman secara berlebihan.

### 2.4 Pimpinan

Pimpinan membutuhkan informasi untuk memantau proses penerimaan.

**Kebutuhan pengguna:**

- Melihat jumlah pendaftar.
    
- Memantau distribusi pendaftaran berdasarkan status.
    
- Melihat ringkasan per gelombang dan program studi.
    
- Mengakses dan mengunduh laporan sesuai izin.
    

**Pendekatan desain:**

Dashboard perlu mengutamakan ringkasan data, visualisasi yang mudah dibaca, filter, dan akses laporan. Hak akses pimpinan tidak otomatis mencakup pengubahan data atau penetapan keputusan.

---

## 3. Prinsip UX

### 3.1 Sederhana dan Terarah

Setiap halaman harus memiliki tujuan utama yang jelas. Pengguna tidak perlu melihat terlalu banyak pilihan yang tidak relevan dengan tugasnya.

Contohnya, halaman pemeriksaan akhir berfokus pada ringkasan data, kelengkapan dokumen, dan tindakan mengirim pendaftaran.

### 3.2 Progres yang Jelas

Proses pendaftaran dibagi menjadi beberapa tahap agar pengguna mengetahui bagian yang sudah dan belum diselesaikan.

Tahapan yang disarankan:

1. Data pribadi.
    
2. Pilihan pendaftaran.
    
3. Dokumen persyaratan.
    
4. Pemeriksaan akhir.
    
5. Pengiriman pendaftaran.
    
6. Pemantauan status.
    

Indikator progres menunjukkan tahap pengisian, bukan jaminan bahwa pendaftaran sudah diterima.

### 3.3 Pencegahan Kesalahan

Sistem harus membantu pengguna menghindari kesalahan sebelum data dikirim.

Contohnya:

- Memvalidasi format email dan NIK.
    
- Menampilkan persyaratan dokumen sebelum unggah.
    
- Menampilkan ringkasan data sebelum pengiriman.
    
- Menonaktifkan tindakan ketika prasyarat belum terpenuhi.
    
- Memeriksa ulang status dan kelengkapan data di backend.
    

### 3.4 Feedback yang Jelas

Setiap tindakan penting harus memberikan respons yang mudah dipahami.

Contoh:

- Setelah menyimpan profil, tampilkan konfirmasi penyimpanan.
    
- Saat dokumen sedang diunggah, tampilkan indikator proses.
    
- Jika pengiriman gagal, jelaskan langkah yang dapat dilakukan.
    
- Setelah pendaftaran berhasil dikirim, tampilkan nomor pendaftaran dan status yang dikonfirmasi backend.
    

### 3.5 Konsistensi

Komponen, istilah, warna, dan pola interaksi digunakan secara konsisten pada seluruh layar.

Status internal seperti `submitted` dan `revision` dapat ditampilkan menggunakan label bahasa Indonesia, tetapi kode status backend tetap konsisten.

### 3.6 Keamanan yang Tidak Membingungkan

Sistem harus menjelaskan tindakan yang berpengaruh pada data tanpa membuka informasi sensitif.

Contohnya, sebelum pengguna mengirim pendaftaran, tampilkan konfirmasi yang menjelaskan bahwa data akan dikirim untuk diperiksa. Dokumen pribadi hanya boleh diakses oleh pengguna yang memiliki kewenangan.

---

## 4. Konsep Visual

### 4.1 Gaya Desain

Gaya visual PMB ITB Akiday menggunakan pendekatan minimalis dengan hierarki informasi yang jelas.

Karakteristik desain:

- Latar halaman netral.
    
- Warna utama teal.
    
- Warna orange sebagai aksen.
    
- Kartu dengan batas yang sederhana.
    
- Tipografi yang mudah dibaca.
    
- Ikon yang konsisten.
    
- Tombol dengan label tindakan yang jelas.
    
- Jarak antarelemen yang cukup.
    
- Penggunaan dekorasi secara terbatas.
    

Desain tidak perlu menggunakan banyak animasi atau elemen visual yang mengganggu proses pendaftaran.

### 4.2 Palet Warna

|Elemen|Warna|Kode|
|---|---|---|
|Warna utama|Teal|`#0F766E`|
|Teal gelap|Dark Teal|`#115E59`|
|Teal muda|Light Teal|`#CCFBF1`|
|Warna aksen|Orange|`#F97316`|
|Latar halaman|Abu-abu sangat muda|`#F8FAFC`|
|Latar kartu|Putih|`#FFFFFF`|
|Teks utama|Biru gelap keabu-abuan|`#0F172A`|
|Teks sekunder|Abu-abu kebiruan|`#475569`|
|Garis batas|Abu-abu muda|`#E2E8F0`|

Warna tersebut merupakan usulan desain proyek, bukan identitas resmi institusi yang telah diverifikasi. Kontras teks dan latar harus diuji sebelum implementasi final.

### 4.3 Tipografi

Gunakan Inter atau font sans-serif yang setara.

Hierarki yang disarankan:

- Judul halaman: 24–28 px.
    
- Judul bagian: 20–24 px.
    
- Isi utama: 16 px.
    
- Teks pendukung: 14 px.
    
- Keterangan kecil: 12 px, hanya untuk informasi yang tidak terlalu penting.
    

Pada Android, gunakan satuan `sp` untuk ukuran teks agar mengikuti pengaturan ukuran font perangkat.

### 4.4 Bentuk Komponen

- Tombol utama menggunakan warna teal.
    
- Tombol sekunder menggunakan latar netral atau border.
    
- Kartu menggunakan radius sekitar 12 px.
    
- Input memiliki label yang selalu terlihat.
    
- Badge status menggunakan warna yang konsisten dengan teks yang jelas.
    
- Modal konfirmasi digunakan untuk tindakan penting.
    
- Ikon selalu memiliki makna yang mudah dikenali.
    

Rincian nilai warna, radius, spacing, dan komponen mengikuti [[Design System]].

---

## 5. Arsitektur Navigasi

### 5.1 Navigasi Android

Navigasi aplikasi Android dibuat sederhana dengan empat menu utama yang disarankan.

|Menu|Isi|
|---|---|
|Beranda|Ringkasan status, informasi gelombang, dan tindakan utama|
|Pendaftaran|Formulir, pilihan program studi, dan dokumen|
|Status|Status dan riwayat pendaftaran|
|Profil|Data akun dan pengaturan|

Halaman Login dan Registrasi berada di luar navigasi utama karena pengguna belum masuk ke sistem.

Menu dapat diubah berdasarkan hasil pengujian prototipe. Apabila jumlah menu terlalu banyak untuk layar perangkat, struktur navigasi perlu disederhanakan.

### 5.2 Navigasi Website Admin

Website admin menggunakan sidebar untuk mengelompokkan fitur operasional.

Struktur yang disarankan:

```
Dashboard
│
├── Pendaftaran
│   ├── Semua Pendaftar
│   ├── Menunggu Pemeriksaan
│   ├── Sedang Ditinjau
│   └── Perlu Revisi
│
├── Data Master
│   ├── Gelombang Penerimaan
│   └── Program Studi
│
├── Pengguna
│   └── Manajemen Pengguna
│
├── Laporan
│   ├── Rekap Pendaftar
│   ├── Rekap Status
│   └── Rekap Program Studi
│
└── Profil dan Logout
```

Menu yang tersedia harus mengikuti role dan hak akses pengguna. Struktur di atas merupakan pengelompokan navigasi, bukan berarti setiap role otomatis dapat mengakses semua fitur.

---

## 6. Rancangan Layout Android

### 6.1 Beranda

Beranda harus menampilkan informasi yang paling dibutuhkan calon mahasiswa tanpa membuat halaman terlalu padat.

**Susunan layout:**

```
┌──────────────────────────────────┐
│ Logo PMB ITB Akiday        Profil │
├──────────────────────────────────┤
│ Halo, Calon Mahasiswa             │
│ Pantau proses pendaftaran Anda    │
├──────────────────────────────────┤
│ STATUS PENDAFTARAN                │
│ Sedang Ditinjau                   │
│ Nomor: PMB-XXXXXXXX                │
│ [Lihat Status]                    │
├──────────────────────────────────┤
│ INFORMASI GELOMBANG               │
│ Gelombang yang tersedia           │
│ Tanggal sesuai data sistem        │
│ [Lihat Gelombang]                 │
├──────────────────────────────────┤
│ Informasi dan Pengumuman          │
├──────────────────────────────────┤
│ Beranda | Daftar | Status | Profil│
└──────────────────────────────────┘
```

Nomor pendaftaran pada ilustrasi hanya contoh format visual, bukan format final yang telah ditetapkan.

**Aturan UX:**

- Jika pengguna belum mendaftar, kartu status diganti dengan ajakan memulai pendaftaran.
    
- Jika tidak ada gelombang tersedia, tampilkan informasi yang jelas.
    
- Jika data belum berhasil dimuat, tampilkan kondisi error dan opsi mencoba kembali.
    
- Tombol utama harus sesuai dengan kondisi pengguna saat ini.
    

### 6.2 Formulir Pendaftaran

Formulir dibagi menjadi beberapa bagian untuk mengurangi beban pengguna.

```
┌──────────────────────────────────┐
│ ← Pendaftaran                    │
├──────────────────────────────────┤
│ Data Pribadi                     │
│ ●────────○────────○────────○      │
│                                  │
│ Nama Lengkap                     │
│ [____________________________]   │
│                                  │
│ NIK                              │
│ [____________________________]   │
│                                  │
│ Tempat Lahir                     │
│ [____________________________]   │
│                                  │
│ Tanggal Lahir                    │
│ [Pilih tanggal_______________]   │
│                                  │
│ Jenis Kelamin                    │
│ [Pilih______________________▼]   │
│                                  │
│ [Simpan Draft] [Lanjutkan]       │
└──────────────────────────────────┘
```

**Aturan UX:**

- Indikator progres menunjukkan tahapan yang sedang dikerjakan.
    
- Label input tidak hilang ketika pengguna mulai mengetik.
    
- Pesan validasi muncul dekat kolom terkait.
    
- Pengguna dapat kembali ke bagian sebelumnya tanpa kehilangan data yang sudah disimpan.
    
- Tombol Lanjutkan tidak menggantikan validasi backend.
    

### 6.3 Unggah Dokumen

```
┌──────────────────────────────────┐
│ ← Dokumen Persyaratan            │
├──────────────────────────────────┤
│ Foto                             │
│ Persyaratan foto sesuai ketentuan│
│ [Pilih Foto]                     │
│ Status: Belum diunggah            │
├──────────────────────────────────┤
│ Ijazah / SKL                     │
│ Unggah dokumen pendidikan        │
│ [Pilih Dokumen]                  │
│ Status: Belum diunggah            │
├──────────────────────────────────┤
│ Pastikan dokumen dapat dibaca.   │
│                                  │
│ [Kembali] [Lanjutkan]            │
└──────────────────────────────────┘
```

**Aturan UX:**

- Tampilkan format dan ukuran maksimum setelah kebijakan ditetapkan.
    
- Berikan feedback saat proses unggah berlangsung.
    
- Tampilkan status berhasil hanya setelah server mengonfirmasi penyimpanan.
    
- Jika unggahan gagal, berikan opsi mencoba kembali.
    
- Jangan meminta pengguna mengunggah dokumen yang tidak dipersyaratkan.
    

### 6.4 Pemeriksaan Akhir

```
┌──────────────────────────────────┐
│ Pemeriksaan Akhir                │
├──────────────────────────────────┤
│ DATA PRIBADI                     │
│ Nama: [Nama Calon Mahasiswa]     │
│ NIK:  [Disamarkan bila sesuai]   │
│ [Ubah Data]                      │
├──────────────────────────────────┤
│ PILIHAN PENDAFTARAN              │
│ Gelombang: [Nama Gelombang]      │
│ Program Studi: [Nama Program]    │
│ [Ubah Pilihan]                   │
├──────────────────────────────────┤
│ DOKUMEN                          │
│ Foto: Berhasil diunggah          │
│ Ijazah/SKL: Berhasil diunggah    │
│ [Periksa Dokumen]                │
├──────────────────────────────────┤
│ [Kembali]                        │
│ [Kirim Pendaftaran]              │
└──────────────────────────────────┘
```

**Aturan UX:**

- Data yang ditampilkan harus berasal dari data aktual pendaftaran.
    
- Informasi sensitif seperti NIK dapat disamarkan jika nilai lengkap tidak diperlukan untuk pemeriksaan akhir.
    
- Tombol kirim membuka dialog konfirmasi.
    
- Setelah pengiriman berhasil, tampilkan nomor pendaftaran dan status yang diberikan backend.
    
- Jika pengiriman mengalami timeout, periksa status pendaftaran sebelum pengguna mengirim kembali.
    

### 6.5 Status Pendaftaran

```
┌──────────────────────────────────┐
│ Status Pendaftaran               │
├──────────────────────────────────┤
│ Nomor Pendaftaran                │
│ PMB-XXXXXXXX                      │
│                                  │
│ Status Saat Ini                  │
│ [SEDANG DITINJAU]                │
│                                  │
│ Riwayat Proses                   │
│ ✓ Pendaftaran dikirim            │
│ ✓ Data diterima sistem           │
│ ● Sedang ditinjau                │
│ ○ Keputusan akhir                │
│                                  │
│ [Lihat Detail Pendaftaran]       │
└──────────────────────────────────┘
```

Urutan riwayat yang sebenarnya harus mengikuti data backend. Ilustrasi tersebut bukan berarti setiap tahap pemeriksaan selalu memiliki status terpisah.

**Aturan UX:**

- Status selalu menggunakan label teks.
    
- Tampilkan waktu pembaruan jika tersedia.
    
- Jika revisi diperlukan, tampilkan catatan dan tindakan berikutnya.
    
- Jangan menampilkan keputusan akhir sebelum disimpan oleh pihak berwenang.
    

---

## 7. Rancangan Layout Website Admin

### 7.1 Dashboard

Dashboard menggunakan sidebar di sebelah kiri dan area konten utama di sebelah kanan.

```
┌────────────────┬──────────────────────────────────────┐
│ PMB ITB Akiday │ Dashboard                            │
├────────────────┤──────────────────────────────────────┤
│ Dashboard      │ Filter: [Gelombang ▼] [Periode ▼]    │
│ Pendaftaran    │                                      │
│ Data Master    │ ┌──────────┐ ┌──────────┐ ┌────────┐  │
│ Pengguna       │ │ Total    │ │ Ditinjau │ │ Revisi │  │
│ Laporan        │ │ Pendaftar│ │          │ │        │  │
│ Profil         │ └──────────┘ └──────────┘ └────────┘  │
│ Logout         │                                      │
│                │ Ringkasan Status                     │
│                │ [Visualisasi data]                   │
│                │                                      │
│                │ Pendaftaran Terbaru                  │
│                │ [Tabel data pendaftar]               │
└────────────────┴──────────────────────────────────────┘
```

**Prinsip desain:**

- Kartu statistik menampilkan informasi yang benar-benar dibutuhkan.
    
- Grafik harus memiliki judul, label, dan keterangan.
    
- Filter harus memiliki nilai yang jelas.
    
- Tabel terbaru tidak menggantikan halaman daftar pendaftar.
    
- Angka statistik harus berasal dari backend.
    

### 7.2 Daftar Pendaftar

```
┌───────────────────────────────────────────────────────┐
│ Daftar Pendaftar                                      │
├───────────────────────────────────────────────────────┤
│ Cari nama/nomor: [________________] [Cari]             │
│ Status: [Semua ▼]  Gelombang: [Semua ▼]                │
├───────────────────────────────────────────────────────┤
│ Nomor       Nama       Program Studi   Status   Aksi  │
│ PMB-001     Contoh A   Informatika     Ditinjau Detail│
│ PMB-002     Contoh B   Sistem Informasi Revisi  Detail│
│ PMB-003     Contoh C   Manajemen       Dikirim Detail │
├───────────────────────────────────────────────────────┤
│                 [Sebelumnya] [Berikutnya]              │
└───────────────────────────────────────────────────────┘
```

Nama, nomor, dan data pada ilustrasi merupakan data contoh.

**Prinsip desain:**

- Gunakan pagination untuk jumlah data besar.
    
- Sediakan pencarian dan filter yang relevan.
    
- Gunakan badge untuk membantu identifikasi status.
    
- Tampilkan informasi yang diperlukan untuk pekerjaan panitia.
    
- Hindari menampilkan NIK lengkap di tabel umum.
    

### 7.3 Pemeriksaan Dokumen

```
┌───────────────────────────────────────────────────────┐
│ Detail Pendaftaran                                    │
├───────────────────────────────────────────────────────┤
│ Data Calon Mahasiswa                                  │
│ Nama, nomor pendaftaran, gelombang, program studi     │
├───────────────────────────────────────────────────────┤
│ Dokumen Persyaratan                                   │
│ Foto            [Lihat Dokumen] [Status Pemeriksaan]  │
│ Ijazah / SKL    [Lihat Dokumen] [Status Pemeriksaan]  │
├───────────────────────────────────────────────────────┤
│ Catatan Pemeriksaan                                   │
│ [_______________________________________________]     │
│ [_______________________________________________]     │
├───────────────────────────────────────────────────────┤
│ [Minta Revisi] [Simpan Pemeriksaan]                   │
└───────────────────────────────────────────────────────┘
```

**Prinsip desain:**

- Data identitas dan dokumen disusun dalam bagian yang berbeda.
    
- Dokumen dapat dibuka melalui akses yang terlindungi.
    
- Catatan revisi harus spesifik dan dapat ditindaklanjuti.
    
- Tombol keputusan final hanya muncul bagi pengguna yang berwenang dan ketika status mengizinkan.
    

### 7.4 Laporan

Halaman laporan menampilkan filter dan ringkasan data yang dapat dibaca dengan cepat.

Elemen yang disarankan:

- Filter gelombang.
    
- Filter periode.
    
- Filter program studi.
    
- Jumlah pendaftar.
    
- Rekap status.
    
- Rekap per program studi.
    
- Tabel rincian.
    
- Tombol unduh PDF.
    

Hasil laporan harus mengikuti filter yang dipilih. Jika data tidak tersedia, tampilkan kondisi kosong yang jelas dan jangan menghasilkan laporan yang seolah-olah berisi data.

---

## 8. User Flow

### 8.1 Flow Pendaftaran Calon Mahasiswa

```
flowchart TD
    A[Mulai] --> B{Sudah punya akun?}
    B -- Tidak --> C[Registrasi Akun]
    C --> D[Login]
    B -- Ya --> D
    D --> E[Beranda]
    E --> F[Lengkapi Profil]
    F --> G{Profil valid?}
    G -- Tidak --> F
    G -- Ya --> H[Pilih Gelombang]
    H --> I{Gelombang tersedia?}
    I -- Tidak --> J[Tampilkan Informasi]
    I -- Ya --> K[Pilih Program Studi]
    K --> L[Isi Formulir]
    L --> M[Unggah Dokumen]
    M --> N{Persyaratan lengkap?}
    N -- Tidak --> M
    N -- Ya --> O[Pemeriksaan Akhir]
    O --> P[Kirim Pendaftaran]
    P --> Q{Backend berhasil?}
    Q -- Tidak --> R[Periksa Pesan dan Status]
    R --> O
    Q -- Ya --> S[Tampilkan Nomor Pendaftaran]
    S --> T[Pantau Status]
```

Jika pengiriman mengalami timeout, sistem harus memeriksa status pendaftaran sebelum mengirim ulang. Dengan demikian, pengguna tidak membuat duplikasi karena mengira permintaan sebelumnya gagal.

### 8.2 Flow Pemeriksaan Panitia

```
flowchart TD
    A[Login Panitia] --> B[Dashboard]
    B --> C[Daftar Pendaftar]
    C --> D[Pilih Pendaftaran]
    D --> E[Periksa Data dan Dokumen]
    E --> F{Ada kekurangan?}
    F -- Ya --> G[Tulis Catatan Revisi]
    G --> H[Ajukan Revisi]
    H --> I[Status Perlu Revisi]
    F -- Tidak --> J[Selesaikan Pemeriksaan]
    J --> K{Berwenang menetapkan keputusan?}
    K -- Tidak --> L[Lanjutkan ke Pihak Berwenang]
    K -- Ya --> M[Tetapkan Keputusan]
    M --> N[Status dan Riwayat Diperbarui]
```

Alur final perlu mengikuti kebijakan penerimaan yang telah disepakati. Pemeriksaan kelengkapan tidak otomatis berarti calon mahasiswa diterima.

### 8.3 Flow Revisi Pendaftaran

```
flowchart TD
    A[Panitia Meminta Revisi] --> B[Status Revision]
    B --> C[Calon Mahasiswa Membuka Status]
    C --> D[Membaca Catatan Revisi]
    D --> E[Memperbaiki Data atau Dokumen]
    E --> F[Memeriksa Ulang]
    F --> G[Mengirim Perbaikan]
    G --> H[Backend Memvalidasi]
    H --> I{Valid?}
    I -- Tidak --> E
    I -- Ya --> J[Status Kembali ke Submitted]
    J --> K[Panitia Memeriksa Ulang]
```

Batas waktu dan jumlah kesempatan revisi masih perlu ditetapkan. Jika kebijakan tersebut diterapkan, antarmuka harus menampilkan batas yang relevan secara jelas.

---

## 9. UX untuk Kondisi Khusus

### 9.1 Koneksi Internet Bermasalah

Karena pendaftaran menggunakan backend, proses yang membutuhkan penyimpanan atau pengiriman data harus menangani gangguan koneksi.

Desain yang disarankan:

- Tampilkan pesan ketika permintaan gagal.
    
- Pertahankan input yang aman agar tidak perlu diisi ulang.
    
- Bedakan tindakan yang belum terkirim dengan tindakan yang sudah dikonfirmasi.
    
- Untuk pengiriman pendaftaran, periksa status terbaru sebelum mencoba ulang.
    
- Jangan menampilkan data lama sebagai data terbaru tanpa penanda yang sesuai.
    

Penyimpanan draft secara lokal dapat dipertimbangkan, tetapi harus dirancang dengan perlindungan data dan mekanisme sinkronisasi yang jelas. Fitur offline penuh tidak diasumsikan tersedia dalam MVP.

### 9.2 Pengunggahan Dokumen Gagal

Jika unggahan gagal:

1. Tampilkan pesan kegagalan.
    
2. Pertahankan informasi berkas yang dipilih apabila memungkinkan.
    
3. Berikan opsi untuk mencoba kembali.
    
4. Jangan mengubah status dokumen menjadi berhasil sebelum server mengonfirmasi.
    
5. Jika penyebabnya format atau ukuran, jelaskan persyaratan yang harus dipenuhi.
    

### 9.3 Pendaftaran Duplikat

Jika calon mahasiswa mencoba membuat pendaftaran kedua pada gelombang yang sama:

- Jangan membuat pendaftaran baru.
    
- Tampilkan informasi bahwa pendaftaran sudah tersedia.
    
- Berikan akses ke pendaftaran yang sudah ada.
    
- Pastikan backend dan database tetap menjadi pengaman utama terhadap duplikasi.
    

### 9.4 Sesi Berakhir

Jika sesi pengguna tidak lagi valid:

- Hentikan permintaan yang membutuhkan autentikasi.
    
- Arahkan pengguna untuk login kembali.
    
- Hindari kehilangan input yang belum disimpan apabila dapat dipertahankan dengan aman.
    
- Jangan menampilkan data terproteksi setelah sesi berakhir.
    

### 9.5 Data Berubah saat Diperiksa

Jika data pendaftaran telah berubah sejak halaman dibuka:

- Tampilkan informasi bahwa data perlu dimuat ulang.
    
- Jangan menimpa perubahan terbaru secara diam-diam.
    
- Minta pengguna meninjau ulang sebelum melanjutkan tindakan penting.
    

---

## 10. Aksesibilitas

Desain harus dapat digunakan oleh sebanyak mungkin calon mahasiswa dan pengguna internal.

Ketentuan:

1. Gunakan kontras warna yang memadai.
    
2. Jangan mengandalkan warna sebagai satu-satunya penanda status.
    
3. Sediakan label yang jelas pada semua input.
    
4. Pastikan tombol dapat dikenali melalui teks atau label aksesibilitas.
    
5. Perhatikan urutan fokus keyboard pada website.
    
6. Pastikan teks tetap terbaca ketika ukuran font perangkat diperbesar.
    
7. Hindari animasi yang tidak diperlukan.
    
8. Sediakan pesan kesalahan yang dapat dikenali teknologi bantu.
    
9. Hindari teks penting yang terlalu kecil.
    
10. Pastikan elemen sentuh Android memiliki area interaksi yang nyaman.
    

Pemeriksaan aksesibilitas perlu dilakukan saat prototipe dan aplikasi telah tersedia, bukan hanya berdasarkan dokumentasi.

---

## 11. Responsivitas

### 11.1 Android

- Layout menyesuaikan ukuran layar.
    
- Formulir dapat digulir secara vertikal.
    
- Tombol utama mudah dijangkau.
    
- Keyboard tidak menutupi input aktif.
    
- Dokumen dan status tetap terbaca pada layar sempit.
    

### 11.2 Website Admin

- Desktop menampilkan sidebar dan tabel dengan lebih banyak kolom.
    
- Tablet dapat menggunakan sidebar yang diperkecil atau dapat dibuka-tutup.
    
- Layar sempit harus tetap dapat mengakses fitur penting.
    
- Tabel dapat menggunakan scroll horizontal secara terkontrol atau tampilan kartu alternatif.
    
- Modal dan formulir tidak boleh melampaui area layar.
    

Titik breakpoint final harus ditetapkan saat implementasi dan diuji pada ukuran layar yang didukung.

---

## 12. Microcopy dan Bahasa Antarmuka

Bahasa yang digunakan harus sederhana, konsisten, dan tidak membingungkan.

|Kondisi|Teks|
|---|---|
|Belum ada pendaftaran|Anda belum memiliki pendaftaran pada gelombang yang tersedia.|
|Profil belum lengkap|Lengkapi data pribadi sebelum melanjutkan.|
|Dokumen belum tersedia|Unggah dokumen yang masih diperlukan.|
|Pengiriman berhasil|Pendaftaran berhasil dikirim.|
|Menunggu pemeriksaan|Pendaftaran Anda sedang menunggu pemeriksaan.|
|Sedang ditinjau|Pendaftaran Anda sedang ditinjau oleh panitia.|
|Perlu revisi|Periksa catatan revisi dan perbaiki data atau dokumen yang diminta.|
|Koneksi bermasalah|Permintaan belum berhasil diproses. Periksa koneksi dan status pendaftaran sebelum mencoba kembali.|
|Tidak ada data|Belum ada data yang tersedia.|
|Akses ditolak|Anda tidak memiliki izin untuk melakukan tindakan ini.|

Pesan harus disesuaikan dengan respons backend yang sebenarnya. Jangan menampilkan pesan berhasil jika server belum mengonfirmasi hasil tindakan.

---

## 13. Rekomendasi Pembuatan Prototipe

Prototipe dapat dibuat menggunakan Figma atau alat desain antarmuka lain.

### 13.1 Prototipe Android

Layar minimum:

- Login.
    
- Registrasi akun.
    
- Beranda.
    
- Profil calon mahasiswa.
    
- Formulir pendaftaran.
    
- Unggah dokumen.
    
- Pemeriksaan akhir.
    
- Status pendaftaran.
    
- Detail pendaftaran.
    

### 13.2 Prototipe Website Admin

Layar minimum:

- Login admin.
    
- Dashboard.
    
- Daftar pendaftar.
    
- Detail pendaftar.
    
- Pemeriksaan dokumen.
    
- Keputusan penerimaan.
    
- Manajemen gelombang.
    
- Manajemen program studi.
    
- Manajemen pengguna.
    
- Laporan.
    

### 13.3 Interaksi yang Harus Diprototipekan

- Berpindah dari login ke beranda.
    
- Membuat dan melengkapi pendaftaran.
    
- Memilih gelombang dan program studi.
    
- Mengunggah dokumen.
    
- Mengirim pendaftaran.
    
- Melihat status pendaftaran.
    
- Meminta revisi dari website admin.
    
- Memperbaiki pendaftaran setelah revisi.
    
- Melihat dashboard dan mengunduh laporan.
    

Gunakan data contoh yang tidak memuat informasi pribadi nyata.

---

## 14. Rencana Pengujian Usability

Pengujian usability bertujuan mengetahui apakah pengguna dapat menyelesaikan tugas tanpa kebingungan yang berarti.

### 14.1 Skenario Pengujian

|ID|Skenario|Kriteria Keberhasilan|
|---|---|---|
|UX-01|Calon mahasiswa membuat akun|Akun berhasil dibuat dengan data valid|
|UX-02|Calon mahasiswa melengkapi profil|Data wajib dapat diisi dan disimpan|
|UX-03|Calon mahasiswa membuat pendaftaran|Gelombang dan program studi dapat dipilih sesuai aturan|
|UX-04|Calon mahasiswa mengunggah dokumen|Dokumen valid berhasil diunggah|
|UX-05|Calon mahasiswa mengirim pendaftaran|Nomor dan status pendaftaran ditampilkan setelah konfirmasi backend|
|UX-06|Calon mahasiswa memeriksa status|Status dan riwayat dapat ditemukan dengan mudah|
|UX-07|Panitia meminta revisi|Catatan revisi dapat diberikan dan disimpan|
|UX-08|Calon mahasiswa memperbaiki pendaftaran|Catatan revisi mudah dipahami dan dapat ditindaklanjuti|
|UX-09|Admin mencari pendaftar|Data dapat ditemukan melalui pencarian dan filter|
|UX-10|Pimpinan melihat laporan|Ringkasan dan filter laporan dapat digunakan|

### 14.2 Aspek yang Diamati

- Apakah pengguna memahami label dan instruksi?
    
- Apakah pengguna dapat menyelesaikan tugas tanpa bantuan?
    
- Bagian mana yang menyebabkan kesalahan?
    
- Apakah pesan error membantu menyelesaikan masalah?
    
- Apakah pengguna mengetahui status proses yang sedang berlangsung?
    
- Apakah pengguna dapat menemukan fungsi utama dengan cepat?
    

### 14.3 Evaluasi

Catat hasil pengujian, kesalahan yang terjadi, komentar pengguna, dan perbaikan yang diperlukan. Jangan menyatakan desain telah lolos usability testing sebelum pengujian benar-benar dilakukan.

---

## 15. Kriteria Penerimaan UI/UX

Desain UI/UX dinyatakan siap untuk implementasi apabila:

- Kebutuhan setiap role telah dipetakan.
    
- Struktur navigasi Android telah ditetapkan.
    
- Struktur navigasi website admin telah ditetapkan.
    
- Alur pendaftaran calon mahasiswa terdokumentasi.
    
- Alur pemeriksaan dan revisi terdokumentasi.
    
- Alur keputusan akhir mengikuti kewenangan yang ditetapkan.
    
- Layout utama telah dibuat dalam bentuk wireframe atau prototipe.
    
- Komponen mengikuti Design System.
    
- Semua layar penting memiliki kondisi loading, empty, error, dan success yang sesuai.
    
- Desain memperhatikan aksesibilitas dan responsivitas.
    
- Prototipe menggunakan data contoh yang aman.
    
- Pengujian usability direncanakan dan hasilnya didokumentasikan setelah dilakukan.
    
- Desain telah ditinjau terhadap PRD, SRS, dan API Contract.
    

---

## 16. Keputusan yang Masih Terbuka

Hal-hal berikut harus dipastikan sebelum desain final:

1. Logo dan identitas visual resmi ITB Akiday.
    
2. Daftar program studi resmi.
    
3. Ukuran minimum perangkat Android yang didukung.
    
4. Format dan ukuran maksimum dokumen.
    
5. Kebijakan revisi dan batas waktunya.
    
6. Aturan pengubahan data setelah pendaftaran dikirim.
    
7. Kewenangan final panitia, admin, dan pimpinan.
    
8. Kebutuhan notifikasi perubahan status.
    
9. Apakah penyimpanan draft lokal diperlukan.
    
10. Pilihan alat prototipe dan mekanisme pengujian pengguna.
    

---

## 17. Kesimpulan

Perancangan UI/UX PMB ITB Akiday berfokus pada kemudahan pendaftaran, kejelasan informasi, konsistensi visual, dan keamanan akses data. Aplikasi Android dirancang untuk membantu calon mahasiswa menyelesaikan proses pendaftaran dan memantau statusnya, sedangkan website admin mendukung pengelolaan data, pemeriksaan dokumen, pengambilan keputusan sesuai kewenangan, serta pelaporan.

Dokumen ini menjadi pedoman untuk membuat wireframe dan prototipe sebelum implementasi. Desain final harus divalidasi melalui pengujian antarmuka, pemeriksaan aksesibilitas, serta kesesuaian dengan kebutuhan sistem.

---

## Dokumen Terkait

- [[Project Overview]]
    
- [[PRD Final v1.0]]
    
- [[SRS]]
    
- [[Module Specification]]
    
- [[UI UX Design]]
    
- [[Design System]]
    
- [[Screen Specification]]
    
- [[API Contract]]
    
- [[Security Specification]]
    
- [[Access Control Matrix]]