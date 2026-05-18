package com.example.medicalrecordapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPatientScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: (
        firstName: String,
        familyName: String,
        cin: String,
        phone: String,
        dateOfBirth: String,
        gender: String,
        address: String,
        bloodGroup: String,
        chronicDiseases: String
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> }
) {
    BackHandler { onBackClick() }

    var firstName by remember { mutableStateOf("") }
    var familyName by remember { mutableStateOf("") }
    var cin by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var chronicDiseases by remember { mutableStateOf("") }

    var formError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val genderOptions = listOf("Male", "Female")
    var genderExpanded by remember { mutableStateOf(false) }

    val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var bloodGroupExpanded by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF7FF))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Register Patient",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                OutlinedTextField(
                    value = firstName, onValueChange = { firstName = it },
                    label = { Text("First Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = familyName, onValueChange = { familyName = it },
                    label = { Text("Family Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cin, onValueChange = { cin = it },
                    label = { Text("CIN / ID Card Number") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '+' }) phone = it },
                    label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dateOfBirth, onValueChange = { dateOfBirth = it },
                    label = { Text("Date of Birth") }, placeholder = { Text("DD/MM/YYYY") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(expanded = genderExpanded, onExpandedChange = { genderExpanded = !genderExpanded }) {
                    OutlinedTextField(
                        value = gender, onValueChange = {}, readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = genderExpanded, onDismissRequest = { genderExpanded = false }) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { gender = option; genderExpanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = address, onValueChange = { address = it },
                    label = { Text("Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(expanded = bloodGroupExpanded, onExpandedChange = { bloodGroupExpanded = !bloodGroupExpanded }) {
                    OutlinedTextField(
                        value = bloodGroup, onValueChange = {}, readOnly = true,
                        label = { Text("Blood Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = bloodGroupExpanded, onDismissRequest = { bloodGroupExpanded = false }) {
                        bloodGroupOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { bloodGroup = option; bloodGroupExpanded = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = chronicDiseases, onValueChange = { chronicDiseases = it },
                    label = { Text("Chronic Diseases") },
                    placeholder = { Text("None / Diabetes / Asthma...") },
                    modifier = Modifier.fillMaxWidth(), minLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (formError.isNotEmpty()) {
                    Text(text = formError, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        when {
                            firstName.isBlank() -> formError = "Please enter first name"
                            familyName.isBlank() -> formError = "Please enter family name"
                            cin.isBlank() -> formError = "Please enter CIN"
                            phone.isBlank() -> formError = "Please enter phone number"
                            phone.length < 9 -> formError = "Please enter a valid phone number"
                            dateOfBirth.isBlank() -> formError = "Please enter date of birth"
                            gender.isBlank() -> formError = "Please select gender"
                            address.isBlank() -> formError = "Please enter address"
                            bloodGroup.isBlank() -> formError = "Please select blood group"
                            chronicDiseases.isBlank() -> formError = "Please enter chronic diseases or write None"
                            else -> {
                                formError = ""
                                isLoading = true
                                val patient = hashMapOf(
                                    "firstName" to firstName, "familyName" to familyName,
                                    "cin" to cin, "phone" to phone, "dateOfBirth" to dateOfBirth,
                                    "gender" to gender, "address" to address,
                                    "bloodGroup" to bloodGroup, "chronicDiseases" to chronicDiseases,
                                    "createdBy" to (auth.currentUser?.uid ?: ""),
                                    "createdAt" to System.currentTimeMillis()
                                )
                                db.collection("patients").add(patient)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        onSaveClick(firstName, familyName, cin, phone, dateOfBirth, gender, address, bloodGroup, chronicDiseases)
                                    }
                                    .addOnFailureListener { e -> isLoading = false; formError = e.message ?: "Failed to save patient" }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7FF9)),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    else Text("Save Patient")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D7FF9))
        ) {
            Text("Back")
        }
    }
}