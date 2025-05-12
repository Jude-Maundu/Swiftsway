package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver

import android.content.pm.PackageManager
import android.os.Looper
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.judemaundu.swiftsway2.ui.theme.Data.Map.UberMapScreen
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_DRIVER_DASHBOARD
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_DRIVER_PROFILE
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_DRIVER_REQUEST_MANAGEMENT
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_DRIVER_ROUTE_MANAGEMENT
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_DRIVER_SCHEDULE
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_DRIVER_VEHICLE_STATUS
import com.judemaundu.swiftsway2.ui.theme.Navigation.ROUTE_LOGIN
import kotlinx.coroutines.launch
import android.Manifest

// Driver Bottom Navigation Items
enum class DriverBottomNavItem(val label: String, val icon: ImageVector, val route: String) {
    Home("Home", Icons.Default.Home, ROUTE_DRIVER_DASHBOARD),
    Profile("Profile", Icons.Default.Person, ROUTE_DRIVER_PROFILE),
    Requests("Requests", Icons.Default.Receipt, ROUTE_DRIVER_REQUEST_MANAGEMENT),
    Routes("Routes", Icons.Default.Map, ROUTE_DRIVER_ROUTE_MANAGEMENT),
    Schedule("Schedule", Icons.Default.Schedule, ROUTE_DRIVER_SCHEDULE)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(navController: NavHostController) {
    var selectedIndex by remember { mutableStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var driverLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    // Live location update logic
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // You may want to show rationale or request permission
            return@LaunchedEffect
        }

        val locationClient = LocationServices.getFusedLocationProviderClient(context)

        // Create LocationRequest correctly
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                driverLocation = LatLng(loc.latitude, loc.longitude)
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
                Text("Menu", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                Divider()
                NavigationDrawerItem(label = { Text("Settings") }, selected = false, onClick = {
                    navController.navigate("DriverSettingsScreen")
                    scope.launch { drawerState.close() }
                })
                NavigationDrawerItem(label = { Text("Vehicle Status") }, selected = false, onClick = {
                    navController.navigate(ROUTE_DRIVER_VEHICLE_STATUS)
                    scope.launch { drawerState.close() }
                })
                NavigationDrawerItem(label = { Text("Logout") }, selected = false, onClick = {
                    navController.navigate(ROUTE_LOGIN) { popUpTo(0) }
                    scope.launch { drawerState.close() }
                })
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Driver Dashboard") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    DriverBottomNavItem.entries.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedIndex == index,
                            onClick = {
                                selectedIndex = index
                                navController.navigate(item.route)
                            }
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                if (selectedIndex == 0) {
                    UberMapScreen(driverLocation)
                } else {
                    // You can load other content for other tabs here
                    Text(
                        "Section: ${DriverBottomNavItem.entries[selectedIndex].label}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DriverDashboardprev() {
    DriverDashboardScreen(rememberNavController())
}
