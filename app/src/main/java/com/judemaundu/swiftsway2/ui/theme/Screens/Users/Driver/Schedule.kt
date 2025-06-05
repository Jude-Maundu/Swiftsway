package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavController) {
    val dbRef = FirebaseDatabase.getInstance().getReference("driverSchedules")
    val scheduleList = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Fetch schedule list in real-time
    LaunchedEffect(Unit) {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scheduleList.clear()
                snapshot.children.mapNotNullTo(scheduleList) {
                    it.child("scheduleDetails").getValue(String::class.java)
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                Toast.makeText(context, "Failed to load schedules.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule") }
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
                text = "View and manage your driving schedule.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(scheduleList) { schedule ->
                        Text("• $schedule", modifier = Modifier.padding(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    addNewSchedule(dbRef, context)
                }) {
                    Text("Add New Schedule")
                }
            }
        }
    }
}

// Function to add a new dummy schedule (you can replace it with user input)
fun addNewSchedule(dbRef: DatabaseReference, context: android.content.Context) {
    val newScheduleRef = dbRef.push()
    val schedule = mapOf(
        "scheduleDetails" to "Wednesday: 9:00 AM - 1:00 PM - Route 3"
    )

    newScheduleRef.setValue(schedule).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Toast.makeText(context, "Schedule Added!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error adding schedule.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview
@Composable
private fun ScheduleScreenPreview() {
    ScheduleScreen(navController = rememberNavController())
}
