package com.example.smartcampuscompanion.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.local.DbProvider
import com.example.smartcampuscompanion.data.local.TaskEntity
import com.example.smartcampuscompanion.data.repository.TaskRepository
import com.example.smartcampuscompanion.ui.theme.SmartCampusCompanionTheme
import com.example.smartcampuscompanion.viewmodel.TaskViewModel
import com.example.smartcampuscompanion.viewmodel.TaskViewModelFactory
import com.example.smartcampuscompanion.util.SessionManager
import com.example.smartcampuscompanion.notifications.TaskAlarmScheduler
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(310.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(24.dp)
                        ) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Smart Campus",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                "COMPANION APP",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    DrawerMenuItem("Dashboard", Icons.Default.Dashboard) {
                        scope.launch { drawerState.close() }
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                    DrawerMenuItem("Campus Information", Icons.Default.Info) {
                        scope.launch { drawerState.close() }
                        navController.navigate("campus")
                    }
                    DrawerMenuItem("Task Manager", Icons.AutoMirrored.Filled.Assignment, isSelected = true) {
                        scope.launch { drawerState.close() }
                    }
                    DrawerMenuItem("Announcements", Icons.Default.Campaign) {
                        scope.launch { drawerState.close() }
                        navController.navigate("announcements")
                    }
                    DrawerMenuItem("Settings", Icons.Default.Settings) { scope.launch { drawerState.close() }; navController.navigate("settings") }
                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(Modifier.padding(horizontal = 24.dp))

                    DrawerMenuItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) {
                        scope.launch { drawerState.close() }
                        SessionManager.logout(context)
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Task Manager",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.AutoMirrored.Filled.MenuOpen, contentDescription = "Menu")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            editingTask = null
                            showAddDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Task")
                    }
                }
            ) { padding ->
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    if (tasks.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.outlineVariant
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No tasks yet",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Tap the + button to add your first task.",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(tasks) { task ->
                                TaskCard(
                                    task = task,
                                    onEdit = {
                                        editingTask = task
                                        showAddDialog = true
                                    },
                                    onDelete = {
                                        val alarmId = (task.dueAtMillis % Int.MAX_VALUE).toInt()
                                        TaskAlarmScheduler.cancel(context, alarmId, task.title)
                                        vm.deleteTask(task)
                                    }

                                )
                            }
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
                            // Schedule notification
                            val alarmId = (dueMillis % Int.MAX_VALUE).toInt()
                            TaskAlarmScheduler.schedule(context, alarmId, title, dueMillis)
                        } else {
                            // Cancel old alarm, schedule new one
                            val oldAlarmId = (existing.dueAtMillis % Int.MAX_VALUE).toInt()
                            TaskAlarmScheduler.cancel(context, oldAlarmId, existing.title)
                            vm.updateTask(existing.copy(title = title.trim(), details = details.trim(), dueAtMillis = dueMillis))
                            val newAlarmId = (dueMillis % Int.MAX_VALUE).toInt()
                            TaskAlarmScheduler.schedule(context, newAlarmId, title, dueMillis)
                        }
                        showAddDialog = false
                    }

                )
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                )

                if (task.details.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        task.details,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(14.dp))

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Due: ${fmt.format(Date(task.dueAtMillis))}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
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

    val cal = remember {
        Calendar.getInstance().apply {
            timeInMillis = initial?.dueAtMillis ?: System.currentTimeMillis()
        }
    }
    var dueMillis by remember { mutableLongStateOf(cal.timeInMillis) }

    val fmt = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initial == null) "Add Task" else "Edit Task",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(
                        text = "Task Title",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text(
                        text = "Details (optional)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Selected: ${fmt.format(Date(dueMillis))}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
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
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Pick Date",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
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
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Pick Time",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isBlank()) return@TextButton
                    onSave(title, details, dueMillis)
                }
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold)
            }
        }
    )
}