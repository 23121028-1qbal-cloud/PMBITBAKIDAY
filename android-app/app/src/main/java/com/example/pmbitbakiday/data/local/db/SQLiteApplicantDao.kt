package com.example.pmbitbakiday.data.local.db

import android.content.ContentValues
import android.database.Cursor
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_ADDRESS
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_BIRTH_DATE
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_BIRTH_PLACE
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_CREATED_AT
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_FULL_NAME
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_GENDER
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_ID
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_IS_AGREED
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_NIK
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_PHONE
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_PHOTO_URI
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.COL_STUDY_PROGRAM
import com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper.Companion.TABLE_APPLICANTS

/**
 * Implementasi DAO menggunakan database SQLite lokal Android.
 */
class SQLiteApplicantDao(private val dbHelper: ApplicantDatabaseHelper) : ApplicantDao {

    override fun insert(applicant: ApplicantLocalEntity): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COL_FULL_NAME, applicant.fullName)
            put(COL_NIK, applicant.nik)
            put(COL_BIRTH_PLACE, applicant.birthPlace)
            put(COL_BIRTH_DATE, applicant.birthDate)
            put(COL_GENDER, applicant.gender)
            put(COL_PHONE, applicant.phone)
            put(COL_ADDRESS, applicant.address)
            put(COL_STUDY_PROGRAM, applicant.studyProgram)
            put(COL_PHOTO_URI, applicant.photoUri)
            put(COL_IS_AGREED, if (applicant.isAgreed) 1 else 0)
            put(COL_CREATED_AT, applicant.createdAt)
        }
        return db.insert(TABLE_APPLICANTS, null, values)
    }

    override fun getAll(): List<ApplicantLocalEntity> {
        val list = mutableListOf<ApplicantLocalEntity>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_APPLICANTS,
            null,
            null,
            null,
            null,
            null,
            "$COL_CREATED_AT DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToEntity(it))
            }
        }
        return list
    }

    override fun getById(id: Long): ApplicantLocalEntity? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_APPLICANTS,
            null,
            "$COL_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) cursorToEntity(it) else null
        }
    }

    override fun update(applicant: ApplicantLocalEntity): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COL_FULL_NAME, applicant.fullName)
            put(COL_NIK, applicant.nik)
            put(COL_BIRTH_PLACE, applicant.birthPlace)
            put(COL_BIRTH_DATE, applicant.birthDate)
            put(COL_GENDER, applicant.gender)
            put(COL_PHONE, applicant.phone)
            put(COL_ADDRESS, applicant.address)
            put(COL_STUDY_PROGRAM, applicant.studyProgram)
            put(COL_PHOTO_URI, applicant.photoUri)
            put(COL_IS_AGREED, if (applicant.isAgreed) 1 else 0)
        }
        return db.update(
            TABLE_APPLICANTS,
            values,
            "$COL_ID = ?",
            arrayOf(applicant.id.toString())
        )
    }

    override fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            TABLE_APPLICANTS,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }

    override fun deleteAll(): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_APPLICANTS, null, null)
    }

    override fun count(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_APPLICANTS", null)
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    private fun cursorToEntity(cursor: Cursor): ApplicantLocalEntity {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID))
        val fullName = cursor.getString(cursor.getColumnIndexOrThrow(COL_FULL_NAME))
        val nik = cursor.getString(cursor.getColumnIndexOrThrow(COL_NIK))
        val birthPlace = cursor.getString(cursor.getColumnIndexOrThrow(COL_BIRTH_PLACE))
        val birthDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_BIRTH_DATE))
        val gender = cursor.getString(cursor.getColumnIndexOrThrow(COL_GENDER))
        val phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE))
        val address = cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS))
        val studyProgram = cursor.getString(cursor.getColumnIndexOrThrow(COL_STUDY_PROGRAM))
        val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_PHOTO_URI))
        val isAgreed = cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_AGREED)) == 1
        val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT))

        return ApplicantLocalEntity(
            id = id,
            fullName = fullName,
            nik = nik,
            birthPlace = birthPlace,
            birthDate = birthDate,
            gender = gender,
            phone = phone,
            address = address,
            studyProgram = studyProgram,
            photoUri = photoUri,
            isAgreed = isAgreed,
            createdAt = createdAt
        )
    }
}
