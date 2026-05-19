package com.example.medicalrecordapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.activity.compose.BackHandler
import com.example.medicalrecordapp.R
import com.example.medicalrecordapp.domain.model.AttachmentType
import com.example.medicalrecordapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitSymptomsScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(AttachmentType.NONE) }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedUri = uri; selectedType = AttachmentType.IMAGE
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
            selectedUri = uri; selectedType = AttachmentType.FILE
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
    
    val errDescribeFirst = stringResource(R.string.err_describe_first)
    val successSubmitted = stringResource(R.string.symptoms_submitted_success)
    val errSubmitFailed = stringResource(R.string.err_submit_failed)

    BackHandler { onBackClick() }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.submit_symptoms), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back)) }
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
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())
        ) {
            Text(stringResource(R.string.describe_symptoms), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text(stringResource(R.string.describe_symptoms_placeholder)) },
                modifier = Modifier.fillMaxWidth().height(160.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 8
            )

            Spacer(Modifier.height(20.dp))
            Text(stringResource(R.string.attach_file), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { imageLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.image))
                }
                OutlinedButton(
                    onClick = { fileLauncher.launch("*/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.file))
                }
            }

            if (selectedUri != null) {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (selectedType == AttachmentType.IMAGE) {
                            AsyncImage(
                                model = selectedUri,
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (selectedType == AttachmentType.IMAGE) Icons.Default.Image else Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(selectedFileName, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            TextButton(onClick = { selectedUri = null; selectedFileName = ""; selectedType = AttachmentType.NONE }) {
                                Text(stringResource(R.string.remove), color = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (successMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(successMessage, color = Color(0xFF2E7D32), modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(8.dp))
            }

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (description.isBlank()) { errorMessage = errDescribeFirst; return@Button }
                    isLoading = true; successMessage = ""; errorMessage = ""
                    authViewModel.uploadSymptom(
                        description = description, fileUri = selectedUri,
                        fileType = selectedType, fileName = selectedFileName
                    ) { success, message ->
                        isLoading = false
                        if (success) {
                            successMessage = successSubmitted
                            description = ""; selectedUri = null; selectedFileName = ""; selectedType = AttachmentType.NONE
                        } else {
                            errorMessage = message ?: errSubmitFailed
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.submit_symptoms))
                }
            }
        }
    }
}