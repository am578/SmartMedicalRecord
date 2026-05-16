package com.example.medicalrecordapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalrecordapp.domain.model.User
import com.example.medicalrecordapp.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ✅ حساب الأدمن المُدمج (Hardcoded)
    private val HARDCODED_ADMIN_EMAIL = "admin@smartmedicalrecord.com"
    private val HARDCODED_ADMIN_PASSWORD = "admin"

    var isHardcodedAdminLoggedIn by mutableStateOf(false)
        private set

    // نحفظ بيانات المستخدم الحالي (للأدمن المُدمج أو العادي)
    var currentUserEmail: String = ""
        private set
    var currentUserPassword: String = ""
        private set

    // ====== قائمة الموظفين ======
    private val _staffList = MutableStateFlow<List<User>>(emptyList())
    val staffList: StateFlow<List<User>> = _staffList

    // ====== تسجيل الدخول ======
    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        // ✅ التحقق من حساب الأدمن المُدمج أولاً
        if (email == HARDCODED_ADMIN_EMAIL && password == HARDCODED_ADMIN_PASSWORD) {
            isHardcodedAdminLoggedIn = true
            currentUserEmail = email
            currentUserPassword = password
            onResult(true, null)
            return
        }

        // تسجيل دخول عادي عبر Firebase
        currentUserEmail = email
        currentUserPassword = password

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // ====== تسجيل مريض جديد ======
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

                    db.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener { onResult(true, null) }
                        .addOnFailureListener { e -> onResult(false, e.message) }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // ====== إنشاء حساب موظف ======
    fun createStaffAccount(
        staffEmail: String,
        staffPassword: String,
        fullName: String,
        role: UserRole,
        onResult: (Boolean, String?, String?) -> Unit
    ) {
        val adminEmail = currentUserEmail
        val adminPassword = currentUserPassword

        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            onResult(false, "Admin session expired. Please login again.", null)
            return
        }

        // ✅ لو الأدمن مُدمج، ننشئ في Firebase عادي لكن نرجعه بعدها
        auth.createUserWithEmailAndPassword(staffEmail, staffPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        reLoginAdmin(adminEmail, adminPassword) {
                            onResult(false, "User ID not found", null)
                        }
                        return@addOnCompleteListener
                    }

                    val userMap = hashMapOf(
                        "fullName" to fullName,
                        "email" to staffEmail,
                        "role" to role.name,
                        "createdBy" to adminEmail,
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            reLoginAdmin(adminEmail, adminPassword) {
                                onResult(true, null, uid)
                            }
                        }
                        .addOnFailureListener { e ->
                            reLoginAdmin(adminEmail, adminPassword) {
                                onResult(false, e.message, null)
                            }
                        }
                } else {
                    onResult(false, task.exception?.message, null)
                }
            }
    }

    // ====== إعادة تسجيل دخول الأدمن ======
    private fun reLoginAdmin(email: String, password: String, onDone: () -> Unit) {
        auth.signOut()
        // ✅ لو الأدمن مُدمج، ما نحتاج نسجل دخول Firebase
        if (isHardcodedAdminLoggedIn) {
            onDone()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onDone() }
    }

    // ====== جلب قائمة الموظفين ======
    fun fetchStaffUsers() {
        viewModelScope.launch {
            db.collection("users")
                .whereIn("role", listOf(UserRole.DOCTOR.name, UserRole.RECEPTIONIST.name))
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents.mapNotNull { doc ->
                        val roleStr = doc.getString("role") ?: return@mapNotNull null
                        val role = try {
                            UserRole.valueOf(roleStr)
                        } catch (e: Exception) { null } ?: return@mapNotNull null

                        User(
                            id = doc.id,
                            fullName = doc.getString("fullName") ?: "",
                            email = doc.getString("email") ?: "",
                            role = role
                        )
                    }
                    _staffList.value = list
                }
                .addOnFailureListener {
                    _staffList.value = emptyList()
                }
        }
    }

    // ====== حذف موظف ======
    fun deleteStaffUser(userId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("users").document(userId).delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // ====== جلب صلاحية المستخدم ======
    fun getUserRole(onResult: (String) -> Unit) {
        // ✅ لو الأدمن المُدمج مسجل دخول، نرجع ADMIN مباشرة
        if (isHardcodedAdminLoggedIn) {
            onResult(UserRole.ADMIN.name)
            return
        }

        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult(UserRole.PATIENT.name)
            return
        }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                val role = doc.getString("role") ?: UserRole.PATIENT.name
                onResult(role)
            }
            .addOnFailureListener {
                onResult(UserRole.PATIENT.name)
            }
    }

    // ====== تسجيل الخروج ======
    fun logoutUser() {
        isHardcodedAdminLoggedIn = false
        auth.signOut()
        currentUserEmail = ""
        currentUserPassword = ""
    }

    // ====== هل المستخدم مسجل دخول؟ ======
    fun isUserLoggedIn(): Boolean {
        return isHardcodedAdminLoggedIn || auth.currentUser != null
    }
}
