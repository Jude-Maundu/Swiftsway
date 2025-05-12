package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(navController: NavController) {
    val context = LocalContext.current
    var destination by remember { mutableStateOf("") }
    var seatPreference by remember { mutableStateOf("") }
    var passengerCount by remember { mutableStateOf("") }

    // Basic fare calculation: KSh 150 per seat
    fun calculateFare(): Double {
        return 150.0 * (passengerCount.toIntOrNull() ?: 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Book a Seat", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = "Destination") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = seatPreference,
                    onValueChange = { seatPreference = it },
                    label = { Text("Seat Preference") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Chair, contentDescription = "Seat Preference") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = passengerCount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) passengerCount = it },
                    label = { Text("Number of Passengers") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Display fare
                Spacer(modifier = Modifier.height(16.dp))
                Text("Fare: KSh ${calculateFare()}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (destination.isBlank()) {
                            Toast.makeText(context, "Destination is required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (seatPreference.isBlank() || passengerCount.isBlank()) {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val bookingDetails = "Booking $passengerCount seats to $destination with preference: $seatPreference"
                        Toast.makeText(context, "Booking Confirmed: $bookingDetails", Toast.LENGTH_LONG).show()

                        // You may replace this with Firebase or actual backend integration.
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Book Now")
                }
            }
        }
    )
}

@Preview
@Composable
private fun BookingPreview() {
    BookingScreen(navController = rememberNavController())
}
