package com.vfite.football.widget.ui

import com.vfite.football.data.models.MatchItem
import com.vfite.football.data.models.NewsItem
import com.vfite.football.data.models.Snapshot
import com.vfite.football.widget.WidgetMode
import com.vfite.football.domain.SyncService
import com.vfite.football.domain.SyncState
import com.vfite.football.data.MatchesRepository
import com.vfite.football.data.NewsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class LatestNewsViewModel(
    val syncService: SyncService,
    val newsRepository: NewsRepository,
    val matchesRepository: MatchesRepository,
    private val applicationScope: CoroutineScope
) {

    val state: SyncState
        get() = syncService.state
    private var currentMode: WidgetMode = WidgetMode.NEWS
    val mode: WidgetMode
        get() = currentMode

    var listNews: ArrayList<NewsItem> = arrayListOf()
    var listMatches: ArrayList<MatchItem> = arrayListOf()
    var currentItem: Int = 0

    fun loadData() {
        applicationScope.launch(Dispatchers.Main) {
            coroutineScope {
                val news = async { newsRepository.getNews() }
                val matches = async { matchesRepository.getTodayGames() }
                applyNews(news.await())
                applyMatches(matches.await())
            }
        }
    }

    fun getCurrentItem(): NewsItem? {
        Timber.d("getCurrentItem $currentItem")
        return listNews.getOrNull(currentItem)
    }

    fun getTodayGames(): List<MatchItem> {
        return listMatches
    }

    private fun applyNews(snapshot: Snapshot<NewsItem>) {
        if (listNews.isNotEmpty()) {
            return
        }
        if (!snapshot.isFetched) {
            // Same rule as before: only start a sync while the widget shows content, not while syncing or failed.
            if (state is SyncState.Synced) {
                syncService.syncNews()
            }
            return
        }
        clearNews()
        listNews.addAll(snapshot.items)
    }

    private fun clearNews() {
        listNews.clear()
    }

    private fun applyMatches(snapshot: Snapshot<MatchItem>) {
        val matches = snapshot.items
        if (matches.isNotEmpty()) {
            listMatches.clear()
            listMatches.addAll(matches)
        }
    }

    fun nextItem() {
        if (listNews.isEmpty()) {
            return
        }
        currentItem += 1
        currentItem = currentItem.coerceIn(0, listNews.lastIndex)
    }

    fun previousItem() {
        if (listNews.isEmpty()) {
            return
        }
        currentItem -= 1
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