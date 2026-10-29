# Runbook — Menjalankan Server (Lokal)

Satu halaman untuk menyalakan, memeriksa, dan mematikan seluruh sistem PMB ITB
Akiday di komputer lokal. Untuk penjelasan konfigurasi, lihat
`Deployment Guide.md` dan `Environment Configuration.md`.

---

## 1. Gambaran singkat

| Komponen | Dijalankan dengan | Alamat |
|---|---|---|
| Database MySQL 8.4.13 | `docker compose up -d db` | `127.0.0.1:3306` (container `pmb-mysql`) |
| Backend Laravel | `php artisan serve` | `http://127.0.0.1:8000` |
| Admin Website | `npm run dev` | `http://localhost:3000` (proxy ke port 8000) |
| Aplikasi Android | Android Studio / `adb install` | API `http://10.0.2.2:8000/api/v1/` (emulator) |

Urutan wajib: **database → backend → admin web / Android**. Admin web dan
Android tidak akan berfungsi jika backend mati.

Database hanya diakses oleh Laravel. Dokumen pendaftar ada di
`backend-api/storage/app/private/` dan hanya keluar lewat endpoint terotorisasi.

---

## 2. Prasyarat (sekali saja)

```bash
php -v          # 8.4.x, ekstensi pdo_mysql aktif
composer -V     # Composer 2
node -v && npm -v
docker -v && docker compose version
java -version   # JDK 17+
```

Android SDK dibutuhkan hanya untuk aplikasi Android.

---

## 3. Setup pertama kali (lewati jika sudah pernah)

### 3.1 Environment root (untuk Docker)

```bash
cd /path/ke/AndroidStudioProjects/Project_PMB_ITB_AKIDAY
cp .env.example .env
# isi MYSQL_PASSWORD dan MYSQL_ROOT_PASSWORD dengan kata sandi lokal
#   openssl rand -base64 24
chmod 600 .env
```

`.env` root **tidak pernah** masuk Git. Jangan menyalin nilainya ke dokumen,
chat, atau commit.

### 3.2 Environment backend

```bash
cd backend-api
cp .env.example .env
# samakan dengan nilai di .env root:
#   DB_CONNECTION=mysql
#   DB_HOST=127.0.0.1
#   DB_PORT=3306                (harus sama dengan MYSQL_FORWARD_PORT di .env root)
#   DB_DATABASE=pmb_itb_akiday  (sama dengan MYSQL_DATABASE)
#   DB_USERNAME=pmb_app         (sama dengan MYSQL_USER)
#   DB_PASSWORD=<MYSQL_PASSWORD>
php artisan key:generate
```

### 3.3 Dependensi

```bash
cd backend-api && composer install
cd ../admin-web && npm ci
```

---

## 4. Menyalakan sistem

### 4.1 Database

```bash
cd /path/ke/AndroidStudioProjects/Project_PMB_ITB_AKIDAY
docker compose up -d db
docker compose ps            # tunggu sampai STATUS = healthy (±30 detik pertama)
```

Jika baru pertama kali (database masih kosong):

```bash
cd backend-api
php artisan migrate          # JANGAN migrate:fresh / db:wipe
```

### 4.2 Backend

```bash
cd backend-api
php artisan config:clear     # wajib setelah mengubah .env
php artisan serve --host=127.0.0.1 --port=8000
```

Biarkan terminal ini terbuka. Uji di terminal lain:

```bash
curl -s -o /dev/null -w '%{http_code}\n' http://127.0.0.1:8000/sanctum/csrf-cookie
# harapan: 204
```

### 4.3 Admin Website

```bash
cd admin-web
npm run dev                  # http://localhost:3000
```

### 4.4 Aplikasi Android

```bash
cd android-app
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Atau buka folder `android-app/` di Android Studio lalu Run.
Alamat API debug sudah `http://10.0.2.2:8000/api/v1/` (khusus emulator).
Untuk perangkat fisik, ubah `BASE_URL` debug di `android-app/app/build.gradle.kts`
menjadi `http://<IP-komputer>:8000/api/v1/`, lalu jalankan backend dengan
`php artisan serve --host=0.0.0.0 --port=8000`.

---

## 5. Memeriksa bahwa semuanya benar-benar jalan

```bash
# 1. Container sehat
docker compose ps                       # db / pmb-mysql → healthy

# 2. Laravel memakai MySQL (bukan SQLite)
cd backend-api
php artisan db:show | head -20          # Driver: mysql, Server: 8.4.13
php artisan migrate:status              # semua migration "Ran"

# 3. Backend melayani request
curl -s -o /dev/null -w '%{http_code}\n' http://127.0.0.1:8000/sanctum/csrf-cookie

# 4. Admin web terbuka
curl -s -o /dev/null -w '%{http_code}\n' http://localhost:3000
```

Login admin memakai akun yang terdaftar di database lokal. Kata sandi akun staf
dev ada di `backend-api/.env` pada kunci `SEED_ADMIN_PASSWORD`,
`SEED_PANITIA_PASSWORD`, dan `SEED_PIMPINAN_PASSWORD` (tidak dilacak Git). Jika
perlu diubah: ganti nilainya, lalu `php artisan db:seed --class=DatabaseSeeder`.

Untuk menjalankan skrip integrasi manual atau live test Android terhadap server
ini, lihat bagian **Pengujian** pada `README.md` (kredensial dibaca dari
environment, tanpa nilai default).

---

## 6. Mematikan sistem

```bash
# Backend dan admin web: Ctrl+C pada terminalnya masing-masing

# Database (data tetap aman di volume pmb_mysql8_data)
cd /path/ke/AndroidStudioProjects/Project_PMB_ITB_AKIDAY
docker compose stop db
```

Menyalakan lagi cukup `docker compose up -d db` — data lama tetap ada.

> **Jangan pernah** menjalankan `docker compose down -v`. Perintah itu
> **menghapus volume** dan seluruh data aplikasi.

---

## 7. Backup dan restore (sebelum perubahan besar)

```bash
cd /path/ke/AndroidStudioProjects/Project_PMB_ITB_AKIDAY

scripts/backup.sh                 # dump MySQL + dokumen privat + checksum + manifest
scripts/restore-verify.sh         # uji restore di lokasi terpisah (tidak menyentuh data aktif)
```

Backup ditulis ke `../pmb_backups/<timestamp>/`, di luar source code. Jangan
menimpa backup lama. Detail prosedur: `Backup & Recovery.md`.

---

## 8. Troubleshooting

| Gejala | Penyebab umum | Tindakan |
|---|---|---|
| `SQLSTATE[HY000] [2002] Connection refused` | Container DB belum siap / port salah | `docker compose ps`, cek `DB_PORT` = `MYSQL_FORWARD_PORT` |
| `Access denied for user ...` | `DB_USERNAME`/`DB_PASSWORD` beda dengan `.env` root | Samakan nilainya, lalu `php artisan config:clear` |
| Perubahan `.env` tidak berefek | Config ter-cache | `php artisan config:clear` (lalu `config:cache` untuk produksi) |
| Admin web 419 / CSRF | Cookie sesi lama atau backend mati | Hapus cookie `localhost:3000`, pastikan backend hidup |
| Admin web gagal panggil API (CORS/proxy) | Backend tidak di port 8000 | Jalankan `php artisan serve --port=8000` |
| Android `Connection refused` | Emulator memakai `10.0.2.2`, bukan `localhost` | Cek `BASE_URL` di `app/build.gradle.kts` |
| `php artisan db:show` masih `sqlite` | `DB_CONNECTION` belum diubah / cache | Set `DB_CONNECTION=mysql`, `config:clear` |
| Port 3306/8000/3000 sudah dipakai | Proses lain berjalan | `ss -lntp \| grep :3306` lalu hentikan prosesnya, atau ganti port |

Log berguna:

```bash
docker compose logs db --tail=50
tail -f backend-api/storage/logs/laravel.log
```

---

## 9. Aturan yang tidak boleh dilanggar

- Jangan `migrate:fresh`, `db:wipe`, `docker compose down -v`, atau menghapus
  volume `pmb_mysql8_data` / `pmb_mysql_data`.
- Jangan menghapus `backend-api/database/database.sqlite` (cadangan rollback ke
  SQLite) maupun dokumen di `storage/app/private/`.
- Jangan commit `.env`, database lokal, dokumen pendaftar, atau backup.
- Jangan menjalankan dua backend sekaligus pada port yang sama.

---

## 10. Git (ringkas)

```bash
git status          # harus bersih setelah pekerjaan selesai
git log --oneline   # baseline commit + commit dokumentasi
```

Satu repository untuk seluruh komponen, branch `main`, **belum ada remote**.
Remote ditetapkan hanya setelah ada instruksi. File yang tidak boleh dilacak
diatur di `.gitignore` root.
