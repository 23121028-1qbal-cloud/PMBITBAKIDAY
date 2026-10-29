package com.example.pmbitbakiday.data.repository

import com.example.pmbitbakiday.data.local.AuthTokenManager
import com.example.pmbitbakiday.data.model.LoginData
import com.example.pmbitbakiday.data.model.LoginRequest
import com.example.pmbitbakiday.data.model.RegisterData
import com.example.pmbitbakiday.data.model.RegisterRequest
import com.example.pmbitbakiday.data.model.UserDto
import com.example.pmbitbakiday.data.remote.ApiService
import com.example.pmbitbakiday.util.NetworkResult

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: AuthTokenManager
) : BaseRepository() {

    suspend fun login(email: String, password: String): NetworkResult<LoginData> {
        val result = safeApiCall {
            apiService.login(LoginRequest(email = email, password = password))
        }

        if (result is NetworkResult.Success) {
            tokenManager.saveToken(result.data.accessToken)
            tokenManager.saveUserEmail(email)
        }

        return result
    }

    suspend fun register(
        email: String,
        password: String,
        passwordConfirmation: String
    ): NetworkResult<RegisterData> {
        return safeApiCall {
            apiService.register(
                RegisterRequest(
                    email = email,
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
            )
        }
    }

    suspend fun getMe(): NetworkResult<UserDto> {
        return safeApiCall {
            apiService.getMe()
        }
    }

    suspend fun logout(): NetworkResult<Unit> {
        return try {
            safeApiCall {
                apiService.logout()
            }
        } finally {
            tokenManager.clearToken()
        }
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.hasToken()
    }

    fun getCurrentEmail(): String? {
        return tokenManager.getUserEmail()
    }
}
