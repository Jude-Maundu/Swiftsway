package com.judemaundu.swiftsway2.ui.theme.Data.UserId

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_ADMIN_PANEL
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_LOGIN
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_PASSENGER_DASHBOARD
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_REGISTER

class AuthViewModel(var navController: NavHostController, var context: Context) : ViewModel() {
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    companion object {
        const val ADMIN_ROLE = "admin"
        const val USER_ROLE = "user"
    }

    fun signup(name: String, email: String, phone: String, pass: String, confpass: String, role: String) {
        if (email.isBlank() || pass.isBlank() || confpass.isBlank() || name.isBlank() || phone.isBlank()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show()
            return
        } else if (pass != confpass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser!!.uid
                val userData = mapOf(
                    "name" to name,
                    "email" to email,
                    "phone" to phone,
                    "role" to role,
                    "isActive" to true
                )

                db.child("Users/$userId").setValue(userData).addOnCompleteListener { regTask ->
                    if (regTask.isSuccessful) {
                        Toast.makeText(context, "Registered Successfully", Toast.LENGTH_LONG).show()
                        navController.navigate(ROUTE_LOGIN)
                    } else {
                        Toast.makeText(context, "${regTask.exception?.message}", Toast.LENGTH_LONG).show()
                        navController.navigate(ROUTE_LOGIN)
                    }
                }
            } else {
                Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_REGISTER)
            }
        }
    }

    fun login(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid ?: ""
                db.child("Users/$userId/role").get().addOnSuccessListener { snapshot ->
                    val role = snapshot.value as? String ?: ""
                    if (role == ADMIN_ROLE) {
                        Toast.makeText(context, "Admin login successful", Toast.LENGTH_LONG).show()
                        navController.navigate(ROUTE_ADMIN_PANEL)
                    } else {
                        Toast.makeText(context, "User login successful", Toast.LENGTH_LONG).show()
                        navController.navigate(ROUTE_PASSENGER_DASHBOARD)
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Error checking user role", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_LOGIN)
                }
            } else {
                Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_LOGIN)
            }
        }
    }

    fun isAdmin(callback: (Boolean) -> Unit) {
        val userId = mAuth.currentUser?.uid ?: run {
            callback(false)
            return
        }

        db.child("Users/$userId/role").get().addOnSuccessListener { snapshot ->
            val role = snapshot.value as? String ?: ""
            callback(role == ADMIN_ROLE)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun logout() {
        mAuth.signOut()
        navController.navigate(ROUTE_LOGIN)
    }

    fun isLoggedIn(): Boolean {
        return mAuth.currentUser != null
    }
}




























































































