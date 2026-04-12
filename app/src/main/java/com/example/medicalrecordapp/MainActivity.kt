package com.example.medicalrecordapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.medicalrecordapp.domain.model.Patient
import com.example.medicalrecordapp.ui.screens.AdminDashboardScreen
import com.example.medicalrecordapp.ui.screens.DoctorAppointmentsScreen
import com.example.medicalrecordapp.ui.screens.DoctorDashboardScreen
import com.example.medicalrecordapp.ui.screens.LoginScreen
import com.example.medicalrecordapp.ui.screens.PatientDashboardScreen
import com.example.medicalrecordapp.ui.screens.PatientDetailsScreen
import com.example.medicalrecordapp.ui.screens.PatientsListScreen
import com.example.medicalrecordapp.ui.screens.ReceptionDashboardScreen
import com.example.medicalrecordapp.ui.screens.RegisterScreen
import com.example.medicalrecordapp.ui.theme.MedicalRecordAppTheme
import com.example.medicalrecordapp.viewmodel.AppointmentViewModel
import com.example.medicalrecordapp.viewmodel.AuthViewModel
import com.example.medicalrecordapp.viewmodel.PatientViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicalRecordAppTheme {

                val authViewModel = remember { AuthViewModel() }
                val patientViewModel = remember { PatientViewModel() }
                val appointmentViewModel = remember { AppointmentViewModel() }

                var currentScreen by remember { mutableStateOf("login") }
                var userRole by remember { mutableStateOf("") }
                var selectedPatient by remember { mutableStateOf<Patient?>(null) }

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

                    "patients_list" -> PatientsListScreen(
                        patients = patientViewModel.getPatients(),
                        onPatientClick = { patient ->
                            selectedPatient = patient
                            currentScreen = "patient_details"
                        },
                        onBackClick = {
                            currentScreen = "dashboard"
                        }
                    )

                    "patient_details" -> {
                        selectedPatient?.let { patient ->
                            PatientDetailsScreen(
                                patient = patient,
                                onBackClick = {
                                    currentScreen = "patients_list"
                                }
                            )
                        }
                    }

                    "doctor_appointments" -> DoctorAppointmentsScreen(
                        appointments = appointmentViewModel.getAppointments(),
                        onBackClick = {
                            currentScreen = "dashboard"
                        }
                    )

                    "dashboard" -> {
                        when (userRole) {

                            "ADMIN" -> AdminDashboardScreen(
                                onManageUsersClick = {
                                },
                                onStatisticsClick = {
                                },
                                onLogoutClick = {
                                    authViewModel.loggedInUser.value = null
                                    userRole = ""
                                    currentScreen = "login"
                                }
                            )

                            "DOCTOR" -> DoctorDashboardScreen(
                                onPatientsClick = {
                                    currentScreen = "patients_list"
                                },
                                onAppointmentsClick = {
                                    currentScreen = "doctor_appointments"
                                },
                                onLogoutClick = {
                                    authViewModel.loggedInUser.value = null
                                    userRole = ""
                                    currentScreen = "login"
                                }
                            )

                            "RECEPTIONIST" -> ReceptionDashboardScreen(
                                onRegisterPatientClick = {
                                },
                                onAppointmentsClick = {
                                },
                                onLogoutClick = {
                                    authViewModel.loggedInUser.value = null
                                    userRole = ""
                                    currentScreen = "login"
                                }
                            )

                            "PATIENT" -> PatientDashboardScreen(
                                onMyRecordClick = {
                                },
                                onMyAppointmentsClick = {
                                },
                                onLogoutClick = {
                                    authViewModel.loggedInUser.value = null
                                    userRole = ""
                                    currentScreen = "login"
                                }
                            )

                            else -> Text("Unknown Role")
                        }
                    }
                }
            }
        }
    }
}