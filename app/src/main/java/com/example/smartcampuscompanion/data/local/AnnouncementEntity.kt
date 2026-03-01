package com.example.smartcampuscompanion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val postedAtMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
