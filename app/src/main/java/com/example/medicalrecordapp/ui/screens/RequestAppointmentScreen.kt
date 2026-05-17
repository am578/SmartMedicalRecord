 package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestAppointmentScreen(
    onBackClick: () -> Unit = {},
    onSubmitClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    var doctorName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }

    var formError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Request Appointment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    AppointmentInputField(
                        value = doctorName,
                        onValueChange = { doctorName = it },
                        label = "Doctor Name",
                        icon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppointmentInputField(
                        value = date,
                        onValueChange = { date = it },
                        label = "Preferred Date (YYYY-MM-DD)",
                        icon = Icons.Default.CalendarMonth
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppointmentInputField(
                        value = time,
                        onValueChange = { time = it },
                        label = "Preferred Time",
                        icon = Icons.Default.Schedule
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                     OutlinedTextField(
                    value = symptoms,
                    onValueChange = { symptoms = it },
                    label = { Text("Symptoms / Notes") },
                    placeholder = { Text("How do you feel?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (formError.isNotEmpty()) {
                        Text(
                            text = formError,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            when {
                                doctorName.isBlank() -> formError = "Please enter doctor name"
                                date.isBlank() -> formError = "Please enter preferred date"
                                time.isBlank() -> formError = "Please enter preferred time"
                                symptoms.isBlank() -> formError = "Please enter symptoms or notes"
                                else -> {
                                    formError = ""
                                    isLoading = true

                                    val currentUser = auth.currentUser
                                    val patientId = currentUser?.uid ?: ""
                                    val patientEmail = currentUser?.email ?: ""

                                    val appointment = hashMapOf(
                                        "patientId" to patientId,
                                        "patientEmail" to patientEmail,
                                        "patientName" to patientEmail,
                                        "doctorName" to doctorName,
                                        "date" to date,
                                        "time" to time,
                                        "symptoms" to symptoms,
                                        "status" to "PENDING",
                                        "paymentStatus" to "UNPAID",
                                        "createdAt" to System.currentTimeMillis()
                                    )

                                    db.collection("appointments")
                                        .add(appointment)
                                        .addOnSuccessListener {
                                            isLoading = false
                                            onSubmitClick(doctorName, date, time, symptoms)
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            formError = e.message ?: "Failed to submit appointment request"
                                        }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                             containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !isLoading
                    ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Submit Request", fontWeight = FontWeight.Bold)
                    }
                }
                }
            }
        }
    }
}

@Composable
fun AppointmentInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}