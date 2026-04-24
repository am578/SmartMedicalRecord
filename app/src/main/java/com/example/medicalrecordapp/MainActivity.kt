package com.example.medicalrecordapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import com.example.medicalrecordapp.domain.model.Patient
import com.example.medicalrecordapp.ui.screens.*
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

                var currentScreen by remember { mutableStateOf("login") }
                var userRole by remember { mutableStateOf("") }
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

                            "ADMIN" -> AdminDashboardScreen(
                                onManageUsersClick = {},
                                onStatisticsClick = {},
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
                                    currentScreen = "register_patient"
                                },
                                onPatientsClick = {
                                    currentScreen = "patients_list"
                                },
                                onAppointmentsClick = {
                                    currentScreen = "doctor_appointments"
                                },
                                onRequestsClick = {
                                    currentScreen = "appointment_requests"
                                },
                                onLogoutClick = {
                                    authViewModel.loggedInUser.value = null
                                    userRole = ""
                                    currentScreen = "login"
                                }
                            )

                            "PATIENT" -> PatientDashboardScreen(
                                onRequestAppointmentClick = {
                                    currentScreen = "request_appointment"
                                },
                                onMyAppointmentsClick = {
                                    currentScreen = "patient_appointments"
                                },
                                onMyRecordClick = {},
                                onLogoutClick = {
                                    authViewModel.loggedInUser.value = null
                                    userRole = ""
                                    currentScreen = "login"
                                }
                            )

                            else -> Text("Unknown Role")
                        }
                    }

                    "register_patient" -> RegisterPatientScreen(
                        onBackClick = {
                            currentScreen = "dashboard"
                        },
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

                    "request_appointment" -> RequestAppointmentScreen(
                        onBackClick = {
                            currentScreen = "dashboard"
                        },
                        onSubmitClick = {
                            currentScreen = "patient_appointments"
                        }
                    )

                    "patient_appointments" -> PatientAppointmentsScreen(
                        appointments = appointmentViewModel.getAppointments(),
                        onBackClick = {
                            currentScreen = "dashboard"
                        }
                    )

                    "appointment_requests" -> AppointmentRequestsScreen(
                        appointments = appointments,
                        onBackClick = {
                            currentScreen = "dashboard"
                        }
                    )
                }
            }
        }
    }
}