package com.example.pmbitbakiday.data.remote

import com.example.pmbitbakiday.data.local.AuthTokenManager
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var fakeTokenManager: FakeTokenManager

    class FakeTokenManager(var storedToken: String? = null) : AuthTokenManager {
        var clearTokenCalled = false
        var storedEmail: String? = null

        override fun saveToken(token: String) { storedToken = token }
        override fun getToken(): String? = storedToken
        override fun clearToken() {
            storedToken = null
            clearTokenCalled = true
        }
        override fun hasToken(): Boolean = !storedToken.isNullOrBlank()
        override fun saveUserEmail(email: String) { storedEmail = email }
        override fun getUserEmail(): String? = storedEmail
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        fakeTokenManager = FakeTokenManager()
        // Konfigurasi ApiConfig agar MockWebServer menjadi trusted host
        ApiConfig.setCustomBaseUrl(server.url("/api/v1/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
        ApiConfig.resetToDefault()
    }

    @Test
    fun testAttachesBearerTokenWhenAvailableOnTrustedHost() {
        fakeTokenManager.saveToken("sample_token_xyz")

        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"success":true}"""))

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(fakeTokenManager))
            .build()

        val request = Request.Builder()
            .url(server.url("/api/v1/auth/me"))
            .build()

        val response = client.newCall(request).execute()
        assertEquals(200, response.code)

        val recorded = server.takeRequest()
        assertEquals("Bearer sample_token_xyz", recorded.getHeader("Authorization"))
        assertEquals("application/json", recorded.getHeader("Accept"))
    }

    @Test
    fun testDoesNotAttachAuthorizationHeaderWhenTokenMissing() {
        fakeTokenManager.clearToken()

        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"success":true}"""))

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(fakeTokenManager))
            .build()

        val request = Request.Builder()
            .url(server.url("/api/v1/admission-waves"))
            .build()

        client.newCall(request).execute()

        val recorded = server.takeRequest()
        assertNull(recorded.getHeader("Authorization"))
    }

    @Test
    fun testClearsTokenAndTriggersCallbackOn401() {
        fakeTokenManager.saveToken("expired_token")
        var unauthorizedTriggered = false

        server.enqueue(MockResponse().setResponseCode(401).setBody("""{"success":false,"error":{"code":"UNAUTHENTICATED"}}"""))

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(fakeTokenManager, onUnauthorized = {
                unauthorizedTriggered = true
            }))
            .build()

        val request = Request.Builder()
            .url(server.url("/api/v1/applicant/profile"))
            .build()

        val response = client.newCall(request).execute()
        assertEquals(401, response.code)
        assertTrue(fakeTokenManager.clearTokenCalled)
        assertFalse(fakeTokenManager.hasToken())
        assertTrue(unauthorizedTriggered)
    }
}
