package com.example.medicalrecordapp.data.repository

import com.example.medicalrecordapp.domain.model.Patient
import com.example.medicalrecordapp.domain.repository.PatientRepository

class FakePatientRepositoryImpl : PatientRepository {

    override fun getPatients(): List<Patient> {
        return listOf(
            Patient(1, "Ahmed", "Benali", 25, "Male", "0550123456"),
            Patient(2, "Sara", "Amrani", 30, "Female", "0661234567"),
            Patient(3, "Yacine", "Boudiaf", 40, "Male", "0777654321")
        )
    }
}