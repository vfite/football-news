package com.vfite.football.widget.system

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.WidgetMode
import com.vfite.football.widget.system.LatestNewsBroadcastReceiver
import java.util.*

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

    override fun createPendingEvent(
        context: Context,
        widgetId: Int,
        action: String
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            Objects.hash(action, widgetId),
            Intent(context, LatestNewsBroadcastReceiver::class.java).apply {
                this.action = action
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}