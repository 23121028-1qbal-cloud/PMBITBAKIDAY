package com.example.pmbitbakiday.data.repository

import com.example.pmbitbakiday.data.local.AuthTokenManager
import com.example.pmbitbakiday.data.remote.ApiService
import com.example.pmbitbakiday.data.remote.RetrofitClient
import com.example.pmbitbakiday.util.NetworkResult
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var fakeTokenManager: FakeTokenManager
    private lateinit var authRepository: AuthRepository

    class FakeTokenManager(var storedToken: String? = null) : AuthTokenManager {
        var storedEmail: String? = null

        override fun saveToken(token: String) { storedToken = token }
        override fun getToken(): String? = storedToken
        override fun clearToken() { storedToken = null }
        override fun hasToken(): Boolean = !storedToken.isNullOrBlank()
        override fun saveUserEmail(email: String) { storedEmail = email }
        override fun getUserEmail(): String? = storedEmail
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        fakeTokenManager = FakeTokenManager()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .build()

        val apiService: ApiService = RetrofitClient.createForTesting(
            okHttpClient,
            server.url("/api/v1/").toString()
        )

        authRepository = AuthRepository(apiService, fakeTokenManager)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun testLoginSuccessSavesTokenAndEmail() = runBlocking {
        val successJson = """
            {
              "success": true,
              "message": "Login berhasil.",
              "data": {
                "user": {
                  "id": 1,
                  "email": "mhs@example.com",
                  "role": "calon_mahasiswa"
                },
                "access_token": "token_abc_123",
                "token_type": "Bearer"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(successJson))

        val result = authRepository.login("mhs@example.com", "Secret123!")

        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals("token_abc_123", data.accessToken)
        assertEquals("mhs@example.com", data.user.email)

        // Verifikasi token dan email tersimpan di token manager
        assertEquals("token_abc_123", fakeTokenManager.getToken())
        assertEquals("mhs@example.com", fakeTokenManager.getUserEmail())
        assertTrue(authRepository.isLoggedIn())
        assertEquals("mhs@example.com", authRepository.getCurrentEmail())
    }

    @Test
    fun testLoginFailureReturnsErrorAndDoesNotSaveToken() = runBlocking {
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

        val result = authRepository.login("mhs@example.com", "WrongPassword")

        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(401, error.httpStatusCode)
        assertEquals("INVALID_CREDENTIALS", error.code)
        assertNull(fakeTokenManager.getToken())
        assertFalse(authRepository.isLoggedIn())
    }

    @Test
    fun testRegisterSuccess() = runBlocking {
        val registerJson = """
            {
              "success": true,
              "message": "Akun berhasil dibuat.",
              "data": {
                "id": 10,
                "email": "baru@example.com",
                "role": "calon_mahasiswa"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(201).setBody(registerJson))

        val result = authRepository.register("baru@example.com", "Password123!", "Password123!")

        assertTrue(result is NetworkResult.Success)
        val data = (result as NetworkResult.Success).data
        assertEquals(10, data.id)
        assertEquals("baru@example.com", data.email)
        assertEquals("calon_mahasiswa", data.role)
    }

    @Test
    fun testGetMeSuccess() = runBlocking {
        val meJson = """
            {
              "success": true,
              "message": "Data akun berhasil diambil.",
              "data": {
                "id": 1,
                "email": "mhs@example.com",
                "role": "calon_mahasiswa"
              }
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(meJson))

        val result = authRepository.getMe()

        assertTrue(result is NetworkResult.Success)
        val user = (result as NetworkResult.Success).data
        assertEquals(1, user.id)
        assertEquals("mhs@example.com", user.email)
        assertEquals("calon_mahasiswa", user.role)
    }

    @Test
    fun testLogoutClearsTokenAlways() = runBlocking {
        fakeTokenManager.saveToken("existing_token")
        fakeTokenManager.saveUserEmail("mhs@example.com")
        assertTrue(authRepository.isLoggedIn())

        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"success":true,"message":"Logout berhasil."}"""))

        val result = authRepository.logout()

        assertTrue(result is NetworkResult.Success)
        assertNull(fakeTokenManager.getToken())
        assertFalse(authRepository.isLoggedIn())
    }
}
