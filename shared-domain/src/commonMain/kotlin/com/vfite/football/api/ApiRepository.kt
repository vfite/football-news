package com.vfite.football.api

import com.vfite.football.domain.FeedResult
import com.vfite.football.data.models.MatchesData
import com.vfite.football.data.models.NewsData
import kotlinx.datetime.LocalDate

interface ApiRepository {
    suspend fun getNewsFeed(): FeedResult<NewsData>
    suspend fun getMatchesFeed(date: LocalDate): FeedResult<MatchesData>
}
