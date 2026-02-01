package com.example.smartcampuscompanion.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import android.content.Context
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.smartcampuscompanion.util.SessionManager
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme


@Composable
fun LoginScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        // NEW: state to toggle password visibility
        var passwordVisible by remember { mutableStateOf(false) }

        // State to control the popup dialog
        var showDialog by remember { mutableStateOf(false) }
        var dialogMessage by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp), // Increased padding for better spacing
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally // CENTER everything
        ) {

            // NEW: App title
            Text(
                text = "Smart Campus Companion",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            // NEW: Subtitle text
            Text(
                text = "Login to connect with your campus!",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Username field
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(
                    text = "Username",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                ) },
                modifier = Modifier.fillMaxWidth(0.9f) // Centered & consistent width
            )

            // Password field with show/hide toggle
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(
                    text = "Password",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall
                ) },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 8.dp),
                visualTransformation =
                    if (passwordVisible)
                        androidx.compose.ui.text.input.VisualTransformation.None
                    else
                        androidx.compose.ui.text.input.PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(
                            text = if (passwordVisible) "Hide" else "Show",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                        )
                    }
                }
            )

            // Centered login button
            Button(
                onClick = {
                    if (username == "student" && password == "1234") {
                        SessionManager.saveLogin(context, username)
                        dialogMessage = "Login Successful! Welcome, $username :)"
                        showDialog = true
                    } else if (username.isBlank() || password.isBlank()) {
                        dialogMessage = "Field/s cannot be blank! Please try again :("
                        showDialog = true
                    } else {
                        dialogMessage = "Invalid login credentials! Please try again :("
                        showDialog = true
                    }
                },
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxWidth(0.5f) // Centered button width
            ) {
                Text(
                    text = "Login",
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                )
            }
        }

        // Popup Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(
                    text = "Login Status",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                ) },
                text = { Text(
                    text = dialogMessage,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                ) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            if (dialogMessage == "Login Successful! Welcome, $username :)") {
                                navController.navigate("dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "OK",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                        )
                    }
                }
            )
        }
    }
}
