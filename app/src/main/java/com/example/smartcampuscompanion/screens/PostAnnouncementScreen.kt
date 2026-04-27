package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.repository.FirestoreRepository
import com.example.smartcampuscompanion.notifications.NotificationHelper
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostAnnouncementScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        var title       by remember { mutableStateOf("") }
        var body        by remember { mutableStateOf("") }
        var category    by remember { mutableStateOf("General") }
        var isImportant by remember { mutableStateOf(false) }
        var isLoading   by remember { mutableStateOf(false) }
        var errorMsg    by remember { mutableStateOf<String?>(null) }
        var success     by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val adminName = SessionManager.getUsername(context)
        val categories = listOf("General", "Academic", "Events", "Urgent")
        var expanded by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { CenterAlignedTopAppBar(
                title = { Text("Post Announcement", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )}
        ) { padding ->
            Surface(modifier = Modifier.fillMaxSize().padding(padding), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    OutlinedTextField(value = title, onValueChange = { title = it; errorMsg = null; success = false },
                        label = { Text("Title", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)

                    OutlinedTextField(value = body, onValueChange = { body = it; errorMsg = null; success = false },
                        label = { Text("Message Body", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.fillMaxWidth().height(150.dp), shape = RoundedCornerShape(16.dp), maxLines = 6)

                    // Category dropdown
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = category, onValueChange = {},
                            label = { Text("Category") }, modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            shape = RoundedCornerShape(16.dp)
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            categories.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; expanded = false })
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isImportant, onCheckedChange = { isImportant = it })
                        Spacer(Modifier.width(8.dp))
                        Text("Mark as Important", style = MaterialTheme.typography.labelSmall)
                    }

                    errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
                    if (success) Text("Announcement posted successfully!", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)

                    if (isLoading) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    } else {
                        Button(
                            onClick = {
                                if (title.isBlank() || body.isBlank()) { errorMsg = "Title and body are required."; return@Button }
                                isLoading = true
                                scope.launch {
                                    try {
                                        FirestoreRepository.postAnnouncement(title.trim(), body.trim(), category, isImportant, adminName)
                                        // Send notification to current device
                                        NotificationHelper.sendAnnouncement(context, title.trim(), body.trim())
                                        isLoading = false; success = true; title = ""; body = ""
                                    } catch (e: Exception) {
                                        isLoading = false; errorMsg = "Failed: ${e.message}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)
                        ) { Text("Post Announcement", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}
