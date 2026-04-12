package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.R

@Composable
fun DoctorDashboardScreen(
    onPatientsClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF7FF)) // baby blue
            .padding(20.dp)
    ) {

        // 🔥 Header فيه icon جديد
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_ai),
                contentDescription = "AI Icon",
                modifier = Modifier.size(150.dp)
            )

            // ❌ نحيدو Spacer نهائي
            // Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier.padding(start = 2.dp) // 👈 هذا هو السر
            ) {
                Text(
                    text = "Doctor Dashboard",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Welcome Doctor",
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🟦 Patients Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPatientsClick() },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Patients",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "View and manage patients",
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onPatientsClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D7FF9)
                    )
                ) {
                    Text("View Patients")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🟦 Appointments Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAppointmentsClick() },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Appointments",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Check your appointments",
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAppointmentsClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D7FF9)
                    )
                ) {
                    Text("View Appointments")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔵 Logout
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
    }}
