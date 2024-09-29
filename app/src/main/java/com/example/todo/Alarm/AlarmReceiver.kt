package com.example.todo.Alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.todo.Activity.MainActivity
import com.example.todo.R

class AlarmReceiver: BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            val title: String = intent.getStringExtra("title").toString()
            val time: String = intent.getStringExtra("time").toString()
            val id: Int = intent.getIntExtra("id", 0)

            createNotification(context!!, title, id, time)
        }
    }

    private fun createNotification(context: Context, title: String, id: Int, time: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "notify_001",
            "Notification Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        // 알림 클릭 시 MainActivity로 이동
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "notify_001")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(time)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(id, builder.build())
    }
}