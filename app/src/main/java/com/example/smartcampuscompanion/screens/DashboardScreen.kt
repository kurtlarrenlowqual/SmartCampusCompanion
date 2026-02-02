package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {

        val username = SessionManager.getUsername(context)

        // State to control drawer open/close
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope() // Needed to open/close drawer

        // Drawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(250.dp)
                ) {
                    // Drawer content
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Added a dashboard button in drawer
                        Spacer(modifier = Modifier.height(70.dp))
                        Text(
                            text = "Dashboard",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Clickable text
                                    navController.navigate("dashboard") {
                                        // Prevents multiple instances of dashboard
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                    // Close the drawer
                                    scope.launch { drawerState.close() }
                                }
                        )
                        Spacer(modifier = Modifier.height(20.dp)) // Added spacers
                        // Edited the campus info button in drawer
                        Text(
                            text = "Campus Information",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Clickable text
                                    navController.navigate("campus") {
                                        // Removed popupto so we can go back to dashboard
                                    }
                                    // Close the drawer
                                    scope.launch { drawerState.close() }
                                }
                        )
                        Spacer(modifier = Modifier.height(20.dp)) // Added spacers
                        Text(
                            text = "Logout",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Logout functionality
                                    navController.navigate("login") {
                                        // Ensures user is logout even after exiting the app
                                        SessionManager.logout(context)
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                    // Close the drawer
                                    scope.launch { drawerState.close() }
                                }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(
                            text = "Dashboard",
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                            ) },
                        navigationIcon = {
                            IconButton(onClick = {
                                // Open the drawer when hamburger is clicked
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                },
                content = { paddingValues ->
                    // Dashboard content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome to your dashboard, $username!",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 30.sp,
                            lineHeight = 40.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            )
        }
    }
}