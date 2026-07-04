package com.turkcell.rencarapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.turkcell.rencarapp.ui.viewmodel.AuthViewModel

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
                }
            )
        }
        composable(Screen.LicenseVerification.route) {
            LicenseVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.SelfieVerification.route)
                }
            )
        }
        composable(Screen.SelfieVerification.route) {
            SelfieVerificationScreen(
                onBackClick = { navController.popBackStack() },
                onContinueClick = {
                    navController.navigate(Screen.MainDashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
