package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ReceptionDashboardScreen(
    onRegisterPatientClick: () -> Unit = {},
    onPatientsClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onRequestsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF7FF))
            .padding(20.dp)
    ) {
        Text(
            text = "Reception Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Welcome Receptionist",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        ReceptionCard(
            title = "Register Patient",
            description = "Add a new patient to the system",
            buttonText = "Register Patient",
            onClick = onRegisterPatientClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ReceptionCard(
            title = "Patients List",
            description = "View registered patients",
            buttonText = "View Patients",
            onClick = onPatientsClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ReceptionCard(
            title = "Appointments",
            description = "Manage patient appointments",
            buttonText = "Manage Appointments",
            onClick = onAppointmentsClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        ReceptionCard(
            title = "Appointment Requests",
            description = "Accept, reject, or suggest another time",
            buttonText = "View Requests",
            onClick = onRequestsClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D7FF9)
            )
        ) {
            Text("Logout")
        }
    }
}

@Composable
private fun ReceptionCard(
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
        ),
        elevation = CardDefaults.cardElevation(6.dp)
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
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2D7FF9)
                )
            ) {
                Text(buttonText)
            }
        }
    }
}