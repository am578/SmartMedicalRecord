package com.example.medicalrecordapp.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicalrecordapp.domain.model.AttachmentType
import com.example.medicalrecordapp.domain.model.Symptom
import com.example.medicalrecordapp.domain.model.User
import com.example.medicalrecordapp.domain.model.UserRole
import com.example.medicalrecordapp.utils.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: android.app.Application) : androidx.lifecycle.AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val HARDCODED_ADMIN_EMAIL = "admin@smartmedicalrecord.com"
    private val HARDCODED_ADMIN_PASSWORD = "admin"

    var isHardcodedAdminLoggedIn by mutableStateOf(false)
        private set

    var currentUserEmail: String = ""
        private set
    var currentUserPassword: String = ""
        private set

    private val _staffList = MutableStateFlow<List<User>>(emptyList())
    val staffList: StateFlow<List<User>> = _staffList

    private val _symptomsList = MutableStateFlow<List<Symptom>>(emptyList())
    val symptomsList: StateFlow<List<Symptom>> = _symptomsList

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email == HARDCODED_ADMIN_EMAIL && password == HARDCODED_ADMIN_PASSWORD) {
            isHardcodedAdminLoggedIn = true
            currentUserEmail = email
            currentUserPassword = password
            onResult(true, null)
            return
        }
        currentUserEmail = email
        currentUserPassword = password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.message)
            }
    }

    fun registerUser(
        email: String, password: String, firstName: String, familyName: String,
        cin: String, phone: String, dateOfBirth: String, gender: String,
        address: String, bloodGroup: String, chronicDiseases: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: run { onResult(false, "User ID not found"); return@addOnCompleteListener }
                    val userMap = hashMapOf(
                        "firstName" to firstName, "familyName" to familyName,
                        "fullName" to "$firstName $familyName",
                        "email" to email, "cin" to cin, "phone" to phone,
                        "dateOfBirth" to dateOfBirth, "gender" to gender,
                        "address" to address, "bloodGroup" to bloodGroup,
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

    fun createStaffAccount(
        staffEmail: String, staffPassword: String,
        firstName: String, lastName: String,
        age: Int, gender: String, cin: String, phone: String,
        role: UserRole,
        speciality: String = "",
        officeNumber: String = "",
        onResult: (Boolean, String?, String?) -> Unit
    ) {
        val adminEmail = currentUserEmail
        val adminPassword = currentUserPassword

        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            onResult(false, "Admin session expired. Please login again.", null)
            return
        }

        auth.createUserWithEmailAndPassword(staffEmail, staffPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: run {
                        reLoginAdmin(adminEmail, adminPassword) { onResult(false, "User ID not found", null) }
                        return@addOnCompleteListener
                    }

                    val userMap = hashMapOf<String, Any>(
                        "firstName" to firstName, "lastName" to lastName,
                        "fullName" to "$firstName $lastName",
                        "age" to age, "gender" to gender,
                        "cin" to cin, "phone" to phone,
                        "email" to staffEmail, "role" to role.name,
                        "createdBy" to adminEmail,
                        "createdAt" to System.currentTimeMillis()
                    )
                    if (role == UserRole.DOCTOR) userMap["speciality"] = speciality
                    if (role == UserRole.RECEPTIONIST) userMap["officeNumber"] = officeNumber

                    db.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            reLoginAdmin(adminEmail, adminPassword) { onResult(true, null, uid) }
                        }
                        .addOnFailureListener { e ->
                            reLoginAdmin(adminEmail, adminPassword) { onResult(false, e.message, null) }
                        }
                } else {
                    onResult(false, task.exception?.message, null)
                }
            }
    }

    private fun reLoginAdmin(email: String, password: String, onDone: () -> Unit) {
        auth.signOut()
        if (isHardcodedAdminLoggedIn) { onDone(); return }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { onDone() }
    }

    fun fetchStaffUsers() {
        viewModelScope.launch {
            db.collection("users")
                .whereIn("role", listOf(UserRole.DOCTOR.name, UserRole.RECEPTIONIST.name))
                .get()
                .addOnSuccessListener { snapshot ->
                    _staffList.value = snapshot.documents.mapNotNull { doc ->
                        val roleStr = doc.getString("role") ?: return@mapNotNull null
                        val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { null } ?: return@mapNotNull null
                        User(
                            id = doc.id,
                            firstName = doc.getString("firstName") ?: "",
                            lastName = doc.getString("lastName") ?: "",
                            fullName = doc.getString("fullName") ?: "",
                            email = doc.getString("email") ?: "",
                            role = role,
                            age = (doc.getLong("age") ?: 0L).toInt(),
                            gender = doc.getString("gender") ?: "",
                            cin = doc.getString("cin") ?: "",
                            phone = doc.getString("phone") ?: "",
                            speciality = doc.getString("speciality") ?: "",
                            officeNumber = doc.getString("officeNumber") ?: ""
                        )
                    }
                }
                .addOnFailureListener { _staffList.value = emptyList() }
        }
    }

    fun deleteStaffUser(userId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("users").document(userId).delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun getUserRole(onResult: (String) -> Unit) {
        if (isHardcodedAdminLoggedIn) { onResult(UserRole.ADMIN.name); return }
        val uid = auth.currentUser?.uid ?: run { onResult(UserRole.PATIENT.name); return }
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc -> onResult(doc.getString("role") ?: UserRole.PATIENT.name) }
            .addOnFailureListener { onResult(UserRole.PATIENT.name) }
    }

    fun uploadSymptom(
        description: String,
        fileUri: Uri?,
        fileType: AttachmentType,
        fileName: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: run { onResult(false, "User not logged in"); return }

        db.collection("users").document(uid).get()
            .addOnSuccessListener { userDoc ->
                val cin = userDoc.getString("cin") ?: ""
                val firstName = userDoc.getString("firstName") ?: ""
                val familyName = userDoc.getString("familyName") ?: ""
                val patientName = "$firstName $familyName".trim()

                fun saveSymptom(url: String, type: AttachmentType, name: String) {
                    val map = hashMapOf(
                        "patientId" to uid,
                        "patientCin" to cin,
                        "patientName" to patientName,
                        "description" to description,
                        "attachmentUrl" to url,
                        "attachmentType" to type.name,
                        "attachmentName" to name,
                        "createdAt" to System.currentTimeMillis()
                    )
                    db.collection("symptoms").add(map)
                        .addOnSuccessListener { onResult(true, null) }
                        .addOnFailureListener { e -> onResult(false, e.message) }
                }

                if (fileUri != null && fileType != AttachmentType.NONE) {
                    viewModelScope.launch {
                        try {
                            val bytes = getApplication<android.app.Application>()
                                .contentResolver
                                .openInputStream(fileUri)
                                ?.readBytes()
                                ?: run { onResult(false, "Cannot read file"); return@launch }

                            val path = "$uid/${System.currentTimeMillis()}_$fileName"

                            SupabaseClient.storage.from("symptoms").upload(path, bytes)

                            val publicUrl = SupabaseClient.storage.from("symptoms").publicUrl(path)

                            saveSymptom(publicUrl, fileType, fileName)

                        } catch (e: Exception) {
                            onResult(false, e.message)
                        }
                    }
                } else {
                    saveSymptom("", AttachmentType.NONE, "")
                }
            }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
    fun fetchAllSymptoms() {
        viewModelScope.launch {
            db.collection("symptoms")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    _symptomsList.value = snapshot.documents.map { doc ->
                        Symptom(
                            id = doc.id,
                            patientId = doc.getString("patientId") ?: "",
                            patientCin = doc.getString("patientCin") ?: "",
                            patientName = doc.getString("patientName") ?: "",
                            description = doc.getString("description") ?: "",
                            attachmentUrl = doc.getString("attachmentUrl") ?: "",
                            attachmentType = try {
                                AttachmentType.valueOf(doc.getString("attachmentType") ?: "NONE")
                            } catch (e: Exception) { AttachmentType.NONE },
                            attachmentName = doc.getString("attachmentName") ?: "",
                            createdAt = doc.getLong("createdAt") ?: 0L
                        )
                    }
                }
                .addOnFailureListener { _symptomsList.value = emptyList() }
        }
    }

    fun logoutUser() {
        isHardcodedAdminLoggedIn = false
        auth.signOut()
        currentUserEmail = ""
        currentUserPassword = ""
    }

    fun isUserLoggedIn(): Boolean = isHardcodedAdminLoggedIn || auth.currentUser != null
}