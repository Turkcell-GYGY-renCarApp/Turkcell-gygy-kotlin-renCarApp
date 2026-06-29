package com.turkcell.rencarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.turkcell.rencarapp.data.ThemePreferences
import com.turkcell.rencarapp.ui.navigation.RenCarNavHost
import com.turkcell.rencarapp.ui.screens.WelcomeScreen
import com.turkcell.rencarapp.ui.theme.RencarTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themePreferences = remember { ThemePreferences(context) }
            val systemTheme = isSystemInDarkTheme()
            val isDarkThemePref by themePreferences.isDarkTheme.collectAsState(initial = null)
            val coroutineScope = rememberCoroutineScope()

            val isDarkTheme = isDarkThemePref ?: systemTheme

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
                            coroutineScope.launch {
                                themePreferences.setDarkTheme(!isDarkTheme)
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