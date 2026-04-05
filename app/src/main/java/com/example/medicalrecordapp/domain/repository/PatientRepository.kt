package com.example.medicalrecordapp.domain.repository

import com.example.medicalrecordapp.domain.model.Patient

interface PatientRepository {
    fun getPatients(): List<Patient>
}