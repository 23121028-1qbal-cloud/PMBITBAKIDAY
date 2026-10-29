package com.example.pmbitbakiday.data.local.db

/**
 * Entitas lokal data pendaftar PMB ITB Akiday untuk database SQLite/Room lokal.
 * Memuat beragam tipe inputan: Teks, Angka, Tanggal, Pilihan (Radio & Dropdown), Gambar, dan Checkbox.
 */
data class ApplicantLocalEntity(
    val id: Long = 0L,
    val fullName: String,           // Input Teks (Nama Lengkap)
    val nik: String,                // Input Angka (NIK 16 digit)
    val birthPlace: String,         // Input Teks (Tempat Lahir)
    val birthDate: String,          // Input Tanggal (YYYY-MM-DD)
    val gender: String,             // Input Pilihan Radio Button ("Laki-laki" / "Perempuan")
    val phone: String,              // Input Angka (Nomor HP/WA)
    val address: String,            // Input Teks (Alamat Domisili)
    val studyProgram: String,       // Input Pilihan Dropdown (Program Studi)
    val photoUri: String? = null,   // Input Gambar (URI Foto Calon Mahasiswa)
    val isAgreed: Boolean = false,  // Input Checkbox / Switch (Persetujuan Ketentuan)
    val createdAt: Long = System.currentTimeMillis()
)
