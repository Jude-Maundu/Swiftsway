package com.judemaundu.swiftsway2.ui.theme.Data.Vehicle

data class Vehicle(
    val vehicleId: String = "",
    val plateNumber: String = "",
    val model: String = "",
    val capacity: Int = 0,
    val driverId: String = "",
    val conductorId: String = "",
    val routeAssigned: String = "",
    val status: String = "active",// "active", "maintenance", "inactive"
    val imageUrl: String = "", // Vehicle photo from Firebase Storage
    val currentLocation: String = "",// Optional: for last known GPS location
    val tripCount: Int = 0,
    val isAvailable: Boolean = true,
    val registrationDate: Long = 0L,
    val id: String

)
