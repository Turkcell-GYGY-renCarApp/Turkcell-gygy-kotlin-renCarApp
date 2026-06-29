package com.turkcell.rencarapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.turkcell.rencarapp.ui.screens.LicenseVerificationScreen
import com.turkcell.rencarapp.ui.screens.LoginScreen
import com.turkcell.rencarapp.ui.screens.VerifyScreen
import com.turkcell.rencarapp.ui.screens.WelcomeScreen

import com.turkcell.rencarapp.ui.screens.MainDashboardScreen
import com.turkcell.rencarapp.ui.screens.SelfieVerificationScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
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
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onRegisterClick = { navController.navigate(Screen.LicenseVerification.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                isDarkTheme = isDarkTheme
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onCodeSent = { phoneNumber ->
                    navController.navigate(Screen.Verify.createRoute(phoneNumber))
                },
                onRegisterClick = {
                    navController.navigate(Screen.LicenseVerification.route) {
                        // Pop up to Welcome to avoid stacking login screens
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
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route)
                    }
                }
            )
        }
    }
}
