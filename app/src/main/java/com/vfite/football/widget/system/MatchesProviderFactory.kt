package com.vfite.football.widget.system

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.vfite.football.R
import com.vfite.football.models.MatchItem
import com.vfite.football.util.appWidgetId
import com.vfite.football.widget.renderer.MatchItemRenderer
import com.vfite.football.widget.ui.LatestNewsViewModel
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.direct
import org.kodein.di.instance
import timber.log.Timber

class MatchesProviderFactory(
    private val context: Context,
    val intent: Intent,
    private val latestNewsViewModel: LatestNewsViewModel
) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var matchItems: List<MatchItem>

    override fun onCreate() {
        Timber.d("onCreate")
    }

    override fun onDataSetChanged() {
        matchItems = latestNewsViewModel.getMatches()
    }

    override fun onDestroy() {}

    override fun getCount(): Int = matchItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val matchItem = matchItems[position]

        val matchItemRenderer: MatchItemRenderer =
            DI.global.direct.instance()

        matchItemRenderer.setItem(matchItem)
        return matchItemRenderer.render(
            intent.extras.appWidgetId
        )
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(context.packageName, R.layout.layout_widget_syncing_state)
    }


    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}