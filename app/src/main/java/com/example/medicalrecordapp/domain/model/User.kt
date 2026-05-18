package com.example.medicalrecordapp.domain.model

data class User(
    val id: String = "",
    val fullName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val role: UserRole = UserRole.PATIENT,
    val age: Int = 0,
    val gender: String = "",
    val cin: String = "",
    val phone: String = "",
    val speciality: String = "",
    val officeNumber: String = ""
)