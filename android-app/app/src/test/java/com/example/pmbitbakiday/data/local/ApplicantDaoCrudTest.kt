package com.example.pmbitbakiday.data.local

import com.example.pmbitbakiday.data.local.db.ApplicantDao
import com.example.pmbitbakiday.data.local.db.ApplicantLocalEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ApplicantDaoCrudTest {

    private lateinit var dao: FakeApplicantDao

    class FakeApplicantDao : ApplicantDao {
        private val storage = mutableMapOf<Long, ApplicantLocalEntity>()
        private var nextId = 1L

        override fun insert(applicant: ApplicantLocalEntity): Long {
            val id = if (applicant.id == 0L) nextId++ else applicant.id
            storage[id] = applicant.copy(id = id)
            return id
        }

        override fun getAll(): List<ApplicantLocalEntity> {
            return storage.values.sortedByDescending { it.createdAt }
        }

        override fun getById(id: Long): ApplicantLocalEntity? {
            return storage[id]
        }

        override fun update(applicant: ApplicantLocalEntity): Int {
            return if (storage.containsKey(applicant.id)) {
                storage[applicant.id] = applicant
                1
            } else 0
        }

        override fun delete(id: Long): Int {
            return if (storage.remove(id) != null) 1 else 0
        }

        override fun deleteAll(): Int {
            val size = storage.size
            storage.clear()
            return size
        }

        override fun count(): Int = storage.size
    }

    @Before
    fun setUp() {
        dao = FakeApplicantDao()
    }

    @Test
    fun testCreateApplicant() {
        val applicant = ApplicantLocalEntity(
            fullName = "Ahmad Dahlan",
            nik = "3201010101010001",
            birthPlace = "Bandung",
            birthDate = "2006-05-10",
            gender = "Laki-laki",
            phone = "081234567890",
            address = "Jl. Sukajadi No. 12",
            studyProgram = "Teknik Informatika",
            isAgreed = true
        )

        val id = dao.insert(applicant)
        assertTrue(id > 0)
        assertEquals(1, dao.count())
    }

    @Test
    fun testReadApplicant() {
        val id = dao.insert(
            ApplicantLocalEntity(
                fullName = "Siti Fatimah",
                nik = "3201010101010002",
                birthPlace = "Jakarta",
                birthDate = "2007-02-14",
                gender = "Perempuan",
                phone = "081987654321",
                address = "Jl. Merdeka No. 45",
                studyProgram = "Sistem Informasi",
                isAgreed = true
            )
        )

        val retrieved = dao.getById(id)
        assertNotNull(retrieved)
        assertEquals("Siti Fatimah", retrieved?.fullName)
        assertEquals("3201010101010002", retrieved?.nik)
        assertEquals("Sistem Informasi", retrieved?.studyProgram)

        val all = dao.getAll()
        assertEquals(1, all.size)
    }

    @Test
    fun testUpdateApplicant() {
        val id = dao.insert(
            ApplicantLocalEntity(
                fullName = "Budi Santoso",
                nik = "3201010101010003",
                birthPlace = "Surabaya",
                birthDate = "2006-08-17",
                gender = "Laki-laki",
                phone = "082111222333",
                address = "Jl. Pahlawan No. 1",
                studyProgram = "Bisnis Digital",
                isAgreed = true
            )
        )

        val original = dao.getById(id)!!
        val updated = original.copy(
            fullName = "Budi Santoso, S.Kom",
            studyProgram = "Teknik Informatika",
            address = "Jl. Pahlawan No. 99"
        )

        val rowsAffected = dao.update(updated)
        assertEquals(1, rowsAffected)

        val result = dao.getById(id)
        assertEquals("Budi Santoso, S.Kom", result?.fullName)
        assertEquals("Teknik Informatika", result?.studyProgram)
        assertEquals("Jl. Pahlawan No. 99", result?.address)
    }

    @Test
    fun testDeleteApplicant() {
        val id = dao.insert(
            ApplicantLocalEntity(
                fullName = "Dewi Lestari",
                nik = "3201010101010004",
                birthPlace = "Yogyakarta",
                birthDate = "2007-11-20",
                gender = "Perempuan",
                phone = "085666777888",
                address = "Jl. Malioboro No. 10",
                studyProgram = "Manajemen Informatika",
                isAgreed = true
            )
        )

        assertEquals(1, dao.count())
        val deletedRows = dao.delete(id)
        assertEquals(1, deletedRows)
        assertEquals(0, dao.count())
        assertNull(dao.getById(id))
    }
}
