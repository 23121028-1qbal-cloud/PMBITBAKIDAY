package com.example.pmbitbakiday.data.repository

import com.example.pmbitbakiday.data.remote.ApiService
import com.example.pmbitbakiday.data.remote.RetrofitClient
import com.example.pmbitbakiday.util.NetworkResult
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit

class ApplicationRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var applicationRepository: ApplicationRepository

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .build()

        val apiService: ApiService = RetrofitClient.createForTesting(
            okHttpClient,
            server.url("/api/v1/").toString()
        )

        applicationRepository = ApplicationRepository(apiService)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun testGetAdmissionWaves() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Data gelombang berhasil diambil.",
              "data": [
                {
                  "id": 1,
                  "name": "Gelombang 1 2026",
                  "start_date": "2026-01-01T00:00:00Z",
                  "end_date": "2026-12-31T23:59:59Z",
                  "is_active": true
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = applicationRepository.getAdmissionWaves()
        assertTrue(result is NetworkResult.Success)
        val list = (result as NetworkResult.Success).data
        assertEquals(1, list.size)
        assertEquals("Gelombang 1 2026", list[0].name)
        assertTrue(list[0].isActive)
    }

    @Test
    fun testGetStudyPrograms() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Data program studi berhasil diambil.",
              "data": [
                {
                  "id": 1,
                  "code": "IF",
                  "name": "Informatika"
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = applicationRepository.getStudyPrograms()
        assertTrue(result is NetworkResult.Success)
        val list = (result as NetworkResult.Success).data
        assertEquals(1, list.size)
        assertEquals("IF", list[0].code)
        assertEquals("Informatika", list[0].name)
    }

    @Test
    fun testCreateApplicationSuccess() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Draft pendaftaran berhasil dibuat.",
              "data": {
                "id": 10,
                "registration_number": "PMB-2026-000010",
                "wave_id": 1,
                "study_program_id": 1,
                "status": "draft",
                "submitted_at": null
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(201).setBody(json))

        val result = applicationRepository.createApplication(waveId = 1, studyProgramId = 1)
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(10, data.id)
        assertEquals("PMB-2026-000010", data.registrationNumber)
        assertEquals("draft", data.status)
    }

    @Test
    fun testGetMyApplications() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Daftar pengajuan berhasil diambil.",
              "data": [
                {
                  "id": 10,
                  "registration_number": "PMB-2026-000010",
                  "wave": { "id": 1, "name": "Gelombang 1" },
                  "study_program": { "id": 1, "name": "Informatika" },
                  "status": "draft",
                  "submitted_at": null
                }
              ],
              "meta": {
                "current_page": 1,
                "per_page": 20,
                "total": 1,
                "last_page": 1
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = applicationRepository.getMyApplications()
        assertTrue(result is NetworkResult.Success)
        val list = (result as NetworkResult.Success).data
        assertEquals(1, list.size)
        assertEquals("PMB-2026-000010", list[0].registrationNumber)
    }

    @Test
    fun testGetApplicationDetail() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Detail pengajuan berhasil diambil.",
              "data": {
                "id": 10,
                "registration_number": "PMB-2026-000010",
                "wave": { "id": 1, "name": "Gelombang 1" },
                "study_program": { "id": 1, "name": "Informatika" },
                "status": "draft",
                "submitted_at": null,
                "applicant": {
                  "full_name": "Budi Santoso",
                  "nik": "3201123456789012"
                }
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = applicationRepository.getApplicationDetail(10)
        assertTrue(result is NetworkResult.Success)
        val detail = (result as NetworkResult.Success).data
        assertEquals(10, detail.id)
        assertEquals("Budi Santoso", detail.applicant?.fullName)
    }

    @Test
    fun testSubmitApplicationSuccess() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Pendaftaran berhasil dikirim untuk pemeriksaan.",
              "data": {
                "id": 10,
                "registration_number": "PMB-2026-000010",
                "status": "submitted",
                "submitted_at": "2026-10-29T10:00:00+07:00"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = applicationRepository.submitApplication(id = 10, expectedStatus = "draft")
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(10, data.id)
        assertEquals("submitted", data.status)
        assertNotNull(data.submittedAt)
    }

    @Test
    fun testSubmitApplicationConflictReturns409() = runBlocking {
        val json = """
            {
              "success": false,
              "message": "Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.",
              "error": {
                "code": "STATE_CONFLICT"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(409).setBody(json))

        val result = applicationRepository.submitApplication(id = 10, expectedStatus = "draft")
        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(409, error.httpStatusCode)
        assertEquals("STATE_CONFLICT", error.code)
    }

    @Test
    fun testResubmitApplicationSuccess() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Perbaikan berhasil dikirim untuk diperiksa kembali.",
              "data": {
                "id": 10,
                "status": "review",
                "submitted_at": "2026-10-29T10:00:00+07:00"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = applicationRepository.resubmitApplication(id = 10, expectedStatus = "revision")
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals("review", data.status)
    }

    @Test
    fun testResubmitApplicationConflictReturns409() = runBlocking {
        val json = """
            {
              "success": false,
              "message": "Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.",
              "error": {
                "code": "STATE_CONFLICT"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(409).setBody(json))

        val result = applicationRepository.resubmitApplication(id = 10, expectedStatus = "revision")
        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(409, error.httpStatusCode)
        assertEquals("STATE_CONFLICT", error.code)
    }

    @Test
    fun testUploadDocumentSuccess() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Dokumen berhasil diunggah.",
              "data": {
                "id": 5,
                "application_id": 10,
                "document_type": "ktp",
                "file_name": "doc_ktp_654321.pdf",
                "file_size": 102400,
                "uploaded_at": "2026-10-29T10:05:00+07:00"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(201).setBody(json))

        val tempFile = File.createTempFile("test_ktp", ".pdf")
        tempFile.writeText("Dummy PDF content")

        try {
            val result = applicationRepository.uploadDocument(
                applicationId = 10,
                documentType = "ktp",
                file = tempFile,
                mimeType = "application/pdf"
            )

            assertTrue(result is NetworkResult.Success)
            val doc = (result as NetworkResult.Success).data
            assertEquals(5, doc.id)
            assertEquals("ktp", doc.documentType)
            assertEquals("doc_ktp_654321.pdf", doc.fileName)
            assertEquals(102400L, doc.fileSize)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun testGetDocumentsSuccess() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Daftar dokumen berhasil diambil.",
              "data": [
                {
                  "id": 5,
                  "application_id": 10,
                  "document_type": "ktp",
                  "file_name": "doc_ktp_654321.pdf",
                  "file_size": 102400,
                  "uploaded_at": "2026-10-29T10:05:00+07:00"
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = applicationRepository.getDocuments(10)
        assertTrue(result is NetworkResult.Success)
        val list = (result as NetworkResult.Success).data
        assertEquals(1, list.size)
        assertEquals("ktp", list[0].documentType)
    }

    @Test
    fun testGetStatusHistorySuccess() = runBlocking {
        val json = """
            {
              "success": true,
              "message": "Riwayat status berhasil diambil.",
              "data": [
                {
                  "from_status": null,
                  "to_status": "draft",
                  "note": "Draft pendaftaran dibuat.",
                  "changed_at": "2026-10-29T09:00:00+07:00"
                },
                {
                  "from_status": "draft",
                  "to_status": "submitted",
                  "note": "Pendaftaran berhasil dikirim.",
                  "changed_at": "2026-10-29T10:00:00+07:00"
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val result = applicationRepository.getStatusHistory(10)
        assertTrue(result is NetworkResult.Success)
        val history = (result as NetworkResult.Success).data
        assertEquals(2, history.size)
        assertEquals("draft", history[0].toStatus)
        assertEquals("submitted", history[1].toStatus)
    }
}
