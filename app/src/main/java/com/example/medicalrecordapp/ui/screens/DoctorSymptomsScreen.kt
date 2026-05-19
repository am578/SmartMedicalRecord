package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Person
import androidx.activity.compose.BackHandler
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.medicalrecordapp.R
import com.example.medicalrecordapp.domain.model.AttachmentType
import com.example.medicalrecordapp.domain.model.Symptom
import com.example.medicalrecordapp.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorSymptomsScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    val symptomsList by authViewModel.symptomsList.collectAsState()
    LaunchedEffect(Unit) { authViewModel.fetchAllSymptoms() }
    BackHandler { onBackClick() }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.patient_symptoms), fontWeight = FontWeight.Bold) },
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

        if (symptomsList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.no_symptoms_submitted), color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(symptomsList) { symptom -> SymptomCard(symptom = symptom) }
            }
        }
    }
}

@Composable
private fun SymptomCard(symptom: Symptom) {
    val uriHandler = LocalUriHandler.current
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(36.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(text = symptom.patientName.ifBlank { stringResource(R.string.unknown) }, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        if (symptom.patientCin.isNotBlank())
                            Text(stringResource(R.string.cin_id_label) + ": ${symptom.patientCin}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
                Text(dateFormat.format(Date(symptom.createdAt)), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Text(stringResource(R.string.symptoms_label), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(symptom.description, style = MaterialTheme.typography.bodyMedium)

            if (symptom.attachmentUrl.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))

                when (symptom.attachmentType) {
                    AttachmentType.IMAGE -> {
                        Text(stringResource(R.string.attached_image), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))
                        AsyncImage(
                            model = symptom.attachmentUrl,
                            contentDescription = "Symptom image",
                            modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(12.dp))
                                .clickable { uriHandler.openUri(symptom.attachmentUrl) },
                            contentScale = ContentScale.Crop
                        )
                        Text(stringResource(R.string.tap_to_open_image), style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    }
                    AttachmentType.FILE -> {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { uriHandler.openUri(symptom.attachmentUrl) }.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(40.dp)) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(symptom.attachmentName.ifBlank { stringResource(R.string.attached_file) }, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                                Text(stringResource(R.string.tap_to_open), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                    AttachmentType.NONE -> {}
                }
            }
        }
    }
}