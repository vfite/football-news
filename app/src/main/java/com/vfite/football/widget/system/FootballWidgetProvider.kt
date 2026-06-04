package com.vfite.football.widget.system

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.vfite.football.widget.ui.LatestNewsViewModel
import com.vfite.football.widget.renderer.IRemoteViewRenderer
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.direct
import org.kodein.di.instance
import timber.log.Timber

class FootballWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray
    ) {
        val currentWidgetMode =
            DI.global.direct.instance<LatestNewsViewModel>().mode
        Timber.d("onUpdate, appWidgetIds= $appWidgetIds, currentWidgetMode=$currentWidgetMode")

        val renderer: IRemoteViewRenderer =
            DI.global.direct.instance(currentWidgetMode)

        for (widgetId in appWidgetIds) {
            val remoteViews = renderer.render(widgetId)
            appWidgetManager?.updateAppWidget(widgetId, remoteViews)
        }
    }
}