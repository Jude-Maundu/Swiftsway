package com.judemaundu.swiftsway2.ui.theme.Data

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class UserProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")
   private val storage = FirebaseStorage.getInstance().reference

    var name by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var profileImageUrl by mutableStateOf("")
        private set

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        email = user?.email ?: ""

        user?.uid?.let { uid ->
            database.child(uid).get().addOnSuccessListener {
                name = it.child("name").getValue(String::class.java) ?: ""
            }

            storage.child("profileImages/$uid.jpg").downloadUrl
                .addOnSuccessListener { uri ->
                    profileImageUrl = uri.toString()
                }
        }
    }

    fun updateName(newName: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        if (newName.isBlank()) return onError()

        database.child(uid).child("name").setValue(newName)
            .addOnSuccessListener {
                name = newName
                onSuccess()
            }
            .addOnFailureListener {
                onError()
            }
    }
}
fun registerBooking(destination: String, seatPreference: String, context: android.content.Context) {
    val database = FirebaseDatabase.getInstance()
    val bookingsRef = database.getReference("bookings")

    val bookingId = bookingsRef.push().key ?: UUID.randomUUID().toString()
    val booking = mapOf(
        "id" to bookingId,
        "destination" to destination,
        "seatPreference" to seatPreference,
        "timestamp" to System.currentTimeMillis()
    )

    bookingsRef.child(bookingId).setValue(booking)
        .addOnSuccessListener {
            Toast.makeText(context, "Booking registered!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Booking failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
}


@Preview
@Composable
private fun Userprev() {
    UserProfileViewModel()
}
