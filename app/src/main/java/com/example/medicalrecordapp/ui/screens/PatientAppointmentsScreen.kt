package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.example.medicalrecordapp.R
import com.example.medicalrecordapp.domain.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class PatientAppointmentItem(
    val documentId: String = "",
    val patientName: String = "",
    val doctorName: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = "",
    val paymentStatus: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen(
    appointments: List<Appointment>,
    onBackClick: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val patientAppointments = remember { mutableStateListOf<PatientAppointmentItem>() }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    val errNotLoggedIn = stringResource(R.string.user_not_logged_in)
    val errLoadFailed = stringResource(R.string.failed_load_appointments)

    DisposableEffect(Unit) {
        val patientId = auth.currentUser?.uid ?: ""

        if (patientId.isBlank()) {
            isLoading = false
            errorMessage = errNotLoggedIn
            onDispose { }
        } else {
            val listener = db.collection("appointments")
                .whereEqualTo("patientId", patientId)
                .addSnapshotListener { snapshot, error ->
                    isLoading = false

                    if (error != null) {
                        errorMessage = error.message ?: errLoadFailed
                        return@addSnapshotListener
                    }

                    patientAppointments.clear()

                    snapshot?.documents?.forEach { document ->
                        patientAppointments.add(
                            PatientAppointmentItem(
                                documentId = document.id,
                                patientName = document.getString("patientName") ?: "",
                                doctorName = document.getString("doctorName") ?: "",
                                date = document.getString("date") ?: "",
                                time = document.getString("time") ?: "",
                                status = document.getString("status") ?: "PENDING",
                                paymentStatus = document.getString("paymentStatus") ?: "UNPAID"
                            )
                        )
                    }
                }

            onDispose {
                listener.remove()
            }
        }
    }
    BackHandler { onBackClick() }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.my_appointments), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
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
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }

                patientAppointments.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(R.string.no_appointments_found),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(patientAppointments) { appointment ->
                            PatientAppointmentCard(
                                appointment = appointment,
                                onConfirmAndPayClick = {
                                    db.collection("appointments")
                                        .document(appointment.documentId)
                                        .update(
                                            mapOf(
                                                "status" to "CONFIRMED",
                                                "paymentStatus" to "PAID"
                                            )
                                        )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PatientAppointmentCard(
    appointment: PatientAppointmentItem,
    onConfirmAndPayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = appointment.doctorName.ifBlank { stringResource(R.string.doctor) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                StatusBadge(status = appointment.status)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoRow(icon = Icons.Default.CalendarMonth, text = appointment.date)
                InfoRow(icon = Icons.Default.AccessTime, text = appointment.time)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.payment_label) + ": ${appointment.paymentStatus}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
             if (
            appointment.status == "ACCEPTED" ||
                    appointment.status == "SUGGESTED"
            ) {
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onConfirmAndPayClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.confirm_and_pay))
            }
        }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatusBadge(status: String) {
    val badgeColor = when (status.uppercase()) {
        "CONFIRMED" -> Color(0xFF4CAF50)
        "ACCEPTED" -> Color(0xFF4CAF50)
        "PENDING" -> MaterialTheme.colorScheme.primary
        "SUGGESTED" -> Color(0xFFFF9800)
        "REJECTED" -> Color(0xFFF44336)
        "CANCELLED" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Surface(
        color = badgeColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = badgeColor,
            fontWeight = FontWeight.Bold
        )
    }
}