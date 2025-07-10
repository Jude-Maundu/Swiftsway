package com.judemaundu.swiftsway2.ui.theme.Screens.Users.Admin

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import android.os.Environment
import com.google.firebase.auth.FirebaseAuth
import com.judemaundu.swiftsway2.ui.theme.Data.Admin.AdminViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.filter


class AdminPanelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AdminTabbedScreen(navController = rememberNavController())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTabbedScreen(viewModel: AdminViewModel = viewModel(), navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedMenuItem by remember { mutableStateOf("Dashboard") }

    val menuItems = listOf("Dashboard", "Users", "Vehicles", "Payments", "Feedback", "Settings")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Admin Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )

                menuItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item) },
                        selected = selectedMenuItem == item,
                        onClick = {
                            selectedMenuItem = item
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )



                }

                Spacer(modifier = Modifier.weight(1f))

                Divider()
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        // TODO: Add your logout logic here
                         FirebaseAuth.getInstance().signOut()
                         navController.navigate("Login")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selectedMenuItem) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                when (selectedMenuItem) {
                    "Dashboard" -> DashboardSection(viewModel)
                    "Users" -> ViewAllUsersScreen(viewModel)
                    "Vehicles" -> VehicleManagementScreen(viewModel)
                    "Payments" -> PaymentMonitoringScreen(viewModel)
                    "Feedback" -> FeedbackSection(viewModel)
                    "Settings" -> SettingsScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun DashboardSection(viewModel: AdminViewModel) {
    val users by viewModel.users.collectAsState()
    val vehicles by viewModel.vehicles.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val feedbacks by viewModel.feedbacks.collectAsState()

    val passengerCount = users.count { it.role == "Passenger" }
    val driverCount = users.count { it.role == "Driver" }
    val conductorCount = users.count { it.role == "Conductor" }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Admin Dashboard Overview", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Total Users: ${users.size}")
        Text("Passengers: $passengerCount")
        Text("Drivers: $driverCount")
        Text("Conductors: $conductorCount")
        Text("Total Payments: ${payments.size}")
        Text("Total Vehicles: ${vehicles.size}")
        Text("Feedback Count: ${feedbacks.size}")
    }
}

class AdminViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<AllUsers>>(emptyList())
    val users: StateFlow<List<AllUsers>> = _users.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    init {
        db.collection("users")  // Replace with your actual Firestore collection name
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val userList = snapshot.documents.mapNotNull { it.toObject(AllUsers::class.java) }
                _users.value = userList
            }
    }
}

annotation class AllUsers

// ===================== COMPOSABLE SCREEN =====================
@Composable
fun ViewAllUsersScreen(viewModel: AdminViewModel = viewModel()) {
    val users by viewModel.users.collectAsState()
    var selectedRole by remember { mutableStateOf("All") }
    val roles = listOf("All", "Passenger", "Driver", "Conductor")
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("View All Users", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        var expanded by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Filter by Role: $selectedRole")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search by name or email") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            exportToCSV(context, users, selectedRole, searchQuery.text)
        }) {
            Icon(Icons.Default.Download, contentDescription = "Export")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export CSV")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredUsers = users.filter {
            (selectedRole == "All" || it.role.equals(selectedRole, ignoreCase = true)) &&
                    (it.fullName.contains(searchQuery.text, ignoreCase = true) ||
                            it.email.contains(searchQuery.text, ignoreCase = true))
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredUsers) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${user.fullName}", fontSize = 16.sp)
                        Text("Email: ${user.email}", fontSize = 14.sp)
                        Text("Role: ${user.role}", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

fun exportToCSV(
    context: Context,
    users: List<com.google.firebase.firestore.auth.Users>,
    selectedRole: String,
    searchQuery: String
) {
    val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    if (exportDir != null) {
        val file = File(exportDir, "users.csv")
        val writer = FileWriter(file)
        writer.append("Name,Email,Role\n")
        val filteredusers = users.filter {
            (selectedRole == "All" || it.role.equals(selectedRole, ignoreCase = true)) &&
                    (it.fullName.contains(searchQuery, ignoreCase = true) ||
                            it.email.contains(searchQuery, ignoreCase = true))
        }
        for (users in filteredusers) {
            writer.append("${users.fullName},${users.email},${users.role}\n")
        }
        writer.flush()
        writer.close()
    }
}



@Composable
fun VehicleManagementScreen(viewModel: AdminViewModel) {
    val vehicles by viewModel.vehicles.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Vehicle Management", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vehicles) { vehicle ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
//                        //Customize vehicle details here
                        Text("Plate: ${vehicle.plateNumber}")
                        Text("Status: ${vehicle.status}")
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMonitoringScreen(viewModel: AdminViewModel) {
    val payments by viewModel.payments.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Payment Monitoring", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(payments) { payment ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("User: ${payment.userName}")
                        Text("Amount: ${payment.amount}")
                        Text("Date: ${payment.date}")
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackSection(viewModel: AdminViewModel) {
    val feedbacks by viewModel.feedbacks.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Feedback Section", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(feedbacks) { feedback ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("User: ${feedback.userName}")
                        Text("Comment: ${feedback.comment}")
                        Text("Date: ${feedback.date}")
                    }
                }
            }
        }
    }
}


@Composable
fun SettingsScreen(viewModel: AdminViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Text("Manage system preferences and configurations.")
    }
}

@Preview
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                when (selectedMenuItem) {
                    "Dashboard" -> DashboardSection(viewModel)
                    "Users" -> ViewAllUsersScreen(viewModel)
                    "Vehicles" -> VehicleManagementScreen(viewModel)
                    "Payments" -> PaymentMonitoringScreen(viewModel)
                    "Feedback" -> FeedbackSection(viewModel)
                    "Settings" -> SettingsScreen(viewModel)
@Composable
private fun AdminPanelPreview() {
    AdminTabbedScreen(navController = rememberNavController())
}
