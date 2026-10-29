package com.example.pmbitbakiday.data.local.db

/**
 * Data Access Object (DAO) untuk operasi CRUD lengkap pada database SQLite lokal.
 */
interface ApplicantDao {
    fun insert(applicant: ApplicantLocalEntity): Long
    fun getAll(): List<ApplicantLocalEntity>
    fun getById(id: Long): ApplicantLocalEntity?
    fun update(applicant: ApplicantLocalEntity): Int
    fun delete(id: Long): Int
    fun deleteAll(): Int
    fun count(): Int
}
