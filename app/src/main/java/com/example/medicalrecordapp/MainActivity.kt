package com.example.medicalrecordapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medicalrecordapp.ui.screens.DoctorDashboardScreen
import com.example.medicalrecordapp.ui.screens.LoginScreen
import com.example.medicalrecordapp.ui.screens.RegisterScreen
import com.example.medicalrecordapp.ui.theme.MedicalRecordAppTheme
import com.example.medicalrecordapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicalRecordAppTheme {

                val authViewModel = remember { AuthViewModel() }

                var currentScreen by remember { mutableStateOf("login") }
                var userRole by remember { mutableStateOf("") }

                when (currentScreen) {

                    "login" -> LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            val user = authViewModel.loggedInUser.value
                            userRole = user?.role?.name ?: ""
                            currentScreen = "dashboard"
                        },
                        onGoToRegister = {
                            currentScreen = "register"
                        }
                    )

                    "register" -> RegisterScreen(
                        authViewModel = authViewModel,
                        onRegisterSuccess = {
                            currentScreen = "login"
                        },
                        onBackToLogin = {
                            currentScreen = "login"
                        }
                    )

                    "dashboard" -> {
                        when (userRole) {
                            "ADMIN" -> Text("Admin Dashboard")
                            "DOCTOR" -> DoctorDashboardScreen(
                                onPatientsClick = {
                                },
                                onAppointmentsClick = {
                                },
                                onLogoutClick = {
                                    authViewModel.loggedInUser.value = null
                                    userRole = ""
                                    currentScreen = "login"
                                }
                            )
                            "RECEPTIONIST" -> Text("Reception Dashboard")
                            "PATIENT" -> Text("Patient Dashboard")
                            else -> Text("Unknown Role")
                        }
                    }
                }
            }
        }
    }
}