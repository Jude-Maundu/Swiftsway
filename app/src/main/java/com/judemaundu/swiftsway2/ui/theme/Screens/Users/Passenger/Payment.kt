package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController) {
    val context = LocalContext.current

    val paymentOptions = listOf("Pochi la Biashara", "Paybill", "Buy Goods and Services")
    var selectedPayment by remember { mutableStateOf(paymentOptions[0]) }
    var expanded by remember { mutableStateOf(false) }
    var transactionCode by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") } // 💵 New state variable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("MPESA Payment", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                // Dropdown for payment methods
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedPayment,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        paymentOptions.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    selectedPayment = method
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 💵 Amount field
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // MPESA transaction code input
                OutlinedTextField(
                    value = transactionCode,
                    onValueChange = { transactionCode = it },
                    label = { Text("MPESA Transaction Code") },
                    leadingIcon = { Icon(Icons.Default.Payment, contentDescription = "Transaction Code") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Confirm Payment Button
                Button(
                    onClick = {
                        if (transactionCode.isNotBlank() && amount.isNotBlank()) {
                            sendPaymentToConductor(selectedPayment, transactionCode, amount, context)
                        } else {
                            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Payment")
                }
            }
        }
    )
}

// ✅ Updated to include 'amount'
fun sendPaymentToConductor(
    paymentMethod: String,
    transactionCode: String,
    amount: String,
    context: android.content.Context
) {
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("Payments").push()

    val paymentData = mapOf(
        "paymentMethod" to paymentMethod,
        "transactionCode" to transactionCode,
        "amount" to amount,
        "timestamp" to System.currentTimeMillis()
    )

    ref.setValue(paymentData)
        .addOnSuccessListener {
            Toast.makeText(context, "Payment slip sent for verification", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
        }
}

@Preview
@Composable
private fun PaymentScreenPreview() {
    PaymentScreen(rememberNavController())

    
}