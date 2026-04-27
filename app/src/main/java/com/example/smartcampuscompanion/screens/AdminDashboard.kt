package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.launch

@Composable
fun DashboardTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController, context: Context) {
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
                            Text("Admin Panel", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text(username, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(0.7f))
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    DrawerMenuItem("Admin Dashboard", Icons.Default.AdminPanelSettings, true) {
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("Post Announcement", Icons.Default.Campaign) {
                        scope.launch { drawerState.close() }; navController.navigate("post_announcement")
                    }
                    DrawerMenuItem("Announcements", Icons.Default.Notifications) {
                        scope.launch { drawerState.close() }; navController.navigate("announcements")
                    }
                    DrawerMenuItem("Manage Accounts", Icons.Default.ManageAccounts) {
                        scope.launch { drawerState.close() }; navController.navigate("add_account")
                    }
                    DrawerMenuItem("Settings", Icons.Default.Settings) {
                        scope.launch { drawerState.close() }; navController.navigate("settings")
                    }

                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(Modifier.padding(horizontal = 24.dp))

                    DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) {
                        scope.launch { drawerState.close() }
                        SessionManager.logout(context)
                        navController.navigate("login") {
                            popUpTo("admin_dashboard") { inclusive = true }
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
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
                        "Welcome, Admin $username",
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
                            DashboardTile("Post", Icons.Default.Campaign) {
                                navController.navigate("post_announcement")
                            }
                        }
                        item {
                            DashboardTile("Announcements", Icons.Default.Notifications) {
                                navController.navigate("announcements")
                            }
                        }
                        item {
                            DashboardTile("Accounts", Icons.Default.ManageAccounts) {
                                navController.navigate("add_account")
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