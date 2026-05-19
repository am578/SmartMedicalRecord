package com.example.medicalrecordapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.R
import com.example.medicalrecordapp.domain.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DoctorAppointmentsScreen(
    appointments: List<Appointment>,
    showActions: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    BackHandler { onBackClick() }

    val db = FirebaseFirestore.getInstance()

    val title = if (showActions) stringResource(R.string.appointment_requests) else stringResource(R.string.appointments)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Text(stringResource(R.string.back))
        }

        Spacer(Modifier.height(16.dp))

        if (appointments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                Text(stringResource(R.string.no_appointments_found), color = Color.Gray) 
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(appointments) { appointment ->
                    AppointmentManagementCard(
                        appointment = appointment,
                        showActions = showActions,
                        onAcceptClick = {
                            if (appointment.documentId.isNotBlank()) {
                                db.collection("appointments").document(appointment.documentId)
                                    .update(mapOf("status" to "ACCEPTED", "paymentStatus" to "UNPAID"))
                            }
                        },
                        onRejectClick = {
                            if (appointment.documentId.isNotBlank()) {
                                db.collection("appointments").document(appointment.documentId)
                                    .update(mapOf("status" to "REJECTED", "paymentStatus" to "UNPAID"))
                            }
                        },
                        onSuggestClick = { suggestedDate, suggestedTime ->
                            if (appointment.documentId.isNotBlank()) {
                                db.collection("appointments").document(appointment.documentId)
                                    .update(mapOf(
                                        "status" to "SUGGESTED",
                                        "date" to suggestedDate,
                                        "time" to suggestedTime,
                                        "suggestedDate" to suggestedDate,
                                        "suggestedTime" to suggestedTime,
                                        "paymentStatus" to "UNPAID"
                                    ))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppointmentManagementCard(
    appointment: Appointment,
    showActions: Boolean,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
    onSuggestClick: (String, String) -> Unit
) {
    var showSuggestFields by remember { mutableStateOf(false) }
    var suggestedDate by remember { mutableStateOf("") }
    var suggestedTime by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val errFieldsRequired = stringResource(R.string.err_suggest_fields_required)

    val statusText = when (appointment.status.uppercase()) {
        "PENDING" -> stringResource(R.string.status_pending)
        "ACCEPTED" -> stringResource(R.string.status_accepted)
        "REJECTED" -> stringResource(R.string.status_rejected)
        "SUGGESTED" -> stringResource(R.string.status_suggested)
        "WAITING" -> stringResource(R.string.status_waiting)
        "CONFIRMED" -> stringResource(R.string.status_confirmed)
        "CANCELLED" -> stringResource(R.string.status_cancelled)
        else -> appointment.status
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(appointment.patientName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.date_label) + ": ${appointment.date}")
            Text(stringResource(R.string.time_label) + ": ${appointment.time}")
            Text(stringResource(R.string.status_label) + ": $statusText")

            if (showActions && (appointment.status == "PENDING" || appointment.status == "WAITING")) {
                Spacer(Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAcceptClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text(stringResource(R.string.accept))
                    }
                    OutlinedButton(onClick = onRejectClick, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Text(stringResource(R.string.reject))
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showSuggestFields = !showSuggestFields; errorMessage = "" },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.suggest_another_time))
                }

                if (showSuggestFields) {
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = suggestedDate, onValueChange = { suggestedDate = it; errorMessage = "" },
                        label = { Text(stringResource(R.string.suggested_date)) }, placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = suggestedTime, onValueChange = { suggestedTime = it; errorMessage = "" },
                        label = { Text(stringResource(R.string.suggested_time)) }, placeholder = { Text("10:00") },
                        modifier = Modifier.fillMaxWidth(), singleLine = true
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (suggestedDate.isBlank() || suggestedTime.isBlank()) {
                                errorMessage = errFieldsRequired
                            } else {
                                onSuggestClick(suggestedDate, suggestedTime)
                                showSuggestFields = false; errorMessage = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.send_suggestion))
                    }
                }
            }
        }
    }
}