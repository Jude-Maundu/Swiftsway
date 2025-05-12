package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleStatusScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vehicle Status") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Check your vehicle's current status.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Example: Show status of vehicle
            Text("• Vehicle: Bus 101")
            Text("• Status: Available")
            Text("• Fuel: 50%")
            // Add more vehicle details here
        }
    }
}

@Preview
@Composable
private fun Statusprev() {
    VehicleStatusScreen(rememberNavController())

}