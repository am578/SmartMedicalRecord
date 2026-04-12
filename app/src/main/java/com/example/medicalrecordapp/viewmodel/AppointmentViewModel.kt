package com.example.medicalrecordapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicalrecordapp.data.repository.FakeAppointmentRepositoryImpl
import com.example.medicalrecordapp.domain.model.Appointment

class AppointmentViewModel : ViewModel() {

    private val repository = FakeAppointmentRepositoryImpl()

    fun getAppointments(): List<Appointment> {
        return repository.getAppointments()
    }
}