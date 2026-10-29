## 1. Informasi Dokumen

|Item|Keterangan|
|---|---|
|Nama Produk|PMB ITB Akiday|
|Jenis Dokumen|Release Notes|
|Versi Dokumen|1.0|
|Versi Rilis Aplikasi|0.1.0-alpha|
|Status Rilis|Draft / Pengembangan|
|Platform|Android, Website Admin, REST API|
|Backend|Laravel|
|Database|MySQL|

> **Catatan:** Versi `0.1.0-alpha` merupakan penanda awal yang diusulkan untuk proyek. Versi ini bukan bukti bahwa aplikasi sudah selesai atau dirilis kepada pengguna.

## 2. Gambaran Umum

PMB ITB Akiday merupakan sistem penerimaan mahasiswa baru yang dirancang untuk mempermudah calon mahasiswa melakukan pendaftaran secara online. Sistem ini terdiri dari aplikasi Android untuk calon mahasiswa dan website admin untuk mengelola proses penerimaan mahasiswa baru.

Sistem dirancang agar proses pendaftaran, pengunggahan dokumen, pemeriksaan berkas, permintaan revisi, pemantauan status, dan pengambilan keputusan penerimaan dapat dilakukan secara terpusat. Panitia tetap bertanggung jawab melakukan pemeriksaan dan menentukan hasil akhir penerimaan mahasiswa.

## 3. Tujuan Rilis

Rilis awal direncanakan sebagai dasar pengembangan dan pengujian fitur utama, meliputi:

- Autentikasi dan pengelolaan hak akses pengguna.
    
- Pengelolaan profil calon mahasiswa.
    
- Informasi gelombang pendaftaran dan program studi.
    
- Pengisian serta pengiriman formulir pendaftaran.
    
- Pengunggahan dan pemeriksaan dokumen persyaratan.
    
- Pemantauan status pendaftaran.
    
- Pengelolaan proses verifikasi dan keputusan oleh panitia.
    
- Dashboard serta laporan penerimaan mahasiswa baru.
    

Fitur-fitur tersebut masih harus disesuaikan dengan hasil implementasi dan pengujian aktual sebelum dinyatakan tersedia pada suatu rilis.

## 4. Riwayat Versi

### Versi 0.1.0-alpha — Rilis Awal yang Direncanakan

**Status:** Dalam pengembangan.

#### Fitur yang direncanakan

- **Autentikasi:** calon mahasiswa dan pengguna internal dapat masuk menggunakan email dan kata sandi sesuai hak aksesnya.
    
- **Profil calon mahasiswa:** pengelolaan data identitas dan informasi kontak.
    
- **Gelombang pendaftaran:** pengaturan periode pendaftaran oleh admin.
    
- **Program studi:** penyediaan daftar program studi yang dapat dipilih calon mahasiswa.
    
- **Pendaftaran:** pengisian formulir dan pengiriman data pendaftaran.
    
- **Dokumen persyaratan:** pengunggahan foto dan ijazah atau Surat Keterangan Lulus (SKL).
    
- **Status pendaftaran:** pemantauan tahapan proses pendaftaran.
    
- **Verifikasi panitia:** pemeriksaan berkas dan permintaan perbaikan apabila diperlukan.
    
- **Keputusan penerimaan:** pencatatan keputusan diterima atau ditolak oleh panitia yang berwenang.
    
- **Dashboard dan laporan:** penyajian statistik serta laporan penerimaan.
    

#### Aturan bisnis utama

1. Satu akun calon mahasiswa hanya dapat memiliki satu pendaftaran pada gelombang yang sama.
    
2. Calon mahasiswa dapat mendaftar pada gelombang lain apabila ketentuan pendaftaran mengizinkannya.
    
3. Data dan dokumen wajib divalidasi sebelum pendaftaran dikirim.
    
4. Calon mahasiswa hanya dapat melihat dan mengelola data pendaftarannya sendiri sesuai aturan sistem.
    
5. Keputusan akhir penerimaan ditentukan oleh panitia, bukan oleh validasi otomatis sistem.
    
6. Riwayat perubahan status perlu disimpan agar proses pemeriksaan dapat ditelusuri.
    

#### Catatan teknis

- Backend direncanakan menggunakan Laravel.
    
- Database utama menggunakan MySQL.
    
- Aplikasi Android dikembangkan menggunakan Kotlin.
    
- Komunikasi antara aplikasi dan backend menggunakan REST API dengan format JSON.
    
- Dokumen persyaratan disimpan pada penyimpanan privat, sedangkan database menyimpan metadata dan lokasi berkas.
    
- Data pendaftaran pada gelombang yang sama harus dilindungi dari duplikasi, termasuk ketika beberapa permintaan dikirim secara bersamaan.
    

## 5. Perubahan dari Versi Sebelumnya

Versi `0.1.0-alpha` merupakan versi awal yang direncanakan sehingga belum memiliki versi rilis sebelumnya untuk dibandingkan.

Bagian ini akan diperbarui pada rilis selanjutnya untuk mencatat fitur baru, perubahan perilaku, perbaikan bug, dan perubahan teknis.

## 6. Batasan dan Hal yang Belum Dipastikan

Hal-hal berikut harus dikonfirmasi sebelum aplikasi dinyatakan siap digunakan:

- Jadwal pembukaan dan penutupan setiap gelombang pendaftaran.
    
- Daftar resmi program studi yang tersedia.
    
- Aturan keunikan NIK.
    
- Format berkas dan batas ukuran dokumen.
    
- Batas waktu serta jumlah kesempatan revisi dokumen.
    
- Ketentuan perubahan keputusan penerimaan.
    
- Isi bukti atau nomor registrasi yang diterima calon mahasiswa.
    
- Versi minimum Android yang didukung.
    
- Target performa dan kapasitas pengguna.
    
- Kebijakan retensi data dan pencadangan.
    
- Mekanisme autentikasi API serta konfigurasi server final.
    

Daftar program studi yang digunakan selama pengembangan harus dianggap sebagai data prototipe sampai dikonfirmasi berdasarkan informasi resmi institusi.

## 7. Status Pengujian

Pengujian harus dilakukan sebelum versi dirilis kepada pengguna. Status pengujian perlu dicatat berdasarkan hasil aktual, bukan berdasarkan rencana atau asumsi.

Area pengujian utama:

- Registrasi, login, dan hak akses pengguna.
    
- Validasi formulir dan dokumen.
    
- Pencegahan pendaftaran ganda pada gelombang yang sama.
    
- Kemampuan mendaftar pada gelombang berbeda sesuai ketentuan.
    
- Pemeriksaan, revisi, dan keputusan pendaftaran.
    
- Perlindungan data pribadi serta dokumen.
    
- Konsistensi riwayat status dan laporan.
    
- Penanganan kesalahan API dan kondisi jaringan.
    
- Pencadangan dan pemulihan data.
    

**Status pengujian saat dokumen dibuat:** belum dinyatakan lulus. Hasil akhirnya harus diperbarui setelah pengujian benar-benar dilaksanakan dan bukti pengujian tersedia.

## 8. Kriteria Kesiapan Rilis

Rilis dapat dipertimbangkan untuk digunakan setelah:

- Fitur wajib telah diimplementasikan dan diperiksa.
    
- Pengujian prioritas tinggi telah dijalankan.
    
- Tidak ada bug kritis yang belum diselesaikan.
    
- Hak akses dan kepemilikan data telah diuji.
    
- Pencegahan pendaftaran ganda telah diverifikasi.
    
- Dokumen persyaratan tidak dapat diakses oleh pengguna yang tidak berwenang.
    
- Proses pencadangan dan pemulihan telah diuji.
    
- Konfigurasi lingkungan dan keamanan telah ditinjau.
    
- Panduan pengguna dan admin telah disesuaikan dengan aplikasi aktual.
    

## 9. Rencana Rilis Berikutnya

Rilis berikutnya akan ditentukan berdasarkan hasil pengembangan dan pengujian versi awal. Prioritasnya adalah menyelesaikan fitur inti, memperbaiki temuan pengujian, memastikan keamanan data, dan memvalidasi aturan bisnis bersama pihak terkait.

Setiap rilis berikutnya perlu mencatat:

- Nomor versi dan tanggal rilis.
    
- Fitur baru atau yang diubah.
    
- Bug yang diperbaiki.
    
- Masalah yang masih diketahui.
    
- Perubahan database atau API yang berdampak pada kompatibilitas.
    
- Status pengujian dan persetujuan rilis.
    

## 10. Riwayat Perubahan Dokumen

|Versi Dokumen|Perubahan|Status|
|---|---|---|
|1.0|Penyusunan awal Release Notes untuk rilis alpha yang direncanakan|Draft|

---

**Peringatan:** Dokumen ini menjelaskan rencana dan target rilis. Ubah status menjadi tersedia, selesai, atau lulus pengujian hanya setelah implementasi dan verifikasi aktual dilakukan.