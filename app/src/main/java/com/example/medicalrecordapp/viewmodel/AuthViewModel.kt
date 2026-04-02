package com.example.medicalrecordapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.medicalrecordapp.data.repository.FakeAuthRepositoryImpl
import com.example.medicalrecordapp.domain.model.User
import com.example.medicalrecordapp.domain.model.UserRole

class AuthViewModel : ViewModel() {

    private val repository = FakeAuthRepositoryImpl()

    val loggedInUser = mutableStateOf<User?>(null)
    val loginError = mutableStateOf("")

    fun login(email: String, password: String) {
        val user = repository.login(email, password)
        if (user != null) {
            loggedInUser.value = user
            loginError.value = ""
        } else {
            loginError.value = "Invalid email or password"
        }
    }

    fun register(fullName: String, email: String, password: String): Boolean {
        val newUser = User(
            id = (1..1000).random(),
            fullName = fullName,
            email = email,
            password = password,
            role = UserRole.PATIENT
        )

        val success = repository.register(newUser)
        if (success) {
            loggedInUser.value = newUser
            loginError.value = ""
        } else {
            loginError.value = "Email already exists"
        }
        return success
    }
}