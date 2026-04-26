package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        val username    = SessionManager.getUsername(context)
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope       = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(310.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp)
                        .background(Brush.linearGradient(colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary)))) {
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                            Text("Admin Panel", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text(username, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    DrawerMenuItem("Admin Dashboard", Icons.Default.AdminPanelSettings, isSelected = true) { scope.launch { drawerState.close() } }
                    DrawerMenuItem("Post Announcement", Icons.Default.Campaign) { scope.launch { drawerState.close() }; navController.navigate("post_announcement") }
                    DrawerMenuItem("Announcements", Icons.Default.Notifications) { scope.launch { drawerState.close() }; navController.navigate("announcements") }
                    DrawerMenuItem("Manage Accounts", Icons.Default.ManageAccounts) { scope.launch { drawerState.close() }; navController.navigate("add_account") }
                    DrawerMenuItem("Settings", Icons.Default.Settings) { scope.launch { drawerState.close() }; navController.navigate("settings") }
                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(Modifier.padding(horizontal = 24.dp))
                    DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) {
                        scope.launch { drawerState.close() }
                        SessionManager.logout(context)
                        navController.navigate("login") { popUpTo("admin_dashboard") { inclusive = true } }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Admin Dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                        navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.AutoMirrored.Filled.MenuOpen, "Menu") } }
                    )
                }
            ) { padding ->
                Surface(modifier = Modifier.fillMaxSize().padding(padding), color = MaterialTheme.colorScheme.surface) {
                    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Welcome, Admin $username!", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(8.dp))
                                Text("Manage announcements, student accounts, and campus settings.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Button(onClick = { navController.navigate("post_announcement") }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) {
                            Icon(Icons.Default.Campaign, null); Spacer(Modifier.width(8.dp))
                            Text("Post New Announcement", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Button(onClick = { navController.navigate("announcements") }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) {
                            Icon(Icons.Default.Notifications, null); Spacer(Modifier.width(8.dp))
                            Text("View All Announcements", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Button(onClick = { navController.navigate("add_account") }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) {
                            Icon(Icons.Default.PersonAdd, null); Spacer(Modifier.width(8.dp))
                            Text("Add Student / Admin Account", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(onClick = { navController.navigate("settings") }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp)) {
                            Icon(Icons.Default.Settings, null); Spacer(Modifier.width(8.dp))
                            Text("Settings", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
