package com.example.todo

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class Alarm: BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
                val title: String = intent.getStringExtra("title").toString()
                val id: Int = intent.getIntExtra("id", 0)
                createNotification(context!!, title, id)

                /*functions = AlarmFunctions(context)
                coroutineScope.launch {
                    val db = AppDatabase.getInstance(context)
                    val list = db!!.alarmDao.getAllAlarms()
                    val size = db.alarmDao.getAllAlarms().size
                    list.let {
                        for (i in 0 until size){
                            val time = list[i].time
                            val code = list[i].alarm_code
                            val content = list[i].content
                            functions.callAlarm(time, code, content) // 알람 실행
                        }
                    }
                }*/
            }
        }

        if(intent != null){
            val title: String = intent.getStringExtra("title").toString()
            val id: Int = intent.getIntExtra("id", 0)

            createNotification(context!!, title, id)
        }
    }

    private fun createNotification(context: Context, title: String, id: Int) {
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
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(id, builder.build())
    }
}