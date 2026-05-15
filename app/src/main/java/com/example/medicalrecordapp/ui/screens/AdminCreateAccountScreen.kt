package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@Composable
fun AdminCreateAccountScreen(
    authViewModel: AuthViewModel,
    adminEmail: String,
    adminPassword: String,
    onBackClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.DOCTOR) }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // Dialog يعرض بيانات الحساب الجديد
    var showCredentialsDialog by remember { mutableStateOf(false) }
    var createdEmail by remember { mutableStateOf("") }
    var createdPassword by remember { mutableStateOf("") }
    var createdRole by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Create Staff Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create a doctor or receptionist account. Password will be auto-generated.",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // حقل كلمة السر مع زر توليد تلقائي
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (or auto-generate)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                TextButton(onClick = { password = generateRandomPassword() }) {
                    Text("Generate")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Account Role", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "Please fill all fields"
                    return@Button
                }

                isLoading = true
                successMessage = ""
                errorMessage = ""

                authViewModel.createStaffAccount(
                    adminEmail = adminEmail,
                    adminPassword = adminPassword,
                    staffEmail = email,
                    staffPassword = password,
                    fullName = fullName,
                    role = selectedRole
                ) { success, message, uid ->
                    isLoading = false
                    if (success) {
                        successMessage = "Account created successfully!"
                        createdEmail = email
                        createdPassword = password
                        createdRole = selectedRole.name
                        showCredentialsDialog = true

                        // نفضي الحقول
                        fullName = ""
                        email = ""
                        password = ""
                        selectedRole = UserRole.DOCTOR
                    } else {
                        errorMessage = message ?: "Failed to create account"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF34A853))
        ) {
            Text(if (isLoading) "Creating..." else "Create Account")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }

    // Dialog يعرض البيانات للأدمن عشان يعطيها للموظف
    if (showCredentialsDialog) {
        Dialog(onDismissRequest = { showCredentialsDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Account Created!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34A853)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Share these credentials with the new staff member:",
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CredentialRow(label = "Role", value = createdRole)
                    CredentialRow(label = "Email", value = createdEmail)
                    CredentialRow(label = "Password", value = createdPassword)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showCredentialsDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private fun generateRandomPassword(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%"
    return (1..10).map { chars[Random.nextInt(chars.length)] }.joinToString("")
}
