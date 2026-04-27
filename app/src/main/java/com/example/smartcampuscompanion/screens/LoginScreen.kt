package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.repository.FirestoreRepository
import com.example.smartcampuscompanion.notifications.NotificationHelper
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {

            var username       by remember { mutableStateOf("") }
            var password       by remember { mutableStateOf("") }
            var passwordVisible by remember { mutableStateOf(false) }
            var isLoading      by remember { mutableStateOf(false) }
            var errorMsg       by remember { mutableStateOf("") }
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        MaterialTheme.colorScheme.surface
                    )))
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountBox,
                            contentDescription = "Campus Icon",
                            modifier = Modifier.size(92.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Smart Campus Companion", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Login to connect with your campus!",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; errorMsg = "" },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMsg = "" },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            visualTransformation = if (passwordVisible)
                                VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Text(
                                        if (passwordVisible) "Hide" else "Show",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        )

                        // Error message
                        if (errorMsg.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                errorMsg,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            // ── LOGIN BUTTON ──────────────────────────────
                            Button(
                                onClick = {
                                    when {
                                        username.isBlank() || password.isBlank() -> {
                                            errorMsg = "Fields cannot be blank."
                                        }
                                        else -> {
                                            isLoading = true
                                            scope.launch {
                                                val role = FirestoreRepository.login(username.trim(), password.trim())
                                                isLoading = false
                                                if (role == null) {
                                                    errorMsg = "Invalid credentials. Please try again."
                                                } else {
                                                    errorMsg = ""
                                                    SessionManager.saveLogin(context, username.trim(), role)
                                                    NotificationHelper.sendWelcome(context, username.trim())
                                                    val dest = if (role == "admin") "admin_dashboard" else "dashboard"
                                                    navController.navigate(dest) {
                                                        popUpTo("login") { inclusive = true }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Login", style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // ── REGISTER BUTTON ───────────────────────────
                            OutlinedButton(
                                onClick = { navController.navigate("register") },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Register New Student Account",
                                    style = MaterialTheme.typography.labelSmall)
                            }




                        }
                    }
                }
            }
        }
    }
}