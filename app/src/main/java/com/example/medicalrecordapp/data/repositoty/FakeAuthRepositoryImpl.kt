package com.example.medicalrecordapp.data.repository

import com.example.medicalrecordapp.domain.model.User
import com.example.medicalrecordapp.domain.model.UserRole
import com.example.medicalrecordapp.domain.repository.AuthRepository

class FakeAuthRepositoryImpl : AuthRepository {

    private val users = mutableListOf(
        User(1, "Admin User", "admin@gmail.com", "1234", UserRole.ADMIN),
        User(2, "Doctor User", "doctor@gmail.com", "1234", UserRole.DOCTOR),
        User(3, "Reception User", "reception@gmail.com", "1234", UserRole.RECEPTIONIST),
        User(4, "Patient User", "patient@gmail.com", "1234", UserRole.PATIENT)
    )

    override fun login(email: String, password: String): User? {
        return users.find { it.email == email && it.password == password }
    }

    override fun register(user: User): Boolean {
        val existingUser = users.find { it.email == user.email }
        return if (existingUser == null) {
            users.add(user)
            true
        } else {
            false
        }
    }
}