package com.example.medicalrecordapp.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // تسجيل دخول
    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // تسجيل حساب جديد — role = "patient" دائماً تلقائياً
    fun registerUser(
        email: String,
        password: String,
        fullName: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val userMap = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "role" to "patient",   // ← دائماً patient عند التسجيل
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users")
                        .document(uid)
                        .set(userMap)
                        .addOnSuccessListener { onResult(true, null) }
                        .addOnFailureListener { e -> onResult(false, e.message) }

                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // جيب الـ role من Firestore بعد الدخول
    fun getUserRole(onResult: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult("patient")
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: "patient"
                onResult(role)
            }
            .addOnFailureListener {
                onResult("patient") // fallback
            }
    }

    // تسجيل خروج
    fun logoutUser() {
        auth.signOut()
    }

    // التحقق هل المستخدم مسجل دخول
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}