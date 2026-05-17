package com.example.medicalrecordapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.medicalrecordapp.domain.model.Appointment
import com.example.medicalrecordapp.domain.model.Patient
import com.example.medicalrecordapp.ui.screens.AdminCreateAccountScreen
import com.example.medicalrecordapp.ui.screens.AdminDashboardScreen
import com.example.medicalrecordapp.ui.screens.DoctorAppointmentsScreen
import com.example.medicalrecordapp.ui.screens.DoctorDashboardScreen
import com.example.medicalrecordapp.ui.screens.LoginScreen
import com.example.medicalrecordapp.ui.screens.MyMedicalRecordScreen
import com.example.medicalrecordapp.ui.screens.PatientAppointmentsScreen
import com.example.medicalrecordapp.ui.screens.PatientDashboardScreen
import com.example.medicalrecordapp.ui.screens.PatientDetailsScreen
import com.example.medicalrecordapp.ui.screens.PatientsListScreen
import com.example.medicalrecordapp.ui.screens.ReceptionDashboardScreen
import com.example.medicalrecordapp.ui.screens.RegisterPatientScreen
import com.example.medicalrecordapp.ui.screens.RegisterScreen
import com.example.medicalrecordapp.ui.screens.RequestAppointmentScreen
import com.example.medicalrecordapp.ui.screens.StaffListScreen
import com.example.medicalrecordapp.ui.theme.MedicalRecordAppTheme
import com.example.medicalrecordapp.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MedicalRecordAppTheme {

                val authViewModel = remember { AuthViewModel() }

                var currentScreen by remember { mutableStateOf("loading") }
                var previousScreen by remember { mutableStateOf("") }
                var selectedPatient by remember { mutableStateOf<Patient?>(null) }

                val patients = remember {
                    mutableStateListOf<Patient>()
                }

                val appointments = remember {
                    mutableStateListOf<Appointment>()
                }

                DisposableEffect(Unit) {
                    val db = FirebaseFirestore.getInstance()

                    val patientsListener = db.collection("patients")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }

                            patients.clear()

                            snapshot?.documents?.forEachIndexed { index, document ->
                                val firstName = document.getString("firstName") ?: ""

                                val familyName = document.getString("familyName")
                                    ?: document.getString("lastName")
                                    ?: ""

                                val gender = document.getString("gender") ?: ""
                                val phone = document.getString("phone") ?: ""
                                val cin = document.getString("cin") ?: ""
                                val dateOfBirth = document.getString("dateOfBirth") ?: ""
                                val address = document.getString("address") ?: ""
                                val bloodGroup = document.getString("bloodGroup") ?: ""
                                val chronicDiseases = document.getString("chronicDiseases") ?: ""

                                val ageValue = document.get("age")
                                val age = when (ageValue) {
                                    is Long -> ageValue.toInt()
                                    is Int -> ageValue
                                    is String -> ageValue.toIntOrNull() ?: 0
                                    else -> 0
                                }

                                patients.add(
                                    Patient(
                                        id = index + 1,
                                        firstName = firstName,
                                        lastName = familyName,
                                        age = age,
                                        gender = gender,
                                        phone = phone,
                                        cin = cin,
                                        dateOfBirth = dateOfBirth,
                                        address = address,
                                        bloodGroup = bloodGroup,
                                        chronicDiseases = chronicDiseases,
                                        documentId = document.id
                                    )
                                )
                            }
                        }

                    val appointmentsListener = db.collection("appointments")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                return@addSnapshotListener
                            }

                            appointments.clear()

                            snapshot?.documents?.forEachIndexed { index, document ->
                                val patientName = document.getString("patientName") ?: ""
                                val date = document.getString("date") ?: ""
                                val time = document.getString("time") ?: ""
                                val status = (document.getString("status") ?: "PENDING")
                                    .trim()
                                    .uppercase()

                                appointments.add(
                                    Appointment(
                                        id = index + 1,
                                        patientName = patientName,
                                        date = date,
                                        time = time,
                                        status = status,
                                        documentId = document.id
                                    )
                                )
                            }
                        }

                    onDispose {
                        patientsListener.remove()
                        appointmentsListener.remove()
                    }
                }

                LaunchedEffect(Unit) {
                    if (authViewModel.isUserLoggedIn()) {
                        authViewModel.getUserRole { role ->
                            currentScreen = when (role) {
                                "DOCTOR" -> "doctor_dashboard"
                                "ADMIN" -> "admin_dashboard"
                                "RECEPTIONIST" -> "reception_dashboard"
                                else -> "patient_dashboard"
                            }
                        }
                    } else {
                        currentScreen = "login"
                    }
                }

                AppNavigation(
                    currentScreen = currentScreen,
                    previousScreen = previousScreen,
                    authViewModel = authViewModel,
                    patients = patients,
                    appointments = appointments,
                    selectedPatient = selectedPatient,
                    onScreenChange = { currentScreen = it },
                    onPreviousScreenChange = { previousScreen = it },
                    onPatientSelected = { selectedPatient = it }
                )
            }
        }
    }
}

@Composable
private fun AppNavigation(
    currentScreen: String,
    previousScreen: String,
    authViewModel: AuthViewModel,
    patients: SnapshotStateList<Patient>,
    appointments: SnapshotStateList<Appointment>,
    selectedPatient: Patient?,
    onScreenChange: (String) -> Unit,
    onPreviousScreenChange: (String) -> Unit,
    onPatientSelected: (Patient?) -> Unit
) {
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
                    onScreenChange(
                        when (role) {
                            "DOCTOR" -> "doctor_dashboard"
                            "ADMIN" -> "admin_dashboard"
                            "RECEPTIONIST" -> "reception_dashboard"
                            else -> "patient_dashboard"
                        }
                    )
                }
            },
            onGoToRegister = {
                onScreenChange("register")
            }
        )

        "register" -> RegisterScreen(
            authViewModel = authViewModel,
            onRegisterSuccess = {
                onScreenChange("login")
            },
            onBackToLogin = {
                onScreenChange("login")
            }
        )

        "doctor_dashboard" -> DoctorDashboardScreen(
            onPatientsClick = {
                onPreviousScreenChange("doctor_dashboard")
                onScreenChange("patients_list")
            },
            onAppointmentsClick = {
                onPreviousScreenChange("doctor_dashboard")
                onScreenChange("doctor_appointments")
            },
            onLogoutClick = {
                authViewModel.logoutUser()
                onScreenChange("login")
            }
        )

        "patient_dashboard" -> PatientDashboardScreen(
            onRequestAppointmentClick = {
                onScreenChange("request_appointment")
            },
            onMyAppointmentsClick = {
                onScreenChange("patient_appointments")
            },
            onMyRecordClick = {
                onScreenChange("my_medical_record")
            },
            onLogoutClick = {
                authViewModel.logoutUser()
                onScreenChange("login")
            }
        )

        "admin_dashboard" -> AdminDashboardScreen(
            onManageUsersClick = {
                onScreenChange("staff_list")
            },
            onCreateAccountClick = {
                onScreenChange("admin_create_account")
            },
            onStatisticsClick = { },
            onLogoutClick = {
                authViewModel.logoutUser()
                onScreenChange("login")
            }
        )

        "reception_dashboard" -> ReceptionDashboardScreen(
            onRegisterPatientClick = {
                onPreviousScreenChange("reception_dashboard")
                onScreenChange("register_patient")
            },
            onPatientsClick = {
                onPreviousScreenChange("reception_dashboard")
                onScreenChange("patients_list")
            },
            onAppointmentsClick = {
                onPreviousScreenChange("reception_dashboard")
                onScreenChange("doctor_appointments")
            },
            onRequestsClick = {
                onPreviousScreenChange("reception_dashboard")
                onScreenChange("appointment_requests")
            },
            onLogoutClick = {
                authViewModel.logoutUser()
                onScreenChange("login")
            }
        )

        "admin_create_account" -> AdminCreateAccountScreen(
            authViewModel = authViewModel,
            onBackClick = {
                onScreenChange("admin_dashboard")
            }
        )

        "staff_list" -> StaffListScreen(
            authViewModel = authViewModel,
            onBackClick = {
                onScreenChange("admin_dashboard")
            }
        )

        "register_patient" -> RegisterPatientScreen(
            onBackClick = {
                onScreenChange("reception_dashboard")
            },
            onSaveClick = { _, _, _, _, _, _, _, _, _ ->
                onPreviousScreenChange("reception_dashboard")
                onScreenChange("patients_list")
            }
        )

        "patients_list" -> PatientsListScreen(
            patients = patients,
            onPatientClick = { patient ->
                onPatientSelected(patient)
                onPreviousScreenChange("patients_list")
                onScreenChange("patient_details")
            },
            onBackClick = {
                onScreenChange(previousScreen.ifBlank { "reception_dashboard" })
            }
        )

        "patient_details" -> {
            selectedPatient?.let { patient ->
                PatientDetailsScreen(
                    patient = patient,
                    onBackClick = {
                        onScreenChange("patients_list")
                    }
                )
            }
        }

        "doctor_appointments" -> DoctorAppointmentsScreen(
            appointments = appointments.filter {
                val status = it.status.trim().uppercase()
                status != "PENDING" && status != "WAITING" && status.isNotBlank()
            },
            showActions = false,
            onBackClick = {
                onScreenChange(previousScreen.ifBlank { "reception_dashboard" })
            }
        )

        "appointment_requests" -> DoctorAppointmentsScreen(
            appointments = appointments.filter {
                val status = it.status.trim().uppercase()
                status == "PENDING" || status == "WAITING" || status.isBlank()
            },
            showActions = true,
            onBackClick = {
                onScreenChange("reception_dashboard")
            }
        )

        "request_appointment" -> RequestAppointmentScreen(
            onBackClick = {
                onScreenChange("patient_dashboard")
            },
            onSubmitClick = { _, _, _, _ ->
                onScreenChange("patient_appointments")
            }
        )

        "patient_appointments" -> PatientAppointmentsScreen(
            appointments = appointments,
            onBackClick = {
                onScreenChange("patient_dashboard")
            }
        )

        "my_medical_record" -> MyMedicalRecordScreen(
            onBackClick = {
                onScreenChange("patient_dashboard")
            }
        )
    }
}
