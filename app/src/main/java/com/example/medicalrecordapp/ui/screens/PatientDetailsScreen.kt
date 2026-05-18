package com.example.medicalrecordapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.medicalrecordapp.domain.model.AttachmentType
import com.example.medicalrecordapp.domain.model.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AppointmentSymptom(
    val symptoms: String,
    val date: String,
    val time: String,
    val attachmentUrl: String,
    val attachmentType: AttachmentType,
    val attachmentName: String,
    val createdAt: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(
    patient: Patient,
    onBackClick: () -> Unit = {}
) {
    BackHandler { onBackClick() }

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uriHandler = LocalUriHandler.current

    var appointmentSymptoms by remember { mutableStateOf<List<AppointmentSymptom>>(emptyList()) }
    var diagnosis by remember { mutableStateOf("") }
    var medicalNotes by remember { mutableStateOf("") }
    var prescription by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val patientFullName = "${patient.firstName} ${patient.lastName}".trim()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()) }

    LaunchedEffect(patient.cin) {
        if (patient.cin.isNotBlank()) {
            db.collection("appointments")
                .whereEqualTo("patientCin", patient.cin)
                .get()
                .addOnSuccessListener { result ->
                    appointmentSymptoms = result.documents
                        .filter { it.getString("symptoms")?.isNotBlank() == true }
                        .map { doc ->
                            AppointmentSymptom(
                                symptoms = doc.getString("symptoms") ?: "",
                                date = doc.getString("date") ?: "",
                                time = doc.getString("time") ?: "",
                                attachmentUrl = doc.getString("attachmentUrl") ?: "",
                                attachmentType = try {
                                    AttachmentType.valueOf(doc.getString("attachmentType") ?: "NONE")
                                } catch (_: Exception) { AttachmentType.NONE },
                                attachmentName = doc.getString("attachmentName") ?: "",
                                createdAt = doc.getLong("createdAt") ?: 0L
                            )
                        }.sortedByDescending { it.createdAt }
                }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Patient Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            // ====== بيانات المريض ======
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(patientFullName, style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    PatientInfoLine("CIN", patient.cin)
                    PatientInfoLine("Date of Birth", patient.dateOfBirth)
                    PatientInfoLine("Age", patient.age.toString())
                    PatientInfoLine("Gender", patient.gender)
                    PatientInfoLine("Phone", patient.phone)
                    PatientInfoLine("Address", patient.address)
                    PatientInfoLine("Blood Group", patient.bloodGroup)
                    PatientInfoLine("Chronic Diseases", patient.chronicDiseases)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ====== الأعراض السابقة ======
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Patient Symptoms History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57F17))
                    Spacer(Modifier.height(10.dp))

                    if (appointmentSymptoms.isEmpty()) {
                        Text("No symptoms history found", color = Color.Gray)
                    } else {
                        appointmentSymptoms.forEach { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(dateFormat.format(Date(item.createdAt)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray)
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(item.symptoms, style = MaterialTheme.typography.bodyMedium)

                                    if (item.attachmentUrl.isNotBlank()) {
                                        Spacer(Modifier.height(10.dp))
                                        when (item.attachmentType) {
                                            AttachmentType.IMAGE -> {
                                                AsyncImage(
                                                    model = item.attachmentUrl,
                                                    contentDescription = "Symptom image",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(200.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .clickable { uriHandler.openUri(item.attachmentUrl) },
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            AttachmentType.FILE -> {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { uriHandler.openUri(item.attachmentUrl) }
                                                        .padding(vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(item.attachmentName.ifBlank { "Attached File" },
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ====== السجل الطبي ======
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Doctor Medical Record",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = diagnosis, onValueChange = { diagnosis = it },
                        label = { Text("Diagnosis") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = medicalNotes, onValueChange = { medicalNotes = it },
                        label = { Text("Medical Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(value = prescription, onValueChange = { prescription = it },
                        label = { Text("Prescription / Treatment") },
                        modifier = Modifier.fillMaxWidth(), minLines = 3)
                    Spacer(Modifier.height(12.dp))

                    if (message.isNotEmpty()) {
                        Text(
                            text = message,
                            color = if (message.contains("saved", ignoreCase = true))
                                Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    Button(
                        onClick = {
                            when {
                                diagnosis.isBlank() -> message = "Please enter diagnosis"
                                medicalNotes.isBlank() -> message = "Please enter medical notes"
                                prescription.isBlank() -> message = "Please enter prescription"
                                patient.cin.isBlank() -> message = "Patient CIN not found"
                                else -> {
                                    isSaving = true; message = ""
                                    val record = hashMapOf(
                                        "patientDocumentId" to patient.documentId,
                                        "patientName" to patientFullName,
                                        "patientCin" to patient.cin,
                                        "patientPhone" to patient.phone,
                                        "diagnosis" to diagnosis,
                                        "medicalNotes" to medicalNotes,
                                        "prescription" to prescription,
                                        "doctorId" to (auth.currentUser?.uid ?: ""),
                                        "createdAt" to System.currentTimeMillis()
                                    )
                                    db.collection("medicalRecords").add(record)
                                        .addOnSuccessListener {
                                            isSaving = false
                                            message = "Medical record saved successfully"
                                            diagnosis = ""; medicalNotes = ""; prescription = ""
                                        }
                                        .addOnFailureListener { e ->
                                            isSaving = false
                                            message = e.message ?: "Failed to save"
                                        }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7FF9)),
                        enabled = !isSaving
                    ) {
                        if (isSaving) CircularProgressIndicator(color = Color.White,
                            modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        else Text("Save Medical Record")
                    }
                }
            }
        }
    }
}

@Composable
fun PatientInfoLine(label: String, value: String) {
    if (value.isNotBlank() && value != "0") {
        Text("$label: $value")
        Spacer(Modifier.height(4.dp))
    }
}
