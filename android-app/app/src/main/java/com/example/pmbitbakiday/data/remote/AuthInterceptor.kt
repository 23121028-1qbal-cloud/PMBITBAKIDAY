package com.example.pmbitbakiday.data.remote

import com.example.pmbitbakiday.data.local.AuthTokenManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor untuk menyematkan Bearer token pada header Authorization
 * dan mendeteksi respons 401 Unauthorized secara otomatis.
 */
class AuthInterceptor(
    private val tokenManager: AuthTokenManager,
    private val onUnauthorized: (() -> Unit)? = null
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Standar header
        requestBuilder.header("Accept", "application/json")

        // Lampirkan Bearer token HANYA jika token tersedia dan host adalah server tepercaya
        if (ApiConfig.isTrustedHost(originalRequest.url)) {
            val token = tokenManager.getToken()
            if (!token.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
        }

        val response = chain.proceed(requestBuilder.build())

        // Tangani 401: Token telah kedaluwarsa atau dicabut
        if (response.code == 401) {
            tokenManager.clearToken()
            onUnauthorized?.invoke()
        }

        return response
    }
}
