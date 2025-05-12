package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun PaymentVerificationScreen(navController: NavController) {
    val dbRef = FirebaseDatabase.getInstance().getReference("payments")
    var paymentStatus by remember { mutableStateOf("No payment yet") }
    var isLoading by remember { mutableStateOf(true) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // Real-time listener for payment updates
    LaunchedEffect(userId) {
        dbRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if there's any payment data for the user
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {
                            val status = child.child("status").getValue(String::class.java)
                            paymentStatus = status ?: "Unknown"
                        }
                    } else {
                        paymentStatus = "No payment found"
                    }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    paymentStatus = "Error: ${error.message}"
                    isLoading = false
                }
            })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text("Payment Status: $paymentStatus", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = {
                // Optional: Trigger any manual refresh or retry logic here
            }) {
                Text("Refresh / Retry")
            }
        }
    }
}

@Preview
@Composable
private fun PaymentVerificationScreenPreview() {
    PaymentVerificationScreen(rememberNavController())
}
