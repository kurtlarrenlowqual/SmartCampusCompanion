package com.example.smartcampuscompanion.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object TaskAlarmScheduler {

    fun schedule(context: Context, taskId: Int, taskTitle: String, dueAtMillis: Long) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = buildPendingIntent(context, taskId, taskTitle)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) return   // user must grant permission
        }

        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueAtMillis, pi)
    }

    fun cancel(context: Context, taskId: Int, taskTitle: String) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(buildPendingIntent(context, taskId, taskTitle))
    }

    private fun buildPendingIntent(context: Context, taskId: Int, taskTitle: String): PendingIntent {
        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra("task_title", taskTitle)
            putExtra("notif_id", 3000 + taskId)
        }
        return PendingIntent.getBroadcast(
            context, taskId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
