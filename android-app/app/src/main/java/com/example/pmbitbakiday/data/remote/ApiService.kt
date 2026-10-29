package com.example.pmbitbakiday.data.remote

import com.example.pmbitbakiday.data.model.AdmissionWaveDto
import com.example.pmbitbakiday.data.model.ApiResponse
import com.example.pmbitbakiday.data.model.ApplicationCreateRequest
import com.example.pmbitbakiday.data.model.ApplicationCreatedDto
import com.example.pmbitbakiday.data.model.ApplicationDetailDto
import com.example.pmbitbakiday.data.model.ApplicationItemDto
import com.example.pmbitbakiday.data.model.ApplicationStatusTransitionDto
import com.example.pmbitbakiday.data.model.ApplicationUpdateRequest
import com.example.pmbitbakiday.data.model.DocumentDto
import com.example.pmbitbakiday.data.model.LoginData
import com.example.pmbitbakiday.data.model.LoginRequest
import com.example.pmbitbakiday.data.model.ProfileDto
import com.example.pmbitbakiday.data.model.ProfileRequest
import com.example.pmbitbakiday.data.model.RegisterData
import com.example.pmbitbakiday.data.model.RegisterRequest
import com.example.pmbitbakiday.data.model.StatusHistoryDto
import com.example.pmbitbakiday.data.model.StatusTransitionRequest
import com.example.pmbitbakiday.data.model.StudyProgramDto
import com.example.pmbitbakiday.data.model.UserDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Kontrak Retrofit 2 untuk komunikasi Android ke Laravel REST API (prefix /api/v1).
 */
interface ApiService {

    // -------------------------------------------------------------------------
    // Autentikasi
    // -------------------------------------------------------------------------

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<ApiResponse<RegisterData>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginData>>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<UserDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // -------------------------------------------------------------------------
    // Data Referensi
    // -------------------------------------------------------------------------

    @GET("admission-waves")
    suspend fun getAdmissionWaves(): Response<ApiResponse<List<AdmissionWaveDto>>>

    @GET("study-programs")
    suspend fun getStudyPrograms(): Response<ApiResponse<List<StudyProgramDto>>>

    // -------------------------------------------------------------------------
    // Profil Calon Mahasiswa
    // -------------------------------------------------------------------------

    @GET("applicant/profile")
    suspend fun getProfile(): Response<ApiResponse<ProfileDto>>

    @PUT("applicant/profile")
    suspend fun updateProfile(
        @Body request: ProfileRequest
    ): Response<ApiResponse<ProfileDto>>

    // -------------------------------------------------------------------------
    // Pengajuan Pendaftaran
    // -------------------------------------------------------------------------

    @POST("applications")
    suspend fun createApplication(
        @Body request: ApplicationCreateRequest
    ): Response<ApiResponse<ApplicationCreatedDto>>

    @GET("applications")
    suspend fun getMyApplications(
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null
    ): Response<ApiResponse<List<ApplicationItemDto>>>

    @GET("applications/{id}")
    suspend fun getApplicationDetail(
        @Path("id") id: Int
    ): Response<ApiResponse<ApplicationDetailDto>>

    @PUT("applications/{id}")
    suspend fun updateApplicationDraft(
        @Path("id") id: Int,
        @Body request: ApplicationUpdateRequest
    ): Response<ApiResponse<ApplicationCreatedDto>>

    @POST("applications/{id}/submit")
    suspend fun submitApplication(
        @Path("id") id: Int,
        @Body request: StatusTransitionRequest
    ): Response<ApiResponse<ApplicationStatusTransitionDto>>

    @POST("applications/{id}/resubmit")
    suspend fun resubmitApplication(
        @Path("id") id: Int,
        @Body request: StatusTransitionRequest
    ): Response<ApiResponse<ApplicationStatusTransitionDto>>

    // -------------------------------------------------------------------------
    // Dokumen Pendaftaran
    // -------------------------------------------------------------------------

    @Multipart
    @POST("applications/{id}/documents")
    suspend fun uploadDocument(
        @Path("id") id: Int,
        @Part("document_type") documentType: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<DocumentDto>>

    @GET("applications/{id}/documents")
    suspend fun getDocuments(
        @Path("id") id: Int
    ): Response<ApiResponse<List<DocumentDto>>>

    @Streaming
    @GET("applications/{id}/documents/{documentId}/download")
    suspend fun downloadDocument(
        @Path("id") id: Int,
        @Path("documentId") documentId: Int
    ): Response<ResponseBody>

    @DELETE("applications/{id}/documents/{documentId}")
    suspend fun deleteDocument(
        @Path("id") id: Int,
        @Path("documentId") documentId: Int
    ): Response<ApiResponse<Unit>>

    // -------------------------------------------------------------------------
    // Riwayat Status
    // -------------------------------------------------------------------------

    @GET("applications/{id}/status-history")
    suspend fun getStatusHistory(
        @Path("id") id: Int
    ): Response<ApiResponse<List<StatusHistoryDto>>>
}
