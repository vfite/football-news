package com.vfite.football.widget.ui

import com.vfite.football.domain.FeedResult
import com.vfite.football.models.MatchItem
import com.vfite.football.models.NewsFeed
import com.vfite.football.models.NewsItem
import com.vfite.football.widget.WidgetMode
import com.vfite.football.widget.data.SyncService
import com.vfite.football.widget.data.SyncState
import com.vfite.football.widget.domain.LoadDayMatchesUseCase
import com.vfite.football.widget.domain.LoadLatestNewsUseCase
import timber.log.Timber

class LatestNewsViewModel(
    val syncService: SyncService,
    val loadLatestNewsUseCase: LoadLatestNewsUseCase,
    val loadDayMatchesUseCase : LoadDayMatchesUseCase
) {

    val state: SyncState
        get() = syncService.state
    private var currentMode: WidgetMode = WidgetMode.NEWS
    val mode : WidgetMode
        get() = currentMode

    var listNews: ArrayList<NewsItem> = arrayListOf()
    var listMatches: ArrayList<MatchItem> = arrayListOf()
    var currentItem: Int = 0

    fun getCurrentItem(): NewsItem? {
        Timber.d("getCurrentItem $currentItem")
        return if (listNews.isEmpty()) {
            handleLoadedResult(loadLatestNewsUseCase.execute(false))
        } else {
            listNews[currentItem]
        }
    }

    fun getMatches(): List<MatchItem> {
        if (listMatches.isEmpty()) {
            loadMatches()
        }
        return listMatches
    }

    private fun handleLoadedResult(result: FeedResult<NewsFeed>): NewsItem? {
        return when (result) {
            is FeedResult.Success -> {
                if (result.feed.items.isNotEmpty()) {
                    clearNews()
                    listNews.addAll(result.feed.items)
                    listNews[currentItem]
                } else {
                    null
                }
            }
            is FeedResult.Error -> null
        }
    }

    private fun clearNews() {
        listNews.clear()
    }

    private fun loadMatches() {
        val matches = loadDayMatchesUseCase.execute(false)
        if (matches.isNotEmpty()) {
            listMatches.clear()
            listMatches.addAll(matches)
        }
    }

    fun nextItem() {
        if (listNews.isEmpty()) {
            return
        }
        currentItem +=1
        currentItem = currentItem.coerceIn(0, listNews.lastIndex)
    }

    fun previousItem() {
        if (listNews.isEmpty()) {
            return
        }
        currentItem -=1
        currentItem = currentItem.coerceIn(0, listNews.lastIndex)
    }

    fun syncNews() {
        currentItem = 0
        syncService.syncNews()
        clearNews()
    }

    fun syncMatches() {
        syncService.syncMatches()
    }

    fun toggleMode() {
        val newMode = when (currentMode) {
            WidgetMode.NEWS -> WidgetMode.MACTHES
            WidgetMode.MACTHES -> WidgetMode.NEWS
        }
        currentMode = newMode
    }
}