package com.example.pmbitbakiday.data.remote

import com.example.pmbitbakiday.BuildConfig
import com.example.pmbitbakiday.data.local.AuthTokenManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Factory & konfigurasi client Retrofit untuk PMB ITB Akiday.
 */
object RetrofitClient {

    private const val TIMEOUT_SECONDS = 30L

    val defaultGson: Gson by lazy {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .registerTypeAdapter(Unit::class.java, com.google.gson.JsonDeserializer { _, _, _ -> Unit })
            .create()
    }

    /**
     * Membangun OkHttpClient yang telah dilengkapi timeout, auth interceptor,
     * serta logging dengan redaksi header Authorization untuk keamanan.
     */
    fun createOkHttpClient(
        tokenManager: AuthTokenManager,
        onUnauthorized: (() -> Unit)? = null,
        enableLogging: Boolean = BuildConfig.DEBUG
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(tokenManager, onUnauthorized))

        if (enableLogging) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
                // Cegah kebocoran token & kredensial ke Logcat
                redactHeader("Authorization")
                redactHeader("Cookie")
                redactHeader("Set-Cookie")
            }
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    /**
     * Membuat instance ApiService.
     */
    fun createApiService(
        tokenManager: AuthTokenManager,
        baseUrl: String = ApiConfig.getBaseUrl(),
        onUnauthorized: (() -> Unit)? = null
    ): ApiService {
        val client = createOkHttpClient(tokenManager, onUnauthorized)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(defaultGson))
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Factory untuk pengujian (MockWebServer).
     */
    fun createForTesting(
        okHttpClient: OkHttpClient,
        baseUrl: String
    ): ApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(defaultGson))
            .build()
            .create(ApiService::class.java)
    }
}
