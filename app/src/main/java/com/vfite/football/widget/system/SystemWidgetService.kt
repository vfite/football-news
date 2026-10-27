package com.vfite.football.widget.system

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.WidgetMode

class SystemWidgetService(
    val context: Context,
    val widgetClass: Class<*>
) : IWidgetService {
    override fun toggleWidget(mode: WidgetMode) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val ids = widgetManager.getAppWidgetIds(ComponentName(context, widgetClass))

        val updateIntent = Intent(context, widgetClass)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        updateIntent.putExtra(IWidgetService.WIDGET_MODE, mode.name)

        context.sendBroadcast(updateIntent)
    }

    override fun invalidateWidget() {
        val widgetManager = AppWidgetManager.getInstance(context)
        val ids = widgetManager.getAppWidgetIds(ComponentName(context, widgetClass))

        val updateIntent = Intent(context, widgetClass)
        updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

        context.sendBroadcast(updateIntent)
    }
}