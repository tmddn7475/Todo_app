package com.ithink.dailytodo.Widget.Today

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.ithink.dailytodo.Activity.AddTodoActivity
import com.ithink.dailytodo.Activity.TodoDetailActivity
import com.ithink.dailytodo.R
import com.ithink.dailytodo.RoomDB.TodoDatabase
import kotlinx.coroutines.runBlocking
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class TodoWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.todo_widget)
            views.setTextViewText(R.id.widget_title, getToday())

            if(Build.VERSION.SDK_INT >= 31){
                views.setRemoteAdapter(R.id.widget_list, getItems(context))
            } else {
                val intent = Intent(context, WidgetService::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
                views.setRemoteAdapter(R.id.widget_list, intent)
            }

            // 클릭 시 TodoDetailActivity 실행
            val clickIntent = Intent(context, TodoDetailActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            views.setPendingIntentTemplate(R.id.widget_list, pendingIntent)

            val newTodoIntent = Intent(context, AddTodoActivity::class.java)
            val newTodoPendingIntent = PendingIntent.getActivity(
                context, 0, newTodoIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            views.setOnClickPendingIntent(R.id.widget_new_todo, newTodoPendingIntent)

            // 위젯을 새로고침
            appWidgetManager.updateAppWidget(appWidgetId, views)
            if(Build.VERSION.SDK_INT >= 31){
                val remoteViews = RemoteViews(context.packageName, R.layout.todo_widget).also {
                    it.setRemoteAdapter(R.id.widget_list, getItems(context))
                }
                appWidgetManager.partiallyUpdateAppWidget(appWidgetIds, remoteViews)
            } else {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getToday(): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        val date = Date(System.currentTimeMillis())

        return dateFormat.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getItems(context: Context): RemoteViews.RemoteCollectionItems {
        val db = TodoDatabase.getInstance(context)!!
        val itemsBuilder = RemoteViews.RemoteCollectionItems.Builder()

        runBlocking {
            val todos = db.todoDAO().getTodo()
            for(i in todos.indices){
                if(todayTodos(todos[i].startDate, todos[i].endDate)){
                    val itemView = RemoteViews(context.packageName, R.layout.widget_item)
                    if(todos[i].priorityHigh){
                        itemView.setViewVisibility(R.id.item_priority, View.VISIBLE)
                    } else {
                        itemView.setViewVisibility(R.id.item_priority, View.GONE)
                    }
                    itemView.setTextViewText(R.id.item_title, todos[i].title)
                    if(todos[i].startTime == "all day" && todos[i].startDate == todos[i].endDate){
                        itemView.setTextViewText(R.id.item_time, context.getString(R.string.all_day))
                    } else if (todos[i].startDate == todos[i].endDate) {
                        itemView.setTextViewText(R.id.item_time, "${todos[i].startTime} ~ ${todos[i].endTime}")
                    } else {
                        itemView.setTextViewText(R.id.item_time, "${todos[i].startDate} ~ ${todos[i].endDate}")
                    }
                    // 클릭 시 실행될 Intent 생성 및 데이터 추가
                    val intent = Intent()
                    intent.putExtra("id", todos[i].id)
                    itemView.setOnClickFillInIntent(R.id.item_main, intent)

                    itemsBuilder.addItem(i.toLong(), itemView) // 아이템 ID와 RemoteView 추가
                }
            }
        }

        return itemsBuilder.build()
    }

    // 오늘 할일 가져오기
    @SuppressLint("SimpleDateFormat")
    private fun todayTodos(startDate: String, endDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        val simpleDate: String = getToday()

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

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, TodoWidget::class.java),
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }
}
