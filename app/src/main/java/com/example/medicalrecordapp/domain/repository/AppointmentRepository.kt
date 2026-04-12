package com.example.medicalrecordapp.domain.repository

import com.example.medicalrecordapp.domain.model.Appointment

interface AppointmentRepository {
    fun getAppointments(): List<Appointment>
}