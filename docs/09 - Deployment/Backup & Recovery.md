## 1. Informasi Dokumen

|Atribut|Keterangan|
|---|---|
|Nama Dokumen|Backup & Recovery|
|Nama Proyek|PMB ITB Akiday|
|Versi Dokumen|1.0|
|Status Dokumen|Draft|
|Kategori|Deployment|
|Backend|Laravel|
|Database|MySQL|
|Penyimpanan Dokumen|Private File Storage|
|Lingkungan|Server lokal|
|Referensi|[[Deployment Guide]], [[Environment Configuration]], [[Database Model]], [[Security Specification]]|

> **Catatan:** Jadwal, lokasi backup, dan target pemulihan pada dokumen ini merupakan usulan awal. Konfigurasi aktual perlu disesuaikan dengan kapasitas server dan kebutuhan pengelola.

---

## 2. Tujuan

Backup & Recovery dibuat untuk menjaga keamanan dan ketersediaan data sistem PMB ITB Akiday apabila terjadi kerusakan database, kehilangan dokumen, kesalahan konfigurasi, kegagalan perangkat, atau kesalahan pengguna.

Backup merupakan proses membuat salinan data yang dapat digunakan kembali ketika data utama mengalami masalah. Recovery merupakan proses memulihkan data dan layanan dari salinan tersebut agar sistem dapat digunakan kembali.

Dokumen ini menjadi pedoman bagi administrator atau pengelola teknis dalam melakukan pencadangan, memeriksa keberhasilan backup, memulihkan database dan dokumen, serta memastikan data pendaftaran tetap konsisten.

## 3. Ruang Lingkup Backup

Komponen yang perlu dicadangkan meliputi:

|Komponen|Data yang Dicadangkan|Alasan|
|---|---|---|
|Database MySQL|Data pengguna, profil, gelombang, program studi, pendaftaran, dan riwayat status|Menjaga data utama sistem|
|Dokumen pendaftaran|File foto, ijazah/SKL, dan dokumen lain yang diizinkan|Mencegah kehilangan dokumen calon mahasiswa|
|Konfigurasi aplikasi|Salinan aman konfigurasi yang diperlukan untuk pemulihan|Membantu mengembalikan aplikasi ke konfigurasi sebelumnya|
|Kode sumber|Kode backend Laravel dan konfigurasi proyek yang tidak berisi rahasia|Mempermudah pemulihan aplikasi|
|Informasi deployment|Catatan versi aplikasi, skema database, dan instruksi instalasi|Membantu proses instalasi ulang|

Database dan dokumen harus dicadangkan secara terkoordinasi. Backup database saja tidak cukup apabila file dokumen yang dirujuk oleh database sudah hilang.

File `.env` dapat berisi kata sandi database, kunci aplikasi, dan informasi rahasia lainnya. Jangan menyimpan file tersebut pada repository publik atau backup tanpa perlindungan yang memadai.

## 4. Strategi Backup

Strategi backup awal yang disarankan adalah sebagai berikut.

|Jenis Backup|Frekuensi Usulan|Tujuan|
|---|---|---|
|Backup database penuh|Setiap hari|Menyediakan salinan data terbaru|
|Backup dokumen|Setiap hari dan setelah perubahan penting|Melindungi file pendaftaran|
|Backup sebelum migrasi|Sebelum perubahan skema database|Memudahkan pemulihan jika migrasi gagal|
|Backup kode sumber|Setiap perubahan yang sudah disetujui dan disimpan ke Git|Memulihkan versi aplikasi|
|Pengujian pemulihan|Setiap bulan|Memastikan backup dapat digunakan|

Untuk server lokal dengan aktivitas rendah, jadwal dapat disesuaikan. Namun, backup tetap harus dilakukan sebelum perubahan besar dan sebelum tindakan yang berisiko terhadap data.

**Prinsip yang digunakan:**

1. Backup harus dijalankan secara teratur.
    
2. Backup harus mencakup database dan file dokumen.
    
3. Salinan backup harus dipisahkan dari data utama.
    
4. Backup harus memiliki perlindungan akses.
    
5. Keberhasilan backup harus diverifikasi.
    
6. Backup harus diuji melalui proses pemulihan.
    

Menyimpan seluruh backup hanya pada komputer server yang sama berisiko menyebabkan semua salinan hilang ketika perangkat mengalami kerusakan. Jika memungkinkan, simpan salinan tambahan pada media atau perangkat terpisah yang terlindungi.

## 5. Kebijakan Retensi Backup

Retensi adalah jangka waktu penyimpanan backup sebelum salinan lama dihapus.

Usulan awal kebijakan retensi:

|Jenis Backup|Masa Penyimpanan Usulan|
|---|---|
|Backup harian|7 hari terakhir|
|Backup mingguan|4 minggu terakhir|
|Backup bulanan|3 bulan terakhir|
|Backup sebelum migrasi besar|Sampai migrasi dinyatakan stabil dan salinan tidak lagi diperlukan|

Kebijakan tersebut merupakan rancangan awal, bukan ketentuan resmi. Masa penyimpanan final harus mempertimbangkan kapasitas media, kebutuhan operasional, kebijakan privasi, serta aturan penyimpanan data yang berlaku.

Penghapusan backup harus dilakukan secara terkontrol. Backup yang sudah tidak diperlukan perlu dihapus dengan cara yang sesuai untuk media penyimpanannya, terutama jika memuat data pribadi calon mahasiswa.

## 6. Prosedur Backup Database MySQL

### 6.1 Persiapan

Sebelum melakukan backup, administrator perlu memastikan:

- Nama database yang benar sudah diketahui.
    
- Akun database memiliki hak akses yang diperlukan.
    
- Lokasi penyimpanan backup tersedia.
    
- Ruang penyimpanan mencukupi.
    
- File backup tidak disimpan pada direktori publik aplikasi.
    
- Data backup akan dilindungi dari akses pengguna yang tidak berwenang.
    

Nama database dan akun pada contoh berikut merupakan placeholder. Sesuaikan dengan konfigurasi lingkungan yang sebenarnya.

### 6.2 Backup menggunakan mysqldump

Contoh perintah backup database:

```
mysqldump --single-transaction --routines --triggers \
  -u USER_DATABASE -p NAMA_DATABASE \
  > pmb_backup.sql
```

Penjelasan:

- `mysqldump` digunakan untuk mengekspor database.
    
- `--single-transaction` membantu menghasilkan backup yang konsisten untuk tabel transaksional seperti InnoDB tanpa mengunci seluruh tabel selama proses berlangsung.
    
- `--routines` menyertakan stored routines jika digunakan.
    
- `--triggers` menyertakan trigger database.
    
- `-p` meminta kata sandi secara interaktif.
    

Perintah ini menghasilkan file SQL yang dapat digunakan untuk mengembalikan struktur dan data database. Pastikan pengguna database memiliki hak yang diperlukan dan prosedur backup disesuaikan apabila sistem memakai fitur database tambahan.

### 6.3 Verifikasi Backup

Setelah backup selesai:

1. Pastikan perintah berakhir tanpa error.
    
2. Pastikan file `pmb_backup.sql` tersedia.
    
3. Periksa ukuran file dan waktu pembuatannya.
    
4. Periksa log backup apabila tersedia.
    
5. Lakukan uji restore secara berkala ke database pengujian.
    

File yang berhasil dibuat belum tentu merupakan backup yang valid. Keberhasilan sebenarnya harus dibuktikan dengan uji pemulihan.

## 7. Prosedur Backup Dokumen Pendaftaran

Dokumen calon mahasiswa disimpan sebagai file pada penyimpanan privat, sedangkan database menyimpan metadata dan lokasi file.

Backup perlu mencakup direktori penyimpanan privat yang digunakan aplikasi, misalnya direktori penyimpanan dokumen pada Laravel. Lokasi aktual harus dikonfirmasi dari konfigurasi proyek.

Contoh pada Linux menggunakan `tar`:

```
tar -czf pmb_documents_backup.tar.gz \
  /PATH/KE/PENYIMPANAN_DOKUMEN
```

Ganti `/PATH/KE/PENYIMPANAN_DOKUMEN` dengan direktori yang benar. Periksa isi arsip dan pastikan proses backup tidak melewatkan file yang diperlukan.

Hal yang harus diperhatikan:

- Jangan membuat dokumen pendaftaran dapat diakses secara publik demi memudahkan backup.
    
- Pertahankan struktur direktori dan nama file yang diperlukan oleh aplikasi.
    
- Jangan mencadangkan file sementara yang tidak dibutuhkan kecuali ada alasan operasional.
    
- Lindungi arsip backup karena berisi data pribadi.
    
- Pastikan backup dokumen memiliki waktu pencadangan yang sesuai dengan backup database.
    

Jika dokumen berubah ketika backup berlangsung, gunakan prosedur snapshot atau mekanisme koordinasi agar hasil pemulihan tidak kehilangan file yang dirujuk oleh database.

## 7A. Prosedur Backup dan Restore Terverifikasi (Tahap 11)

Prosedur berikut sudah dijalankan pada lingkungan lokal; hasil dan angkanya
dicatat pada [[Tahap 11 - Local Deployment & Backup Report]].

### 7A.0 Driver yang aktif

Sejak cutover 29 Oktober 2026, database aplikasi adalah **MySQL 8.4.13** di
container Docker, sehingga `scripts/backup.sh` menghasilkan `database.sql`
(dump `mysqldump`) dan verifikasi restore membuat database scratch MySQL.
Prosedur SQLite di atas tetap relevan selama file SQLite lama dipakai sebagai
jalur rollback.

### 7A.1 Membuat backup

```
scripts/backup.sh
PMB_BACKUP_ROOT=/media/eksternal/pmb-backups scripts/backup.sh   # lokasi lain
```

Secara default backup ditulis ke `<root proyek>/../pmb_backups/<timestamp-UTC>/`,
yaitu di luar source code, dengan `umask 077` dan `chmod 700`/`600`.

> **Peringatan permission:** sebagian filesystem (misalnya mount NTFS/FAT) tidak
> menegakkan permission Unix. Pada kondisi tersebut skrip mencetak peringatan dan
> menulis `filesystem_enforces_permissions=no` pada manifest. Simpan backup pada
> filesystem yang menegakkan permission atau enkripsi arsipnya.

Isi satu backup:

|File|Isi|
|---|---|
|`database.sqlite` atau `database.sql`|Dump konsisten (SQLite `VACUUM INTO`, atau `mysqldump --single-transaction` tanpa `CREATE DATABASE`/`USE`)|
|`documents.tar.gz`|Arsip private storage dengan path yang sama seperti `file_path` pada database|
|`documents.sha256`|Checksum setiap file dokumen|
|`db-counts.json`|Jumlah baris per tabel saat backup diambil|
|`manifest.txt`|Waktu, host, versi framework/PHP, driver, jumlah tabel/file, status|
|`SHA256SUMS`|Checksum seluruh artefak|

Skrip memeriksa exit code, ukuran tidak nol, `PRAGMA integrity_check` (SQLite) atau
penanda `CREATE TABLE`/`Dump completed` (MySQL), jumlah entri arsip, dan
perubahan dokumen selama proses. Status `complete` hanya ditulis bila seluruh
pemeriksaan lolos.

### 7A.2 Memverifikasi backup dengan restore terisolasi

```
scripts/restore-verify.sh                 # backup terbaru
scripts/restore-verify.sh <folder-backup>
```

Skrip ini tidak menyentuh database maupun storage aktif. Data dipulihkan ke
`$TMPDIR/pmb-restore-work/<timestamp>`; untuk MySQL dibuat database scratch yang
dihapus kembali setelah verifikasi. Pemeriksaan mencakup checksum seluruh
artefak, checksum per file dokumen, jumlah baris per tabel, keberadaan setiap
file yang dirujuk database, serta uji akses dokumen melalui backend sementara
pada port 8011 (tanpa autentikasi, sebagai pemilik, sebagai calon mahasiswa
lain, dan ke endpoint admin).

Untuk backup MySQL, verifikasi memerlukan akun yang boleh membuat dan menghapus
database scratch:

```
PMB_RESTORE_DB_ADMIN_USER=root PMB_RESTORE_DB_ADMIN_PASSWORD=... scripts/restore-verify.sh
```

Tanpa akun tersebut skrip melaporkan `NOT RUN` dan tidak mengubah apa pun.

### 7A.3 Batasan yang diketahui

1. Arsip backup belum dienkripsi; lindungi melalui permission dan media aman.
2. Konsistensi dump database dan arsip dokumen bersifat *best effort*. Perubahan
   file selama proses dicatat pada `documents_changed_during_backup`. Untuk data
   produksi, jalankan backup pada maintenance window singkat.
3. Belum ada penjadwalan otomatis, retensi, maupun salinan off-site. Menyimpan
   semua backup pada mesin yang sama tidak melindungi dari kerusakan perangkat.
4. Restore ke database aktif tetap memerlukan persetujuan; jangan mengganti
   database atau dokumen aktif dengan hasil pengujian.

## 8. Perlindungan Backup

Backup harus diperlakukan sebagai data sensitif, bukan sebagai file biasa.

Langkah perlindungan yang diterapkan:

1. Batasi akses backup hanya kepada administrator yang berwenang.
    
2. Gunakan enkripsi saat menyimpan atau memindahkan backup yang mengandung data pribadi.
    
3. Simpan kunci enkripsi secara terpisah dari arsip backup.
    
4. Jangan memasukkan password, token, atau kunci aplikasi ke dalam repository Git.
    
5. Jangan mengunggah backup database atau dokumen ke layanan publik tanpa persetujuan dan perlindungan yang sesuai.
    
6. Catat waktu backup, lokasi, status proses, dan pihak yang melaksanakannya.
    
7. Pastikan penghapusan backup mengikuti kebijakan retensi.
    

Untuk server lokal, salinan pada media eksternal dapat digunakan sebagai lapisan tambahan. Media tersebut harus disimpan secara aman dan tidak terus-menerus terhubung jika risiko malware atau penghapusan tidak sengaja menjadi perhatian.

## 9. Prosedur Recovery Database

Recovery dilakukan apabila database utama rusak, data hilang, atau terjadi kesalahan yang membuat data tidak dapat digunakan.

### 9.1 Persiapan Pemulihan

Sebelum memulihkan data:

1. Hentikan sementara proses yang dapat menulis data baru ke database.
    
2. Catat masalah dan waktu kejadian.
    
3. Tentukan backup valid yang akan digunakan.
    
4. Amankan salinan database yang rusak jika diperlukan untuk investigasi.
    
5. Pastikan target pemulihan benar dan tidak berisi data yang masih dibutuhkan.
    
6. Informasikan kepada pihak yang bertanggung jawab atas layanan.
    

### 9.2 Membuat Database Target

Sebaiknya pemulihan diuji terlebih dahulu pada database terpisah.

Contoh membuat database pemulihan:

```
CREATE DATABASE pmb_recovery;
```

Perintah tersebut dijalankan melalui akun MySQL yang memiliki hak yang diperlukan. Nama database dapat disesuaikan dengan lingkungan pengujian.

### 9.3 Restore dari File SQL

Contoh pemulihan:

```
mysql -u USER_DATABASE -p pmb_recovery < pmb_backup.sql
```

Perintah ini mengimpor isi file SQL ke database `pmb_recovery`.

Jangan langsung mengimpor backup ke database produksi sebelum memastikan targetnya benar. Pemulihan dapat menimpa atau mengubah data yang sudah ada, bergantung pada isi file dan skema database.

### 9.4 Validasi Hasil Recovery

Setelah proses restore:

- Pastikan tabel yang dibutuhkan tersedia.
    
- Periksa jumlah data utama, termasuk pengguna, pendaftaran, dan riwayat status.
    
- Periksa hubungan foreign key dan konsistensi data.
    
- Pastikan nomor pendaftaran tidak mengalami duplikasi.
    
- Pastikan data status pendaftaran sesuai dengan riwayatnya.
    
- Jalankan pengujian API dan login menggunakan akun uji.
    
- Pastikan aplikasi dapat membaca data hasil pemulihan.
    

Jika validasi gagal, jangan mengganti database utama dengan hasil pemulihan tersebut sebelum masalah dianalisis dan diperbaiki.

## 10. Prosedur Recovery Dokumen

Pemulihan dokumen dilakukan apabila file pendaftaran hilang, rusak, atau tidak dapat dibaca.

Langkah-langkahnya:

1. Tentukan backup dokumen yang sesuai dengan waktu backup database.
    
2. Siapkan direktori pemulihan yang terpisah.
    
3. Ekstrak backup ke direktori tersebut.
    
4. Periksa keberadaan dan integritas file.
    
5. Bandingkan metadata database dengan file yang berhasil dipulihkan.
    
6. Pastikan hak akses dan izin direktori sesuai.
    
7. Uji akses dokumen melalui aplikasi menggunakan akun yang berwenang.
    
8. Setelah hasil valid, pindahkan atau pulihkan dokumen sesuai prosedur deployment.
    

Jangan menampilkan dokumen melalui URL publik hanya untuk menguji keberhasilannya. Pengujian harus tetap melewati mekanisme otorisasi aplikasi.

## 11. Recovery Aplikasi

Apabila kode sumber atau konfigurasi aplikasi mengalami kerusakan, pemulihan dilakukan dengan menggunakan versi kode yang diketahui baik dan konfigurasi yang sesuai.

Tahapan pemulihan:

1. Siapkan lingkungan server yang bersih.
    
2. Ambil versi kode sumber yang telah disetujui dari repository.
    
3. Pasang dependensi sesuai versi proyek.
    
4. Pulihkan konfigurasi lingkungan secara aman.
    
5. Pastikan koneksi database dan penyimpanan privat sudah benar.
    
6. Jalankan pemeriksaan konfigurasi Laravel.
    
7. Terapkan migrasi hanya jika skema backup dan versi aplikasi sudah dipastikan kompatibel.
    
8. Pulihkan database dan dokumen sesuai prosedur.
    
9. Jalankan smoke test dan pemeriksaan hak akses.
    
10. Aktifkan kembali layanan setelah validasi berhasil.
    

**Peringatan:** Jangan menjalankan migrasi terbaru secara otomatis pada database hasil restore tanpa menilai kompatibilitasnya. Migrasi yang tidak sesuai dapat menyebabkan perubahan skema atau kehilangan data.

## 12. Target Pemulihan

Dua ukuran yang dapat digunakan untuk menilai kesiapan recovery adalah RPO dan RTO.

|Istilah|Pengertian|Target Awal Usulan|
|---|---|---|
|RPO (Recovery Point Objective)|Batas kehilangan data yang masih dapat diterima, dihitung sebagai rentang waktu data.|Maksimal 24 jam untuk backup harian|
|RTO (Recovery Time Objective)|Target waktu untuk memulihkan layanan.|Maksimal 8 jam untuk pemulihan dasar|

Target di atas merupakan sasaran awal untuk proyek lokal, bukan jaminan pemulihan. Pencapaiannya harus diuji dengan kondisi perangkat, ukuran database, jumlah dokumen, dan kecepatan media penyimpanan yang sebenarnya.

Jika sistem nantinya digunakan untuk penerimaan mahasiswa secara aktif, target RPO dan RTO perlu ditinjau ulang. Backup lebih sering atau mekanisme replikasi mungkin diperlukan apabila kehilangan data hingga 24 jam tidak dapat diterima.

## 13. Pengujian Backup & Recovery

Pengujian berkala diperlukan untuk membuktikan bahwa backup benar-benar dapat dipakai.

|ID|Skenario Pengujian|Hasil yang Diharapkan|Status Awal|
|---|---|---|---|
|BR-001|Membuat backup database.|File backup dibuat tanpa error dan dapat dibaca.|**Pass** (29-10-2026)|
|BR-002|Memulihkan backup ke database pengujian.|Struktur dan data dapat dipulihkan.|**Pass** (SQLite dan MariaDB)|
|BR-003|Membuat backup dokumen pendaftaran.|File yang dibutuhkan tercakup dalam backup.|**Pass** (6 file, checksum per file)|
|BR-004|Memulihkan dokumen dari backup.|Dokumen dapat dibaca dan cocok dengan metadata database.|**Pass** (5/5 referensi)|
|BR-005|Memeriksa akses ke arsip backup.|Hanya pihak yang berwenang yang dapat mengaksesnya.|**Pass sebagian** — mode 700/600 diterapkan, tetapi tidak ditegakkan pada mount NTFS (fuseblk); enkripsi belum|
|BR-006|Menjalankan aplikasi setelah recovery.|Login, akses pendaftaran, status, dan dokumen berjalan sesuai hak akses.|**Pass** (uji akses dokumen pada hasil restore)|
|BR-007|Menguji skenario backup tidak valid.|Prosedur mendeteksi masalah dan tidak mengganti data utama dengan hasil yang rusak.|**Pass** (dump rusak, arsip terpotong, manifest gagal ditolak)|

Simpan tanggal pengujian, nama penguji, hasil aktual, bukti, durasi pemulihan, serta temuan yang harus diperbaiki.

## 14. Penanganan Kegagalan Backup

Jika backup gagal:

1. Catat pesan error dan waktu kejadian.
    
2. Periksa ruang penyimpanan, izin akses, koneksi database, dan status perangkat.
    
3. Pastikan proses tidak menghasilkan arsip yang tidak lengkap.
    
4. Jalankan ulang backup setelah penyebabnya ditangani.
    
5. Verifikasi hasil backup yang baru.
    
6. Jika backup gagal berulang kali, eskalasikan kepada pengelola teknis.
    
7. Jangan menghapus satu-satunya backup valid sebelum salinan pengganti diverifikasi.
    

Backup yang gagal harus dicatat sebagai insiden operasional agar penyebabnya dapat diperbaiki.

## 15. Checklist Pemulihan

Gunakan checklist berikut saat melakukan simulasi atau pemulihan aktual.

- Masalah dan waktu kejadian telah dicatat.
    
- Backup yang akan digunakan telah dipilih.
    
- Integritas dan kelengkapan backup telah diperiksa.
    
- Database telah dipulihkan ke target yang benar.
    
- Dokumen pendaftaran telah dipulihkan.
    
- Konsistensi antara metadata dan file dokumen telah diperiksa.
    
- Hak akses pengguna telah diverifikasi.
    
- API dan aplikasi telah diuji.
    
- Hasil pemulihan dan durasinya telah dicatat.
    
- Pihak yang berwenang telah menyetujui pengaktifan kembali layanan.
    

## 16. Catatan dan Keputusan yang Belum Ditetapkan

Hal-hal berikut perlu dipastikan sebelum implementasi final:

- Lokasi backup utama dan salinan terpisah.
    
- Jadwal backup otomatis dan pihak yang memantaunya.
    
- Media atau perangkat penyimpanan backup.
    
- Mekanisme enkripsi dan pengelolaan kunci.
    
- Masa retensi yang disetujui.
    
- Target RPO dan RTO berdasarkan kebutuhan operasional.
    
- Penanggung jawab backup, recovery, dan persetujuan pengaktifan layanan.
    
- Kebutuhan pencatatan log dan notifikasi ketika backup gagal.
    
- Prosedur khusus jika database dan dokumen rusak pada waktu yang sama.
    

## 17. Kesimpulan

Backup & Recovery merupakan bagian penting dari deployment sistem PMB ITB Akiday karena sistem menyimpan data akun, pendaftaran, status seleksi, dan dokumen pribadi calon mahasiswa. Pencadangan harus mencakup database MySQL dan file dokumen secara terkoordinasi, sedangkan proses pemulihan harus melalui validasi agar data kembali konsisten dan tetap terlindungi.

Dengan menjalankan backup secara berkala, menyimpan salinan secara aman, dan menguji pemulihan secara rutin, risiko kehilangan data dapat dikurangi. Sistem belum dapat dinyatakan memiliki mekanisme backup yang siap digunakan sampai prosedur tersebut benar-benar diterapkan dan hasil restore berhasil diverifikasi.

---

## 18. Dokumen Terkait

- [[Deployment Guide]]
    
- [[Environment Configuration]]
    
- [[Database Model]]
    
- [[ERD]]
    
- [[Data Dictionary]]
    
- [[Migration Plan]]
    
- [[Security Specification]]
    
- [[Access Control Matrix]]
    
- [[Test Plan]]
    
- [[Test Results]]