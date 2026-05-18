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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.R
import com.example.medicalrecordapp.utils.LanguageManager
import com.example.medicalrecordapp.utils.LocalLanguage
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

    val currentLang = LocalLanguage.current.value
    val isAr = currentLang == LanguageManager.LANG_AR

    val genderOptions = if (isAr) listOf("ذكر", "أنثى") else listOf("Male", "Female")
    var genderExpanded by remember { mutableStateOf(false) }

    val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var bloodGroupExpanded by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Error strings
    val errFirst = stringResource(R.string.err_first_name)
    val errFamily = stringResource(R.string.err_family_name)
    val errCin = stringResource(R.string.err_cin)
    val errPhone = stringResource(R.string.err_phone)
    val errDob = stringResource(R.string.err_dob)
    val errGender = stringResource(R.string.err_gender)
    val errAddress = stringResource(R.string.err_address)
    val errBlood = stringResource(R.string.err_blood_group)
    val errChronic = stringResource(R.string.err_chronic)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.register_patient),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                OutlinedTextField(
                    value = firstName, onValueChange = { firstName = it },
                    label = { Text(stringResource(R.string.first_name)) }, 
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = familyName, onValueChange = { familyName = it },
                    label = { Text(stringResource(R.string.family_name)) }, 
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cin, onValueChange = { cin = it },
                    label = { Text(stringResource(R.string.cin_id)) }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '+' }) phone = it },
                    label = { Text(stringResource(R.string.phone)) }, modifier = Modifier.fillMaxWidth(),
                    singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dateOfBirth, onValueChange = { dateOfBirth = it },
                    label = { Text(stringResource(R.string.dob)) }, 
                    placeholder = { Text(stringResource(R.string.dob_hint)) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(expanded = genderExpanded, onExpandedChange = { genderExpanded = !genderExpanded }) {
                    OutlinedTextField(
                        value = gender, onValueChange = {}, readOnly = true,
                        label = { Text(stringResource(R.string.gender)) },
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
                    label = { Text(stringResource(R.string.address)) }, 
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(expanded = bloodGroupExpanded, onExpandedChange = { bloodGroupExpanded = !bloodGroupExpanded }) {
                    OutlinedTextField(
                        value = bloodGroup, onValueChange = {}, readOnly = true,
                        label = { Text(stringResource(R.string.blood_group)) },
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
                    label = { Text(stringResource(R.string.chronic_diseases)) },
                    placeholder = { Text(stringResource(R.string.chronic_diseases_hint)) },
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
                            firstName.isBlank() -> formError = errFirst
                            familyName.isBlank() -> formError = errFamily
                            cin.isBlank() -> formError = errCin
                            phone.isBlank() -> formError = errPhone
                            dateOfBirth.isBlank() -> formError = errDob
                            gender.isBlank() -> formError = errGender
                            address.isBlank() -> formError = errAddress
                            bloodGroup.isBlank() -> formError = errBlood
                            chronicDiseases.isBlank() -> formError = errChronic
                            else -> {
                                formError = ""
                                isLoading = true
                                val patient = hashMapOf(
                                    "firstName" to firstName, "familyName" to familyName,
                                    "cin" to cin, "phone" to phone, "dateOfBirth" to dateOfBirth,
                                    "gender" to (if (isAr) (if (gender == "ذكر") "Male" else "Female") else gender),
                                    "address" to address,
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    else Text(stringResource(R.string.save_patient_btn))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(stringResource(R.string.back))
        }
    }
}