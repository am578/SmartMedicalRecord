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
fun PatientDashboardScreen(
    onRequestAppointmentClick: () -> Unit,
    onMyAppointmentsClick: () -> Unit,
    onMyRecordClick: () -> Unit,
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
            title = stringResource(id = R.string.my_health),
            subtitle = "Welcome Patient",
            onLanguageChange = onLanguageChange
        )

        Spacer(modifier = Modifier.height(24.dp))

        MainDashboardCard(
            icon = "➕",
            title = stringResource(id = R.string.request_appointment),
            description = stringResource(id = R.string.book_appointment_desc),
            buttonText = stringResource(id = R.string.request_appointment),
            onClick = onRequestAppointmentClick,
            iconColor = PrimaryBlue,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        MainDashboardCard(
            icon = "📅",
            title = stringResource(id = R.string.my_appointments),
            description = stringResource(id = R.string.view_appointments_desc),
            buttonText = stringResource(id = R.string.my_appointments),
            onClick = onMyAppointmentsClick,
            iconColor = PrimaryGreen,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        MainDashboardCard(
            icon = "↺",
            title = stringResource(id = R.string.my_medical_record),
            description = stringResource(id = R.string.access_history_desc),
            buttonText = stringResource(id = R.string.my_medical_record),
            onClick = onMyRecordClick,
            iconColor = PrimaryBlue,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(22.dp))

        LogoutButton(onLogoutClick = onLogoutClick)
    }
}