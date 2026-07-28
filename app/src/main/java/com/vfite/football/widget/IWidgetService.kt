package com.vfite.football.widget

import android.app.PendingIntent
import android.content.Context

enum class WidgetMode{
    NEWS,
    MACTHES
}

interface IWidgetService {

    companion object {

        const val WIDGET_MODE = "WidgetMode"
    }

    fun toggleWidget(mode: WidgetMode)
    fun invalidateWidget()
    fun createPendingEvent(context: Context, widgetId: Int, action: String): PendingIntent
}