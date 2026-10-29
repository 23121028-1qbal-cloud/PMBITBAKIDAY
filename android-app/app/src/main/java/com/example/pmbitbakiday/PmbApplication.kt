package com.example.pmbitbakiday

import android.app.Application
import com.example.pmbitbakiday.data.local.AuthTokenManager
import com.example.pmbitbakiday.data.local.EncryptedTokenManager
import com.example.pmbitbakiday.data.remote.ApiService
import com.example.pmbitbakiday.data.remote.RetrofitClient
import com.example.pmbitbakiday.data.repository.ApplicationRepository
import com.example.pmbitbakiday.data.repository.AuthRepository
import com.example.pmbitbakiday.data.repository.ProfileRepository

/**
 * Entry point aplikasi untuk inisialisasi fondasi token storage, network client, dan repository.
 */
class PmbApplication : Application() {

    lateinit var tokenManager: AuthTokenManager
        private set

    lateinit var apiService: ApiService
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var profileRepository: ProfileRepository
        private set

    lateinit var applicationRepository: ApplicationRepository
        private set

    lateinit var applicantDao: com.example.pmbitbakiday.data.local.db.ApplicantDao
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        val dbHelper = com.example.pmbitbakiday.data.local.db.ApplicantDatabaseHelper(this)
        applicantDao = com.example.pmbitbakiday.data.local.db.SQLiteApplicantDao(dbHelper)

        tokenManager = EncryptedTokenManager.create(this)
        apiService = RetrofitClient.createApiService(tokenManager)

        authRepository = AuthRepository(apiService, tokenManager)
        profileRepository = ProfileRepository(apiService)
        applicationRepository = ApplicationRepository(apiService)
    }

    companion object {
        lateinit var instance: PmbApplication
            private set
    }
}
