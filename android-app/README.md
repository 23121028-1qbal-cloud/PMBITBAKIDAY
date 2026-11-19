# 📱 PMB ITB Akiday — Aplikasi Android Offline-First & REST API Integration

Aplikasi Android Penerimaan Mahasiswa Baru (**PMB ITB Akiday**) yang dibangun menggunakan **Kotlin**, **Jetpack Compose (Material3)**, dan **SQLite Database** untuk mendukung arsitektur **Offline-First**, serta terintegrasi penuh dengan **Laravel Sanctum REST API Backend**.

---

## 🚀 Fitur Utama & Arsitektur

### 1. Offline-First Architecture (SQLite Internal)
- **Penyimpanan Lokal**: Aplikasi menyimpan seluruh data calon mahasiswa ke database internal **SQLite HP** melalui DAO layer (`SQLiteApplicantDao`).
- **Akses Tanpa Internet**: Pengguna dapat mengisi, membaca, mengubah, dan menghapus data pendaftaran secara lokal tanpa bergantung pada koneksi internet.
- **Indikator Mode Offline / Online**:
  - Apabila di bagian atas aplikasi tertulis **"Data Calon Mahasiswa (CRUD & API)"** serta terdapat banner **"Integrasi Server Online: Masuk untuk sinkronisasi berkas dengan REST API. [Masuk Akun]"**, artinya pengguna berada dalam **Mode Belum Login (Offline-First)**.
  - Saat durasi belum login, tombol **"Simpan Pendaftaran Baru"** akan menyimpan data secara aman ke **SQLite lokal HP**.
  - Saat pengguna melakukan **Login Akun Online**, data pendaftaran otomatis dikirim dan tersinkronisasi dengan server backend Laravel via token otentikasi **Laravel Sanctum (`auth:sanctum`)**.

---

## 🛠️ Teknologi & Depedensi Utama

- **Bahasa Pemrograman**: Kotlin (Target SDK 37, Min SDK 33)
- **UI Framework**: Jetpack Compose (Material3 Design Tokens, Custom Dark Charcoal & Pastel Palette)
- **Database Lokal**: SQLite Native Helper (`ApplicantDatabaseHelper` & `SQLiteApplicantDao`)
- **Arsitektur**: MVVM (Model-View-ViewModel) dengan `ApplicantViewModel`, `StateFlow`, dan Kotlin Coroutines.
- **Networking**: Retrofit2 + Gson Converter + OkHttp Logging Interceptor
- **Keamanan Token**: AndroidX Security Crypto (`EncryptedSharedPreferences` / `EncryptedTokenManager`)

---

## 📝 10 Jenis Inputan Berbeda (Formulir Pendaftaran)

Formulir Pendaftaran calon mahasiswa pada aplikasi memenuhi persyaratan minimal 5 jenis inputan dengan menyediakan **10 jenis inputan berbeda**:

1. **Input Teks (Single-Line)**: Nama Lengkap
2. **Input Angka (16 Digit)**: NIK / KTP Calon Mahasiswa
3. **Input Teks**: Tempat Lahir
4. **Input Tanggal**: Tanggal Lahir (DatePicker Dialog Native)
5. **Input Pilihan Radio Button**: Jenis Kelamin (*Laki-laki* / *Perempuan*)
6. **Input Angka (Telepon)**: Nomor HP / WhatsApp
7. **Input Teks Multiline (Paragraf)**: Alamat Domisili Lengkap
8. **Input Pilihan Dropdown**: Program Studi Pilihan (*Teknik Informatika*, *Sistem Informasi*, *Bisnis Digital*, *Manajemen Informatika*)
9. **Input Gambar**: Unggah Pas Foto Calon Mahasiswa (`ActivityResultContracts.GetContent` Image Picker Launcher)
10. **Input Checkbox**: Persetujuan Pernyataan Syarat & Ketentuan Pendaftaran

---

## 🔄 Operasi CRUD (Create, Read, Update, Delete)

Aplikasi memiliki fungsionalitas CRUD lengkap yang bekerja langsung pada database SQLite lokal:

- **CREATE (Tambah Data)**: Mengisi formulir pendaftaran baru dan menyimpannya ke database SQLite lokal (serta mengirimkan draft online jika sudah login).
- **READ (Tampil Data)**:
  - **Read List**: Membaca dan menampilkan seluruh daftar pendaftar dalam bentuk kartu bernuansa pastel pada layar Beranda.
  - **Read Detail**: Membaca dan menampilkan rincian data lengkap calon mahasiswa di layar [`ApplicantDetailScreen`](file:///media/For_Laptop/Android_Development/AndroidStudioProjects/PMBITBAKIDAY/android-app/app/src/main/java/com/example/pmbitbakiday/ui/screen/ApplicantDetailScreen.kt) dengan menekan ikon mata 👁️ (*Lihat Detail*).
- **UPDATE (Ubah Data)**: Mengubah dan memperbarui data pendaftar yang sudah ada melalui tombol Edit ✏️.
- **DELETE (Hapus Data)**: Menghapus data pendaftar dari SQLite lokal melalui tombol Hapus 🗑️ dengan dialog konfirmasi keamanan.

---

## 📹 Skenario Demonstrasi Aplikasi (Petunjuk Pengambilan Video Demo)

Gunakan skenario berurutan berikut saat merekam video demonstrasi aplikasi:

### 🎬 Bagian 1: Pengenalan & Indikator Offline-First
1. Buka aplikasi **PMB ITB Akiday**.
2. Tunjukkan bagian header dan banner atas yang menampilkan status **"Integrasi Server Online: Masuk untuk sinkronisasi berkas dengan REST API"**.
3. Jelaskan bahwa aplikasi berada dalam **Mode Offline / Belum Login**, di mana seluruh operasi data akan disimpan ke **SQLite internal HP**.

### 🎬 Bagian 2: Demonstrasi CREATE (Tambah Data Baru & 10 Inputan)
1. Tekan tombol **`+ Tambah`** di kanan atas atau tab **Pendaftaran** pada Floating Navigation Bar bawah.
2. Isikan data dummy ke dalam formulir:
   - **Nama Lengkap**: *Norbert Wurttemberg*
   - **NIK**: *3201234567890001*
   - **Tempat Lahir**: *Bandung*
   - **Tanggal Lahir**: Tekan ikon kalender 📅 dan pilih tanggal (*31 Oktober 2008*).
   - **Jenis Kelamin**: Pilih Radio Button *Laki-laki*.
   - **Nomor HP/WA**: *081234567890*
   - **Alamat Domisili**: *Jl. Soekarno Hatta No. 123, Bandung*
   - **Program Studi**: Pilih Dropdown *Teknik Informatika*.
   - **Pas Foto**: Tekan *Pilih Berkas Foto* untuk memilih gambar dari galeri.
   - **Persetujuan Ketentuan**: Centang Checkbox persetujuan.
3. Tekan tombol **"Simpan Pendaftaran Baru"**.
4. Tunjukkan pesan toast keberhasilan dan data baru yang langsung muncul di daftar Beranda.

### 🎬 Bagian 3: Demonstrasi READ LIST & READ DETAIL
1. Pada layar Beranda, tunjukkan kartu data pendaftar yang baru disimpan (*Read List*).
2. Tekan tombol ikon mata 👁️ (**Lihat Detail**) atau tekan kartu pendaftar.
3. Tunjukkan layar **Detail Calon Mahasiswa (READ)** yang menampilkan rincian data pribadi, kontak, prodi, status foto, persetujuan, ID SQLite (`#1`), waktu pendaftaran, dan status integrasi server.
4. Tekan tombol **Kembali** (◀) di kiri atas.

### 🎬 Bagian 4: Demonstrasi UPDATE (Ubah Data)
1. Tekan tombol pensil ✏️ (**Edit Data**) pada kartu pendaftar atau di dalam layar detail.
2. Ubah Alamat Domisili menjadi: *Jl. Dago No. 45, Bandung* dan Program Studi menjadi *Sistem Informasi*.
3. Tekan tombol **"Perbarui Data Pendaftar"**.
4. Tunjukkan bahwa data pada Beranda dan Detail telah berhasil diperbarui.

### 🎬 Bagian 5: Demonstrasi DELETE (Hapus Data)
1. Tekan tombol tempat sampah 🗑️ (**Hapus Data**) pada kartu pendaftar.
2. Tunjukkan dialog konfirmasi hapus: *"Apakah Anda yakin ingin menghapus data calon mahasiswa 'Norbert Wurttemberg' dari database?"*.
3. Tekan **"Hapus"**.
4. Tunjukkan bahwa data pendaftar telah terhapus dari database SQLite dan layar kembali ke *Empty State*.

### 🎬 Bagian 6: Demonstrasi Integrasi REST API Server Online (Opsional/Nilai Tambah)
1. Tekan tab **Akun** pada Floating Navigation Bar bawah.
2. Pilih tab **Registrasi Baru**, masukan email (`norbertwurttemberg@gmail.com`) dan password (`12345678`), lalu tekan **Daftarkan Akun**.
3. Setelah registrasi berhasil, tekan **Masuk Sekarang** untuk login online via Laravel Sanctum REST API.
4. Tunjukkan banner status terhubung: **`TERHUBUNG KE SERVER: norbertwurttemberg@gmail.com`** dan status PMB online (**`Status PMB: DRAFT`**).

---

## 🛠️ Petunjuk Menjalankan Proyek

1. **Persiapan Port Forwarding ADB (Untuk Server Lokal Laptop)**:
   ```bash
   adb reverse tcp:8000 tcp:8000
   ```
2. **Kompilasi & Unit Test**:
   ```bash
   ./gradlew :app:testDebugUnitTest
   ./gradlew :app:assembleDebug
   ```
3. **Pemasangan APK ke Perangkat HP/Emulator**:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 📂 Struktur Direktori Proyek

```
android-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/pmbitbakiday/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/db/          # SQLite Database Helper & DAO
│   │   │   │   │   ├── model/             # DTO & Entity Models
│   │   │   │   │   ├── remote/            # Retrofit ApiService & AuthInterceptor
│   │   │   │   │   └── repository/        # Repositories (Base, Auth, Application, Profile)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── component/         # PmbFloatingBottomBar
│   │   │   │   │   ├── screen/            # List, Detail, Form, Status, & Auth Screens
│   │   │   │   │   ├── theme/             # Design Tokens, Colors, & Theme
│   │   │   │   │   └── viewmodel/         # ApplicantViewModel
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── PmbApplication.kt
│   │   │   └── res/                       # Adaptive Icons, Strings, Drawables, Themes
│   │   └── test/                          # 40 Unit Tests (DAO, Repositories, Serialization)
│   └── build.gradle.kts
└── build.gradle.kts
```
