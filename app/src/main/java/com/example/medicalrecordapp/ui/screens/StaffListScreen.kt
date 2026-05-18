package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.example.medicalrecordapp.R
import com.example.medicalrecordapp.domain.model.User
import com.example.medicalrecordapp.domain.model.UserRole
import com.example.medicalrecordapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffListScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    val staffList by authViewModel.staffList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }

    LaunchedEffect(Unit) { authViewModel.fetchStaffUsers() }

    val filteredList = staffList.filter { user ->
        val matchesSearch = searchQuery.isBlank() ||
                user.fullName.contains(searchQuery, ignoreCase = true) ||
                user.cin.contains(searchQuery, ignoreCase = true) ||
                user.speciality.contains(searchQuery, ignoreCase = true)
        val matchesRole = selectedRoleFilter == null || user.role == selectedRoleFilter
        matchesSearch && matchesRole
    }
    BackHandler { onBackClick() }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.staff_list), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(stringResource(R.string.search_staff_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = selectedRoleFilter == null, onClick = { selectedRoleFilter = null }, label = { Text(stringResource(R.string.all)) })
                FilterChip(selected = selectedRoleFilter == UserRole.DOCTOR, onClick = { selectedRoleFilter = UserRole.DOCTOR }, label = { Text(stringResource(R.string.doctors)) })
                FilterChip(selected = selectedRoleFilter == UserRole.RECEPTIONIST, onClick = { selectedRoleFilter = UserRole.RECEPTIONIST }, label = { Text(stringResource(R.string.receptionists)) })
            }

            Spacer(Modifier.height(8.dp))
            Text("${filteredList.size} " + stringResource(R.string.members), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))

            if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_staff_found), color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filteredList) { user ->
                        StaffCard(
                            user = user,
                            onDelete = {
                                authViewModel.deleteStaffUser(user.id) { success, _ ->
                                    if (success) authViewModel.fetchStaffUsers()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffCard(user: User, onDelete: () -> Unit) {
    val isDoctor = user.role == UserRole.DOCTOR
    val roleColor = if (isDoctor) Color(0xFF2D7FF9) else Color(0xFFFF9800)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Surface(
                color = roleColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = if (isDoctor) Icons.Default.MedicalServices else Icons.Default.Person,
                        contentDescription = null,
                        tint = roleColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName.ifBlank { "${user.firstName} ${user.lastName}" },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Surface(
                    color = roleColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (isDoctor) stringResource(R.string.doctor) else stringResource(R.string.receptionist),
                        color = roleColor,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                StaffInfoRow(stringResource(R.string.cin_id_label), user.cin)
                StaffInfoRow(stringResource(R.string.age_label), if (user.age > 0) "${user.age} " + stringResource(R.string.years) else "-")
                StaffInfoRow(stringResource(R.string.gender_label), if (user.gender.lowercase() == "male") stringResource(R.string.male) else stringResource(R.string.female))
                StaffInfoRow(stringResource(R.string.phone_label), user.phone)
                StaffInfoRow(stringResource(R.string.email_label), user.email)
                if (isDoctor && user.speciality.isNotBlank()) StaffInfoRow(stringResource(R.string.speciality_label), user.speciality)
                if (!isDoctor && user.officeNumber.isNotBlank()) StaffInfoRow(stringResource(R.string.office_label), user.officeNumber)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = Color.Red)
            }
        }
    }
}

@Composable
private fun StaffInfoRow(label: String, value: String) {
    if (value.isBlank() || value == "-") return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
        Text("$label: ", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}