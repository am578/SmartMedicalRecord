package com.example.medicalrecordapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.medicalrecordapp.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun registerUser(
        email: String,
        password: String,
        firstName: String,
        familyName: String,
        cin: String,
        phone: String,
        dateOfBirth: String,
        gender: String,
        address: String,
        bloodGroup: String,
        chronicDiseases: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid

                    if (uid == null) {
                        onResult(false, "User ID not found")
                        return@addOnCompleteListener
                    }

                    val userMap = hashMapOf(
                        "firstName" to firstName,
                        "familyName" to familyName,
                        "email" to email,
                        "cin" to cin,
                        "phone" to phone,
                        "dateOfBirth" to dateOfBirth,
                        "gender" to gender,
                        "address" to address,
                        "bloodGroup" to bloodGroup,
                        "chronicDiseases" to chronicDiseases,
                        "role" to UserRole.PATIENT.name,
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users")
                        .document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.message)
                        }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun createStaffAccount(
        email: String,
        password: String,
        fullName: String,
        role: UserRole,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid

                    if (uid == null) {
                        onResult(false, "User ID not found")
                        return@addOnCompleteListener
                    }

                    val userMap = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "role" to role.name,
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users")
                        .document(uid)
                        .set(userMap)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.message)
                        }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getUserRole(onResult: (String) -> Unit) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onResult(UserRole.PATIENT.name)
            return
        }

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: UserRole.PATIENT.name
                onResult(role)
            }
            .addOnFailureListener {
                onResult(UserRole.PATIENT.name)
            }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
