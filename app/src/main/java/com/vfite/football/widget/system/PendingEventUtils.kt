package com.vfite.football.widget.system

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import java.util.Objects

fun createWidgetPendingEvent(context: Context, widgetId: Int, action: String): PendingIntent {
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
