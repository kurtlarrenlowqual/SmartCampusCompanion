package com.example.smartcampuscompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartcampuscompanion.screens.LoginScreen
import com.example.smartcampuscompanion.screens.DashboardScreen
import com.example.smartcampuscompanion.screens.CampusInfoScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(navController, LocalContext.current)
        }

        composable("dashboard") {
            DashboardScreen(navController, LocalContext.current)
        }

        // include campus info screen in navigation
        composable("campus") {
            CampusInfoScreen()
        }
    }
}
