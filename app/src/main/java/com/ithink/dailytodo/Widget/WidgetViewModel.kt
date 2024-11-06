package com.ithink.dailytodo.Widget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.ithink.dailytodo.R
import com.ithink.dailytodo.RoomDB.TodoDatabase
import com.ithink.dailytodo.Widget.Today.TodoWidget
import kotlinx.coroutines.runBlocking

// 위젯 업데이트
class WidgetViewModel(application: Application) : AndroidViewModel(application) {
    fun updateWidget() {
        val intent = Intent(getApplication(), TodoWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val appWidgetManager = AppWidgetManager.getInstance(getApplication())
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(getApplication(), TodoWidget::class.java),
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
    }
}