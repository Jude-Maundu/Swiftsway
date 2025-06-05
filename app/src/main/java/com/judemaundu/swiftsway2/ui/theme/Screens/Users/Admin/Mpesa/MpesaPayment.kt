package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Admin.Mpesa

import com.judemaundu.swiftsway2.ui.theme.Data.Mpesa.MpesaViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavIcon(
    icon: ImageVector,
    contentDesc: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = Color.White
        )
    }
}

@Composable
fun MpesaPaymentScreen(
    navController: NavHostController,
    viewModel: MpesaViewModel = viewModel()
) {
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var paymentSuccess by remember { mutableStateOf<Boolean?>(null) }

    val lightLemonGreen = Color(0xFFE4F4D7)
    val lemonPrimary = Color(0xFF424441)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(lightLemonGreen)
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = lemonPrimary.copy(alpha = 0.05f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    " Pay Service",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = lemonPrimary
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (2547XXXXXXXX)", color = lemonPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount", color = lemonPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (phone.isBlank() || amount.isBlank()) {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        } else {
                            isProcessing = true
                            viewModel.initiatePayment(phone, amount) { success ->
                                isProcessing = false
                                paymentSuccess = success
                            }
                        }
                    },
                    enabled = phone.isNotBlank() && amount.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lemonPrimary,
                        contentColor = Color.White
                    )
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Pay Now")
                    }
                }

                paymentSuccess?.let { success ->
                    val message = if (success) " Payment Request Sent!" else " Payment Failed. Try Again."
                    val color = if (success) Color(0xFFFCFDFC) else Color(0xFF5E5B5B)
                    Text(
                        text = message,
                        color = color,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Bottom Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(lemonPrimary)
                .padding(vertical = 8.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
//            BottomNavIcon(icon = Icons.Default.Home, contentDesc = "Home") {
//                navController.navigate(CLIENT_HOME) {
//                    popUpTo(CLIENT_HOME) { inclusive = true }
//                }
//           }
//            BottomNavIcon(icon = Icons.Default.AddCircle, contentDesc = "Post Job") {
//                navController.navigate(JOB_POST)
//            }
//            BottomNavIcon(icon = Icons.Default.Search, contentDesc = "Search Cleaners") {
//                navController.navigate(CLIENT_SEARCH)
//            }
//            BottomNavIcon(icon = Icons.Default.ThumbUp, contentDesc = "Pay Cleaners") {
//                navController.navigate(ROUTE_ADMIN_MPESA)
//            }
//            BottomNavIcon(icon = Icons.Default.Person, contentDesc = "Profile") {
//                navController.navigate(CLIENT_PROFILE)
//            }
        }
    }
}