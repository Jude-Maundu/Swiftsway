package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestManagementScreen(navController: NavController) {
    val dbRef = FirebaseDatabase.getInstance().getReference("tripRequests")
    val requestList = remember { mutableStateListOf<TripRequest>() }
    var isLoading by remember { mutableStateOf(true) }

    // Real-time listener for requests
    LaunchedEffect(Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                requestList.clear()
                snapshot.children.mapNotNullTo(requestList) {
                    it.getValue(TripRequest::class.java)
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Management") }
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
                text = "Manage Requests.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Show a loading indicator while data is being fetched
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                // Display the trip requests in a list
                requestList.forEach { request ->
                    RequestItem(request, dbRef)
                }
            }
        }
    }
}

@Composable
fun RequestItem(request: TripRequest, dbRef: DatabaseReference) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("Passenger: ${request.passengerName}")
        Text("Route: ${request.route}")
        Text("Status: ${request.status}")

        Spacer(modifier = Modifier.height(8.dp))

        // Buttons to accept or reject a request
        Row {
            Button(
                onClick = {
                    acceptRequest(dbRef, request)
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Accept")
            }

            Button(onClick = {
                rejectRequest(dbRef, request)
            }) {
                Text("Reject")
            }
        }
    }
}

fun acceptRequest(dbRef: DatabaseReference, request: TripRequest) {
    val requestRef = dbRef.child(request.id)
    requestRef.child("status").setValue("Accepted")
    // Handle any other logic related to acceptance (e.g., notification to passenger)
}

fun rejectRequest(dbRef: DatabaseReference, request: TripRequest) {
    val requestRef = dbRef.child(request.id)
    requestRef.child("status").setValue("Rejected")
    // Handle any other logic related to rejection (e.g., notification to passenger)
}

// Data class to represent a trip request
data class TripRequest(
    val id: String = "",
    val passengerName: String = "",
    val route: String = "",
    val status: String = "" // Accepted, Rejected, Pending
)

@Preview
@Composable
private fun RequestManagementScreenPreview() {
    RequestManagementScreen(rememberNavController())
}
