package com.judemaundu.swiftsway2.ui.theme.Data.FeedBack

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Feedback(
    val userName: String = "",
    val email: String = "",
    val comment: String = "",
    val feedbackId: String = "",
    val userId: String = "", // ID of the user (passenger, driver, etc.)
    val userRole: String = "", // "passenger", "driver", "conductor", etc.
    val message: String = "",
    val rating: Int = 0, // Optional: 1 to 5 stars
    val timestamp: Long = System.currentTimeMillis(),
    val tripId: String = "", // If the feedback is about a specific trip
    val resolved: Boolean = false,// Whether the admin has addressed it
    val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val time: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val id: String


)

