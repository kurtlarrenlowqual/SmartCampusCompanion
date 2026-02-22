package com.example.smartcampuscompanion.screens


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.local.DbProvider
import com.example.smartcampuscompanion.data.local.TaskEntity
import com.example.smartcampuscompanion.data.repository.TaskRepository
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.viewmodel.TaskViewModel
import com.example.smartcampuscompanion.viewmodel.TaskViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        // State to control drawer open/close
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope() // Needed to open/close drawer

        // Drawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(250.dp)
                ) {
                    // Drawer content
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Added a dashboard button in drawer
                        Spacer(modifier = Modifier.height(70.dp))
                        Text(
                            text = "Dashboard",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Clickable text
                                    // Will only pop the current screen
                                    navController.popBackStack()
                                    // Close the drawer
                                    scope.launch { drawerState.close() }
                                }
                        )
                        Spacer(modifier = Modifier.height(20.dp)) // Added spacers
                        // Edited the campus info button in drawer
                        Text(
                            text = "Campus Information",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Clickable text
                                    navController.navigate("campus") {
                                        // Prevents multiple instances of campus info
                                        popUpTo("campus") { inclusive = true }
                                    }
                                    // Close the drawer
                                    scope.launch { drawerState.close() }
                                }
                        )
                        Spacer(modifier = Modifier.height(20.dp)) // Added spacers
                        Text(
                            text = "Logout",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Logout functionality
                                    navController.navigate("login") {
                                        // Ensures user is logout even after exiting the app
                                        SessionManager.logout(context)
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                    // Close the drawer
                                    scope.launch { drawerState.close() }
                                }
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(
                            text = "Campus Information",
                            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                        ) },
                        navigationIcon = {
                            IconButton(onClick = {
                                // Open the drawer when hamburger is clicked
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    Button(
                        onClick = {
                            // Will pop the current screen
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding( horizontal = 10.dp)
                            .padding( bottom = 18.dp)
                    ) {
                        Text(
                            text = "Go back to dashboard",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                content = { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Text("Put content here!")
                    }
                }
            )
        }
    }

}
