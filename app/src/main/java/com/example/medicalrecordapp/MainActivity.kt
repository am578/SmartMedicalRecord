 package com.example.medicalrecordapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

                // مؤقتًا: قائمة مرضى محلية، بعدها نقدر نربطها بـ Firestore
                val patients = remember {
                    mutableStateListOf(
                        Patient(1, "Ahmed", "Benali", 25, "Male", "0550123456"),
                        Patient(2, "Sara", "Amrani", 30, "Female", "0661234567"),
                        Patient(3, "Yacine", "Boudiaf", 40, "Male", "0777654321")
                    )
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
                    authViewModel = authViewModel,
                    appointmentViewModel = appointmentViewModel,
                    patients = patients,
                    selectedPatient = selectedPatient,
                    onScreenChange = { currentScreen = it },
                    onPatientSelected = { selectedPatient = it }
                )
            }
        }
    }
}

@Composable
private fun AppNavigation(
     currentScreen: String,
authViewModel: AuthViewModel,
appointmentViewModel: AppointmentViewModel,
patients: androidx.compose.runtime.snapshots.SnapshotStateList<Patient>,
selectedPatient: Patient?,
onScreenChange: (String) -> Unit,
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
            onGoToRegister = { onScreenChange("register") }
        )

        "register" -> RegisterScreen(
            authViewModel = authViewModel,
            onRegisterSuccess = { onScreenChange("login") },
            onBackToLogin = { onScreenChange("login") }
        )

        "doctor_dashboard" -> DoctorDashboardScreen(
            onPatientsClick = { onScreenChange("patients_list") },
            onAppointmentsClick = { onScreenChange("doctor_appointments") },
            onLogoutClick = {
                authViewModel.logoutUser()
                onScreenChange("login")
            }
        )

        "patient_dashboard" -> PatientDashboardScreen(
            onRequestAppointmentClick = { onScreenChange("request_appointment") },
            onMyAppointmentsClick = { onScreenChange("patient_appointments") },
            onMyRecordClick = { onScreenChange("my_medical_record") },
            onLogoutClick = {
                authViewModel.logoutUser()
                onScreenChange("login")
            }
        )

        "admin_dashboard" -> AdminDashboardScreen(
            onManageUsersClick = { onScreenChange("staff_list") },
            onCreateAccountClick = { onScreenChange("admin_create_account") },
            onStatisticsClick = { },
            onLogoutClick = {
                authViewModel.logoutUser()
                onScreenChange("login")
            }
        )

        // صفحة السكرتير بعد التعديل
        "reception_dashboard" -> ReceptionDashboardScreen(
            onRegisterPatientClick = {
                onScreenChange("register_patient")
            },
            onPatientsClick = {
                onScreenChange("patients_list")
            },
            onAppointmentsClick = {
                onScreenChange("doctor_appointments")
            },
            onRequestsClick = {
                onScreenChange("doctor_appointments")
            },
            onLogoutClick = {
                authViewModel.logoutUser()
                onScreenChange("login")
            }
        )

        "admin_create_account" -> AdminCreateAccountScreen(
            authViewModel = authViewModel,
            onBackClick = { onScreenChange("admin_dashboard") }
        )

        "staff_list" -> StaffListScreen(
            authViewModel = authViewModel,
            onBackClick = { onScreenChange("admin_dashboard") }
        )

        "register_patient" -> RegisterPatientScreen(
            onBackClick = { onScreenChange("reception_dashboard") },
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
                onScreenChange("patients_list")
            }
        )
        "patients_list" -> PatientsListScreen(
        patients = patients,
        onPatientClick = { patient ->
            onPatientSelected(patient)
            onScreenChange("patient_details")
        },
        onBackClick = { onScreenChange("reception_dashboard") }
    )

        "patient_details" -> {
            selectedPatient?.let { patient ->
                PatientDetailsScreen(
                    patient = patient,
                    onBackClick = { onScreenChange("patients_list") }
                )
            }
        }

        "doctor_appointments" -> DoctorAppointmentsScreen(
            appointments = appointmentViewModel.getAppointments(),
            onBackClick = { onScreenChange("reception_dashboard") }
        )

        "request_appointment" -> RequestAppointmentScreen(
            onBackClick = { onScreenChange("patient_dashboard") },
            onSubmitClick = { _, _, _, _ ->
                onScreenChange("patient_appointments")
            }
        )

        "patient_appointments" -> PatientAppointmentsScreen(
            appointments = appointmentViewModel.getAppointments(),
            onBackClick = { onScreenChange("patient_dashboard") }
        )

        "my_medical_record" -> MyMedicalRecordScreen(
            onBackClick = { onScreenChange("patient_dashboard") }
        )
    }
}