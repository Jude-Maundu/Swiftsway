package com.judemaundu.swiftsway2.ui.theme.Data.Admin

data class Admin(
    val adminId: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "admin", // Default role
    val profileImageUrl: String = "", // URL to Firebase Storage or other
    val isActive: Boolean = true // Whether the admin account is active
)
