package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.local.AnnouncementEntity
import com.example.smartcampuscompanion.data.local.DbProvider
import com.example.smartcampuscompanion.data.repository.AnnouncementRepository
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import com.example.smartcampuscompanion.viewmodel.AnnouncementViewModel
import com.example.smartcampuscompanion.viewmodel.AnnouncementViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        val appContext = context.applicationContext
        val db = remember { DbProvider.get(appContext) }
        val repo = remember { AnnouncementRepository(db.announcementDao()) }
        val vm: AnnouncementViewModel = viewModel(factory = AnnouncementViewModelFactory(repo))

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val items by vm.announcements.collectAsState()

        LaunchedEffect(Unit) { vm.seedIfNeeded() }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer))),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            "Smart Campus",
                            Modifier.padding(24.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Nav Items
                    DrawerItem("Dashboard", Icons.Default.Dashboard) {
                        scope.launch { drawerState.close() }
                        navController.popBackStack()
                    }
                    DrawerItem("Campus Info", Icons.Default.Info) {
                        scope.launch { drawerState.close() }
                        navController.navigate("campus")
                    }
                    DrawerItem("Announcements", Icons.Default.Notifications, true) {
                        scope.launch { drawerState.close() }
                    }

                    HorizontalDivider(Modifier.padding(16.dp))

                    DrawerItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) {
                        scope.launch { drawerState.close() }
                        SessionManager.logout(context)
                        navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Campus News", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Menu")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                    if (items.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No announcements yet.", color = MaterialTheme.colorScheme.outline)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items) { ann ->
                                AnnouncementCard(ann) { if (!ann.isRead) vm.markRead(ann.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerItem(label: String, icon: ImageVector, selected: Boolean = false, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        icon = { Icon(icon, null) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}

@Composable
fun AnnouncementCard(item: AnnouncementEntity, onClick: () -> Unit) {
    val fmt = remember { SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()) }
    val bgColor by animateColorAsState(if (item.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(0.1f))

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(if (item.isRead) 1.dp else 4.dp)
    ) {
        Row(Modifier.padding(16.dp)) {
            if (!item.isRead) {
                Box(Modifier.size(8.dp).background(MaterialTheme.colorScheme.primary, CircleShape).align(Alignment.CenterVertically))
                Spacer(Modifier.width(12.dp))
            }
            Column {
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(item.body, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                Spacer(Modifier.height(8.dp))
                Text(fmt.format(Date(item.postedAtMillis)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}