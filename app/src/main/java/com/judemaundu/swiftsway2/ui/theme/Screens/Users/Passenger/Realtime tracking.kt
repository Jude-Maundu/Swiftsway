package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.database.*
import com.google.maps.android.compose.*

@Composable
fun VehicleTrackingScreen(navController: NavHostController) {
    val vehicleLocation = remember { mutableStateOf(LatLng(0.0, 0.0)) }

    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("vehicle_location")

    // Listen for changes in the vehicle's location in real-time
    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                val lng = snapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                vehicleLocation.value = LatLng(lat, lng)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors if needed
            }
        })
    }

    GoogleMapView(vehicleLocation = vehicleLocation.value)
}

@Composable
fun GoogleMapView(vehicleLocation: LatLng) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(vehicleLocation, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true)
    ) {
        Marker(
            state = rememberMarkerState(position = vehicleLocation),
            title = "Vehicle Location"
        )
    }
}

@Preview
@Composable
private fun TrackingPrev() {
    VehicleTrackingScreen(rememberNavController())
}
