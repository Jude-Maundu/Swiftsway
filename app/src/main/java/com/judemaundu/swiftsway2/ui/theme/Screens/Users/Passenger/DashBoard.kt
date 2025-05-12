package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_LOGIN
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_PASSENGER_SETTINGS
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor.ProfileScreen
import com.judemaundu.swiftsway2.ui.theme.Data.Map.UberMapScreen
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_PASSENGER_RIDE_HISTORY
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerDashboardScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableStateOf(0) }
    val navItems = PassengerNavItem.values()

    // ✅ Live location state
    var passengerLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val context = LocalContext.current



    // ✅ Location request
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission request in production
            return@LaunchedEffect
        }

        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                passengerLocation = LatLng(loc.latitude, loc.longitude)
            }
        }

        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text("Menu", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        navController.navigate(ROUTE_PASSENGER_SETTINGS)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Ride History") },
                    selected = false,
                    onClick = {
                        // Navigate to Ride History screen
                        navController.navigate("ride_history/${ROUTE_PASSENGER_RIDE_HISTORY}") // Replace with actual passengerId
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        navController.navigate(ROUTE_LOGIN)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Passenger Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedIndex) {
                    0 -> BookingScreen(navController)
                    1 -> PaymentScreen(navController)
                    2 -> UberMapScreen(passengerLocation) // ✅ Show live map
                    3 -> ProfileScreen(navController)
                }
            }
        }
    }
}

enum class PassengerNavItem(val icon: ImageVector, val label: String, val route: String) {
    BOOKING(Icons.Default.Book, "Bookings", "bookings"),
    PAYMENT(Icons.Default.Payment, "Payments", "payments"),
    MAP(Icons.Default.Map, "Map", "map"),
    PROFILE(Icons.Default.Person, "Profile", "profile")
}


@Preview
@Composable
private fun PassengerDashboardPreview() {
    PassengerDashboardScreen(rememberNavController())
}
