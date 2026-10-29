package com.example.pmbitbakiday.data.model

import com.example.pmbitbakiday.data.remote.RetrofitClient
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiResponseSerializationTest {

    private val gson = RetrofitClient.defaultGson

    @Test
    fun testDeserializeSuccessLoginResponse() {
        val json = """
            {
              "success": true,
              "message": "Login berhasil.",
              "data": {
                "user": {
                  "id": 101,
                  "email": "mahasiswa@example.com",
                  "role": "calon_mahasiswa"
                },
                "access_token": "1|sampleSanctumToken12345",
                "token_type": "Bearer"
              }
            }
        """.trimIndent()

        val type = object : TypeToken<ApiResponse<LoginData>>() {}.type
        val response: ApiResponse<LoginData> = gson.fromJson(json, type)

        assertTrue(response.success)
        assertEquals("Login berhasil.", response.message)
        assertNotNull(response.data)
        assertEquals("1|sampleSanctumToken12345", response.data?.accessToken)
        assertEquals("Bearer", response.data?.tokenType)
        assertEquals(101, response.data?.user?.id)
        assertEquals("mahasiswa@example.com", response.data?.user?.email)
        assertNull(response.error)
    }

    @Test
    fun testDeserializeErrorInvalidCredentials() {
        val json = """
            {
              "success": false,
              "message": "Email atau password tidak valid.",
              "error": {
                "code": "INVALID_CREDENTIALS"
              }
            }
        """.trimIndent()

        val type = object : TypeToken<ApiResponse<Any>>() {}.type
        val response: ApiResponse<Any> = gson.fromJson(json, type)

        assertFalse(response.success)
        assertEquals("Email atau password tidak valid.", response.message)
        assertNull(response.data)
        assertNotNull(response.error)
        assertEquals("INVALID_CREDENTIALS", response.error?.code)
    }

    @Test
    fun testDeserializeValidationErrorWithDetails() {
        val json = """
            {
              "success": false,
              "message": "Periksa kembali data yang dimasukkan.",
              "error": {
                "code": "VALIDATION_ERROR",
                "details": {
                  "email": ["Format email tidak valid."],
                  "password": ["Password minimal 8 karakter."]
                }
              }
            }
        """.trimIndent()

        val type = object : TypeToken<ApiResponse<Any>>() {}.type
        val response: ApiResponse<Any> = gson.fromJson(json, type)

        assertFalse(response.success)
        assertEquals("VALIDATION_ERROR", response.error?.code)
        val details = response.error?.details
        assertNotNull(details)
        assertEquals(1, details?.get("email")?.size)
        assertEquals("Format email tidak valid.", details?.get("email")?.get(0))
        assertEquals("Password minimal 8 karakter.", details?.get("password")?.get(0))
    }

    @Test
    fun testDeserializePaginatedApplications() {
        val json = """
            {
              "success": true,
              "message": "Daftar pengajuan berhasil diambil.",
              "data": [
                {
                  "id": 501,
                  "registration_number": "PMB-2026-000501",
                  "wave": {
                    "id": 1,
                    "name": "Gelombang 1"
                  },
                  "study_program": {
                    "id": 2,
                    "name": "Informatika"
                  },
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

        val type = object : TypeToken<ApiResponse<List<ApplicationItemDto>>>() {}.type
        val response: ApiResponse<List<ApplicationItemDto>> = gson.fromJson(json, type)

        assertTrue(response.success)
        assertEquals(1, response.data?.size)
        val item = response.data?.first()
        assertEquals(501, item?.id)
        assertEquals("PMB-2026-000501", item?.registrationNumber)
        assertEquals("Gelombang 1", item?.wave?.name)
        assertEquals("Informatika", item?.studyProgram?.name)
        assertEquals(1, response.meta?.currentPage)
        assertEquals(1, response.meta?.total)
    }
}
