package com.example.pmbitbakiday

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pmbitbakiday.ui.screen.ApplicantFormScreen
import com.example.pmbitbakiday.ui.screen.ApplicantListScreen
import com.example.pmbitbakiday.ui.screen.ApplicationStatusScreen
import com.example.pmbitbakiday.ui.screen.AuthScreen
import com.example.pmbitbakiday.ui.theme.PmbTheme
import com.example.pmbitbakiday.ui.viewmodel.ApplicantViewModel
import com.example.pmbitbakiday.ui.viewmodel.ApplicantViewModelFactory

enum class AppScreen {
    LIST,
    FORM,
    AUTH,
    STATUS
}

class MainActivity : ComponentActivity() {

    private val viewModel: ApplicantViewModel by viewModels {
        val app = PmbApplication.instance
        ApplicantViewModelFactory(
            dao = app.applicantDao,
            authRepository = app.authRepository,
            profileRepository = app.profileRepository,
            applicationRepository = app.applicationRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PmbTheme {
                PmbMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PmbMainApp(viewModel: ApplicantViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.LIST) }
    val applicants by viewModel.applicants.collectAsState()
    val selectedApplicant by viewModel.selectedApplicant.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentEmail by viewModel.currentEmail.collectAsState()
    val myApp by viewModel.myApplication.collectAsState()

    when (currentScreen) {
        AppScreen.LIST -> {
            ApplicantListScreen(
                applicants = applicants,
                isLoggedIn = isLoggedIn,
                userEmail = currentEmail,
                onlineStatus = myApp?.status,
                onAddNew = {
                    viewModel.selectApplicantForEdit(null)
                    currentScreen = AppScreen.FORM
                },
                onEdit = { applicant ->
                    viewModel.selectApplicantForEdit(applicant)
                    currentScreen = AppScreen.FORM
                },
                onDelete = { id ->
                    viewModel.deleteApplicant(id)
                },
                onNavigateToAuth = {
                    currentScreen = AppScreen.AUTH
                },
                onNavigateToStatus = {
                    currentScreen = AppScreen.STATUS
                },
                onLogout = {
                    viewModel.logoutOnline {
                        Toast.makeText(PmbApplication.instance, "Berhasil keluar dari akun.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        AppScreen.FORM -> {
            ApplicantFormScreen(
                applicant = selectedApplicant,
                onBack = {
                    viewModel.selectApplicantForEdit(null)
                    currentScreen = AppScreen.LIST
                },
                onSave = { id, fullName, nik, birthPlace, birthDate, gender, phone, address, studyProgram, photoUri, isAgreed ->
                    viewModel.saveApplicant(
                        id = id,
                        fullName = fullName,
                        nik = nik,
                        birthPlace = birthPlace,
                        birthDate = birthDate,
                        gender = gender,
                        phone = phone,
                        address = address,
                        studyProgram = studyProgram,
                        photoUri = photoUri,
                        isAgreed = isAgreed
                    ) { success, message ->
                        Toast.makeText(PmbApplication.instance, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            currentScreen = AppScreen.LIST
                        }
                    }
                }
            )
        }
        AppScreen.AUTH -> {
            AuthScreen(
                viewModel = viewModel,
                onBack = { currentScreen = AppScreen.LIST },
                onAuthSuccess = { currentScreen = AppScreen.LIST }
            )
        }
        AppScreen.STATUS -> {
            ApplicationStatusScreen(
                viewModel = viewModel,
                onBack = { currentScreen = AppScreen.LIST }
            )
        }
    }
}
