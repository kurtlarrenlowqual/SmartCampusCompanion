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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartcampuscompanion.util.SessionManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
@Composable
fun LoginScreen(navController: NavController, context: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // State to control the popup dialog
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        Button(
            onClick = {
                if (username == "student" && password == "1234") {
                    SessionManager.saveLogin(context, username)
                    // Dialog window for successful login instead of toast
                    dialogMessage = "Login Successful! Welcome, $username :)"
                    showDialog = true
                    //  Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    // Removed the navigation code here so that the alert dialog will not automatically close
                    // Dialog window for failed logins instead of toast
                } else if (username == "" || password == ""){
                    dialogMessage = "Field/s cannot be blank! Please try again :("
                    showDialog = true
                    // Toast.makeText(context, "Field/s cannot be blank!", Toast.LENGTH_SHORT).show()
                } else {
                    dialogMessage = "Invalid login credentials! Please try again :("
                    showDialog = true
                    // Toast.makeText(context, "Login failed due to invalid credentials", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Login")
        }
    }

    // Popup Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Login Status") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        // Navigate to dashboard if login is successful
                        if (dialogMessage == "Login Successful! Welcome, $username :)") {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
