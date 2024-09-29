package com.example.todo.Widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.example.todo.Activity.TodoDetailActivity
import com.example.todo.AddTodoActivity
import com.example.todo.R
import java.text.SimpleDateFormat
import java.util.Date

class TodoWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val intent = Intent(context, WidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val views = RemoteViews(context.packageName, R.layout.todo_widget)
            views.setTextViewText(R.id.widget_title, getToday())
            views.setRemoteAdapter(R.id.widget_list, intent)

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
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getToday(): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        val date = Date(System.currentTimeMillis())

        return dateFormat.format(date)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(context, TodoWidget::class.java)
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }
}
