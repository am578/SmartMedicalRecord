package com.example.medicalrecordapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicalrecordapp.data.repository.FakePatientRepositoryImpl
import com.example.medicalrecordapp.domain.model.Patient

class PatientViewModel : ViewModel() {

    private val repository = FakePatientRepositoryImpl()

    fun getPatients(): List<Patient> {
        return repository.getPatients()
    }
}