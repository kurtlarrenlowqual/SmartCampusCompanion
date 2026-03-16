package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.local.TaskDao
import com.example.smartcampuscompanion.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow


class TaskRepository(private val dao: TaskDao) {


    fun observeTasks(): Flow<List<TaskEntity>> = dao.observeTasks()


    suspend fun addTask(title: String, details: String, dueAtMillis: Long) {
        dao.insert(
            TaskEntity(
                title = title.trim(),
                details = details.trim(),
                dueAtMillis = dueAtMillis
            )
        )
    }


    suspend fun updateTask(task: TaskEntity) = dao.update(task)


    suspend fun deleteTask(task: TaskEntity) = dao.delete(task)
}
