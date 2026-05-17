package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.domain.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DoctorAppointmentsScreen(
    appointments: List<Appointment>,
    showActions: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF7FF))
            .padding(16.dp)
    ) {

        Text(
            text = "Appointments",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (appointments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text("No appointments found")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(appointments) { appointment ->

                    AppointmentManagementCard(
                        appointment = appointment,
                        showActions = showActions,
                        onAcceptClick = {
                            if (appointment.documentId.isNotBlank()) {
                                db.collection("appointments")
                                    .document(appointment.documentId)
                                    .update(
                                        mapOf(
                                            "status" to "ACCEPTED",
                                            "paymentStatus" to "UNPAID"
                                        )
                                    )
                            }
                        },
                        onRejectClick = {
                            if (appointment.documentId.isNotBlank()) {
                                db.collection("appointments")
                                    .document(appointment.documentId)
                                    .update(
                                        mapOf(
                                            "status" to "REJECTED",
                                            "paymentStatus" to "UNPAID"
                                        )
                                    )
                            }
                        },
                        onSuggestClick = { suggestedDate, suggestedTime ->
                            if (appointment.documentId.isNotBlank()) {
                                db.collection("appointments")
                                    .document(appointment.documentId)
                                    .update(
                                        mapOf(
                                            "status" to "SUGGESTED",
                                            "date" to suggestedDate,
                                            "time" to suggestedTime,
                                            "suggestedDate" to suggestedDate,
                                            "suggestedTime" to suggestedTime,
                                            "paymentStatus" to "UNPAID"
                                        )
                                    )
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = appointment.patientName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Date: ${appointment.date}")
            Text(text = "Time: ${appointment.time}")
            Text(text = "Status: ${appointment.status}")

            if (showActions && appointment.status == "PENDING") {

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAcceptClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Accept")
                    }

                    OutlinedButton(
                        onClick = onRejectClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reject")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        showSuggestFields = !showSuggestFields
                        errorMessage = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Suggest another time")
                }

                if (showSuggestFields) {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = suggestedDate,
                        onValueChange = {
                            suggestedDate = it
                            errorMessage = ""
                        },
                        label = { Text("Suggested Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = suggestedTime,
                        onValueChange = {
                            suggestedTime = it
                            errorMessage = ""
                        },
                        label = { Text("Suggested Time") },
                        placeholder = { Text("10:00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.
                     height(8.dp))

                    Button(
                        onClick = {
                            if (suggestedDate.isBlank() || suggestedTime.isBlank()) {
                                errorMessage = "Please enter suggested date and time"
                            } else {
                                onSuggestClick(suggestedDate, suggestedTime)
                                showSuggestFields = false
                                errorMessage = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Send Suggestion")
                    }
                }
            }
        }
    }
}