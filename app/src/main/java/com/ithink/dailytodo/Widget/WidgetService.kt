package com.ithink.dailytodo.Widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.ithink.dailytodo.R
import com.ithink.dailytodo.RoomDB.TodoDatabase
import com.ithink.dailytodo.RoomDB.TodoEntity
import kotlinx.coroutines.runBlocking
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class WidgetService : RemoteViewsService(){
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TodoViewsFactory(this.applicationContext)
    }
}

class TodoViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var data: MutableList<TodoEntity> = mutableListOf()
    private lateinit var db: TodoDatabase

    override fun onCreate() {
        db = TodoDatabase.getInstance(context)!!
    }

    override fun onDataSetChanged() {
        data.clear()
        runBlocking {
            val todos = db.todoDAO().getTodo()
            for(i in todos.indices){
                if(todayTodos(todos[i].startDate, todos[i].endDate)){
                    data.add(todos[i])
                }
            }
        }
    }

    // 오늘 할일 가져오기
    @SuppressLint("SimpleDateFormat")
    private fun todayTodos(startDate: String, endDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        val date = Date(System.currentTimeMillis())
        val simpleDate: String = dateFormat.format(date)

        var bool = false

        try {
            // 문자열을 Date 객체로 변환
            val today: Date = dateFormat.parse(simpleDate)!!
            val date1: Date = dateFormat.parse(startDate)!!
            val date2: Date = dateFormat.parse(endDate)!!

            if((today.after(date1) && today.before(date2)) || today == date1 || today == date2){
                bool = true
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return bool
    }

    override fun onDestroy() {}

    override fun getCount(): Int {
        return data.size
    }

    @SuppressLint("RemoteViewLayout")
    override fun getViewAt(position: Int): RemoteViews {
        // listItem 제목, 시간
        val listviewWidget = RemoteViews(context.packageName, R.layout.widget_item)

        if(data[position].priorityHigh){
            listviewWidget.setViewVisibility(R.id.item_priority, View.VISIBLE)
        } else {
            listviewWidget.setViewVisibility(R.id.item_priority, View.GONE)
        }

        listviewWidget.setTextViewText(R.id.item_title, data[position].title)

        if(data[position].startTime == "all day" && data[position].startDate == data[position].endDate){
            listviewWidget.setTextViewText(R.id.item_time, context.getString(R.string.all_day))
        } else if (data[position].startDate == data[position].endDate) {
            listviewWidget.setTextViewText(R.id.item_time, "${data[position].startTime} ~ ${data[position].endTime}")
        } else {
            listviewWidget.setTextViewText(R.id.item_time, "${data[position].startDate} ~ ${data[position].endDate}")
        }

        // 클릭 시 실행될 Intent 생성 및 데이터 추가
        val intent = Intent()
        intent.putExtra("id", data[position].id)
        listviewWidget.setOnClickFillInIntent(R.id.item_main, intent)

        return listviewWidget
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}