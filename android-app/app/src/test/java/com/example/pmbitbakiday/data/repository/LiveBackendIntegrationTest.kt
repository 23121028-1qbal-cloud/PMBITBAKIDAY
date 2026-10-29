package com.example.pmbitbakiday.data.repository

import com.example.pmbitbakiday.data.local.AuthTokenManager
import com.example.pmbitbakiday.data.remote.ApiConfig
import com.example.pmbitbakiday.data.remote.ApiService
import com.example.pmbitbakiday.data.remote.AuthInterceptor
import com.example.pmbitbakiday.data.remote.RetrofitClient
import com.example.pmbitbakiday.util.NetworkResult
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit

class LiveBackendIntegrationTest {

    private lateinit var fakeTokenManager: FakeTokenManager
    private lateinit var adminEmail: String
    private lateinit var adminPassword: String
    private lateinit var apiService: ApiService
    private lateinit var authRepository: AuthRepository
    private lateinit var applicationRepository: ApplicationRepository

    class FakeTokenManager(var storedToken: String? = null) : AuthTokenManager {
        var storedEmail: String? = null

        override fun saveToken(token: String) { storedToken = token }
        override fun getToken(): String? = storedToken
        override fun clearToken() { storedToken = null }
        override fun hasToken(): Boolean = !storedToken.isNullOrBlank()
        override fun saveUserEmail(email: String) { storedEmail = email }
        override fun getUserEmail(): String? = storedEmail
    }

    /**
     * Kredensial live test dibaca dari environment atau system property.
     * Tidak ada nilai default: kata sandi tidak boleh tersimpan di repository.
     */
    private fun config(name: String): String? =
        System.getenv(name)?.takeIf { it.isNotBlank() }
            ?: System.getProperty(name)?.takeIf { it.isNotBlank() }

    private fun requireConfig(name: String): String = config(name)
        ?: throw AssertionError(
            "$name belum diisi. Live test membutuhkan akun admin lokal yang nyata; " +
                "set PMB_TEST_ADMIN_EMAIL dan PMB_TEST_ADMIN_PASSWORD (lihat README)."
        )

    private fun isBackendReachable(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("127.0.0.1", 8000), 1000)
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    @Before
    fun setUp() {
        Assume.assumeTrue(
            "Live test dilewati: set PMB_LIVE_TESTS=1 untuk menjalankannya terhadap backend lokal.",
            config("PMB_LIVE_TESTS") == "1",
        )

        val reachable = isBackendReachable()
        Assume.assumeTrue("Backend server at 127.0.0.1:8000 is not running; skipping live test.", reachable)

        adminEmail = requireConfig("PMB_TEST_ADMIN_EMAIL")
        adminPassword = requireConfig("PMB_TEST_ADMIN_PASSWORD")

        fakeTokenManager = FakeTokenManager()
        ApiConfig.setCustomBaseUrl("http://127.0.0.1:8000/api/v1/")

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(fakeTokenManager))
            .build()

        apiService = RetrofitClient.createForTesting(okHttpClient, "http://127.0.0.1:8000/api/v1/")
        authRepository = AuthRepository(apiService, fakeTokenManager)
        applicationRepository = ApplicationRepository(apiService)
    }

    @After
    fun tearDown() {
        ApiConfig.resetToDefault()
    }

    @Test
    fun testLiveBackendAuthenticatedWorkflow() = runBlocking {
        // 1. Login menggunakan akun admin dari environment
        val loginResult = authRepository.login(adminEmail, adminPassword)
        assertTrue("Live admin login failed: $loginResult", loginResult is NetworkResult.Success)
        val loginData = (loginResult as NetworkResult.Success).data
        assertNotNull(loginData.accessToken)
        assertTrue(authRepository.isLoggedIn())

        // 2. Akses identitas akun aktif (GET /auth/me)
        val meResult = authRepository.getMe()
        assertTrue("Live getMe failed: $meResult", meResult is NetworkResult.Success)
        val user = (meResult as NetworkResult.Success).data
        assertEquals(adminEmail, user.email)
        assertEquals("admin", user.role)

        // 3. Ambil data referensi gelombang pendaftaran
        val wavesResult = applicationRepository.getAdmissionWaves()
        assertTrue("Fetching admission waves failed: $wavesResult", wavesResult is NetworkResult.Success)
        val waves = (wavesResult as NetworkResult.Success).data
        assertTrue("Expected at least 1 admission wave in live DB", waves.isNotEmpty())
        assertNotNull(waves[0].name)

        // 4. Ambil data referensi program studi
        val progsResult = applicationRepository.getStudyPrograms()
        assertTrue("Fetching study programs failed: $progsResult", progsResult is NetworkResult.Success)
        val progs = (progsResult as NetworkResult.Success).data
        assertTrue("Expected at least 1 study program in live DB", progs.isNotEmpty())
        assertEquals("Informatika", progs[0].name)

        // 5. Uji permintaan resource yang tidak ditemukan (404)
        val notFoundResult = applicationRepository.getApplicationDetail(999999)
        assertTrue("Expected 404 for non-existent application", notFoundResult is NetworkResult.Error)
        val error = notFoundResult as NetworkResult.Error
        assertEquals(404, error.httpStatusCode)
        assertEquals("RESOURCE_NOT_FOUND", error.code)

        // 6. Logout dan verifikasi token dicabut
        val logoutResult = authRepository.logout()
        assertTrue("Live logout failed: $logoutResult", logoutResult is NetworkResult.Success)
        assertFalse(authRepository.isLoggedIn())
    }

    @Test
    fun testLiveBackendInvalidCredentialsReturns401() = runBlocking {
        val result = authRepository.login(adminEmail, "${adminPassword}_salah")
        assertTrue(result is NetworkResult.Error)
        val error = result as NetworkResult.Error
        assertEquals(401, error.httpStatusCode)
        assertEquals("INVALID_CREDENTIALS", error.code)
        assertFalse(authRepository.isLoggedIn())
    }
}
