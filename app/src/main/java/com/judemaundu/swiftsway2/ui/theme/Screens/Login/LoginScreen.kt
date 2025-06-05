package com.judemaundu.swiftsway2.ui.theme.Screens.Login

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.judemaundu.swiftsway2.ui.theme.Navigation.*
import kotlin.random.Random

@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        ParticleBackground()

        Card(
            modifier = Modifier
                .padding(20.dp)
                .shadow(30.dp, shape = RoundedCornerShape(25.dp))
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF03A9F4).copy(alpha = glowAlpha),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.maxDimension
                        ),
                        radius = size.maxDimension,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }
                .fillMaxWidth(0.92f),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login to SwiftSway",
                    fontSize = 26.sp,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    placeholder = { Text("Enter username") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF03A9F4),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF03A9F4),
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    placeholder = { Text("Enter password") },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF03A9F4),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF03A9F4),
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        val userTrim = username.trim()
                        val passTrim = password.trim()

                        if (userTrim.isEmpty() || passTrim.isEmpty()) {
                            Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true

                        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
                        val auth = FirebaseAuth.getInstance()

                        dbRef.orderByChild("username").equalTo(userTrim).get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    val user = snapshot.children.first()
                                    val email = user.child("email").value.toString()
                                    val role = user.child("role").value.toString().lowercase()

                                    auth.signInWithEmailAndPassword(email, passTrim)
                                        .addOnSuccessListener {
                                            isLoading = false

                                            if (role == "admin" && email != "judemaundu001@gmail.com") {
                                                Toast.makeText(
                                                    context,
                                                    "Unauthorized admin login",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                auth.signOut()
                                                return@addOnSuccessListener
                                            }

                                            when (role) {
                                                "passenger" -> navController.navigate(ROUTE_PASSENGER)
                                                "driver" -> navController.navigate(ROUTE_DRIVER)
                                                "conductor" -> navController.navigate(ROUTE_CONDUCTOR)
                                                "admin" -> navController.navigate(ROUTE_ADMIN_PANEL)
                                                else -> Toast.makeText(
                                                    context,
                                                    "Unknown role",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Login failed: ${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    isLoading = false
                                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(context, "DB error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(40),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Login", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate(ROUTE_REGISTER) }) {
                    Text("Don't have an account? Register", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ParticleBackground() {
    val particles = remember {
        List(20) {
            Particle(
                offset = Offset(Random.nextInt(0, 500).toFloat(), Random.nextInt(0, 1200).toFloat()),
                radius = Random.nextInt(2, 6).toFloat(),
                speed = Random.nextDouble(0.5, 2.0).toFloat()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val offsetAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = it.radius,
                center = Offset(it.offset.x, (it.offset.y + offsetAnim.value) % size.height)
            )
        }
    }
}

data class Particle(val offset: Offset, val radius: Float, val speed: Float)

@Preview(showSystemUi = true)
@Composable
fun LoginPreview() {
    LoginScreen(rememberNavController())
}
