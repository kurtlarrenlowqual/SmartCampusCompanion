package com.example.smartcampuscompanion.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import com.example.smartcampuscompanion.util.SessionManager
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusInfoScreen(navController: NavController, context: Context) {
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
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            // Clickable text
                            navController.navigate("dashboard") {
                                // Closes campus info screen
                                popUpTo("campus") { inclusive = true }
                            }
                            // Close the drawer
                            scope.launch { drawerState.close() }
                        }
                )
                // Edited the campus info button in drawer
                Text(
                    text = "Campus Information",
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
                Text(
                    text = "Logout",
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
                    title = { Text(text = "Campus Information") },
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
            content = { paddingValues ->
                // Campus info content
                val departments = listOf(
                    "Sample - sample text"
                )

                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                ) {
                    items(departments) { dept ->
                        Card(modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)) {
                            Text(dept, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        )
    }
}
