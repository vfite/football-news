package com.vfite.football.data

import com.vfite.football.api.ApiRepository
import com.vfite.football.data.models.NewsData
import com.vfite.football.data.models.NewsItem
import com.vfite.football.data.models.Snapshot
import com.vfite.football.domain.FeedResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

interface NewsRepository {

    suspend fun getNews(): Snapshot<NewsItem>

    suspend fun refresh(): FeedResult<NewsData>
}

class DefaultNewsRepository(
    private val apiRepository: ApiRepository,
    private val newsDataSource: LatestNewsDataSource,
    private val clock: Clock = Clock.System,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NewsRepository {

    override suspend fun getNews(): Snapshot<NewsItem> = withContext(ioDispatcher) {
        newsDataSource.loadNews() ?: Snapshot.notFetched()
    }

    override suspend fun refresh(): FeedResult<NewsData> = withContext(ioDispatcher) {
        val newsResponse = apiRepository.getNewsFeed()
        if (newsResponse is FeedResult.Success) {
            val saved = newsDataSource.saveNews(
                items = newsResponse.feed.data.items,
                fetchedAt = clock.now()
            )
            if (saved is FeedResult.Error) {
                return@withContext saved
            }
        }
        newsResponse
    }
}
