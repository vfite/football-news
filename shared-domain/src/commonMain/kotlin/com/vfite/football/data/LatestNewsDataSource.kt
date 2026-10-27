package com.vfite.football.data

import com.vfite.football.domain.FeedResult
import com.vfite.football.data.models.NewsFeed
import com.vfite.football.data.models.empty

interface LatestNewsDataSource {

    fun loadNews(): FeedResult<NewsFeed>
    suspend fun saveNews(data: NewsFeed)
}

class RealLatestNewsDataSource : LatestNewsDataSource {

    private var data: NewsFeed = empty()

    override fun loadNews(): FeedResult<NewsFeed> {
        return FeedResult.Success(data)
    }

    override suspend fun saveNews(data: NewsFeed) {
        this.data = data
    }
}
