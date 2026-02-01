package com.example.smartcampuscompanion.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import com.example.smartcampuscompanion.util.SessionManager


@Composable
fun DashboardScreen(navController: NavController, context: Context) {
    val username = SessionManager.getUsername(context)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text (text = "Welcome, $username!", style = MaterialTheme.typography.headlineMedium)

    Spacer(modifier = Modifier.height(24.dp))
    }
}