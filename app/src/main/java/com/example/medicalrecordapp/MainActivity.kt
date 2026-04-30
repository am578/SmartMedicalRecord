package com.example.medicalrecordapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.medicalrecordapp.domain.model.Patient
import com.example.medicalrecordapp.ui.screens.AdminDashboardScreen
import com.example.medicalrecordapp.ui.screens.DoctorAppointmentsScreen
import com.example.medicalrecordapp.ui.screens.DoctorDashboardScreen
import com.example.medicalrecordapp.ui.screens.LoginScreen
import com.example.medicalrecordapp.ui.screens.PatientAppointmentsScreen
import com.example.medicalrecordapp.ui.screens.PatientDashboardScreen
import com.example.medicalrecordapp.ui.screens.PatientDetailsScreen
import com.example.medicalrecordapp.ui.screens.PatientsListScreen
import com.example.medicalrecordapp.ui.screens.ReceptionDashboardScreen
import com.example.medicalrecordapp.ui.screens.RegisterPatientScreen
import com.example.medicalrecordapp.ui.screens.RegisterScreen
import com.example.medicalrecordapp.ui.screens.RequestAppointmentScreen
import com.example.medicalrecordapp.ui.theme.MedicalRecordAppTheme
import com.example.medicalrecordapp.viewmodel.AppointmentViewModel
import com.example.medicalrecordapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MedicalRecordAppTheme {

                val authViewModel = remember { AuthViewModel() }
                val appointmentViewModel = remember { AppointmentViewModel() }

                var currentScreen by remember { mutableStateOf("loading") }

                var selectedPatient by remember { mutableStateOf<Patient?>(null) }

                val patients = remember {
                    mutableStateListOf(
                        Patient(1, "Ahmed", "Benali", 25, "Male", "0550123456"),
                        Patient(2, "Sara", "Amrani", 30, "Female", "0661234567"),
                        Patient(3, "Yacine", "Boudiaf", 40, "Male", "0777654321")
                    )
                }

                val appointments = remember {
                    mutableStateListOf<com.example.medicalrecordapp.domain.model.Appointment>()
                }

                LaunchedEffect(Unit) {
                    if (authViewModel.isUserLoggedIn()) {
                        authViewModel.getUserRole { role ->
                            currentScreen = when (role) {
                                "doctor" -> "doctor_dashboard"
                                "admin" -> "admin_dashboard"
                                else -> "patient_dashboard"
                            }
                        }
                    } else {
                        currentScreen = "login"
                    }
                }

                when (currentScreen) {

                    "loading" -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                    "login" -> LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            authViewModel.getUserRole { role ->
                                currentScreen = when (role) {
                                    "doctor" -> "doctor_dashboard"
                                    "admin" -> "admin_dashboard"
                                    else -> "patient_dashboard"
                                }
                            }
                        },
                        onGoToRegister = { currentScreen = "register" }
                    )

                    "register" -> RegisterScreen(
                        authViewModel = authViewModel,
                        onRegisterSuccess = { currentScreen = "login" },
                        onBackToLogin = { currentScreen = "login" }
                    )

                    // ────── Doctor ──────
                    "doctor_dashboard" -> DoctorDashboardScreen(
                        onPatientsClick = { currentScreen = "patients_list" },
                        onAppointmentsClick = { currentScreen = "doctor_appointments" },
                        onLogoutClick = {
                            authViewModel.logoutUser()
                            currentScreen = "login"
                        }
                    )

                    // ────── Patient ──────
                    "patient_dashboard" -> PatientDashboardScreen(
                        onRequestAppointmentClick = { currentScreen = "request_appointment" },
                        onMyAppointmentsClick = { currentScreen = "patient_appointments" },
                        onMyRecordClick = { /* TODO */ },
                        onLogoutClick = {
                            authViewModel.logoutUser()
                            currentScreen = "login"
                        }
                    )

                    // ────── Admin ──────
                    "admin_dashboard" -> AdminDashboardScreen(
                        onLogoutClick = {
                            authViewModel.logoutUser()
                            currentScreen = "login"
                        }
                    )

                    // ────── Shared screens ──────
                    "register_patient" -> RegisterPatientScreen(
                        onBackClick = { currentScreen = "doctor_dashboard" },
                        onSaveClick = { firstName, lastName, age, gender, phone ->
                            patients.add(
                                Patient(
                                    id = patients.size + 1,
                                    firstName = firstName,
                                    lastName = lastName,
                                    age = age.toIntOrNull() ?: 0,
                                    gender = gender,
                                    phone = phone
                                )
                            )
                            currentScreen = "patients_list"
                        }
                    )

                    "patients_list" -> PatientsListScreen(
                        patients = patients,
                        onPatientClick = { patient ->
                            selectedPatient = patient
                            currentScreen = "patient_details"
                        },
                        onBackClick = { currentScreen = "doctor_dashboard" }
                    )

                    "patient_details" -> {
                        selectedPatient?.let { patient ->
                            PatientDetailsScreen(
                                patient = patient,
                                onBackClick = { currentScreen = "patients_list" }
                            )
                        }
                    }

                    "doctor_appointments" -> DoctorAppointmentsScreen(
                        appointments = appointmentViewModel.getAppointments(),
                        onBackClick = { currentScreen = "doctor_dashboard" }
                    )

                    "request_appointment" -> RequestAppointmentScreen(
                        onBackClick = { currentScreen = "patient_dashboard" },
                        onSubmitClick = { currentScreen = "patient_appointments" }
                    )

                    "patient_appointments" -> PatientAppointmentsScreen(
                        appointments = appointmentViewModel.getAppointments(),
                        onBackClick = { currentScreen = "patient_dashboard" }
                    )
                }
            }
        }
    }
}
