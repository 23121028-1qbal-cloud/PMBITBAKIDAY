package com.example.pmbitbakiday.util

import com.example.pmbitbakiday.data.model.ApiError

/**
 * State pembungkus untuk hasil pemanggilan API pada lapisan UI dan ViewModel.
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T, val message: String? = null) : NetworkResult<T>()
    data class Error(
        val message: String,
        val code: String? = null,
        val httpStatusCode: Int? = null,
        val error: ApiError? = null
    ) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data
}
