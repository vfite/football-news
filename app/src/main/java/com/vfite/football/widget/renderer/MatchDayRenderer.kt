package com.vfite.football.widget.renderer

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.vfite.football.R
import com.vfite.football.util.appWidgetManager
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.data.SyncState
import com.vfite.football.widget.system.FootballWidgetProvider
import com.vfite.football.widget.system.LatestNewsBroadcastReceiver
import com.vfite.football.widget.system.MatchesRemoteService
import com.vfite.football.widget.ui.LatestNewsViewModel
import timber.log.Timber

class MatchDayRenderer(
    val context: Context,
    val latestNewsViewModel: LatestNewsViewModel,
    val widgetService: IWidgetService
) : IRemoteViewRenderer {

    override fun render(widgetId: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.layout_macthes_list)

        val state = latestNewsViewModel.state
        Timber.d("render widgetId with state= $state")

        initMainViews(remoteViews, widgetId)

        when (state) {
            is SyncState.Synced -> {
                showMatchesContent(remoteViews, widgetId)
            }

            is SyncState.Failed -> {
                showFailedState(remoteViews, widgetId)
            }

            is SyncState.Syncing -> {
                showSyncingState(remoteViews)
            }

            else -> {
                showSyncingState(remoteViews)
            }
        }

        return remoteViews
    }

    private fun initMainViews(remoteViews: RemoteViews, widgetId: Int) {
        remoteViews.setOnClickPendingIntent(
            R.id.matches_btn,
            widgetService.createPendingEvent(
                context = context,
                widgetId = widgetId,
                action = LatestNewsBroadcastReceiver.ACTION_SHOW_MATCHES
            )
        )

        remoteViews.setOnClickPendingIntent(
            R.id.sync_btn,
            widgetService.createPendingEvent(
                context = context,
                widgetId = widgetId,
                action = LatestNewsBroadcastReceiver.ACTION_SYNC_MATCHES
            )
        )
    }

    private fun showMatchesContent(remoteViews: RemoteViews, widgetId: Int) {

        val widgetManager = context.appWidgetManager
        context.appWidgetManager.notifyAppWidgetViewDataChanged(widgetManager.getAppWidgetIds(
            ComponentName(context.applicationContext.packageName, FootballWidgetProvider::class.java.name)
        ),
            R.id.matches_list
        )

        remoteViews.setViewVisibility(R.id.matches_list, View.VISIBLE)
        remoteViews.setViewVisibility(R.id.failure_layout, View.GONE)
        remoteViews.setViewVisibility(R.id.progressBar, View.GONE)

        val intent = Intent(context, MatchesRemoteService::class.java).apply {
            // Add the widget ID to the intent extras.
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        remoteViews.setRemoteAdapter(
            R.id.matches_list,
            intent
        )
        remoteViews.setEmptyView(R.id.matches_list, R.id.progressBar)
    }

    private fun showSyncingState(views: RemoteViews) {
        views.setViewVisibility(R.id.matches_list, View.GONE)
        views.setViewVisibility(R.id.failure_layout, View.GONE)
        views.setViewVisibility(R.id.progressBar, View.VISIBLE)
    }

    private fun showFailedState(views: RemoteViews, widgetId: Int) {
        views.setViewVisibility(R.id.matches_list, View.GONE)
        views.setViewVisibility(R.id.progressBar, View.GONE)
        views.setViewVisibility(R.id.failure_layout, View.VISIBLE)
    }
}