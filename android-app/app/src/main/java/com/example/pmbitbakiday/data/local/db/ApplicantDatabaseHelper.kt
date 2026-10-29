package com.example.pmbitbakiday.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLite Database Helper untuk mengelola tabel data pendaftaran mahasiswa baru di penyimpanan lokal Android.
 */
class ApplicantDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "pmb_itbakiday_local.db"
        const val DATABASE_VERSION = 1

        const val TABLE_APPLICANTS = "applicants"
        const val COL_ID = "id"
        const val COL_FULL_NAME = "full_name"
        const val COL_NIK = "nik"
        const val COL_BIRTH_PLACE = "birth_place"
        const val COL_BIRTH_DATE = "birth_date"
        const val COL_GENDER = "gender"
        const val COL_PHONE = "phone"
        const val COL_ADDRESS = "address"
        const val COL_STUDY_PROGRAM = "study_program"
        const val COL_PHOTO_URI = "photo_uri"
        const val COL_IS_AGREED = "is_agreed"
        const val COL_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSql = """
            CREATE TABLE $TABLE_APPLICANTS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FULL_NAME TEXT NOT NULL,
                $COL_NIK TEXT NOT NULL,
                $COL_BIRTH_PLACE TEXT NOT NULL,
                $COL_BIRTH_DATE TEXT NOT NULL,
                $COL_GENDER TEXT NOT NULL,
                $COL_PHONE TEXT NOT NULL,
                $COL_ADDRESS TEXT NOT NULL,
                $COL_STUDY_PROGRAM TEXT NOT NULL,
                $COL_PHOTO_URI TEXT,
                $COL_IS_AGREED INTEGER NOT NULL DEFAULT 0,
                $COL_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPLICANTS")
        onCreate(db)
    }
}
