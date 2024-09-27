package com.example.todo.Object

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.todo.Alarm.AlarmReceiver
import com.example.todo.RoomDB.TodoEntity
import com.example.todo.Widget.WidgetViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object Command {
    @SuppressLint("SimpleDateFormat")
    fun compareDates(selectedDate: String, comparisonDate: String): Boolean{
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        var bool = false
        try {
            // 문자열을 Date 객체로 변환
            val date1: Date = dateFormat.parse(selectedDate)!!
            val date2: Date = dateFormat.parse(comparisonDate)!!

            if(date1.after(date2)){
                bool = true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return bool
    }

    @SuppressLint("SimpleDateFormat")
    fun compareTime(selectedDate: String, comparisonDate: String, selectTime: String, comparisonTime: String): Boolean{
        val dateFormat = SimpleDateFormat("HH:mm")
        var bool = false
        try {
            // 문자열을 Date 객체로 변환
            val time1: Date = dateFormat.parse(selectTime)!!
            val time2: Date = dateFormat.parse(comparisonTime)!!

            if(time1.after(time2) && selectedDate == comparisonDate){
                bool = true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return bool
    }

    // 알림 설정
    fun setAlarm(context: Context, data: TodoEntity){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("title", data.title)
        intent.putExtra("id", data.id.toInt())

        val pIntent = PendingIntent.getBroadcast(
            context, data.id.toInt(), intent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val c = Calendar.getInstance()
        val dateArr = data.startDate.split(".")

        c.set(Calendar.YEAR, dateArr[0].toInt())
        c.set(Calendar.MONTH, dateArr[1].toInt()-1)
        c.set(Calendar.DAY_OF_MONTH, dateArr[2].toInt())

        for(i in dateArr){
            Log.i("test", i)
        }

        if(data.startTime == "all day"){
            c.set(Calendar.HOUR_OF_DAY, 8)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
        } else {
            val timeArr = data.startTime.split(":")
            c.set(Calendar.HOUR_OF_DAY, timeArr[0].toInt())
            c.set(Calendar.MINUTE, timeArr[1].toInt())
            c.set(Calendar.SECOND, 0)

            when (data.alert) {
                "5분 전" -> {
                    c.add(Calendar.MINUTE, -5)
                }
                "10분 전" -> {
                    c.add(Calendar.MINUTE, -10)
                }
                "1시간 전" -> {
                    c.add(Calendar.HOUR_OF_DAY, -1)
                }
                "1일 전" -> {
                    c.add(Calendar.DAY_OF_MONTH, -1)
                }
            }
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis, pIntent)
    }

    // 알림 삭제
    fun delAlarm(context: Context, data: TodoEntity){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, data.id.toInt(), intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }

    fun widgetUpdate(owner: ViewModelStoreOwner){
        val widgetViewModel = ViewModelProvider(owner)[WidgetViewModel::class.java]

        widgetViewModel.updateWidget()
    }
}