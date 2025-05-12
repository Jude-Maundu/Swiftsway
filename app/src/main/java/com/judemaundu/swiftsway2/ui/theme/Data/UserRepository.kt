package com.judemaundu.swiftsway2.ui.theme.Data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_HOME
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_LOGIN
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_REGISTER

class AuthViewModel(var navController: NavHostController, var context: Context) {

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

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
                    "role" to role
                )

                // Save to general Users table
                val regRef = FirebaseDatabase.getInstance().getReference("Users/$userId")
                regRef.setValue(userData).addOnCompleteListener { regTask ->
                    if (regTask.isSuccessful) {
                        // Save to role-specific table (include 'active' status as true)
                        UserRepository.saveUserToRoleLibrary(role, name, email, true)

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
                Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_HOME)
            } else {
                Toast.makeText(context, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_LOGIN)
            }
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

class ProfileActivity : AppCompatActivity() {
    fun uploadImageToFirebase(uid: String?, imageUri: Uri) {
        uid?.let {
            val storageRef = FirebaseStorage.getInstance().reference
                .child("users/$uid/profile.jpg")

            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Handle success, update UI with the URL
                    }
                }
                .addOnFailureListener {
                    // Handle error
                }
        } ?: run {
            // Handle null UID error
        }
    }
}
