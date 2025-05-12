package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun RequestsScreen(navController: NavController) {
    val dbRef = FirebaseDatabase.getInstance().getReference("tripRequests")
    val requestList = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Real-time listener for trip requests related to the logged-in user
    LaunchedEffect(userId) {
        dbRef.orderByChild("passengerId").equalTo(userId) // Assuming each trip request has a "passengerId"
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    requestList.clear()
                    snapshot.children.mapNotNullTo(requestList) {
                        it.child("passengerName").getValue(String::class.java)
                    }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                }
            })
    }

    // UI components
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(requestList) { name ->
                    Text("Request from: $name", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun RequestHandlingScreenPreview() {
    RequestsScreen(rememberNavController())
}
