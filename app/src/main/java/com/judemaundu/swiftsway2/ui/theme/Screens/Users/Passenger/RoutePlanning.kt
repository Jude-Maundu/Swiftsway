package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerTripPlanningScreen(navController: NavController) {
    val context = LocalContext.current
    var startPoint by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var tripDate by remember { mutableStateOf("") }
    var tripTime by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Plan a Trip",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = startPoint,
            onValueChange = { startPoint = it },
            label = { Text("Starting Point") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = destination,
            onValueChange = { destination = it },
            label = { Text("Destination") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tripDate,
            onValueChange = { tripDate = it },
            label = { Text("Date (e.g., 2025-05-07)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tripTime,
            onValueChange = { tripTime = it },
            label = { Text("Time (e.g., 14:00)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (startPoint.isNotEmpty() && destination.isNotEmpty()) {
                    Toast.makeText(
                        context,
                        "Trip planned from $startPoint to $destination at $tripTime on $tripDate",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Plan Trip")
        }
    }
}

@Preview
@Composable
private fun TripPlanningPrev() {
    PassengerTripPlanningScreen(rememberNavController())

}