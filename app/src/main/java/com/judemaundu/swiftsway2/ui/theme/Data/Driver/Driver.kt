package com.judemaundu.swiftsway2.ui.theme.Data.Driver

data class Driver(
    val driverId: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val licenseNumber: String = "",
    val vehicleId: String = "", // ID of the vehicle they drive
    val profileImageUrl: String = "", // URL to profile picture in Firebase Storage
    val routeAssigned: String = "", // Route name or ID
    val isAvailable: Boolean = true, // For real-time availability tracking
    val role: String = "driver", // Fixed role
    val rating: Double = 0.0,
    val completedTrips: Int = 0,
    val lastLogin: Long = 0L // Timestamp

)
