package com.example.pmbitbakiday.data.model

import com.google.gson.annotations.SerializedName

data class AdmissionWaveDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("end_date")
    val endDate: String,

    @SerializedName("is_active")
    val isActive: Boolean
)

data class StudyProgramDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("code")
    val code: String,

    @SerializedName("name")
    val name: String
)
