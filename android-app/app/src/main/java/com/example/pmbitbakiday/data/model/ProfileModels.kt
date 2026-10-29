package com.example.pmbitbakiday.data.model

import com.google.gson.annotations.SerializedName

data class ProfileRequest(
    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("nik")
    val nik: String,

    @SerializedName("birth_place")
    val birthPlace: String,

    @SerializedName("birth_date")
    val birthDate: String, // Format YYYY-MM-DD

    @SerializedName("gender")
    val gender: String, // "L" atau "P"

    @SerializedName("phone")
    val phone: String,

    @SerializedName("address")
    val address: String
)

data class ProfileDto(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("nik")
    val nik: String,

    @SerializedName("birth_place")
    val birthPlace: String,

    @SerializedName("birth_date")
    val birthDate: String,

    @SerializedName("gender")
    val gender: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("address")
    val address: String
)
