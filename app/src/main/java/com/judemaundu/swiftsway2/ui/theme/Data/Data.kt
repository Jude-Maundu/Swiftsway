package com.judemaundu.swiftsway2.ui.theme.Data

data class TripDetails(
    val origin: String,
    val destination: String,
    val time: String,
    val vehicleId: String,
    val passengerCount: Int
)

data class Trip(
    val date: String = "",
    val from: String = "",
    val to: String = "",
    val fare: String = ""
)
data class Ride(
    val date: String = "",
    val pickup: String = "",
    val dropoff: String = "",
    val fare: String = "",
    val status: String = ""
)


data class Booking(
    val bookingId: String = "",
    val passengerId: String = "",
    val id: String = "",
    val userId: String = "",
    val tripId: String = "",
    val status: String = "",  // "pending", "confirmed", "canceled"
    val paymentStatus: String = "",  // "paid", "unpaid"
    val timestamp: Long = System.currentTimeMillis()
)
