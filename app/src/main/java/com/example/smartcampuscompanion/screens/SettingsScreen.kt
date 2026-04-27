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
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.UserPrefsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        val scope = rememberCoroutineScope()
        var darkMode       by remember { mutableStateOf(false) }
        var notifsEnabled  by remember { mutableStateOf(true) }

        // Load current prefs
        LaunchedEffect(Unit) {
            darkMode      = UserPrefsDataStore.observeDarkMode(context).first()
            notifsEnabled = UserPrefsDataStore.observeNotifications(context).first()
        }

        Scaffold(
            topBar = { CenterAlignedTopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )}
        ) { padding ->
            Surface(modifier = Modifier.fillMaxSize().padding(padding), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Appearance", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Dark Mode", style = MaterialTheme.typography.labelSmall)
                                Switch(
                                    checked = darkMode,
                                    onCheckedChange = {
                                        darkMode = it
                                        scope.launch { UserPrefsDataStore.setDarkMode(context, it) }
                                    }
                                )
                            }
                        }
                    }

                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Notifications", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Enable Notifications", style = MaterialTheme.typography.labelSmall)
                                Switch(
                                    checked = notifsEnabled,
                                    onCheckedChange = {
                                        notifsEnabled = it
                                        scope.launch { UserPrefsDataStore.setNotifications(context, it) }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))
                    Text("Beta version", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
