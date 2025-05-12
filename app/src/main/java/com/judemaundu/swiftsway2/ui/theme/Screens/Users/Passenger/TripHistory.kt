package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

data class Trip(
    val date: String = "",
    val from: String = "",
    val to: String = "",
    val fare: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerTripHistoryScreen(navController: NavHostController) {
    val trips = remember { mutableStateListOf<Trip>() }
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        user?.uid?.let { uid ->
            val dbRef = FirebaseDatabase.getInstance().getReference("passenger_trips").child(uid)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trips.clear()
                    for (child in snapshot.children) {
                        val trip = child.getValue(Trip::class.java)
                        trip?.let { trips.add(it) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Trip History") })
        }
    ) { padding ->
        if (trips.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No trip history found.")
            }
        } else {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                items(trips) { trip ->
                    TripCard(trip)
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Date: ${trip.date}", style = MaterialTheme.typography.bodyLarge)
            Text("From: ${trip.from}", style = MaterialTheme.typography.bodyMedium)
            Text("To: ${trip.to}", style = MaterialTheme.typography.bodyMedium)
            Text("Fare: ${trip.fare}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview
@Composable
private fun PassengerTripHistoryScreenPrev() {
    PassengerTripHistoryScreen(rememberNavController())

}