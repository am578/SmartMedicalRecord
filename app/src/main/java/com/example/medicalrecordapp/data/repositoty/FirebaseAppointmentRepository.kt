package com.example.medicalrecordapp.data.repository

import com.example.medicalrecordapp.domain.model.Appointment
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseAppointmentRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAppointments(
        onSuccess: (List<Appointment>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("appointments")
            .get()
            .addOnSuccessListener { result ->

                val appointments = result.documents.mapIndexed { index, document ->

                    val patientName = document.getString("patientName") ?: ""
                    val date = document.getString("date") ?: ""
                    val time = document.getString("time") ?: ""
                    val status = document.getString("status") ?: "Pending"

                    Appointment(
                        id = index + 1,
                        patientName = patientName,
                        date = date,
                        time = time,
                        status = status
                    )
                }

                onSuccess(appointments)
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to load appointments")
            }
    }
}
