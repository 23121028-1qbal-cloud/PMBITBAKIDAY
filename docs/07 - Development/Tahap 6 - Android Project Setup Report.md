# Tahap 6 — Android Project Setup Report
## Proyek PMB ITB Akiday

### 1. Ringkasan

Tahap 6 berfokus pada penyiapan fondasi proyek Android Kotlin di Android Studio sebagai basis pengembangan aplikasi calon mahasiswa PMB ITB Akiday. Pekerjaan ini meliputi audit workspace, verifikasi toolchain (JDK, Gradle, AGP, SDK), penataan arsitektur kode (MVVM / Clean Architecture), fondasi networking dan HTTP client (Retrofit, OkHttp, Gson), penanganan error sesuai katalog kontrak backend, mekanisme penyimpanan token terenkripsi (EncryptedSharedPreferences), proteksi keamanan jaringan (`network_security_config.xml`), pencegahan kebocoran kredensial pada logging, serta implementasi unit test fondasi dengan 100% tingkat kelulusan.

---

### 2. Tahap dan Cakupan

- **Tahap resmi:** **Tahap 6 — Android Project Setup**.
- **Cakupan yang dikerjakan:**
  1. Audit toolchain dan kompatibilitas dependensi.
  2. Penataan arsitektur direktori `data/local`, `data/model`, `data/remote`, `data/repository`, dan `util`.
  3. Konfigurasi HTTP client Retrofit + OkHttp dengan timeout 30 detik.
  4. Penyimpanan token Sanctum terenkripsi (`AuthTokenManager` dan `EncryptedTokenManager` menggunakan MasterKey Keystore AES-256-GCM).
  5. Interceptor autentikasi (`AuthInterceptor`) dengan validasi host tepercaya untuk mencegah kebocoran token ke pihak ketiga.
  6. Interceptor logging aman dengan redaksi header `Authorization` dan `Cookie`.
  7. Penanganan error dan pemetaan status HTTP (`NetworkResult` dan `BaseRepository`) sesuai dokumen `API Error Handling.md`.
  8. Konfigurasi multi-environment Base URL via `ApiConfig` dan `BuildConfig` (`10.0.2.2` untuk emulator, override untuk LAN perangkat fisik, HTTPS untuk produksi).
  9. Konfigurasi keamanan jaringan (`network_security_config.xml`) yang membatasi traffic cleartext hanya pada host lokal development.
  10. Eksekusi build (`compileDebugSources`, `assembleDebug`) dan pengujian unit test fondasi.
- **Batasan (Bukan cakupan Tahap 6):**
  - Implementasi penuh layar UI login, registrasi, profil, pemilihan gelombang/prodi, upload dokumen form, dan dashboard (dijadwalkan pada Tahap 7).
  - Instrumented test pada perangkat fisik/emulator nyata (tidak ada emulator/perangkat yang terhubung).

---

### 3. Kondisi Awal Workspace Sebelum Perubahan

1. Proyek Android berada di root workspace dengan modul tunggal `:app`.
2. Menggunakan View XML standar (ConstraintLayout, Material Components, AppCompat). Tidak menggunakan Jetpack Compose.
3. Gradle wrapper: Gradle 9.6.0.
4. Android Gradle Plugin (AGP): 9.4.1.
5. JDK: OpenJDK 23 (Debian).
6. Android SDK: Berada di `/media/federick/DATA_P4/Android_Development/Android/Sdk` (`platforms/android-36`, `platforms/android-37.0`, `build-tools/37.0.0`).
7. Belum tersedia library networking (Retrofit/OkHttp), belum ada serialisasi JSON, belum ada penyimpanan token terenkripsi, belum ada permission internet pada AndroidManifest, dan belum ada arsitektur repository.

---

### 4. Hasil Audit Toolchain

| Komponen | Versi Terdeteksi | Catatan Kompatibilitas |
|---|---|---|
| JDK | OpenJDK 23-ea (Debian) | Kompatibel dengan Gradle 9.6.0 |
| Gradle | 9.6.0 | Dijalankan via wrapper (`./gradlew`) |
| AGP | 9.4.1 | Built-in Kotlin support berjalan optimal |
| Compile SDK | 37 | Kompatibel dengan platform android-37.0 terpasang |
| Min SDK | 33 | Android 13+, mendukung penuh Keystore & EncryptedSharedPreferences |
| Target SDK | 37 | Android 15/16 preview |

---

### 5. Arsitektur Android Aktual

Mengikuti rekomendasi pada `System Design.md` Bagian 14:

```text
com.example.pmbitbakiday/
├── PmbApplication.kt               # Application entry point & service locator dasar
├── MainActivity.kt                 # Single Activity UI host
├── data/
│   ├── local/
│   │   ├── AuthTokenManager.kt      # Kontrak interface penyimpanan token aman
│   │   └── EncryptedTokenManager.kt # Implementasi EncryptedSharedPreferences (Keystore AES-256)
│   ├── model/
│   │   ├── ApiResponse.kt           # Amplop JSON standar {success, message, data, error, meta}
│   │   ├── AuthModels.kt            # DTO login, register, token, user
│   │   ├── ProfileModels.kt         # DTO profil calon mahasiswa
│   │   ├── ReferenceModels.kt       # DTO gelombang penerimaan & program studi
│   │   └── ApplicationModels.kt     # DTO pengajuan, dokumen, dan riwayat status
│   ├── remote/
│   │   ├── ApiConfig.kt             # Resolver Base URL & filter trusted host
│   │   ├── ApiService.kt            # Interface Retrofit untuk REST API Laravel
│   │   ├── AuthInterceptor.kt       # Injeksi Bearer token, deteksi 401 auto-logout
│   │   └── RetrofitClient.kt        # Factory OkHttpClient, Gson, dan Retrofit
│   └── repository/
│       ├── BaseRepository.kt        # Safe API call, pemetaan timeout, koneksi, & katalog error
│       ├── AuthRepository.kt        # Pengelolaan autentikasi & token session
│       ├── ProfileRepository.kt     # Akses data profil calon mahasiswa
│       └── ApplicationRepository.kt # Akses data pengajuan, berkas, & status
└── util/
    ├── NetworkResult.kt             # Sealed class state UI (Success, Error, Loading)
    └── NetworkUtils.kt              # Pengecekan koneksi internet aktif
```

---

### 6. File yang Dibuat dan Diubah

#### File Baru:
1. `app/src/main/res/xml/network_security_config.xml` — Konfigurasi keamanan jaringan.
2. `app/src/main/java/com/example/pmbitbakiday/PmbApplication.kt` — Application class.
3. `app/src/main/java/com/example/pmbitbakiday/data/model/ApiResponse.kt` — Model amplop API.
4. `app/src/main/java/com/example/pmbitbakiday/data/model/AuthModels.kt` — DTO autentikasi.
5. `app/src/main/java/com/example/pmbitbakiday/data/model/ProfileModels.kt` — DTO profil.
6. `app/src/main/java/com/example/pmbitbakiday/data/model/ReferenceModels.kt` — DTO data referensi.
7. `app/src/main/java/com/example/pmbitbakiday/data/model/ApplicationModels.kt` — DTO pendaftaran & dokumen.
8. `app/src/main/java/com/example/pmbitbakiday/data/local/AuthTokenManager.kt` — Interface token storage.
9. `app/src/main/java/com/example/pmbitbakiday/data/local/EncryptedTokenManager.kt` — Implementasi EncryptedSharedPreferences.
10. `app/src/main/java/com/example/pmbitbakiday/data/remote/ApiConfig.kt` — Konfigurasi Base URL.
11. `app/src/main/java/com/example/pmbitbakiday/data/remote/AuthInterceptor.kt` — OkHttp Auth interceptor.
12. `app/src/main/java/com/example/pmbitbakiday/data/remote/ApiService.kt` — Retrofit endpoint interface.
13. `app/src/main/java/com/example/pmbitbakiday/data/remote/RetrofitClient.kt` — Retrofit & OkHttp factory.
14. `app/src/main/java/com/example/pmbitbakiday/data/repository/BaseRepository.kt` — Safe API handler.
15. `app/src/main/java/com/example/pmbitbakiday/data/repository/AuthRepository.kt` — Repository autentikasi.
16. `app/src/main/java/com/example/pmbitbakiday/data/repository/ProfileRepository.kt` — Repository profil.
17. `app/src/main/java/com/example/pmbitbakiday/data/repository/ApplicationRepository.kt` — Repository aplikasi.
18. `app/src/main/java/com/example/pmbitbakiday/util/NetworkResult.kt` — Sealed class status jaringan.
19. `app/src/main/java/com/example/pmbitbakiday/util/NetworkUtils.kt` — Helper konektivitas internet.
20. `app/src/test/java/com/example/pmbitbakiday/data/model/ApiResponseSerializationTest.kt` — Unit test serialisasi.
21. `app/src/test/java/com/example/pmbitbakiday/data/remote/ApiConfigTest.kt` — Unit test konfigurasi URL.
22. `app/src/test/java/com/example/pmbitbakiday/data/remote/AuthInterceptorTest.kt` — Unit test interceptor & 401.
23. `app/src/test/java/com/example/pmbitbakiday/data/repository/BaseRepositoryTest.kt` — Unit test repository & MockWebServer.
24. `docs/07 - Development/Tahap 6 - Android Project Setup Report.md` — Laporan ini.

#### File yang Diubah:
1. `gradle/libs.versions.toml` — Menambahkan version catalog untuk Retrofit, OkHttp, Gson, Security Crypto, Coroutines, Lifecycle, dan MockWebServer.
2. `app/build.gradle.kts` — Menambahkan dependensi baru, mengaktifkan `buildFeatures { buildConfig = true }`, menambahkan `buildConfigField` untuk `BASE_URL`, dan mengarahkan kompatibilitas Java ke versi 17.
3. `app/src/main/AndroidManifest.xml` — Menambahkan permission `INTERNET` dan `ACCESS_NETWORK_STATE`, mengaktifkan `PmbApplication`, dan menghubungkan `network_security_config`.

---

### 7. Aspek Keamanan yang Diterapkan

1. **Penyimpanan Token:** Tidak menggunakan SharedPreferences biasa. Menggunakan `EncryptedSharedPreferences` dengan skema `AES256_SIV` untuk key dan `AES256_GCM` untuk value yang dilindungi oleh Android Keystore System.
2. **Perlindungan Terhadap Kebocoran Token:**
   - `AuthInterceptor` memeriksa `ApiConfig.isTrustedHost(requestUrl)`. Bearer token tidak akan dikirimkan apabila terjadi request ke domain atau host di luar backend API terkonfigurasi.
   - `HttpLoggingInterceptor` dikonfigurasi dengan `redactHeader("Authorization")` dan `redactHeader("Cookie")` sehingga token sesi tidak tercatat pada logcat.
3. **Keamanan Jaringan (Cleartext Traffic):**
   - File `network_security_config.xml` memberlakukan `cleartextTrafficPermitted="false"` secara default untuk seluruh domain (wajib HTTPS).
   - Traffic HTTP cleartext hanya diizinkan untuk host development lokal (`10.0.2.2`, `localhost`, `127.0.0.1`).
4. **Penanganan Otomatis Sesi Kedaluwarsa:** Respons HTTP 401 secara otomatis menghapus token dari storage dan memicu callback `onUnauthorized` untuk memandu user kembali ke alur login.

---

### 8. Pengujian dan Hasil Aktual

#### Unit Testing Fondasi (`./gradlew testDebugUnitTest`):
Eksekusi pengujian berjalan sukses tanpa kegagalan:
- `com.example.pmbitbakiday.ExampleUnitTest`: 1 passed.
- `com.example.pmbitbakiday.data.model.ApiResponseSerializationTest`: 4 passed.
  - Pengujian deserialisasi login sukses.
  - Pengujian deserialisasi error kredensial (`INVALID_CREDENTIALS`).
  - Pengujian deserialisasi error validasi beserta rincian field (`VALIDATION_ERROR`).
  - Pengujian deserialisasi daftar pendaftaran berpaginasi.
- `com.example.pmbitbakiday.data.remote.ApiConfigTest`: 3 passed.
  - Pengujian trailing slash normalizer.
  - Pengujian custom host override pada runtime.
  - Pengujian validasi host tepercaya (`isTrustedHost`).
- `com.example.pmbitbakiday.data.remote.AuthInterceptorTest`: 3 passed.
  - Pengujian penambahan header `Authorization: Bearer <token>` pada host tepercaya.
  - Pengujian tanpa header jika token kosong.
  - Pengujian penghapusan token otomatis dan pemicuan callback saat menerima HTTP 401.
- `com.example.pmbitbakiday.data.repository.BaseRepositoryTest`: 5 passed (MockWebServer).
  - Pengujian pemetaan respons 200 ke `NetworkResult.Success`.
  - Pengujian pemetaan respons 401 `INVALID_CREDENTIALS`.
  - Pengujian pemetaan respons 409 `STATE_CONFLICT`.
  - Pengujian pemetaan respons 422 `VALIDATION_ERROR` beserta rincian pesan error bahasa Indonesia.
  - Pengujian fallback error server HTTP 500.

**Total Unit Test:** 16 tests, 16 passed, 0 failed, 0 errors (**100% Pass**).

#### Build Debug APK (`./gradlew assembleDebug`):
- Perintah: `./gradlew assembleDebug`
- Status: **BUILD SUCCESSFUL in 1m 32s**.
- Lokasi Output: `app/build/outputs/apk/debug/app-debug.apk` (Ukuran: 18.7 MB).

#### Instrumented Test:
- Perintah: `adb devices`
- Status: Tidak ada emulator atau perangkat fisik Android yang terhubung ke host saat audit dilakukan. Oleh karena itu, instrumented test ditandai belum dilakukan sesuai panduan batas cakupan.

---

### 9. Status Akhir dan Rekomendasi Checkpoint

* **Status Tahap 6:** **VERIFIED**
* **Rekomendasi Checkpoint:** **ACCEPTED**

Fondasi arsitektur, komunikasi data, keamanan token, dan penanganan respons telah selesai dan terverifikasi secara penuh. Proyek siap dilanjutkan ke **Tahap 7 — Android Core Features** (pembangunan layar UI dan alur bisnis pendaftaran calon mahasiswa).
