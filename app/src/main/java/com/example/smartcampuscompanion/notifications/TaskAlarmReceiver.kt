package com.example.smartcampuscompanion.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title  = intent.getStringExtra("task_title") ?: "Task"
        val notifId = intent.getIntExtra("notif_id", 3000)
        NotificationHelper.sendTaskDue(context, title, notifId)
    }
}
