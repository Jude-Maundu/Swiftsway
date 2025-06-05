package com.judemaundu.swiftsway2.ui.theme.Screens.Register

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_LOGIN

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("passenger") } // Default role
    val roles = listOf("passenger", "driver", "conductor")
    var loading by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val animatedGlowColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF8E2DE2),
        targetValue = Color(0xFF4A00E0),
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animated border"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, animatedGlowColor),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Register", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("Select Role", style = MaterialTheme.typography.labelLarge)

                Spacer(modifier = Modifier.height(8.dp))

                roles.forEach { role ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (selectedRole == role),
                            onClick = { selectedRole = role }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = role.replaceFirstChar { it.uppercase() })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        loading = true
                        registerUser(fullName, email, password, selectedRole, context) { success ->
                            loading = false
                            if (success) {
                                Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(ROUTE_LOGIN)
                            } else {
                                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                ) {
                    Text(if (loading) "Registering..." else "Register")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate(ROUTE_LOGIN) }) {
                    Text("Already have an account? Login")
                }
            }
        }
    }
}

// 🔧 Register Function
fun registerUser(
    fullName: String,
    email: String,
    password: String,
    role: String,
    context: Context,
    onResult: (Boolean) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                val userMap = mapOf(
                    "uid" to uid,
                    "fullName" to fullName,
                    "email" to email,
                    "role" to role,
                    "profileImageUrl" to "",
                    "isVerified" to false
                )
                database.child("users").child(uid).setValue(userMap)
                    .addOnCompleteListener { dbTask ->
                        onResult(dbTask.isSuccessful)
                    }
            } else {
                onResult(false)
            }
        }
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}
