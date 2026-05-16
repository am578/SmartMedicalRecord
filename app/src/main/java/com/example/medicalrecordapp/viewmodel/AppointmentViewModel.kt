package com.example.medicalrecordapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicalrecordapp.data.repository.FirebaseAppointmentRepository
import com.example.medicalrecordapp.domain.model.Appointment

class AppointmentViewModel : ViewModel() {

    private val repository = FirebaseAppointmentRepository()

    fun getAppointments(
        onSuccess: (List<Appointment>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        repository.getAppointments(
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}