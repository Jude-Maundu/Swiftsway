package com.judemaundu.swiftsway2.ui.theme.screens.users.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteManagementScreen(navController: NavController) {
    val dbRef = FirebaseDatabase.getInstance().getReference("routes")
    val routeList = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }
    var newRouteName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Firebase listener for route data
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
        topBar = { TopAppBar(title = { Text("Route Management") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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

            // Text field to enter a new route name
            OutlinedTextField(
                value = newRouteName,
                onValueChange = { newRouteName = it },
                label = { Text("Enter New Route Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Button to create a route
            Button(
                onClick = {
                    if (newRouteName.isNotBlank()) {
                        createNewRoute(dbRef, newRouteName.trim(), snackbarHostState)
                        newRouteName = ""
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            snackbarHostState.showSnackbar("Route name cannot be empty.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create New Route")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(routeList) { route ->
                        Text("• $route", modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

fun createNewRoute(
    dbRef: DatabaseReference,
    routeName: String,
    snackbarHostState: SnackbarHostState
) {
    val newRouteRef = dbRef.push()
    val route = mapOf("routeName" to routeName)

    newRouteRef.setValue(route).addOnCompleteListener { task ->
        CoroutineScope(Dispatchers.Main).launch {
            if (task.isSuccessful) {
                snackbarHostState.showSnackbar("Route \"$routeName\" created successfully!")
            } else {
                snackbarHostState.showSnackbar("Failed to create route.")
            }
        }
    }
}

@Preview
@Composable
private fun RouteManagementScreenPreview() {
    RouteManagementScreen(navController = rememberNavController())
}
