package com.example.smartcampuscompanion

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class SmartCampusApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)

            // Channel for admin announcements
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ANNOUNCEMENTS,
                    "Announcements",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "New campus announcements" }
            )

            // Channel for task due-date reminders
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_TASKS,
                    "Task Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Alerts when a task is due" }
            )

            // Channel for general (welcome-back) notifications
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_GENERAL,
                    "General",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = "General app notifications" }
            )
        }
    }

    companion object {
        const val CHANNEL_ANNOUNCEMENTS = "channel_announcements"
        const val CHANNEL_TASKS         = "channel_tasks"
        const val CHANNEL_GENERAL       = "channel_general"
    }
}
