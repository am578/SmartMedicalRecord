package com.example.medicalrecordapp.data.repository

import com.example.medicalrecordapp.domain.model.Patient
import com.google.firebase.firestore.FirebaseFirestore

class FirebasePatientRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getPatients(
        onSuccess: (List<Patient>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("patients")
            .get()
            .addOnSuccessListener { result ->
                val patients = result.documents.mapIndexed { index, document ->
                    val firstName = document.getString("firstName") ?: ""
                    val familyName = document.getString("familyName")
                        ?: document.getString("lastName")
                        ?: ""

                    val gender = document.getString("gender") ?: ""
                    val phone = document.getString("phone") ?: ""

                    val ageValue = document.get("age")
                    val age = when (ageValue) {
                        is Long -> ageValue.toInt()
                        is Int -> ageValue
                        is String -> ageValue.toIntOrNull() ?: 0
                        else -> 0
                    }

                    Patient(
                        id = index + 1,
                        firstName = firstName,
                        lastName = familyName,
                        age = age,
                        gender = gender,
                        phone = phone
                    )
                }

                onSuccess(patients)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to load patients")
            }
    }
}