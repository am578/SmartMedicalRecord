package com.example.medicalrecordapp.domain.model

data class Patient(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val gender: String,
    val phone: String
)