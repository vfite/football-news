package com.vfite.football.domain

import com.vfite.football.data.models.NewsFeed
import com.vfite.football.data.LatestNewsDataSource

class LoadLatestNewsUseCase(
    val newsDataSource: LatestNewsDataSource,
    val syncService: SyncService
) {

    fun execute(forceLoad: Boolean): FeedResult<NewsFeed> {
        return newsDataSource.loadNews().onSuccess { data ->
            if (data.items.isEmpty()) {
                syncService.syncNews()
            }
        }
    }
}
