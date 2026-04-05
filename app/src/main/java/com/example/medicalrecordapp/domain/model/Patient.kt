package com.example.medicalrecordapp.domain.model

data class Patient(
    val id: Int,
    val fullName: String,
    val age: Int,
    val gender: String,
    val phone: String
)