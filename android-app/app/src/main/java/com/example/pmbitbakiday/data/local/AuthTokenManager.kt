package com.example.pmbitbakiday.data.local

/**
 * Kontrak penyimpanan aman token autentikasi Sanctum dan kredensial sesi klien Android.
 */
interface AuthTokenManager {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
    fun hasToken(): Boolean
    fun saveUserEmail(email: String)
    fun getUserEmail(): String?
}
