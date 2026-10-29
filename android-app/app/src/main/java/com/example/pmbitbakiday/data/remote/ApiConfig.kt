package com.example.pmbitbakiday.data.remote

import com.example.pmbitbakiday.BuildConfig
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * Pengelolaan konfigurasi alamat API (Base URL) dan validasi host tepercaya.
 * Mendukung Android Emulator (10.0.2.2), perangkat fisik (LAN IP), dan produksi.
 */
object ApiConfig {

    private const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:8000/api/v1/"
    private const val DEFAULT_PRODUCTION_BASE_URL = "https://pmb.itbakiday.ac.id/api/v1/"

    @Volatile
    private var customBaseUrl: String? = null

    /**
     * Mengembalikan Base URL aktif saat ini.
     * Jika customBaseUrl diset (misalnya untuk pengujian perangkat fisik), nilai tersebut digunakan.
     * Jika tidak, menggunakan konfigurasi dari BuildConfig (default emulator pada debug).
     */
    fun getBaseUrl(): String {
        val current = customBaseUrl
        if (!current.isNullOrBlank()) {
            return ensureTrailingSlash(current)
        }

        return try {
            val buildConfigUrl = BuildConfig.BASE_URL
            if (buildConfigUrl.isNotBlank()) ensureTrailingSlash(buildConfigUrl) else DEFAULT_EMULATOR_BASE_URL
        } catch (_: Throwable) {
            DEFAULT_EMULATOR_BASE_URL
        }
    }

    /**
     * Memungkinkan penyesuaian host pada runtime (misalnya input IP komputer dev untuk perangkat fisik).
     */
    fun setCustomBaseUrl(url: String?) {
        if (url.isNullOrBlank()) {
            customBaseUrl = null
        } else {
            val parsed = url.toHttpUrlOrNull()
                ?: throw IllegalArgumentException("Format URL tidak valid: $url")
            customBaseUrl = ensureTrailingSlash(parsed.toString())
        }
    }

    /**
     * Reset konfigurasi URL ke default.
     */
    fun resetToDefault() {
        customBaseUrl = null
    }

    /**
     * Memeriksa apakah URL yang dituju merupakan host API internal yang tepercaya.
     * Digunakan oleh AuthInterceptor untuk mencegah pengiriman bearer token ke host luar.
     */
    fun isTrustedHost(requestUrl: HttpUrl): Boolean {
        val configuredUrl = getBaseUrl().toHttpUrlOrNull() ?: return false
        return requestUrl.host.equals(configuredUrl.host, ignoreCase = true)
    }

    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }
}
