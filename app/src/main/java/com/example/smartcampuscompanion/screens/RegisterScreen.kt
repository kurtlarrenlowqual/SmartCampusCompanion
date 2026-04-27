package com.example.smartcampuscompanion.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.repository.FirestoreRepository
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
            var displayName by remember { mutableStateOf("") }
            var username    by remember { mutableStateOf("") }
            var email       by remember { mutableStateOf("") }
            var password    by remember { mutableStateOf("") }
            var isLoading   by remember { mutableStateOf(false) }
            var errorMsg    by remember { mutableStateOf<String?>(null) }
            var success     by remember { mutableStateOf(false) }
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
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {

                        Text("Create Student Account",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Register with your campus credentials",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(24.dp))

                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it; errorMsg = null },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp), singleLine = true)
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it; errorMsg = null },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp), singleLine = true)
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMsg = null },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp), singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                            ))
                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMsg = null },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp), singleLine = true,
                            visualTransformation = PasswordVisualTransformation())

                        errorMsg?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall)
                        }

                        if (success) {
                            Spacer(Modifier.height(16.dp))
                            Text("Account created! You can now log in.",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(Modifier.height(20.dp))

                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(40.dp))
                        } else {
                            Button(
                                onClick = {
                                    when {
                                        displayName.isBlank() || username.isBlank() ||
                                                email.isBlank() || password.isBlank() ->
                                        { errorMsg = "All fields are required."; return@Button }
                                        !email.contains("@") ->
                                        { errorMsg = "Enter a valid email."; return@Button }
                                        password.length < 6 ->
                                        { errorMsg = "Password must be at least 6 characters."; return@Button }
                                    }
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            FirestoreRepository.register(
                                                username  = username.trim(),
                                                password  = password.trim(),
                                                role      = "student",
                                                displayName = displayName.trim(),
                                                email     = email.trim()
                                            )
                                            isLoading = false
                                            success = true
                                        } catch (e: Exception) {
                                            isLoading = false
                                            errorMsg = "Registration failed: ${e.message}"
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Register", style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(12.dp))
                            TextButton(onClick = { navController.popBackStack() }) {
                                Text("Back to Login",
                                    style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}