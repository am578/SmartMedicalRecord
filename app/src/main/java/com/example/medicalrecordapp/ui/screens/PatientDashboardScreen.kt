package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PatientDashboardScreen(
    onRequestAppointmentClick: () -> Unit = {},
    onMyAppointmentsClick: () -> Unit = {},
    onMyRecordClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF7FF))
            .padding(20.dp)
    ) {
        Text(
            text = "Patient Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome Patient",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        PatientCard(
            title = "Request Appointment",
            description = "Send a new appointment request",
            buttonText = "Request Appointment",
            onClick = onRequestAppointmentClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        PatientCard(
            title = "My Appointments",
            description = "Check your appointments",
            buttonText = "View My Appointments",
            onClick = onMyAppointmentsClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        PatientCard(
            title = "My Medical Record",
            description = "View your medical record",
            buttonText = "View My Record",
            onClick = onMyRecordClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D7FF9)
            )
        ) {
            Text("Logout")
        }
    }
}

@Composable
private fun PatientCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D7FF9)
                )
            ) {
                Text(buttonText)
            }
        }
    }
}