package com.example.smartcampuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.local.TaskEntity
import com.example.smartcampuscompanion.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TaskViewModel(private val repo: TaskRepository) : ViewModel() {


    val tasks: StateFlow<List<TaskEntity>> =
        repo.observeTasks().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun addTask(title: String, details: String, dueAtMillis: Long) {
        viewModelScope.launch { repo.addTask(title, details, dueAtMillis) }
    }


    fun updateTask(task: TaskEntity) {
        viewModelScope.launch { repo.updateTask(task) }
    }


    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repo.deleteTask(task) }
    }
}


class TaskViewModelFactory(private val repo: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(repo) as T
    }
}
