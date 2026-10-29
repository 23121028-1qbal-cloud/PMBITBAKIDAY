package com.example.pmbitbakiday.data.repository

import com.example.pmbitbakiday.data.model.ApiError
import com.example.pmbitbakiday.data.model.ApiResponse
import com.example.pmbitbakiday.data.remote.RetrofitClient
import com.example.pmbitbakiday.util.NetworkResult
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Base repository yang menangani eksekusi panggilan API Retrofit secara aman,
 * memetakan respons JSON sukses/error, timeout, dan kehilangan koneksi internet
 * sesuai spesifikasi API Error Handling.
 */
abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>
    ): NetworkResult<T> {
        return try {
            val response = apiCall()
            handleResponse(response)
        } catch (e: SocketTimeoutException) {
            NetworkResult.Error(
                message = "Koneksi ke server melebihi batas waktu (timeout). Silakan coba lagi.",
                code = "TIMEOUT"
            )
        } catch (e: UnknownHostException) {
            NetworkResult.Error(
                message = "Alamat server tidak dapat dijangkau. Periksa koneksi internet atau pengaturan host.",
                code = "HOST_UNREACHABLE"
            )
        } catch (e: ConnectException) {
            NetworkResult.Error(
                message = "Gagal terhubung ke server. Pastikan server backend sedang berjalan.",
                code = "CONNECTION_FAILED"
            )
        } catch (e: IOException) {
            NetworkResult.Error(
                message = "Terjadi gangguan jaringan. Periksa koneksi internet Anda.",
                code = "NETWORK_ERROR"
            )
        } catch (e: Exception) {
            NetworkResult.Error(
                message = e.localizedMessage ?: "Terjadi kesalahan yang tidak terduga.",
                code = "UNEXPECTED_ERROR"
            )
        }
    }

    private fun <T> handleResponse(response: Response<ApiResponse<T>>): NetworkResult<T> {
        val statusCode = response.code()

        if (response.isSuccessful) {
            val body = response.body()
            return if (body != null && body.success) {
                // Respons sukses
                if (body.data != null) {
                    NetworkResult.Success(body.data, body.message)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    NetworkResult.Success(Unit as T, body.message)
                }
            } else {
                NetworkResult.Error(
                    message = body?.message ?: "Permintaan tidak dapat diproses.",
                    code = body?.error?.code ?: "BAD_REQUEST",
                    httpStatusCode = statusCode,
                    error = body?.error
                )
            }
        }

        // Tangani HTTP non-2xx
        val errorBodyString = response.errorBody()?.string()
        if (!errorBodyString.isNullOrBlank()) {
            try {
                val type = object : TypeToken<ApiResponse<Any>>() {}.type
                val parsed: ApiResponse<Any>? = RetrofitClient.defaultGson.fromJson(errorBodyString, type)
                if (parsed != null && parsed.error != null) {
                    return NetworkResult.Error(
                        message = parsed.message,
                        code = parsed.error.code,
                        httpStatusCode = statusCode,
                        error = parsed.error
                    )
                }
            } catch (_: Exception) {
                // Gagal parse JSON error, gunakan fallback status code
            }
        }

        return NetworkResult.Error(
            message = defaultMessageForStatus(statusCode),
            code = defaultCodeForStatus(statusCode),
            httpStatusCode = statusCode,
            error = ApiError(code = defaultCodeForStatus(statusCode))
        )
    }

    private fun defaultCodeForStatus(status: Int): String {
        return when (status) {
            400 -> "BAD_REQUEST"
            401 -> "UNAUTHENTICATED"
            403 -> "FORBIDDEN"
            404 -> "RESOURCE_NOT_FOUND"
            409 -> "STATE_CONFLICT"
            413 -> "FILE_TOO_LARGE"
            415 -> "UNSUPPORTED_FILE_TYPE"
            422 -> "VALIDATION_ERROR"
            429 -> "RATE_LIMIT_EXCEEDED"
            503 -> "SERVICE_UNAVAILABLE"
            else -> if (status >= 500) "INTERNAL_SERVER_ERROR" else "BAD_REQUEST"
        }
    }

    private fun defaultMessageForStatus(status: Int): String {
        return when (status) {
            400 -> "Permintaan tidak dapat diproses."
            401 -> "Silakan login terlebih dahulu."
            403 -> "Anda tidak memiliki izin untuk melakukan tindakan ini."
            404 -> "Data yang diminta tidak ditemukan."
            409 -> "Status pendaftaran telah berubah. Muat ulang data sebelum mencoba kembali."
            413 -> "Ukuran berkas melebihi batas yang diizinkan."
            415 -> "Format berkas tidak didukung."
            422 -> "Periksa kembali data yang dimasukkan."
            429 -> "Terlalu banyak permintaan. Silakan coba kembali nanti."
            503 -> "Layanan sementara tidak tersedia. Silakan coba kembali nanti."
            else -> if (status >= 500) {
                "Terjadi kesalahan pada server. Silakan coba kembali nanti."
            } else {
                "Permintaan tidak dapat diproses."
            }
        }
    }
}
