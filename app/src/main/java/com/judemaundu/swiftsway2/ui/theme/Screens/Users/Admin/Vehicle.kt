package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Admin
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun VehicleScreen(viewModel: VehicleViewModel = viewModel(),navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            "Live Vehicles",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Stats Row
        VehicleStats(viewModel)

        // Vehicle List
        VehicleList(viewModel)
    }
}

@Composable
fun VehicleStats(viewModel: VehicleViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatCard("Total", viewModel.vehicles.size.toString(), Icons.Default.DirectionsBus)
        StatCard("Drivers", viewModel.totalDrivers.toString(), Icons.Default.Person)
        StatCard("Passengers", viewModel.totalPassengersToday.toString(), Icons.Default.People)
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun VehicleList(viewModel: VehicleViewModel) {
    LazyColumn {
        items(viewModel.vehicles) { vehicle ->
            VehicleCard(vehicle)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Vehicle Number
            Text(
                vehicle.vehicleNumber,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Driver & Conductor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem("Driver", vehicle.driverName)
                InfoItem("Conductor", vehicle.conductorName)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Passengers
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Passengers: ")
                Text(
                    "${vehicle.passengersToday}",
                    fontWeight = FontWeight.Bold
                )
                Text("/${vehicle.capacity}")
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

// Simplified ViewModel
class VehicleViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val _vehicles = mutableStateListOf<Vehicle>()
    val vehicles: List<Vehicle> get() = _vehicles

    val totalDrivers get() = _vehicles.distinctBy { it.driverName }.size
    val totalPassengersToday get() = _vehicles.sumOf { it.passengersToday }

    init {
        db.collection("vehicles")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.toObjects(Vehicle::class.java)?.let {
                    _vehicles.clear()
                    _vehicles.addAll(it)
                }
            }
    }
}

// Simplified Data Model
data class Vehicle(
    val id: String = "",
    val vehicleNumber: String = "",
    val driverName: String = "",
    val conductorName: String = "",
    val passengersToday: Int = 0,
    val capacity: Int = 50
)

@Preview
@Composable
private fun VehicleScreenPreview() {
    VehicleScreen(navController =rememberNavController())


}