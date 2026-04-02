package com.example.medicalrecordapp.domain.repository

import com.example.medicalrecordapp.domain.model.User

interface AuthRepository {
    fun login(email: String, password: String): User?
    fun register(user: User): Boolean
}