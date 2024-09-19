package com.example.todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class Alarm: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.getIntExtra("notification_id", 0)!!
        val notification = intent.getStringExtra("notification_title")
        if (context != null) {
            createNotification(context, notification.toString(), notificationId)
        }
    }

    private fun createNotification(context: Context, notification: String, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "notify_001",
            "Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, "notify_001")
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(notificationId, builder.build())
    }
}