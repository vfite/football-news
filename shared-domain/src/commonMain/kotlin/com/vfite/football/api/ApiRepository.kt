package com.vfite.football.api

import com.vfite.football.domain.FeedResult
import com.vfite.football.data.models.MatchesData
import com.vfite.football.data.models.NewsData

interface ApiRepository {
    suspend fun getNewsFeed(): FeedResult<NewsData>
    suspend fun getMatchesFeed(date: String): FeedResult<MatchesData>
}
