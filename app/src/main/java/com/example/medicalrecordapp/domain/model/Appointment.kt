package com.example.medicalrecordapp.domain.model

data class Appointment(
    val id: Int,
    val patientName: String,
    val date: String,
    val time: String,
    val status: String
)