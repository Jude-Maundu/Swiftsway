package com.judemaundu.swiftsway2.ui.theme.Data

import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

fun registerBookingAndSendSlip(
    destination: String,
    seatPreference: String,
    paymentMethod: String,
    transactionCode: String,
    context: android.content.Context
) {
    val database = FirebaseDatabase.getInstance()
    val bookingRef = database.getReference("bookings").push()
    val conductorPaymentRef = database.getReference("conductor_verifications").push()

    val bookingData = mapOf(
        "id" to bookingRef.key,
        "destination" to destination,
        "seatPreference" to seatPreference,
        "paymentMethod" to paymentMethod,
        "transactionCode" to transactionCode,
        "timestamp" to System.currentTimeMillis()
    )

    // Write to bookings node
    bookingRef.setValue(bookingData)
        .addOnSuccessListener {
            // Also send slip to conductor's verification queue
            conductorPaymentRef.setValue(bookingData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Booking and payment slip submitted!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Slip send failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Booking failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
}
