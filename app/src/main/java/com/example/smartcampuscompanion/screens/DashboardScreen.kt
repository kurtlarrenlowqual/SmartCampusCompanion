package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {

        val username = SessionManager.getUsername(context)
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(310.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                        ) {
                            Text("Smart Campus", fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("COMPANION APP", color = Color.White.copy(0.7f))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    DrawerMenuItem("Dashboard", Icons.Default.Dashboard, true) {
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("Campus", Icons.Default.Info) {
                        scope.launch { drawerState.close() }; navController.navigate("campus")
                    }
                    DrawerMenuItem("Tasks", Icons.AutoMirrored.Filled.Assignment) {
                        scope.launch { drawerState.close() }; navController.navigate("tasks")
                    }
                    DrawerMenuItem("Announcements", Icons.Default.Campaign) {
                        scope.launch { drawerState.close() }; navController.navigate("announcements")
                    }

                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(Modifier.padding(horizontal = 24.dp))

                    DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) {
                        scope.launch { drawerState.close() }
                        SessionManager.logout(context)
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Dashboard", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.AutoMirrored.Filled.MenuOpen, null)
                            }
                        }
                    )
                }
            ) { padding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {

                    Text(
                        "Welcome, $username",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            DashboardTile("Campus", Icons.Default.Info) {
                                navController.navigate("campus")
                            }
                        }
                        item {
                            DashboardTile("Tasks", Icons.AutoMirrored.Filled.Assignment) {
                                navController.navigate("tasks")
                            }
                        }
                        item {
                            DashboardTile("Announcements", Icons.Default.Campaign) {
                                navController.navigate("announcements")
                            }
                        }
                        item {
                            DashboardTile("Settings", Icons.Default.Settings) {
                                navController.navigate("settings")
                            }
                        }
                    }
                }
            }
        }
    }
}