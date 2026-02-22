package com.example.smartcampuscompanion.screens


import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.local.AnnouncementEntity
import com.example.smartcampuscompanion.data.local.DbProvider
import com.example.smartcampuscompanion.data.repository.AnnouncementRepository
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.viewmodel.AnnouncementViewModel
import com.example.smartcampuscompanion.viewmodel.AnnouncementViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        val appContext = context.applicationContext
        val db = remember { DbProvider.get(appContext) }
        val repo = remember { AnnouncementRepository(db.announcementDao()) }
        val vm: AnnouncementViewModel = viewModel(factory = AnnouncementViewModelFactory(repo))


        // seed once
        LaunchedEffect(Unit) { vm.seedIfNeeded() }


        val items by vm.announcements.collectAsState()


        Scaffold(
            topBar = { TopAppBar(title = { Text("Campus Announcements") }) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (items.isEmpty()) {
                    Text("No announcements yet.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(items) { ann ->
                            AnnouncementCard(
                                item = ann,
                                onClick = { if (!ann.isRead) vm.markRead(ann.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun AnnouncementCard(
    item: AnnouncementEntity,
    onClick: () -> Unit
) {
    val fmt = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                )
                AssistChip(
                    onClick = { /* just label */ },
                    label = { Text(if (item.isRead) "Read" else "Unread") }
                )
            }


            Spacer(Modifier.height(8.dp))
            Text(item.body, style = MaterialTheme.typography.bodyMedium)


            Spacer(Modifier.height(12.dp))
            Text(
                "Posted: ${fmt.format(Date(item.postedAtMillis))}",
                style = MaterialTheme.typography.labelSmall
            )


            if (!item.isRead) {
                Spacer(Modifier.height(8.dp))
                Text("Tap to mark as read", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
