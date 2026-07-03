package com.turkcell.rencarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.turkcell.rencarapp.data.preferences.ThemePreferenceRepository
import com.turkcell.rencarapp.ui.navigation.RenCarNavHost
import com.turkcell.rencarapp.ui.screens.WelcomeScreen
import com.turkcell.rencarapp.ui.theme.RencarTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferenceRepository: ThemePreferenceRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themePreferenceRepository.isDarkTheme.collectAsState(initial = true)

            RencarTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RenCarNavHost(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = {
                            lifecycleScope.launch {
                                themePreferenceRepository.setDarkTheme(!isDarkTheme)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Welcome Screen Preview")
@Composable
fun AppWelcomePreview() {
    RencarTheme {
        WelcomeScreen(
            onRegisterClick = {},
            onLoginClick = {},
            isDarkTheme = false
        )
    }
}