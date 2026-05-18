package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.R

@Composable
fun AdminDashboardScreen(
    onManageUsersClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onLanguageChange: (String) -> Unit,
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
            title = stringResource(id = R.string.admin_dashboard),
            subtitle = stringResource(id = R.string.welcome_admin),
            onLanguageChange = onLanguageChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SmallStatCard(
                    icon = "👥",
                    title = stringResource(id = R.string.users),
                    subtitle = "24",
                    iconColor = PrimaryGreen
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                SmallStatCard(
                    icon = "📋",
                    title = stringResource(id = R.string.reports),
                    subtitle = "08",
                    iconColor = PrimaryGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        MainDashboardCard(
            icon = "👥",
            title = stringResource(id = R.string.users_management),
            description = stringResource(id = R.string.users_management_desc),
            buttonText = stringResource(id = R.string.view_staff_list),
            onClick = onManageUsersClick,
            iconColor = PrimaryGreen,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(12.dp))

        MainDashboardCard(
            icon = "➕",
            title = stringResource(id = R.string.create_account),
            description = stringResource(id = R.string.create_account_desc_admin),
            buttonText = stringResource(id = R.string.create_staff_btn),
            onClick = onCreateAccountClick,
            iconColor = PrimaryGreen,
            buttonColor = PrimaryGreen
        )

        Spacer(modifier = Modifier.height(18.dp))

        MainDashboardCard(
            icon = "📊",
            title = stringResource(id = R.string.system_statistics),
            description = stringResource(id = R.string.system_statistics_desc),
            buttonText = stringResource(id = R.string.view_statistics),
            onClick = onStatisticsClick,
            iconColor = PrimaryGreen,
            buttonColor = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { 0.78f },
            modifier = Modifier.fillMaxWidth(),
            color = PrimaryGreen,
            trackColor = PrimaryGreen.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.height(22.dp))

        LogoutButton(onLogoutClick = onLogoutClick)
    }
}
