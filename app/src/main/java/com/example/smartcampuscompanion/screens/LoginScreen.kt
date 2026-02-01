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
import androidx.compose.ui.tooling.preview.Preview
import com.example.smartcampuscompanion.util.SessionManager
import android.widget.Toast

@Composable
fun LoginScreen(navController: NavController, context: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })

        Button(
            onClick = {
                if (username == "student" && password == "1234") {
                    SessionManager.saveLogin(context, username)
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show() //Show toast for successful login
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                } else if (username == "" || password == "") {
                        Toast.makeText(context, "Field/s cannot be blank!", Toast.LENGTH_SHORT).show()  // Show toast when fields are blank
                } else {
                    Toast.makeText(context, "Login Failed due to invalid credentials", Toast.LENGTH_SHORT).show()  //toast for login failed due to invalid credentials
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Login")
        }
    }
}
