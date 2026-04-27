package com.example.smartcampuscompanion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.navigation.AppNavGraph
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import com.example.smartcampuscompanion.util.UserPrefsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Read dark mode preference synchronously once so there's no flash
        val initialDark = runBlocking {
            UserPrefsDataStore.observeDarkMode(this@MainActivity).first()
        }

        setContent {
            val context = LocalContext.current
            // Observe dark mode as state so it reacts to Settings changes live
            val darkMode by UserPrefsDataStore.observeDarkMode(context)
                .collectAsState(initial = initialDark)

            SmartCampusCompanionTheme(darkTheme = darkMode) {
                val navController = rememberNavController()
                val start = when {
                    !SessionManager.isLoggedIn(context) -> "login"
                    SessionManager.isAdmin(context)     -> "admin_dashboard"
                    else                                -> "dashboard"
                }
                AppNavGraph(navController, start)
            }
        }
    }
}