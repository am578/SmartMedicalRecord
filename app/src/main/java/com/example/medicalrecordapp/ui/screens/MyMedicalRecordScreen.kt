package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.activity.compose.BackHandler
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class MedicalRecordItem(
    val diagnosis: String = "",
    val medicalNotes: String = "",
    val prescription: String = "",
    val lastSymptoms: String = "",
    val createdAt: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyMedicalRecordScreen(
    onBackClick: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var bloodGroup by remember { mutableStateOf("") }
    var chronicDiseases by remember { mutableStateOf("") }
    var patientFullName by remember { mutableStateOf("") }
    var patientCin by remember { mutableStateOf("") }

    var records by remember { mutableStateOf<List<MedicalRecordItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val errNotLoggedIn = stringResource(R.string.user_not_logged_in)
    val errCinNotFound = stringResource(R.string.err_cin_not_found)

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        val patientId = currentUser?.uid ?: ""

        if (patientId.isBlank()) {
            isLoading = false
            errorMessage = errNotLoggedIn
            return@LaunchedEffect
        }

        db.collection("users")
            .document(patientId)
            .get()
            .addOnSuccessListener { userDoc ->

                val firstName = userDoc.getString("firstName") ?: ""
                val familyName = userDoc.getString("familyName") ?: ""

                patientFullName = "$firstName $familyName".trim()
                patientCin = userDoc.getString("cin") ?: ""
                bloodGroup = userDoc.getString("bloodGroup") ?: ""
                chronicDiseases = userDoc.getString("chronicDiseases") ?: ""

                if (patientCin.isBlank()) {
                    isLoading = false
                    errorMessage = errCinNotFound
                    return@addOnSuccessListener
                }

                db.collection("medicalRecords")
                    .whereEqualTo("patientCin", patientCin)
                    .get()
                    .addOnSuccessListener { result ->

                        records = result.documents.map { document ->
                            MedicalRecordItem(
                                diagnosis = document.getString("diagnosis") ?: "",
                                medicalNotes = document.getString("medicalNotes") ?: "",
                                prescription = document.getString("prescription") ?: "",
                                lastSymptoms = document.getString("lastSymptoms") ?: "",
                                createdAt = document.getLong("createdAt") ?: 0L
                            )
                        }.sortedByDescending { it.createdAt }

                        isLoading = false
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        errorMessage = e.message ?: "Failed to load medical records"
                    }
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = e.message ?: "Failed to load patient data"
            }
    }
    BackHandler { onBackClick() }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                 title = {
            Text(
                text = stringResource(R.string.my_medical_record),
                fontWeight = FontWeight.Bold
            )
        },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
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
                .verticalScroll(rememberScrollState())
        ) {

            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    Row {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.health_summary),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {

                            Text(
                                text = stringResource(R.string.patient) + ": ${patientFullName.ifBlank { stringResource(R.string.patient) }}",
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            MedicalInfoLine(
                                label = stringResource(R.string.cin_id_label),
                                value = patientCin
                            )

                            MedicalInfoLine(
                                label = stringResource(R.string.blood_group),
                                value = bloodGroup.ifBlank { stringResource(R.string.not_specified) }
                            )

                            MedicalInfoLine(
                                label = stringResource(R.string.chronic_diseases),
                                value = chronicDiseases.ifBlank { stringResource(R.string.none) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.medical_records),
                            style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (records.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(stringResource(R.string.no_records_found))
                            }
                        }
                    } else {
                        records.forEach { record ->
                            MedicalRecordCard(record = record)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MedicalRecordCard(record: MedicalRecordItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.diagnosis),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = record.diagnosis.ifBlank { stringResource(R.string.no_diagnosis) },
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.medical_notes) + ":",
                fontWeight = FontWeight.Bold
            )
            Text(text = record.medicalNotes.ifBlank { stringResource(R.string.no_notes) })

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.symptoms) + ":",
                fontWeight = FontWeight.Bold
            )
            Text(text = record.lastSymptoms.ifBlank { stringResource(R.string.no_symptoms) })

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.prescription),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = record.prescription.ifBlank { stringResource(R.string.no_prescription) })
        }
    }
}

@Composable
fun MedicalInfoLine(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}