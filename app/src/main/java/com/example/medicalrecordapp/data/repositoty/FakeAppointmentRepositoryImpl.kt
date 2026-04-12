package com.example.medicalrecordapp.data.repository

import com.example.medicalrecordapp.domain.model.Appointment
import com.example.medicalrecordapp.domain.repository.AppointmentRepository

class FakeAppointmentRepositoryImpl : AppointmentRepository {

    override fun getAppointments(): List<Appointment> {
        return listOf(
            Appointment(1, "Ahmed Benali", "2026-04-15", "09:00 AM", "Scheduled"),
            Appointment(2, "Sara Amrani", "2026-04-15", "11:00 AM", "Completed"),
            Appointment(3, "Yacine Boudiaf", "2026-04-16", "02:30 PM", "Scheduled")
        )
    }
}