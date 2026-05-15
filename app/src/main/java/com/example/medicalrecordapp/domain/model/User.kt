package com.example.medicalrecordapp.domain.model

data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: UserRole = UserRole.PATIENT
)
