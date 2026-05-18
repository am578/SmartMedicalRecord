package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.R

@Composable
fun ReceptionDashboardScreen(
    onRegisterPatientClick: () -> Unit,
    onPatientsClick: () -> Unit,
    onAppointmentsClick: () -> Unit,
    onRequestsClick: () -> Unit,
    onLanguageChange: (String) -> Unit = {},
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {
        DashboardHeader(
            title = stringResource(id = R.string.reception),
            subtitle = "Welcome Receptionist",
            onLanguageChange = onLanguageChange
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SmallStatCard(
                    icon = "👤",
                    title = "Today: 12",
                    subtitle = stringResource(id = R.string.register_patient),
                    iconColor = PrimaryBlue
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                SmallStatCard(
                    icon = "⏰",
                    title = "Pending: 5",
                    subtitle = stringResource(id = R.string.appointment_requests),
                    iconColor = PrimaryGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        MainDashboardCard(
            icon = "👤",
            title = stringResource(id = R.string.register_patient),
            description = stringResource(id = R.string.add_patient_desc),
            buttonText = stringResource(id = R.string.register_patient),
            onClick = onRegisterPatientClick,
            iconColor = PrimaryBlue,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        MainDashboardCard(
            icon = "👥",
            title = stringResource(id = R.string.patients_list),
            description = stringResource(id = R.string.browse_patients_desc),
            buttonText = stringResource(id = R.string.patients_list),
            onClick = onPatientsClick,
            iconColor = PrimaryGreen,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        MainDashboardCard(
            icon = "📅",
            title = stringResource(id = R.string.appointments),
            description = stringResource(id = R.string.view_scheduled_desc),
            buttonText = stringResource(id = R.string.appointments),
            onClick = onAppointmentsClick,
            iconColor = PrimaryBlue,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        MainDashboardCard(
            icon = "🔔",
            title = stringResource(id = R.string.appointment_requests),
            description = stringResource(id = R.string.manage_requests_desc),
            buttonText = stringResource(id = R.string.appointment_requests),
            onClick = onRequestsClick,
            iconColor = PrimaryGreen,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(22.dp))

        LogoutButton(onLogoutClick = onLogoutClick)
    }
}