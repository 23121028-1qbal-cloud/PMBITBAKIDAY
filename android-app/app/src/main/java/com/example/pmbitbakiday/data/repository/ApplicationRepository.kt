package com.example.pmbitbakiday.data.repository

import com.example.pmbitbakiday.data.model.AdmissionWaveDto
import com.example.pmbitbakiday.data.model.ApplicationCreateRequest
import com.example.pmbitbakiday.data.model.ApplicationCreatedDto
import com.example.pmbitbakiday.data.model.ApplicationDetailDto
import com.example.pmbitbakiday.data.model.ApplicationItemDto
import com.example.pmbitbakiday.data.model.ApplicationStatusTransitionDto
import com.example.pmbitbakiday.data.model.ApplicationUpdateRequest
import com.example.pmbitbakiday.data.model.DocumentDto
import com.example.pmbitbakiday.data.model.StatusHistoryDto
import com.example.pmbitbakiday.data.model.StatusTransitionRequest
import com.example.pmbitbakiday.data.model.StudyProgramDto
import com.example.pmbitbakiday.data.remote.ApiService
import com.example.pmbitbakiday.util.NetworkResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ApplicationRepository(
    private val apiService: ApiService
) : BaseRepository() {

    suspend fun getAdmissionWaves(): NetworkResult<List<AdmissionWaveDto>> {
        return safeApiCall {
            apiService.getAdmissionWaves()
        }
    }

    suspend fun getStudyPrograms(): NetworkResult<List<StudyProgramDto>> {
        return safeApiCall {
            apiService.getStudyPrograms()
        }
    }

    suspend fun createApplication(waveId: Int, studyProgramId: Int): NetworkResult<ApplicationCreatedDto> {
        return safeApiCall {
            apiService.createApplication(
                ApplicationCreateRequest(waveId = waveId, studyProgramId = studyProgramId)
            )
        }
    }

    suspend fun getMyApplications(page: Int? = null, perPage: Int? = null): NetworkResult<List<ApplicationItemDto>> {
        return safeApiCall {
            apiService.getMyApplications(page = page, perPage = perPage)
        }
    }

    suspend fun getApplicationDetail(id: Int): NetworkResult<ApplicationDetailDto> {
        return safeApiCall {
            apiService.getApplicationDetail(id)
        }
    }

    suspend fun updateApplicationDraft(id: Int, studyProgramId: Int): NetworkResult<ApplicationCreatedDto> {
        return safeApiCall {
            apiService.updateApplicationDraft(
                id,
                ApplicationUpdateRequest(studyProgramId = studyProgramId)
            )
        }
    }

    suspend fun submitApplication(id: Int, expectedStatus: String = "draft"): NetworkResult<ApplicationStatusTransitionDto> {
        return safeApiCall {
            apiService.submitApplication(
                id,
                StatusTransitionRequest(expectedStatus = expectedStatus)
            )
        }
    }

    suspend fun resubmitApplication(id: Int, expectedStatus: String = "revision"): NetworkResult<ApplicationStatusTransitionDto> {
        return safeApiCall {
            apiService.resubmitApplication(
                id,
                StatusTransitionRequest(expectedStatus = expectedStatus)
            )
        }
    }

    suspend fun uploadDocument(
        applicationId: Int,
        documentType: String,
        file: File,
        mimeType: String = "application/pdf"
    ): NetworkResult<DocumentDto> {
        val typeRequestBody = documentType.toRequestBody("text/plain".toMediaTypeOrNull())
        val fileRequestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)

        return safeApiCall {
            apiService.uploadDocument(applicationId, typeRequestBody, multipartBody)
        }
    }

    suspend fun getDocuments(applicationId: Int): NetworkResult<List<DocumentDto>> {
        return safeApiCall {
            apiService.getDocuments(applicationId)
        }
    }

    suspend fun deleteDocument(applicationId: Int, documentId: Int): NetworkResult<Unit> {
        return safeApiCall {
            apiService.deleteDocument(applicationId, documentId)
        }
    }

    suspend fun getStatusHistory(applicationId: Int): NetworkResult<List<StatusHistoryDto>> {
        return safeApiCall {
            apiService.getStatusHistory(applicationId)
        }
    }
}
