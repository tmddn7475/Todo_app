package com.ithink.dailylist.Alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ithink.dailylist.Activity.MainActivity
import com.ithink.dailylist.R
import com.ithink.dailylist.RoomDB.TodoEntity
import java.io.Serializable

class AlarmReceiver: BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            val data: TodoEntity = intent.intentSerializable("todoEntity", TodoEntity::class.java)!!
            createNotification(context!!, data)
        }
    }

    private fun createNotification(context: Context, data: TodoEntity) {
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
            .setContentTitle(data.title)
            .setContentText(if (data.startTime == "all day"){
                "하루종일"
            } else {
                data.startTime
            })
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(data.id.toInt(), builder.build())
    }

    private fun <T: Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializableExtra(key, clazz)
        } else {
            this.getSerializableExtra(key) as T?
        }
    }
}