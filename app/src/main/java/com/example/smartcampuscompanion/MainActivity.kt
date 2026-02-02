package com.example.smartcampuscompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.navigation.AppNavGraph
import com.example.smartcampuscompanion.util.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier,
                    color = Color.White
                ) {
                    //user stays logged in
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    val startDestination =
                        if (SessionManager.isLoggedIn(context)) "dashboard" else "login"

                    AppNavGraph(navController, startDestination)
                }
            }
        }
    }
}
