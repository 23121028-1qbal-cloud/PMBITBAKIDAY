package com.example.pmbitbakiday.data.model

import com.google.gson.annotations.SerializedName

/**
 * Amplop response JSON standar sesuai API Contract dan API Error Handling PMB ITB Akiday.
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("error")
    val error: ApiError? = null,

    @SerializedName("meta")
    val meta: PaginationMeta? = null
)

/**
 * Detail objek error sesuai standar backend PMB ITB Akiday.
 */
data class ApiError(
    @SerializedName("code")
    val code: String,

    @SerializedName("details")
    val details: Map<String, List<String>>? = null
)

/**
 * Metadata pagination untuk list endpoint.
 */
data class PaginationMeta(
    @SerializedName("current_page")
    val currentPage: Int,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("last_page")
    val lastPage: Int
)
