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
import java.util.concurrent.TimeUnit

class BaseRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var repository: TestRepository

    class TestRepository(private val service: ApiService) : BaseRepository() {
        suspend fun testGetMe() = safeApiCall { service.getMe() }
        suspend fun testGetWaves() = safeApiCall { service.getAdmissionWaves() }
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .build()

        apiService = RetrofitClient.createForTesting(okHttpClient, server.url("/api/v1/").toString())
        repository = TestRepository(apiService)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun testSuccessfulResponseReturnsSuccessNetworkResult() = runBlocking {
        val successJson = """
            {
              "success": true,
              "message": "Data gelombang berhasil diambil.",
              "data": [
                {
                  "id": 1,
                  "name": "Gelombang 1",
                  "start_date": "2026-11-01T00:00:00+07:00",
                  "end_date": "2026-12-15T23:59:59+07:00",
                  "is_active": true
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(successJson))

        val result = repository.testGetWaves()
        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(1, data.size)
        assertEquals("Gelombang 1", data[0].name)
    }

    @Test
    fun testErrorInvalidCredentialsMappedProperly() = runBlocking {
        val errorJson = """
            {
              "success": false,
              "message": "Email atau password tidak valid.",
              "error": {
                "code": "INVALID_CREDENTIALS"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(401).setBody(errorJson))

        val result = repository.testGetMe()
        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(401, error.httpStatusCode)
        assertEquals("INVALID_CREDENTIALS", error.code)
        assertEquals("Email atau password tidak valid.", error.message)
    }

    @Test
    fun testErrorStateConflictMappedProperly() = runBlocking {
        val conflictJson = """
            {
              "success": false,
              "message": "Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali.",
              "error": {
                "code": "STATE_CONFLICT"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(409).setBody(conflictJson))

        val result = repository.testGetMe()
        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(409, error.httpStatusCode)
        assertEquals("STATE_CONFLICT", error.code)
    }

    @Test
    fun testValidationErrorWithDetailsParsedProperly() = runBlocking {
        val validationJson = """
            {
              "success": false,
              "message": "Periksa kembali data yang dimasukkan.",
              "error": {
                "code": "VALIDATION_ERROR",
                "details": {
                  "nik": ["Bidang NIK harus berisi 16 digit."]
                }
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(422).setBody(validationJson))

        val result = repository.testGetMe()
        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(422, error.httpStatusCode)
        assertEquals("VALIDATION_ERROR", error.code)
        assertNotNull(error.error?.details)
        assertEquals("Bidang NIK harus berisi 16 digit.", error.error?.details?.get("nik")?.get(0))
    }

    @Test
    fun testHttp500ServerErrorFallback() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500).setBody("<html>Internal Server Error</html>"))

        val result = repository.testGetMe()
        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(500, error.httpStatusCode)
        assertEquals("INTERNAL_SERVER_ERROR", error.code)
        assertEquals("Terjadi kesalahan pada server. Silakan coba kembali nanti.", error.message)
    }
}
