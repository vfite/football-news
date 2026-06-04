package com.vfite.football.widget.renderer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vfite.football.R
import com.vfite.football.models.NewsItem
import com.vfite.football.widget.IWidgetService
import com.vfite.football.widget.system.LatestNewsBroadcastReceiver.Companion.ACTION_NEXT
import com.vfite.football.widget.system.LatestNewsBroadcastReceiver.Companion.ACTION_PREV
import com.vfite.football.widget.system.LatestNewsBroadcastReceiver.Companion.ACTION_SYNC
import com.vfite.football.widget.ui.LatestNewsViewModel
import com.vfite.football.widget.data.SyncState
import com.vfite.football.widget.system.LatestNewsBroadcastReceiver.Companion.ACTION_SHOW_MATCHES
import timber.log.Timber

class LatestNewsRenderer(
    val context: Context,
    val latestNewsViewModel: LatestNewsViewModel,
    val widgetService: IWidgetService,
    val newsUrl : String
) : IRemoteViewRenderer {

    override fun render(widgetId: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.layout_widget_container)

        val state = latestNewsViewModel.state
        Timber.d("render widgetId with state= $state")
        when (state) {
            is SyncState.Synced -> {
                remoteViews.setViewVisibility(R.id.mainLayout, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.progressFrameLayout, View.GONE)
                remoteViews.setViewVisibility(R.id.failure_layout, View.GONE)
                val newItem: NewsItem? = latestNewsViewModel.getCurrentItem()
                newItem?.let {
                    showNewsItem(it, remoteViews, widgetId)
                }
            }
            is SyncState.Failed -> {
                showFailedState(remoteViews, widgetId)
            }
            is SyncState.Syncing -> {
                showSyncingState(remoteViews)
            } else -> {
                showSyncingState(remoteViews)
            }
        }


        return remoteViews
    }

    private fun showSyncingState(views: RemoteViews) {
        views.setViewVisibility(R.id.mainLayout, View.GONE)
        views.setViewVisibility(R.id.failure_layout, View.GONE)
        views.setViewVisibility(R.id.progressFrameLayout, View.VISIBLE)
    }

    private fun showFailedState(views: RemoteViews, widgetId: Int) {
        views.setViewVisibility(R.id.mainLayout, View.GONE)
        views.setViewVisibility(R.id.progressFrameLayout, View.GONE)
        views.setViewVisibility(R.id.failure_layout, View.VISIBLE)

        views.setOnClickPendingIntent(
            R.id.failed_sync_btn,
            widgetService.createPendingEvent(context = context,
                widgetId = widgetId,
                action = ACTION_SYNC)
        )
    }

    private fun showNewsItem(newsItem: NewsItem, remoteViews: RemoteViews, widgetId: Int) {
        Glide.with(context)
            .asBitmap()
            .load(newsItem.imageSmall)
            .into(object: CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Timber.d("Glide onResourceReady")
                    if (newsItem.isVideoLoaded) {
                        remoteViews.setImageViewBitmap(R.id.news_image, resource)
                    } else {
                        newsItem.isVideoLoaded = true
                        widgetService.invalidateWidget()
                    }
                }
            })

        remoteViews.setTextViewText(R.id.news_title, newsItem?.itemTitle)

        remoteViews.setOnClickPendingIntent(
            R.id.prev_btn,
            widgetService.createPendingEvent(context = context,
                widgetId = widgetId,
                action = ACTION_PREV)

        )
        remoteViews.setOnClickPendingIntent(
            R.id.next_btn,
            widgetService.createPendingEvent(context = context,
                widgetId = widgetId,
                action = ACTION_NEXT)
        )

        remoteViews.setOnClickPendingIntent(
            R.id.sync_btn,
            widgetService.createPendingEvent(context = context,
                widgetId = widgetId,
                action = ACTION_SYNC)
        )

        remoteViews.setOnClickPendingIntent(
            R.id.matches_btn,
            widgetService.createPendingEvent(context = context,
                widgetId = widgetId,
                action = ACTION_SHOW_MATCHES)
        )

        val newsIntent = getBrowserIntent(context, newsItem)
        remoteViews.setOnClickPendingIntent(R.id.news_image, newsIntent)
    }

    private fun getBrowserIntent(context: Context, newsItem: NewsItem): PendingIntent {
        val urlIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(getUrl(newsUrl, context, newsItem))
        )

        return PendingIntent.getActivity(context, 2,
            urlIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getUrl(newsUrl: String, context: Context, newsItem: NewsItem): String {
        val url = newsUrl.format(
            newsItem.categoryName(context), newsItem.id.toInt()
        )
        Timber.d("getUrl for NewsItem,  Url = $url")
        return url
    }

}