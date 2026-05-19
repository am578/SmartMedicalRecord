package com.example.medicalrecordapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.medicalrecordapp.domain.model.Appointment
import com.example.medicalrecordapp.domain.model.Patient
import com.example.medicalrecordapp.ui.screens.*
import com.example.medicalrecordapp.ui.theme.MedicalRecordAppTheme
import com.example.medicalrecordapp.utils.LanguageManager
import com.example.medicalrecordapp.utils.LocalLanguage
import com.example.medicalrecordapp.viewmodel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.wrapContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val authViewModel = remember { AuthViewModel(application) }
            val languageState = remember { mutableStateOf(LanguageManager.getSavedLanguage(this)) }

            CompositionLocalProvider(LocalLanguage provides languageState) {
                MedicalRecordAppTheme {

                    var currentScreen by remember { mutableStateOf("loading") }
                    var previousScreen by remember { mutableStateOf("") }
                    var selectedPatient by remember { mutableStateOf<Patient?>(null) }
                    val patients = remember { mutableStateListOf<Patient>() }
                    val appointments = remember { mutableStateListOf<Appointment>() }

                    // زر الباك من الهاتف (System Back Button)
                    BackHandler(enabled = currentScreen != "login" && currentScreen != "loading") {
                        currentScreen = when (currentScreen) {
                            "admin_create_account" -> "admin_dashboard"
                            "staff_list" -> "admin_dashboard"
                            "register" -> "login"
                            "patients_list" -> previousScreen.ifBlank { "reception_dashboard" }
                            "patient_details" -> "patients_list"
                            "doctor_appointments" -> previousScreen.ifBlank { "doctor_dashboard" }
                            "appointment_requests" -> "reception_dashboard"
                            "register_patient" -> "reception_dashboard"
                            "request_appointment" -> "patient_dashboard"
                            "patient_appointments" -> "patient_dashboard"
                            "my_medical_record" -> "patient_dashboard"
                            "doctor_symptoms" -> "doctor_dashboard"
                            "submit_symptoms" -> "patient_dashboard"
                            "doctor_dashboard", "patient_dashboard", "admin_dashboard", "reception_dashboard" -> "login"
                            else -> "login"
                        }
                    }

                    DisposableEffect(Unit) {
                        val db = FirebaseFirestore.getInstance()
                        val patientsListener = db.collection("patients").addSnapshotListener { snapshot, error ->
                            if (error != null) return@addSnapshotListener
                            patients.clear()
                            snapshot?.documents?.forEachIndexed { index, document ->
                                val ageValue = document.get("age")
                                val age = when (ageValue) {
                                    is Long -> ageValue.toInt(); is Int -> ageValue
                                    is String -> ageValue.toIntOrNull() ?: 0; else -> 0
                                }
                                patients.add(Patient(
                                    id = index + 1,
                                    firstName = document.getString("firstName") ?: "",
                                    lastName = document.getString("familyName") ?: document.getString("lastName") ?: "",
                                    age = age,
                                    gender = document.getString("gender") ?: "",
                                    phone = document.getString("phone") ?: "",
                                    cin = document.getString("cin") ?: "",
                                    dateOfBirth = document.getString("dateOfBirth") ?: "",
                                    address = document.getString("address") ?: "",
                                    bloodGroup = document.getString("bloodGroup") ?: "",
                                    chronicDiseases = document.getString("chronicDiseases") ?: "",
                                    documentId = document.id
                                ))
                            }
                        }
                        val appointmentsListener = db.collection("appointments").addSnapshotListener { snapshot, error ->
                            if (error != null) return@addSnapshotListener
                            appointments.clear()
                            snapshot?.documents?.forEachIndexed { index, document ->
                                appointments.add(Appointment(
                                    id = index + 1,
                                    patientName = document.getString("patientName") ?: "",
                                    date = document.getString("date") ?: "",
                                    time = document.getString("time") ?: "",
                                    status = (document.getString("status") ?: "PENDING").trim().uppercase(),
                                    documentId = document.id
                                ))
                            }
                        }
                        onDispose { patientsListener.remove(); appointmentsListener.remove() }
                    }

                    LaunchedEffect(Unit) {
                        if (authViewModel.isUserLoggedIn()) {
                            authViewModel.getUserRole { role ->
                                currentScreen = when (role) {
                                    "DOCTOR" -> "doctor_dashboard"; "ADMIN" -> "admin_dashboard"
                                    "RECEPTIONIST" -> "reception_dashboard"; else -> "patient_dashboard"
                                }
                            }
                        } else currentScreen = "login"
                    }

                    AppNavigation(
                        currentScreen = currentScreen, previousScreen = previousScreen,
                        authViewModel = authViewModel, patients = patients, appointments = appointments,
                        selectedPatient = selectedPatient,
                        onScreenChange = { currentScreen = it },
                        onPreviousScreenChange = { previousScreen = it },
                        onPatientSelected = { selectedPatient = it },
                        onLanguageChange = { lang ->
                            LanguageManager.saveLanguage(this@MainActivity, lang)
                            languageState.value = lang
                            recreate()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppNavigation(
    currentScreen: String, previousScreen: String,
    authViewModel: AuthViewModel,
    patients: SnapshotStateList<Patient>, appointments: SnapshotStateList<Appointment>,
    selectedPatient: Patient?,
    onScreenChange: (String) -> Unit, onPreviousScreenChange: (String) -> Unit,
    onPatientSelected: (Patient?) -> Unit, onLanguageChange: (String) -> Unit
) {
    when (currentScreen) {
        "loading" -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

        "login" -> LoginScreen(
            authViewModel = authViewModel,
            onLoginSuccess = { authViewModel.getUserRole { role -> onScreenChange(when (role) { "DOCTOR" -> "doctor_dashboard"; "ADMIN" -> "admin_dashboard"; "RECEPTIONIST" -> "reception_dashboard"; else -> "patient_dashboard" }) } },
            onGoToRegister = { onScreenChange("register") }
        )
        "register" -> RegisterScreen(authViewModel = authViewModel, onRegisterSuccess = { onScreenChange("login") }, onBackToLogin = { onScreenChange("login") })

        "doctor_dashboard" -> DoctorDashboardScreen(
            onPatientsClick = { onPreviousScreenChange("doctor_dashboard"); onScreenChange("patients_list") },
            onAppointmentsClick = { onPreviousScreenChange("doctor_dashboard"); onScreenChange("doctor_appointments") },
            onLanguageChange = onLanguageChange,
            onLogoutClick = { authViewModel.logoutUser(); onScreenChange("login") }
        )
        "patient_dashboard" -> PatientDashboardScreen(
            onRequestAppointmentClick = { onScreenChange("request_appointment") },
            onMyAppointmentsClick = { onScreenChange("patient_appointments") },
            onMyRecordClick = { onScreenChange("my_medical_record") },
            onSubmitSymptomsClick = { onScreenChange("submit_symptoms") },
            onLanguageChange = onLanguageChange,
            onLogoutClick = { authViewModel.logoutUser(); onScreenChange("login") }
        )
        "admin_dashboard" -> AdminDashboardScreen(
            onManageUsersClick = { onScreenChange("staff_list") },
            onCreateAccountClick = { onScreenChange("admin_create_account") },
            onStatisticsClick = { },
            onLanguageChange = onLanguageChange,
            onLogoutClick = { authViewModel.logoutUser(); onScreenChange("login") }
        )
        "reception_dashboard" -> ReceptionDashboardScreen(
            onRegisterPatientClick = { onPreviousScreenChange("reception_dashboard"); onScreenChange("register_patient") },
            onAppointmentsClick = { onPreviousScreenChange("reception_dashboard"); onScreenChange("doctor_appointments") },
            onRequestsClick = { onPreviousScreenChange("reception_dashboard"); onScreenChange("appointment_requests") },
            onLanguageChange = onLanguageChange,
            onLogoutClick = { authViewModel.logoutUser(); onScreenChange("login") }
        )
        "admin_create_account" -> AdminCreateAccountScreen(authViewModel = authViewModel, onBackClick = { onScreenChange("admin_dashboard") })
        "staff_list" -> StaffListScreen(authViewModel = authViewModel, onBackClick = { onScreenChange("admin_dashboard") })
        "register_patient" -> RegisterPatientScreen(onBackClick = { onScreenChange("reception_dashboard") }, onSaveClick = { _, _, _, _, _, _, _, _, _ -> onScreenChange("patients_list") })
        "patients_list" -> PatientsListScreen(
            patients = patients,
            onPatientClick = { patient -> 
                if (previousScreen == "doctor_dashboard") {
                    onPatientSelected(patient)
                    onPreviousScreenChange("patients_list")
                    onScreenChange("patient_details")
                }
            },
            onBackClick = { onScreenChange(previousScreen.ifBlank { "reception_dashboard" }) }
        )
        "patient_details" -> selectedPatient?.let { PatientDetailsScreen(patient = it, onBackClick = { onScreenChange("patients_list") }) }
        "doctor_appointments" -> DoctorAppointmentsScreen(
            appointments = appointments.filter { val s = it.status.trim().uppercase(); s != "PENDING" && s != "WAITING" && s.isNotBlank() },
            showActions = false, onBackClick = { onScreenChange(previousScreen.ifBlank { "reception_dashboard" }) }
        )
        "appointment_requests" -> DoctorAppointmentsScreen(
            appointments = appointments.filter { val s = it.status.trim().uppercase(); s == "PENDING" || s == "WAITING" || s.isBlank() },
            showActions = true, onBackClick = { onScreenChange("reception_dashboard") }
        )
        "request_appointment" -> RequestAppointmentScreen(onBackClick = { onScreenChange("patient_dashboard") }, onSubmitClick = { _, _, _, _ -> onScreenChange("patient_appointments") })
        "patient_appointments" -> PatientAppointmentsScreen(appointments = appointments, onBackClick = { onScreenChange("patient_dashboard") })
        "my_medical_record" -> MyMedicalRecordScreen(onBackClick = { onScreenChange("patient_dashboard") })
        "doctor_symptoms" -> DoctorSymptomsScreen(authViewModel = authViewModel, onBackClick = { onScreenChange("doctor_dashboard") })
        "submit_symptoms" -> SubmitSymptomsScreen(authViewModel = authViewModel, onBackClick = { onScreenChange("patient_dashboard") })
    }
}
