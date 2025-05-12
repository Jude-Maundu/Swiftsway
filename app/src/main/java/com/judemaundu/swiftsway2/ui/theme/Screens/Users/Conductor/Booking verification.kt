package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.judemaundu.swiftsway2.ui.theme.Data.Booking
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

// Main Composable Screen for Conductor to verify bookings
@Composable
fun BookingVerificationScreen(conductorRouteId: String, navController: NavController) {
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val firestore = FirebaseFirestore.getInstance()

    // Real-time listener for bookings on the conductor's route
    LaunchedEffect(conductorRouteId) {
        firestore.collection("bookings")
            .whereEqualTo("routeId", conductorRouteId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    isLoading = false
                    Toast.makeText(navController.context, "Error fetching bookings", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    bookings = snapshot.documents.mapNotNull { it.toObject(Booking::class.java) }
                    isLoading = false
                } else {
                    bookings = emptyList()
                    isLoading = false
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (bookings.isNotEmpty()) {
            LazyColumn {
                items(bookings.size) { index ->
                    val booking = bookings[index]
                    BookingVerificationItem(booking = booking, navController = navController)
                }
            }
        } else {
            Text("No bookings found to verify.")
        }
    }
}

@Composable
fun BookingVerificationItem(booking: Booking, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    var isVerifying by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Booking ID: ${booking.bookingId}")
            Text("Passenger ID: ${booking.passengerId}")
            Text("Status: ${booking.status}")
            Text("Payment: ${booking.paymentStatus}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isVerifying = true
                        try {
                            firestore.collection("bookings")
                                .document(booking.bookingId)
                                .update("status", "Verified")
                                .await()
                            Toast.makeText(navController.context, "Booking Verified", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(navController.context, "Verification Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isVerifying = false
                        }
                    }
                },
                enabled = !isVerifying && booking.status != "Verified"
            ) {
                Text(if (booking.status == "Verified") "Already Verified" else "Verify Booking")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingVerificationScreenPreview() {
    val navController = rememberNavController()
    BookingVerificationScreen(
        conductorRouteId = "route001",
        navController = navController
    )
}
