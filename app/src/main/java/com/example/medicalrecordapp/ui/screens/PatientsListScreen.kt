package com.example.medicalrecordapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medicalrecordapp.domain.model.Patient

@Composable
fun PatientsListScreen(
    patients: List<Patient>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(patients) { patient ->
            Card(
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = patient.fullName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = "Age: ${patient.age}")
                    Text(text = "Gender: ${patient.gender}")
                    Text(text = "Phone: ${patient.phone}")
                }
            }
        }
    }
}