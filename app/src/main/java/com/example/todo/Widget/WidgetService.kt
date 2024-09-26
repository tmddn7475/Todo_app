package com.example.todo.Widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.todo.R
import com.example.todo.RoomDB.TodoDatabase
import com.example.todo.RoomDB.TodoEntity
import kotlinx.coroutines.runBlocking

class WidgetService : RemoteViewsService(){
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return TodoViewsFactory(this.applicationContext)
    }
}

class TodoViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private lateinit var data: List<TodoEntity>
    private lateinit var db: TodoDatabase

    override fun onCreate() {
        db = TodoDatabase.getInstance(context)!!
        data = runBlocking {
            db.todoDAO().getTodo()
        }
    }

    override fun onDataSetChanged() {
        data = runBlocking {
            db.todoDAO().getTodo()
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int {
        return data.size
    }

    @SuppressLint("RemoteViewLayout")
    override fun getViewAt(position: Int): RemoteViews {
        val listviewWidget = RemoteViews(context.packageName, R.layout.widget_item)
        listviewWidget.setTextViewText(R.id.item_title, data[position].title)

        if(data[position].startTime == "all day"){
            listviewWidget.setTextViewText(R.id.item_time, "하루 종일")
        } else {
            listviewWidget.setTextViewText(R.id.item_time, "${data[position].startTime} ~ ${data[position].endTime}")
        }

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