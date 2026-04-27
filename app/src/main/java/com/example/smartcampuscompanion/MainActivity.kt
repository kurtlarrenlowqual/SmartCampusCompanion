package com.example.smartcampuscompanion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.navigation.AppNavGraph
import com.example.smartcampuscompanion.util.SessionManager

class MainActivity : ComponentActivity() {

    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { /* result ignored for simplicity */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier, color = Color.White) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
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
}
