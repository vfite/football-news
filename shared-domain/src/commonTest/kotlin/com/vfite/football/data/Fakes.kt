package com.vfite.football.data

import com.vfite.football.api.ApiRepository
import com.vfite.football.data.models.MatchItem
import com.vfite.football.data.models.MatchesData
import com.vfite.football.data.models.MatchesFeed
import com.vfite.football.data.models.NewsData
import com.vfite.football.data.models.NewsFeed
import com.vfite.football.data.models.NewsItem
import com.vfite.football.data.models.Pages
import com.vfite.football.domain.FeedResult
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class FakeApiRepository : ApiRepository {
    var newsResult: FeedResult<NewsData> = FeedResult.Error(Exception("not set"))
    var matchesResult: FeedResult<MatchesData> = FeedResult.Error(Exception("not set"))
    val requestedMatchDates = mutableListOf<String>()

    override suspend fun getNewsFeed(): FeedResult<NewsData> = newsResult

    override suspend fun getMatchesFeed(date: String): FeedResult<MatchesData> {
        requestedMatchDates += date
        return matchesResult
    }
}

class FakeClock(var now: Instant) : Clock {
    override fun now(): Instant = now
}

fun newsItem(id: Long) = NewsItem(
    id = id,
    itemTitle = "News $id",
    imageWide = "",
    imageSmall = "",
    isImportant = 0,
    isVideo = 0,
    category = "",
    postDate = "",
    commentsCount = 0,
    commentsCountTop = 0
)

fun newsData(vararg items: NewsItem) = NewsData(
    NewsFeed(pages = Pages(1, 1, 20), teams = emptyList(), items = items.toList())
)

fun matchItem(gameId: Int) = MatchItem(gameId = gameId, team1Score = "-", team2Score = "-", time = "20:00")

fun matchesData(vararg items: MatchItem) = MatchesData(MatchesFeed(items = items.toList()))
