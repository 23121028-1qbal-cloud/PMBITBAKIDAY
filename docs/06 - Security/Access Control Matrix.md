
**Proyek:** PMB ITB Akiday  
**Versi:** 1.0  
**Status:** Draft untuk implementasi  
**Folder Obsidian:** `06 - Security/Access Control Matrix.md`

---

## 1. Tujuan

Dokumen ini menjelaskan pembagian hak akses setiap pengguna pada sistem Penerimaan Mahasiswa Baru (PMB) ITB Akiday. Tujuannya adalah memastikan bahwa setiap pengguna hanya dapat mengakses fitur dan data sesuai dengan tugas serta tanggung jawabnya.

Sistem menerapkan **Role-Based Access Control (RBAC)**, yaitu mekanisme pengaturan akses berdasarkan peran pengguna. Dengan pendekatan ini, hak akses tidak diberikan secara bebas kepada setiap pengguna, tetapi ditentukan berdasarkan role yang dimiliki.

Selain membatasi akses fitur, sistem juga harus memastikan bahwa pengguna tidak dapat melihat, mengubah, atau menghapus data yang bukan menjadi kewenangannya.

## 2. Daftar Role Pengguna

Sistem PMB ITB Akiday memiliki empat role utama.

|Kode Role|Nama Role|Deskripsi|
|---|---|---|
|ROLE-01|`calon_mahasiswa`|Pengguna yang mendaftar sebagai calon mahasiswa dan memantau proses pendaftarannya sendiri.|
|ROLE-02|`admin`|Pengguna yang mengelola akun, data master, konfigurasi sistem, dan operasional administrasi sesuai kewenangannya.|
|ROLE-03|`panitia`|Pengguna yang memeriksa kelengkapan berkas, melakukan verifikasi, meminta revisi, serta memproses keputusan penerimaan sesuai kewenangan yang diberikan.|
|ROLE-04|`pimpinan`|Pengguna yang melihat dashboard, statistik, dan laporan untuk mendukung pengambilan keputusan.|

**Catatan:** Hak akses pada dokumen ini merupakan rancangan awal. Hak untuk menetapkan keputusan akhir penerimaan perlu dikonfirmasi dengan aturan organisasi PMB yang sebenarnya.

## 3. Keterangan Matriks Akses

Matriks menggunakan simbol berikut:

|Simbol|Arti|
|---|---|
|**V**|Dapat melihat data.|
|**C**|Dapat membuat data.|
|**U**|Dapat mengubah data.|
|**D**|Dapat menghapus data.|
|**A**|Dapat melakukan tindakan khusus atau persetujuan sesuai kewenangan.|
|**—**|Tidak memiliki akses secara langsung.|

Hak akses yang ditampilkan tetap harus dibatasi berdasarkan kepemilikan data, status pendaftaran, dan aturan bisnis. Sebagai contoh, calon mahasiswa dapat melihat data pendaftarannya sendiri, tetapi tidak boleh melihat pendaftaran milik pengguna lain.

## 4. Matriks Hak Akses Fungsional

### 4.1 Autentikasi dan Akun

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Registrasi akun calon mahasiswa|C|—|—|—|
|Login ke sistem|A|A|A|A|
|Logout|A|A|A|A|
|Melihat profil akun sendiri|V|V|V|V|
|Mengubah informasi akun sendiri|U|U|U|U|
|Mengubah password sendiri|U|U|U|U|
|Melihat seluruh akun pengguna|—|V|—|—|
|Membuat akun internal|—|C|—|—|
|Mengubah role pengguna|—|U|—|—|
|Menonaktifkan akun pengguna|—|U|—|—|

**Aturan tambahan:**

1. Registrasi publik hanya tersedia untuk calon mahasiswa.
2. Akun admin, panitia, dan pimpinan dibuat atau dikelola oleh admin yang berwenang.
3. Pengguna tidak dapat mengubah role miliknya sendiri melalui fitur profil.
4. Pengguna yang akunnya dinonaktifkan tidak boleh menggunakan akun tersebut untuk mengakses fitur yang memerlukan autentikasi.
5. Pengubahan password harus melalui proses yang aman dan tidak boleh menampilkan password lama dalam bentuk teks biasa.

### 4.2 Profil Calon Mahasiswa

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Melihat profil sendiri|V|—|—|—|
|Membuat profil sendiri|C|—|—|—|
|Mengubah profil sendiri|U|—|—|—|
|Melihat profil calon mahasiswa lain|—|V|V|—|
|Mengoreksi data profil untuk kebutuhan administrasi|—|U|Sesuai kebutuhan|—|
|Menghapus profil calon mahasiswa|—|Terbatas|—|—|

**Aturan tambahan:**

- Calon mahasiswa hanya boleh melihat dan mengubah profil miliknya sendiri.
- Admin hanya boleh mengoreksi data calon mahasiswa jika memang diperlukan untuk proses administrasi dan tindakan tersebut diizinkan oleh kebijakan PMB.
- Panitia dapat melihat informasi profil yang diperlukan untuk verifikasi pendaftaran, tetapi tidak otomatis memperoleh hak untuk mengubah seluruh data profil.
- Penghapusan profil tidak boleh dilakukan sembarangan apabila masih terkait dengan pendaftaran atau riwayat keputusan.
- Data sensitif, seperti NIK, tidak boleh ditampilkan secara penuh kepada role yang tidak membutuhkannya.

### 4.3 Gelombang Pendaftaran

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Melihat daftar gelombang yang tersedia|V|V|V|V|
|Melihat detail gelombang|V|V|V|V|
|Membuat gelombang pendaftaran|—|C|—|—|
|Mengubah jadwal gelombang|—|U|—|—|
|Mengaktifkan atau menonaktifkan gelombang|—|U|—|—|
|Menghapus gelombang|—|D terbatas|—|—|

**Aturan tambahan:**

- Gelombang hanya dapat dipilih untuk pendaftaran apabila memenuhi aturan periode dan status aktif yang ditetapkan sistem.
- Perubahan jadwal gelombang harus divalidasi oleh backend.
- Gelombang yang sudah memiliki data pendaftaran sebaiknya dinonaktifkan atau diarsipkan, bukan dihapus secara permanen.
- Hak akses melihat gelombang tidak berarti pengguna berhak mengubah konfigurasi gelombang.

### 4.4 Program Studi

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Melihat program studi yang tersedia|V|V|V|V|
|Membuat program studi|—|C|—|—|
|Mengubah informasi program studi|—|U|—|—|
|Mengaktifkan atau menonaktifkan program studi|—|U|—|—|
|Menghapus program studi|—|D terbatas|—|—|

**Aturan tambahan:**

- Calon mahasiswa hanya dapat memilih program studi yang tersedia sesuai aturan pendaftaran.
- Program studi yang sudah digunakan dalam pendaftaran sebaiknya tidak dihapus secara permanen.
- Data program studi yang ditampilkan kepada pengguna harus berasal dari backend, bukan hanya dari daftar statis pada aplikasi Android.
- Daftar program studi pada prototipe harus diverifikasi terlebih dahulu sebelum digunakan dalam sistem resmi.

### 4.5 Pendaftaran Calon Mahasiswa

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Membuat pendaftaran|C|—|—|—|
|Melihat pendaftaran sendiri|V|V|V|—|
|Melihat seluruh daftar pendaftaran|—|V|V|—|
|Mengubah pendaftaran berstatus `draft`|U|Terbatas|—|—|
|Mengirim pendaftaran|A|—|—|—|
|Melihat status pendaftaran sendiri|V|V|V|—|
|Memproses verifikasi administrasi|—|Terbatas|A|—|
|Meminta revisi dokumen atau data|—|Terbatas|A|—|
|Mengubah status pendaftaran secara manual|—|Terbatas|Sesuai kewenangan|—|
|Menetapkan keputusan akhir|—|Sesuai kewenangan|Sesuai kewenangan|—|
|Menghapus pendaftaran yang sudah dikirim|—|Terbatas|—|—|

**Aturan tambahan:**

1. Setiap akun calon mahasiswa hanya boleh memiliki **satu pendaftaran pada setiap gelombang yang sama**.
2. Akun dapat membuat pendaftaran pada gelombang lain jika aturan dan periode gelombang tersebut mengizinkannya.
3. Calon mahasiswa tidak boleh mengubah data pendaftaran yang sudah dikirim apabila statusnya tidak mengizinkan perubahan tersebut.
4. Panitia bertanggung jawab memeriksa kelengkapan dan kebenaran administratif sesuai prosedur.
5. Keputusan akhir penerimaan harus dibuat oleh petugas atau pejabat yang secara resmi diberi kewenangan.
6. Sistem tidak boleh menerima perubahan status secara langsung dari pengguna yang tidak berwenang.
7. Penghapusan pendaftaran yang sudah dikirim harus dibatasi dan mengikuti kebijakan retensi data.

### 4.6 Dokumen Pendaftaran

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Mengunggah dokumen sendiri|C|—|—|—|
|Melihat dokumen pendaftaran sendiri|V|V sesuai kebutuhan|V|—|
|Mengganti dokumen saat status mengizinkan|U|Terbatas|—|—|
|Menghapus dokumen sebelum dikirim|D terbatas|Terbatas|—|—|
|Mengunduh dokumen untuk verifikasi|V, milik sendiri|V sesuai kebutuhan|V|—|
|Memeriksa kelengkapan dokumen|—|Terbatas|A|—|
|Mengubah metadata dokumen|—|Terbatas|Terbatas|—|

**Aturan keamanan dokumen:**

- Dokumen pribadi tidak boleh disimpan di direktori publik yang dapat diakses langsung tanpa pemeriksaan hak akses.
- Backend harus memeriksa kepemilikan pendaftaran sebelum memberikan akses dokumen kepada calon mahasiswa.
- Panitia hanya dapat mengakses dokumen yang diperlukan untuk tugas verifikasi.
- Pimpinan tidak mendapatkan akses otomatis untuk membuka dokumen pribadi hanya karena memiliki akses ke laporan.
- Sistem harus memvalidasi ekstensi, MIME type, ukuran, dan isi file sesuai kebijakan unggahan.
- Nama file yang diunggah pengguna tidak boleh langsung dijadikan nama file penyimpanan tanpa pemrosesan yang aman.
- Pengunduhan dokumen harus melewati endpoint yang memeriksa autentikasi dan otorisasi.

### 4.7 Riwayat Status Pendaftaran

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Melihat riwayat status sendiri|V|V|V|—|
|Melihat riwayat status seluruh pendaftaran|—|V|V|—|
|Menambahkan riwayat melalui perubahan status yang sah|—|Sesuai kewenangan|A|—|
|Mengubah riwayat lama|—|—|—|—|
|Menghapus riwayat lama|—|—|—|—|

Riwayat status digunakan untuk mencatat perubahan status pendaftaran, waktu perubahan, pengguna yang melakukan tindakan, dan catatan terkait.

Riwayat tersebut berfungsi sebagai jejak audit. Karena itu, pengguna tidak boleh mengubah atau menghapus riwayat lama melalui fitur operasional biasa.

Jika terjadi kesalahan pencatatan, sistem sebaiknya membuat catatan koreksi baru sesuai prosedur daripada menghapus bukti perubahan sebelumnya.

### 4.8 Dashboard dan Laporan

|Fitur|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Melihat ringkasan pendaftaran sendiri|V|—|—|—|
|Melihat dashboard administrasi|—|V|V terbatas|V|
|Melihat statistik pendaftaran|—|V|V|V|
|Melihat statistik per program studi|—|V|V|V|
|Melihat statistik per gelombang|—|V|V|V|
|Membuat laporan PDF|—|A|A terbatas|V sesuai kebutuhan|
|Mengunduh data pendaftaran terperinci|—|A|A sesuai tugas|Terbatas|
|Melihat dokumen pribadi melalui laporan|—|—|—|—|

**Aturan tambahan:**

- Laporan pimpinan sebaiknya menampilkan data agregat, seperti jumlah pendaftar, jumlah diterima, jumlah ditolak, dan statistik per program studi.
- Laporan tidak boleh otomatis menyertakan NIK lengkap, alamat, nomor telepon, atau dokumen pribadi apabila informasi tersebut tidak diperlukan.
- Hak akses terhadap dashboard tidak otomatis memberikan hak untuk mengakses seluruh data mentah.
- Ekspor data terperinci harus dibatasi berdasarkan role, tujuan penggunaan, dan kewenangan yang ditetapkan.

## 5. Matriks Akses Berdasarkan Data

Matriks fungsional sebelumnya menjelaskan akses berdasarkan fitur. Bagian ini merangkum batasan akses terhadap objek data utama.

|Objek Data|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|Akun pengguna|Akun sendiri|Seluruh akun sesuai tugas|Tidak ada akses langsung secara umum|Tidak ada akses langsung secara umum|
|Profil calon mahasiswa|Profil sendiri|Sesuai kebutuhan administrasi|Profil yang diperlukan untuk verifikasi|Statistik agregat|
|Gelombang pendaftaran|Data yang tersedia|Seluruh data gelombang|Melihat data gelombang|Melihat data gelombang|
|Program studi|Data yang tersedia|Seluruh data program studi|Melihat data program studi|Melihat data program studi|
|Pendaftaran|Pendaftaran sendiri|Seluruh data sesuai tugas|Pendaftaran yang perlu diproses|Statistik agregat|
|Dokumen pendaftaran|Dokumen sendiri|Sesuai kebutuhan administrasi|Dokumen yang perlu diverifikasi|Tidak ada akses langsung secara default|
|Riwayat status|Riwayat sendiri|Seluruh riwayat sesuai tugas|Riwayat pendaftaran yang ditangani|Statistik agregat|
|Laporan|Ringkasan pribadi|Laporan administrasi|Laporan operasional|Laporan manajerial|
|Audit log|Tidak|Sesuai kewenangan khusus|Terbatas sesuai tugas|Tidak secara langsung|

**Penting:** Hak akses terhadap seluruh data oleh admin tidak berarti admin bebas melakukan semua tindakan. Hak melihat, mengubah, menetapkan keputusan, dan menghapus data harus diperiksa secara terpisah.

## 6. Aturan Otorisasi yang Wajib Diterapkan

### 6.1 Autentikasi

Setiap endpoint yang memerlukan login wajib memverifikasi identitas pengguna melalui mekanisme autentikasi yang diterapkan backend.

Jika pengguna belum login atau token autentikasinya tidak valid, sistem mengembalikan:

- HTTP `401 Unauthorized`
- Kode kesalahan `UNAUTHENTICATED`

Endpoint yang memang bersifat publik, seperti registrasi akun atau daftar program studi yang tersedia, dapat dikecualikan dari autentikasi sesuai desain API.

### 6.2 Pemeriksaan Role

Backend harus memeriksa role pengguna sebelum menjalankan tindakan yang dibatasi.

Contoh:

- Calon mahasiswa tidak boleh membuka endpoint pengelolaan pengguna.
- Panitia tidak boleh mengubah konfigurasi gelombang jika tidak memiliki kewenangan tersebut.
- Pimpinan tidak boleh menetapkan keputusan penerimaan hanya karena dapat membuka dashboard.
- Admin tidak otomatis berhak membuat keputusan akhir apabila kebijakan PMB memisahkan kewenangan administrasi dan keputusan penerimaan.

Jika pengguna sudah terautentikasi tetapi tidak memiliki izin, sistem mengembalikan:

- HTTP `403 Forbidden`
- Kode kesalahan `FORBIDDEN`

### 6.3 Pemeriksaan Kepemilikan Data

Selain memeriksa role, backend wajib memeriksa kepemilikan data.

Contoh:

- Calon mahasiswa A hanya boleh melihat pendaftaran miliknya sendiri.
- Calon mahasiswa A tidak boleh mengakses dokumen milik calon mahasiswa B.
- Calon mahasiswa tidak boleh mengubah status pendaftarannya sendiri melalui manipulasi request API.

Pemeriksaan ini harus dilakukan di backend untuk setiap operasi yang mengakses objek berdasarkan ID.

**Jangan mengandalkan penyembunyian tombol atau halaman pada aplikasi Android sebagai mekanisme keamanan.**

### 6.4 Pembatasan Berdasarkan Status

Hak akses juga bergantung pada status pendaftaran.

|Status|Hak Calon Mahasiswa|Hak Panitia|
|---|---|---|
|`draft`|Mengubah data dan dokumen sesuai aturan|Belum memproses sebagai pendaftaran final|
|`submitted`|Melihat data dan status; tidak bebas mengubah data|Memeriksa pendaftaran|
|`review`|Melihat status dan informasi yang relevan|Melakukan verifikasi sesuai tugas|
|`revision`|Memperbaiki data atau dokumen yang diminta|Memeriksa hasil revisi|
|`accepted`|Melihat keputusan|Melihat hasil dan melakukan tindakan yang diizinkan|
|`rejected`|Melihat keputusan|Melihat hasil dan melakukan tindakan yang diizinkan|

Perubahan status hanya boleh dilakukan melalui proses bisnis yang sah. Backend harus memvalidasi status awal, status tujuan, role pelaksana, dan aturan transisi status.

### 6.5 Pembatasan Keputusan Akhir

Keputusan penerimaan tidak boleh ditetapkan secara otomatis oleh sistem berdasarkan hasil pemeriksaan format data atau kelengkapan dokumen.

Sistem hanya membantu validasi administratif. Penetapan `accepted` atau `rejected` harus dilakukan oleh pihak yang berwenang sesuai kebijakan PMB.

Jika keputusan sudah final, perubahan keputusan harus melalui prosedur koreksi resmi yang mencatat:

- Keputusan sebelumnya.
- Keputusan baru.
- Alasan perubahan.
- Pengguna yang melakukan perubahan.
- Waktu perubahan.

Kewenangan perubahan keputusan final harus ditetapkan secara eksplisit dan tidak diberikan secara otomatis kepada seluruh admin atau panitia.

## 7. Penerapan pada Backend Laravel

Hak akses sebaiknya diterapkan pada beberapa lapisan agar tidak bergantung pada satu pemeriksaan saja.

### 7.1 Middleware Autentikasi

Middleware autentikasi memastikan bahwa request berasal dari pengguna yang sudah login.

Contoh konseptual:

```
Route::middleware('auth:sanctum')->group(function () {
    // Endpoint yang memerlukan autentikasi.
});
```

Contoh tersebut menggunakan Laravel Sanctum sebagai opsi token autentikasi. Penggunaan dan konfigurasi akhirnya perlu disesuaikan dengan implementasi proyek.

### 7.2 Middleware Role

Endpoint tertentu hanya boleh diakses oleh role yang sesuai.

Contoh konseptual:

```
Route::middleware(['auth:sanctum', 'role:admin'])
    ->prefix('admin')
    ->group(function () {
        // Endpoint administrasi yang khusus untuk admin.
    });
```

Middleware `role:admin` pada contoh merupakan ilustrasi. Middleware tersebut harus dibuat sendiri atau menggunakan library otorisasi yang dipilih dalam implementasi.

Pemeriksaan role tetap harus disertai pemeriksaan izin yang lebih spesifik apabila suatu fitur tidak boleh digunakan oleh seluruh anggota role tersebut.

### 7.3 Policy dan Gate

Gunakan Laravel Policy atau Gate untuk memeriksa izin terhadap objek tertentu.

Contoh konseptual:

```
public function view(User $user, Application $application): bool
{
    if ($user->role->name === 'calon_mahasiswa') {
        return $application->applicant->user_id === $user->id;
    }

    if ($user->role->name === 'panitia') {
        return true; // Perlu diperketat sesuai penugasan dan kebijakan.
    }

    return in_array($user->role->name, ['admin']);
}
```

Contoh tersebut hanya ilustrasi dasar. Implementasi sebenarnya harus memastikan bahwa:

- Panitia hanya mengakses data sesuai tugasnya.
- Admin mengikuti batasan kewenangan operasional.
- Pimpinan hanya menerima akses yang diperlukan.
- Relasi `applicant` dan `user` sesuai dengan model database yang digunakan.

Jangan menggunakan pemeriksaan role yang terlalu umum jika sistem membutuhkan pembatasan akses per pendaftaran.

### 7.4 Pemeriksaan Kepemilikan

Saat calon mahasiswa mengakses pendaftaran, backend perlu memeriksa bahwa pendaftaran tersebut terkait dengan akun yang sedang login.

Contoh konseptual:

```
$application = Application::findOrFail($id);

$this->authorize('view', $application);
```

Pemeriksaan Policy harus dilakukan sebelum data atau dokumen dikirim kepada pengguna.

### 7.5 Validasi Perubahan Status

Perubahan status tidak boleh dilakukan melalui pengisian field status secara bebas pada endpoint pembaruan pendaftaran.

Gunakan proses khusus yang:

1. Memeriksa autentikasi.
2. Memeriksa role dan izin.
3. Memuat pendaftaran yang akan diubah.
4. Memeriksa status saat ini.
5. Memvalidasi status tujuan dan kewenangan pelaksana.
6. Menjalankan pembaruan dan pencatatan riwayat dalam transaksi database.
7. Mengembalikan hasil yang sesuai.

Dengan cara ini, sistem dapat menghindari perubahan status yang tidak sah dan menjaga konsistensi riwayat pendaftaran.

## 8. Penerapan pada API

Berikut contoh pembagian akses endpoint. Nama endpoint merupakan rancangan yang perlu diselaraskan dengan `API Contract.md`.

|Endpoint|Calon Mahasiswa|Admin|Panitia|Pimpinan|
|---|---|---|---|---|
|`POST /api/v1/auth/register`|Publik|Publik jika diizinkan|Publik jika diizinkan|Publik jika diizinkan|
|`POST /api/v1/auth/login`|Publik|Publik|Publik|Publik|
|`GET /api/v1/auth/me`|Akun sendiri|Akun sendiri|Akun sendiri|Akun sendiri|
|`GET /api/v1/admission-waves`|Ya|Ya|Ya|Ya|
|`GET /api/v1/study-programs`|Ya|Ya|Ya|Ya|
|`POST /api/v1/applications`|Ya|Tidak secara default|Tidak|Tidak|
|`GET /api/v1/applications/{id}`|Pendaftaran sendiri|Sesuai izin|Sesuai tugas|Tidak secara default|
|`POST /api/v1/applications/{id}/submit`|Pendaftaran sendiri|Tidak|Tidak|Tidak|
|`GET /api/v1/applications/{id}/status-history`|Pendaftaran sendiri|Sesuai izin|Sesuai tugas|Tidak secara default|
|`GET /api/v1/admin/applications`|Tidak|Ya|Ya|Tidak secara default|
|`POST /api/v1/admin/applications/{id}/review`|Tidak|Sesuai kewenangan|Ya|Tidak|
|`POST /api/v1/admin/applications/{id}/request-revision`|Tidak|Sesuai kewenangan|Ya|Tidak|
|`POST /api/v1/admin/applications/{id}/decision`|Tidak|Sesuai kewenangan|Sesuai kewenangan|Tidak secara default|
|`GET /api/v1/admin/reports`|Tidak|Ya|Terbatas|Ya|
|`GET /api/v1/admin/users`|Tidak|Ya|Tidak secara default|Tidak|

**Catatan penting:**

- Registrasi akun internal sebaiknya tidak dilakukan melalui endpoint registrasi publik.
- Endpoint dengan awalan `/admin` bukan berarti semua role admin otomatis boleh mengaksesnya; backend tetap harus memeriksa izin.
- Endpoint dokumen harus memeriksa kepemilikan atau kewenangan sebelum menampilkan, mengunduh, mengganti, atau menghapus file.
- Jika diperlukan, akses pimpinan dapat menggunakan endpoint laporan khusus yang hanya mengembalikan data agregat.
- Endpoint keputusan akhir harus dibatasi kepada role atau pengguna yang secara resmi memiliki kewenangan tersebut.

## 9. Audit Log dan Akuntabilitas

Tindakan penting harus dicatat agar aktivitas pada sistem dapat ditelusuri.

|Aktivitas|Perlu Dicatat|
|---|---|
|Login berhasil dan kegagalan login yang relevan|Ya|
|Pembuatan atau penonaktifan akun internal|Ya|
|Perubahan role pengguna|Ya|
|Perubahan data gelombang|Ya|
|Perubahan data program studi|Ya|
|Pengiriman pendaftaran|Ya|
|Permintaan revisi|Ya|
|Perubahan status pendaftaran|Ya|
|Penetapan keputusan penerimaan|Ya|
|Koreksi keputusan final|Ya|
|Ekspor laporan berisi data pribadi|Ya|
|Akses atau pengunduhan dokumen sensitif|Sebaiknya dicatat sesuai kebijakan|

Audit log minimal mencatat waktu, identitas pelaku, jenis tindakan, objek yang terpengaruh, hasil tindakan, dan alasan apabila diperlukan.

Password, token autentikasi, dan isi dokumen pribadi tidak boleh dimasukkan ke log dalam bentuk yang dapat membahayakan keamanan.

Hak untuk melihat atau mengelola audit log harus dibatasi secara khusus. Pengguna yang tercatat melakukan tindakan tidak boleh bebas menghapus catatan tindakannya sendiri.

## 10. Pengujian Hak Akses

Pengujian berikut harus dilakukan sebelum sistem dinyatakan siap digunakan.

|ID|Skenario Pengujian|Hasil yang Diharapkan|
|---|---|---|
|ACL-01|Calon mahasiswa membuka pendaftaran miliknya sendiri|Data dapat ditampilkan|
|ACL-02|Calon mahasiswa membuka pendaftaran pengguna lain|Akses ditolak|
|ACL-03|Calon mahasiswa mencoba membuka endpoint pengelolaan pengguna|Akses ditolak|
|ACL-04|Calon mahasiswa mengubah role melalui request API|Perubahan ditolak|
|ACL-05|Calon mahasiswa mencoba mengirim pendaftaran yang belum lengkap|Sistem menolak pengiriman dan menampilkan kesalahan validasi|
|ACL-06|Calon mahasiswa mencoba mengirim pendaftaran kedua pada gelombang yang sama|Sistem menolak duplikasi|
|ACL-07|Panitia membuka pendaftaran yang menjadi tanggung jawabnya|Akses diberikan sesuai izin|
|ACL-08|Panitia mencoba mengubah konfigurasi gelombang tanpa izin|Akses ditolak|
|ACL-09|Pengguna mencoba mengakses dokumen milik pengguna lain|Akses ditolak|
|ACL-10|Pimpinan membuka dashboard statistik|Statistik yang diizinkan dapat ditampilkan|
|ACL-11|Pimpinan mencoba menetapkan keputusan penerimaan tanpa kewenangan|Akses ditolak|
|ACL-12|Pengguna yang belum login membuka endpoint privat|Sistem mengembalikan `401 Unauthorized`|
|ACL-13|Pengguna login tanpa izin membuka endpoint terlarang|Sistem mengembalikan `403 Forbidden` atau respons penolakan yang aman sesuai kebijakan|
|ACL-14|Panitia mengubah status melalui transisi yang tidak diizinkan|Sistem menolak perubahan|
|ACL-15|Pengguna mencoba menghapus riwayat status lama|Sistem menolak penghapusan|
|ACL-16|Admin menonaktifkan akun internal|Akun tidak dapat melakukan autentikasi baru|
|ACL-17|Pengguna mengubah ID pada URL untuk mengakses data lain|Backend tetap memeriksa izin dan menolak akses yang tidak sah|
|ACL-18|Pengguna mengekspor data yang tidak sesuai dengan role-nya|Sistem menolak ekspor|

Status setiap pengujian harus dicatat setelah pengujian benar-benar dilaksanakan. Jangan menyatakan seluruh pengujian berhasil sebelum hasilnya diverifikasi.

## 11. Kriteria Penerimaan

Implementasi hak akses dapat dianggap memenuhi dokumen ini apabila:

1. Sistem memiliki role `calon_mahasiswa`, `admin`, `panitia`, dan `pimpinan`.
2. Setiap endpoint privat memeriksa autentikasi.
3. Setiap operasi terbatas memeriksa role dan izin yang sesuai.
4. Calon mahasiswa hanya dapat mengakses data pendaftaran dan dokumennya sendiri.
5. Pendaftaran ganda pada gelombang yang sama dicegah oleh validasi aplikasi dan batasan database.
6. Panitia hanya dapat menjalankan tindakan yang sesuai dengan kewenangannya.
7. Keputusan akhir penerimaan hanya dapat ditetapkan oleh pihak yang diberi kewenangan.
8. Pimpinan hanya memperoleh data yang diperlukan untuk pelaporan dan pengambilan keputusan.
9. Dokumen pribadi tidak dapat diakses langsung tanpa pemeriksaan otorisasi.
10. Riwayat status dan aktivitas penting terlindungi dari perubahan atau penghapusan tanpa izin.
11. Respons API tidak membocorkan password, token, stack trace, atau informasi internal yang sensitif.
12. Pengujian hak akses dilaksanakan dan hasilnya didokumentasikan.

## 12. Keputusan yang Masih Perlu Dikonfirmasi

Beberapa aturan berikut perlu dipastikan sebelum implementasi final:

- Apakah admin boleh menetapkan keputusan akhir penerimaan atau hanya mengelola administrasi?
- Apakah panitia memiliki akses ke semua pendaftaran atau hanya pendaftaran yang ditugaskan?
- Apakah pimpinan diperbolehkan mengunduh data pendaftaran terperinci?
- Apakah admin boleh mengoreksi profil calon mahasiswa dan dalam kondisi apa?
- Apakah keputusan final dapat diubah, siapa yang berwenang, dan bagaimana prosedurnya?
- Berapa lama data pendaftaran dan audit log harus disimpan?
- Apakah akses dan pengunduhan dokumen perlu dicatat seluruhnya atau hanya untuk tindakan tertentu?

Sampai keputusan tersebut ditetapkan, gunakan prinsip **least privilege**, yaitu memberikan hak akses seminimal mungkin untuk menyelesaikan tugas pengguna.

## 13. Kesimpulan

Access Control Matrix menjadi acuan untuk mengatur hak akses pada sistem PMB ITB Akiday. Penerapan RBAC, pemeriksaan kepemilikan data, pembatasan berdasarkan status pendaftaran, serta pencatatan audit membantu mencegah akses yang tidak sah dan menjaga kerahasiaan data calon mahasiswa.

Pengamanan harus diterapkan pada backend Laravel, bukan hanya melalui pembatasan tampilan pada aplikasi Android atau website admin. Dengan demikian, manipulasi request API tidak dapat digunakan untuk melewati hak akses yang sudah ditetapkan.

Dokumen ini menjadi dasar implementasi otorisasi dan pengujian keamanan sistem PMB ITB Akiday.

</WritingBlock>