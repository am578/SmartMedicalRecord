package com.example.medicalrecordapp.domain.model

data class User(
    val id: Int,
    val fullName: String,
    val email: String,
    val password: String,
    val role: UserRole
)