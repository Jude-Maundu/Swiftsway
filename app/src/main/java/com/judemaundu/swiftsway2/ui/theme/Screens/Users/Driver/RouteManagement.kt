package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
fun RouteManagementScreen(navController: NavController) {
    val dbRef = FirebaseDatabase.getInstance().getReference("routes")
    val routeList = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch the routes in real-time
    LaunchedEffect(Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                routeList.clear()
                snapshot.children.mapNotNullTo(routeList) {
                    it.child("routeName").getValue(String::class.java)
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
                title = { Text("Route Management") }
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
                text = "Manage your assigned routes here.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to create a new route
            Button(onClick = {
                // Example route creation logic
                createNewRoute(dbRef)
            }) {
                Text("Create New Route")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show a loading indicator while fetching data
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                // Display list of routes
                LazyColumn {
//                    items(routeList) { route ->
//                        Text("• $route", modifier = Modifier.padding(8.dp))
//                    }
                }
            }
        }
    }
}

fun createNewRoute(dbRef: DatabaseReference) {
    val newRouteRef = dbRef.push() // Push creates a new unique key
    val route = mapOf(
        "routeName" to "New Route from A to B" // Sample route name, you can change this to dynamic input
    )

    // Add the new route to Firebase
    newRouteRef.setValue(route).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Success, handle accordingly (e.g., show a message or refresh the UI)
        } else {
            // Error handling
        }
    }
}

@Preview
@Composable
private fun RouteManagementScreenPreview() {
    RouteManagementScreen(navController = rememberNavController())
}
