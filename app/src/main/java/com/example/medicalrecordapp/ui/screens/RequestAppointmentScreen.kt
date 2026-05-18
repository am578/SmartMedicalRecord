package com.example.medicalrecordapp.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.medicalrecordapp.R
import com.example.medicalrecordapp.domain.model.AttachmentType
import com.example.medicalrecordapp.utils.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestAppointmentScreen(
    onBackClick: () -> Unit = {},
    onSubmitClick: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var doctorName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AttachmentType.NONE) }

    var formError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedUri = uri
            selectedType = AttachmentType.IMAGE
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) selectedFileName = it.getString(idx)
                }
            }
            if (selectedFileName.isBlank()) selectedFileName = "image_${System.currentTimeMillis()}.jpg"
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedUri = uri
            selectedType = AttachmentType.FILE
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val idx = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) selectedFileName = it.getString(idx)
                }
            }
            if (selectedFileName.isBlank()) selectedFileName = "file_${System.currentTimeMillis()}"
        }
    }

    BackHandler { onBackClick() }

    // Error message strings
    val errDoctor = stringResource(R.string.please_enter_doctor_name)
    val errDate = stringResource(R.string.please_enter_date)
    val errTime = stringResource(R.string.please_enter_time)
    val errSymptoms = stringResource(R.string.please_enter_symptoms)
    val errNotLoggedIn = stringResource(R.string.user_not_logged_in)
    val errReadFail = stringResource(R.string.cannot_read_file)
    val errUploadFail = stringResource(R.string.upload_failed)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.request_appointment), fontWeight = FontWeight.Bold) },
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    AppointmentInputField(
                        value = doctorName,
                        onValueChange = { doctorName = it },
                        label = stringResource(R.string.doctor_name),
                        icon = Icons.Default.Person
                    )
                    Spacer(Modifier.height(16.dp))

                    AppointmentInputField(
                        value = date,
                        onValueChange = { date = it },
                        label = stringResource(R.string.preferred_date),
                        icon = Icons.Default.CalendarMonth
                    )
                    Spacer(Modifier.height(16.dp))

                    AppointmentInputField(
                        value = time,
                        onValueChange = { time = it },
                        label = stringResource(R.string.preferred_time),
                        icon = Icons.Default.Schedule
                    )
                    Spacer(Modifier.height(16.dp))

                    // ====== الأعراض ======
                    OutlinedTextField(
                        value = symptoms,
                        onValueChange = { symptoms = it },
                        label = { Text(stringResource(R.string.symptoms_notes)) },
                        placeholder = { Text(stringResource(R.string.how_do_you_feel)) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // ====== رفع صورة أو ملف ======
                    Text(stringResource(R.string.attach_file), fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { imageLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.image))
                        }
                        OutlinedButton(
                            onClick = { fileLauncher.launch("*/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = null,
                                modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.file))
                        }
                    }

                    // ====== معاينة المرفق ======
                    if (selectedUri != null) {
                        Spacer(Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (selectedType == AttachmentType.IMAGE) {
                                    AsyncImage(
                                        model = selectedUri,
                                        contentDescription = "Preview",
                                        modifier = Modifier.fillMaxWidth().height(160.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (selectedType == AttachmentType.IMAGE)
                                            Icons.Default.Image else Icons.Default.AttachFile,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(selectedFileName, modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodySmall)
                                    TextButton(onClick = {
                                        selectedUri = null
                                        selectedFileName = ""
                                        selectedType = AttachmentType.NONE
                                    }) {
                                        Text(stringResource(R.string.remove), color = Color.Red)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (formError.isNotEmpty()) {
                        Text(text = formError, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            when {
                                doctorName.isBlank() -> formError = errDoctor
                                date.isBlank() -> formError = errDate
                                time.isBlank() -> formError = errTime
                                symptoms.isBlank() -> formError = errSymptoms
                                else -> {
                                    formError = ""
                                    isLoading = true

                                    val currentUser = auth.currentUser
                                    val patientId = currentUser?.uid ?: ""
                                    val patientEmail = currentUser?.email ?: ""

                                    if (patientId.isBlank()) {
                                        isLoading = false
                                        formError = errNotLoggedIn
                                        return@Button
                                    }

                                    fun saveAppointment(patientName: String, patientCin: String, attachmentUrl: String) {
                                        val appointment = hashMapOf(
                                            "patientId" to patientId,
                                            "patientEmail" to patientEmail,
                                            "patientName" to patientName.ifBlank { patientEmail },
                                            "patientCin" to patientCin,
                                            "doctorName" to doctorName,
                                            "date" to date,
                                            "time" to time,
                                            "symptoms" to symptoms,
                                            "attachmentUrl" to attachmentUrl,
                                            "attachmentType" to selectedType.name,
                                            "attachmentName" to selectedFileName,
                                            "status" to "PENDING",
                                            "paymentStatus" to "UNPAID",
                                            "createdAt" to System.currentTimeMillis()
                                        )
                                        db.collection("appointments").add(appointment)
                                            .addOnSuccessListener {
                                                isLoading = false
                                                onSubmitClick(doctorName, date, time, symptoms)
                                            }
                                            .addOnFailureListener { e ->
                                                isLoading = false
                                                formError = e.message ?: "Failed to submit"
                                            }
                                    }

                                    fun uploadAndSave(patientName: String, patientCin: String) {
                                        if (selectedUri != null && selectedType != AttachmentType.NONE) {
                                            scope.launch {
                                                try {
                                                    val bytes = context.contentResolver
                                                        .openInputStream(selectedUri!!)
                                                        ?.readBytes()
                                                        ?: run {
                                                            isLoading = false
                                                            formError = errReadFail
                                                            return@launch
                                                        }
                                                    val safeName = selectedFileName
                                                        .replace(Regex("[^a-zA-Z0-9._-]"), "_")
                                                    val path = "$patientId/${System.currentTimeMillis()}_$safeName"
                                                    SupabaseClient.storage.from("symptoms").upload(path, bytes)
                                                    val url = SupabaseClient.storage.from("symptoms").publicUrl(path)
                                                    saveAppointment(patientName, patientCin, url)
                                                } catch (e: Exception) {
                                                    isLoading = false
                                                    formError = e.message ?: errUploadFail
                                                }
                                            }
                                        } else {
                                            saveAppointment(patientName, patientCin, "")
                                        }
                                    }

                                    db.collection("users").document(patientId).get()
                                        .addOnSuccessListener { userDoc ->
                                            val firstName = userDoc.getString("firstName") ?: ""
                                            val familyName = userDoc.getString("familyName") ?: ""
                                            val patientCin = userDoc.getString("cin") ?: ""
                                            uploadAndSave("$firstName $familyName".trim(), patientCin)
                                        }
                                        .addOnFailureListener {
                                            uploadAndSave(patientEmail, "")
                                        }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.submit_request), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}