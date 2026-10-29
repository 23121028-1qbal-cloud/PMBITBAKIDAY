package com.example.pmbitbakiday.data.repository

import com.example.pmbitbakiday.data.model.ProfileRequest
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

class ProfileRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var profileRepository: ProfileRepository

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

        profileRepository = ProfileRepository(apiService)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun testGetProfileSuccess() = runBlocking {
        val profileJson = """
            {
              "success": true,
              "message": "Profil berhasil diambil.",
              "data": {
                "user_id": 1,
                "full_name": "Ahmad Dani",
                "nik": "3201123456789012",
                "birth_place": "Bandung",
                "birth_date": "2005-08-17",
                "gender": "L",
                "phone": "081234567890",
                "address": "Jl. Ganesha No. 10 Bandung"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(profileJson))

        val result = profileRepository.getProfile()

        assertTrue(result is NetworkResult.Success)
        val profile = (result as NetworkResult.Success).data
        assertEquals("Ahmad Dani", profile.fullName)
        assertEquals("3201123456789012", profile.nik)
        assertEquals("Bandung", profile.birthPlace)
        assertEquals("2005-08-17", profile.birthDate)
        assertEquals("L", profile.gender)
    }

    @Test
    fun testUpdateProfileSuccess() = runBlocking {
        val updatedJson = """
            {
              "success": true,
              "message": "Profil berhasil disimpan.",
              "data": {
                "user_id": 1,
                "full_name": "Ahmad Dani Updated",
                "nik": "3201123456789012",
                "birth_place": "Bandung",
                "birth_date": "2005-08-17",
                "gender": "L",
                "phone": "081299998888",
                "address": "Jl. Dago No. 100"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(updatedJson))

        val request = ProfileRequest(
            fullName = "Ahmad Dani Updated",
            nik = "3201123456789012",
            birthPlace = "Bandung",
            birthDate = "2005-08-17",
            gender = "L",
            phone = "081299998888",
            address = "Jl. Dago No. 100"
        )

        val result = profileRepository.updateProfile(request)

        assertTrue(result is NetworkResult.Success)
        val profile = (result as NetworkResult.Success).data
        assertEquals("Ahmad Dani Updated", profile.fullName)
        assertEquals("081299998888", profile.phone)
    }

    @Test
    fun testUpdateProfileValidationError() = runBlocking {
        val errorJson = """
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

        server.enqueue(MockResponse().setResponseCode(422).setBody(errorJson))

        val request = ProfileRequest(
            fullName = "Invalid",
            nik = "123",
            birthPlace = "Bandung",
            birthDate = "2005-08-17",
            gender = "L",
            phone = "081234567890",
            address = "Alamat"
        )

        val result = profileRepository.updateProfile(request)

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(422, error.httpStatusCode)
        assertEquals("VALIDATION_ERROR", error.code)
        assertNotNull(error.error?.details)
        assertTrue(error.error?.details?.containsKey("nik") == true)
    }
}
