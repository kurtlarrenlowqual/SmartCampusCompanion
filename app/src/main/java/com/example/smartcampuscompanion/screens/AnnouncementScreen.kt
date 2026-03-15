package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextOverflow
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

        // Ensure we observe changes so the UI reacts instantly when marked as read
        val items by vm.announcements.collectAsState(initial = emptyList())

        LaunchedEffect(Unit) { vm.seedIfNeeded() }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(310.dp)
                ) {
                    // --- Aesthetic Drawer Header ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Smart Campus",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                "Connected Excellence",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // --- Updated Navigation Menu Items ---
                    DrawerMenuItem("Dashboard", Icons.Default.Dashboard) {
                        scope.launch { drawerState.close() }
                        navController.popBackStack()
                    }
                    DrawerMenuItem("Campus Information", Icons.Default.Info) {
                        scope.launch { drawerState.close() }
                        navController.navigate("campus")
                    }
                    DrawerMenuItem("Task Manager", Icons.Default.Assignment) {
                        scope.launch { drawerState.close() }
                        navController.navigate("tasks")
                    }
                    DrawerMenuItem("Announcements", Icons.Default.Campaign, isSelected = true) {
                        scope.launch { drawerState.close() }
                    }

                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(Modifier.padding(horizontal = 24.dp))

                    DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) {
                        scope.launch { drawerState.close() }
                        SessionManager.logout(context)
                        navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Updates",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.MenuOpen, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            ) { padding ->
                Surface(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    if (items.isEmpty()) {
                        EmptyState()
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Added key = { it.id } to help compose track item changes efficiently
                            items(items, key = { it.id }) { ann ->
                                AestheticAnnouncementCard(ann) {
                                    if (!ann.isRead) vm.markRead(ann.id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Medium) },
        icon = { Icon(icon, null) },
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun AestheticAnnouncementCard(item: AnnouncementEntity, onClick: () -> Unit) {
    val fmt = remember { SimpleDateFormat("EEEE, MMM dd • hh:mm a", Locale.getDefault()) }

    // Smooth transition between colors based on isRead status
    val cardBg by animateColorAsState(
        targetValue = if (item.isRead)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), // Tinted when unread
        label = "CardColorAnimation"
    )

    val elevation by animateDpAsState(
        targetValue = if (item.isRead) 0.dp else 4.dp,
        label = "ElevationAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = if (item.isRead) CardDefaults.outlinedCardBorder() else BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                // Explicit Unread Badge
                if (!item.isRead) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Unread",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = item.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time Badge
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = fmt.format(Date(item.postedAtMillis)),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Interactive Action Button / Read Confirmation
                if (!item.isRead) {
                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Mark as Read", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Feedback text once read
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Read",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.AutoAwesomeMotion,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Nothing new yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}