package com.example.pmbitbakiday.data.model

import com.google.gson.annotations.SerializedName

data class ApplicationCreateRequest(
    @SerializedName("wave_id")
    val waveId: Int,

    @SerializedName("study_program_id")
    val studyProgramId: Int
)

data class ApplicationUpdateRequest(
    @SerializedName("study_program_id")
    val studyProgramId: Int
)

data class StatusTransitionRequest(
    @SerializedName("expected_status")
    val expectedStatus: String
)

data class WaveSummaryDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)

data class StudyProgramSummaryDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String
)

data class ApplicantSummaryDto(
    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("nik")
    val nik: String
)

data class ApplicationItemDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("registration_number")
    val registrationNumber: String,

    @SerializedName("wave")
    val wave: WaveSummaryDto? = null,

    @SerializedName("study_program")
    val studyProgram: StudyProgramSummaryDto? = null,

    @SerializedName("status")
    val status: String,

    @SerializedName("submitted_at")
    val submittedAt: String? = null
)

data class ApplicationCreatedDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("registration_number")
    val registrationNumber: String,

    @SerializedName("wave_id")
    val waveId: Int? = null,

    @SerializedName("study_program_id")
    val studyProgramId: Int? = null,

    @SerializedName("status")
    val status: String,

    @SerializedName("submitted_at")
    val submittedAt: String? = null
)

data class ApplicationDetailDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("registration_number")
    val registrationNumber: String,

    @SerializedName("wave")
    val wave: WaveSummaryDto? = null,

    @SerializedName("study_program")
    val studyProgram: StudyProgramSummaryDto? = null,

    @SerializedName("status")
    val status: String,

    @SerializedName("submitted_at")
    val submittedAt: String? = null,

    @SerializedName("applicant")
    val applicant: ApplicantSummaryDto? = null
)

data class ApplicationStatusTransitionDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("submitted_at")
    val submittedAt: String? = null
)

data class DocumentDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("application_id")
    val applicationId: Int,

    @SerializedName("document_type")
    val documentType: String,

    @SerializedName("file_name")
    val fileName: String,

    @SerializedName("file_size")
    val fileSize: Long,

    @SerializedName("uploaded_at")
    val uploadedAt: String? = null
)

data class StatusHistoryDto(
    @SerializedName("from_status")
    val fromStatus: String? = null,

    @SerializedName("to_status")
    val toStatus: String,

    @SerializedName("note")
    val note: String? = null,

    @SerializedName("changed_at")
    val changedAt: String? = null
)
