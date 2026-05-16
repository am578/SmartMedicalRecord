package com.example.medicalrecordapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicalrecordapp.data.repository.FirebasePatientRepository
import com.example.medicalrecordapp.domain.model.Patient

class PatientViewModel : ViewModel() {

    private val repository = FirebasePatientRepository()

    fun getPatients(
        onSuccess: (List<Patient>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.getPatients(
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}