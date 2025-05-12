package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.judemaundu.swiftsway2.ui.theme.Data.Map.UberMapScreen
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_CONDUCTOR_BOOKING
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_CONDUCTOR_PAYMENT
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_DRIVER_SETTINGS
import kotlinx.coroutines.launch

enum class BottomNavItem(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Map("Map", Icons.Default.LocationOn),
    Requests("Requests", Icons.Default.Inbox),
    Profile("Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConductorDashboardScreen(navController: NavHostController) {
    var selectedIndex by remember { mutableStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var conductorLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var locationReady by remember { mutableStateOf(false) }  // Track if location is ready

    // Seat state variables
    var seatsOccupied by remember { mutableStateOf(0) }
    var seatsVacant by remember { mutableStateOf(14) } // Total seats: adjust as needed

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission request in real app
            return@LaunchedEffect
        }

        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                conductorLocation = LatLng(loc.latitude, loc.longitude)
                locationReady = true // Location is now ready
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
                Text(
                    "Menu",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        navController.navigate(ROUTE_DRIVER_SETTINGS)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text(ROUTE_CONDUCTOR_PAYMENT) },
                    selected = false,
                    onClick = {
                        navController.navigate(ROUTE_CONDUCTOR_PAYMENT)
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Booking Verification") },
                    selected = false,
                    onClick = {
                        navController.navigate(ROUTE_CONDUCTOR_BOOKING)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Conductor Dashboard") },
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
                    BottomNavItem.entries.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (selectedIndex) {
                    1 -> {
                        if (locationReady) {
                            UberMapScreen(conductorLocation)
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    2 -> RequestsScreen(navController)
                    3 -> ProfileScreen(navController)
                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text("Welcome to the Conductor Dashboard", style = MaterialTheme.typography.headlineSmall)
                            Spacer(modifier = Modifier.height(24.dp))

                            Text("Seats Occupied: $seatsOccupied", style = MaterialTheme.typography.titleMedium)
                            Text("Seats Vacant: $seatsVacant", style = MaterialTheme.typography.titleMedium)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        if (seatsVacant > 0) {
                                            seatsOccupied += 1
                                            seatsVacant -= 1
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Seat")
                                    Spacer(Modifier.width(8.dp))
                                    Text("Add Occupied")
                                }

                                Button(
                                    onClick = {
                                        if (seatsOccupied > 0) {
                                            seatsOccupied -= 1
                                            seatsVacant += 1
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Remove Seat")
                                    Spacer(Modifier.width(8.dp))
                                    Text("Remove Occupied")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConductorDashboardprev() {
    ConductorDashboardScreen(rememberNavController())
}
