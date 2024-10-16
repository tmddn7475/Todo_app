package com.ithink.dailytodo.Alarm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ithink.dailytodo.Object.Command
import com.ithink.dailytodo.RoomDB.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class RestartReceiver: BroadcastReceiver() {
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
                coroutineScope.launch {
                    val db = TodoDatabase.getInstance(context!!)
                    val list = db!!.todoDAO().getTodo()
                    list.let {
                        for (i in list.indices){
                            if(list[i].alert != "알림 없음" && checkDate(list[i].startDate)){
                                Command.setAlarm(context, list[i])
                            }
                        }
                    }
                }
            }
        }
    }

    // 일정 시작 날짜가 현재 날짜보다 빠를 경우 알람에 추가하지 않음
    @SuppressLint("SimpleDateFormat")
    private fun checkDate(day: String): Boolean {
        var bool = false

        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        val today: String = dateFormat.format(Date(System.currentTimeMillis()))

        val date = dateFormat.parse(today)!!
        val date2 = dateFormat.parse(day)!!

        if(date.after(date2)){
            bool = true
        }
        return bool
    }
}