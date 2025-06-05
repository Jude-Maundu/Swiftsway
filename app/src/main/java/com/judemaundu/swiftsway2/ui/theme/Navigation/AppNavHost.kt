package com.judemaundu.swiftsway2.ui.theme.Navigation



import ProfilesScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.judemaundu.swiftsway2.ui.theme.Data.UserId.UserListScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Login.LoginScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Register.RegisterScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Admin.AdminTabbedScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Admin.ContentManagementScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor.BookingVerificationScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor.ConductorDashboardScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor.ConductorSettingsScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor.PaymentVerificationScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor.ProfileScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Conductor.RequestsScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver.DriverDashboardScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver.DriverSettingsScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver.RequestManagementScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver.ScheduleScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Driver.VehicleStatusScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.BookingScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.PassengerDashboardScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.PassengerProfileScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.PassengerSettingsScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.PassengerTripHistoryScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.PassengerTripPlanningScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.PaymentScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.RideHistoryScreen
import com.judemaundu.swiftsway2.ui.theme.Screens.Users.Passenger.VehicleTrackingScreen
import com.judemaundu.swiftsway2.ui.theme.screens.users.driver.RouteManagementScreen


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_PASSENGER_DASHBOARD
) {
    NavHost(navController = navController, modifier = modifier, startDestination = startDestination) {
        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(navController)
        }

        // Passenger screens
        composable(ROUTE_PASSENGER_DASHBOARD) { PassengerDashboardScreen(navController) }
        composable(ROUTE_PASSENGER_BOOKING) { BookingScreen(navController) }
        composable(ROUTE_PASSENGER_PAYMENT) { PaymentScreen(navController) }
        composable(ROUTE_PASSENGER_PROFILE) { PassengerProfileScreen(navController) }
        composable(ROUTE_PASSENGER_REALTIME_TRACKING) { VehicleTrackingScreen(navController) }
        composable(ROUTE_PASSENGER_RIDE_HISTORY) { RideHistoryScreen("passengerId", navController) }
        composable(ROUTE_PASSENGER_ROUTE_PLANNING) { PassengerTripPlanningScreen(navController) }
        composable(ROUTE_PASSENGER_SETTINGS) { PassengerSettingsScreen(navController) }
        composable(ROUTE_PASSENGER_TRIP_HISTORY) { PassengerTripHistoryScreen(navController) }
        composable(ROUTE_PASSENGER_DASHBOARD) { PassengerDashboardScreen(navController) }

        // Driver screens
        composable(ROUTE_DRIVER_DASHBOARD) { DriverDashboardScreen(navController) }
        composable(ROUTE_DRIVER_PROFILE) { ProfilesScreen(navController) }
        composable(ROUTE_DRIVER_SETTINGS) { DriverSettingsScreen(navController) }
        composable(ROUTE_DRIVER_REQUEST_MANAGEMENT) { RequestManagementScreen(navController) }
        composable(ROUTE_DRIVER_ROUTE_MANAGEMENT) { RouteManagementScreen(navController) }
        composable(ROUTE_DRIVER_SCHEDULE) { ScheduleScreen(navController) }
        composable(ROUTE_DRIVER_VEHICLE_STATUS) { VehicleStatusScreen(navController) }

        // Conductor screens
        composable(ROUTE_CONDUCTOR_DASHBOARD) { ConductorDashboardScreen(navController) }
        composable(ROUTE_CONDUCTOR_PROFILE) { ProfileScreen(navController) }
        composable(ROUTE_CONDUCTOR_SETTINGS) { ConductorSettingsScreen(navController) }
        composable(ROUTE_CONDUCTOR_BOOKING) {
            BookingVerificationScreen(
                "conductorRouteId",
                navController
            )
        }
        composable(ROUTE_CONDUCTOR_PAYMENT) { PaymentVerificationScreen(navController) }
        composable(ROUTE_CONDUCTOR_REQUESTS) { RequestsScreen(navController) }
        // Admin screens
        composable(ROUTE_ADMIN_PANEL) {AdminTabbedScreen(navController = navController) }
        composable(ROUTE_USER_INPUT) { UserListScreen (navController = navController) }
        composable(ROUTE_USER_MANAGEMENT) { UserListScreen (navController = navController) }
        composable(ROUTE_CONTENT_MANAGEMENT) { ContentManagementScreen(navController = navController) }
        composable(ROUTE_DATA_MANAGEMENT) { ContentManagementScreen(navController = navController) }
        composable(ROUTE_VEHICLE_MANAGEMENT) { ContentManagementScreen(navController = navController) }
    }

}




