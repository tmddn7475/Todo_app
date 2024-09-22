package com.example.todo.Alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todo.Object.Command
import com.example.todo.RoomDB.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                            if(list[i].alert != "알림 없음"){
                                Command.setAlarm(context, list[i])
                            }
                        }
                    }
                }
            }
        }
    }
}