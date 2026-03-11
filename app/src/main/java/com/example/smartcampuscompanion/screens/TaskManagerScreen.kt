package com.example.smartcampuscompanion.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    modifier = Modifier.width(300.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Smart Campus",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    
                    DrawerItem(
                        icon = Icons.Default.Dashboard,
                        label = "Dashboard",
                        onClick = {
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = true }
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                    DrawerItem(
                        icon = Icons.Default.Info,
                        label = "Campus Information",
                        onClick = {
                            navController.navigate("campus")
                            scope.launch { drawerState.close() }
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    DrawerItem(
                        icon = Icons.Default.ExitToApp,
                        label = "Logout",
                        onClick = {
                            navController.navigate("login") {
                                SessionManager.logout(context)
                                popUpTo("dashboard") { inclusive = true }
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "Tasks & Schedules",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text("Add Task") },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        onClick = {
                            editingTask = null
                            showAddDialog = true
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                bottomBar = {
                    Surface(
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Back to Dashboard")
                        }
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AnimatedVisibility(
                        visible = tasks.isEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.EventNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "No tasks yet.",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    "Tap 'Add Task' to get started!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(tasks, key = { it.id }) { task ->
                            SwipeToDismissTask(
                                task = task,
                                onDismiss = { vm.deleteTask(task) },
                                onEdit = {
                                    editingTask = task
                                    showAddDialog = true
                                }
                            )
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
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissTask(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                    else -> Color.Transparent
                }, label = ""
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = {
            TaskCard(task = task, onClick = onEdit)
        }
    )
}

@Composable
fun TaskCard(
    task: TaskEntity,
    onClick: () -> Unit
) {
    val fmt = remember { SimpleDateFormat("EEE, MMM dd • hh:mm a", Locale.getDefault()) }
    val now = System.currentTimeMillis()
    val isOverdue = task.dueAtMillis < now
    
    val indicatorColor = if (isOverdue) MaterialTheme.colorScheme.error 
                        else if (task.dueAtMillis - now < 86400000) Color(0xFFFF9800) // Today/Soon
                        else MaterialTheme.colorScheme.primary

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = if (isOverdue) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error.copy(alpha = 0.5f))) else null
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(6.dp)
                    .background(indicatorColor)
            )
            
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                    if (isOverdue) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "OVERDUE",
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                
                if (task.details.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        task.details,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = indicatorColor
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        fmt.format(Date(task.dueAtMillis)),
                        style = MaterialTheme.typography.labelMedium,
                        color = indicatorColor,
                        fontWeight = FontWeight.Medium
                    )
                }
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
        icon = { Icon(Icons.Default.EditCalendar, contentDescription = null) },
        title = { 
            Text(
                if (initial == null) "New Schedule" else "Edit Task",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What needs to be done?") },
                    placeholder = { Text("e.g. Study for Exam") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) }
                )

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Additional Notes") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) }
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Due Date & Time",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            fmt.format(Date(dueMillis)),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(
                                onClick = {
                                    val now = Calendar.getInstance().apply { timeInMillis = dueMillis }
                                    DatePickerDialog(context, { _, y, m, d ->
                                        now.set(y, m, d)
                                        dueMillis = now.timeInMillis
                                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) { 
                                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Date", fontSize = 12.sp) 
                            }

                            FilledTonalButton(
                                onClick = {
                                    val now = Calendar.getInstance().apply { timeInMillis = dueMillis }
                                    TimePickerDialog(context, { _, hh, mm ->
                                        now.set(Calendar.HOUR_OF_DAY, hh)
                                        now.set(Calendar.MINUTE, mm)
                                        dueMillis = now.timeInMillis
                                    }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show()
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(0.dp)
                            ) { 
                                Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Time", fontSize = 12.sp) 
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onSave(title, details, dueMillis) },
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Save Task") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
