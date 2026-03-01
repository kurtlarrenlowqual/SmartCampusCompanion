package com.example.smartcampuscompanion.screens


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.local.DbProvider
import com.example.smartcampuscompanion.data.local.TaskEntity
import com.example.smartcampuscompanion.data.repository.TaskRepository
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.util.SessionManager
import com.example.smartcampuscompanion.viewmodel.TaskViewModel
import com.example.smartcampuscompanion.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(navController: NavController, context: Context) {
    SmartCampusCompanionTheme {
        val appContext = LocalContext.current.applicationContext
        val db = remember { DbProvider.get(appContext) }
        val repo = remember { TaskRepository(db.taskDao()) }
        val vm: TaskViewModel = viewModel(factory = TaskViewModelFactory(repo))

        val tasks by vm.tasks.collectAsState()

        var showAddDialog by remember { mutableStateOf(false) }
        var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

        // Added for menu drawer
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // Menu drawer
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(250.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Spacer(modifier = Modifier.height(70.dp))
                        Text(
                            text = "Dashboard",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate("dashboard") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                    scope.launch { drawerState.close() }
                                }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Campus Information",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate("campus")
                                    scope.launch { drawerState.close() }
                                }
                        )
                        Spacer(modifier = Modifier.height(20.dp)) // Added spacers
                        // Task manager button in drawer
                        Text(
                            text = "Task Manager",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Clickable text
                                    navController.navigate("tasks") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                    // Close the drawer
                                    scope.launch { drawerState.close() }
                                }
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    SessionManager.logout(context)
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
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
                        title = { Text("Task & Schedule Manager") },
                        // ADD THE HAMBURGER ICON HERE
                        navigationIcon = {
                            IconButton(onClick = {
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
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        editingTask = null
                        showAddDialog = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    if (tasks.isEmpty()) {
                        Text("No tasks yet. Tap + to add one.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(tasks) { task ->
                                TaskCard(
                                    task = task,
                                    onEdit = {
                                        editingTask = task
                                        showAddDialog = true
                                    },
                                    onDelete = { vm.deleteTask(task) }
                                )
                            }
                        }
                    }
                }

                if (showAddDialog) {
                    AddOrEditTaskDialog(
                        initial = editingTask,
                        onDismiss = { showAddDialog = false },
                        onSave = { title, details, dueMillis ->
                            val existing = editingTask
                            if (existing == null) {
                                vm.addTask(title, details, dueMillis)
                            } else {
                                vm.updateTask(
                                    existing.copy(
                                        title = title.trim(),
                                        details = details.trim(),
                                        dueAtMillis = dueMillis
                                    )
                                )
                            }
                            showAddDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val fmt = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }
    Card(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                if (task.details.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(task.details, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Due: ${fmt.format(Date(task.dueAtMillis))}",
                    style = MaterialTheme.typography.labelMedium
                )
            }


            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}


@Composable
private fun AddOrEditTaskDialog(
    initial: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, details: String, dueMillis: Long) -> Unit
) {
    val context = LocalContext.current


    var title by remember { mutableStateOf(initial?.title ?: "") }
    var details by remember { mutableStateOf(initial?.details ?: "") }


    // calendar holder for date+time
    val cal = remember {
        Calendar.getInstance().apply {
            timeInMillis = initial?.dueAtMillis ?: System.currentTimeMillis()
        }
    }
    var dueMillis by remember { mutableLongStateOf(cal.timeInMillis) }


    val fmt = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Task" else "Edit Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Details (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )


                Text("Selected: ${fmt.format(Date(dueMillis))}")


                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = {
                        val now = Calendar.getInstance().apply { timeInMillis = dueMillis }
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                now.set(Calendar.YEAR, y)
                                now.set(Calendar.MONTH, m)
                                now.set(Calendar.DAY_OF_MONTH, d)
                                dueMillis = now.timeInMillis
                            },
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }) { Text("Pick Date") }


                    Button(onClick = {
                        val now = Calendar.getInstance().apply { timeInMillis = dueMillis }
                        TimePickerDialog(
                            context,
                            { _, hh, mm ->
                                now.set(Calendar.HOUR_OF_DAY, hh)
                                now.set(Calendar.MINUTE, mm)
                                now.set(Calendar.SECOND, 0)
                                now.set(Calendar.MILLISECOND, 0)
                                dueMillis = now.timeInMillis
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            false
                        ).show()
                    }) { Text("Pick Time") }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isBlank()) return@TextButton
                    onSave(title, details, dueMillis)
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
