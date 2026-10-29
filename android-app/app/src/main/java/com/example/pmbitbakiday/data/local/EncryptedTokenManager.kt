package com.example.pmbitbakiday.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Implementasi penyimpanan token menggunakan Android Keystore dan EncryptedSharedPreferences.
 * Token disimpan secara terenkripsi (AES-256-GCM) dan tidak pernah disimpan dalam format teks biasa.
 */
class EncryptedTokenManager(
    private val prefs: SharedPreferences
) : AuthTokenManager {

    companion object {
        private const val PREFS_FILE = "pmb_secure_auth_prefs"
        private const val KEY_AUTH_TOKEN = "auth_bearer_token"
        private const val KEY_USER_EMAIL = "auth_user_email"

        /**
         * Factory method untuk menginisialisasi EncryptedSharedPreferences dengan MasterKey.
         */
        fun create(context: Context): EncryptedTokenManager {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                context,
                PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            return EncryptedTokenManager(sharedPreferences)
        }
    }

    override fun saveToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    override fun getToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    override fun clearToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).remove(KEY_USER_EMAIL).apply()
    }

    override fun hasToken(): Boolean {
        return !getToken().isNullOrBlank()
    }

    override fun saveUserEmail(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    override fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
}
