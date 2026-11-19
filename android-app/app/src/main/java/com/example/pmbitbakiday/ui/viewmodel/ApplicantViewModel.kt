package com.example.pmbitbakiday.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pmbitbakiday.data.local.db.ApplicantDao
import com.example.pmbitbakiday.data.local.db.ApplicantLocalEntity
import com.example.pmbitbakiday.data.model.AdmissionWaveDto
import com.example.pmbitbakiday.data.model.ApplicationDetailDto
import com.example.pmbitbakiday.data.model.ProfileDto
import com.example.pmbitbakiday.data.model.ProfileRequest
import com.example.pmbitbakiday.data.model.StatusHistoryDto
import com.example.pmbitbakiday.data.model.StudyProgramDto
import com.example.pmbitbakiday.data.repository.ApplicationRepository
import com.example.pmbitbakiday.data.repository.AuthRepository
import com.example.pmbitbakiday.data.repository.ProfileRepository
import com.example.pmbitbakiday.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel terpadu untuk mengelola state pendaftar:
 * - Operasi CRUD ke database SQLite lokal (offline cache).
 * - Integrasi online ke Laravel REST API via AuthRepository, ProfileRepository, dan ApplicationRepository.
 */
class ApplicantViewModel(
    private val applicantDao: ApplicantDao,
    private val authRepository: AuthRepository? = null,
    private val profileRepository: ProfileRepository? = null,
    private val applicationRepository: ApplicationRepository? = null
) : ViewModel() {

    // --- State Database Lokal ---
    private val _applicants = MutableStateFlow<List<ApplicantLocalEntity>>(emptyList())
    val applicants: StateFlow<List<ApplicantLocalEntity>> = _applicants.asStateFlow()

    private val _selectedApplicant = MutableStateFlow<ApplicantLocalEntity?>(null)
    val selectedApplicant: StateFlow<ApplicantLocalEntity?> = _selectedApplicant.asStateFlow()

    // --- State Online / REST API ---
    private val _isLoggedIn = MutableStateFlow(authRepository?.isLoggedIn() ?: false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentEmail = MutableStateFlow(authRepository?.getCurrentEmail())
    val currentEmail: StateFlow<String?> = _currentEmail.asStateFlow()

    private val _admissionWaves = MutableStateFlow<List<AdmissionWaveDto>>(emptyList())
    val admissionWaves: StateFlow<List<AdmissionWaveDto>> = _admissionWaves.asStateFlow()

    private val _studyPrograms = MutableStateFlow<List<StudyProgramDto>>(emptyList())
    val studyPrograms: StateFlow<List<StudyProgramDto>> = _studyPrograms.asStateFlow()

    private val _myApplication = MutableStateFlow<ApplicationDetailDto?>(null)
    val myApplication: StateFlow<ApplicationDetailDto?> = _myApplication.asStateFlow()

    private val _myProfile = MutableStateFlow<ProfileDto?>(null)
    val myProfile: StateFlow<ProfileDto?> = _myProfile.asStateFlow()

    private val _statusHistory = MutableStateFlow<List<StatusHistoryDto>>(emptyList())
    val statusHistory: StateFlow<List<StatusHistoryDto>> = _statusHistory.asStateFlow()

    private val _isOnlineLoading = MutableStateFlow(false)
    val isOnlineLoading: StateFlow<Boolean> = _isOnlineLoading.asStateFlow()

    private val _onlineError = MutableStateFlow<String?>(null)
    val onlineError: StateFlow<String?> = _onlineError.asStateFlow()

    init {
        loadApplicants()
        if (authRepository?.isLoggedIn() == true) {
            loadOnlineData()
        }
    }

    // -------------------------------------------------------------------------
    // Operasi Lokal SQLite
    // -------------------------------------------------------------------------

    fun loadApplicants() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = applicantDao.getAll()
            withContext(Dispatchers.Main) {
                _applicants.value = list
            }
        }
    }

    fun selectApplicantForEdit(applicant: ApplicantLocalEntity?) {
        _selectedApplicant.value = applicant
    }

    fun saveApplicant(
        id: Long = 0L,
        fullName: String,
        nik: String,
        birthPlace: String,
        birthDate: String,
        gender: String,
        phone: String,
        address: String,
        studyProgram: String,
        photoUri: String?,
        isAgreed: Boolean,
        onComplete: (Boolean, String) -> Unit
    ) {
        if (fullName.isBlank()) {
            onComplete(false, "Nama lengkap wajib diisi.")
            return
        }
        if (nik.length != 16 || !nik.all { it.isDigit() }) {
            onComplete(false, "NIK harus terdiri dari 16 digit angka.")
            return
        }
        if (birthPlace.isBlank()) {
            onComplete(false, "Tempat lahir wajib diisi.")
            return
        }
        if (birthDate.isBlank()) {
            onComplete(false, "Tanggal lahir wajib dipilih.")
            return
        }
        if (gender.isBlank()) {
            onComplete(false, "Jenis kelamin wajib dipilih.")
            return
        }
        if (phone.isBlank()) {
            onComplete(false, "Nomor telepon/WA wajib diisi.")
            return
        }
        if (address.isBlank()) {
            onComplete(false, "Alamat domisili wajib diisi.")
            return
        }
        if (studyProgram.isBlank()) {
            onComplete(false, "Program studi wajib dipilih.")
            return
        }
        if (!isAgreed) {
            onComplete(false, "Anda harus menyetujui pernyataan kebenaran data.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val entity = ApplicantLocalEntity(
                id = id,
                fullName = fullName.trim(),
                nik = nik.trim(),
                birthPlace = birthPlace.trim(),
                birthDate = birthDate.trim(),
                gender = gender,
                phone = phone.trim(),
                address = address.trim(),
                studyProgram = studyProgram,
                photoUri = photoUri,
                isAgreed = isAgreed
            )

            if (id == 0L) {
                applicantDao.insert(entity)
            } else {
                applicantDao.update(entity)
            }

            // Sinkronisasi profil ke server jika calon mahasiswa sedang login online
            val genderCode = if (gender.contains("Laki", ignoreCase = true) || gender.equals("L", ignoreCase = true)) "L" else "P"
            if (authRepository?.isLoggedIn() == true && profileRepository != null) {
                profileRepository.updateProfile(
                    ProfileRequest(
                        fullName = fullName.trim(),
                        nik = nik.trim(),
                        birthPlace = birthPlace.trim(),
                        birthDate = birthDate.trim(),
                        gender = genderCode,
                        phone = phone.trim(),
                        address = address.trim()
                    )
                )

                // Otomatis buat pengajuan pendaftaran PMB ke server agar tampil di web admin
                if (applicationRepository != null) {
                    val progId = if (studyProgram.contains("Sistem", ignoreCase = true)) 2 else 1
                    var waveId = _admissionWaves.value.firstOrNull { it.isActive }?.id
                    if (waveId == null) {
                        val wavesRes = applicationRepository.getAdmissionWaves()
                        if (wavesRes is NetworkResult.Success) {
                            waveId = wavesRes.data.firstOrNull { it.isActive }?.id ?: wavesRes.data.firstOrNull()?.id
                        }
                    }
                    val targetWaveId = waveId ?: 1

                    val currentApp = _myApplication.value
                    val appId = if (currentApp == null) {
                        val createRes = applicationRepository.createApplication(targetWaveId, progId)
                        if (createRes is NetworkResult.Success) {
                            createRes.data.id
                        } else null
                    } else {
                        currentApp.id
                    }

                    if (appId != null) {
                        applicationRepository.submitApplication(appId, expectedStatus = "draft")
                        loadOnlineData()
                    }
                }
            }

            val updatedList = applicantDao.getAll()
            withContext(Dispatchers.Main) {
                _applicants.value = updatedList
                _selectedApplicant.value = null
                onComplete(true, if (id == 0L) "Data pendaftar berhasil ditambahkan (tersinkronisasi)!" else "Data pendaftar berhasil diperbarui!")
            }
        }
    }

    fun deleteApplicant(id: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            applicantDao.delete(id)
            val updatedList = applicantDao.getAll()
            withContext(Dispatchers.Main) {
                _applicants.value = updatedList
                onComplete()
            }
        }
    }

    // -------------------------------------------------------------------------
    // Operasi Online / REST API
    // -------------------------------------------------------------------------

    fun loginOnline(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        if (authRepository == null) {
            onResult(false, "Layanan autentikasi tidak tersedia.")
            return
        }
        _isOnlineLoading.value = true
        _onlineError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val res = authRepository.login(email.trim(), password)
            withContext(Dispatchers.Main) {
                _isOnlineLoading.value = false
                when (res) {
                    is NetworkResult.Success -> {
                        _isLoggedIn.value = true
                        _currentEmail.value = res.data.user.email
                        loadOnlineData()
                        onResult(true, "Login berhasil sebagai ${res.data.user.email}")
                    }
                    is NetworkResult.Error -> {
                        _onlineError.value = res.message
                        onResult(false, res.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun registerOnline(email: String, password: String, passwordConfirmation: String, onResult: (Boolean, String) -> Unit) {
        if (authRepository == null) {
            onResult(false, "Layanan registrasi tidak tersedia.")
            return
        }
        _isOnlineLoading.value = true
        _onlineError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val res = authRepository.register(email.trim(), password, passwordConfirmation)
            withContext(Dispatchers.Main) {
                _isOnlineLoading.value = false
                when (res) {
                    is NetworkResult.Success -> {
                        onResult(true, "Registrasi akun berhasil. Silakan login.")
                    }
                    is NetworkResult.Error -> {
                        _onlineError.value = res.message
                        onResult(false, res.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun logoutOnline(onComplete: () -> Unit) {
        if (authRepository == null) {
            onComplete()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.logout()
            withContext(Dispatchers.Main) {
                _isLoggedIn.value = false
                _currentEmail.value = null
                _myApplication.value = null
                _myProfile.value = null
                _statusHistory.value = emptyList()
                onComplete()
            }
        }
    }

    fun loadOnlineData() {
        if (applicationRepository == null) return

        viewModelScope.launch(Dispatchers.IO) {
            // 1. Data referensi
            val wavesRes = applicationRepository.getAdmissionWaves()
            val progsRes = applicationRepository.getStudyPrograms()

            // 2. Profil
            val profRes = profileRepository?.getProfile()

            // 3. Aplikasi saya
            val myAppsRes = applicationRepository.getMyApplications()
            var detail: ApplicationDetailDto? = null
            var histories: List<StatusHistoryDto> = emptyList()

            if (myAppsRes is NetworkResult.Success && myAppsRes.data.isNotEmpty()) {
                val firstAppId = myAppsRes.data[0].id
                val detailRes = applicationRepository.getApplicationDetail(firstAppId)
                if (detailRes is NetworkResult.Success) {
                    detail = detailRes.data
                }
                val histRes = applicationRepository.getStatusHistory(firstAppId)
                if (histRes is NetworkResult.Success) {
                    histories = histRes.data
                }
            }

            withContext(Dispatchers.Main) {
                if (wavesRes is NetworkResult.Success) _admissionWaves.value = wavesRes.data
                if (progsRes is NetworkResult.Success) _studyPrograms.value = progsRes.data
                if (profRes is NetworkResult.Success) _myProfile.value = profRes.data
                _myApplication.value = detail
                _statusHistory.value = histories
            }
        }
    }

    fun submitOnlineApplication(
        waveId: Int,
        studyProgramId: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        if (applicationRepository == null) {
            onResult(false, "Layanan pendaftaran online tidak tersedia.")
            return
        }

        _isOnlineLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            // Buat draft jika belum ada
            val currentApp = _myApplication.value
            val appId = if (currentApp == null) {
                val createRes = applicationRepository.createApplication(waveId, studyProgramId)
                if (createRes is NetworkResult.Error) {
                    withContext(Dispatchers.Main) {
                        _isOnlineLoading.value = false
                        onResult(false, createRes.message)
                    }
                    return@launch
                }
                (createRes as NetworkResult.Success).data.id
            } else {
                currentApp.id
            }

            // Submit draft
            val submitRes = applicationRepository.submitApplication(appId, expectedStatus = "draft")
            withContext(Dispatchers.Main) {
                _isOnlineLoading.value = false
                when (submitRes) {
                    is NetworkResult.Success -> {
                        loadOnlineData()
                        onResult(true, "Pendaftaran berhasil dikirim untuk pemeriksaan.")
                    }
                    is NetworkResult.Error -> {
                        onResult(false, submitRes.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun resubmitOnlineApplication(
        applicationId: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        if (applicationRepository == null) {
            onResult(false, "Layanan pendaftaran online tidak tersedia.")
            return
        }

        _isOnlineLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val resubmitRes = applicationRepository.resubmitApplication(applicationId, expectedStatus = "revision")
            withContext(Dispatchers.Main) {
                _isOnlineLoading.value = false
                when (resubmitRes) {
                    is NetworkResult.Success -> {
                        loadOnlineData()
                        onResult(true, "Perbaikan berkas berhasil dikirim kembali.")
                    }
                    is NetworkResult.Error -> {
                        onResult(false, resubmitRes.message)
                    }
                    else -> {}
                }
            }
        }
    }
}

class ApplicantViewModelFactory(
    private val dao: ApplicantDao,
    private val authRepository: AuthRepository? = null,
    private val profileRepository: ProfileRepository? = null,
    private val applicationRepository: ApplicationRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ApplicantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ApplicantViewModel(dao, authRepository, profileRepository, applicationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
