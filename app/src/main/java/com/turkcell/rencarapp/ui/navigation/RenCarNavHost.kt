package com.turkcell.rencarapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.turkcell.rencarapp.ui.screens.LicenseVerificationScreen
import com.turkcell.rencarapp.ui.screens.LoginScreen
import com.turkcell.rencarapp.ui.screens.VerifyScreen
import com.turkcell.rencarapp.ui.screens.WelcomeScreen
import com.turkcell.rencarapp.ui.screens.RegisterScreen
import com.turkcell.rencarapp.ui.screens.MainDashboardScreen
import com.turkcell.rencarapp.ui.screens.SelfieVerificationScreen
import com.turkcell.rencarapp.ui.screens.VehiclePhotoUploadScreen
import com.turkcell.rencarapp.ui.screens.ReservationApprovalScreen
import com.turkcell.rencarapp.ui.viewmodel.AuthViewModel
import com.turkcell.rencarapp.ui.viewmodel.LicenseViewModel
import com.turkcell.rencarapp.ui.viewmodel.ReservationViewModel

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Verify : Screen("verify/{phoneNumber}") {
        fun createRoute(phoneNumber: String) = "verify/$phoneNumber"
    }
    object LicenseVerification : Screen("license_verification")
    object SelfieVerification : Screen("selfie_verification")
    object MainDashboard : Screen("main_dashboard")
    object VehiclePhotoUpload : Screen("vehicle_photo_upload/{vehicleId}") {
        fun createRoute(vehicleId: String) = "vehicle_photo_upload/$vehicleId"
    }
    object ReservationApproval : Screen("reservation_approval/{vehicleId}") {
        fun createRoute(vehicleId: String) = "reservation_approval/$vehicleId"
    }
    object PaymentSummary : Screen("payment_summary/{rentalId}") {
        fun createRoute(rentalId: String) = "payment_summary/$rentalId"
    }
}

@Composable
fun RenCarNavHost(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState = authViewModel.authState.collectAsState().value
    val licenseViewModel: LicenseViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                isDarkTheme = isDarkTheme
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    authViewModel.resetState()
                    navController.navigate(Screen.LicenseVerification.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onCodeSent = { phoneNumber ->
                    authViewModel.resetState()
                    navController.navigate(Screen.Verify.createRoute(phoneNumber))
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                }
            )
        }
        composable(
            route = Screen.Verify.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            VerifyScreen(
                phoneNumber = phoneNumber,
                onBackClick = { navController.popBackStack() },
                onChangeNumberClick = {
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                },
                onVerifySuccess = {
                    authViewModel.resetState()
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.MainDashboard.route) {
            MainDashboardScreen(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onLogoutClick = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                onLicenseClick = {
                    navController.navigate(Screen.LicenseVerification.route)
                },
                onReserveClick = { vehicleId ->
                    navController.navigate(Screen.VehiclePhotoUpload.createRoute(vehicleId))
                }
            )
        }
        composable(Screen.LicenseVerification.route) {
            LicenseVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.SelfieVerification.route)
                },
                viewModel = licenseViewModel
            )
        }
        composable(Screen.SelfieVerification.route) {
            SelfieVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                viewModel = licenseViewModel
            )
        }
        composable(
            route = Screen.VehiclePhotoUpload.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val reservationViewModel: ReservationViewModel = hiltViewModel()
            VehiclePhotoUploadScreen(
                vehicleId = vehicleId,
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.ReservationApproval.createRoute(vehicleId))
                },
                viewModel = reservationViewModel
            )
        }
        composable(
            route = Screen.ReservationApproval.route,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.VehiclePhotoUpload.route)
            }
            val reservationViewModel: ReservationViewModel = hiltViewModel(parentEntry)
            val authViewModel: AuthViewModel = hiltViewModel()
            ReservationApprovalScreen(
                vehicleId = vehicleId,
                onBackClick = { navController.popBackStack() },
                onReservationSuccess = {
                    // Update auth state to make sure active reservation triggers screen updates if needed
                    authViewModel.getProfile()
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.MainDashboard.route) { inclusive = true }
                    }
                },
                viewModel = reservationViewModel
            )
        }
        composable(
            route = Screen.PaymentSummary.route,
            arguments = listOf(navArgument("rentalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId") ?: ""
            com.turkcell.rencarapp.ui.screens.PaymentSummaryScreen(
                rentalId = rentalId,
                onPaymentSuccess = {
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
