package com.vfite.football.widget.system

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vfite.football.util.appWidgetId
import com.vfite.football.util.isValidAppWidgetId
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.ui.LatestNewsViewModel
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.direct
import org.kodein.di.instance
import timber.log.Timber

class LatestNewsBroadcastReceiver : BroadcastReceiver() {

    companion object {

        val EXTRA_ID: String = "EXTRA_ID"
        val EXTRA_REMOTE_VIEWS: String = "EXTRA_REMOTE_VIEWS"
        val ACTION_NEXT = "ACTION_NEXT"
        val ACTION_PREV = "ACTION_PREV"
        val ACTION_SYNC = "ACTION_SYNC"
        val ACTION_SYNC_MATCHES = "ACTION_SYNC_MATCHES"
        val ACTION_SHOW_MATCHES = "ACTION_SHOW_MATCHES"
        val MIN_POKE_ID = 1
        val MAX_POKE_ID = 898

        fun newWidget(widgetId: Int) = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("onReceive ${intent.action}")
        val widgetId = intent.extras.appWidgetId

        val latestNewsViewModel: LatestNewsViewModel = DI.global.direct.instance()
        val widgetService: IWidgetService = DI.global.direct.instance()

        when (intent.action) {
            ACTION_NEXT, ACTION_PREV -> {
                if (widgetId.isValidAppWidgetId) {
                    when (intent.action) {
                        ACTION_NEXT -> latestNewsViewModel.nextItem()
                        ACTION_PREV -> latestNewsViewModel.previousItem()
                        else -> {}
                    }
                    widgetService.invalidateWidget()
                }
            }
            ACTION_SYNC -> {
                if (widgetId.isValidAppWidgetId) {
                    latestNewsViewModel.syncNews()
                }
            }
            ACTION_SYNC_MATCHES -> {
                if (widgetId.isValidAppWidgetId) {
                    latestNewsViewModel.syncMatches()
                }
            }
            ACTION_SHOW_MATCHES -> {
                if (widgetId.isValidAppWidgetId) {
                    latestNewsViewModel.toggleMode()
                    widgetService.invalidateWidget()
                }
            }
        }
    }
}