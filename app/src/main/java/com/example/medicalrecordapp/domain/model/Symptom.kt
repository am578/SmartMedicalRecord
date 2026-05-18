package com.example.medicalrecordapp.domain.model

data class Symptom(
    val id: String = "",
    val patientId: String = "",
    val patientCin: String = "",
    val patientName: String = "",
    val description: String = "",
    val attachmentUrl: String = "",
    val attachmentType: AttachmentType = AttachmentType.NONE,
    val attachmentName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class AttachmentType {
    NONE, IMAGE, FILE
}