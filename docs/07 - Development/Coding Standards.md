**Proyek:** PMB ITB Akiday  
**Versi:** 1.0  
**Status:** Draft untuk implementasi  
**Folder Obsidian:** `07 - Development/Coding Standards.md`

---

## 1. Tujuan

Coding Standards merupakan pedoman yang digunakan oleh tim pengembang dalam menulis, mengatur, menguji, dan memelihara kode sumber aplikasi PMB ITB Akiday.

Standar ini dibuat agar kode yang dikembangkan oleh beberapa anggota tim tetap konsisten dan mudah dipahami. Dengan aturan penamaan, struktur folder, pemisahan tanggung jawab, dan penanganan kesalahan yang jelas, proses pengembangan maupun perbaikan sistem dapat dilakukan dengan lebih terarah.

Dokumen ini juga menjadi acuan saat melakukan code review sebelum perubahan kode digabungkan ke branch utama repository.

## 2. Ruang Lingkup

Standar penulisan kode berlaku untuk komponen berikut:

|Komponen|Teknologi|Fokus Standar|
|---|---|---|
|Aplikasi Android|Kotlin|Penamaan, struktur class, pengelolaan state, dan pemanggilan API|
|Backend|Laravel dan PHP|Struktur controller, service, model, validasi, dan otorisasi|
|Database|MySQL|Penamaan tabel, kolom, relasi, dan migration|
|API|REST API dan JSON|Konsistensi endpoint, format respons, dan penanganan kesalahan|
|Website administrasi|Teknologi frontend yang dipilih|Struktur komponen, validasi, dan pengelolaan tampilan|
|Pengujian|Unit test dan integration test|Penamaan test, cakupan pengujian, dan keterbacaan|
|Repository|Git dan GitHub|Penamaan branch, commit, dan code review|

Standar ini merupakan acuan awal. Detail yang bergantung pada teknologi website administrasi akan disesuaikan setelah framework frontend ditetapkan.

## 3. Prinsip Umum Penulisan Kode

### 3.1 Readability

Kode harus mudah dibaca oleh pengembang lain. Gunakan nama variabel, fungsi, dan class yang menjelaskan tujuan penggunaannya.

Hindari penamaan yang terlalu pendek atau tidak memiliki makna.

Contoh kurang baik:

```
val d = getData()
```

Contoh yang lebih baik:

```
val admissionWaves = getAdmissionWaves()
```

### 3.2 Single Responsibility

Setiap class atau fungsi sebaiknya memiliki satu tanggung jawab utama.

Contohnya, fungsi untuk memvalidasi formulir pendaftaran tidak sebaiknya sekaligus mengunggah dokumen, mengirim email, dan menghasilkan laporan.

Pisahkan proses tersebut ke komponen yang sesuai agar lebih mudah diuji dan diperbaiki.

### 3.3 Reusability

Logika yang digunakan berulang kali sebaiknya dibuat menjadi fungsi, service, atau komponen yang dapat digunakan kembali.

Namun, hindari membuat abstraksi yang terlalu rumit untuk kebutuhan sederhana.

### 3.4 Consistency

Penamaan, format kode, struktur folder, format respons API, dan pola penanganan kesalahan harus mengikuti standar yang sama.

### 3.5 Security by Design

Keamanan harus dipertimbangkan sejak kode ditulis, bukan hanya saat pengujian akhir.

Contohnya:

- Validasi input dilakukan di backend.
- Hak akses diperiksa pada setiap operasi yang dilindungi.
- Password tidak disimpan dalam bentuk teks biasa.
- Dokumen pribadi tidak diletakkan di penyimpanan publik tanpa perlindungan.
- Rahasia aplikasi tidak dimasukkan ke repository.

### 3.6 Maintainability

Kode harus mudah dikembangkan ketika sistem membutuhkan perubahan, seperti penambahan gelombang pendaftaran, perubahan alur revisi, atau penambahan jenis laporan.

---

## 4. Standar Penamaan

### 4.1 Kotlin

Gunakan konvensi penamaan Kotlin yang umum.

|Elemen|Konvensi|Contoh|
|---|---|---|
|Class|PascalCase|`ApplicationRepository`|
|Data class|PascalCase|`ApplicantProfile`|
|Function|camelCase|`submitApplication()`|
|Variable|camelCase|`admissionWaveId`|
|Constant|UPPER_SNAKE_CASE|`MAX_DOCUMENT_SIZE`|
|Package|Huruf kecil|`com.example.pmb.data`|
|Boolean|Nama yang menjelaskan kondisi|`isApplicationComplete`|
|Interface|PascalCase|`ApplicationDataSource`|

Contoh:

```
data class ApplicantProfile(
    val fullName: String,
    val email: String,
    val phoneNumber: String
)

fun isApplicationComplete(): Boolean {
    // Implementasi validasi.
    return true
}
```

Nama package di atas merupakan contoh. Package final harus mengikuti konfigurasi aplikasi Android yang sebenarnya.

### 4.2 Laravel dan PHP

Gunakan konvensi penamaan PHP dan Laravel.

|Elemen|Konvensi|Contoh|
|---|---|---|
|Class|PascalCase|`ApplicationController`|
|Model Eloquent|PascalCase, singular|`Application`|
|Controller|PascalCase dengan akhiran Controller|`ApplicationController`|
|Method|camelCase|`submitApplication()`|
|Variable|camelCase|$admissionWaveId|
|Constant|UPPER_SNAKE_CASE|`MAX_DOCUMENT_SIZE`|
|Migration|Nama deskriptif|`create_applications_table`|
|Service|PascalCase dengan akhiran Service|`ApplicationSubmissionService`|
|Policy|PascalCase dengan akhiran Policy|`ApplicationPolicy`|
|Form Request|PascalCase dengan akhiran Request|`StoreApplicationRequest`|

Contoh:

```
class ApplicationController extends Controller
{
    public function show(Application $application)
    {
        // Otorisasi dan pengambilan data.
    }
}
```

Gunakan fitur Laravel yang sesuai, seperti Form Request, Policy, Eloquent, dan transaction, daripada membuat mekanisme pengganti yang tidak diperlukan.

### 4.3 Database MySQL

Gunakan `snake_case` dengan nama tabel berbentuk jamak agar konsisten dengan rancangan database.

|Elemen|Konvensi|Contoh|
|---|---|---|
|Tabel|snake_case, jamak|`applications`|
|Kolom|snake_case|`registration_number`|
|Primary key|`id`|`id`|
|Foreign key|`{nama_entitas}_id`|`applicant_id`|
|Timestamp|snake_case|`created_at`|
|Boolean|Nama yang jelas|`is_active`|
|Index|Nama deskriptif|`applications_applicant_wave_unique`|

Contoh struktur kolom:

```
applications
├── id
├── applicant_id
├── wave_id
├── study_program_id
├── registration_number
├── status
├── submitted_at
├── created_at
└── updated_at
```

Penamaan tersebut harus konsisten dengan dokumen `Database Model.md`, `ERD.md`, dan `Data Dictionary.md`.

### 4.4 API

Gunakan nama endpoint berbentuk resource dan konsisten dengan REST API.

Contoh:

```
GET    /api/v1/applications
POST   /api/v1/applications
GET    /api/v1/applications/{id}
POST   /api/v1/applications/{id}/submit
GET    /api/v1/applications/{id}/status-history
```

Ketentuan:

- Gunakan kata benda untuk resource utama.
- Gunakan metode HTTP sesuai tujuan operasi.
- Gunakan versi API, yaitu `/api/v1`.
- Jangan membuat endpoint dengan nama yang berbeda untuk fungsi yang sama.
- Perubahan endpoint harus diselaraskan dengan `API Contract.md`.

Endpoint tersebut merupakan rancangan dan belum berarti sudah diimplementasikan.

---

## 5. Struktur Folder Aplikasi Android

Struktur package Android disarankan berdasarkan tanggung jawab fitur agar kode mudah dipelihara.

Contoh:

```
app/src/main/java/com/example/pmb/
├── core/
│   ├── network/
│   ├── security/
│   ├── common/
│   └── utils/
├── data/
│   ├── remote/
│   ├── repository/
│   └── model/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── feature/
│   ├── auth/
│   ├── home/
│   ├── profile/
│   ├── admission/
│   ├── application/
│   ├── documents/
│   └── status/
└── MainActivity.kt
```

Struktur tersebut merupakan rekomendasi. Implementasi dapat menggunakan pola arsitektur yang lebih sederhana jika ukuran proyek dan kemampuan tim masih terbatas.

### 5.1 Aturan Struktur Android

1. Pisahkan pemanggilan API dari kode tampilan.
2. Hindari menempatkan seluruh logika aplikasi di `Activity` atau `Fragment`.
3. Gunakan repository untuk mengatur akses data.
4. Gunakan ViewModel jika sesuai dengan arsitektur aplikasi.
5. Pisahkan model respons API dari model tampilan jika kebutuhan sistem memerlukannya.
6. Tangani loading, success, empty, dan error state secara konsisten.
7. Hindari menyimpan token atau password pada kode sumber.
8. Jangan menulis kredensial rahasia langsung pada file Kotlin.

### 5.2 Contoh Pemisahan Tanggung Jawab

Kurang disarankan:

```
class ApplicationActivity : AppCompatActivity() {

    fun submitApplication() {
        // Validasi formulir.
        // Mengirim request API.
        // Mengunggah dokumen.
        // Mengubah tampilan.
        // Menangani semua jenis kesalahan.
    }
}
```

Lebih disarankan untuk memisahkan tanggung jawab:

```
ApplicationActivity
        |
        v
ApplicationViewModel
        |
        v
ApplicationRepository
        |
        v
ApplicationApiService
        |
        v
Laravel REST API
```

Dengan pola tersebut, tampilan menangani interaksi pengguna, ViewModel mengelola state dan logika presentasi, repository mengatur akses data, dan API service menangani komunikasi HTTP.

---

## 6. Struktur Folder Backend Laravel

Struktur backend mengikuti konvensi Laravel dan menambahkan service atau komponen pendukung jika diperlukan.

Contoh:

```
app/
├── Http/
│   ├── Controllers/
│   │   ├── Api/
│   │   │   └── V1/
│   │   └── Admin/
│   ├── Requests/
│   └── Resources/
├── Models/
├── Policies/
├── Services/
└── Exceptions/

database/
├── factories/
├── migrations/
└── seeders/

routes/
├── api.php
└── web.php

tests/
├── Feature/
└── Unit/
```

Tidak semua folder tambahan harus dibuat sejak awal. Tambahkan folder ketika memang diperlukan oleh implementasi.

### 6.1 Controller

Controller bertanggung jawab menerima request, menjalankan otorisasi yang diperlukan, memanggil komponen pemrosesan, dan menghasilkan respons.

Controller sebaiknya tidak menampung seluruh proses bisnis yang kompleks.

Contoh:

```
class ApplicationController extends Controller
{
    public function store(StoreApplicationRequest $request)
    {
        $application = app(ApplicationSubmissionService::class)
            ->createDraft(
                $request->user(),
                $request->validated()
            );

        return response()->json([
            'success' => true,
            'message' => 'Pendaftaran berhasil dibuat.',
            'data' => $application,
        ], 201);
    }
}
```

Contoh tersebut merupakan ilustrasi. Nama service, model, dan struktur respons harus disesuaikan dengan implementasi final.

### 6.2 Form Request

Gunakan Form Request untuk memisahkan validasi request dari controller apabila validasinya cukup kompleks atau digunakan pada endpoint penting.

Contoh:

```
class StoreApplicationRequest extends FormRequest
{
    public function authorize(): bool
    {
        return $this->user() !== null
            && $this->user()->role->name === 'calon_mahasiswa';
    }

    public function rules(): array
    {
        return [
            'wave_id' => ['required', 'integer', 'exists:admission_waves,id'],
            'study_program_id' => [
                'required',
                'integer',
                'exists:study_programs,id',
            ],
        ];
    }
}
```

Validasi role pada contoh hanya pemeriksaan awal. Pemeriksaan kepemilikan, status pendaftaran, periode gelombang, dan aturan bisnis lain tetap harus diterapkan pada lapisan yang sesuai.

Jangan menganggap keberadaan ID yang valid berarti pengguna otomatis berhak menggunakan data tersebut.

### 6.3 Service

Gunakan service untuk proses bisnis yang kompleks atau melibatkan beberapa operasi.

Contoh tanggung jawab `ApplicationSubmissionService`:

- Memeriksa kelengkapan pendaftaran.
- Memeriksa periode gelombang.
- Mencegah pendaftaran ganda.
- Membuat nomor registrasi.
- Memperbarui status pendaftaran.
- Mencatat riwayat status.

Operasi database yang saling berkaitan harus dijalankan dalam transaksi bila diperlukan.

### 6.4 Model

Model Eloquent bertanggung jawab merepresentasikan data dan relasinya.

Contoh relasi:

```
class Application extends Model
{
    public function applicant()
    {
        return $this->belongsTo(ApplicantProfile::class, 'applicant_id');
    }

    public function wave()
    {
        return $this->belongsTo(AdmissionWave::class, 'wave_id');
    }

    public function studyProgram()
    {
        return $this->belongsTo(StudyProgram::class, 'study_program_id');
    }
}
```

Nama foreign key harus mengikuti struktur database yang telah ditetapkan.

---

## 7. Aturan Validasi dan Pemrosesan Data

### 7.1 Validasi Backend

Semua input harus divalidasi di backend sebelum diproses.

Validasi minimal meliputi:

- Format dan keunikan email.
- Format NIK sesuai aturan.
- Format nomor telepon.
- Tanggal lahir yang valid.
- Keberadaan gelombang dan program studi.
- Periode pendaftaran.
- Kelengkapan data dan dokumen.
- Status pendaftaran yang diizinkan.
- Ukuran dan format file.

Validasi di Android hanya membantu pengguna mengisi formulir dengan benar. Validasi tersebut bukan pengganti validasi Laravel.

### 7.2 Mass Assignment

Laravel menggunakan mekanisme mass assignment untuk memetakan data ke model.

Pengembang harus memastikan bahwa hanya field yang diizinkan yang dapat diisi dari request.

Jangan langsung memasukkan seluruh input pengguna ke model tanpa validasi dan pembatasan field.

Contoh yang perlu dihindari:

```
$application->update($request->all());
```

Lebih baik menggunakan data yang telah divalidasi dan field yang memang boleh diubah:

```
$data = $request->validated();

$application->fill([
    'study_program_id' => $data['study_program_id'],
]);

$application->save();
```

Contoh kedua tetap harus dilindungi oleh otorisasi dan pemeriksaan status pendaftaran.

### 7.3 Konsistensi Status

Status pendaftaran yang digunakan:

```
draft
submitted
review
revision
accepted
rejected
```

Jangan menambahkan status baru tanpa memperbarui:

- `SRS.md`
- `Database Model.md`
- `Data Dictionary.md`
- `API Contract.md`
- `Workflow & Business Process.md`
- `Test Cases.md`

Perubahan status harus melewati validasi transisi yang diizinkan dan pencatatan riwayat.

### 7.4 Pencegahan Pendaftaran Ganda

Sistem menerapkan aturan satu pendaftaran per akun calon mahasiswa pada setiap gelombang.

Gunakan:

- Validasi di backend.
- Unique constraint pada kombinasi `applicant_id` dan `wave_id`.
- Transaksi database ketika operasi membutuhkan konsistensi.
- Penanganan konflik ketika dua request terjadi hampir bersamaan.

Jika constraint database menolak pendaftaran duplikat, backend harus mengubahnya menjadi respons kesalahan yang sesuai, bukan menampilkan pesan kesalahan SQL mentah.

---

## 8. Standar Keamanan Kode

### 8.1 Password dan Token

- Gunakan hashing password yang disediakan framework.
- Jangan mencatat password atau token dalam log.
- Jangan menampilkan token pada pesan kesalahan.
- Jangan menaruh rahasia dalam kode sumber.
- Gunakan mekanisme penyimpanan token yang sesuai dengan platform.
- Cabut token atau akhiri sesi ketika diperlukan.

### 8.2 Otorisasi

Sebelum menampilkan atau mengubah data, backend harus memeriksa:

1. Apakah pengguna telah terautentikasi.
2. Apakah pengguna memiliki role atau izin yang diperlukan.
3. Apakah pengguna berhak mengakses objek yang diminta.
4. Apakah status objek mengizinkan tindakan tersebut.

Gunakan Laravel Policy atau Gate untuk otorisasi terhadap objek ketika sesuai.

### 8.3 File Upload

- Validasi file di backend.
- Batasi ekstensi, MIME type, dan ukuran file.
- Gunakan nama file penyimpanan yang aman.
- Simpan dokumen pada private storage.
- Periksa izin sebelum mengunduh dokumen.
- Jangan mempercayai nama file dari pengguna.
- Jangan menjalankan file yang diunggah sebagai kode.

### 8.4 Query Database

Gunakan Eloquent ORM, Query Builder, atau prepared statement.

Hindari membuat query SQL dengan menggabungkan input pengguna langsung ke string query.

### 8.5 Penanganan Error

Respons kepada pengguna harus mudah dipahami, tetapi tidak membocorkan detail internal.

Jangan mengembalikan:

- Stack trace.
- Password.
- Token.
- Query SQL internal.
- Informasi konfigurasi server.

Kesalahan teknis dicatat pada log yang terlindungi sesuai kebutuhan debugging.

---

## 9. Standar REST API

### 9.1 Format Respons

Respons sukses harus konsisten dengan API Contract.

Contoh:

```
{
  "success": true,
  "message": "Data pendaftaran berhasil diambil.",
  "data": {
    "id": 12,
    "status": "draft"
  }
}
```

Contoh respons kesalahan:

```
{
  "success": false,
  "message": "Data yang dikirim belum sesuai.",
  "error": {
    "code": "VALIDATION_ERROR",
    "details": {
      "wave_id": [
        "Gelombang pendaftaran wajib dipilih."
      ]
    }
  }
}
```

Struktur tersebut merupakan standar rancangan dan harus diterapkan secara konsisten pada endpoint yang dikembangkan.

### 9.2 HTTP Status Code

Gunakan kode HTTP sesuai hasil operasi.

|Kode|Penggunaan|
|---|---|
|`200 OK`|Request berhasil|
|`201 Created`|Data berhasil dibuat|
|`204 No Content`|Operasi berhasil tanpa isi respons|
|`400 Bad Request`|Request tidak sesuai format atau tidak dapat diproses|
|`401 Unauthorized`|Autentikasi tidak valid atau belum dilakukan|
|`403 Forbidden`|Pengguna tidak memiliki izin|
|`404 Not Found`|Resource tidak ditemukan atau disembunyikan sesuai kebijakan|
|`409 Conflict`|Konflik data, seperti pendaftaran ganda|
|`413 Content Too Large`|File melebihi batas ukuran|
|`415 Unsupported Media Type`|Jenis media tidak didukung|
|`422 Unprocessable Content`|Validasi data gagal|
|`429 Too Many Requests`|Batas request tercapai|
|`500 Internal Server Error`|Kesalahan internal server|
|`503 Service Unavailable`|Layanan sementara tidak tersedia|

### 9.3 Konsistensi Endpoint

Endpoint yang sudah didefinisikan dalam `API Contract.md` harus menjadi acuan implementasi.

Jika endpoint berubah, perbarui dokumentasi dan pengujian terkait. Hindari mengubah kontrak API hanya pada backend tanpa memperbarui aplikasi Android dan website administrasi.

---

## 10. Standar Penulisan Migration

Setiap perubahan struktur database harus dilakukan melalui migration Laravel.

Ketentuan:

1. Nama migration harus menjelaskan perubahan.
2. Tabel dan kolom mengikuti konvensi penamaan database.
3. Primary key dan foreign key didefinisikan secara jelas.
4. Constraint penting harus diterapkan di database.
5. Perubahan struktur harus diperiksa dampaknya terhadap data yang sudah ada.
6. Migration yang sudah digunakan bersama oleh tim tidak boleh diubah sembarangan.
7. Jika diperlukan perubahan lanjutan, buat migration baru.

Contoh nama migration:

```
create_roles_table
create_users_table
create_applicant_profiles_table
create_admission_waves_table
create_study_programs_table
create_applications_table
create_application_documents_table
create_application_status_histories_table
```

Nama di atas disesuaikan dengan delapan tabel utama pada rancangan database PMB ITB Akiday.

### 10.1 Seeder

Seeder digunakan untuk memasukkan data awal, misalnya:

- Role pengguna.
- Program studi prototipe.
- Data gelombang untuk lingkungan pengembangan.
- Akun uji untuk kebutuhan testing.

Ketentuan:

- Data prototipe harus dibedakan dari data resmi.
- Password akun uji tidak boleh menggunakan kredensial produksi.
- Seeder produksi tidak boleh secara sembarangan menonaktifkan atau menimpa data yang sudah digunakan.
- Program studi harus diverifikasi sebelum sistem digunakan secara resmi.

---

## 11. Standar Penanganan State pada Android

Aplikasi Android harus menangani kondisi request API secara konsisten.

State utama yang perlu dipertimbangkan:

- `Loading`
- `Success`
- `Empty`
- `Error`

Contoh model state:

```
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
    data object Empty : UiState<Nothing>
}
```

Contoh tersebut menggunakan fitur Kotlin modern. Versi Kotlin dan konfigurasi proyek harus mendukung sintaks yang digunakan.

Ketentuan:

1. Tampilkan indikator loading saat request berlangsung.
2. Tampilkan pesan yang jelas jika request gagal.
3. Jangan menganggap data berhasil disimpan sebelum backend mengonfirmasi hasilnya.
4. Cegah pengiriman berulang yang tidak diperlukan.
5. Tangani token kedaluwarsa atau tidak valid.
6. Tangani kegagalan jaringan tanpa menghapus input pengguna secara sembarangan.
7. Pastikan perubahan state tidak menyebabkan request ganda yang tidak disengaja.

---

## 12. Standar Pengujian

Kode harus diuji sesuai tingkat risiko dan kompleksitasnya.

### 12.1 Unit Test

Unit test digunakan untuk memeriksa logika yang dapat diuji secara terpisah.

Contoh:

- Validasi kelengkapan pendaftaran.
- Pemeriksaan transisi status.
- Pembentukan nomor registrasi.
- Logika perhitungan statistik.
- Validasi aturan bisnis tertentu.

### 12.2 Feature dan Integration Test

Pengujian ini digunakan untuk memeriksa kerja sama beberapa komponen.

Contoh:

- Registrasi dan login.
- Pembuatan pendaftaran.
- Pengiriman pendaftaran.
- Unggah dokumen.
- Permintaan revisi.
- Penetapan keputusan.
- Pembatasan akses berdasarkan role.
- Pencegahan pendaftaran ganda.

### 12.3 Pengujian Keamanan

Pengujian keamanan harus mencakup:

- Akses data milik pengguna lain.
- Manipulasi ID pada URL.
- Perubahan role melalui request.
- Pengiriman status yang tidak diizinkan.
- Unggahan file yang tidak diperbolehkan.
- Request berulang pada endpoint sensitif.
- Kebocoran informasi pada respons error.

### 12.4 Penamaan Test

Gunakan nama test yang menjelaskan kondisi dan hasil yang diharapkan.

Contoh:

```
public function test_applicant_cannot_view_another_applicants_application(): void
{
    // Arrange, act, assert.
}
```

```
@Test
fun submitApplication_whenRequiredFieldsAreMissing_returnsValidationError() {
    // Arrange, act, assert.
}
```

Nama test tersebut merupakan contoh. Struktur dan framework pengujian mengikuti konfigurasi aktual proyek.

### 12.5 Prinsip Pengujian

Setiap fitur baru atau perubahan yang berisiko harus disertai pengujian yang relevan.

Jangan menyatakan fitur aman atau seluruh pengujian lulus hanya karena aplikasi dapat dijalankan. Hasil harus dibuktikan melalui pengujian yang benar-benar dilakukan.

---

## 13. Standar Git dan Commit

### 13.1 Penamaan Branch

Gunakan nama branch yang menjelaskan pekerjaan.

|Jenis Pekerjaan|Format|Contoh|
|---|---|---|
|Fitur baru|`feature/nama-fitur`|`feature/applicant-registration`|
|Perbaikan bug|`fix/nama-bug`|`fix/duplicate-application`|
|Dokumentasi|`docs/nama-dokumen`|`docs/security-specification`|
|Refactoring|`refactor/nama-komponen`|`refactor/application-service`|
|Pengujian|`test/nama-pengujian`|`test/application-authorization`|

### 13.2 Pesan Commit

Gunakan pesan commit yang singkat dan menjelaskan perubahan.

Contoh:

```
feat: add applicant registration
fix: prevent duplicate application
docs: update API contract
test: add application access tests
refactor: separate application business logic
```

Format tersebut merupakan konvensi yang disarankan dan dapat digunakan secara konsisten oleh tim.

### 13.3 Aturan Repository

- Jangan mengunggah file `.env`.
- Jangan mengunggah password atau token.
- Jangan mengunggah dokumen pribadi calon mahasiswa ke repository.
- Jangan memasukkan file build atau file sementara yang tidak diperlukan.
- Periksa perubahan sebelum melakukan commit.
- Gunakan `.gitignore` yang sesuai untuk Android, Laravel, dan lingkungan pengembangan.
- Jangan langsung menggabungkan perubahan berisiko ke branch utama tanpa pemeriksaan.

### 13.4 Code Review

Sebelum perubahan digabungkan, reviewer harus memeriksa:

1. Kesesuaian dengan kebutuhan sistem.
2. Konsistensi penamaan dan struktur kode.
3. Kebenaran logika bisnis.
4. Validasi input dan otorisasi.
5. Penanganan error.
6. Dampak terhadap database dan API.
7. Pengujian yang menyertai perubahan.
8. Pembaruan dokumentasi apabila kontrak atau perilaku sistem berubah.

---

## 14. Dokumentasi Kode

Dokumentasi kode harus membantu pengembang memahami bagian yang kompleks atau memiliki aturan bisnis penting.

Gunakan komentar untuk menjelaskan alasan, batasan, atau keputusan teknis yang tidak langsung terlihat dari kode.

Hindari komentar yang hanya mengulang apa yang sudah jelas.

Contoh kurang bermanfaat:

```
// Menambahkan angka satu.
counter += 1
```

Contoh yang lebih bermanfaat:

```
// Mencegah tombol submit diproses kembali saat request sebelumnya masih berjalan.
if (isSubmitting) return
```

Fungsi, class, dan API yang kompleks dapat dilengkapi dokumentasi sesuai konvensi bahasa yang digunakan.

Dokumentasi juga harus diperbarui ketika aturan bisnis berubah.

---

## 15. Checklist Sebelum Kode Digabungkan

Gunakan checklist berikut sebelum menggabungkan perubahan ke branch utama.

### 15.1 Kualitas Kode

- [ ] Nama class, fungsi, dan variabel sudah jelas.
- [ ] Struktur folder sesuai standar proyek.
- [ ] Tanggung jawab komponen sudah dipisahkan dengan baik.
- [ ] Tidak terdapat kode duplikat yang seharusnya dapat digunakan kembali.
- [ ] Tidak terdapat kode debug yang tidak diperlukan.
- [ ] Format kode konsisten.

### 15.2 Keamanan

- [ ] Input sudah divalidasi di backend.
- [ ] Otorisasi sudah diperiksa.
- [ ] Kepemilikan data sudah diperiksa jika diperlukan.
- [ ] Password dan token tidak dicatat dalam log.
- [ ] Tidak ada kredensial rahasia di repository.
- [ ] Dokumen pribadi disimpan dan diakses secara aman.
- [ ] Kesalahan tidak membocorkan informasi internal.

### 15.3 Database dan API

- [ ] Migration sudah dibuat jika struktur database berubah.
- [ ] Relasi dan constraint database sudah sesuai.
- [ ] Respons API mengikuti kontrak.
- [ ] HTTP status code digunakan dengan tepat.
- [ ] Transaksi digunakan untuk operasi yang memerlukannya.
- [ ] Pencegahan duplikasi sudah diperhatikan.

### 15.4 Pengujian dan Dokumentasi

- [ ] Test yang relevan sudah dibuat.
- [ ] Pengujian sudah dijalankan.
- [ ] Hasil pengujian dicatat.
- [ ] Dokumentasi terkait sudah diperbarui.
- [ ] Perubahan sudah diperiksa melalui code review.

---

## 16. Kriteria Penerimaan

Coding Standards dianggap siap digunakan sebagai acuan pengembangan apabila:

1. Tim menyepakati konvensi penamaan kode.
2. Struktur folder Android dan Laravel telah disesuaikan dengan implementasi proyek.
3. Penamaan tabel dan kolom konsisten dengan dokumen database.
4. Controller, service, model, dan komponen tampilan memiliki tanggung jawab yang jelas.
5. Validasi dan otorisasi diterapkan pada backend.
6. Respons API mengikuti `API Contract.md`.
7. Migration digunakan untuk perubahan struktur database.
8. Kode penting memiliki pengujian yang relevan.
9. Perubahan kode ditinjau melalui code review.
10. Rahasia dan data pribadi tidak dimasukkan ke repository.
11. Dokumentasi diperbarui saat terjadi perubahan perilaku sistem.
12. Checklist kualitas kode digunakan sebelum perubahan digabungkan.

---

## 17. Dokumen Terkait

Coding Standards berhubungan dengan dokumen berikut:

|Dokumen|Hubungan|
|---|---|
|[[Project Overview]]|Gambaran umum proyek|
|[[SRS]]|Kebutuhan fungsional dan nonfungsional|
|[[Module Specification]]|Spesifikasi modul aplikasi|
|[[Database Model]]|Struktur dan relasi database|
|[[ERD]]|Hubungan antarentitas|
|[[Data Dictionary]]|Definisi tabel dan kolom|
|[[Migration Plan]]|Rencana implementasi database|
|[[API Contract]]|Kontrak endpoint dan respons API|
|[[API Error Handling]]|Standar penanganan kesalahan API|
|[[Access Control Matrix]]|Pembagian hak akses|
|[[Security Specification]]|Persyaratan keamanan sistem|
|[[Test Plan]]|Strategi pengujian|
|[[Test Cases]]|Skenario pengujian|

---

## 18. Kesimpulan

Coding Standards menjadi pedoman agar pengembangan PMB ITB Akiday dilakukan secara konsisten, terstruktur, dan mudah dipelihara. Standar ini mencakup penamaan kode, struktur folder, pemisahan tanggung jawab, validasi data, keamanan, pengelolaan database, kontrak API, pengujian, dan penggunaan Git.

Penerapan standar tersebut diharapkan dapat mengurangi kesalahan implementasi, mempermudah kolaborasi antaranggota tim, dan menjaga agar kode yang dikembangkan tetap sesuai dengan kebutuhan sistem.

Dokumen ini perlu ditinjau kembali ketika teknologi, struktur proyek, atau aturan bisnis mengalami perubahan.

</WritingBlock>