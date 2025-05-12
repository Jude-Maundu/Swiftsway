package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*
import com.judemaundu.swiftsway2.ui.theme.Data.Ride
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideHistoryScreen(passengerId: String, navController: NavController) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().getReference("ride_history").child(passengerId)
    var rideList by remember { mutableStateOf(listOf<Ride>()) }

    val currentContext by rememberUpdatedState(context)

    // Add and remove the listener properly
    DisposableEffect(passengerId) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rides = mutableListOf<Ride>()
                for (rideSnapshot in snapshot.children) {
                    rideSnapshot.getValue(Ride::class.java)?.let { rides.add(it) }
                }
                rideList = rides.reversed() // show latest first
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(currentContext, "Failed to load ride history.", Toast.LENGTH_SHORT).show()
            }
        }

        database.addValueEventListener(listener)

        onDispose {
            database.removeEventListener(listener)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ride History") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(rideList) { ride ->
                RideCard(ride)
            }
        }
    }
}
@Composable
fun RideCard(ride: Ride) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Date: ${ride.date}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "From: ${ride.pickup}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "To: ${ride.dropoff}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fare: ${ride.fare}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: ${ride.status}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Preview
@Composable
private fun RideHistoryScreenPreview() {
    RideHistoryScreen( passengerId = "passengerId", navController = rememberNavController())

}