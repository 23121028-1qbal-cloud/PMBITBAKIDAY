# Test Plan — PMB ITB Akiday

## 1. Informasi Dokumen

|Informasi|Keterangan|
|---|---|
|Nama Proyek|PMB ITB Akiday|
|Nama Dokumen|Test Plan|
|Versi Dokumen|1.0|
|Status|Draft untuk Pengujian|
|Sistem yang Diuji|Aplikasi Android, Backend API, Website Admin|
|Backend|Laravel|
|Database|MySQL|
|Aplikasi Mobile|Kotlin|
|Metode Pengujian|Black Box Testing, Functional Testing, Integration Testing, Security Testing|
|Dokumen Terkait|SRS, Module Specification, API Contract, Database Model, Security Specification, Test Cases|

---

## 2. Tujuan Pengujian

Test Plan disusun untuk menentukan bagaimana sistem PMB ITB Akiday akan diuji sebelum digunakan. Pengujian dilakukan untuk memastikan bahwa fungsi aplikasi berjalan sesuai kebutuhan, data pendaftaran tersimpan dengan benar, dan setiap pengguna hanya dapat mengakses fitur sesuai hak aksesnya.

Sistem PMB ITB Akiday memiliki tiga komponen utama, yaitu aplikasi Android untuk calon mahasiswa, backend Laravel sebagai pemroses data, dan website admin untuk mengelola proses penerimaan mahasiswa baru. Ketiga komponen tersebut harus diuji secara terpisah maupun terintegrasi karena kesalahan pada salah satu komponen dapat memengaruhi keseluruhan proses pendaftaran.

Pengujian juga berfokus pada masalah penting, seperti pendaftaran ganda pada gelombang yang sama, kegagalan unggah dokumen, perubahan status yang tidak sah, dan akses tidak berwenang terhadap data pribadi calon mahasiswa.

Hasil pengujian akan digunakan untuk menentukan apakah sistem siap melanjutkan ke tahap integrasi, evaluasi, atau deployment lokal.

## 3. Deskripsi Sistem yang Diuji

PMB ITB Akiday merupakan sistem penerimaan mahasiswa baru yang dirancang untuk mengelola proses pendaftaran secara terpusat.

Sistem memiliki empat role utama:

|Role|Tanggung Jawab|
|---|---|
|Calon Mahasiswa|Membuat akun, mengisi profil, memilih gelombang dan program studi, mengunggah dokumen, serta memantau status pendaftaran.|
|Admin|Mengelola akun pengguna, gelombang pendaftaran, program studi, dan kebutuhan administrasi sesuai kewenangannya.|
|Panitia|Memeriksa pendaftaran, memeriksa dokumen, meminta revisi, dan mengambil keputusan jika memiliki kewenangan.|
|Pimpinan|Melihat dashboard dan laporan sesuai hak akses yang diberikan.|

Alur utama yang menjadi fokus pengujian:

1. Calon mahasiswa membuat akun dan login.
2. Calon mahasiswa melengkapi profil.
3. Calon mahasiswa memilih gelombang pendaftaran yang tersedia.
4. Calon mahasiswa memilih satu program studi.
5. Calon mahasiswa mengunggah dokumen persyaratan.
6. Sistem memvalidasi kelengkapan data.
7. Calon mahasiswa mengirim pendaftaran.
8. Panitia memeriksa data dan dokumen.
9. Panitia meminta revisi jika diperlukan.
10. Pihak yang berwenang menetapkan keputusan.
11. Calon mahasiswa memantau status dan riwayat pendaftaran.
12. Admin atau pimpinan melihat laporan sesuai kewenangan.

**Aturan bisnis penting:** Satu akun hanya boleh memiliki satu pendaftaran pada gelombang yang sama. Akun dapat memiliki pendaftaran pada gelombang berbeda jika gelombang tersebut tersedia dan kebijakan sistem mengizinkannya.

Keputusan diterima atau ditolak dilakukan oleh pihak yang berwenang. Sistem hanya membantu validasi kelengkapan dan format data, bukan mengambil keputusan akademik secara otomatis.

## 4. Ruang Lingkup Pengujian

### 4.1 Pengujian yang Termasuk

Pengujian mencakup hal-hal berikut.

|No.|Area|Fokus Pengujian|
|---|---|---|
|1|Autentikasi|Registrasi, login, logout, dan penolakan kredensial yang salah.|
|2|Profil|Pengisian, penyimpanan, dan validasi data calon mahasiswa.|
|3|Gelombang|Penampilan gelombang serta validasi periode pendaftaran.|
|4|Program Studi|Pemilihan program studi yang tersedia dan aktif.|
|5|Pendaftaran|Pembuatan, penyimpanan, dan pengiriman pendaftaran.|
|6|Dokumen|Unggah, validasi, akses, dan pengunduhan dokumen.|
|7|Status|Pemantauan status dan riwayat perubahan status.|
|8|Pemeriksaan|Pemeriksaan data oleh panitia dan permintaan revisi.|
|9|Keputusan|Penetapan diterima atau ditolak oleh pihak berwenang.|
|10|Administrasi|Pengelolaan akun, gelombang, dan program studi.|
|11|Dashboard|Statistik pendaftaran dan ringkasan data.|
|12|Laporan|Filter laporan dan pengunduhan PDF.|
|13|Keamanan|Autentikasi, otorisasi, validasi input, dan perlindungan dokumen.|
|14|Database|Relasi tabel, constraint, transaksi, dan konsistensi data.|
|15|Integrasi|Komunikasi Android, backend API, dan database.|
|16|Ketahanan|Penanganan koneksi terputus, server tidak tersedia, dan respons lambat.|
|17|Antarmuka|Navigasi, validasi formulir, keterbacaan, dan responsivitas tampilan.|

### 4.2 Pengujian yang Tidak Termasuk

Hal-hal berikut belum menjadi fokus pengujian pada tahap awal:

- Pengujian performa berskala sangat besar dengan ribuan pengguna bersamaan.
- Audit keamanan independen atau penetration test profesional.
- Pengujian integrasi dengan sistem eksternal yang belum ditetapkan.
- Pengujian pembayaran karena proses pendaftaran dirancang gratis.
- Pengujian pengambilan keputusan akademik otomatis karena keputusan penerimaan dilakukan manusia.
- Pengujian deployment produksi skala besar karena tahap awal berfokus pada lingkungan lokal.

Pengujian tambahan dapat dilakukan jika kebutuhan sistem dan lingkungan deployment sudah ditetapkan.

## 5. Strategi Pengujian

Pengujian dilakukan secara bertahap agar masalah dapat ditemukan sejak awal dan tidak menumpuk menjelang akhir pengembangan.

### 5.1 Unit Testing

Unit testing digunakan untuk memeriksa bagian kode secara terpisah, misalnya fungsi validasi data, service pendaftaran, dan fungsi pembentukan nomor pendaftaran.

Contoh pengujian:

- Validasi format NIK.
- Validasi kelengkapan profil.
- Validasi periode gelombang.
- Pemeriksaan aturan perubahan status.
- Validasi format respons atau fungsi pembantu.

Alat yang dapat digunakan:

- PHPUnit atau Pest untuk backend Laravel.
- JUnit atau framework pengujian yang sesuai untuk Kotlin.

**Tujuan:** Memastikan fungsi individual berjalan sesuai aturan yang diharapkan.

### 5.2 Integration Testing

Integration testing dilakukan untuk memeriksa interaksi antarkomponen, terutama antara backend, database, dan aplikasi Android.

Contoh pengujian:

- Android mengirim data login ke API.
- Backend memverifikasi akun melalui database.
- Backend menyimpan pendaftaran dan riwayat status dalam transaksi yang sesuai.
- Aplikasi Android menampilkan status berdasarkan respons API.
- Dokumen diunggah dan metadata tersimpan pada pendaftaran yang benar.

**Tujuan:** Memastikan komponen sistem dapat berkomunikasi dan bertukar data dengan benar.

### 5.3 Black Box Testing

Black box testing dilakukan dengan memeriksa input dan output sistem tanpa bergantung pada detail implementasi internal.

Contoh pengujian:

- Login dengan password yang benar dan salah.
- Mengirim pendaftaran dengan data lengkap dan tidak lengkap.
- Mengunggah file yang valid dan tidak valid.
- Mengakses endpoint tanpa autentikasi.
- Melakukan tindakan admin menggunakan akun yang tidak berwenang.

**Tujuan:** Memastikan perilaku sistem sesuai kebutuhan pengguna.

### 5.4 System Testing

System testing dilakukan pada sistem yang sudah terintegrasi.

Pengujian mencakup alur lengkap dari pembuatan akun sampai calon mahasiswa dapat melihat keputusan penerimaan.

**Tujuan:** Memastikan seluruh komponen bekerja bersama sebagai satu sistem.

### 5.5 Security Testing

Security testing berfokus pada perlindungan data dan pembatasan hak akses.

Contoh pengujian:

- Pengguna tidak dapat melihat pendaftaran milik akun lain.
- Calon mahasiswa tidak dapat mengakses endpoint admin.
- Panitia hanya dapat menjalankan tindakan sesuai kewenangan.
- Dokumen pribadi tidak dapat diunduh oleh pengguna yang tidak berhak.
- Request tanpa token ditolak.
- Data input yang tidak valid tidak diproses.
- Rate limiting bekerja sesuai konfigurasi.

**Tujuan:** Mengurangi risiko akses ilegal, manipulasi data, dan kebocoran informasi pribadi.

### 5.6 Concurrency Testing

Concurrency testing digunakan untuk memeriksa apakah sistem tetap konsisten ketika beberapa request dilakukan pada waktu yang hampir bersamaan.

Skenario penting adalah ketika satu akun mengirim dua request pembuatan pendaftaran untuk gelombang yang sama.

Pengujian dilakukan dengan mengirim request secara bersamaan dan memeriksa hasilnya pada database.

**Hasil yang diharapkan:** Maksimal satu pendaftaran tersimpan untuk pasangan `applicant_id` dan `wave_id` yang sama.

Pencegahan duplikasi harus didukung oleh unique constraint database dan penanganan konflik pada backend, bukan hanya pemeriksaan di antarmuka aplikasi.

### 5.7 Compatibility Testing

Compatibility testing digunakan untuk memeriksa apakah aplikasi tetap dapat digunakan pada lingkungan yang didukung.

Contoh:

- Menjalankan aplikasi pada beberapa ukuran layar Android.
- Memeriksa tampilan website admin pada browser modern.
- Memastikan tombol, teks, dan formulir tetap dapat digunakan.
- Memeriksa tampilan ketika jaringan lambat.

Versi Android minimum dan daftar browser yang didukung harus ditetapkan sebelum pengujian kompatibilitas final.

### 5.8 Regression Testing

Regression testing dilakukan setelah bug diperbaiki atau fitur diubah.

Pengujian ulang diperlukan untuk memastikan perubahan tidak merusak fungsi yang sebelumnya sudah berjalan.

Contoh:

Setelah aturan duplikasi pendaftaran diperbaiki, jalankan kembali pengujian pembuatan pendaftaran, pengiriman pendaftaran, dan pendaftaran pada gelombang berbeda.

## 6. Prioritas Pengujian

|Prioritas|Keterangan|Contoh|
|---|---|---|
|P1 — Tinggi|Berpengaruh terhadap keamanan, integritas data, atau alur utama sistem.|Login, pengiriman pendaftaran, duplikasi data, akses dokumen, keputusan penerimaan.|
|P2 — Sedang|Berpengaruh terhadap fitur penting pendukung.|Filter laporan, dashboard, penanganan koneksi gagal.|
|P3 — Rendah|Berpengaruh terutama terhadap kenyamanan penggunaan.|Penyesuaian tampilan dan detail visual.|

Urutan pelaksanaan pengujian dimulai dari fungsi P1, kemudian P2, dan dilanjutkan P3. Namun, pengujian prioritas rendah tetap perlu dilakukan sebelum rilis jika berkaitan dengan persyaratan yang telah disepakati.

## 7. Lingkungan Pengujian

Lingkungan pengujian menggunakan perangkat dan perangkat lunak yang tersedia selama pengembangan.

|Komponen|Lingkungan|
|---|---|
|Sistem Operasi|Windows atau Linux|
|Aplikasi Mobile|Android Emulator atau perangkat Android fisik|
|Backend|Laravel pada server lokal|
|Database|MySQL|
|API Testing|Postman atau alat sejenis|
|IDE Android|Android Studio|
|Backend Editor|Visual Studio Code atau editor sejenis|
|Browser Admin|Chrome atau browser modern yang didukung|
|Version Control|Git|
|Data Pengujian|Data dummy|

Contoh alamat backend lokal:

```
http://localhost:8000/api/v1
```

Untuk Android Emulator standar yang mengakses komputer host:

```
http://10.0.2.2:8000/api/v1
```

Konfigurasi tersebut hanya berlaku untuk skenario pengembangan lokal. Jika sistem dipindahkan ke server lain, alamat API dan pengaturan lingkungan harus disesuaikan.

### 7.1 Persiapan Lingkungan

Sebelum pengujian dimulai, pastikan:

1. Backend Laravel dapat dijalankan.
2. Database MySQL tersedia.
3. Migration sudah diterapkan pada database pengujian.
4. Data dummy dan akun pengujian telah disiapkan.
5. Aplikasi Android dapat dijalankan.
6. Website admin dapat diakses.
7. Endpoint API dapat diuji.
8. Konfigurasi autentikasi sudah aktif.
9. Penyimpanan dokumen telah dikonfigurasi secara privat.
10. Log aplikasi dapat diperiksa tanpa mengekspos data pribadi.

Database pengujian harus dipisahkan dari database yang menyimpan data penting. Hindari menjalankan pengujian destruktif pada database produksi.

## 8. Data dan Skenario Pengujian

Data pengujian menggunakan data dummy yang tidak mengandung informasi pribadi nyata.

Jenis data yang perlu disiapkan:

|Jenis Data|Contoh|
|---|---|
|Akun valid|Akun calon mahasiswa aktif|
|Akun nonaktif|Akun yang dinonaktifkan|
|Akun admin|Akun dengan hak akses administratif|
|Akun panitia|Akun untuk pemeriksaan pendaftaran|
|Akun pimpinan|Akun untuk melihat laporan|
|Data profil valid|Nama, NIK dummy, tanggal lahir, dan alamat fiktif|
|Data profil tidak valid|NIK dengan format salah atau field wajib kosong|
|Gelombang aktif|Gelombang yang berada dalam periode pendaftaran|
|Gelombang belum dibuka|Gelombang dengan tanggal mulai di masa mendatang|
|Gelombang berakhir|Gelombang yang melewati tanggal akhir|
|Dokumen valid|File dummy dengan format yang diizinkan|
|Dokumen tidak valid|File dengan format tidak didukung atau ukuran berlebihan|
|Pendaftaran duplikat|Dua request dari akun dan gelombang yang sama|

Semua data harus disiapkan secara konsisten agar hasil pengujian dapat diulang.

## 9. Prosedur Pelaksanaan Pengujian

Pelaksanaan pengujian dilakukan melalui tahapan berikut.

### Tahap 1 — Persiapan

- Memeriksa kebutuhan sistem.
- Memeriksa dokumentasi API.
- Menyiapkan perangkat dan lingkungan pengujian.
- Menyiapkan akun serta data dummy.
- Memastikan database pengujian dapat digunakan.
- Menentukan penguji dan jadwal.

### Tahap 2 — Pengujian Unit

- Menguji fungsi validasi.
- Menguji logika pendaftaran.
- Menguji aturan status.
- Menguji fungsi pendukung yang kritis.

### Tahap 3 — Pengujian API dan Database

- Menguji endpoint autentikasi.
- Menguji endpoint profil dan pendaftaran.
- Menguji unggah dokumen.
- Menguji relasi dan constraint database.
- Menguji penanganan error.
- Menguji pendaftaran duplikat dan request bersamaan.

### Tahap 4 — Pengujian Aplikasi dan Website

- Menguji alur calon mahasiswa melalui Android.
- Menguji pengelolaan pendaftaran melalui website admin.
- Menguji pembagian akses berdasarkan role.
- Menguji tampilan, validasi formulir, dan navigasi.

### Tahap 5 — Pengujian Sistem

- Menjalankan alur pendaftaran secara menyeluruh.
- Memeriksa perubahan status.
- Memeriksa riwayat pendaftaran.
- Memeriksa dashboard dan laporan.
- Menguji skenario kegagalan jaringan.

### Tahap 6 — Pengujian Keamanan

- Menguji akses tanpa token.
- Menguji akses data pengguna lain.
- Menguji pembatasan role.
- Menguji perlindungan dokumen.
- Menguji validasi file dan input.
- Menguji rate limiting sesuai konfigurasi.

### Tahap 7 — Regression Testing

- Menjalankan kembali test case yang terdampak setelah perbaikan bug.
- Memeriksa bahwa fitur utama tetap berfungsi.
- Mencatat hasil pengujian ulang.

### Tahap 8 — Evaluasi

- Mengumpulkan hasil pengujian.
- Mengelompokkan bug berdasarkan tingkat keparahan.
- Memeriksa kriteria kelulusan.
- Menyusun laporan hasil pengujian.
- Memberikan rekomendasi kesiapan sistem.

## 10. Jadwal Pengujian

Jadwal berikut merupakan contoh rencana untuk satu siklus pengujian. Waktu aktual dapat disesuaikan dengan progres pengembangan dan jumlah anggota tim.

|Tahap|Kegiatan|Estimasi|
|---|---|---|
|1|Persiapan lingkungan dan data pengujian|Hari 1|
|2|Unit testing|Hari 2|
|3|Pengujian API dan database|Hari 3|
|4|Pengujian aplikasi Android dan website admin|Hari 4|
|5|Pengujian integrasi dan alur sistem|Hari 5|
|6|Pengujian keamanan dan concurrency|Hari 6|
|7|Perbaikan bug dan regression testing|Hari 7–8|
|8|Evaluasi dan penyusunan laporan|Hari 9|

Estimasi tersebut bukan tanggal pelaksanaan yang sudah dikonfirmasi. Pengujian hanya dapat dilakukan setelah komponen terkait tersedia dan siap diuji.

## 11. Pembagian Tanggung Jawab

Pembagian tugas pengujian dapat dilakukan sebagai berikut.

|Peran|Tanggung Jawab|
|---|---|
|Project Coordinator|Mengatur jadwal, prioritas, dan evaluasi hasil pengujian.|
|Android Developer|Menguji aplikasi Android dan memperbaiki bug pada aplikasi.|
|Backend Developer|Menguji API, validasi, autentikasi, dan logika bisnis.|
|Database Designer/Developer|Memeriksa relasi, constraint, migration, dan konsistensi data.|
|Admin Website Developer|Menguji fitur administrasi, pemeriksaan, dan laporan.|
|Tester|Menjalankan test case, mencatat hasil, dan melaporkan bug.|

Dalam tim kecil, satu orang dapat memegang beberapa peran. Namun, hasil pengujian sebaiknya tetap diperiksa secara objektif, terutama untuk fungsi keamanan dan keputusan penerimaan.

## 12. Kriteria Mulai Pengujian

Pengujian dapat dimulai ketika:

- [ ] Kebutuhan fitur yang akan diuji sudah tersedia.
- [ ] Test case terkait sudah ditulis.
- [ ] Lingkungan pengujian siap.
- [ ] Database pengujian dapat diakses.
- [ ] Akun pengujian tersedia.
- [ ] Backend dan aplikasi dapat dijalankan untuk area yang diuji.
- [ ] Konfigurasi penyimpanan dokumen tersedia untuk pengujian dokumen.
- [ ] Penguji memahami langkah dan hasil yang diharapkan.
- [ ] Data pengujian tidak menggunakan data pribadi calon mahasiswa yang sebenarnya.

Jika salah satu prasyarat penting belum terpenuhi, pengujian yang bergantung padanya dapat ditandai sebagai `Blocked`.

## 13. Kriteria Kelulusan

Sistem dapat dipertimbangkan memenuhi kriteria pengujian apabila:

1. Seluruh test case P1 telah dijalankan.
2. Tidak ada kegagalan kritis yang belum diselesaikan.
3. Registrasi dan login berfungsi sesuai kebutuhan.
4. Calon mahasiswa dapat melengkapi profil dan mengirim pendaftaran yang valid.
5. Sistem menolak pengiriman pendaftaran yang tidak lengkap.
6. Pendaftaran duplikat pada gelombang yang sama berhasil dicegah, termasuk pada request bersamaan.
7. Dokumen dapat diunggah dan diakses sesuai hak pengguna.
8. Perubahan status hanya dilakukan melalui alur dan kewenangan yang sah.
9. Riwayat status dan keputusan tersimpan konsisten.
10. Hak akses admin, panitia, pimpinan, dan calon mahasiswa berjalan sesuai ketentuan.
11. Statistik dan laporan sesuai dengan data yang menjadi sumbernya.
12. Pengujian integrasi antara Android, backend, dan database berhasil.
13. Bug yang ditemukan telah dicatat dan ditindaklanjuti.
14. Bukti pengujian tersedia dan hasilnya dapat ditelusuri.

Pengujian tidak boleh dianggap lulus hanya karena aplikasi dapat dibuka atau tidak menampilkan error. Fungsi, keamanan, dan konsistensi data tetap harus diperiksa.

## 14. Kriteria Penghentian dan Pengujian Ulang

Pengujian pada suatu area dapat dihentikan sementara jika:

- Backend tidak dapat dijalankan.
- Database pengujian mengalami kerusakan.
- Data pengujian tidak konsisten.
- Fitur utama belum diimplementasikan.
- Terdapat bug yang membuat rangkaian pengujian lain tidak dapat dilakukan.
- Lingkungan pengujian berisiko merusak data yang tidak seharusnya diubah.

Pengujian dapat dilanjutkan setelah penyebab masalah diperbaiki.

Pengujian ulang dilakukan apabila:

- Bug sudah diperbaiki.
- Ada perubahan pada logika bisnis.
- Ada perubahan migration atau struktur database.
- Ada perubahan kontrak API.
- Ada perubahan kontrol akses.
- Ada perubahan pada proses autentikasi atau penyimpanan dokumen.

Setelah perbaikan, jalankan test case yang gagal dan test case lain yang berpotensi terdampak.

## 15. Klasifikasi Bug

Bug dikelompokkan berdasarkan dampaknya terhadap sistem.

|Tingkat|Deskripsi|Contoh|
|---|---|---|
|Critical|Mengakibatkan pelanggaran keamanan serius, kerusakan data utama, atau kegagalan total proses penting.|Pengguna dapat membaca dokumen pribadi seluruh calon mahasiswa; data pendaftaran rusak secara luas.|
|High|Mengganggu fungsi utama atau menghasilkan keputusan/data yang salah.|Dua pendaftaran tercipta untuk akun dan gelombang yang sama; status berubah tanpa otorisasi.|
|Medium|Mengganggu fitur tertentu tetapi masih terdapat cara lain untuk menyelesaikan sebagian proses.|Filter laporan tidak berfungsi; sebagian validasi formulir salah.|
|Low|Dampak terbatas pada tampilan atau kenyamanan.|Jarak antarelemen tidak konsisten; teks kurang rapi.|

Prioritas perbaikan ditentukan berdasarkan tingkat keparahan, dampak terhadap pengguna, dan risiko keamanan.

Bug Critical dan High yang memengaruhi keamanan atau integritas data harus diselesaikan sebelum sistem dinyatakan siap digunakan.

## 16. Risiko Pengujian

|Risiko|Dampak|Mitigasi|
|---|---|---|
|Fitur belum selesai|Test case tidak dapat dijalankan|Prioritaskan fitur yang sudah siap dan tandai sisanya `Blocked`.|
|Data pengujian tidak konsisten|Hasil pengujian sulit diulang|Gunakan data dummy dan prosedur persiapan yang terdokumentasi.|
|Perubahan API|Aplikasi Android tidak kompatibel|Perbarui API Contract dan jalankan regression testing.|
|Konflik migration|Struktur database berbeda antarlingkungan|Gunakan migration sebagai sumber perubahan skema.|
|Data pribadi masuk ke pengujian|Risiko kebocoran informasi|Gunakan data dummy dan database pengujian terpisah.|
|Pengujian concurrency tidak memadai|Duplikasi pendaftaran tidak terdeteksi|Uji request bersamaan dan verifikasi unique constraint database.|
|Pengujian keamanan hanya melalui UI|Celah akses API terlewat|Uji endpoint secara langsung dengan token dan role yang berbeda.|
|Perbaikan bug merusak fitur lain|Regression bug|Jalankan ulang test case terkait setelah perubahan.|
|Tidak ada bukti pengujian|Hasil sulit diverifikasi|Catat hasil aktual, waktu pengujian, dan bukti pendukung.|

## 17. Dokumentasi Hasil Pengujian

Seluruh hasil pengujian dicatat dalam dokumen `Test Results.md`.

Informasi yang perlu dicatat:

- ID test case.
- Tanggal pengujian.
- Nama atau identitas penguji sesuai kebutuhan dokumentasi.
- Lingkungan dan versi aplikasi yang diuji.
- Langkah pengujian yang dijalankan.
- Hasil yang diharapkan.
- Hasil aktual.
- Status pengujian.
- Bukti pengujian.
- ID bug jika ditemukan.
- Status perbaikan dan hasil pengujian ulang.

Status hasil pengujian:

|Status|Arti|
|---|---|
|Not Run|Belum dijalankan.|
|Pass|Hasil aktual sesuai dengan hasil yang diharapkan.|
|Fail|Hasil aktual tidak sesuai dengan hasil yang diharapkan.|
|Blocked|Pengujian belum dapat dilanjutkan karena kendala atau ketergantungan.|

Hasil pengujian harus mencerminkan kondisi nyata. Test case yang belum dijalankan tidak boleh dicatat sebagai `Pass`.

## 18. Metrik Pengujian

Metrik digunakan untuk mengevaluasi kemajuan dan hasil pengujian.

### 18.1 Persentase Test Case yang Dijalankan

\[ \text{Execution Rate} = \frac{\text{Jumlah test case yang sudah dijalankan}} {\text{Jumlah seluruh test case yang direncanakan}} \times 100\% \]

### 18.2 Persentase Kelulusan

\[ \text{Pass Rate} = \frac{\text{Jumlah test case Pass}} {\text{Jumlah test case yang sudah dijalankan}} \times 100\% \]

Test case berstatus `Not Run` dan `Blocked` tidak dihitung sebagai lulus.

### 18.3 Jumlah Bug Berdasarkan Tingkat Keparahan

Catat jumlah bug Critical, High, Medium, dan Low yang ditemukan serta jumlah yang sudah diselesaikan.

### 18.4 Cakupan Kebutuhan

Cakupan kebutuhan digunakan untuk memeriksa berapa banyak kebutuhan dalam SRS yang sudah memiliki test case dan sudah diuji.

Metrik tersebut membantu evaluasi, tetapi tidak boleh digunakan sendirian untuk menyatakan sistem aman atau siap digunakan. Kegagalan kritis tetap harus diperhatikan meskipun pass rate tinggi.

## 19. Dokumen yang Berhubungan

|Dokumen|Hubungan|
|---|---|
|`SRS.md`|Menjadi sumber kebutuhan fungsional dan nonfungsional yang diuji.|
|`Traceability Matrix.md`|Menghubungkan kebutuhan dengan test case dan hasil pengujian.|
|`Module Specification.md`|Menjelaskan modul yang perlu diuji.|
|`API Contract.md`|Menjadi acuan endpoint, request, response, dan kode status API.|
|`API Error Handling.md`|Menjadi acuan pengujian penanganan error.|
|`Database Model.md`|Menjadi acuan struktur entitas dan relasi database.|
|`Security Specification.md`|Menjadi acuan pengujian keamanan.|
|`Access Control Matrix.md`|Menjadi acuan hak akses setiap role.|
|`Test Cases.md`|Berisi langkah-langkah pengujian terperinci.|
|`Test Results.md`|Digunakan untuk mencatat hasil aktual pengujian.|

## 20. Kesimpulan

Test Plan PMB ITB Akiday menetapkan strategi pengujian untuk memastikan bahwa aplikasi Android, backend Laravel, website admin, dan database MySQL berfungsi sesuai kebutuhan. Pengujian dilakukan secara bertahap melalui unit testing, integration testing, black box testing, system testing, security testing, concurrency testing, dan regression testing.

Fokus utama pengujian adalah memastikan proses pendaftaran berjalan dengan benar, dokumen terlindungi, hak akses sesuai role, keputusan penerimaan dilakukan oleh pihak berwenang, serta data tetap konsisten ketika terjadi request bersamaan atau kegagalan koneksi.

Dokumen ini menjadi pedoman pelaksanaan pengujian. Status kelulusan sistem baru dapat ditentukan setelah test case benar-benar dijalankan, hasilnya dicatat, dan seluruh masalah kritis ditindaklanjuti.