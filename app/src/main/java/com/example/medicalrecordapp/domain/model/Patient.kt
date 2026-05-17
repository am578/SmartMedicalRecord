package com.example.medicalrecordapp.domain.model

data class Patient(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val age: Int = 0,
    val gender: String = "",
    val phone: String = "",
    val cin: String = "",
    val dateOfBirth: String = "",
    val address: String = "",
    val bloodGroup: String = "",
    val chronicDiseases: String = "",
    val documentId: String = ""
)