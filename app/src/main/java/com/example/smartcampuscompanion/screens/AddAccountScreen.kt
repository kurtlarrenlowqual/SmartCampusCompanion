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
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        var username    by remember { mutableStateOf("") }
        var password    by remember { mutableStateOf("") }
        var displayName by remember { mutableStateOf("") }
        var selectedRole by remember { mutableStateOf("student") }
        var isLoading   by remember { mutableStateOf(false) }
        var errorMsg    by remember { mutableStateOf<String?>(null) }
        var success     by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = { CenterAlignedTopAppBar(
                title = { Text("Add Account", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )}
        ) { padding ->
            Surface(modifier = Modifier.fillMaxSize().padding(padding), color = MaterialTheme.colorScheme.surface) {
                Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Create a new student or admin account.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(value = displayName, onValueChange = { displayName = it; errorMsg = null; success = false },
                        label = { Text("Display Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
                    OutlinedTextField(value = username, onValueChange = { username = it; errorMsg = null; success = false },
                        label = { Text("Username") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true)
                    OutlinedTextField(value = password, onValueChange = { password = it; errorMsg = null; success = false },
                        label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())

                    // Role selection
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Role:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        listOf("student", "admin").forEach { role ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = selectedRole == role, onClick = { selectedRole = role })
                                Text(role.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    errorMsg?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
                    if (success) Text("Account created successfully!", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)

                    if (isLoading) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    } else {
                        Button(
                            onClick = {
                                if (username.isBlank() || password.isBlank() || displayName.isBlank()) { errorMsg = "All fields required."; return@Button }
                                isLoading = true
                                scope.launch {
                                    try {
                                        FirestoreRepository.register(username.trim(), password.trim(), selectedRole, displayName.trim())
                                        isLoading = false; success = true; username = ""; password = ""; displayName = ""
                                    } catch (e: Exception) { isLoading = false; errorMsg = "Error: ${e.message}" }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)
                        ) { Text("Create Account", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}
