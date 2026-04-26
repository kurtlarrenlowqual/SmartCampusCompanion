package com.example.smartcampuscompanion.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartcampuscompanion.MainActivity
import com.example.smartcampuscompanion.R
import com.example.smartcampuscompanion.SmartCampusApp

object NotificationHelper {

    fun sendWelcomeBack(context: Context, username: String) {
        send(
            context,
            channel = SmartCampusApp.CHANNEL_GENERAL,
            id = 1001,
            title = "Welcome back!",
            message = "Welcome back, $username"
        )
    }

    fun sendAnnouncement(context: Context, title: String, body: String) {
        send(
            context,
            channel = SmartCampusApp.CHANNEL_ANNOUNCEMENTS,
            id = 2000 + (System.currentTimeMillis() % 1000).toInt(),
            title = title,
            message = body
        )
    }

    fun sendTaskDue(context: Context, taskTitle: String, notifId: Int) {
        send(
            context,
            channel = SmartCampusApp.CHANNEL_TASKS,
            id = notifId,
            title = "Task Due!",
            message = "\"$taskTitle\" is due now."
        )
    }

    private fun send(context: Context, channel: String, id: Int, title: String, message: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notif = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notif)
    }
}
