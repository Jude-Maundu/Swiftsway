package com.judemaundu.swiftsway2.ui.theme.Data.Admin

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.judemaundu.swiftsway2.ui.theme.Data.FeedBack.Feedback
import com.judemaundu.swiftsway2.ui.theme.Data.Payment.Payment
import com.judemaundu.swiftsway2.ui.theme.Data.Vehicle.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.judemaundu.swiftsway2.ui.theme.Data.AllUsers.AllUsers

class AdminViewModel : ViewModel() {
    private val db = Firebase.firestore

    // Users data
    private val _users = MutableStateFlow<List<User>>(emptyList())
    @SuppressLint("RestrictedApi")
    val users: StateFlow<List<User>> = _users.asStateFlow()

    // Vehicles data
    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles.asStateFlow()

    // Payments data
    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    // Feedbacks data
    private val _feedbacks = MutableStateFlow<List<Feedback>>(emptyList())
    val feedbacks: StateFlow<List<Feedback>> = _feedbacks.asStateFlow()

    init {
        setupRealtimeListeners()
    }

    private fun setupRealtimeListeners() {
        // Users collection listener
        db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val usersList = mutableListOf<User>()
                snapshot?.documents?.forEach { doc ->
                    val user = doc.toObject(User::class.java)
                  //  user?.let { usersList.add(it.copy(id = doc.id)) }
                }
                _users.value = usersList
            }

        // Vehicles collection listener
        db.collection("vehicles")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val vehiclesList = mutableListOf<Vehicle>()
                snapshot?.documents?.forEach { doc ->
                    val vehicle = doc.toObject(Vehicle::class.java)
                    vehicle?.let { vehiclesList.add(it.copy(id = doc.id)) }
                }
                _vehicles.value = vehiclesList
            }

        // Payments collection listener
        db.collection("payments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val paymentsList = mutableListOf<Payment>()
                snapshot?.documents?.forEach { doc ->
                    val payment = doc.toObject(Payment::class.java)
                    payment?.let { paymentsList.add(it.copy(id = doc.id)) }
                }
                _payments.value = paymentsList
            }

        // Feedback collection listener
        db.collection("feedback")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val feedbackList = mutableListOf<Feedback>()
                snapshot?.documents?.forEach { doc ->
                    val feedback = doc.toObject(Feedback::class.java)
                    feedback?.let { feedbackList.add(it.copy(id = doc.id)) }
                }
                _feedbacks.value = feedbackList
            }
    }

    // Functions to modify data
    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            try {
                db.collection("users").document(userId)
                    .update("role", newRole)
                    .await()
            } catch (e: Exception) {
                // Log or handle error
            }
        }
    }

    fun updateVehicleStatus(vehicleId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                db.collection("vehicles").document(vehicleId)
                    .update("status", newStatus)
                    .await()
            } catch (e: Exception) {
                // Log or handle error
            }
        }
    }

    fun deleteFeedback(feedbackId: String) {
        viewModelScope.launch {
            try {
                db.collection("feedback").document(feedbackId)
                    .delete()
                    .await()
            } catch (e: Exception) {
                // Log or handle error
            }
        }
    }

class AdminViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<AllUsers>>(emptyList())
    val users: StateFlow<List<AllUsers>> = _users.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    init {
        db.collection("users")  // adjust collection name if different
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val userList = snapshot.documents.mapNotNull { it.toObject(AllUsers::class.java) }
                _users.value = userList
            }
    }
}
}