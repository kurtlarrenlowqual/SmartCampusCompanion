package com.example.smartcampuscompanion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartcampuscompanion.screens.*

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") { LoginScreen(navController, LocalContext.current) }
        composable("register") { RegisterScreen(navController, LocalContext.current) }
        composable("dashboard") { DashboardScreen(navController, LocalContext.current) }
        composable("admin_dashboard") { AdminDashboardScreen(navController, LocalContext.current) }
        composable("campus") { CampusInfoScreen(navController, LocalContext.current) }
        composable("tasks") { TaskManagerScreen(navController, LocalContext.current) }
        composable("announcements") { AnnouncementsScreen(navController, LocalContext.current) }
        composable("post_announcement") { PostAnnouncementScreen(navController, LocalContext.current) }
        composable("add_account") { AddAccountScreen(navController, LocalContext.current) }
        composable("settings") { SettingsScreen(navController, LocalContext.current) }
    }
}
