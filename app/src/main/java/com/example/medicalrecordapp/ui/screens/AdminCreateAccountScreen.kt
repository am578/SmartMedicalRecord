package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.medicalrecordapp.domain.model.UserRole
import com.example.medicalrecordapp.viewmodel.AuthViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCreateAccountScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("male") }
    var cin by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.DOCTOR) }
    var speciality by remember { mutableStateOf("") }
    var officeNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showCredentialsDialog by remember { mutableStateOf(false) }
    var createdEmail by remember { mutableStateOf("") }
    var createdPassword by remember { mutableStateOf("") }
    var createdRole by remember { mutableStateOf("") }
    var createdName by remember { mutableStateOf("") }

    BackHandler { onBackClick() }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Staff Account", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // === اختيار الدور ===
            Text("Account Role", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = selectedRole == UserRole.DOCTOR,
                    onClick = { selectedRole = UserRole.DOCTOR },
                    label = { Text("Doctor") }
                )
                FilterChip(
                    selected = selectedRole == UserRole.RECEPTIONIST,
                    onClick = { selectedRole = UserRole.RECEPTIONIST },
                    label = { Text("Receptionist") }
                )
            }

            Spacer(Modifier.height(20.dp))

            // === المعلومات الشخصية ===
            Text("Personal Information", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Text("Gender", fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = selectedGender == "male",
                    onClick = { selectedGender = "male" },
                    label = { Text("Male") }
                )
                FilterChip(
                    selected = selectedGender == "female",
                    onClick = { selectedGender = "female" },
                    label = { Text("Female") }
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = cin,
                onValueChange = { cin = it },
                label = { Text("CIN (Identity Card Number)") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Unique identifier for each user") }
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // === حقل خاص بالدور ===
            if (selectedRole == UserRole.DOCTOR) {
                Text("Professional Information", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = speciality,
                    onValueChange = { speciality = it },
                    label = { Text("Speciality") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
            } else {
                Text("Office Information", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = officeNumber,
                    onValueChange = { officeNumber = it },
                    label = { Text("Office Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
            }

            // === بيانات الحساب ===
            Text("Account Credentials", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { password = generateRandomPassword() }) {
                        Text("Generate")
                    }
                }
            )

            Spacer(Modifier.height(20.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    val ageInt = age.toIntOrNull() ?: 0
                    when {
                        firstName.isBlank() || lastName.isBlank() -> errorMessage = "Please enter first and last name"
                        cin.isBlank() -> errorMessage = "CIN is required"
                        phone.isBlank() -> errorMessage = "Phone number is required"
                        ageInt == 0 -> errorMessage = "Please enter a valid age"
                        email.isBlank() -> errorMessage = "Email is required"
                        password.isBlank() -> errorMessage = "Password is required"
                        selectedRole == UserRole.DOCTOR && speciality.isBlank() -> errorMessage = "Please enter speciality"
                        selectedRole == UserRole.RECEPTIONIST && officeNumber.isBlank() -> errorMessage = "Please enter office number"
                        else -> {
                            isLoading = true
                            errorMessage = ""
                            authViewModel.createStaffAccount(
                                staffEmail = email, staffPassword = password,
                                firstName = firstName, lastName = lastName,
                                age = ageInt, gender = selectedGender,
                                cin = cin, phone = phone,
                                role = selectedRole,
                                speciality = speciality,
                                officeNumber = officeNumber
                            ) { success, message, _ ->
                                isLoading = false
                                if (success) {
                                    createdEmail = email; createdPassword = password
                                    createdRole = selectedRole.name; createdName = "$firstName $lastName"
                                    showCredentialsDialog = true
                                    firstName = ""; lastName = ""; age = ""
                                    cin = ""; phone = ""; email = ""; password = ""
                                    speciality = ""; officeNumber = ""
                                    selectedRole = UserRole.DOCTOR; selectedGender = "male"
                                } else {
                                    errorMessage = message ?: "Failed to create account"
                                }
                            }
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853))
            ) {
                Text(if (isLoading) "Creating..." else "Create Account")
            }
        }
    }

    if (showCredentialsDialog) {
        Dialog(onDismissRequest = { showCredentialsDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✅ Account Created!", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, color = Color(0xFF34A853))
                    Spacer(Modifier.height(16.dp))
                    Text("Share these credentials with the new staff member:", color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    CredentialRow("Name", createdName)
                    CredentialRow("Role", createdRole)
                    CredentialRow("Email", createdEmail)
                    CredentialRow("Password", createdPassword)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { showCredentialsDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun CredentialRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
    }
}

private fun generateRandomPassword(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%"
    return (1..10).map { chars[Random.nextInt(chars.length)] }.joinToString("")
}