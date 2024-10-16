package com.ithink.dailytodo.Alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ithink.dailytodo.Activity.MainActivity
import com.ithink.dailytodo.R
import com.ithink.dailytodo.RoomDB.TodoEntity
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


        val text = if(data.startTime == "all day" && data.startDate == data.endDate){
            context.getString(R.string.all_day)
        } else if (data.startDate == data.endDate) {
            "${data.startTime} ~ ${data.endTime}"
        } else if (data.startTime == "all day") {
            "${data.startDate} ~ ${data.endDate}"
        } else {
            "${data.startDate} / ${data.startTime} ~ ${data.endDate} / ${data.endTime}"
        }

        val builder = NotificationCompat.Builder(context, "notify_001")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(data.title)
            .setContentText(text)
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