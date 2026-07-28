package com.vfite.football.widget.domain

import com.vfite.football.domain.FeedResult
import com.vfite.football.domain.onSuccess
import com.vfite.football.models.NewsFeed
import com.vfite.football.widget.data.LatestNewsDataSource
import com.vfite.football.widget.data.SyncService
import timber.log.Timber

class LoadLatestNewsUseCase(
    val newsDataSource: LatestNewsDataSource,
    val syncService: SyncService
) {

    fun execute(forceLoad: Boolean) : FeedResult<NewsFeed> {
        Timber.d("execute")
        return newsDataSource.
            loadNews().
            onSuccess { data ->
                if (data.items.isEmpty()) {
                    syncService.syncNews()
                }
            }
    }
}
