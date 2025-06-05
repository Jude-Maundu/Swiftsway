package com.judemaundu.swiftsway2.ui.theme.Data.Conductor

data class Conductor(
    val conductorId: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val vehicleId: String = "", // ID of the vehicle they manage
    val profileImageUrl: String = "", // URL to Firebase Storage
    val routeAssigned: String = "", // Route name or ID
    val isActive: Boolean = true, // For suspension/activation
    val role: String = "conductor" ,// Fixed role
    val totalTicketsIssued: Int = 0,
    val lastLogin: Long = 0L,
    val ratings: Double = 0.0
)