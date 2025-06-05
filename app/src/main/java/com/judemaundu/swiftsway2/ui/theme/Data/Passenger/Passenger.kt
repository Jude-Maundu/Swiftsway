package com.judemaundu.swiftsway2.ui.theme.Data.Passenger

data class Passenger(
    val passengerId: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "", // URL to Firebase Storage
    val walletBalance: Double = 0.0, // For in-app payments
    val isActive: Boolean = true, // Whether account is active
    val role: String = "passenger", // Fixed role
    val lastLogin: Long = 0L,
    val favoriteRoutes: List<String> = emptyList(),
    val lastTripTime: Long = 0L,
    val tripHistoryCount: Int = 0

)
