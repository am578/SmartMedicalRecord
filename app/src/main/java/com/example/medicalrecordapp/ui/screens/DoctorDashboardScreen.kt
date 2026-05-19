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
fun DoctorDashboardScreen(
    onPatientsClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onLanguageChange: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {
        DashboardHeader(
            title = stringResource(id = R.string.doctor_dashboard),
            subtitle = "Welcome Doctor",
            onLanguageChange = onLanguageChange
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SmallStatCard(
                    icon = "👥",
                    title = "Today: 8",
                    subtitle = stringResource(id = R.string.my_patients),
                    iconColor = PrimaryBlue
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                SmallStatCard(
                    icon = "📋",
                    title = "Pending: 3",
                    subtitle = stringResource(id = R.string.appointments),
                    iconColor = PrimaryGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        MainDashboardCard(
            icon = "👥",
            title = stringResource(id = R.string.my_patients),
            description = stringResource(id = R.string.my_patients_desc),
            buttonText = stringResource(id = R.string.patients_list),
            onClick = onPatientsClick,
            iconColor = PrimaryBlue,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        MainDashboardCard(
            icon = "📅",
            title = stringResource(id = R.string.appointments),
            description = stringResource(id = R.string.appointments_desc),
            buttonText = "View all your scheduled appointments",
            onClick = onAppointmentsClick,
            iconColor = PrimaryGreen,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(22.dp))

        LogoutButton(onLogoutClick = onLogoutClick)
    }
}