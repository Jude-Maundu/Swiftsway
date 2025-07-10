package com.judemaundu.swiftsway2.ui.theme.Data.AllUsers
data class users(
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val role: String = "",
    val location: String = "",
    val joinedDate: String = "",

    val driverLicenseNumber: String = "",
    val vehicleRegistration: String = "",
    val vehicleType: String = "",
    val routeAssigned: String = "",
    val driverStatus: String = "",

    val staffId: String = "",
    val assignedBusId: String = "",
    val shiftTiming: String = "",
    val ticketsHandled: Int = 0,

    val bookingHistory: List<String> = emptyList(),
    val walletBalance: Double = 0.0,
    val preferredRoutes: List<String> = emptyList()
)


