package com.vfite.football.widget.renderer

import android.content.Context
import android.widget.RemoteViews
import com.vfite.football.R
import com.vfite.football.models.MatchItem

class MatchItemRenderer(
    val context: Context
) : IRemoteViewRenderer {
    private lateinit var matchItem : MatchItem

    override fun render(widgetId: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.layout_match_iten).apply {
            setTextColor(R.id.score, context.resources.getColor(R.color.white))
            setTextViewText(R.id.team1,matchItem.team1Name)
            setTextViewText(R.id.team2, matchItem.team2Name)
            setTextViewText(R.id.score, matchItem.score)
            if (matchItem.isInProgress) {
                setTextColor(R.id.score, context.resources.getColor(R.color.red))
            }
        }
    }

    fun setItem(matchItem: MatchItem) {
        this.matchItem = matchItem
    }
}