package com.judemaundu.swiftsway2.ui.theme.Data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object UserRepository {

    // Save the user to a specific role in Firebase
    fun saveUserToRoleLibrary(role: String, name: String, email: String, active: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("users/$role/$uid")

        // Create the user data map
        val userData = mapOf(
            "name" to name,
            "email" to email,
            "status" to active,
            "role" to role,
            "uid" to uid
        )

        // Save user data in Firebase Realtime Database
        databaseRef.setValue(userData)
    }
}

sealed class ResultState {
    object Success : ResultState()
    data class Error(val message: String) : ResultState()
    object Idle : ResultState()
}
