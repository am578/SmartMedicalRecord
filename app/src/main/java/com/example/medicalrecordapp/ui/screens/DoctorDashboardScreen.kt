package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.R
import com.example.medicalrecordapp.ui.components.LanguageSwitcherButton
import com.example.medicalrecordapp.utils.LocalLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(
    onPatientsClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {},
    onLanguageChange: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val currentLang = LocalLanguage.current.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.doctor_dashboard), fontWeight = FontWeight.Bold) },
                actions = {
                    LanguageSwitcherButton(currentLang = currentLang, onLanguageChange = onLanguageChange)
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                .padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DashboardActionCard(
                stringResource(R.string.my_patients),
                stringResource(R.string.my_patients_desc),
                Icons.Default.People,
                Color(0xFF2D7FF9),
                onPatientsClick
            )
            DashboardActionCard(
                stringResource(R.string.appointments),
                stringResource(R.string.appointments_desc),
                Icons.Default.CalendarToday,
                Color(0xFF34A853),
                onAppointmentsClick
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onLogoutClick, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.logout))
            }
        }
    }
}

@Composable
private fun DashboardActionCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(14.dp), modifier = Modifier.size(52.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}