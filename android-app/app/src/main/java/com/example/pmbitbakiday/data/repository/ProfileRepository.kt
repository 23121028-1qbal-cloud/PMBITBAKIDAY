package com.example.pmbitbakiday.data.repository

import com.example.pmbitbakiday.data.model.ProfileDto
import com.example.pmbitbakiday.data.model.ProfileRequest
import com.example.pmbitbakiday.data.remote.ApiService
import com.example.pmbitbakiday.util.NetworkResult

class ProfileRepository(
    private val apiService: ApiService
) : BaseRepository() {

    suspend fun getProfile(): NetworkResult<ProfileDto> {
        return safeApiCall {
            apiService.getProfile()
        }
    }

    suspend fun updateProfile(request: ProfileRequest): NetworkResult<ProfileDto> {
        return safeApiCall {
            apiService.updateProfile(request)
        }
    }
}
