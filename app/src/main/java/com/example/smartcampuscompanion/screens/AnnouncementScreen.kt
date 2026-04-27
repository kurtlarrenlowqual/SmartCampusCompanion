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
import com.example.smartcampuscompanion.data.repository.FirestoreRepository
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

        // Use Firestore real-time flow instead of Room
        val firestoreItems by FirestoreRepository.observeAnnouncements()
            .collectAsState(initial = emptyList())

// Map FirestoreAnnouncement -> display data (no DB needed for display)
        var isRefreshing by remember { mutableStateOf(false) }


        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val items by vm.announcements.collectAsState(initial = emptyList())

        LaunchedEffect(Unit) { vm.seedIfNeeded() }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {

            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Updates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                        navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.AutoMirrored.Filled.MenuOpen, "Menu") } },
                        actions = {
                            IconButton(onClick = { isRefreshing = true; scope.launch { kotlinx.coroutines.delay(500); isRefreshing = false } }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        }
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
                            items(items, key = { it.id }) { ann ->
                                AestheticAnnouncementCard(
                                    item = ann,
                                    onToggleRead = {
                                        // THE FIX: Removed .toLong(), passing the Int directly
                                        vm.updateReadStatus(ann.id, !ann.isRead)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerMenuItem(label: String, icon: ImageVector, isSelected: Boolean = false, onClick: () -> Unit) {
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
fun AestheticAnnouncementCard(item: AnnouncementEntity, onToggleRead: () -> Unit) {
    val fmt = remember { SimpleDateFormat("EEEE, MMM dd • hh:mm a", Locale.getDefault()) }

    val cardBg by animateColorAsState(
        targetValue = if (item.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
        label = "CardColorAnimation"
    )

    val elevation by animateDpAsState(targetValue = if (item.isRead) 0.dp else 4.dp, label = "ElevationAnimation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleRead() },
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
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                if (!item.isRead) {
                    Surface(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = "Unread",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = item.body,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
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
                Surface(color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = fmt.format(Date(item.postedAtMillis)),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                if (!item.isRead) {

                } else {
                    OutlinedButton(
                        onClick = onToggleRead,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.outline),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Read", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
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
        Icon(Icons.Default.AutoAwesomeMotion, null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(16.dp))
        Text("Nothing new yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
    }
}
