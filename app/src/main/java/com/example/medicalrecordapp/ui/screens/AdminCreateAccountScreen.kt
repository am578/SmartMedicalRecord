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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.medicalrecordapp.R
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

    val errName = stringResource(R.string.err_name_required)
    val errCin = stringResource(R.string.err_cin_required)
    val errPhone = stringResource(R.string.err_phone_required)
    val errAge = stringResource(R.string.err_age_invalid)
    val errEmail = stringResource(R.string.err_email_required)
    val errPass = stringResource(R.string.err_password_required)
    val errSpec = stringResource(R.string.err_speciality_required)
    val errOffice = stringResource(R.string.err_office_required)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.create_staff_account), fontWeight = FontWeight.Bold) },
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
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // === اختيار الدور ===
            Text(stringResource(R.string.account_role), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = selectedRole == UserRole.DOCTOR,
                    onClick = { selectedRole = UserRole.DOCTOR },
                    label = { Text(stringResource(R.string.doctor)) }
                )
                FilterChip(
                    selected = selectedRole == UserRole.RECEPTIONIST,
                    onClick = { selectedRole = UserRole.RECEPTIONIST },
                    label = { Text(stringResource(R.string.receptionist)) }
                )
            }

            Spacer(Modifier.height(20.dp))

            // === المعلومات الشخصية ===
            Text(stringResource(R.string.personal_info), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(R.string.first_name)) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(stringResource(R.string.last_name)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text(stringResource(R.string.age)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            Text(stringResource(R.string.gender), fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilterChip(
                    selected = selectedGender == "male",
                    onClick = { selectedGender = "male" },
                    label = { Text(stringResource(R.string.male)) }
                )
                FilterChip(
                    selected = selectedGender == "female",
                    onClick = { selectedGender = "female" },
                    label = { Text(stringResource(R.string.female)) }
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = cin,
                onValueChange = { cin = it },
                label = { Text(stringResource(R.string.cin_id)) },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text(stringResource(R.string.cin_supporting_text)) }
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.phone)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // === حقل خاص بالدور ===
            if (selectedRole == UserRole.DOCTOR) {
                Text(stringResource(R.string.professional_info), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = speciality,
                    onValueChange = { speciality = it },
                    label = { Text(stringResource(R.string.speciality)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
            } else {
                Text(stringResource(R.string.office_info), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = officeNumber,
                    onValueChange = { officeNumber = it },
                    label = { Text(stringResource(R.string.office_number)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(20.dp))
            }

            // === بيانات الحساب ===
            Text(stringResource(R.string.account_credentials), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { password = generateRandomPassword() }) {
                        Text(stringResource(R.string.generate))
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
                        firstName.isBlank() || lastName.isBlank() -> errorMessage = errName
                        cin.isBlank() -> errorMessage = errCin
                        phone.isBlank() -> errorMessage = errPhone
                        ageInt == 0 -> errorMessage = errAge
                        email.isBlank() -> errorMessage = errEmail
                        password.isBlank() -> errorMessage = errPass
                        selectedRole == UserRole.DOCTOR && speciality.isBlank() -> errorMessage = errSpec
                        selectedRole == UserRole.RECEPTIONIST && officeNumber.isBlank() -> errorMessage = errOffice
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
                Text(if (isLoading) stringResource(R.string.creating) else stringResource(R.string.create_account))
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
                    Text(stringResource(R.string.account_created), style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, color = Color(0xFF34A853))
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(R.string.share_credentials), color = Color.Gray)
                    Spacer(Modifier.height(12.dp))
                    CredentialRow(stringResource(R.string.name), createdName)
                    CredentialRow(stringResource(R.string.role), createdRole)
                    CredentialRow(stringResource(R.string.email), createdEmail)
                    CredentialRow(stringResource(R.string.password), createdPassword)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = { showCredentialsDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.done))
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