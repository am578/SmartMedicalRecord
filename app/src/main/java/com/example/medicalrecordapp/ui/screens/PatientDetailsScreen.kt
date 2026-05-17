 package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.domain.model.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PatientDetailsScreen(
    patient: Patient,
    onBackClick: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var lastSymptoms by remember { mutableStateOf("No symptoms found") }
    var lastAppointmentDate by remember { mutableStateOf("") }
    var lastAppointmentTime by remember { mutableStateOf("") }

    var diagnosis by remember { mutableStateOf("") }
    var medicalNotes by remember { mutableStateOf("") }
    var prescription by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val patientFullName = "${patient.firstName} ${patient.lastName}".trim()

    LaunchedEffect(patient.cin) {
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->

                val matchedAppointments = result.documents.filter { document ->
                    val appointmentPatientCin = document.getString("patientCin") ?: ""

                    appointmentPatientCin.isNotBlank() &&
                            patient.cin.isNotBlank() &&
                            appointmentPatientCin == patient.cin
                }

                val lastAppointment = matchedAppointments.maxByOrNull { document ->
                    document.getLong("createdAt") ?: 0L
                }

                if (lastAppointment != null) {
                    lastSymptoms = lastAppointment.getString("symptoms") ?: "No symptoms found"
                    lastAppointmentDate = lastAppointment.getString("date") ?: ""
                    lastAppointmentTime = lastAppointment.getString("time") ?: ""
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF7FF))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Patient Details",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = patientFullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                PatientInfoLine(label = "ID", value = patient.id.toString())
                PatientInfoLine(label = "CIN", value = patient.cin)
                PatientInfoLine(label = "Date of Birth", value = patient.dateOfBirth)
                PatientInfoLine(label = "Age", value = patient.age.toString())
                PatientInfoLine(label = "Gender", value = patient.gender)
                PatientInfoLine(label = "Phone", value = patient.phone)
                PatientInfoLine(label = "Address", value = patient.address)
                PatientInfoLine(label = "Blood Group", value = patient.bloodGroup)
                PatientInfoLine(label = "Chronic Diseases", value = patient.
                 chronicDiseases)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Last Appointment Symptoms",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (lastAppointmentDate.isNotBlank() || lastAppointmentTime.isNotBlank()) {
                    Text(text = "Date: $lastAppointmentDate")
                    Text(text = "Time: $lastAppointmentTime")
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(text = lastSymptoms)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Doctor Medical Record",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("Diagnosis") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = medicalNotes,
                    onValueChange = { medicalNotes = it },
                    label = { Text("Medical Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = prescription,
                    onValueChange = { prescription = it },
                    label = { Text("Prescription / Treatment") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (message.contains("saved", ignoreCase = true)) {
                            Color(0xFF2E7D32)
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        when {
                            diagnosis.isBlank() -> {
                                message = "Please enter diagnosis"
                            }

                            medicalNotes.isBlank() -> {
                                message = "Please enter medical notes"
                            }

                            prescription.isBlank() -> {
                                message = "Please enter prescription or treatment"
                            }

                            patient.cin.isBlank() -> {
                                message = "Patient CIN not found"
                            }

                            else -> {
                                isSaving = true
                                message = ""

                                val record = hashMapOf(
                                    "patientDocumentId" to patient.documentId,
                                "patientName" to patientFullName,
                                "patientCin" to patient.cin,
                                "patientPhone" to patient.phone,
                                "diagnosis" to diagnosis,
                                "medicalNotes" to medicalNotes,
                                "prescription" to prescription,
                                "lastSymptoms" to lastSymptoms,
                                "doctorId" to (auth.currentUser?.uid ?: ""),
                                "createdAt" to System.currentTimeMillis()
                                )

                                db.collection("medicalRecords")
                                    .add(record)
                                    .addOnSuccessListener {
                                        isSaving = false
                                        message = "Medical record saved successfully"
                                        diagnosis = ""
                                        medicalNotes = ""
                                        prescription = ""
                                    }
                                    .addOnFailureListener { e ->
                                        isSaving = false
                                        message = e.message ?: "Failed to save medical record"
                                    }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D7FF9)
                    ),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Medical Record")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D7FF9)
            )
        ) {
            Text("Back")
        }
    }
}

@Composable
fun PatientInfoLine(
    label: String,
    value: String
) {
    if (value.isNotBlank() && value != "0") {
        Text(text = "$label: $value")
        Spacer(modifier = Modifier.height(4.dp))
    }
}