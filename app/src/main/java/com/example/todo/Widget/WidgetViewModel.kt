package com.example.todo.Widget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.todo.R

// 위젯 업데이트
class WidgetViewModel(application: Application) : AndroidViewModel(application) {
    fun updateWidget() {
        val intent = Intent(getApplication(), TodoWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val appWidgetManager = AppWidgetManager.getInstance(getApplication())
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(getApplication(), TodoWidget::class.java)
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
    }
}